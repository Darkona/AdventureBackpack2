package com.darkona.adventurebackpack.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class WearableModePacket implements IMessageHandler<WearableModePacket.Message, WearableModePacket.Message>
{
    public static final byte COPTER_ON_OFF = 0;
    public static final byte COPTER_TOGGLE = 1;
    public static final byte JETPACK_ON_OFF = 2;
    public static final byte CYCLING_ON_OFF = 3;
    public static final byte NIGHTVISION_ON_OFF = 4;

    @Override
    public Message onMessage(Message message, MessageContext ctx)
    {
        if (ctx.side.isServer())
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (player != null)
            {
                if (message.type == COPTER_ON_OFF || message.type == COPTER_TOGGLE)
                {
                    ItemStack copter = Wearing.getWearingCopter(player);
                    // for concurrency reasons, at the time of death with OpenBlocks mod, the copter may already be
                    // in the grave, and Wearing#getWearingCopter will return null (c) Relvl
                    if (copter != null)
                        ServerActions.toggleCopterPack(player, copter, message.type);
                }
                if (message.type == JETPACK_ON_OFF)
                {
                    ItemStack jetpack = Wearing.getWearingJetpack(player);
                    if (jetpack != null) // so now we are well-defended
                        ServerActions.toggleCoalJetpack(player, jetpack);
                }
                if (message.type == CYCLING_ON_OFF || message.type == NIGHTVISION_ON_OFF)
                {
                    ItemStack backpack = Wearing.getWearingBackpack(player);
                    if (backpack != null) // null shall not pass!
                    {
                        if (message.type == CYCLING_ON_OFF)
                            ServerActions.toggleToolCycling(player, backpack);
                        if (message.type == NIGHTVISION_ON_OFF)
                            ServerActions.toggleNightVision(player, backpack);
                    }
                }
            }
        }
        return null;
    }

    public static class Message implements IMessage
    {
        private byte type;
        private String playerID;

        public Message() {}

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