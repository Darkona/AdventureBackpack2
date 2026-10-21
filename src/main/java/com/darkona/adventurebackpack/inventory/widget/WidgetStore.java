package com.darkona.adventurebackpack.inventory.widget;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.BackpackNbt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class WidgetStore
{
    private WidgetStore()
    {
    }

    public static NBTTagList[] keptFor(WidgetLayout layout, NBTTagList[] kept)
    {
        return kept != null && kept.length == layout.count() ? kept : new NBTTagList[layout.count()];
    }

    public static ItemStack[] read(NBTTagCompound data, WidgetLayout layout, ItemStack[] inventory, NBTTagList[] kept)
    {
        ItemStack[] into = BackpackNbt.readItems(data, inventory, layout.inventorySize());
        for (int w = 0; w < layout.count(); w++)
        {
            SlotList list = listOf(layout, w);
            kept[w] = list == null ? null : BackpackNbt.readSlots(data, list.key, into, layout.invIndex(w, 0), layout.slots(w), list.countKey);
        }
        return into;
    }

    public static void write(NBTTagCompound data, WidgetLayout layout, ItemStack[] inventory, NBTTagList[] kept, int storage)
    {
        data.setTag(BackpackNbt.ITEMS, BackpackNbt.writeItems(inventory, layout, inventory.length));
        data.setInteger(BackpackNbt.STORAGE_SIZE, storage);
        for (int w = 0; w < layout.count(); w++)
        {
            SlotList list = listOf(layout, w);
            if (list == null) continue;
            int first = layout.invIndex(w, 0);
            NBTTagList k = kept == null || w >= kept.length ? null : kept[w];
            if (list.dropWhenEmpty && k == null && empty(inventory, first, layout.slots(w)))
            {
                data.removeTag(list.key);
                continue;
            }
            int items = BackpackNbt.writeSlots(data, list.key, inventory, first, layout.slots(w), k, list.countKey, list.slotTags);
            WidgetTypes.get(layout.kind(w)).stored(data, layout.slots(w), items);
        }
    }

    public static boolean persistsApart(WidgetLayout layout, int inventoryIndex)
    {
        int w = layout.widgetAt(inventoryIndex);
        return w != WidgetLayout.NONE && WidgetTypes.get(layout.kind(w)).store() != null;
    }

    public static void keepUnknown(NBTTagCompound previous, NBTTagCompound fresh, WidgetLayout layout)
    {
        if (previous == null) return;
        for (Object o : previous.func_150296_c())
        {
            String key = (String) o;
            if (!fresh.hasKey(key) && !BackpackNbt.isCoreKey(key) && !written(layout, key)) fresh.setTag(key, previous.getTag(key).copy());
        }
    }

    public static String[] readKeys(WidgetLayout layout)
    {
        String[] core = BackpackNbt.CORE_KEYS;
        int n = core.length;
        for (int w = 0; w < layout.count(); w++) n += keyCount(layout, w);
        String[] keys = new String[n];
        System.arraycopy(core, 0, keys, 0, core.length);
        int at = core.length;
        for (int w = 0; w < layout.count(); w++)
        {
            WidgetType type = WidgetTypes.get(layout.kind(w));
            SlotList list = layout.ownsInventory(w) ? type.store() : null;
            if (list != null) keys[at++] = list.key;
            for (String k : type.keys()) keys[at++] = k;
        }
        return keys;
    }

    private static int keyCount(WidgetLayout layout, int w)
    {
        WidgetType type = WidgetTypes.get(layout.kind(w));
        return (layout.ownsInventory(w) && type.store() != null ? 1 : 0) + type.keys().length;
    }

    private static boolean written(WidgetLayout layout, String key)
    {
        for (int w = 0; w < layout.count(); w++)
        {
            WidgetType type = WidgetTypes.get(layout.kind(w));
            SlotList list = layout.ownsInventory(w) ? type.store() : null;
            if (list != null && list.key.equals(key)) return true;
            for (String k : type.keys())
            {
                if (k.equals(key)) return true;
            }
        }
        return false;
    }

    private static SlotList listOf(WidgetLayout layout, int w)
    {
        return layout.ownsInventory(w) && layout.slots(w) > 0 ? WidgetTypes.get(layout.kind(w)).store() : null;
    }

    private static boolean empty(ItemStack[] inventory, int first, int slots)
    {
        for (int i = first; i < first + slots && i < inventory.length; i++)
        {
            if (inventory[i] != null) return false;
        }
        return true;
    }
}
