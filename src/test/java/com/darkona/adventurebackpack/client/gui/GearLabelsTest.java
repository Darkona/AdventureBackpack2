package com.darkona.adventurebackpack.client.gui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.darkona.adventurebackpack.util.Utils;

/** The jetpack and copter window texts, built on change now (audit P13), are the ones the windows drew per frame. */
class GearLabelsTest
{
    /** The old per-frame expression of GuiSteamJetpack, with the fluid name already looked up. */
    private static String oldTank(String name, int amount)
    {
        return ((amount > 0) ? Utils.capitalize(name) : "None") + "-" + amount;
    }

    @Test
    void jetpackTexts()
    {
        assertEquals(oldTank("water", 1200), GearLabels.tank("water", 1200));
        assertEquals("Water-1200", GearLabels.tank("water", 1200));
        assertEquals("None-0", GearLabels.tank(null, 0));
        assertEquals(oldTank("steam", 1), GearLabels.tank("steam", 1));
        assertEquals("35 C", GearLabels.temperature(35));
        assertEquals("-2 C", GearLabels.temperature(-2));
    }

    @Test
    void copterTexts()
    {
        assertEquals("Biofuel", GearLabels.fuelName("biofuel"));
        assertEquals("None", GearLabels.fuelName(null));
        assertEquals("" + 4321, GearLabels.fuelAmount(true, 4321));
        assertEquals("0", GearLabels.fuelAmount(false, 0));
        assertEquals("0", GearLabels.fuelAmount(true, 0));
        assertEquals("Consumption: " + Float.toString(1.5f), GearLabels.consumption(true, 1.5f));
        assertEquals("Consumption: 0", GearLabels.consumption(false, 0f));
    }
}
