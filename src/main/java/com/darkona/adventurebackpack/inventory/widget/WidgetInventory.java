package com.darkona.adventurebackpack.inventory.widget;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.inventory.InventoryActions;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

public final class WidgetInventory
{
    private WidgetInventory()
    {
    }

    public static WidgetType typeAt(WidgetLayout layout, int inventoryIndex)
    {
        return WidgetTypes.get(layout.kindAt(inventoryIndex));
    }

    public static boolean accepts(WidgetLayout layout, int inventoryIndex, ItemStack stack)
    {
        int widget = layout.widgetAt(inventoryIndex);
        if (widget == WidgetLayout.NONE) return false;
        int n = layout.slotAt(inventoryIndex) - layout.slotFirst(widget);
        return WidgetTypes.get(layout.kind(widget)).acceptsAt(layout, widget, n, stack);
    }

    public static int limit(WidgetLayout layout, int inventoryIndex, int fallback)
    {
        int widget = layout.widgetAt(inventoryIndex);
        if (widget == WidgetLayout.NONE) return fallback;
        int limit = WidgetTypes.get(layout.kind(widget)).stackLimit();
        return limit == WidgetType.DEFAULT_LIMIT ? fallback : limit;
    }

    public static boolean dropsOnClose(WidgetLayout layout, int inventoryIndex)
    {
        int widget = layout.widgetAt(inventoryIndex);
        return widget != WidgetLayout.NONE && WidgetTypes.get(layout.kind(widget)).dropsOnClose();
    }

    public static boolean consumable(WidgetLayout layout, int inventoryIndex)
    {
        int widget = layout.widgetAt(inventoryIndex);
        return widget == WidgetLayout.NONE || WidgetTypes.get(layout.kind(widget)).consumable();
    }

    public static boolean showsTank(WidgetLayout layout, int tank)
    {
        for (int w = 0; w < layout.count(); w++)
        {
            if (WidgetTypes.get(layout.kind(w)).tank(layout, w) == tank) return true;
        }
        return false;
    }

    public static boolean fillTanks(IInventoryAdventureBackpack inventory, WidgetLayout layout)
    {
        boolean changed = false;
        for (int w = 0; w < layout.count(); w++)
        {
            int tank = WidgetTypes.get(layout.kind(w)).tank(layout, w);
            if (tank < 0 || layout.slots(w) == 0) continue;
            FluidTank fluid = tank == 0 ? inventory.getLeftTank() : inventory.getRightTank();
            if (InventoryActions.transferContainerTank(inventory, fluid, layout.invIndex(w, 0))) changed = true;
        }
        return changed;
    }

    public static boolean consume(IInventory inventory, WidgetLayout layout, Item item)
    {
        for (int w = 0; w < layout.count(); w++)
        {
            if (!layout.ownsInventory(w) || !WidgetTypes.get(layout.kind(w)).consumedFirst()) continue;
            for (int n = 0; n < layout.slots(w); n++)
            {
                int i = layout.invIndex(w, n);
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() == item)
                {
                    inventory.decrStackSize(i, 1);
                    return true;
                }
            }
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            if (!consumable(layout, i)) continue;
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null && stack.getItem() == item)
            {
                inventory.decrStackSize(i, 1);
                return true;
            }
        }
        return false;
    }

    public static int control(WidgetLayout layout, WidgetType.Control control)
    {
        for (int w = 0; w < layout.count(); w++)
        {
            if (WidgetTypes.get(layout.kind(w)).control() == control) return w;
        }
        return WidgetLayout.NONE;
    }
}
