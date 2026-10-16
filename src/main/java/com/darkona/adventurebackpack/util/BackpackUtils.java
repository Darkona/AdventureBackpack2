package com.darkona.adventurebackpack.util;

import com.darkona.adventurebackpack.compat.baubles.BackpackBauble;
import adventurebackpack.api.WearableEvent;
import com.darkona.adventurebackpack.inventory.QuiverFeed;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;

public class BackpackUtils
{
    public enum reasons{
        SUCCESFUL,ALREADY_EQUIPPED
    }
    public static reasons equipWearable(ItemStack backpack, EntityPlayer player)
    {
        BackpackProperty prop = BackpackProperty.get(player);
        if(prop.getWearable() == null)
        {
            player.openContainer.onContainerClosed(player);
            prop.setWearable(backpack.copy());
            BackpackProperty.get(player).executeWearableEquipProtocol();
            backpack.stackSize--;
            WearableEvent event = new WearableEvent.EquipWearableEvent(player, prop.getWearable());
            MinecraftForge.EVENT_BUS.post(event);
            BackpackProperty.sync(player);
            return reasons.SUCCESFUL;
        }else
        {
            return reasons.ALREADY_EQUIPPED;
        }
    }

    public static void unequipWearable(EntityPlayer player)
    {
        BackpackProperty prop = BackpackProperty.get(player);
        if(prop.getWearable() != null)
        {
            QuiverFeed.flush(player);
            player.openContainer.onContainerClosed(player);
            ItemStack gimme;
            if (prop.isWearableInBauble())
            {
                gimme = BackpackBauble.takeOutOfSlot(player);
                if (gimme == null) return;
            } else
            {
                gimme = prop.getWearable().copy();
                BackpackProperty.get(player).executeWearableUnequipProtocol();
                prop.setWearable(null);
            }
            if(!player.inventory.addItemStackToInventory(gimme))
            {
                player.dropPlayerItemWithRandomChoice(gimme,false);
            }
            WearableEvent event = new WearableEvent.UnequipWearableEvent(player, gimme);
            MinecraftForge.EVENT_BUS.post(event);
            BackpackProperty.sync(player);
        }else
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.impossibru"));
        }
    }

    public static NBTTagCompound getBackpackData(ItemStack backpack)
    {
        if(backpack.hasTagCompound() && backpack.stackTagCompound.hasKey("backpackData"))
        {
            return backpack.stackTagCompound.getCompoundTag("backpackData");
        }
        return null;
    }

    public static long getTimeMark(ItemStack stack, String key)
    {
        NBTTagCompound tag = stack.stackTagCompound;
        return tag != null && tag.hasKey(key) ? tag.getLong(key) : -1;
    }

    public static void setTimeMark(ItemStack stack, String key, long worldTime)
    {
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setLong(key, worldTime);
    }

    public static void setBackpackData(ItemStack stack, NBTTagCompound compound)
    {
        if(!stack.hasTagCompound())stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setTag("backpackData",compound);
    }
}
