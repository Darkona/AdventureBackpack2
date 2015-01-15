package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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

    public InventorySteamJetpack(ItemStack jetpack)
    {
        containerStack = jetpack;
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
            inventory[3] = ItemStack.loadItemStackFromNBT(jetpackData.getCompoundTag("fuel"));
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
        jetpackData.setTag("fuel",inventory[2].writeToNBT(new NBTTagCompound()));

        compound.setTag("jetpackData",jetpackData);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] tanks = {waterTank,steamTank};
        return tanks;
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack stack)
    {

    }

    @Override
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
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
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
    {

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
        saveToNBT(containerStack.stackTagCompound);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if(slot < 2)return SlotFluid.valid(stack);
        return true;
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
}
