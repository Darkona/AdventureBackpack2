package com.darkona.adventurebackpack.common.sort;

import java.util.Arrays;

public final class SlotLocks
{
    public static final int CAPACITY = 128;
    public static final int WORDS = CAPACITY / 32;
    public static final int CHUNKS = CAPACITY / 16;
    private static final int CHUNK_MASK = 0xFFFF;

    private final int[] bits = new int[WORDS];
    private int version;

    public boolean isLocked(int index)
    {
        return index >= 0 && index < CAPACITY && (bits[index >> 5] >>> (index & 31) & 1) != 0;
    }

    public boolean set(int index, boolean locked)
    {
        if (index < 0 || index >= CAPACITY || isLocked(index) == locked) return false;
        bits[index >> 5] ^= 1 << (index & 31);
        version++;
        return true;
    }

    public boolean toggle(int index)
    {
        return set(index, !isLocked(index));
    }

    public boolean isEmpty()
    {
        for (int i = 0; i < WORDS; i++)
        {
            if (bits[i] != 0) return false;
        }
        return true;
    }

    public int count()
    {
        int n = 0;
        for (int i = 0; i < WORDS; i++) n += Integer.bitCount(bits[i]);
        return n;
    }

    public int version()
    {
        return version;
    }

    public int chunk(int i)
    {
        return bits[i >> 1] >>> ((i & 1) << 4) & CHUNK_MASK;
    }

    public void setChunk(int i, int value)
    {
        if (i < 0 || i >= CHUNKS) return;
        int shift = (i & 1) << 4;
        int word = bits[i >> 1];
        int next = word & ~(CHUNK_MASK << shift) | (value & CHUNK_MASK) << shift;
        if (next == word) return;
        bits[i >> 1] = next;
        version++;
    }

    public int[] toInts()
    {
        return bits.clone();
    }

    public void load(int[] saved)
    {
        int[] next = new int[WORDS];
        if (saved != null) System.arraycopy(saved, 0, next, 0, Math.min(saved.length, WORDS));
        if (Arrays.equals(next, bits)) return;
        System.arraycopy(next, 0, bits, 0, WORDS);
        version++;
    }

    public void clear()
    {
        load(null);
    }
}
