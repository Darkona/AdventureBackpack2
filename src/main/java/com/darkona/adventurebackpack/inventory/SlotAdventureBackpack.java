package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

/**
 * Created on 17/01/2015
 *
 * @author Darkona
 */
abstract class SlotAdventureBackpack extends Slot
{
    SlotAdventureBackpack(IInventory p_i1824_1_, int p_i1824_2_, int p_i1824_3_, int p_i1824_4_) //TODO whats the point?
    {
        super(p_i1824_1_, p_i1824_2_, p_i1824_3_, p_i1824_4_);
    }
}
