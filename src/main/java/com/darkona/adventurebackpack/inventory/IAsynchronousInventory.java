package com.darkona.adventurebackpack.inventory;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created on 12/01/2015
 *
 * @author Darkona
 */
public interface IAsynchronousInventory extends IInventory
{
    void setInventorySlotContentsNoSave(int slot, @Nullable ItemStack stack);

    ItemStack decrStackSizeNoSave(int slot, int amount);
}
