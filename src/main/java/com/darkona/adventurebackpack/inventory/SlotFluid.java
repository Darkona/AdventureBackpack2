package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.util.Utils;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class SlotFluid extends SlotAdventureBackpack
{
    private static final String[] VALID_CONTAINER_NAMES = {"bucket"};

    private static final String[] VALID_CONTAINER_IDS = {"minecraft:milk_bucket"};

    private static final String[] INVALID_CONTAINER_NAMES = {"cell"};

    public SlotFluid(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    public static boolean isEmpty(ItemStack stack)
    {
        return (stack != null && FluidContainerRegistry.isEmptyContainer(stack));
    }

    public static boolean isFilled(ItemStack stack)
    {
        return (stack != null && FluidContainerRegistry.isFilledContainer(stack));
    }

    public static String getFluidName(ItemStack stack)
    {
        if (stack == null || isEmpty(stack)) return "";
        return FluidContainerRegistry.getFluidForFilledItem(stack).getUnlocalizedName();
    }

    public static String getFluidName(FluidTank stack)
    {
        if (stack != null && stack.getFluidAmount() != 0)
        {
            return stack.getFluid().getUnlocalizedName();
        }
        return "";
    }

    public static int getCapacity(ItemStack stack)
    {
        return FluidContainerRegistry.getContainerCapacity(stack);
    }

    public static boolean isContainer(ItemStack stack)
    {
        return (stack != null && FluidContainerRegistry.isContainer(stack));
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return (stack != null && FluidContainerRegistry.isContainer(stack) && isValidContainer(stack));
    }

    public static boolean isValidContainer(ItemStack stack)
    {

        if (stack != null && stack.getMaxStackSize() <= 16)
        {
            Item itemCurrent = stack.getItem();
            String nameCurrent = itemCurrent.getUnlocalizedName().toLowerCase();

            for (String toolName : VALID_CONTAINER_NAMES)
            {
                if (nameCurrent.contains(toolName)) return true;
            }
            for (String toolID : VALID_CONTAINER_IDS)
            {
                if (Item.itemRegistry.getNameForObject(itemCurrent).equals(toolID)) return true;
            }

            for (String toolName : INVALID_CONTAINER_NAMES)
            {
                if (nameCurrent.contains(toolName)) return false;
            }
        }
        return false;
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
    public void putStack(ItemStack par1ItemStack)
    {
        super.putStack(par1ItemStack);
    }

}
