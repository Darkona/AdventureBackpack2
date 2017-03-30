package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.util.FluidUtils;

import static com.darkona.adventurebackpack.common.Constants.JETPACK_BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_COMPOUND_TAG;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_FUEL_SLOT;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_STEAM_TANK;
import static com.darkona.adventurebackpack.common.Constants.JETPACK_WATER_TANK;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class InventoryCoalJetpack implements IInventoryTanks
{

    private ItemStack[] inventory = new ItemStack[Constants.JETPACK_INVENTORY_SIZE];
    private FluidTank waterTank = new FluidTank(Constants.JETPACK_WATER_CAPACITY);
    private FluidTank steamTank = new FluidTank(Constants.JETPACK_STEAM_CAPACITY);

    private static final boolean OFF = false;
    private static final boolean ON = true;
    private boolean status = OFF;

    private int temperature = 25;
    private int burnTicks = 0;
    private ItemStack containerStack;
    private long systemTime = 0;
    private boolean boiling = false;
    private boolean leaking = false;
    private boolean inUse = false;
    public int currentItemBurnTime = 0;

    public static final int MAX_TEMPERATURE = 200;
    private int coolTicks = 5000;

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
    public boolean updateTankSlots()
    {
        return false;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackTag = compound.getCompoundTag(JETPACK_COMPOUND_TAG);
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
        NBTTagList items = jetpackTag.getTagList(JETPACK_INVENTORY, NBT.TAG_COMPOUND);
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
        NBTTagCompound jetpackTag = compound.getCompoundTag(JETPACK_COMPOUND_TAG);
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
        jetpackTag.setTag(JETPACK_INVENTORY, items);
        compound.setTag(JETPACK_COMPOUND_TAG, jetpackTag);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] tanks = {waterTank, steamTank};
        return tanks;
    }

    @Override
    public void dirtyInventory()
    {
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
        containerStack.stackTagCompound.getCompoundTag(JETPACK_COMPOUND_TAG).setTag(JETPACK_INVENTORY, items);
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag(JETPACK_COMPOUND_TAG).setTag(JETPACK_WATER_TANK, waterTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag(JETPACK_COMPOUND_TAG).setTag(JETPACK_STEAM_TANK, steamTank.writeToNBT(new NBTTagCompound()));
    }

    public void dirtyBoiler()
    {
        NBTTagCompound jetpackTag = containerStack.stackTagCompound.getCompoundTag(JETPACK_COMPOUND_TAG);
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

    public boolean isFuel(ItemStack stack)
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
                } else
                {
                    setInventorySlotContents(slot, null);
                }
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
        return (i == 0 || i == 1) ? inventory[i] : null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
        if (slot < JETPACK_FUEL_SLOT) onInventoryChanged();
        dirtyInventory();

    }

    public void onInventoryChanged()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (i == 0)
            {
                ItemStack container = getStackInSlot(i);
                if (FluidContainerRegistry.isFilledContainer(container) && FluidUtils.isContainerForFluid(container, FluidRegistry.WATER))
                {
                    InventoryActions.transferContainerTank(this, waterTank, i);
                } else if (FluidContainerRegistry.isEmptyContainer(container) && waterTank.getFluid() != null && FluidUtils.isContainerForFluid(container, FluidRegistry.WATER))
                {
                    InventoryActions.transferContainerTank(this, waterTank, i);
                }
            }
        }
        markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return "Coal Jetpack";
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
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
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
        if (slot == JETPACK_BUCKET_IN)
            return SlotFluid.isContainer(stack) && FluidUtils.isContainerForFluid(stack, FluidRegistry.WATER);
        if (slot == JETPACK_FUEL_SLOT)
            return TileEntityFurnace.isItemFuel(stack);
        return false;
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
        this.inventory = inventory;
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

    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
    }

    public ItemStack getContainerStack()
    {
        return containerStack;
    }

    public void calculateLostTime()
    {
        long elapsedTimesince = System.currentTimeMillis() - systemTime;
    }

}