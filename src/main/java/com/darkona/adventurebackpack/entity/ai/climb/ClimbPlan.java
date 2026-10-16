package com.darkona.adventurebackpack.entity.ai.climb;

public final class ClimbPlan
{
    public enum Mode
    {
        NONE,
        APPROACH,
        ASCEND,
        LIP,
        DESCEND,
        DROP,
        FAILED
    }

    public Mode mode = Mode.NONE;
    public int wallX, wallZ;
    public int approachX, approachZ;
    public int startY;
    public int topY;
    public int bottomY;
    public boolean approachByPath;

    public void reset()
    {
        mode = Mode.NONE;
        wallX = wallZ = approachX = approachZ = startY = topY = bottomY = 0;
        approachByPath = false;
    }

    public long columnKey()
    {
        return key(wallX, wallZ, mode == Mode.DESCEND || mode == Mode.DROP ? bottomY : topY);
    }

    public static long key(int x, int z, int y)
    {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }
}
