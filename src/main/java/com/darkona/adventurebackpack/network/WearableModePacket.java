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
public class WearableModePacket implements IMessageHandler<WearableModePacket.Message, WearableModePacket.Message>
{
    public static byte COPTER_ON_OFF = 0;
    public static byte COPTER_TOGGLE = 1;
    public static byte JETPACK_ON_OFF = 2;
    public static byte CYCLING_ON_OFF = 3;
    public static byte NIGHTVISION_ON_OFF = 4;

    @Override
    public Message onMessage(Message message, MessageContext ctx)
    {
        if (ctx.side.isServer())
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player != null)
            {
                if((message.type == COPTER_ON_OFF || message.type == COPTER_TOGGLE))
                    ServerActions.toggleCopterPack(player, Wearing.getWearingCopter(player), message.type);

                if(message.type == JETPACK_ON_OFF)
                    ServerActions.toggleCoalJetpack(player,Wearing.getWearingJetpack(player),message.type);

                if(message.type == CYCLING_ON_OFF)
                    ServerActions.toggleToolCycling(player,Wearing.getWearingBackpack(player),message.type);

                if(message.type == NIGHTVISION_ON_OFF)
                    ServerActions.toggleNightVision(player,Wearing.getWearingBackpack(player),message.type);
            }

        }
        if (ctx.side.isClient())
        {

        }
        return null;
    }

    public static class Message implements IMessage
    {

        private byte type;
        private String playerID;

        public Message()
        {
        }

        public Message(byte type, String playerID)
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