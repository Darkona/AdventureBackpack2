package com.darkona.adventurebackpack.inventory.widget;

import com.darkona.adventurebackpack.inventory.SlotAdventureBackpack;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotWidget extends SlotAdventureBackpack
{
    private final WidgetType type;

    public SlotWidget(IInventory inventory, int index, int x, int y, WidgetType type)
    {
        super(inventory, index, x, y);
        this.type = type;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return stack != null && type.accepts(stack);
    }

    @Override
    public int getSlotStackLimit()
    {
        return type.stackLimit();
    }
}
