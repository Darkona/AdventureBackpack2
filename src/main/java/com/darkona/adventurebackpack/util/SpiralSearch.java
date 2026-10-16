package com.darkona.adventurebackpack.util;

public final class SpiralSearch
{
    public interface Cell
    {
        boolean accept(int x, int z);
    }

    public static final long NONE = Long.MIN_VALUE;

    private SpiralSearch() {}

    public static long find(int x, int z, int radius, Cell cell)
    {
        for (int steps = 1; steps < radius; steps++)
        {
            int dir = (steps % 2 == 0) ? 1 : -1;
            for (int k = 0; k <= steps; k++)
            {
                if (cell.accept(x + dir * k, z)) return pack(x + dir * k, z);
            }
            x += dir * steps;
            for (int k = 0; k <= steps; k++)
            {
                if (cell.accept(x, z - dir * k)) return pack(x, z - dir * k);
            }
            z -= dir * steps;
        }
        return NONE;
    }

    public static long pack(int x, int z)
    {
        return ((long) x << 32) | (z & 0xFFFFFFFFL);
    }

    public static int unpackX(long packed)
    {
        return (int) (packed >> 32);
    }

    public static int unpackZ(long packed)
    {
        return (int) packed;
    }
}
