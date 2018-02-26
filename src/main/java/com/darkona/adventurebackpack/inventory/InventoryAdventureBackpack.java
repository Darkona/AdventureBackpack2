package com.darkona.adventurebackpack.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Created on 15.07.2017
 *
 * @author Ugachaga
 */
abstract class InventoryAdventureBackpack implements IInventoryTanks
{
    ItemStack containerStack;

    /** IInventory START --- */

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
        return "";
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

    /* --- IInventory END || IAsynchronousInventory START --- */

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

    /* --- IAsynchronousInventory END || IInventoryTanks START --- */

    //TODO

}
