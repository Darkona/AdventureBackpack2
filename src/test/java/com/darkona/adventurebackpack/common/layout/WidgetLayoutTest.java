package com.darkona.adventurebackpack.common.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;
import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import org.junit.jupiter.api.Test;

/** The widget model: every widget owns one contiguous range of container slots and the inventory map agrees with it. */
class WidgetLayoutTest
{
    @Test
    void widgetsCoverTheSlotsInOrderWithContiguousRanges()
    {
        for (int r = ClassicArrangement.MIN_ROWS; r <= ClassicArrangement.MAX_ROWS; r++)
        {
            for (int c = ClassicArrangement.MIN_COLUMNS; c <= ClassicArrangement.MAX_COLUMNS; c++)
            {
                for (int q = 0; q <= ClassicArrangement.maxQuiver(r); q++)
                {
                    WidgetLayout l = ClassicArrangement.of(r, c, q);
                    int next = 0;
                    for (int w = 0; w < l.count(); w++)
                    {
                        assertEquals(next, l.slotFirst(w), r + "x" + c + " q" + q + " widget " + w);
                        for (int s = l.slotFirst(w); s < l.slotFirst(w) + l.slots(w); s++) assertEquals(w, l.slotWidget(s));
                        next += l.slots(w);
                    }
                    assertEquals(l.slotCount(), next);
                    for (int s = 0; s < l.slotCount(); s++)
                    {
                        int w = l.slotWidget(s);
                        if (l.ownsInventory(w)) assertEquals(w, l.widgetAt(l.slotInv(s)));
                    }
                    int mapped = 0;
                    for (int i = 0; i < l.inventorySize(); i++) if (l.widgetAt(i) != WidgetLayout.NONE) mapped++;
                    int owned = 0;
                    for (int w = 0; w < l.count(); w++) if (l.ownsInventory(w)) owned += l.slots(w);
                    assertEquals(owned, mapped, "each backpack inventory index belongs to at most one widget");
                    assertEquals(0, l.slotFirst(l.first(ClassicWidgets.STORAGE)), "storage stays one range from 0");
                }
            }
        }
    }

    @Test
    void theClassicWindowIsTheOldOne()
    {
        WidgetLayout l = ClassicArrangement.of(6, 8);
        assertEquals(248, l.width());
        assertEquals(207, l.height());
        assertEquals(46, l.inventorySize());
        assertEquals(92, l.slotCount());
        assertEquals(ClassicWidgets.TANK, l.kindAt(41));
        assertEquals(ClassicWidgets.TOOLS, l.kindAt(40));
        assertEquals(ClassicWidgets.ROPE, l.kindAt(45));
        assertEquals(WidgetLayout.NONE, l.kindAt(46));
        assertTrue(l.first(ClassicWidgets.QUIVER) == WidgetLayout.NONE);
        assertEquals(1, l.param(l.widgetOf(ClassicWidgets.TANK, 1)));
    }
}
