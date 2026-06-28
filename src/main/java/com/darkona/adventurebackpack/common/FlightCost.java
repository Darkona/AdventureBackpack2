package com.darkona.adventurebackpack.common;

/**
 * What flying costs, as pure functions so the config values can be tested without the game ("flying is expensive").
 * Copter Pack: fuel per tick while the engine runs, more while hovering and climbing, taken once every few ticks, and
 * a start cost. Steam Jetpack: steam per tick of thrust and an ignition cost.
 */
public final class FlightCost
{
    private FlightCost() {}

    /** Copter fuel per tick before the fuel's own multiplier. */
    public static int copterPerTick(int base, boolean hovering, int hoverExtra, boolean climbing, int climbExtra)
    {
        int cost = base;
        if (hovering) cost += hoverExtra;
        if (climbing) cost += climbExtra;
        return Math.max(0, cost);
    }

    /**
     * Fuel taken once every {@code interval} ticks: the cost of all those ticks at once, times the fuel's multiplier
     * (oil 1.5, fuel 1.0...). Rounded up, so a fuel with a multiplier below 1 is never free; the old code rounded
     * {@code 1 * 0.8} down to 0 and took the per-tick amount only once per interval.
     */
    public static int copterFuelPerInterval(int perTick, int interval, double multiplier)
    {
        if (perTick <= 0 || interval <= 0 || multiplier <= 0) return 0;
        // the multipliers come from floats (1.2f is 1.2000000477): a tiny slack keeps 5 x 1.2 at 6, not 7
        return (int) Math.ceil(perTick * (double) interval * multiplier - 1e-4);
    }

    /** True when the copter engine may start: it needs its whole start cost in the tank. */
    public static boolean canStart(int fuel, int startCost)
    {
        return startCost <= 0 || fuel >= startCost;
    }

    /** Steam the jetpack spends when its thrust starts: the ignition cost, or what is left when there is less. */
    public static int ignitionSteam(int steam, int cost)
    {
        return Math.max(0, Math.min(steam, cost));
    }

    /** Whole ticks a full tank lasts at a cost of {@code perTick}; for the balance notes and tests. */
    public static int ticksPerTank(int capacity, double perTick)
    {
        return perTick <= 0 ? Integer.MAX_VALUE : (int) (capacity / perTick);
    }
}
