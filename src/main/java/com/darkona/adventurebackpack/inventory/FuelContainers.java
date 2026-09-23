package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Fuel containers for the container slots of the Flamethrower Pack: any container of {@link FluidContainerRegistry}
 * (BuildCraft buckets, Forestry cans and capsules, GregTech and IC2 cells, Railcraft buckets) of a fuel on
 * fuels.list, full to empty into the tanks, or empty to fill from them. Runs on player actions only (Tier 2), and
 * catches what another mod's container throws.
 */
public final class FuelContainers
{
    private FuelContainers() {}

    /** True for a registered container of a fuel (full), or an empty container that some fuel fits in. */
    public static boolean isFuelContainer(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return false;
        try
        {
            FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (contained != null) return GeneralReference.isValidFuel(contained);
            return FluidContainerRegistry.isEmptyContainer(stack);
        } catch (RuntimeException foreign)
        {
            return false;
        }
    }

    /**
     * Moves one item of slot {@code in} through the tank and into slot {@code out}: a full fuel container is emptied
     * into the tank, an empty one is filled from it. Returns true when something moved. Works on the slot array so
     * the caller decides when to save.
     */
    public static boolean process(ItemStack[] slots, int in, int out, FluidTank tank)
    {
        ItemStack stack = slots[in];
        if (stack == null || stack.getItem() == null || tank == null) return false;
        try
        {
            FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (contained != null)
            {
                if (!GeneralReference.isValidFuel(contained) || tank.fill(contained, false) != contained.amount) return false;
                ItemStack emptied = FluidContainerRegistry.drainFluidContainer(stack);
                if (!fits(slots[out], emptied)) return false;
                tank.fill(contained, true);
                move(slots, in, out, emptied);
                return true;
            }
            FluidStack inTank = tank.getFluid();
            if (inTank == null || inTank.amount <= 0 || !FluidContainerRegistry.isEmptyContainer(stack)) return false;
            int capacity = FluidContainerRegistry.getContainerCapacity(inTank, stack);
            if (capacity <= 0 || inTank.amount < capacity) return false;
            ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(inTank.getFluid(), capacity), stack);
            if (filled == null || !fits(slots[out], filled)) return false;
            tank.drain(capacity, true);
            move(slots, in, out, filled);
            return true;
        } catch (RuntimeException foreign)
        {
            LogHelper.error("Could not move fuel through " + stack + ": " + foreign);
        }
        return false;
    }

    /** Runs {@link #process} until nothing more moves (bounded by the stack in the slot). */
    public static boolean processAll(ItemStack[] slots, int in, int out, FluidTank tank)
    {
        boolean moved = false;
        for (int i = 0; i < 64 && process(slots, in, out, tank); i++)
        {
            moved = true;
        }
        return moved;
    }

    private static boolean fits(ItemStack inOut, ItemStack result)
    {
        if (result == null || inOut == null) return true;
        return inOut.isItemEqual(result) && ItemStack.areItemStackTagsEqual(inOut, result)
                && inOut.stackSize + 1 <= inOut.getMaxStackSize();
    }

    private static void move(ItemStack[] slots, int in, int out, ItemStack result)
    {
        if (--slots[in].stackSize <= 0) slots[in] = null;
        if (result == null) return;
        if (slots[out] == null)
        {
            ItemStack one = result.copy();
            one.stackSize = 1;
            slots[out] = one;
        } else
        {
            slots[out].stackSize++;
        }
    }
}
