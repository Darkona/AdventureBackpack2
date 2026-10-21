package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.RopeRules;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemRope;
import net.minecraft.item.ItemStack;

public class SlotRope extends SlotAdventureBackpack
{
    public SlotRope(IInventoryAdventureBackpack inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public int getSlotStackLimit()
    {
        return RopeRules.SLOT_LIMIT;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return ItemRope.is(stack);
    }
}
