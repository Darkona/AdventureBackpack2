package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;

public final class ResultWidget extends WidgetType
{
    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        return new SlotCrafting(container.getPlayer(), ((ContainerBackpack) container).craftMatrix, ((ContainerBackpack) container).craftResult, inventoryIndex, x, y);
    }
}
