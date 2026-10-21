package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.inventory.widget.classic.QuiverNbt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.darkona.adventurebackpack.common.QuiverRules;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Test;

/**
 * What the crossbow fed from a worn quiver reads from and writes to the backpack NBT: the counts of each quiver slot,
 * read without building stacks, and the spent items taken out at the end of a burst, never more than are there.
 */
class QuiverFeedNbtTest
{
    private static final int ARROW = 262;
    private static final int BOLT = 4321;

    private static NBTTagCompound entry(int slot, int id, int count)
    {
        NBTTagCompound c = new NBTTagCompound();
        c.setByte("Slot", (byte) slot);
        c.setShort("id", (short) id);
        c.setByte("Count", (byte) count);
        c.setShort("Damage", (short) 0);
        return c;
    }

    private static NBTTagCompound data(NBTTagCompound... entries)
    {
        NBTTagCompound data = new NBTTagCompound();
        NBTTagList list = new NBTTagList();
        for (NBTTagCompound e : entries) list.appendTag(e);
        data.setTag(QuiverNbt.QUIVER, list);
        return data;
    }

    @Test
    void theCountsOfEachQuiverSlotAreReadWithoutStacks()
    {
        NBTTagCompound tagged = entry(3, ARROW, 5);
        tagged.setTag("tag", new NBTTagCompound());
        NBTTagCompound damaged = entry(2, ARROW, 9);
        damaged.setShort("Damage", (short) 1);
        // Slot 5 is kept for a bigger quiver: not one of today's 4 slots
        NBTTagCompound data = data(entry(0, ARROW, 40), entry(1, BOLT, 12), damaged, tagged, entry(5, BOLT, 64));
        int[] ids = {7, 7, 7, 7, 7, 7};
        int[] counts = {7, 7, 7, 7, 7, 7};
        QuiverNbt.readQuiverCounts(data, 4, ids, counts);
        assertArrayEquals(new int[]{ARROW, BOLT, 0, 0, 7, 7}, ids, "a tagged or damaged stack reads as empty");
        assertArrayEquals(new int[]{40, 12, 0, 0, 7, 7}, counts);

        QuiverNbt.readQuiverCounts(null, 4, ids, counts);
        assertArrayEquals(new int[]{0, 0, 0, 0, 7, 7}, counts, "no backpack data: an empty quiver");
    }

    @Test
    void spentItemsLeaveTheQuiverAndItsFillLevelFollows()
    {
        NBTTagCompound data = data(entry(0, ARROW, 40), entry(1, BOLT, 3), entry(5, BOLT, 64));
        int[] ids = {ARROW, BOLT, 0, 0};
        int[] spent = {10, 3, 0, 0};
        assertEquals(13, QuiverNbt.takeFromQuiver(data, 4, ids, spent));
        int[] counts = new int[4];
        QuiverNbt.readQuiverCounts(data, 4, ids, counts);
        assertArrayEquals(new int[]{30, 0, 0, 0}, counts, "the emptied bolt slot left the list");
        assertEquals(2, data.getTagList(QuiverNbt.QUIVER, 10).tagCount(), "the kept slot 5 stays untouched");
        assertEquals(QuiverRules.level(30), data.getByte(QuiverNbt.QUIVER_FILL), "the kept stack does not count for the model");
    }

    @Test
    void writingBackNeverTakesWhatIsNotThere()
    {
        // Someone swapped slot 0 for bolts and emptied half of slot 1 meanwhile
        NBTTagCompound data = data(entry(0, BOLT, 20), entry(1, ARROW, 2));
        int[] ids = {ARROW, ARROW};
        int[] spent = {5, 6};
        assertEquals(2, QuiverNbt.takeFromQuiver(data, 2, ids, spent));
        int[] newIds = new int[2];
        int[] counts = new int[2];
        QuiverNbt.readQuiverCounts(data, 2, newIds, counts);
        assertArrayEquals(new int[]{BOLT, 0}, newIds);
        assertArrayEquals(new int[]{20, 0}, counts);

        assertEquals(0, QuiverNbt.takeFromQuiver(new NBTTagCompound(), 2, ids, spent), "no quiver: nothing to take");
        assertEquals(0, QuiverNbt.takeFromQuiver(null, 2, ids, spent));
    }
}
