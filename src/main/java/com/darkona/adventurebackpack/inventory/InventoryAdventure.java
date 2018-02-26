package com.darkona.adventurebackpack.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import static com.darkona.adventurebackpack.common.Constants.TAG_INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.TAG_SLOT;
import static com.darkona.adventurebackpack.common.Constants.TAG_WEARABLE_COMPOUND;

/**
 * Created on 15.07.2017
 *
 * @author Ugachaga
 */
@SuppressWarnings("WeakerAccess")
abstract class InventoryAdventure implements IInventoryTanks
{
    ItemStack containerStack;

    @Override
    public int getSizeInventory()
    {
        return getInventory().length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return getInventory()[slot];
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int slot, int quantity)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= quantity)
                setInventorySlotContents(slot, null);
            else
                stack = stack.splitStack(quantity);
        }
        return stack;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        for (int s : getSlotsOnClosingArray())
            if (slot == s)
                return getInventory()[slot];

        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, @Nullable ItemStack stack)
    {
        setInventorySlotContentsNoSave(slot, stack);
        dirtyInventory();
    }


    @Override
    public String getInventoryName()
    {
        return ""; //TODO name heirs
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return getInventoryName() != null && !getInventoryName().isEmpty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {
        saveToNBT(containerStack.stackTagCompound);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory()
    {
        loadFromNBT(containerStack.stackTagCompound);
    }

    @Override
    public void closeInventory()
    {
        saveToNBT(containerStack.stackTagCompound);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return false;
    }

    @Nullable
    @Override
    public ItemStack decrStackSizeNoSave(int slot, int quantity)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= quantity)
                setInventorySlotContentsNoSave(slot, null);
            else
                stack = stack.splitStack(quantity);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, @Nullable ItemStack stack)
    {
        if (slot >= getSizeInventory())
            return;

        if (stack != null)
        {
            if (stack.stackSize > getInventoryStackLimit())
                stack.stackSize = getInventoryStackLimit();

            if (stack.stackSize == 0)
                stack = null;
        }

        getInventory()[slot] = stack;
    }

    @Override
    public void dirtyInventory()
    {
        if (updateTankSlots()) //TODO this can be generalized too
            dirtyTanks();      //TODO and also this

        getWearableCompound().removeTag(TAG_INVENTORY); //TODO why? sync related?
        getWearableCompound().setTag(TAG_INVENTORY, getInventoryTagList());
    }

    protected NBTTagCompound getWearableCompound()
    {
        return containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND);
    }

    protected void setInventoryFromTagList(NBTTagList items)
    {
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte(TAG_SLOT);
            if (slot >= 0 && slot < getSizeInventory())
            {
                getInventory()[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }

    protected NBTTagList getInventoryTagList()
    {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++)
        {
            ItemStack stack = getInventory()[i];
            if (stack != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte(TAG_SLOT, (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        return items;
    }
}
