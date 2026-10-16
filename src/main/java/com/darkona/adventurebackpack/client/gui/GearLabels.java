package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.util.Utils;

final class GearLabels
{
    private GearLabels() {}

    static String tank(String fluidName, int amount)
    {
        return (amount > 0 ? Utils.capitalize(fluidName) : "None") + "-" + amount;
    }

    static String temperature(int celsius)
    {
        return celsius + " C";
    }

    static String fuelName(String fluidName)
    {
        return fluidName != null ? Utils.capitalize(fluidName) : "None";
    }

    static String fuelAmount(boolean hasFluid, int amount)
    {
        return hasFluid ? Integer.toString(amount) : "0";
    }

    static String consumption(boolean isFuel, float multiplier)
    {
        return "Consumption: " + (isFuel ? Float.toString(multiplier) : "0");
    }
}
