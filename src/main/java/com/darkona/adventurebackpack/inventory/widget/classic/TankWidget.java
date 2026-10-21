package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.AirContainers;
import com.darkona.adventurebackpack.inventory.FuelContainers;
import com.darkona.adventurebackpack.inventory.SlotAirContainer;
import com.darkona.adventurebackpack.inventory.SlotFluid;
import com.darkona.adventurebackpack.inventory.SlotFuelContainer;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public final class TankWidget extends WidgetType
{
    public static final int SHIFT_PRIORITY = 40;

    public enum Rule
    {
        FLUID, FUEL, AIR
    }

    private final Rule rule;

    public TankWidget()
    {
        this(Rule.FLUID);
    }

    public TankWidget(Rule rule)
    {
        this.rule = rule;
    }

    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        IInventory inventory = container.hostInventory();
        switch (rule)
        {
            case FUEL:
                return new SlotFuelContainer(inventory, inventoryIndex, x, y, n == 0);
            case AIR:
                return new SlotAirContainer(inventory, inventoryIndex, x, y, n == 0);
            default:
                return new SlotFluid(inventory, inventoryIndex, x, y);
        }
    }

    @Override
    public boolean accepts(ItemStack stack)
    {
        switch (rule)
        {
            case FUEL:
                return FuelContainers.isFuelContainer(stack);
            case AIR:
                return AirContainers.isAirContainer(stack);
            default:
                return SlotFluid.valid(stack);
        }
    }

    @Override
    public boolean acceptsAt(WidgetLayout layout, int widget, int n, ItemStack stack)
    {
        return n != 0 || accepts(stack);
    }

    @Override
    public int shiftPriority()
    {
        return SHIFT_PRIORITY;
    }

    @Override
    public boolean shiftTarget(WidgetLayout layout, int widget, int n)
    {
        return n == 0;
    }

    @Override
    public boolean dropsOnClose()
    {
        return true;
    }

    @Override
    public boolean consumable()
    {
        return false;
    }

    @Override
    public int tank(WidgetLayout layout, int widget)
    {
        return layout.param(widget);
    }
}
