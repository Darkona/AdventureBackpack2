package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;

public final class ClassicWidgetTypes
{
    private static boolean installed;

    private ClassicWidgetTypes()
    {
    }

    public static synchronized void install()
    {
        if (installed) return;
        installed = true;
        WidgetTypes.register(ClassicWidgets.STORAGE, new StorageWidget());
        WidgetTypes.register(ClassicWidgets.TOOLS, new ToolsWidget());
        WidgetTypes.register(ClassicWidgets.TANK, new TankWidget());
        WidgetTypes.register(ClassicWidgets.QUIVER, new QuiverWidget());
        WidgetTypes.register(ClassicWidgets.ROPE, new RopeWidget());
        WidgetTypes.register(ClassicWidgets.CRAFT, new CraftWidget());
        WidgetTypes.register(ClassicWidgets.RESULT, new ResultWidget());
        WidgetTypes.register(ClassicWidgets.PLAYER, new PlayerWidget(WidgetType.Section.PLAYER));
        WidgetTypes.register(ClassicWidgets.HOTBAR, new PlayerWidget(WidgetType.Section.HOTBAR));
        WidgetTypes.register(ClassicWidgets.BUTTON, new ControlWidget(WidgetType.Control.BACKPACK_BUTTON));
        WidgetTypes.register(ClassicWidgets.TOGGLE, new ControlWidget(WidgetType.Control.ABILITY_TOGGLE));
        WidgetTypes.register(ClassicWidgets.SORT, new ControlWidget(WidgetType.Control.SORT_BUTTON));
        WidgetTypes.register(ClassicWidgets.LOCK, new ControlWidget(WidgetType.Control.LOCK_BUTTON));
    }
}
