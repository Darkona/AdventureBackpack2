package com.darkona.adventurebackpack.common;

public final class SleepingBagRules
{
    private static final int[][] FOOT_TO_HEAD = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

    private SleepingBagRules() {}

    public static int direction(int meta)
    {
        return meta & 3;
    }

    public static boolean isHead(int meta)
    {
        return (meta & 8) != 0;
    }

    public static int headDx(int meta)
    {
        return FOOT_TO_HEAD[direction(meta)][0];
    }

    public static int headDz(int meta)
    {
        return FOOT_TO_HEAD[direction(meta)][1];
    }

    public static int footX(int x, int meta)
    {
        return isHead(meta) ? x - headDx(meta) : x;
    }

    public static int footZ(int z, int meta)
    {
        return isHead(meta) ? z - headDz(meta) : z;
    }

    public static int backpackX(int footX, int meta)
    {
        return footX - headDx(meta);
    }

    public static int backpackZ(int footZ, int meta)
    {
        return footZ - headDz(meta);
    }

    public static int directionFromYaw(float yaw)
    {
        return (int) Math.floor(yaw * 4.0F / 360.0F + 0.5D) & 3;
    }

    public static boolean dropsItem(int meta, boolean ownedByBackpack)
    {
        return !isHead(meta) && !ownedByBackpack;
    }
}
