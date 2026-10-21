package com.darkona.adventurebackpack.common.sort;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SlotLocksTest
{
    @Test
    void aSlotIsLockedAfterTheToggleAndFreeAfterTheSecond()
    {
        SlotLocks locks = new SlotLocks();
        assertFalse(locks.isLocked(5));
        assertTrue(locks.toggle(5));
        assertTrue(locks.isLocked(5));
        assertTrue(locks.toggle(5));
        assertFalse(locks.isLocked(5));
        assertTrue(locks.isEmpty());
    }

    @Test
    void indicesOutsideTheCapacityAreNeverLockedAndChangeNothing()
    {
        SlotLocks locks = new SlotLocks();
        assertFalse(locks.set(-1, true));
        assertFalse(locks.set(SlotLocks.CAPACITY, true));
        assertFalse(locks.isLocked(SlotLocks.CAPACITY));
        assertTrue(locks.isEmpty());
        assertEquals(0, locks.version());
    }

    @Test
    void theVersionMovesOnlyWhenTheStateChanges()
    {
        SlotLocks locks = new SlotLocks();
        locks.set(3, true);
        int v = locks.version();
        locks.set(3, true);
        assertEquals(v, locks.version());
        locks.set(3, false);
        assertEquals(v + 1, locks.version());
    }

    @Test
    void savedIntsRestoreTheSameLocksAndShortArraysAreAccepted()
    {
        SlotLocks a = new SlotLocks();
        a.set(0, true);
        a.set(31, true);
        a.set(32, true);
        a.set(116, true);
        SlotLocks b = new SlotLocks();
        b.load(a.toInts());
        for (int i = 0; i < SlotLocks.CAPACITY; i++) assertEquals(a.isLocked(i), b.isLocked(i), "slot " + i);
        assertEquals(4, b.count());
        b.load(new int[]{1});
        assertEquals(1, b.count());
        assertTrue(b.isLocked(0));
        b.load(null);
        assertTrue(b.isEmpty());
    }

    @Test
    void chunksOf16BitsRebuildTheState()
    {
        SlotLocks a = new SlotLocks();
        a.set(1, true);
        a.set(17, true);
        a.set(100, true);
        SlotLocks b = new SlotLocks();
        for (int i = 0; i < SlotLocks.CHUNKS; i++) b.setChunk(i, a.chunk(i) | 0xFFFF0000);
        assertArrayEquals(a.toInts(), b.toInts());
    }
}
