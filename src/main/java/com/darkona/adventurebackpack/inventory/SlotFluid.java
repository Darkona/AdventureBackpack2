package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.util.Utils;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class SlotFluid extends SlotAdventure
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

    static boolean isEmpty(FluidTank tank)
    {
        return tank.getFluidAmount() == 0;
    }

    static boolean isFilled(ItemStack stack)
    {
        return FluidContainerRegistry.isFilledContainer(stack);
    }

    static boolean isEqualFluid(ItemStack container, FluidTank tank)
    {
        return getFluidID(container) == getFluidID(tank);
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

    static boolean canFitToTank(ItemStack container, FluidTank tank)
    {
        return tank.getFluidAmount() + getCapacity(container) <= tank.getCapacity();
    }

    static boolean isEqualAndCanFit(ItemStack container, FluidTank tank)
    {
        return isEqualFluid(container, tank) && canFitToTank(container, tank);
    }

    static ItemStack getEmptyContainer(ItemStack container)
    {
        return FluidContainerRegistry.drainFluidContainer(container);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack != null && (isContainer(stack) || stack.getItem() instanceof ItemHose);
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
