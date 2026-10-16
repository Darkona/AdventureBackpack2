package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** A running jump in the Piston Boots carries more speed forward, up to a cap, and never slows the player down. */
class PistonLeapTest {
    private static final double EPS = 1e-9;

    @Test
    void leapsOnlyWhenOnAndRunningUnlessRunningIsNotRequired() {
        assertTrue(PistonLeap.leaps(true, true, true));
        assertFalse(PistonLeap.leaps(true, false, true));
        assertTrue(PistonLeap.leaps(true, false, false));
        assertFalse(PistonLeap.leaps(false, true, true));
        assertFalse(PistonLeap.leaps(false, true, false));
    }

    @Test
    void boostsTheHorizontalSpeedBelowTheCap() {
        assertEquals(1.4, PistonLeap.scale(0.3, 0.0, 1.4, 0.7), EPS);
        assertEquals(1.4, PistonLeap.scale(-0.2, 0.2, 1.4, 0.7), EPS, "the direction does not matter");
    }

    @Test
    void stopsAtTheCap() {
        double scale = PistonLeap.scale(0.0, 0.6, 1.4, 0.7);
        assertEquals(0.7, 0.6 * scale, EPS);
    }

    @Test
    void neverSlowsAPlayerAlreadyPastTheCap() {
        assertEquals(1.0, PistonLeap.scale(0.9, 0.0, 1.4, 0.7), EPS);
        assertEquals(1.0, PistonLeap.scale(0.7, 0.0, 1.4, 0.7), EPS);
    }

    @Test
    void aStandingJumpOrNoBoostChangesNothing() {
        assertEquals(1.0, PistonLeap.scale(0.0, 0.0, 1.4, 0.7), EPS);
        assertEquals(1.0, PistonLeap.scale(1.0E-4, 0.0, 1.4, 0.7), EPS);
        assertEquals(1.0, PistonLeap.scale(0.3, 0.0, 1.0, 0.7), EPS);
    }

    @Test
    void aSprintJumpGoesFartherThanVanilla() {
        // Vanilla sprint on flat ground leaves about 0.156 blocks/tick of motion; the jump adds 0.2 forward.
        double vanilla = 0.156 + 0.2;
        double leap = vanilla * PistonLeap.scale(0.0, vanilla, 1.4, 0.7);
        assertTrue(leap > vanilla * 1.3 && leap <= 0.7, "leap " + leap);
    }
}
