package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.layout.StorageIndex;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.sort.SlotLocks;
import com.darkona.adventurebackpack.inventory.widget.WidgetStore;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public final class BackpackNbt
{
    public static final String ITEMS = "ABPItems";
    public static final String STORAGE_SIZE = "storageSize";
    public static final String EXTENDED = "extendedProperties";
    public static final String LEGACY_EXTENDED = "extended";
    public static final String LOCKED = "ABPLocked";

    public static final String[] CORE_KEYS = {ITEMS, STORAGE_SIZE, "colorName", "lastTime", "special", EXTENDED, LEGACY_EXTENDED, "leftTank", "rightTank", LOCKED};

    private BackpackNbt()
    {
    }

    public static boolean isCoreKey(String key)
    {
        for (String k : CORE_KEYS)
        {
            if (k.equals(key)) return true;
        }
        return false;
    }

    public static int savedStorage(NBTTagCompound data)
    {
        int saved = data.hasKey(STORAGE_SIZE) ? data.getInteger(STORAGE_SIZE)
                : data.hasKey(ITEMS) ? StorageIndex.LEGACY_STORAGE : 0;
        NBTTagList items = data.getTagList(ITEMS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            int slot = items.getCompoundTagAt(i).getByte("Slot") & 0xFF;
            saved = Math.max(saved, StorageIndex.storageOfSavedIndex(slot));
        }
        return saved;
    }

    public static NBTTagCompound readExtended(NBTTagCompound data)
    {
        if (data.hasKey(EXTENDED, Constants.NBT.TAG_COMPOUND)) return data.getCompoundTag(EXTENDED);
        return data.getCompoundTag(LEGACY_EXTENDED);
    }

    public static void writeExtended(NBTTagCompound data, NBTTagCompound extended)
    {
        data.setTag(EXTENDED, extended == null ? new NBTTagCompound() : extended);
        data.removeTag(LEGACY_EXTENDED);
    }

    public static void readLocks(NBTTagCompound data, SlotLocks locks)
    {
        locks.load(data.getIntArray(LOCKED));
    }

    public static void writeLocks(NBTTagCompound data, SlotLocks locks)
    {
        if (locks.isEmpty()) data.removeTag(LOCKED);
        else data.setIntArray(LOCKED, locks.toInts());
    }

    public static ItemStack[] readItems(NBTTagCompound data, ItemStack[] inventory, int size)
    {
        ItemStack[] into = inventory != null && inventory.length == size ? inventory : new ItemStack[size];
        java.util.Arrays.fill(into, null);
        NBTTagList items = data.getTagList(ITEMS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            if (slot < into.length)
            {
                into[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
        return into;
    }

    public static NBTTagList readSlots(NBTTagCompound data, String key, ItemStack[] inventory, int offset, int slots, String countKey)
    {
        NBTTagList list = data.getTagList(key, Constants.NBT.TAG_COMPOUND);
        NBTTagList kept = null;
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            if (slot < slots && offset + slot < inventory.length)
            {
                ItemStack stack = ItemStack.loadItemStackFromNBT(item);
                if (stack != null && countKey != null && item.hasKey(countKey)) stack.stackSize = item.getInteger(countKey);
                inventory[offset + slot] = stack;
            } else
            {
                if (kept == null) kept = new NBTTagList();
                kept.appendTag(item.copy());
            }
        }
        return kept;
    }

    public static int writeSlots(NBTTagCompound data, String key, ItemStack[] inventory, int offset, int slots, NBTTagList kept, String countKey, boolean slotTags)
    {
        NBTTagList list = new NBTTagList();
        int items = 0;
        for (int q = 0; q < slots && offset + q < inventory.length; q++)
        {
            ItemStack stack = inventory[offset + q];
            if (stack != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                if (slotTags) item.setByte("Slot", (byte) q);
                stack.writeToNBT(item);
                if (countKey != null) item.setInteger(countKey, stack.stackSize);
                list.appendTag(item);
                items += stack.stackSize;
            }
        }
        if (kept != null)
        {
            for (int i = 0; i < kept.tagCount(); i++) list.appendTag(kept.getCompoundTagAt(i).copy());
        }
        data.setTag(key, list);
        return items;
    }

    public static NBTTagList writeItems(ItemStack[] inventory)
    {
        return writeItems(inventory, inventory.length);
    }

    public static NBTTagList writeItems(ItemStack[] inventory, int end)
    {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < Math.min(end, inventory.length); i++)
        {
            ItemStack stack = inventory[i];
            if (stack != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        return items;
    }

    public static NBTTagList writeItems(ItemStack[] inventory, WidgetLayout layout, int end)
    {
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < Math.min(end, inventory.length); i++)
        {
            ItemStack stack = inventory[i];
            if (stack != null && !WidgetStore.persistsApart(layout, i))
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        return items;
    }
}
