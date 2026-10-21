package com.darkona.adventurebackpack.inventory.widget.gear;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.SlotFuel;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public final class FuelSlotWidget extends WidgetType
{
    public static final int SHIFT_PRIORITY = 30;

    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        return new SlotFuel(container.hostInventory(), inventoryIndex, x, y);
    }

    @Override
    public boolean accepts(ItemStack stack)
    {
        return TileEntityFurnace.isItemFuel(stack);
    }

    @Override
    public int shiftPriority()
    {
        return SHIFT_PRIORITY;
    }
}
