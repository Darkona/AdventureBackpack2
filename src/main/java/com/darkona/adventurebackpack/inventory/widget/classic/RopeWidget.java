package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.RopeRules;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.SlotRope;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.item.ItemRope;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import com.darkona.adventurebackpack.inventory.widget.SlotList;

public final class RopeWidget extends WidgetType
{
    public static final int SHIFT_PRIORITY = 10;

    public static final String KEY = "ABPRope";
    public static final String COUNT_KEY = "RopeCount";
    private static final SlotList STORE = new SlotList(KEY, COUNT_KEY, false, true);

    private ItemStack ghost;

    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        return new SlotRope(((ContainerBackpack) container).inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean accepts(ItemStack stack)
    {
        return ItemRope.is(stack);
    }

    @Override
    public int stackLimit()
    {
        return RopeRules.SLOT_LIMIT;
    }

    @Override
    public int shiftPriority()
    {
        return SHIFT_PRIORITY;
    }

    @Override
    public ItemStack ghost()
    {
        if (ghost == null && ModItems.rope != null) ghost = new ItemStack(ModItems.rope);
        return ghost;
    }

    @Override
    public SlotList store()
    {
        return STORE;
    }
}
