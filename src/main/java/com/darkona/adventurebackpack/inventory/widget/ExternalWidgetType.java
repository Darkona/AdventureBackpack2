package com.darkona.adventurebackpack.inventory.widget;

import adventurebackpack.api.BackpackWidget;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class ExternalWidgetType extends WidgetType
{
    private static final int BYTE_COUNT_LIMIT = 127;
    private static final String COUNT_KEY = "ABPCount";

    private final int kind;
    private final BackpackWidget widget;
    private final SlotList store;

    ExternalWidgetType(int kind, BackpackWidget widget)
    {
        this.kind = kind;
        this.widget = widget;
        store = new SlotList(ExternalWidgets.KEY_PREFIX + widget.getId(), widget.getStackLimit() > BYTE_COUNT_LIMIT ? COUNT_KEY : null, true, false);
    }

    public int kind()
    {
        return kind;
    }

    public int slots()
    {
        return widget.getSlots();
    }

    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int w, int n, int inventoryIndex, int x, int y)
    {
        return new SlotWidget(container.hostInventory(), inventoryIndex, x, y, this);
    }

    @Override
    public boolean accepts(ItemStack stack)
    {
        return stack != null && widget.accepts(stack);
    }

    @Override
    public int stackLimit()
    {
        return widget.getStackLimit();
    }

    @Override
    public int shiftPriority()
    {
        return widget.getShiftPriority();
    }

    @Override
    public SlotList store()
    {
        return store;
    }

    @Override
    public ItemStack ghost()
    {
        return widget.getGhostIcon();
    }
}
