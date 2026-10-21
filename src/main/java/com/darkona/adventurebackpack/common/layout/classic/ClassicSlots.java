package com.darkona.adventurebackpack.common.layout.classic;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;

public final class ClassicSlots
{
    private ClassicSlots()
    {
    }

    public static int quiverSlots(WidgetLayout layout)
    {
        return layout.slotsOf(ClassicWidgets.QUIVER);
    }

    public static int quiverStart(WidgetLayout layout)
    {
        return start(layout, ClassicWidgets.QUIVER);
    }

    public static int ropeIndex(WidgetLayout layout)
    {
        return start(layout, ClassicWidgets.ROPE);
    }

    public static int toolIndex(WidgetLayout layout, int n)
    {
        int w = layout.first(ClassicWidgets.TOOLS);
        return w == WidgetLayout.NONE ? WidgetLayout.NONE : layout.invIndex(w, n);
    }

    private static int start(WidgetLayout layout, int kind)
    {
        int w = layout.first(kind);
        return w == WidgetLayout.NONE || layout.slots(w) == 0 ? WidgetLayout.NONE : layout.invIndex(w, 0);
    }
}
