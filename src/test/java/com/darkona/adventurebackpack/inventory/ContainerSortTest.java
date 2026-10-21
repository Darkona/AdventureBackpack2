package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.sort.SlotLocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ContainerSortTest
{
    private static final Item DIRT = new Item().setUnlocalizedName("sortDirt");
    private static final Item APPLE = new Item().setUnlocalizedName("sortApple");

    private static ItemStack stack(Item item, int count)
    {
        return new ItemStack(item, count);
    }

    @Test
    void theStorageIsMergedAndPackedAtTheStartAndTheRestStaysEmpty() throws Exception
    {
        Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
        contents.put(5, stack(DIRT, 40));
        contents.put(9, stack(APPLE, 3));
        contents.put(20, stack(DIRT, 40));
        ContainerBackpack c = ContainerBackpackSlotsTest.build("Standard", contents, 0, new SlotLocks());
        assertTrue(c.sortStorage());
        assertEquals(3, contents.size());
        int dirt = 0;
        int apples = 0;
        for (Map.Entry<Integer, ItemStack> e : contents.entrySet())
        {
            assertTrue(e.getKey() < 3, "packed at the start, found at " + e.getKey());
            if (e.getValue().getItem() == DIRT) dirt += e.getValue().stackSize;
            else apples += e.getValue().stackSize;
        }
        assertEquals(80, dirt);
        assertEquals(3, apples);
    }

    @Test
    void lockedSlotsKeepTheirStackAndAreNotUsedAsRoom() throws Exception
    {
        Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
        ItemStack kept = stack(APPLE, 7);
        contents.put(0, kept);
        contents.put(4, stack(DIRT, 10));
        contents.put(8, stack(DIRT, 10));
        SlotLocks locks = new SlotLocks();
        locks.set(0, true);
        locks.set(1, true);
        ContainerBackpack c = ContainerBackpackSlotsTest.build("Standard", contents, 0, locks);
        assertTrue(c.sortStorage());
        assertSame(kept, contents.get(0));
        assertNull(contents.get(1));
        assertEquals(20, contents.get(2).stackSize);
        assertEquals(2, contents.size());
    }

    @Test
    void nothingOutsideTheStorageIsTouched() throws Exception
    {
        Map<Integer, ItemStack> contents = new HashMap<Integer, ItemStack>();
        ItemStack tool = stack(DIRT, 1);
        ItemStack rope = stack(APPLE, 5);
        contents.put(39, tool);
        contents.put(45, rope);
        contents.put(12, stack(DIRT, 4));
        ContainerBackpack c = ContainerBackpackSlotsTest.build("Standard", contents, 0, new SlotLocks());
        c.sortStorage();
        assertSame(tool, contents.get(39));
        assertSame(rope, contents.get(45));
        assertEquals(4, contents.get(0).stackSize);
    }
}
