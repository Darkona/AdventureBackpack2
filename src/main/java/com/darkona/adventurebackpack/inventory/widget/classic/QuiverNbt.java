package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.common.QuiverFeedRules;
import com.darkona.adventurebackpack.common.QuiverRules;
import com.darkona.adventurebackpack.inventory.BackpackNbt;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

public final class QuiverNbt
{
    public static final String QUIVER = "ABPQuiver";
    public static final String QUIVER_FILL = "quiverFill";

    private QuiverNbt()
    {
    }

    public static NBTTagList readQuiver(NBTTagCompound data, ItemStack[] inventory, int offset, int slots)
    {
        return BackpackNbt.readSlots(data, QUIVER, inventory, offset, slots, null);
    }

    public static void writeQuiver(NBTTagCompound data, ItemStack[] inventory, int offset, int slots, NBTTagList kept)
    {
        if (slots <= 0 && kept == null)
        {
            data.removeTag(QUIVER);
            data.removeTag(QUIVER_FILL);
            return;
        }
        int items = BackpackNbt.writeSlots(data, QUIVER, inventory, offset, slots, kept, null, true);
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
}
