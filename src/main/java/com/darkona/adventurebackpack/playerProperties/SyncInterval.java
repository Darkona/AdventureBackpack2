package com.darkona.adventurebackpack.playerProperties;

public final class SyncInterval
{
    private SyncInterval()
    {
    }

    public static boolean due(int now, int last, int interval)
    {
        if (interval <= 1 || last == Integer.MIN_VALUE) return true;
        long elapsed = (long) now - last;
        return elapsed < 0 || elapsed >= interval;
    }
}
