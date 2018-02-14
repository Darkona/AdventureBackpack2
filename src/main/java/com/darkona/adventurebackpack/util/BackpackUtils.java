package com.darkona.adventurebackpack.util;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.events.WearableEvent;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;

/**
 * Created on 08/01/2015
 *
 * @author Darkona
 */
public class BackpackUtils
{
    private static Timer timer = new Timer();

    public enum Reasons
    {
        SUCCESSFUL, ALREADY_EQUIPPED
    }

    public static Reasons equipWearable(ItemStack backpack, EntityPlayer player)
    {
        BackpackProperty prop = BackpackProperty.get(player);
        if (prop.getWearable() == null)
        {
            player.openContainer.onContainerClosed(player);
            prop.setWearable(backpack.copy());
            BackpackProperty.get(player).executeWearableEquipProtocol();
            backpack.stackSize--;
            WearableEvent event = new WearableEvent.EquipWearableEvent(player, prop.getWearable());
            MinecraftForge.EVENT_BUS.post(event);
            BackpackProperty.sync(player);
            return Reasons.SUCCESSFUL;
        }
        else
        {
            return Reasons.ALREADY_EQUIPPED;
        }
    }

    public static void unequipWearable(EntityPlayer player)
    {
        TimerTask unequipTask = new DelayUnequipTask(player);
        timer.schedule(unequipTask, 200);
    }

    //TODO as we unificate NTB structure for all wearables, have to remake this to getWearableTag
    public static NBTTagCompound getBackpackTag(ItemStack backpack)
    {
        if (backpack.hasTagCompound() && backpack.stackTagCompound.hasKey(Constants.TAG_WEARABLE_COMPOUND))
        {
            return backpack.stackTagCompound.getCompoundTag(Constants.TAG_WEARABLE_COMPOUND);
        }
        return new NBTTagCompound();
    }

    public static void setBackpackTag(ItemStack stack, NBTTagCompound compound)
    {
        if (!stack.hasTagCompound())
        {
            stack.stackTagCompound = new NBTTagCompound();
        }
        stack.stackTagCompound.setTag(Constants.TAG_WEARABLE_COMPOUND, compound);
    }

    private static class DelayUnequipTask extends TimerTask
    {
        private EntityPlayer player;

        DelayUnequipTask(EntityPlayer player)
        {
            this.player = player;
        }

        @Override
        public void run()
        {
            BackpackProperty prop = BackpackProperty.get(player);
            if (prop.getWearable() != null)
            {
                player.openContainer.onContainerClosed(player);
                ItemStack gimme = prop.getWearable().copy();
                BackpackProperty.get(player).executeWearableUnequipProtocol();
                prop.setWearable(null);
                if (!player.inventory.addItemStackToInventory(gimme))
                {
                    player.dropPlayerItemWithRandomChoice(gimme, false);
                }
                WearableEvent event = new WearableEvent.UnequipWearableEvent(player, gimme);
                MinecraftForge.EVENT_BUS.post(event);
                BackpackProperty.sync(player);
            }
            else
            {
                player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.impossibru"));
            }
        }
    }
}