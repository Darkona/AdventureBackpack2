package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created on 17/01/2015
 *
 * @author Darkona
 */
public abstract class SlotAdventureBackpack extends Slot
{
    public SlotAdventureBackpack(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_)
    {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }

    @Override
    public void onSlotChanged()
    {
        if(inventory instanceof IInventoryTanks)
        ((IInventoryTanks)this.inventory).dirtyInventory();
    }
}
