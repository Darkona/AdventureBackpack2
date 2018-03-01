package com.darkona.adventurebackpack.util;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.events.WearableEvent;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.BackpackTypes;

import static com.darkona.adventurebackpack.common.Constants.TAG_TYPE;

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

    public static NBTTagCompound getWearableCompound(ItemStack stack)
    {
        if (!stack.hasTagCompound() || !stack.stackTagCompound.hasKey(Constants.TAG_WEARABLE_COMPOUND))
            setWearableCompound(stack, new NBTTagCompound());

        return stack.stackTagCompound.getCompoundTag(Constants.TAG_WEARABLE_COMPOUND);
    }

    public static void setWearableCompound(ItemStack stack, NBTTagCompound compound)
    {
        if (!stack.hasTagCompound())
            stack.stackTagCompound = new NBTTagCompound();

        stack.stackTagCompound.setTag(Constants.TAG_WEARABLE_COMPOUND, compound);
    }

    public static NBTTagList getInventoryTag(NBTTagCompound compound)
    {
        if (!compound.hasKey(Constants.TAG_INVENTORY))
            return compound.getTagList(Constants.TAG_INVENTORY, NBT.TAG_COMPOUND);

        return new NBTTagList();
    }

    public static NBTTagList getInventoryTag(ItemStack stack)
    {
        return getInventoryTag(getWearableCompound(stack));
    }

    public static ItemStack createBackpackStack(BackpackTypes type)
    {
        ItemStack backpackStack = new ItemStack(ModItems.adventureBackpack, 1, 0);
        backpackStack.setItemDamage(BackpackTypes.getMeta(type));
        NBTTagCompound compound = new NBTTagCompound();
        compound.setByte(TAG_TYPE, BackpackTypes.getMeta(type));
        setWearableCompound(backpackStack, compound);
        return backpackStack;
    }

    public static ItemStack createCopterStack()
    {
        ItemStack copterStack = new ItemStack(ModItems.copterPack, 1, 0);
        setWearableCompound(copterStack, new NBTTagCompound());
        return copterStack;
    }

    public static ItemStack createJetpackStack()
    {
        ItemStack jetpackStack = new ItemStack(ModItems.coalJetpack, 1, 0);
        setWearableCompound(jetpackStack, new NBTTagCompound());
        return jetpackStack;
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