package com.darkona.adventurebackpack.compat.bogosorter;

import com.cleanroommc.bogosorter.api.IBogoSortAPI;
import com.cleanroommc.bogosorter.api.ISortingContextBuilder;
import com.cleanroommc.bogosorter.common.sort.GuiSortingContext;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import net.minecraft.inventory.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

final class BackpackGroups
{
    private static final int MIN_GROUP = 2;

    private BackpackGroups()
    {
    }

    static void register()
    {
        IBogoSortAPI.getInstance().addCompat(ContainerBackpack.class, new BiConsumer<ContainerBackpack, ISortingContextBuilder>()
        {
            @Override
            public void accept(ContainerBackpack container, ISortingContextBuilder builder)
            {
                build(container, builder);
            }
        });
    }

    static void invalidate()
    {
        GuiSortingContext.cleanup();
    }

    static void build(ContainerBackpack container, ISortingContextBuilder builder)
    {
        WidgetLayout l = container.widgets;
        List<Slot> storage = new ArrayList<Slot>();
        for (int w = 0; w < l.count(); w++)
        {
            if (!WidgetTypes.get(l.kind(w)).sortable()) continue;
            for (int s = l.slotFirst(w); s < l.slotFirst(w) + l.slots(w); s++)
            {
                if (!container.isLocked(s)) storage.add(container.getSlot(s));
            }
        }
        if (storage.size() >= Math.max(MIN_GROUP, l.rowSize())) builder.addSlotGroupOf(storage, l.rowSize());
    }
}
