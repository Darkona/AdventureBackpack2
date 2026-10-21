package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.Quiver;
import com.darkona.adventurebackpack.inventory.SlotQuiver;
import com.darkona.adventurebackpack.inventory.ContainerBackpack;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import com.darkona.adventurebackpack.common.QuiverRules;
import com.darkona.adventurebackpack.inventory.widget.SlotList;

public final class QuiverWidget extends WidgetType
{
    public static final int SHIFT_PRIORITY = 20;

    private static final SlotList STORE = SlotList.of(QuiverNbt.QUIVER);
    private static final String[] KEYS = {QuiverNbt.QUIVER_FILL};

    private ItemStack ghost;

    @Override
    public Slot createSlot(SlotHost container, WidgetLayout layout, int widget, int n, int inventoryIndex, int x, int y)
    {
        return new SlotQuiver(((ContainerBackpack) container).inventory, inventoryIndex, x, y);
    }

    @Override
    public boolean accepts(ItemStack stack)
    {
        return Quiver.accepts(stack);
    }

    @Override
    public int shiftPriority()
    {
        return SHIFT_PRIORITY;
    }

    @Override
    public ItemStack ghost()
    {
        if (ghost == null) ghost = new ItemStack(Items.arrow);
        return ghost;
    }

    @Override
    public SlotList store()
    {
        return STORE;
    }

    @Override
    public String[] keys()
    {
        return KEYS;
    }

    @Override
    public void stored(NBTTagCompound data, int slots, int items)
    {
        data.setByte(QuiverNbt.QUIVER_FILL, (byte) QuiverRules.level(items));
    }

    @Override
    public boolean consumedFirst()
    {
        return true;
    }
}
