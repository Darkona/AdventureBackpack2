package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JetpackHeatTest {
    @Test
    void losesOneDegreeAboveAmbient() {
        assertEquals(99, JetpackHeat.coolDown(100, 25));
    }

    @Test
    void stopsAtAmbient() {
        assertEquals(25, JetpackHeat.coolDown(26, 25));
        assertEquals(25, JetpackHeat.coolDown(25, 25));
    }

    @Test
    void doesNotDropBelowWhatItHas() {
        assertEquals(10, JetpackHeat.coolDown(10, 40));
    }

    /** Ticks to go from {@code from} to {@code to} degrees with the firebox lit, at a heating speed. */
    private static int heatUpTicks(int from, int to, double speed) {
        int ticks = 0;
        for (int t = from; t < to; t++) ticks += JetpackHeat.scaled(JetpackHeat.heatingTicks(t), speed);
        return ticks;
    }

    /** Ticks to cool from {@code from} down to {@code to} degrees without fire, at a cooling speed. */
    private static int coolTicks(int from, int to, double speed) {
        int ticks = 0;
        for (int t = from; t > to; t--) ticks += JetpackHeat.scaled(JetpackHeat.coolingTicks(t), speed);
        return ticks;
    }

    @Test
    void builtInCurveBoilsInAboutAMinute() {
        assertEquals(1250, heatUpTicks(25, 100, 1.0));
        assertEquals(650, heatUpTicks(25, 100, 2.0)); // 20/2 = 10 and 15/2 = 7.5, rounded to 8
    }

    @Test
    void defaultCoolingSpeedEmptiesTheHeatFaster() {
        assertEquals(6000, coolTicks(200, 100, 1.0));  // 5 minutes, the old curve
        assertEquals(4000, coolTicks(200, 100, 1.5));  // 3 min 20 s, the new default
    }

    @Test
    void scaledStepIsNeverZero() {
        assertEquals(1, JetpackHeat.scaled(5, 10.0));
        assertEquals(5, JetpackHeat.scaled(5, 0));
    }

    @Test
    void thrustBurnsFuelFaster() {
        assertEquals(1, JetpackHeat.burnPerTick(false, 3));
        assertEquals(3, JetpackHeat.burnPerTick(true, 3));
        assertEquals(1, JetpackHeat.burnPerTick(true, 0));
        // one coal (1600 ticks) lasts 80 s idle and about 27 s of thrust
        assertEquals(533, 1600 / JetpackHeat.burnPerTick(true, 3));
    }

    @Test
    void withoutFireALongFlightCoolsBelowBoiling() {
        // from 200 with the fire out, thrust loses a degree every 10 ticks on top of normal cooling (speed 1.5)
        int temperature = 200;
        int ticks = 0;
        while (temperature >= 100 && ticks < 10_000) {
            ticks++;
            if (ticks % JetpackHeat.scaled(JetpackHeat.coolingTicks(temperature), 1.5) == 0) temperature = JetpackHeat.coolDown(temperature, 25);
            if (ticks % 10 == 0) temperature = JetpackHeat.coolDown(temperature, 25);
        }
        assertTrue(ticks < 1200, "below boiling after " + ticks + " ticks of thrust");
    }
}
