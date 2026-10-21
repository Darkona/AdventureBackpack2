package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import net.minecraft.inventory.Slot;

public final class PlayerWidget extends WidgetType
{
    private final Section section;

    public PlayerWidget(Section section)
    {
        this.section = section;
    }

    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        return new Slot(container.getPlayer().inventory, inventoryIndex, x, y);
    }

    @Override
    public Section section()
    {
        return section;
    }
}
