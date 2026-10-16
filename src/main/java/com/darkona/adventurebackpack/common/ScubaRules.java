package com.darkona.adventurebackpack.common;

public final class ScubaRules
{
    private ScubaRules() {}

    public static final int MAX_AIR = 300;

    public static boolean consumesNow(boolean headInWater, boolean airDroppedSinceBeat, boolean hasAir)
    {
        return headInWater && airDroppedSinceBeat && hasAir;
    }

    public static int airPerBeat(int perSecond, int intervalTicks)
    {
        if (perSecond <= 0 || intervalTicks <= 0) return 0;
        long ticks = (long) perSecond * intervalTicks;
        return (int) Math.min(Integer.MAX_VALUE, (ticks + 19) / 20);
    }

    public static int fromFirst(int first, int due)
    {
        if (first <= 0 || due <= 0) return 0;
        return Math.min(first, due);
    }

    public static int fromSecond(int first, int second, int due)
    {
        int rest = due - fromFirst(first, due);
        if (second <= 0 || rest <= 0) return 0;
        return Math.min(second, rest);
    }

    public static boolean shouldWarn(int before, int after, int threshold)
    {
        return threshold > 0 && before >= threshold && after < threshold;
    }

    public static boolean becameEmpty(int before, int after)
    {
        return before > 0 && after <= 0;
    }
}
