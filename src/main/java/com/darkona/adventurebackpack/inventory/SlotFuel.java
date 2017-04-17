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
    SlotFuel(IInventory inventory, int slotIndex, int posX, int posY)
    {
        super(inventory, slotIndex, posX, posY);
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
