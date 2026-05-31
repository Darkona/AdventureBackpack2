package com.darkona.adventurebackpack.util;

/**
 * Pure "is it time to redo this?" decision for work that does not need to run every frame (#41).
 */
public final class TickThrottle
{
    private TickThrottle() {}

    /** True when at least {@code interval} ticks passed since {@code lastTick}, or the clock went backwards (new world). */
    public static boolean due(int lastTick, int nowTick, int interval)
    {
        return nowTick < lastTick || (long) nowTick - lastTick >= interval;
    }

    /**
     * True once every {@code interval} ticks of world time, without a counter to store. The entity id shifts the beat
     * so that several players do not all do the work in the same tick. An interval of 1 or less is every tick.
     */
    public static boolean every(long worldTime, int entityId, int interval)
    {
        if (interval <= 1) return true;
        return Math.floorMod(worldTime + entityId, (long) interval) == 0;
    }
}
