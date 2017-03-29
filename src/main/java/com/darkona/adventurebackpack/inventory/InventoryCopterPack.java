package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.FluidUtils;

import static com.darkona.adventurebackpack.common.Constants.COPTER_FUEL_TANK;

/**
 * Created on 02/01/2015
 *
 * @author Darkona
 */
public class InventoryCopterPack implements IInventoryTanks
{
    public FluidTank fuelTank = new FluidTank(Constants.COPTER_FUEL_CAPACITY);

    private ItemStack containerStack;
    public int tickCounter = 0;
    public byte status = ItemCopterPack.OFF_MODE;
    private ItemStack[] inventory = new ItemStack[2];

    public InventoryCopterPack(ItemStack copterPack)
    {
        status = ItemCopterPack.OFF_MODE;
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
        return inventory[i];
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
        if (FluidContainerRegistry.isFilledContainer(stack) && GeneralReference.isValidFuel(FluidContainerRegistry.getFluidForFilledItem(stack).getFluid()))
        {
            InventoryActions.transferContainerTank(this, fuelTank, 0);
        } else if (FluidContainerRegistry.isEmptyContainer(stack) && fuelTank.getFluid() != null && FluidUtils.isContainerForFluid(stack, fuelTank.getFluid().getFluid()))
        {
            InventoryActions.transferContainerTank(this, fuelTank, 0);
        }
        dirtyTanks();
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
        containerStack.stackTagCompound.setTag(COPTER_FUEL_TANK, fuelTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.setByte("status", status);
        containerStack.stackTagCompound.setInteger("tickCounter", this.tickCounter);
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.setTag(COPTER_FUEL_TANK, fuelTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void dirtyInventory()
    {

    }

    public void dirtyCounter()
    {
        containerStack.stackTagCompound.setInteger("tickCounter", this.tickCounter);
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

    public void closeInventoryNoStatus()
    {
        containerStack.stackTagCompound.setTag(COPTER_FUEL_TANK, this.fuelTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.setInteger("tickCounter", this.tickCounter);
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack)
    {
        return false;
    }

    public void onInventoryChanged()
    {
        ItemStack container = getStackInSlot(0);

        closeInventory();
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
                ItemStack result = inventory[slot].splitStack(amount);
                return result;
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

    @Override
    public boolean updateTankSlots()
    {
        return false;
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
        FluidTank[] tanks = {fuelTank};
        return tanks;
    }

    public void setStatus(byte status)
    {
        this.status = status;
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
