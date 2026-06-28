package com.darkona.adventurebackpack.common;

/**
 * Timing decisions of the worn backpack abilities, on world time (ticks). An ability does not count down in the
 * stack: it keeps one mark (the world time when it may fire next, or when a run ends) that is written only when the
 * ability fires. A countdown written every tick changed the stack every tick, which re-sent the wearable to every
 * nearby player 20 times a second.
 */
public final class AbilityTimer
{
    /** How often the abilities with a wait of whole seconds (Pig, Chicken) look at their mark. */
    public static final int SECOND_CHECK_TICKS = 20;

    private AbilityTimer() {}

    /**
     * True when an ability whose next allowed time is {@code next} may fire at {@code now}. A mark further ahead than
     * the longest wait ({@code maxWait}) cannot come from this world (the backpack came from another save, or the clock
     * went back), so it counts as due instead of blocking the ability for a very long time.
     */
    public static boolean due(long now, long next, long maxWait)
    {
        return now >= next || next - now > maxWait;
    }

    /**
     * Ticks left of a run of {@code length} ticks that ends at world time {@code end}; 0 when it is over. A run that
     * would end further ahead than its length cannot be real (the clock went back) and counts as over.
     */
    public static long remaining(long now, long end, long length)
    {
        long left = end - now;
        return left > 0 && left <= length ? left : 0;
    }
}
