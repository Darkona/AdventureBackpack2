package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 02/01/2015
 *
 * @author Darkona
 */
public class InventoryInflatableBoat implements IInventoryTanks
{


    private ItemStack containerStack;
    private EntityInflatableBoat containerEntity;
    public FluidTank fuelTank;
    public int tickCounter;
    public byte status;
    private ItemStack[] inventory;

    public FluidTank getFuelTank()
    {
        return fuelTank;
    }

    public InventoryInflatableBoat(ItemStack copterPack)
    {
        this.containerStack = copterPack;
        this.fuelTank = new FluidTank(6000);
        this.status = ItemCopterPack.OFF_MODE;
        this.inventory = new ItemStack[2];
        openInventory();
    }

    public InventoryInflatableBoat(EntityInflatableBoat boat)
    {
        this.fuelTank = new FluidTank(4000);

    }
    public void consumeFuel(int quantity)
    {
        fuelTank.drain(quantity, true);
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
                onInventoryChanged();
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
        onInventoryChanged();
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

    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory()
    {
        NBTTagCompound compound = containerStack.getTagCompound() != null ? containerStack.stackTagCompound : new NBTTagCompound();
        if (compound.hasKey("fuelTank"))
        {
            this.fuelTank.readFromNBT(compound.getCompoundTag("fuelTank"));
        }
        if (compound.hasKey("status"))
        {
            this.status = compound.getByte("status");
        } else
        {
            this.status = ItemCopterPack.OFF_MODE;
        }
        if (compound.hasKey("tickCounter"))
        {
            this.tickCounter = compound.getInteger("tickCounter");
        } else
        {
            this.tickCounter = 0;
        }
    }

    @Override
    public void closeInventory()
    {

        NBTTagCompound compound = containerStack.hasTagCompound() ? containerStack.stackTagCompound : new NBTTagCompound();
        compound.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));
        compound.setByte("status", this.status);
        compound.setInteger("tickCounter", this.tickCounter);
        containerStack.stackTagCompound = compound;
    }

    public void closeInventoryNoStatus()
    {
        NBTTagCompound compound = containerStack.stackTagCompound;
        compound.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));
        compound.setInteger("tickCounter", this.tickCounter);
        containerStack.stackTagCompound = compound;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stack)
    {
        return false;
    }

    public void onInventoryChanged()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (i == 0)
            {
                ItemStack container = getStackInSlot(i);
                FluidStack oil = new FluidStack(FluidRegistry.getFluid("oil"), 1);
                FluidStack fuel = new FluidStack(FluidRegistry.getFluid("fuel"), 1);
                if (fuel != null && oil != null)
                {
                    if (FluidContainerRegistry.containsFluid(container, oil) || FluidContainerRegistry.containsFluid(container, fuel) || FluidContainerRegistry.isEmptyContainer(container))
                    {
                        InventoryActions.transferContainerTank(this, fuelTank, i);
                    }
                }
            }
        }
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

    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {

    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[0];
    }

    @Override
    public void dirtyInventory()
    {

    }

    @Override
    public void dirtyTanks()
    {

    }
}
