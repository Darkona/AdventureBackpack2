package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.item.ItemRope;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/** Shift-click from the player follows the widgets' priorities: rope, quiver, tools, buckets, then storage. */
class ShiftRouteTest
{
    private final Item arrow = new Item();
    private final Item bucket = new Item();
    private Quiver.Names saved;

    @BeforeEach
    void names()
    {
        saved = Quiver.names;
        Quiver.names = new Quiver.Names()
        {
            @Override
            public String of(Item item)
            {
                return item == arrow ? "minecraft:arrow" : null;
            }
        };
    }

    @AfterEach
    void restore()
    {
        Quiver.names = saved;
    }

    private static int route(ContainerBackpack c, ItemStack stack)
    {
        int w = c.firstShiftWidget(stack);
        return w == WidgetLayout.NONE ? -1 : c.widgets.slotFirst(w);
    }

    @Test
    void eachItemFindsItsWidgetFirst() throws Exception
    {
        BackpackLayoutTest.registerBigExample();
        WidgetType tanks = WidgetTypes.get(ClassicWidgets.TANK);
        WidgetTypes.register(ClassicWidgets.TANK, new WidgetType()
        {
            @Override
            public boolean accepts(ItemStack stack)
            {
                return stack.getItem() == bucket;
            }

            @Override
            public int shiftPriority()
            {
                return tanks.shiftPriority();
            }

            @Override
            public boolean shiftTarget(WidgetLayout layout, int widget, int n)
            {
                return tanks.shiftTarget(layout, widget, n);
            }

            @Override
            public net.minecraft.inventory.Slot createSlot(com.darkona.adventurebackpack.inventory.widget.SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
            {
                return tanks.createSlot(container, layout, widget, n, inventoryIndex, x, y);
            }
        });
        try
        {
            for (String color : new String[]{"Standard", "Skeleton", "LayoutTestBig"})
            {
                ContainerBackpack c = ContainerBackpackSlotsTest.build(color);
                BackpackLayout l = BackpackLayout.view(c);
                assertEquals(-1, route(c, new ItemStack(new Item(), 5)), color + " plain item goes to storage");
                assertEquals(l.toolStart(), route(c, new ItemStack(new ItemShears())), color + " tool");
                assertEquals(l.ropeStart(), route(c, new ItemStack(new ItemRope(), 3)), color + " rope");
                assertEquals(l.quiver() > 0 ? l.quiverStart() : -1, route(c, new ItemStack(arrow, 16)), color + " arrow");
                assertEquals(l.bucketStart(), route(c, new ItemStack(bucket)), color + " bucket to the left tank first");
                assertEquals(l.tanks(), c.widgets.countOf(ClassicWidgets.TANK));
            }
        } finally
        {
            WidgetTypes.register(ClassicWidgets.TANK, tanks);
        }
    }

    @Test
    void theRopeWidgetTakesMoreThanAStack()
    {
        assertEquals(128, WidgetTypes.get(ClassicWidgets.ROPE).stackLimit());
        assertTrue(WidgetTypes.get(ClassicWidgets.STORAGE).accepts(new ItemStack(new Item())));
    }
}
