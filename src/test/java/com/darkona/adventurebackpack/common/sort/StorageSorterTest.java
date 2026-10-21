package com.darkona.adventurebackpack.common.sort;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class StorageSorterTest
{
    private static StorageSorter.Entry entry(int group, int category, String name, int id, int damage, int count, int max)
    {
        return new StorageSorter.Entry(group, category, name, id, damage, count, max, name + "#" + group);
    }

    @Test
    void stacksOfTheSameKindMergeUpToTheMaximumAndTheRestGoesInOneStack()
    {
        StorageSorter.Entry[] in = {entry(1, 0, "Dirt", 3, 0, 40, 64), entry(1, 0, "Dirt", 3, 0, 50, 64), entry(1, 0, "Dirt", 3, 0, 30, 64)};
        StorageSorter.Plan plan = StorageSorter.arrange(in, 6);
        assertNotNull(plan);
        assertEquals(2, plan.used);
        assertEquals(64, plan.count[0]);
        assertEquals(56, plan.count[1]);
        assertEquals(0, plan.count[2]);
    }

    @Test
    void emptySlotsComeAtTheEnd()
    {
        StorageSorter.Entry[] in = {entry(1, 0, "Stone", 1, 0, 1, 64), entry(2, 0, "Apple", 260, 0, 1, 64)};
        StorageSorter.Plan plan = StorageSorter.arrange(in, 5);
        assertEquals(2, plan.used);
        for (int i = 2; i < 5; i++) assertEquals(0, plan.count[i]);
    }

    @Test
    void categoryComesFirstThenNameThenIdThenDamage()
    {
        StorageSorter.Entry[] in = {
                entry(1, 5, "Apple", 260, 0, 1, 64),
                entry(2, 1, "zinc", 900, 0, 1, 64),
                entry(3, 1, "Brick", 45, 0, 1, 64),
                entry(4, StorageSorter.NO_CATEGORY, "Aaa", 1, 0, 1, 64),
                entry(5, 1, "brick", 44, 2, 1, 64),
                entry(6, 1, "brick", 44, 1, 1, 64)};
        StorageSorter.Plan plan = StorageSorter.arrange(in, 10);
        Object[] expected = {"brick#6", "brick#5", "Brick#3", "zinc#2", "Apple#1", "Aaa#4"};
        assertArrayEquals(expected, java.util.Arrays.copyOf(plan.source, 6));
    }

    @Test
    void theSameInputGivesTheSameOrderAndEqualKeysKeepTheirFirstSeenOrder()
    {
        StorageSorter.Entry[] in = {entry(1, 0, "Same", 5, 0, 1, 1), entry(2, 0, "Same", 5, 0, 1, 1), entry(3, 0, "Same", 5, 0, 1, 1)};
        Object[] first = StorageSorter.arrange(in, 3).source;
        Object[] second = StorageSorter.arrange(in, 3).source;
        assertArrayEquals(first, second);
        assertArrayEquals(new Object[]{"Same#1", "Same#2", "Same#3"}, first);
    }

    @Test
    void unstackableItemsStaySeparate()
    {
        StorageSorter.Entry[] in = {entry(1, 0, "Pickaxe", 257, 0, 1, 1), entry(2, 0, "Pickaxe", 257, 10, 1, 1)};
        StorageSorter.Plan plan = StorageSorter.arrange(in, 2);
        assertEquals(2, plan.used);
        assertEquals(1, plan.count[0]);
        assertEquals(1, plan.count[1]);
    }

    @Test
    void nothingIsLostWhenTheResultDoesNotFit()
    {
        StorageSorter.Entry[] in = {entry(1, 0, "Oversized", 9, 0, 200, 16), entry(2, 0, "Other", 8, 0, 5, 64)};
        assertNull(StorageSorter.arrange(in, 13));
        assertEquals(14, StorageSorter.arrange(in, 14).used);
    }

    @Test
    void totalsAreKept()
    {
        StorageSorter.Entry[] in = {entry(1, 0, "A", 1, 0, 37, 16), entry(2, 0, "B", 2, 0, 9, 64), entry(1, 0, "A", 1, 0, 30, 16)};
        StorageSorter.Plan plan = StorageSorter.arrange(in, 12);
        int a = 0, b = 0;
        for (int i = 0; i < plan.used; i++)
        {
            if (plan.source[i].equals("A#1")) a += plan.count[i];
            else b += plan.count[i];
        }
        assertEquals(67, a);
        assertEquals(9, b);
    }

    @Test
    void anEmptyStorageGivesAnEmptyPlan()
    {
        StorageSorter.Plan plan = StorageSorter.arrange(new StorageSorter.Entry[0], 4);
        assertEquals(0, plan.used);
    }
}
