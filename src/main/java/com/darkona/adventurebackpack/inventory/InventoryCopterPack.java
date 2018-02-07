package com.darkona.adventurebackpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemCopterPack;

import static com.darkona.adventurebackpack.common.Constants.Copter.BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.Copter.BUCKET_OUT;
import static com.darkona.adventurebackpack.common.Constants.Copter.INVENTORY_SIZE;
import static com.darkona.adventurebackpack.common.Constants.Copter.TAG_FUEL_TANK;
import static com.darkona.adventurebackpack.common.Constants.Copter.TAG_STATUS;
import static com.darkona.adventurebackpack.common.Constants.TAG_WEARABLE_COMPOUND;

/**
 * Created on 02/01/2015
 *
 * @author Darkona
 */
public class InventoryCopterPack extends InventoryAdventureBackpack
{
    public int tickCounter = 0;

    private FluidTank fuelTank = new FluidTank(Constants.Copter.FUEL_CAPACITY);
    private ItemStack[] inventory = new ItemStack[INVENTORY_SIZE];

    private byte status = ItemCopterPack.OFF_MODE;

    public InventoryCopterPack(ItemStack copterPack)
    {
        containerStack = copterPack;
        detectAndConvertFromOldNBTFormat(containerStack.stackTagCompound);
        openInventory();
    }

    private void detectAndConvertFromOldNBTFormat(NBTTagCompound compound) // backwards compatibility
    {
        if (compound == null || compound.hasKey(TAG_WEARABLE_COMPOUND))
            return;

        if (compound.hasKey(TAG_STATUS))
            compound.removeTag(TAG_STATUS);
        if (compound.hasKey("tickCounter"))
            compound.removeTag("tickCounter");

        fuelTank.readFromNBT(compound.getCompoundTag(TAG_FUEL_TANK));

        NBTTagCompound newCopterTag = new NBTTagCompound();
        newCopterTag.setTag(TAG_FUEL_TANK, fuelTank.writeToNBT(new NBTTagCompound()));

        compound.setTag(TAG_WEARABLE_COMPOUND, newCopterTag);
        compound.removeTag(TAG_FUEL_TANK);
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
            }
            else
            {
                itemstack = itemstack.splitStack(quantity);
            }
        }
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return (slot == BUCKET_IN || slot == BUCKET_OUT) ? inventory[slot] : null;
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
    public void loadFromNBT(NBTTagCompound compound)
    {
        NBTTagCompound copterTag = compound.getCompoundTag(TAG_WEARABLE_COMPOUND);
        fuelTank.readFromNBT(copterTag.getCompoundTag(TAG_FUEL_TANK));
        status = copterTag.getByte(TAG_STATUS);
        tickCounter = copterTag.getInteger("tickCounter");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound copterTag = new NBTTagCompound();
        copterTag.setTag(TAG_FUEL_TANK, fuelTank.writeToNBT(new NBTTagCompound()));
        copterTag.setByte(TAG_STATUS, status);
        copterTag.setInteger("tickCounter", this.tickCounter);
        compound.setTag(TAG_WEARABLE_COMPOUND, copterTag);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{fuelTank};
    }

    @Override
    public boolean updateTankSlots()
    {
        boolean result = false;
        while (InventoryActions.transferContainerTank(this, getFuelTank(), BUCKET_IN))
            result = true;
        return result;
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
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).setTag(TAG_FUEL_TANK, fuelTank.writeToNBT(new NBTTagCompound()));
    }

    //TODO to interface: getWearableCompound() { return containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND);}

    public void dirtyStatus()
    {
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).setByte(TAG_STATUS, status);
    }

    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
    }
}
