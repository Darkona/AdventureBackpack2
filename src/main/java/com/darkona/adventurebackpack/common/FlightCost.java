package com.darkona.adventurebackpack.common;

public final class FlightCost
{
    private FlightCost() {}

    public static int copterPerTick(int base, boolean hovering, int hoverExtra, boolean climbing, int climbExtra)
    {
        int cost = base;
        if (hovering) cost += hoverExtra;
        if (climbing) cost += climbExtra;
        return Math.max(0, cost);
    }

    public static int copterFuelPerInterval(int perTick, int interval, double multiplier)
    {
        if (perTick <= 0 || interval <= 0 || multiplier <= 0) return 0;
        return (int) Math.ceil(perTick * (double) interval * multiplier - 1e-4);
    }

    public static boolean canStart(int fuel, int startCost)
    {
        return startCost <= 0 || fuel >= startCost;
    }

    public static int ignitionSteam(int steam, int cost)
    {
        return Math.max(0, Math.min(steam, cost));
    }

    public static int ticksPerTank(int capacity, double perTick)
    {
        return perTick <= 0 ? Integer.MAX_VALUE : (int) (capacity / perTick);
    }
}
