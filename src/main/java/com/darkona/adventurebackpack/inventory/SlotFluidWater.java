package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.util.FluidUtils;

/**
 * Created on 10.04.2017
 *
 * @author Ugachaga
 */
public class SlotFluidWater extends SlotFluid
{
    SlotFluidWater(IInventory inventory, int slotIndex, int posX, int posY)
    {
        super(inventory, slotIndex, posX, posY);
    }

    private static boolean isContainerForWater(ItemStack stack)
    {
        return FluidUtils.isContainerForFluid(stack, FluidRegistry.WATER);
    }

    private static boolean isContainerWithWater(ItemStack stack)
    {
        return isFilled(stack) && FluidContainerRegistry.getFluidForFilledItem(stack).getFluid().getName().contains("water");
    }

    private static boolean isValidContainer(ItemStack stack)
    {
        return isContainerForWater(stack) && (isEmpty(stack) || isContainerWithWater(stack));
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
        return Constants.JETPACK_WATER_CAPACITY / Constants.BUCKET;
    }
}
