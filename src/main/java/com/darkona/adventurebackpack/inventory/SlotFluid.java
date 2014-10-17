package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class SlotFluid extends Slot
{

    public SlotFluid(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return ((stack != null) && (FluidContainerRegistry.isContainer(stack)));
    }

    @Override
    public void onSlotChanged()
    {

    }

    @Override
    public void putStack(ItemStack par1ItemStack)
    {
        // TODO Auto-generated method stub
        super.putStack(par1ItemStack);
    }
}
