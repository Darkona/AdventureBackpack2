package com.darkona.adventurebackpack.network;

import io.netty.buffer.ByteBuf;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 08/01/2015
 *
 * @author Darkona
 */
public class EquipUnequipBackWearablePacket implements IMessageHandler<EquipUnequipBackWearablePacket.Message, EquipUnequipBackWearablePacket.Message>
{
    public static final byte EQUIP_WEARABLE = 0;
    public static final byte UNEQUIP_WEARABLE = 1;

    @Override
    public Message onMessage(Message message, MessageContext ctx)
    {

        if (ctx.side.isServer())
        {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            if (message.action == EQUIP_WEARABLE)
            {
                //before reenable make sure to takes into account the delay in unequipWearable()
                /*if (message.force && Wearing.isWearingWearable(player))
                {
                    BackpackUtils.unequipWearable(player);
                } else */
                if (Wearing.isHoldingWearable(player) && !Wearing.isWearingWearable(player))
                {
                    if (BackpackUtils.equipWearable(player.getCurrentEquippedItem(), player) == BackpackUtils.Reasons.SUCCESSFUL)
                    {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        player.inventoryContainer.detectAndSendChanges();
                    }
                }
                else if (Wearing.isWearingWearable(player))
                {
                    if (Wearing.isWearingBackpack(player))
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.backpack"));
                    }
                    else if (Wearing.isWearingCopter(player))
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.copterpack"));
                    }
                    else if (Wearing.isWearingJetpack(player))
                    {
                        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.jetpack"));
                    }
                }
            }
            if (message.action == UNEQUIP_WEARABLE)
            {
                BackpackUtils.unequipWearable(player);
            }
        }
        return null;
    }

    public static class Message implements IMessage
    {
        private byte action;
        private boolean force;

        public Message()
        {

        }

        public Message(byte action, boolean force)
        {
            this.action = action;
            this.force = force;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            action = buf.readByte();
            force = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeByte(action);
            buf.writeBoolean(force);
        }
    }
}
