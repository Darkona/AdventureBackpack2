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
    public SlotFuel(IInventory inventory, int id, int x, int y) 
    {
	super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) 
    {
	return (stack != null) && TileEntityFurnace.isItemFuel(stack) && !SlotFluid.isValidItem(stack); //fuel slot accept only solid fuel
    }
}
