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

    //TODO



    void loadFromNBT(NBTTagCompound compound);

    void saveToNBT(NBTTagCompound compound);

    boolean updateTankSlots();

    void dirtyTanks();

    void dirtyInventory();
}
