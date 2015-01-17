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
    public boolean updateTankSlots();

    public void loadFromNBT(NBTTagCompound compound);

    public void saveToNBT(NBTTagCompound compound);

    public FluidTank[] getTanksArray();

    public void dirtyInventory();

    public void dirtyTanks();
}
