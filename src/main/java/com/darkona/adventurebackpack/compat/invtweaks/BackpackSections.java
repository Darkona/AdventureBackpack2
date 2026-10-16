package com.darkona.adventurebackpack.compat.invtweaks;

import com.darkona.adventurebackpack.inventory.BackpackLayout;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
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
        BackpackLayout l = container.layout;
        Map<ContainerSection, List<Slot>> map = new EnumMap<ContainerSection, List<Slot>>(ContainerSection.class);
        map.put(ContainerSection.CHEST, slots(container, 0, l.storage()));
        map.put(ContainerSection.INVENTORY, slots(container, l.playerStart(), l.slotCount()));
        map.put(ContainerSection.INVENTORY_NOT_HOTBAR, slots(container, l.playerStart(), l.hotbarStart()));
        map.put(ContainerSection.INVENTORY_HOTBAR, slots(container, l.hotbarStart(), l.slotCount()));
        return map;
    }

    private static List<Slot> slots(ContainerBackpack container, int from, int to)
    {
        List<Slot> list = new ArrayList<Slot>(to - from);
        for (int i = from; i < to; i++)
        {
            list.add(container.getSlot(i));
        }
        return list;
    }
}
