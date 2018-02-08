package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.reference.GeneralReference;

/**
 * Created on 10.04.2017
 *
 * @author Ugachaga
 */
public class SlotFluidFuel extends SlotFluid
{
    SlotFluidFuel(IInventory inventory, int slotIndex, int posX, int posY)
    {
        super(inventory, slotIndex, posX, posY);
    }

    private static boolean isValidContainer(ItemStack stack)
    {
        return isEmpty(stack) || (isFilled(stack) && GeneralReference.isValidFuel(getFluid(stack).getName()));
    }

    static boolean isValidItem(ItemStack stack)
    {
        return isContainer(stack) && isValidContainer(stack);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack != null && (isValidItem(stack) || stack.getItem() instanceof ItemHose);
    }

    @Override
    public int getSlotStackLimit()
    {
        return Constants.Copter.FUEL_CAPACITY / Constants.BUCKET;
    }
}
