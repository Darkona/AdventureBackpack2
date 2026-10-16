package com.darkona.adventurebackpack.common.ability;

final class MilkBatch
{
    private MilkBatch()
    {
    }

    static int remaining(int milkTime)
    {
        return Math.max(0, milkTime);
    }

    static boolean eats(int remaining, boolean hasWheat)
    {
        return remaining <= 0 && hasWheat;
    }

    static boolean starts(int wheatConsumed, int wheatPerBatch)
    {
        return wheatConsumed >= wheatPerBatch;
    }

    static int chunk(int remaining, int eatInterval)
    {
        return remaining <= 0 ? 0 : Math.min(remaining, Math.max(1, eatInterval));
    }
}
