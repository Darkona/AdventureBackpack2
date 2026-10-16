package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.item.ItemCrossbow;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class CrossbowPacket implements IMessageHandler<CrossbowPacket.Message, IMessage>
{
    public static final byte RELEASE_GRAPPLE = 1;

    @Override
    public IMessage onMessage(Message message, MessageContext ctx)
    {
        if (!ctx.side.isServer()) return null;
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) return null;
        if (message.action == RELEASE_GRAPPLE)
        {
            ItemCrossbow.releaseGrapple(player);
        }
        return null;
    }

    public static class Message implements IMessage
    {
        private byte action;

        public Message()
        {
        }

        public Message(byte action)
        {
            this.action = action;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            action = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(action);
        }
    }
}
