package com.darkona.adventurebackpack.common;

public final class AbilityTimer
{
    public static final int SECOND_CHECK_TICKS = 20;

    private AbilityTimer() {}

    public static boolean due(long now, long next, long maxWait)
    {
        return now >= next || next - now > maxWait;
    }

    public static long remaining(long now, long end, long length)
    {
        long left = end - now;
        return left > 0 && left <= length ? left : 0;
    }
}
