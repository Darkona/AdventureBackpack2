package com.darkona.adventurebackpack.util;

import java.util.Timer;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;

import com.darkona.adventurebackpack.events.WearableEvent;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.reference.BackpackTypes;

import static com.darkona.adventurebackpack.common.Constants.TAG_INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.TAG_TYPE;
import static com.darkona.adventurebackpack.common.Constants.TAG_WEARABLE_COMPOUND;

/**
 * Created on 08/01/2015
 *
 * @author Darkona
 */
public class BackpackUtils
{
    private static Timer timer = new Timer(); //TODO remove timer, find the dupe, fix the dupe

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

    public static NBTTagCompound getWearableCompound(ItemStack stack)
    {
        // it also creates wearable compound if stack has no own, so maybe worth to rename the method
        if (!stack.hasTagCompound() || !stack.stackTagCompound.hasKey(TAG_WEARABLE_COMPOUND))
            createWearableCompound(stack);

        return stack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND);
    }

    private static void createWearableCompound(ItemStack stack)
    {
        if (!stack.hasTagCompound())
            stack.stackTagCompound = new NBTTagCompound();

        stack.stackTagCompound.setTag(TAG_WEARABLE_COMPOUND, new NBTTagCompound());
    }

    public static NBTTagList getWearableInventory(ItemStack stack)
    {
        // it also creates TagList if stack has no own, so maybe worth to rename the method
        if (!getWearableCompound(stack).hasKey(TAG_INVENTORY))
            createWearableInventory(stack);

        return getWearableCompound(stack).getTagList(TAG_INVENTORY, Constants.NBT.TAG_COMPOUND);
    }

    private static void createWearableInventory(ItemStack stack)
    {
        getWearableCompound(stack).setTag(TAG_INVENTORY, new NBTTagList());
    }

    public static ItemStack createBackpackStack(BackpackTypes type)
    {
        ItemStack backpackStack = new ItemStack(ModItems.adventureBackpack, 1, BackpackTypes.getMeta(type));
        setBackpackType(backpackStack, type);
        return backpackStack;
    }

    public static void setBackpackType(ItemStack stack, BackpackTypes type)
    {
        getWearableCompound(stack).setByte(TAG_TYPE, BackpackTypes.getMeta(type));
    }

    public static ItemStack createCopterStack()
    {
        ItemStack copterStack = new ItemStack(ModItems.copterPack, 1, 0);
        createWearableCompound(copterStack);
        return copterStack;
    }

    public static ItemStack createJetpackStack()
    {
        ItemStack jetpackStack = new ItemStack(ModItems.coalJetpack, 1, 0);
        createWearableCompound(jetpackStack);
        return jetpackStack;
    }

}