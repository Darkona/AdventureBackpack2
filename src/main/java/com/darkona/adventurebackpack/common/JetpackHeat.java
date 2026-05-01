package com.darkona.adventurebackpack.common;

/**
 * Cooling of the steam jetpack boiler when the firebox is out: it loses one degree per step down to the ambient
 * temperature of the biome. The old code dropped it to 0 as soon as it reached the ambient value.
 */
public final class JetpackHeat
{
    private JetpackHeat() {}

    public static int coolDown(int temperature, int ambient)
    {
        return temperature > ambient ? temperature - 1 : temperature;
    }
}
