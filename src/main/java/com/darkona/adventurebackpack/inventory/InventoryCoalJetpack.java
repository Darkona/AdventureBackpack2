package com.darkona.adventurebackpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;

import static com.darkona.adventurebackpack.common.Constants.Jetpack.BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.Jetpack.BUCKET_OUT;
import static com.darkona.adventurebackpack.common.Constants.Jetpack.FUEL_SLOT;
import static com.darkona.adventurebackpack.common.Constants.Jetpack.TAG_STEAM_TANK;
import static com.darkona.adventurebackpack.common.Constants.Jetpack.TAG_WATER_TANK;
import static com.darkona.adventurebackpack.common.Constants.TAG_INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.TAG_WEARABLE_COMPOUND;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class InventoryCoalJetpack extends InventoryAdventure
{
    private ItemStack[] inventory = new ItemStack[Constants.Jetpack.INVENTORY_SIZE];
    private FluidTank waterTank = new FluidTank(Constants.Jetpack.WATER_CAPACITY);
    private FluidTank steamTank = new FluidTank(Constants.Jetpack.STEAM_CAPACITY);

    private boolean inUse = false;
    private boolean status = false;
    private boolean boiling = false;
    private boolean leaking = false;
    private int temperature = 25;
    private int burnTicks = 0;
    private int coolTicks = 5000;
    private int currentItemBurnTime = 0;

    public InventoryCoalJetpack(final ItemStack jetpack)
    {
        containerStack = jetpack;
        detectAndConvertFromOldNBTFormat(containerStack.stackTagCompound);
        openInventory();
    }

    @Override
    public ItemStack[] getInventory()
    {
        return inventory;
    }

    public FluidTank getWaterTank()
    {
        return waterTank;
    }

    public FluidTank getSteamTank()
    {
        return steamTank;
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{waterTank, steamTank};
    }

    @Override
    public int[] getSlotsOnClosingArray()
    {
        return new int[]{BUCKET_IN, BUCKET_OUT};
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackTag = compound.getCompoundTag(TAG_WEARABLE_COMPOUND);
        setInventoryFromTagList(jetpackTag.getTagList(TAG_INVENTORY, NBT.TAG_COMPOUND));
        waterTank.readFromNBT(jetpackTag.getCompoundTag(TAG_WATER_TANK));
        steamTank.readFromNBT(jetpackTag.getCompoundTag(TAG_STEAM_TANK));
        inUse = jetpackTag.getBoolean("inUse");
        status = jetpackTag.getBoolean("status");
        boiling = jetpackTag.getBoolean("boiling");
        leaking = jetpackTag.getBoolean("leaking");
        temperature = jetpackTag.getInteger("temperature");
        burnTicks = jetpackTag.getInteger("burnTicks");
        coolTicks = jetpackTag.getInteger("coolTicks");
        currentItemBurnTime = jetpackTag.getInteger("currentBurn");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackTag = compound.getCompoundTag(TAG_WEARABLE_COMPOUND);
        jetpackTag.setTag(TAG_INVENTORY, getInventoryTagList());
        jetpackTag.setTag(TAG_WATER_TANK, waterTank.writeToNBT(new NBTTagCompound()));
        jetpackTag.setTag(TAG_STEAM_TANK, steamTank.writeToNBT(new NBTTagCompound()));
        jetpackTag.setBoolean("inUse", inUse);
        jetpackTag.setBoolean("status", status);
        jetpackTag.setBoolean("boiling", boiling);
        jetpackTag.setBoolean("leaking", leaking);
        jetpackTag.setInteger("temperature", temperature);
        jetpackTag.setInteger("burnTicks", burnTicks);
        jetpackTag.setInteger("coolTicks", coolTicks);
        jetpackTag.setInteger("currentBurn", currentItemBurnTime);
        compound.setTag(TAG_WEARABLE_COMPOUND, jetpackTag);
    }

    @Override
    public boolean updateTankSlots()
    {
        boolean changesMade = false;
        while (InventoryActions.transferContainerTank(this, getWaterTank(), BUCKET_IN))
            changesMade = true;
        return changesMade;
    }

    @Override
    public void dirtyTanks()
    {
        getWearableCompound().setTag(TAG_WATER_TANK, waterTank.writeToNBT(new NBTTagCompound()));
        getWearableCompound().setTag(TAG_STEAM_TANK, steamTank.writeToNBT(new NBTTagCompound()));
    }

    public void dirtyBoiler()
    {
        NBTTagCompound jetpackTag = getWearableCompound();
        jetpackTag.setBoolean("boiling", boiling);
        jetpackTag.setBoolean("leaking", leaking);
        jetpackTag.setInteger("temperature", temperature);
        jetpackTag.setInteger("burnTicks", burnTicks);
        jetpackTag.setInteger("coolTicks", coolTicks);
        jetpackTag.setInteger("currentBurn", currentItemBurnTime);
    }

    public int getBurnTimeRemainingScaled(int scale)
    {
        if (this.currentItemBurnTime == 0)
            this.currentItemBurnTime = 200;

        return this.burnTicks * scale / this.currentItemBurnTime;
    }

    public int consumeFuel()
    {
        int result = 0;
        if (isFuel(inventory[FUEL_SLOT]))
        {
            result = TileEntityFurnace.getItemBurnTime(inventory[FUEL_SLOT]);
            --inventory[FUEL_SLOT].stackSize;
            if (inventory[FUEL_SLOT].stackSize == 0)
            {
                inventory[FUEL_SLOT] = inventory[FUEL_SLOT].getItem().getContainerItem(inventory[FUEL_SLOT]);
            }
            dirtyInventory();
        }
        return result;
    }

    private boolean isFuel(ItemStack stack)
    {
        return TileEntityFurnace.isItemFuel(stack);
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


    public boolean isInUse()
    {
        return inUse;
    }

    public void setInUse(boolean inUse)
    {
        this.inUse = inUse;
    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
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

    public int getTemperature()
    {
        return temperature;
    }

    public void setTemperature(int temperature)
    {
        this.temperature = temperature;
    }

    public int getBurnTicks()
    {
        return burnTicks;
    }

    public void setBurnTicks(int burnTicks)
    {
        this.burnTicks = burnTicks;
    }

    public int getCoolTicks()
    {
        return coolTicks;
    }

    public void setCoolTicks(int coolTicks)
    {
        this.coolTicks = coolTicks;
    }

    public void setCurrentItemBurnTime(int currentItemBurnTime)
    {
        this.currentItemBurnTime = currentItemBurnTime;
    }

    private void detectAndConvertFromOldNBTFormat(NBTTagCompound compound) // backwards compatibility
    {
        if (compound == null || !compound.hasKey("jetpackData"))
            return;

        NBTTagCompound oldJetpackTag = compound.getCompoundTag("jetpackData");
        NBTTagList oldItems = oldJetpackTag.getTagList("inventory", NBT.TAG_COMPOUND);
        waterTank.readFromNBT(oldJetpackTag.getCompoundTag("waterTank"));
        steamTank.readFromNBT(oldJetpackTag.getCompoundTag("steamTank"));

        NBTTagCompound newJetpackTag = new NBTTagCompound();
        newJetpackTag.setTag(TAG_INVENTORY, oldItems);
        newJetpackTag.setTag(TAG_WATER_TANK, waterTank.writeToNBT(new NBTTagCompound()));
        newJetpackTag.setTag(TAG_STEAM_TANK, steamTank.writeToNBT(new NBTTagCompound()));

        compound.setTag(TAG_WEARABLE_COMPOUND, newJetpackTag);
        compound.removeTag("jetpackData");
    }
}