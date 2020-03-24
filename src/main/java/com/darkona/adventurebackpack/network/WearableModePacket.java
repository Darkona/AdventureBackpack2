package com.darkona.adventurebackpack.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
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
            // we don't want to process any packets from a player who's already considered dead on the server.
            // in the current tick, at the time the packets are processed, the entity data must already be updated
            // and isDead flag will be set, see: MinecraftServer#updateTimeLightAndEntities (func_71190_q)
            if (player == null || player.isDead)
                return null;

            if (message.type == COPTER_ON_OFF || message.type == COPTER_TOGGLE)
            {
                ItemStack copter = Wearing.getWearingCopter(player);
                // for concurrency reasons, at the time of death with OpenBlocks mod, the copter may already be
                // in the grave, and Wearing#getWearingCopter will return null (c) Relvl
                if (copter != null)
                    ServerActions.toggleCopterPack(player, copter, message.type);
            }
            else if (message.type == JETPACK_ON_OFF)
            {
                ItemStack jetpack = Wearing.getWearingJetpack(player);
                if (jetpack != null) // so now we are well-defended
                    ServerActions.toggleCoalJetpack(player, jetpack);
            }
            else if (message.type == CYCLING_ON_OFF || message.type == NIGHTVISION_ON_OFF)
            {
                ItemStack backpack = Wearing.getWearingBackpack(player);
                if (backpack != null) // null shall not pass!
                {
                    if (message.type == CYCLING_ON_OFF)
                        ServerActions.toggleToolCycling(player, backpack);
                    else if (message.type == NIGHTVISION_ON_OFF)
                        ServerActions.toggleNightVision(player, backpack);
                }
            }
        }
        return null;
    }

    public static class Message implements IMessage
    {
        private byte type;

        public Message() {}

        public Message(byte type)
        {
            this.type = type;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            this.type = buf.readByte();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(type);
        }
    }
}