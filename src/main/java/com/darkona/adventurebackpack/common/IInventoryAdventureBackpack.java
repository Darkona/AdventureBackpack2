package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.sort.SlotLocks;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import adventurebackpack.api.IInventoryTanks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

public interface IInventoryAdventureBackpack extends IInventoryTanks
{
    public FluidTank getLeftTank();

    public FluidTank getRightTank();

    public ItemStack[] getInventory();

    int getStorageSize();

    WidgetLayout getLayout();

    public TileAdventureBackpack getTile();

    public ItemStack getParentItemStack();

    public String getColorName();

    public int getLastTime();

    public NBTTagCompound getExtendedProperties();

    public void setExtendedProperties(NBTTagCompound properties);

    public boolean isSpecial();

    public void saveTanks(NBTTagCompound compound);

    public void loadTanks(NBTTagCompound compound);

    public boolean hasItem(Item item);

    void consumeInventoryItem(Item item);

    boolean isSBDeployed();

    void setLastTime(int time);

    public void dirtyTime();

    public void dirtyExtended();

    SlotLocks getLocks();

    void dirtyLocks();
}
