package com.darkona.adventurebackpack.compat.invtweaks;

import com.darkona.adventurebackpack.common.DistilleryRules;
import com.darkona.adventurebackpack.inventory.ContainerDistillery;
import invtweaks.api.container.ContainerSection;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class DistillerySections
{
    private DistillerySections()
    {
    }

    public static Map<ContainerSection, List<Slot>> of(ContainerDistillery container)
    {
        Map<ContainerSection, List<Slot>> map = new EnumMap<ContainerSection, List<Slot>>(ContainerSection.class);
        map.put(ContainerSection.CHEST, slots(container, 0, DistilleryRules.GRID));
        map.put(ContainerSection.INVENTORY, slots(container, ContainerDistillery.PLAYER_START, ContainerDistillery.SLOT_COUNT));
        map.put(ContainerSection.INVENTORY_NOT_HOTBAR, slots(container, ContainerDistillery.PLAYER_START, ContainerDistillery.HOTBAR_START));
        map.put(ContainerSection.INVENTORY_HOTBAR, slots(container, ContainerDistillery.HOTBAR_START, ContainerDistillery.SLOT_COUNT));
        return map;
    }

    private static List<Slot> slots(ContainerDistillery container, int from, int to)
    {
        List<Slot> list = new ArrayList<Slot>(to - from);
        for (int i = from; i < to; i++)
        {
            list.add(container.getSlot(i));
        }
        return list;
    }
}
