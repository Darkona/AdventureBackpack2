package com.darkona.adventurebackpack.inventory.widget;

import adventurebackpack.api.BackpackWidget;

import java.util.concurrent.ConcurrentHashMap;

public final class ExternalWidgets
{
    public static final String KEY_PREFIX = "ABPW:";
    private static final ConcurrentHashMap<String, ExternalWidgetType> BY_ID = new ConcurrentHashMap<String, ExternalWidgetType>();
    private static int next = WidgetTypes.FIRST_EXTERNAL;

    private ExternalWidgets()
    {
    }

    public static synchronized int register(BackpackWidget widget)
    {
        if (widget == null) throw new IllegalArgumentException("null widget");
        String id = widget.getId();
        if (id == null || id.indexOf(':') <= 0) throw new IllegalArgumentException("a widget id needs a mod prefix, like mymod:pouch: " + id);
        if (widget.getSlots() <= 0) throw new IllegalArgumentException(id + " has no slots");
        if (BY_ID.containsKey(id)) throw new IllegalStateException(id + " is already registered");
        ExternalWidgetType type = new ExternalWidgetType(next++, widget);
        WidgetTypes.register(type.kind(), type);
        BY_ID.put(id, type);
        return type.kind();
    }

    public static ExternalWidgetType get(String id)
    {
        return id == null ? null : BY_ID.get(id);
    }

    public static int kindOf(String id)
    {
        ExternalWidgetType type = get(id);
        return type == null ? WidgetTypes.NO_KIND : type.kind();
    }

    public static synchronized void forget(String id)
    {
        ExternalWidgetType type = BY_ID.remove(id);
        if (type != null) WidgetTypes.register(type.kind(), null);
    }
}
