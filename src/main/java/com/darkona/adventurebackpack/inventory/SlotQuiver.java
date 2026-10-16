package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import net.minecraft.item.ItemStack;

public class SlotQuiver extends SlotAdventureBackpack
{
    public SlotQuiver(IInventoryAdventureBackpack inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return Quiver.accepts(stack);
    }
}
