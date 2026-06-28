package com.darkona.adventurebackpack.common;

/**
 * Heat of the steam jetpack boiler, as pure functions. The firebox heats it one degree at a time, faster when it is
 * already hot; without fire it cools one degree at a time down to the ambient temperature of the biome (the old code
 * dropped it to 0 as soon as it reached the ambient value). Thrusting burns the fuel faster and takes heat from the
 * boiler, so a long flight needs fuel and not only water ("flying is expensive").
 */
public final class JetpackHeat
{
    private JetpackHeat() {}

    public static int coolDown(int temperature, int ambient)
    {
        return temperature > ambient ? temperature - 1 : temperature;
    }

    /** Ticks per degree gained while the firebox burns, by temperature band (the built-in curve). */
    public static int heatingTicks(int temperature)
    {
        if (temperature < 50) return 20;
        if (temperature < 100) return 15;
        if (temperature < 150) return 10;
        return 5;
    }

    /** Ticks per degree lost without fire, by temperature band (the built-in curve). */
    public static int coolingTicks(int temperature)
    {
        if (temperature > 150) return 40;
        if (temperature > 100) return 80;
        if (temperature > 50) return 120;
        return 5;
    }

    /** A curve step scaled by a speed from the config: speed 2 takes half the ticks. Never less than one tick. */
    public static int scaled(int ticks, double speed)
    {
        if (speed <= 0) return ticks;
        return Math.max(1, (int) Math.round(ticks / speed));
    }

    /** Firebox burn time used this tick: {@code thrustBurn} while thrusting, 1 otherwise. */
    public static int burnPerTick(boolean thrusting, int thrustBurn)
    {
        return thrusting ? Math.max(1, thrustBurn) : 1;
    }
}
