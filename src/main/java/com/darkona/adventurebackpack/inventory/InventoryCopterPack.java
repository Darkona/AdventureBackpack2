package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.util.FluidUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 02/01/2015
 *
 * @author Darkona
 */
public class InventoryCopterPack implements IInventoryTanks
{
    private ItemStack containerStack;
    public FluidTank fuelTank;
    public int tickCounter;
    public byte status;
    private ItemStack[] inventory;



    public InventoryCopterPack(ItemStack copterPack)
    {
        fuelTank = new FluidTank(6000);
        status = ItemCopterPack.OFF_MODE;
        inventory = new ItemStack[2];
        containerStack = copterPack;
        if(!copterPack.hasTagCompound())
        {
            copterPack.stackTagCompound = new NBTTagCompound();
            saveToNBT(copterPack.stackTagCompound);
        }

        openInventory();
    }

    public FluidTank getFuelTank()
    {
        return fuelTank;
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
      loadFromNBT(containerStack.stackTagCompound);
    }

    @Override
    public void closeInventory()
    {
        saveToNBT(containerStack.stackTagCompound);
    }

    public void closeInventoryNoStatus()
    {
        containerStack.stackTagCompound.setTag("fuelTank", this.fuelTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.setInteger("tickCounter", this.tickCounter);
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
                if(FluidContainerRegistry.isFilledContainer(container) && FluidUtils.isValidFuel(FluidContainerRegistry.getFluidForFilledItem(container).getFluid()))
                {
                    InventoryActions.transferContainerTank(this, fuelTank, i);
                }else
                if(FluidContainerRegistry.isEmptyContainer(container) && fuelTank.getFluid()!=null && FluidUtils.isContainerForFluid(container, fuelTank.getFluid().getFluid()))
                {
                    InventoryActions.transferContainerTank(this, fuelTank, i);
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

    public void updateTankSlots(FluidTank tank, int slotIN)
    {

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
        fuelTank.readFromNBT(compound.getCompoundTag("fuelTank"));
        status = compound.getByte("status");
        tickCounter = compound.getInteger("tickCounter");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        compound.setTag("fuelTank", fuelTank.writeToNBT(new NBTTagCompound()));
        compound.setByte("status", status);
        compound.setInteger("tickCounter", this.tickCounter);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] tanks = {fuelTank};
        return tanks;
    }
}
