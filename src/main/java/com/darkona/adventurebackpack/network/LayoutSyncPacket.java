package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.layout.BackpackSpec;
import com.darkona.adventurebackpack.inventory.WidgetLayouts;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

import java.util.LinkedHashMap;
import java.util.Map;

public class LayoutSyncPacket implements IMessageHandler<LayoutSyncPacket.Message, IMessage>
{
    private static final int MAX_SPECS = 256;
    private static final int MAX_INTS = 1024;

    @Override
    public IMessage onMessage(Message message, MessageContext ctx)
    {
        if (ctx.side.isClient()) WidgetLayouts.useServerSpecs(message.specs);
        return null;
    }

    public static class Message implements IMessage
    {
        public Map<String, BackpackSpec> specs = new LinkedHashMap<String, BackpackSpec>();

        public Message()
        {
        }

        public Message(Map<String, BackpackSpec> specs)
        {
            this.specs = specs;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            int n = buf.readShort();
            if (n < 0 || n > MAX_SPECS) throw new IllegalArgumentException("too many backpack specs: " + n);
            Map<String, BackpackSpec> read = new LinkedHashMap<String, BackpackSpec>();
            for (int i = 0; i < n; i++)
            {
                String name = ByteBufUtils.readUTF8String(buf);
                int length = buf.readShort();
                if (length < 0 || length > MAX_INTS) throw new IllegalArgumentException("backpack spec too long: " + length);
                int[] ints = new int[length];
                for (int k = 0; k < length; k++) ints[k] = buf.readInt();
                read.put(name, BackpackSpec.decode(ints));
            }
            specs = read;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeShort(specs.size());
            for (Map.Entry<String, BackpackSpec> e : specs.entrySet())
            {
                ByteBufUtils.writeUTF8String(buf, e.getKey());
                int[] ints = e.getValue().encode();
                buf.writeShort(ints.length);
                for (int v : ints) buf.writeInt(v);
            }
        }
    }
}
