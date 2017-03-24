package com.darkona.adventurebackpack.inventory;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 12/01/2015
 *
 * @author Darkona
 */
public interface IInventoryTanks extends IAsynchronousInventory
{
    boolean updateTankSlots();

    void loadFromNBT(NBTTagCompound compound);

    void saveToNBT(NBTTagCompound compound);

    FluidTank[] getTanksArray();

    void dirtyInventory();

    void dirtyTanks();
}
