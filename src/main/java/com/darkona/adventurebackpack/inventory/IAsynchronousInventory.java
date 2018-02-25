package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Created on 12/01/2015
 *
 * @author Darkona
 */
public interface IAsynchronousInventory extends IInventory
{
    void setInventorySlotContentsNoSave(int slot, ItemStack stack); //TODO rework realisations, see InventoryBackpack

    ItemStack decrStackSizeNoSave(int slot, int amount); //TODO whats the point and diff with decrStackSize?
}
