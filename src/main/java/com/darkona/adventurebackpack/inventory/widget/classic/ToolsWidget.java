package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ToolsWidget extends WidgetType
{
    public static final int SHIFT_PRIORITY = 30;

    private ItemStack ghost;

    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        return new SlotTool(container.hostInventory(), inventoryIndex, x, y);
    }

    @Override
    public boolean accepts(ItemStack stack)
    {
        return SlotTool.isValidTool(stack);
    }

    @Override
    public int shiftPriority()
    {
        return SHIFT_PRIORITY;
    }

    @Override
    public boolean consumable()
    {
        return false;
    }

    @Override
    public ItemStack ghost()
    {
        if (ghost == null) ghost = new ItemStack(Items.iron_pickaxe);
        return ghost;
    }
}
