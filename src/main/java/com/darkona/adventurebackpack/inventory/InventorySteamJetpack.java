package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.util.FluidUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class InventorySteamJetpack implements IInventoryTanks
{
    public static final boolean OFF = false;
    public static final boolean ON = true;
    private FluidTank waterTank = new FluidTank(6000);
    private FluidTank steamTank = new FluidTank(12000);
    private ItemStack[] inventory = new ItemStack[3];
    private int temperature = 25;
    private boolean status = OFF;
    private int burnTicks = 0;
    private ItemStack containerStack;
    private long systemTime = 0;
    private boolean boiling = false;
    private boolean leaking = false;
    private boolean inUse = false;
    public int currentItemBurnTime = 0;


    public static final int MAX_TEMPERATURE = 200;
    public static final int BUCKET_IN_SLOT = 0;
    public static final int BUCKET_OUT_SLOT = 1;
    public static final int FUEL_SLOT = 2;
    private int coolTicks = 5000;

    public InventorySteamJetpack(final ItemStack jetpack)
    {
        this.containerStack = jetpack;
        if (!containerStack.hasTagCompound())
        {
            containerStack.stackTagCompound = new NBTTagCompound();
            closeInventory();
        }
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
        if(compound.hasKey("jetpackData"))
        {
            NBTTagCompound jetpackData = compound.getCompoundTag("jetpackData");
            waterTank.readFromNBT(jetpackData.getCompoundTag("waterTank"));
            steamTank.readFromNBT(jetpackData.getCompoundTag("steamTank"));
            temperature = jetpackData.getInteger("temperature");
            status = jetpackData.getBoolean("status");
            burnTicks = jetpackData.getInteger("burnTicks");
            coolTicks = jetpackData.getInteger("coolTicks");
            systemTime = jetpackData.getLong("systemTime");
            inUse = jetpackData.getBoolean("inUse");
            boiling = jetpackData.getBoolean("boiling");
            leaking = jetpackData.getBoolean("leaking");
            currentItemBurnTime = jetpackData.getInteger("currentBurn");
            NBTTagList items = jetpackData.getTagList("inventory", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
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
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackData;
        if(compound.hasKey("jetpackData"))
        {
            jetpackData = compound.getCompoundTag("jetpackData");
        }else
        {
            jetpackData = new NBTTagCompound();
        }

        jetpackData.setTag("waterTank",waterTank.writeToNBT(new NBTTagCompound()));
        jetpackData.setTag("steamTank",steamTank.writeToNBT(new NBTTagCompound()));
        jetpackData.setInteger("temperature", temperature);
        jetpackData.setBoolean("status", status);
        jetpackData.setInteger("burnTicks", burnTicks);
        jetpackData.setInteger("coolTicks", coolTicks);
        jetpackData.setLong("systemTime",systemTime);
        jetpackData.setBoolean("inUse", inUse);
        jetpackData.setBoolean("boiling",boiling);
        jetpackData.setBoolean("leaking",leaking);
        jetpackData.setInteger("currentBurn",currentItemBurnTime);
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
        jetpackData.setTag("inventory", items);
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
        containerStack.stackTagCompound.getCompoundTag("jetpackData").setTag("inventory", items);
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag("jetPackData").setTag("waterTank",waterTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag("jetPackData").setTag("steamTank",steamTank.writeToNBT(new NBTTagCompound()));
    }

    public void dirtyBoiler()
    {
        NBTTagCompound jetpackData = containerStack.stackTagCompound.getCompoundTag("jetPackData");
        jetpackData.setBoolean("boiling", boiling);
        jetpackData.setBoolean("leaking", leaking);
        jetpackData.setInteger("temperature", temperature);
        jetpackData.setInteger("burnTicks", burnTicks);
        jetpackData.setInteger("coolTicks", coolTicks);
        jetpackData.setInteger("currentBurn",currentItemBurnTime);
    }

    public int consumeFuel()
    {
        int result = 0;
        if(isFuel(inventory[FUEL_SLOT]))
        {
            result = TileEntityFurnace.getItemBurnTime(inventory[FUEL_SLOT]);
            --inventory[FUEL_SLOT].stackSize;
            if(inventory[FUEL_SLOT].stackSize == 0)
            {
                inventory[FUEL_SLOT] = inventory[FUEL_SLOT].getItem().getContainerItem(inventory[FUEL_SLOT]);
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
                if(slot == FUEL_SLOT)
                {
                    setInventorySlotContents(slot, itemstack.getItem().getContainerItem(itemstack));
                }
                else
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
        if(slot < FUEL_SLOT)onInventoryChanged();
        dirtyInventory();

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
        markDirty();
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
       if(temperature < 50)return 20;
       if(temperature < 100)return 15;
       if(temperature < 150)return 10;
       return 5;
    }

    public int getDecreasingFactor()
    {
        if(temperature > 150)return 40;
        if(temperature > 100)return 80;
        if(temperature > 50)return 120;
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
        @SuppressWarnings("unused")
		long elapsedTimesince = System.currentTimeMillis() - systemTime;

    }
}
