package com.darkona.adventurebackpack.compat.invtweaks;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import invtweaks.api.container.ContainerSection;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class BackpackSections
{
    private BackpackSections()
    {
    }

    public static Map<ContainerSection, List<Slot>> of(ContainerBackpack container)
    {
        Map<ContainerSection, List<Slot>> map = new EnumMap<ContainerSection, List<Slot>>(ContainerSection.class);
        List<Slot> chest = slots(container, WidgetType.Section.CHEST);
        List<Slot> main = slots(container, WidgetType.Section.PLAYER);
        List<Slot> hotbar = slots(container, WidgetType.Section.HOTBAR);
        List<Slot> inventory = new ArrayList<Slot>(main.size() + hotbar.size());
        inventory.addAll(main);
        inventory.addAll(hotbar);
        map.put(ContainerSection.CHEST, chest);
        map.put(ContainerSection.INVENTORY, inventory);
        map.put(ContainerSection.INVENTORY_NOT_HOTBAR, main);
        map.put(ContainerSection.INVENTORY_HOTBAR, hotbar);
        return map;
    }

    public static List<Slot> slots(ContainerBackpack container, WidgetType.Section section)
    {
        WidgetLayout l = container.widgets;
        List<Slot> list = new ArrayList<Slot>();
        for (int w = 0; w < l.count(); w++)
        {
            if (WidgetTypes.get(l.kind(w)).section() != section) continue;
            for (int s = l.slotFirst(w); s < l.slotFirst(w) + l.slots(w); s++)
            {
                if (!container.isLocked(s)) list.add(container.getSlot(s));
            }
        }
        return list;
    }
}
