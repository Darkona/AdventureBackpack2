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
}
