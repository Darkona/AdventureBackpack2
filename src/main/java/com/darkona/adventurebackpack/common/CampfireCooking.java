package com.darkona.adventurebackpack.common;

public final class CampfireCooking
{
    private CampfireCooking()
    {
    }

    public static boolean canCook(boolean enabled, boolean hasResult, boolean resultIsFood, boolean onlyFood)
    {
        return enabled && hasResult && (resultIsFood || !onlyFood);
    }

    public static int take(int held, int inSlot, boolean sameItem, int capacity)
    {
        if (held <= 0) return 0;
        if (inSlot > 0 && !sameItem) return 0;
        int room = capacity - inSlot;
        return room <= 0 ? 0 : Math.min(held, room);
    }

    public static boolean ready(long now, long readyAt)
    {
        return now >= readyAt;
    }

    public static int delay(long now, long readyAt)
    {
        long left = readyAt - now;
        if (left < 1) return 1;
        return left > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) left;
    }

    public static boolean inReturnRange(double distanceSq, double range)
    {
        return range > 0 && distanceSq <= range * range;
    }
}
