package com.darkona.adventurebackpack.block;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

import com.darkona.adventurebackpack.inventory.IInventoryTanks;

import static com.darkona.adventurebackpack.common.Constants.TAG_SLOT;

/**
 * Created on 26.02.2018
 *
 * @author Ugachaga
 */
@SuppressWarnings("WeakerAccess")
abstract class TileAdventure extends TileEntity implements IInventoryTanks
{
    // when porting to java 8+ most this methods should move to IInventoryTanks

    protected final ItemStack[] inventory;

    protected TileAdventure(int inventorySize)
    {
        this.inventory = new ItemStack[inventorySize];
    }

    @Override
    public int getSizeInventory()
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inventory[slot];
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int slot, int quantity)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= quantity)
            {
                setInventorySlotContents(slot, null);
            }
            else
            {
                stack = stack.splitStack(quantity);
                dirtyInventory();
            }
        }
        return stack;
    }

    @Nullable
    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        for (int s : getSlotsOnClosing())
            if (slot == s)
                return inventory[slot];

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

    // we have to inherit markDirty() implemented in TileEntity.class

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public void openInventory()
    {
        //
    }

    @Override
    public void closeInventory()
    {
        dirtyInventory();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return false; // override when automation is allowed
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

        inventory[slot] = stack;
    }

    @Override
    public void dirtyInventory()
    {
        updateTankSlots();
        markDirty();
    }

    @Deprecated
    @Override
    public void dirtyTanks()
    {
        // for now none is calling this for tile.backpack
        // if we really want to use it, we have to re-implement it, more efficient way
        dirtyInventory();
    }

    protected void setInventoryFromTagList(NBTTagList items)
    {
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte(TAG_SLOT);
            if (slot >= 0 && slot < getSizeInventory())
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }

    protected NBTTagList getInventoryTagList()
    {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < getSizeInventory(); i++)
        {
            ItemStack stack = inventory[i];
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
