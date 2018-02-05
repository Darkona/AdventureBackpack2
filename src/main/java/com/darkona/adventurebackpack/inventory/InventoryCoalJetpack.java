package com.darkona.adventurebackpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;

import static com.darkona.adventurebackpack.common.Constants.WEARABLE_TAG;
import static com.darkona.adventurebackpack.common.Constants.INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_BUCKET_OUT;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_FUEL_SLOT;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_STEAM_TANK;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_WATER_TANK;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class InventoryCoalJetpack extends InventoryAdventureBackpack
{
    public int currentItemBurnTime = 0;

    private ItemStack[] inventory = new ItemStack[Constants.JETPACK_INVENTORY_SIZE];
    private FluidTank waterTank = new FluidTank(Constants.JETPACK_WATER_CAPACITY);
    private FluidTank steamTank = new FluidTank(Constants.JETPACK_STEAM_CAPACITY);

    private boolean boiling = false;
    private boolean inUse = false;
    private boolean leaking = false;
    private boolean status = false;
    private int temperature = 25;
    private int burnTicks = 0;
    private int coolTicks = 5000;
    private long systemTime = 0;

    public InventoryCoalJetpack(final ItemStack jetpack)
    {
        containerStack = jetpack;
        openInventory();
    }

    public int getBurnTimeRemainingScaled(int scale)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = 200;
        }

        return this.burnTicks * scale / this.currentItemBurnTime;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackTag = compound.getCompoundTag(WEARABLE_TAG);
        waterTank.readFromNBT(jetpackTag.getCompoundTag(JETPACK_WATER_TANK));
        steamTank.readFromNBT(jetpackTag.getCompoundTag(JETPACK_STEAM_TANK));
        temperature = jetpackTag.getInteger("temperature");
        status = jetpackTag.getBoolean("status");
        burnTicks = jetpackTag.getInteger("burnTicks");
        coolTicks = jetpackTag.getInteger("coolTicks");
        systemTime = jetpackTag.getLong("systemTime");
        inUse = jetpackTag.getBoolean("inUse");
        boiling = jetpackTag.getBoolean("boiling");
        leaking = jetpackTag.getBoolean("leaking");
        currentItemBurnTime = jetpackTag.getInteger("currentBurn");
        NBTTagList items = jetpackTag.getTagList(INVENTORY, NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackTag = compound.getCompoundTag(WEARABLE_TAG);
        jetpackTag.setTag(JETPACK_WATER_TANK, waterTank.writeToNBT(new NBTTagCompound()));
        jetpackTag.setTag(JETPACK_STEAM_TANK, steamTank.writeToNBT(new NBTTagCompound()));
        jetpackTag.setInteger("temperature", temperature);
        jetpackTag.setBoolean("status", status);
        jetpackTag.setInteger("burnTicks", burnTicks);
        jetpackTag.setInteger("coolTicks", coolTicks);
        jetpackTag.setLong("systemTime", systemTime);
        jetpackTag.setBoolean("inUse", inUse);
        jetpackTag.setBoolean("boiling", boiling);
        jetpackTag.setBoolean("leaking", leaking);
        jetpackTag.setInteger("currentBurn", currentItemBurnTime);
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++)
        {
            ItemStack stack = inventory[i];
            if (stack != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        jetpackTag.setTag(INVENTORY, items);
        compound.setTag(WEARABLE_TAG, jetpackTag);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{waterTank, steamTank};
    }

    @Override
    public boolean updateTankSlots()
    {
        boolean result = false;
        while (InventoryActions.transferContainerTank(this, getWaterTank(), JETPACK_BUCKET_IN))
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
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++)
        {
            ItemStack stack = inventory[i];
            if (stack != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setTag(INVENTORY, items);
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setTag(JETPACK_WATER_TANK, waterTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setTag(JETPACK_STEAM_TANK, steamTank.writeToNBT(new NBTTagCompound()));
    }

    public void dirtyBoiler()
    {
        NBTTagCompound jetpackTag = containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG);
        jetpackTag.setBoolean("boiling", boiling);
        jetpackTag.setBoolean("leaking", leaking);
        jetpackTag.setInteger("temperature", temperature);
        jetpackTag.setInteger("burnTicks", burnTicks);
        jetpackTag.setInteger("coolTicks", coolTicks);
        jetpackTag.setInteger("currentBurn", currentItemBurnTime);
    }

    public int consumeFuel()
    {
        int result = 0;
        if (isFuel(inventory[JETPACK_FUEL_SLOT]))
        {
            result = TileEntityFurnace.getItemBurnTime(inventory[JETPACK_FUEL_SLOT]);
            --inventory[JETPACK_FUEL_SLOT].stackSize;
            if (inventory[JETPACK_FUEL_SLOT].stackSize == 0)
            {
                inventory[JETPACK_FUEL_SLOT] = inventory[JETPACK_FUEL_SLOT].getItem().getContainerItem(inventory[JETPACK_FUEL_SLOT]);
            }
            dirtyInventory();
        }
        return result;
    }

    private boolean isFuel(ItemStack stack)
    {
        return TileEntityFurnace.isItemFuel(stack);
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

    @Override
    public ItemStack decrStackSize(int slot, int quantity)
    {
        ItemStack itemstack = getStackInSlot(slot);

        if (itemstack != null)
        {
            if (itemstack.stackSize <= quantity)
            {
                if (slot == JETPACK_FUEL_SLOT)
                {
                    setInventorySlotContents(slot, itemstack.getItem().getContainerItem(itemstack));
                }
                else
                {
                    setInventorySlotContents(slot, null);
                }
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
        return (slot == JETPACK_BUCKET_IN || slot == JETPACK_BUCKET_OUT) ? inventory[slot] : null;
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

    public ItemStack getParentItemStack()
    {
        return containerStack;
    }

    public FluidTank getWaterTank()
    {
        return waterTank;
    }

    public FluidTank getSteamTank()
    {
        return steamTank;
    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }

    public int getTemperature()
    {
        return temperature;
    }

    public void setTemperature(int temperature)
    {
        this.temperature = temperature;
    }

    public boolean isInUse()
    {
        return inUse;
    }

    public void setInUse(boolean inUse)
    {
        this.inUse = inUse;
    }

    public boolean isBoiling()
    {
        return boiling;
    }

    public void setBoiling(boolean boiling)
    {
        this.boiling = boiling;
    }

    public boolean isLeaking()
    {
        return leaking;
    }

    public void setLeaking(boolean leaking)
    {
        this.leaking = leaking;
    }

    public long getSystemTime()
    {
        return systemTime;
    }

    public void setSystemTime(long systemTime)
    {
        this.systemTime = systemTime;
    }

    public int getBurnTicks()
    {
        return burnTicks;
    }

    public void setBurnTicks(int burnTicks)
    {
        this.burnTicks = burnTicks;
    }

    public ItemStack[] getInventory()
    {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory)
    {
        this.inventory = inventory; //TODO wtf
    }

    public int getIncreasingFactor()
    {
        if (temperature < 50) return 20;
        if (temperature < 100) return 15;
        if (temperature < 150) return 10;
        return 5;
    }

    public int getDecreasingFactor()
    {
        if (temperature > 150) return 40;
        if (temperature > 100) return 80;
        if (temperature > 50) return 120;
        return 5;
    }

    public int getCoolTicks()
    {
        return coolTicks;
    }

    public void setCoolTicks(int coolTicks)
    {
        this.coolTicks = coolTicks;
    }

    public ItemStack getContainerStack()
    {
        return containerStack;
    }

    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
    }

    public void calculateLostTime()
    {
        long elapsedTimesince = System.currentTimeMillis() - systemTime;
    }
}