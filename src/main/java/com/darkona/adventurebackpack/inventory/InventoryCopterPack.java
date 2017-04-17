package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemCopterPack;

import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_OUT;
import static com.darkona.adventurebackpack.common.Constants.COPTER_FUEL_TANK;
import static com.darkona.adventurebackpack.common.Constants.COPTER_INVENTORY_SIZE;

/**
 * Created on 02/01/2015
 *
 * @author Darkona
 */
public class InventoryCopterPack implements IInventoryTanks
{
    public int tickCounter = 0;

    private FluidTank fuelTank = new FluidTank(Constants.COPTER_FUEL_CAPACITY);
    private ItemStack[] inventory = new ItemStack[COPTER_INVENTORY_SIZE];

    private byte status = ItemCopterPack.OFF_MODE;
    private ItemStack containerStack;

    //TODO copter sound doesn't init at login (status init, so you can keep flying)
    public InventoryCopterPack(ItemStack copterPack)
    {
        containerStack = copterPack;
        openInventory();
    }

    public FluidTank getFuelTank()
    {
        return fuelTank;
    }

    public void consumeFuel(int quantity)
    {
        fuelTank.drain(quantity, true);
        dirtyTanks();
    }

    public boolean canConsumeFuel(int quantity)
    {
        return fuelTank.drain(quantity, false) != null && fuelTank.drain(quantity, false).amount > 0;
    }

    @Override
    public int getSizeInventory()
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int i)
    {
        return inventory[i];
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity)
    {
        ItemStack itemstack = getStackInSlot(slot);

        if (itemstack != null)
        {
            if (itemstack.stackSize <= quantity)
            {
                setInventorySlotContents(slot, null);
            } else
            {
                itemstack = itemstack.splitStack(quantity);
            }
        }
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return (i == COPTER_BUCKET_IN || i == COPTER_BUCKET_OUT) ? inventory[i] : null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
        dirtyInventory();
    }

    @Override
    public String getInventoryName()
    {
        return "Copter Pack";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
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
    public boolean isItemValidForSlot(int i, ItemStack stack)
    {
        return false;
    }

    public void dirtyCounter()
    {
        containerStack.stackTagCompound.setInteger("tickCounter", this.tickCounter);
    }

    public void closeInventoryNoStatus()
    {
        containerStack.stackTagCompound.setTag(COPTER_FUEL_TANK, this.fuelTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.setInteger("tickCounter", this.tickCounter);
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack stack)
    {
        if (slot > inventory.length) return;
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        if (slot < inventory.length && inventory[slot] != null)
        {
            if (inventory[slot].stackSize > amount)
            {
                return inventory[slot].splitStack(amount);
            }
            ItemStack stack = inventory[slot];
            setInventorySlotContentsNoSave(slot, null);
            return stack;
        }
        return null;
    }

    public ItemStack getParentItemStack()
    {
        return this.containerStack;
    }

    public int getTickCounter()
    {
        return tickCounter;
    }

    public void setTickCounter(int ticks)
    {
        this.tickCounter = ticks;
    }

    public byte getStatus()
    {
        return status;
    }

    public void setStatus(byte status)
    {
        this.status = status;
    }

    @Override
    public boolean updateTankSlots()
    {
        boolean result = false;
        while (InventoryActions.transferContainerTank(this, getFuelTank(), COPTER_BUCKET_IN))
            result = true;
        return result;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        fuelTank.readFromNBT(compound.getCompoundTag(COPTER_FUEL_TANK));
        status = compound.getByte("status");
        tickCounter = compound.getInteger("tickCounter");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        compound.setTag(COPTER_FUEL_TANK, fuelTank.writeToNBT(new NBTTagCompound()));
        compound.setByte("status", status);
        compound.setInteger("tickCounter", this.tickCounter);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{fuelTank};
    }

    @Override
    public void dirtyInventory()
    {
        if (updateTankSlots())
        {
            dirtyTanks();
        }
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.setTag(COPTER_FUEL_TANK, fuelTank.writeToNBT(new NBTTagCompound()));
    }

    public void dirtyStatus()
    {
        containerStack.stackTagCompound.setByte("status", status);
    }

    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
    }
}
