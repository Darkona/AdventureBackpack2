package com.darkona.adventurebackpack.inventory.widget;

import java.util.Arrays;

public final class WidgetTypes
{
    public static final int NO_KIND = -1;
    public static final int FIRST_EXTERNAL = 64;

    private static final WidgetType NOTHING = new WidgetType()
    {
    };
    private static volatile WidgetType[] types = new WidgetType[32];

    private WidgetTypes()
    {
    }

    public static synchronized void register(int kind, WidgetType type)
    {
        if (kind < 0) throw new IllegalArgumentException("kind " + kind);
        WidgetType[] next = Arrays.copyOf(types, Math.max(types.length, kind + 1));
        next[kind] = type;
        types = next;
    }

    public static WidgetType get(int kind)
    {
        WidgetType[] t = types;
        WidgetType type = kind >= 0 && kind < t.length ? t[kind] : null;
        return type == null ? NOTHING : type;
    }

    public static boolean has(int kind)
    {
        WidgetType[] t = types;
        return kind >= 0 && kind < t.length && t[kind] != null;
    }
}
