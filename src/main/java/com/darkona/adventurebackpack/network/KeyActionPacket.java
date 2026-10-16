package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.KeyActions;
import com.darkona.adventurebackpack.common.ServerActions;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class KeyActionPacket implements IMessageHandler<KeyActionPacket.Message, IMessage>
{
    @Override
    public IMessage onMessage(Message message, MessageContext ctx)
    {
        if (!ctx.side.isServer()) return null;
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null || player.isDead) return null;
        ServerActions.keyAction(player, KeyActions.Key.fromId(message.key), KeyActions.clampDirection(message.direction));
        return null;
    }

    public static class Message implements IMessage
    {
        private byte key;
        private byte direction;

        public Message()
        {
        }

        public Message(KeyActions.Key key, int direction)
        {
            this.key = (byte) key.ordinal();
            this.direction = (byte) KeyActions.clampDirection(direction);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            key = buf.readByte();
            direction = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(key);
            buf.writeByte(direction);
        }
    }
}
