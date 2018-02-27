package com.darkona.adventurebackpack.inventory;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.reference.BackpackTypes;

/**
 * Created by Darkona on 12/10/2014.
 */
public interface IInventoryBackpack extends IInventoryTanks
{
    BackpackTypes getType();

    FluidTank getLeftTank();

    FluidTank getRightTank();

    NBTTagCompound getExtendedProperties(); //TODO move to IInventoryTanks to use with Copter/Jet?

    void dirtyExtended();

    void dirtyTime();

    int getLastTime();

    void setLastTime(int time);

    boolean hasItem(Item item);

    void consumeInventoryItem(Item item);

    boolean isSleepingBagDeployed();
}
