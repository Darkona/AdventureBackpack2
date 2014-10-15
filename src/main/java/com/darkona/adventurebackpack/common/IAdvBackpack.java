package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.blocks.TileAdventureBackpack;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public interface IAdvBackpack extends IInventory {

    public FluidTank getLeftTank();

    public FluidTank getRightTank();

    public void setLeftTank(FluidTank tank);

    public void setRightTank(FluidTank tank);

    public NBTTagCompound writeToNBT();

    public boolean updateTankSlots(FluidTank tank, int slotIN);

    public TileAdventureBackpack getTile();
}
