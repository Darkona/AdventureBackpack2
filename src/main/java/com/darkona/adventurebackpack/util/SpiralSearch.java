package com.darkona.adventurebackpack.util;

/**
 * Square spiral walk around a point on one Y level, nearest cells first. Used to find a free spot for a backpack
 * placed on death and for a player respawning at a campfire. The old recursive walk tested the first cell of each
 * leg over and over instead of every cell along it, so it saw only the corners of the spiral.
 */
public final class SpiralSearch
{
    public interface Cell
    {
        boolean accept(int x, int z);
    }

    /** Value returned when no cell is accepted. */
    public static final long NONE = Long.MIN_VALUE;

    private SpiralSearch() {}

    /**
     * Walks legs of length 1, 2, 3... up to {@code radius - 1}, alternating +X/-Z and -X/+Z, starting at (x, z).
     *
     * @return the accepted cell packed with {@link #pack}, or {@link #NONE}
     */
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
