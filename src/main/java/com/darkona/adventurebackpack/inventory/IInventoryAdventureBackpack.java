package com.darkona.adventurebackpack.inventory;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.reference.BackpackTypes;

/**
 * Created by Darkona on 12/10/2014.
 */
public interface IInventoryAdventureBackpack extends IInventoryTanks
{
    FluidTank getLeftTank();

    FluidTank getRightTank();

    ItemStack[] getInventory();

    TileAdventureBackpack getTile();

    ItemStack getParentItemStack();

    //String getColorName();

    BackpackTypes getType();

    int getLastTime();

    NBTTagCompound getExtendedProperties();

    void setExtendedProperties(NBTTagCompound properties);

    boolean isSpecial();

    boolean hasItem(Item item);

    void consumeInventoryItem(Item item);

    boolean isSBDeployed();

    void setLastTime(int time);

    void dirtyTime();

    void dirtyExtended();
}
