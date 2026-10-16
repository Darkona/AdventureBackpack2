package com.darkona.adventurebackpack.common;

public final class JetpackHeat
{
    private JetpackHeat() {}

    public static int coolDown(int temperature, int ambient)
    {
        return temperature > ambient ? temperature - 1 : temperature;
    }

    public static int heatingTicks(int temperature)
    {
        if (temperature < 50) return 20;
        if (temperature < 100) return 15;
        if (temperature < 150) return 10;
        return 5;
    }

    public static int coolingTicks(int temperature)
    {
        if (temperature > 150) return 40;
        if (temperature > 100) return 80;
        if (temperature > 50) return 120;
        return 5;
    }

    public static int scaled(int ticks, double speed)
    {
        if (speed <= 0) return ticks;
        return Math.max(1, (int) Math.round(ticks / speed));
    }

    public static int burnPerTick(boolean thrusting, int thrustBurn)
    {
        return thrusting ? Math.max(1, thrustBurn) : 1;
    }
}
