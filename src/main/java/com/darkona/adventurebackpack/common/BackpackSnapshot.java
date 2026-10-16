package com.darkona.adventurebackpack.common;

import adventurebackpack.api.IBackpackView;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

final class BackpackSnapshot implements IBackpackView
{
    private final ItemStack stack;
    private final String colorName;
    private final FluidStack[] fluids;
    private final int[] capacities;
    private final ItemStack[] slots;

    BackpackSnapshot(IInventoryAdventureBackpack backpack)
    {
        ItemStack parent = backpack.getParentItemStack();
        this.stack = parent == null ? null : parent.copy();
        this.colorName = backpack.getColorName();
        FluidTank[] tanks = backpack.getTanksArray();
        this.fluids = new FluidStack[tanks.length];
        this.capacities = new int[tanks.length];
        for (int i = 0; i < tanks.length; i++)
        {
            FluidStack fluid = tanks[i].getFluid();
            fluids[i] = fluid == null ? null : fluid.copy();
            capacities[i] = tanks[i].getCapacity();
        }
        ItemStack[] live = backpack.getInventory();
        this.slots = new ItemStack[live.length];
        for (int i = 0; i < live.length; i++)
        {
            slots[i] = live[i] == null ? null : live[i].copy();
        }
    }

    @Override
    public ItemStack getStack()
    {
        return stack == null ? null : stack.copy();
    }

    @Override
    public String getColorName()
    {
        return colorName;
    }

    @Override
    public int getTankCount()
    {
        return fluids.length;
    }

    @Override
    public FluidStack getTankFluid(int tank)
    {
        return tank < 0 || tank >= fluids.length || fluids[tank] == null ? null : fluids[tank].copy();
    }

    @Override
    public int getTankCapacity(int tank)
    {
        return tank < 0 || tank >= capacities.length ? 0 : capacities[tank];
    }

    @Override
    public int getSlotCount()
    {
        return slots.length;
    }

    @Override
    public ItemStack getSlot(int slot)
    {
        return slot < 0 || slot >= slots.length || slots[slot] == null ? null : slots[slot].copy();
    }
}
