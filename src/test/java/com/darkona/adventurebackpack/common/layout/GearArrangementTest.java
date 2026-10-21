package com.darkona.adventurebackpack.common.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import com.darkona.adventurebackpack.common.layout.gear.GearArrangement;
import com.darkona.adventurebackpack.common.layout.gear.GearWidgets;
import org.junit.jupiter.api.Test;

class GearArrangementTest
{
    private static WidgetLayout[] all()
    {
        return new WidgetLayout[]{GearArrangement.jetpack(), GearArrangement.copter(), GearArrangement.flamethrower(), GearArrangement.scuba()};
    }

    @Test
    void everyGearWindowIsTheStandardSingleWidthWindowWithThePlayerInventoryAtTheBottom()
    {
        for (WidgetLayout l : all())
        {
            assertEquals(176, l.width());
            assertEquals(166, l.height());
            assertEquals(0, l.lowerX());
            assertEquals(l.width(), l.lowerWidth());
            assertEquals(1, l.countOf(ClassicWidgets.PLAYER));
            assertEquals(27, l.slotsOf(ClassicWidgets.PLAYER));
            assertEquals(9, l.slotsOf(ClassicWidgets.HOTBAR));
            int player = l.slotFirst(l.first(ClassicWidgets.PLAYER));
            assertEquals(8, l.slotX(player));
            assertEquals(84, l.slotY(player));
            assertEquals(142, l.slotY(l.slotFirst(l.first(ClassicWidgets.HOTBAR))));
        }
    }

    @Test
    void widgetsCoverTheSlotsInOrderWithContiguousRanges()
    {
        for (WidgetLayout l : all())
        {
            int next = 0;
            for (int w = 0; w < l.count(); w++)
            {
                assertEquals(next, l.slotFirst(w));
                next += l.slots(w);
            }
            assertEquals(l.slotCount(), next);
        }
    }

    @Test
    void noGearSlotOverlapsAnotherAndAllStayInsideTheWindow()
    {
        for (WidgetLayout l : all())
        {
            for (int a = 0; a < l.slotCount(); a++)
            {
                assertTrue(l.slotX(a) >= 4 && l.slotX(a) + 16 <= l.width() - 4);
                assertTrue(l.slotY(a) >= 4 && l.slotY(a) + 16 <= l.height() - 4);
                for (int b = a + 1; b < l.slotCount(); b++)
                {
                    boolean apart = l.slotX(a) + 18 <= l.slotX(b) || l.slotX(b) + 18 <= l.slotX(a)
                            || l.slotY(a) + 18 <= l.slotY(b) || l.slotY(b) + 18 <= l.slotY(a);
                    assertTrue(apart, "slots " + a + " and " + b);
                }
            }
        }
    }

    @Test
    void upperWidgetsStayAboveTheLowerPanelAndDoNotOverlapEachOther()
    {
        for (WidgetLayout l : all())
        {
            for (int a = 0; a < l.count(); a++)
            {
                if (l.w(a) == 0 || l.h(a) == 0 || l.kind(a) == ClassicWidgets.PLAYER || l.kind(a) == ClassicWidgets.HOTBAR) continue;
                assertTrue(l.y(a) + l.h(a) <= l.upperHeight() - 4, "widget " + a + " kind " + l.kind(a));
                for (int b = a + 1; b < l.count(); b++)
                {
                    if (l.w(b) == 0 || l.h(b) == 0 || l.kind(b) == ClassicWidgets.PLAYER || l.kind(b) == ClassicWidgets.HOTBAR) continue;
                    boolean apart = l.x(a) + l.w(a) <= l.x(b) || l.x(b) + l.w(b) <= l.x(a)
                            || l.y(a) + l.h(a) <= l.y(b) || l.y(b) + l.h(b) <= l.y(a);
                    assertTrue(apart, "widgets " + a + " (" + l.kind(a) + ") and " + b + " (" + l.kind(b) + ")");
                }
            }
        }
    }

    @Test
    void theGearInventoryIndicesMatchTheOldContainers()
    {
        WidgetLayout jetpack = GearArrangement.jetpack();
        assertEquals(3, jetpack.inventorySize());
        assertEquals(2, jetpack.slotInv(jetpack.slotFirst(jetpack.first(GearWidgets.FUEL_SLOT))));
        int tank = jetpack.first(ClassicWidgets.TANK);
        assertEquals(0, jetpack.slotInv(jetpack.slotFirst(tank)));
        assertEquals(1, jetpack.slotInv(jetpack.slotFirst(tank) + 1));
        assertEquals(2, jetpack.countOf(ClassicWidgets.TANK));
        assertEquals(0, jetpack.slots(jetpack.widgetOf(ClassicWidgets.TANK, 1)));
        assertFalse(jetpack.first(ClassicWidgets.BUTTON) == WidgetLayout.NONE);
        assertEquals(WidgetLayout.NONE, GearArrangement.scuba().first(ClassicWidgets.TOGGLE));
        assertEquals(2, GearArrangement.scuba().countOf(GearWidgets.AIR_TANK));
    }
}
