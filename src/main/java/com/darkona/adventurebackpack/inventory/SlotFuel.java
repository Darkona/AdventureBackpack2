package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

/**
 * Created on 17/01/2015
 *
 * @author Darkona
 */
public class SlotFuel extends SlotAdventureBackpack
{
    SlotFuel(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    static boolean isValidItem(ItemStack stack)
    {
        return TileEntityFurnace.isItemFuel(stack) && !SlotFluid.isContainer(stack); //fuel slot accept only solid fuel
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack != null && isValidItem(stack);
    }
}
