package com.darkona.adventurebackpack.network;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class CopterPacket implements IMessageHandler<CopterPacket.CopterMessage, CopterPacket.CopterMessage>
{
    public static byte ON_OFF = 0;
    public static byte TOGGLE = 1;

    @Override
    public CopterPacket.CopterMessage onMessage(CopterMessage message, MessageContext ctx)
    {
        if (ctx.side.isServer())
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player != null)
            {
                ServerActions.toggleCopterPack(player, Wearing.getWearingCopter(player), message.type);
            }
        }
        if (ctx.side.isClient())
        {

        }
        return null;
    }

    public static class CopterMessage implements IMessage
    {

        private byte type;
        private String playerID;

        public CopterMessage()
        {
        }

        public CopterMessage(byte type, String playerID)
        {
            this.type = type;
            this.playerID = playerID;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.type = buf.readByte();
            playerID = ByteBufUtils.readUTF8String(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(type);
            ByteBufUtils.writeUTF8String(buf, playerID);
        }
    }
}