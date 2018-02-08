package com.darkona.adventurebackpack.inventory;

import javax.annotation.Nullable;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;

/**
 * Created on 04.02.2018
 *
 * @author Ugachaga
 */
public class InventoryCraftingBackpack extends InventoryCrafting
{
    public InventoryCraftingBackpack(Container eventHandler, int columns, int rows)
    {
        super(eventHandler, columns, rows);
    }

    public void setInventorySlotContentsNoUpdate(int slotID, @Nullable ItemStack stack)
    {
        this.stackList[slotID] = stack;
    }
}