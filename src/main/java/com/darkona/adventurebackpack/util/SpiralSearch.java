package com.darkona.adventurebackpack.util;

import java.util.Arrays;

public final class SpiralSearch
{
    public interface Cell
    {
        boolean accept(int x, int y, int z);
    }

    public static final long NONE = Long.MIN_VALUE;

    private static final int MAX_EXTENT = 127;
    private static final int BIAS = 128;
    private static final int[][][] TABLES = new int[MAX_EXTENT + 1][][];

    private SpiralSearch() {}

    public static long find(int x, int y, int z, int radius, int vertical, Cell cell)
    {
        int[] offsets = offsets(radius, vertical);
        for (int i = 0; i < offsets.length; i++)
        {
            int packed = offsets[i];
            int cx = x + (packed >> 16 & 0xFF) - BIAS;
            int cy = y + (packed >> 8 & 0xFF) - BIAS;
            int cz = z + (packed & 0xFF) - BIAS;
            if (cell.accept(cx, cy, cz)) return pack(cx, cy, cz);
        }
        return NONE;
    }

    public static long pack(int x, int y, int z)
    {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }

    public static int unpackX(long packed)
    {
        return (int) (packed >> 38);
    }

    public static int unpackY(long packed)
    {
        return (int) (packed << 52 >> 52);
    }

    public static int unpackZ(long packed)
    {
        return (int) (packed << 26 >> 38);
    }

    private static synchronized int[] offsets(int radius, int vertical)
    {
        if (radius < 0 || radius > MAX_EXTENT || vertical < 0 || vertical > MAX_EXTENT)
        {
            throw new IllegalArgumentException("radius " + radius + " and vertical " + vertical + " must be between 0 and " + MAX_EXTENT);
        }
        int[][] byVertical = TABLES[radius];
        if (byVertical == null)
        {
            byVertical = new int[MAX_EXTENT + 1][];
            TABLES[radius] = byVertical;
        }
        int[] table = byVertical[vertical];
        if (table == null)
        {
            table = build(radius, vertical);
            byVertical[vertical] = table;
        }
        return table;
    }

    private static int[] build(int radius, int vertical)
    {
        int reach = radius * radius;
        long[] keys = new long[(2 * radius + 1) * (2 * radius + 1) * (2 * vertical + 1)];
        int count = 0;
        for (int dx = -radius; dx <= radius; dx++)
        {
            for (int dy = -vertical; dy <= vertical; dy++)
            {
                for (int dz = -radius; dz <= radius; dz++)
                {
                    int squared = dx * dx + dy * dy + dz * dz;
                    if (squared > reach) continue;
                    long packed = (dx + BIAS) << 16 | (dy + BIAS) << 8 | (dz + BIAS);
                    keys[count++] = (long) squared << 32 | (long) Math.abs(dy) << 24 | packed;
                }
            }
        }
        Arrays.sort(keys, 0, count);
        int[] offsets = new int[count];
        for (int i = 0; i < count; i++) offsets[i] = (int) (keys[i] & 0xFFFFFF);
        return offsets;
    }
}
