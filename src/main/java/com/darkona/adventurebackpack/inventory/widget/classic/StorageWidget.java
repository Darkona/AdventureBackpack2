package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.SlotBackpack;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class StorageWidget extends WidgetType
{
    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        return new SlotBackpack(((ContainerBackpack) container).inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean accepts(ItemStack stack)
    {
        return SlotBackpack.valid(stack);
    }

    @Override
    public boolean sortable()
    {
        return true;
    }

    @Override
    public Section section()
    {
        return Section.CHEST;
    }
}
