package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.util.FluidUtils;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class InventorySteamJetpack implements IInventoryTanks
{
    private FluidTank waterTank = new FluidTank(6000);
    private FluidTank steamTank = new FluidTank(12000);
    private ItemStack[] inventory = new ItemStack[3];
    private int temperature = 0;
    private byte status = 0;
    private int burnTicks = 0;
    private ItemStack containerStack;
    private long systemTime = 0;
    private boolean boiling = false;
    private boolean leaking = false;
    private boolean inUse = false;

    public static final int BUCKET_IN_SLOT = 0;
    public static final int BUCKET_OUT_SLOT = 1;
    public static final int FUEL_SLOT = 2;

    public InventorySteamJetpack(final ItemStack jetpack)
    {
        this.containerStack = jetpack;
        if (!jetpack.hasTagCompound())
        {
            jetpack.stackTagCompound = new NBTTagCompound();
            saveToNBT(jetpack.stackTagCompound);
        }
       loadFromNBT(jetpack.stackTagCompound);
    }

    @Override
    public boolean updateTankSlots()
    {
        return false;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if(compound.hasKey("jetpackData"))
        {
            NBTTagCompound jetpackData = compound.getCompoundTag("jetpackData");
            waterTank.readFromNBT(jetpackData.getCompoundTag("waterTank"));
            steamTank.readFromNBT(jetpackData.getCompoundTag("steamTank"));
            temperature = jetpackData.getInteger("temperature");
            status = jetpackData.getByte("status");
            burnTicks = jetpackData.getInteger("burnTicks");
            systemTime = jetpackData.getLong("systemTime");
            inUse = jetpackData.getBoolean("inUse");
            boiling = jetpackData.getBoolean("boiling");
            leaking = jetpackData.getBoolean("leaking");
            if(jetpackData.hasKey("fuel"))inventory[2] = ItemStack.loadItemStackFromNBT(jetpackData.getCompoundTag("fuel"));
        }
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackData = new NBTTagCompound();
        jetpackData.setTag("waterTank",waterTank.writeToNBT(new NBTTagCompound()));
        jetpackData.setTag("steamTank",steamTank.writeToNBT(new NBTTagCompound()));
        jetpackData.setInteger("temperature", temperature);
        jetpackData.setByte("status", status);
        jetpackData.setInteger("burnTicks", burnTicks);
        jetpackData.setLong("systemTime",systemTime);
        jetpackData.setBoolean("inUse", inUse);
        jetpackData.setBoolean("boiling",boiling);
        jetpackData.setBoolean("leaking",leaking);
        if(inventory[2]!=null)jetpackData.setTag("fuel",inventory[2].writeToNBT(new NBTTagCompound()));
        compound.setTag("jetpackData",jetpackData);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] tanks = {waterTank,steamTank};
        return tanks;
    }

    @Override
    public void dirtyInventory()
    {

    }

    @Override
    public void dirtyTanks()
    {

    }

    public void consumeFuel()
    {
        if(inventory[FUEL_SLOT] != null && TileEntityFurnace.getItemBurnTime(inventory[FUEL_SLOT]) > 0 && burnTicks <= 0)
        {
            LogHelper.info("Consuming fuel for value in ticks" + TileEntityFurnace.getItemBurnTime(inventory[FUEL_SLOT]));
            burnTicks = TileEntityFurnace.getItemBurnTime(inventory[FUEL_SLOT]);
            --inventory[FUEL_SLOT].stackSize;

            if (inventory[FUEL_SLOT].stackSize == 0)
            {
                inventory[FUEL_SLOT] = inventory[FUEL_SLOT].getItem().getContainerItem(inventory[FUEL_SLOT]);
            }
        }
        closeInventory();
    }

    public void runBoiler()
    {
        if(temperature < 200 && burnTicks > 0)
        {
            int heatFactor = 200;
            if(burnTicks % heatFactor == 0)
            {
                if(burnTicks > 0)
                {
                    ++temperature;
                    --burnTicks;
                    LogHelper.info("Burnticks is: " + burnTicks);
                    LogHelper.info("Boiler is heating up temperature is: " + temperature);
                }

            }

        }else
        {
            --temperature;
            LogHelper.info("Burnticks is: " + burnTicks);
            LogHelper.info("Boiler is cooling down temperature is: " + temperature);
        }

        if(temperature > 100)
        {
            if(!boiling)
            {
                boiling = true;
                LogHelper.info("Boiler boiling");
                //TODO start playing boiling sound
            }
            if(steamTank.getFluidAmount() < steamTank.getCapacity())
            {
                if(leaking)
                {
                    leaking = false;
                }
                int steam = waterTank.drain((temperature/100),true).amount;
                steamTank.fill(new FluidStack(FluidRegistry.LAVA, steam * 4), true);
            }else
            {
                if(!leaking)
                {
                    leaking = true;
                }
            }
        }
        else
        {
            if(boiling)
            {
                boiling = false;
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
        return (i == 0 || i== 1) ? inventory[i] : null;
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

    public void onInventoryChanged()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (i == 0)
            {
                ItemStack container = getStackInSlot(i);
                if(FluidContainerRegistry.isFilledContainer(container) && FluidUtils.isContainerForFluid(container, FluidRegistry.WATER))
                {
                    InventoryActions.transferContainerTank(this, waterTank, i);
                }else
                if(FluidContainerRegistry.isEmptyContainer(container) && waterTank.getFluid()!=null && FluidUtils.isContainerForFluid(container, FluidRegistry.WATER))
                {
                    InventoryActions.transferContainerTank(this, waterTank, i);
                }
            }
        }
        closeInventory();
    }
    @Override
    public String getInventoryName()
    {
        return "Steam Jetpack";
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
        if(Utils.inServer()){
            LogHelper.info("saving inventory");
            saveToNBT(containerStack.stackTagCompound);
        }
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if(slot == BUCKET_IN_SLOT)return SlotFluid.valid(stack) && FluidUtils.isContainerForFluid(stack, FluidRegistry.WATER);
        if(slot == FUEL_SLOT)return TileEntityFurnace.isItemFuel(stack);
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

    public byte getStatus()
    {
        return status;
    }

    public void setStatus(byte status)
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

    public ItemStack getContainerStack()
    {
        return containerStack;
    }

    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
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

    public void setSteamTank(FluidTank steamTank)
    {
        this.steamTank = steamTank;
    }

    public void setWaterTank(FluidTank waterTank)
    {
        this.waterTank = waterTank;
    }
}
