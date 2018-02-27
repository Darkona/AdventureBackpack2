package com.darkona.adventurebackpack.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 12/01/2015
 *
 * @author Darkona
 */
public interface IInventoryTanks extends IAsynchronousInventory
{
    ItemStack[] getInventory();

    FluidTank[] getTanksArray();

    int[] getSlotsOnClosingArray();

    void loadFromNBT(NBTTagCompound compound);

    void saveToNBT(NBTTagCompound compound);

    void dirtyInventory();

    boolean updateTankSlots();

    void dirtyTanks();
}
