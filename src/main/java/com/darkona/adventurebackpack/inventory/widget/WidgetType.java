package com.darkona.adventurebackpack.inventory.widget;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public abstract class WidgetType
{
    public static final int NO_SHIFT = Integer.MIN_VALUE;
    public static final int DEFAULT_LIMIT = 64;
    public static final int NO_TANK = -1;
    private static final String[] NO_KEYS = new String[0];

    public enum Section
    {
        NONE, CHEST, PLAYER, HOTBAR
    }

    public enum Control
    {
        NONE, BACKPACK_BUTTON, ABILITY_TOGGLE, SORT_BUTTON, LOCK_BUTTON
    }

    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        throw new IllegalStateException(getClass().getSimpleName() + " has no slots");
    }

    public boolean accepts(ItemStack stack)
    {
        return true;
    }

    public boolean acceptsAt(WidgetLayout layout, int widget, int n, ItemStack stack)
    {
        return accepts(stack);
    }

    public int stackLimit()
    {
        return DEFAULT_LIMIT;
    }

    public int shiftPriority()
    {
        return NO_SHIFT;
    }

    public boolean shiftTarget(WidgetLayout layout, int widget, int n)
    {
        return true;
    }

    public boolean sortable()
    {
        return false;
    }

    public boolean dropsOnClose()
    {
        return false;
    }

    public boolean consumable()
    {
        return true;
    }

    public Section section()
    {
        return Section.NONE;
    }

    public ItemStack ghost()
    {
        return null;
    }

    public SlotList store()
    {
        return null;
    }

    public String[] keys()
    {
        return NO_KEYS;
    }

    public void stored(NBTTagCompound data, int slots, int items)
    {
    }

    public boolean consumedFirst()
    {
        return false;
    }

    public int tank(WidgetLayout layout, int widget)
    {
        return NO_TANK;
    }

    public Control control()
    {
        return Control.NONE;
    }
}
