package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.inventory.IInventoryTanks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public interface IAdvBackpack extends IInventoryTanks
{

    public FluidTank getLeftTank();

    public FluidTank getRightTank();

    public ItemStack[] getInventory();

    public TileAdventureBackpack getTile();

    public ItemStack getParentItemStack();

    public String getColorName();

    public int getLastTime();

    public NBTTagCompound getExtendedProperties();

    public void setExtendedProperties(NBTTagCompound properties);

    public boolean isSpecial();

    public void saveTanks();

    public void loadTanks();

    public NBTTagCompound writeToNBT();

    public void readFromNBT();

    public boolean hasItem(Item item);

    void consumeInventoryItem(Item item);

    void onInventoryChanged();

    void saveChanges();

    boolean isSBDeployed();

    void setLastTime(int time);
}
