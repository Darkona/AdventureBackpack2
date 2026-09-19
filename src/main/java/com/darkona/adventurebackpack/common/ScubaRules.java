package com.darkona.adventurebackpack.common;

/**
 * Pure decisions of the Scuba Gear: when it pays for the air, how much each beat costs, which tank pays first and
 * when the wearer is warned. No Minecraft types: the unit test calls it directly.
 */
public final class ScubaRules
{
    private ScubaRules() {}

    /** Vanilla's full air bar. */
    public static final int MAX_AIR = 300;

    /**
     * The gear pays only when the head is under water, the tanks hold air and the vanilla air bar went down since
     * the last beat. A potion of water breathing (or anything else that keeps the bar full) makes it free.
     */
    public static boolean consumesNow(boolean headInWater, boolean airDroppedSinceBeat, boolean hasAir)
    {
        return headInWater && airDroppedSinceBeat && hasAir;
    }

    /** Millibuckets one beat of {@code intervalTicks} costs at {@code perSecond}, rounded up so a cost is never lost. */
    public static int airPerBeat(int perSecond, int intervalTicks)
    {
        if (perSecond <= 0 || intervalTicks <= 0) return 0;
        long ticks = (long) perSecond * intervalTicks;
        return (int) Math.min(Integer.MAX_VALUE, (ticks + 19) / 20);
    }

    /** What the first (left) tank pays of {@code due}: everything it can. */
    public static int fromFirst(int first, int due)
    {
        if (first <= 0 || due <= 0) return 0;
        return Math.min(first, due);
    }

    /** What the second (right) tank pays: what the first one could not, as far as it can. */
    public static int fromSecond(int first, int second, int due)
    {
        int rest = due - fromFirst(first, due);
        if (second <= 0 || rest <= 0) return 0;
        return Math.min(second, rest);
    }

    /** True when this beat took the air from {@code threshold} or more to below it (once per descent). */
    public static boolean shouldWarn(int before, int after, int threshold)
    {
        return threshold > 0 && before >= threshold && after < threshold;
    }

    /** True when this beat used the last of the air. */
    public static boolean becameEmpty(int before, int after)
    {
        return before > 0 && after <= 0;
    }
}
