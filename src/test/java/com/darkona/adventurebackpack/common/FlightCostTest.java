package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

class FlightCostTest {
    // defaults of the copterpack and steamjetpack keys
    private static final int COPTER_TANK = 6000;
    private static final int BASE = 2;
    private static final int HOVER = 2;
    private static final int CLIMB = 2;
    private static final int INTERVAL = 3;
    private static final int STEAM_TANK = 12000;
    private static final int WATER_TANK = 6000;
    private static final int STEAM_PER_TICK = 20;
    private static final int STEAM_PER_WATER = 4;

    @Test
    void defaultsInTheConfigFieldsMatchTheseNumbers() {
        assertEquals(BASE, ConfigHandler.COPTER_FUEL_PER_TICK);
        assertEquals(HOVER, ConfigHandler.COPTER_HOVER_EXTRA_FUEL);
        assertEquals(CLIMB, ConfigHandler.COPTER_CLIMB_EXTRA_FUEL);
        assertEquals(INTERVAL, ConfigHandler.COPTER_FUEL_INTERVAL_TICKS);
        assertEquals(COPTER_TANK, ConfigHandler.COPTER_TANK_CAPACITY);
        assertEquals(STEAM_PER_TICK, ConfigHandler.JETPACK_STEAM_PER_TICK);
        assertEquals(STEAM_TANK, ConfigHandler.JETPACK_STEAM_CAPACITY);
        assertEquals(WATER_TANK, ConfigHandler.JETPACK_WATER_CAPACITY);
        assertEquals(STEAM_PER_WATER, ConfigHandler.JETPACK_STEAM_PER_WATER);
    }

    @Test
    void copterCostAddsHoverAndClimb() {
        assertEquals(2, FlightCost.copterPerTick(BASE, false, HOVER, false, CLIMB));
        assertEquals(4, FlightCost.copterPerTick(BASE, true, HOVER, false, CLIMB));
        assertEquals(4, FlightCost.copterPerTick(BASE, false, HOVER, true, CLIMB));
        assertEquals(6, FlightCost.copterPerTick(BASE, true, HOVER, true, CLIMB));
        assertEquals(0, FlightCost.copterPerTick(-5, false, HOVER, false, CLIMB));
    }

    @Test
    void fuelTakenOnceAnIntervalCoversEveryTickOfIt() {
        assertEquals(6, FlightCost.copterFuelPerInterval(2, 3, 1.0));
        assertEquals(9, FlightCost.copterFuelPerInterval(2, 3, 1.5));
        assertEquals(1, FlightCost.copterFuelPerInterval(1, 1, 1.0));
    }

    @Test
    void aCheapFuelIsNeverFree() {
        // the old code did floor(1 * 0.8) = 0
        assertEquals(1, FlightCost.copterFuelPerInterval(1, 1, 0.8));
        assertEquals(5, FlightCost.copterFuelPerInterval(2, 3, 0.8));
    }

    @Test
    void floatMultipliersDoNotRoundUpByAccident() {
        assertEquals(6, FlightCost.copterFuelPerInterval(2, 3, 1.0f));
        assertEquals(6, FlightCost.copterFuelPerInterval(5, 1, 1.2f)); // 1.2f * 5 = 6.0000002, still 6
        assertEquals(8, FlightCost.copterFuelPerInterval(2, 3, 1.2f)); // 7.2 rounds up
    }

    @Test
    void nothingRunsForFree() {
        assertEquals(0, FlightCost.copterFuelPerInterval(0, 3, 1.0));
        assertEquals(0, FlightCost.copterFuelPerInterval(2, 3, 0));
    }

    @Test
    void copterFullTankLastsAboutOneToTwoAndAHalfMinutes() {
        int glide = FlightCost.ticksPerTank(COPTER_TANK, FlightCost.copterPerTick(BASE, false, HOVER, false, CLIMB));
        int hover = FlightCost.ticksPerTank(COPTER_TANK, FlightCost.copterPerTick(BASE, true, HOVER, false, CLIMB));
        int climbHover = FlightCost.ticksPerTank(COPTER_TANK, FlightCost.copterPerTick(BASE, true, HOVER, true, CLIMB));
        assertEquals(3000, glide);       // 150 s
        assertEquals(1500, hover);       // 75 s
        assertEquals(1000, climbHover);  // 50 s
        // one minute of hovering takes most of the tank
        assertTrue(1200 * FlightCost.copterPerTick(BASE, true, HOVER, false, CLIMB) >= COPTER_TANK * 3 / 4);
    }

    @Test
    void engineNeedsItsStartFuel() {
        assertTrue(FlightCost.canStart(100, 100));
        assertFalse(FlightCost.canStart(99, 100));
        assertTrue(FlightCost.canStart(0, 0));
    }

    @Test
    void ignitionTakesWhatIsLeftWhenThereIsLess() {
        assertEquals(150, FlightCost.ignitionSteam(12000, 150));
        assertEquals(40, FlightCost.ignitionSteam(40, 150));
        assertEquals(0, FlightCost.ignitionSteam(0, 150));
        assertEquals(0, FlightCost.ignitionSteam(500, 0));
    }

    @Test
    void jetpackSteamTankAloneIsHalfAMinuteAndEverythingAboutNinetySeconds() {
        assertEquals(600, FlightCost.ticksPerTank(STEAM_TANK, STEAM_PER_TICK));
        int allSteam = STEAM_TANK + WATER_TANK * STEAM_PER_WATER;
        assertEquals(1800, FlightCost.ticksPerTank(allSteam, STEAM_PER_TICK));
    }
}
