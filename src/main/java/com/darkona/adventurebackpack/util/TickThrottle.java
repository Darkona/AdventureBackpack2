package com.darkona.adventurebackpack.util;

public final class TickThrottle
{
    private TickThrottle() {}

    public static boolean due(int lastTick, int nowTick, int interval)
    {
        return nowTick < lastTick || (long) nowTick - lastTick >= interval;
    }

    public static boolean every(long worldTime, int entityId, int interval)
    {
        if (interval <= 1) return true;
        return Math.floorMod(worldTime + entityId, (long) interval) == 0;
    }
}
