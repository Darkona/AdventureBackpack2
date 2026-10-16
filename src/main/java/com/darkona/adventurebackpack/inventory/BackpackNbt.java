package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.QuiverFeedRules;
import com.darkona.adventurebackpack.common.QuiverRules;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public final class BackpackNbt
{
    public static final String ITEMS = "ABPItems";
    public static final String STORAGE_SIZE = "storageSize";
    public static final String QUIVER = "ABPQuiver";
    public static final String QUIVER_FILL = "quiverFill";
    public static final String EXTENDED = "extendedProperties";
    public static final String LEGACY_EXTENDED = "extended";

    private BackpackNbt()
    {
    }

    public static int savedStorage(NBTTagCompound data)
    {
        int saved = data.hasKey(STORAGE_SIZE) ? data.getInteger(STORAGE_SIZE)
                : data.hasKey(ITEMS) ? BackpackLayout.LEGACY_STORAGE : 0;
        NBTTagList items = data.getTagList(ITEMS, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            int slot = items.getCompoundTagAt(i).getByte("Slot") & 0xFF;
            if (slot >= BackpackLayout.FIRST_EXTRA_STORAGE)
            {
                saved = Math.max(saved, slot - BackpackLayout.SPECIAL_SLOTS + 1);
            }
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

    public static NBTTagList readQuiver(NBTTagCompound data, ItemStack[] inventory, int offset, int slots)
    {
        NBTTagList list = data.getTagList(QUIVER, Constants.NBT.TAG_COMPOUND);
        NBTTagList kept = null;
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            if (slot < slots && offset + slot < inventory.length)
            {
                inventory[offset + slot] = ItemStack.loadItemStackFromNBT(item);
            } else
            {
                if (kept == null) kept = new NBTTagList();
                kept.appendTag(item.copy());
            }
        }
        return kept;
    }

    public static void writeQuiver(NBTTagCompound data, ItemStack[] inventory, int offset, int slots, NBTTagList kept)
    {
        if (slots <= 0 && kept == null)
        {
            data.removeTag(QUIVER);
            data.removeTag(QUIVER_FILL);
            return;
        }
        NBTTagList list = new NBTTagList();
        int items = 0;
        for (int q = 0; q < slots && offset + q < inventory.length; q++)
        {
            ItemStack stack = inventory[offset + q];
            if (stack != null)
            {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) q);
                stack.writeToNBT(item);
                list.appendTag(item);
                items += stack.stackSize;
            }
        }
        if (kept != null)
        {
            for (int i = 0; i < kept.tagCount(); i++) list.appendTag(kept.getCompoundTagAt(i).copy());
        }
        data.setTag(QUIVER, list);
        data.setByte(QUIVER_FILL, (byte) QuiverRules.level(slots > 0 ? items : 0));
    }

    public static void readQuiverCounts(NBTTagCompound data, int slots, int[] ids, int[] counts)
    {
        int n = Math.min(slots, Math.min(ids.length, counts.length));
        for (int q = 0; q < n; q++)
        {
            ids[q] = 0;
            counts[q] = 0;
        }
        if (data == null) return;
        NBTTagList list = data.getTagList(QUIVER, Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.tagCount(); i++)
        {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            if (slot >= n || item.hasKey("tag") || item.getShort("Damage") != 0) continue;
            ids[slot] = item.getShort("id");
            counts[slot] = Math.max(0, (int) item.getByte("Count"));
        }
    }

    public static int takeFromQuiver(NBTTagCompound data, int slots, int[] ids, int[] spent)
    {
        if (data == null || !data.hasKey(QUIVER)) return 0;
        NBTTagList list = data.getTagList(QUIVER, Constants.NBT.TAG_COMPOUND);
        int n = Math.min(slots, Math.min(ids.length, spent.length));
        int taken = 0;
        int items = 0;
        for (int i = list.tagCount() - 1; i >= 0; i--)
        {
            NBTTagCompound item = list.getCompoundTagAt(i);
            int slot = item.getByte("Slot") & 0xFF;
            int count = item.getByte("Count");
            if (slot < n && spent[slot] > 0 && item.getShort("id") == ids[slot])
            {
                int remove = QuiverFeedRules.toRemove(spent[slot], count);
                count -= remove;
                taken += remove;
                if (count <= 0)
                {
                    list.removeTag(i);
                } else
                {
                    item.setByte("Count", (byte) count);
                }
            }
            if (slot < slots && count > 0) items += count;
        }
        if (slots > 0) data.setByte(QUIVER_FILL, (byte) QuiverRules.level(items));
        return taken;
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
}
