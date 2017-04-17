package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.util.Utils;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class SlotFluid extends SlotAdventureBackpack
{
    SlotFluid(IInventory inventory, int slotIndex, int posX, int posY)
    {
        super(inventory, slotIndex, posX, posY);
    }

    static boolean isContainer(ItemStack stack)
    {
        return FluidContainerRegistry.isContainer(stack);
    }

    static boolean isEmpty(ItemStack stack)
    {
        return FluidContainerRegistry.isEmptyContainer(stack);
    }

    static boolean isFilled(ItemStack stack)
    {
        return FluidContainerRegistry.isFilledContainer(stack);
    }

    static boolean isBucket(ItemStack stack)
    {
        return FluidContainerRegistry.isBucket(stack);
    }

    static boolean isEmptyBucket(ItemStack stack)
    {
        return FluidContainerRegistry.isBucket(stack) && isEmpty(stack);
    }

    static boolean isFilledBucket(ItemStack stack)
    {
        return FluidContainerRegistry.isBucket(stack) && isFilled(stack);
    }

    static String getFluidName(ItemStack stack)
    {
        if (stack == null || isEmpty(stack))
            return "";
        return FluidContainerRegistry.getFluidForFilledItem(stack).getFluid().getName();
    }

    static String getFluidName(FluidTank tank)
    {
        if (tank == null || tank.getFluidAmount() <= 0)
            return "";
        return tank.getFluid().getFluid().getName();
    }

    static int getFluidID(ItemStack stack)
    {
        if (stack == null || isEmpty(stack))
            return -1;
        return FluidContainerRegistry.getFluidForFilledItem(stack).getFluid().getID();
    }

    static int getFluidID(FluidTank tank)
    {
        if (tank == null || tank.getFluidAmount() <= 0)
            return -1;
        return tank.getFluid().getFluid().getID();
    }

    static Fluid getFluid(ItemStack stack)
    {
        if (stack == null || isEmpty(stack))
            return null;
        return FluidContainerRegistry.getFluidForFilledItem(stack).getFluid();
    }

    static int getCapacity(ItemStack stack)
    {
        return FluidContainerRegistry.getContainerCapacity(stack);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack != null && isContainer(stack);
    }

    @Override
    public void onSlotChanged()
    {
        if (Utils.inServer())
        {
            if (inventory instanceof IInventoryTanks)
            {
                ((IInventoryTanks) this.inventory).updateTankSlots();
            }
        }
        super.onSlotChanged();
    }

    @Override
    public int getSlotStackLimit()
    {
        return Constants.BASIC_TANK_CAPACITY / Constants.BUCKET;
    }

}
