package com.darkona.adventurebackpack.inventory.widget.gear;

import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import com.darkona.adventurebackpack.common.layout.gear.GearWidgets;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import com.darkona.adventurebackpack.inventory.widget.classic.ClassicWidgetTypes;
import com.darkona.adventurebackpack.inventory.widget.classic.ControlWidget;
import com.darkona.adventurebackpack.inventory.widget.classic.TankWidget;

public final class GearWidgetTypes
{
    private static boolean installed;

    private GearWidgetTypes()
    {
    }

    public static synchronized void install()
    {
        if (installed) return;
        installed = true;
        ClassicWidgetTypes.install();
        WidgetTypes.register(GearWidgets.FUEL_TANK, new TankWidget(TankWidget.Rule.FUEL));
        WidgetTypes.register(GearWidgets.AIR_TANK, new TankWidget(TankWidget.Rule.AIR));
        WidgetTypes.register(GearWidgets.BAR, new WidgetType()
        {
        });
        WidgetTypes.register(GearWidgets.FUEL_SLOT, new FuelSlotWidget());
    }
}
