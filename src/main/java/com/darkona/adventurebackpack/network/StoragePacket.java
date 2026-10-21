package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class StoragePacket implements IMessageHandler<StoragePacket.Message, IMessage>
{
    @Override
    public IMessage onMessage(Message message, MessageContext ctx)
    {
        if (!ctx.side.isServer()) return null;
        EntityPlayer player = ctx.getServerHandler().playerEntity;
        if (player.openContainer instanceof ContainerBackpack && player.openContainer.windowId == message.window)
        {
            ((ContainerBackpack) player.openContainer).request(message.action, message.slot);
        }
        return null;
    }

    public static class Message implements IMessage
    {
        public int window;
        public int action;
        public int slot;

        public Message()
        {
        }

        public Message(int window, int action, int slot)
        {
            this.window = window;
            this.action = action;
            this.slot = slot;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            window = buf.readByte();
            action = buf.readByte();
            slot = buf.readShort();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(window);
            buf.writeByte(action);
            buf.writeShort(slot);
        }
    }
}
