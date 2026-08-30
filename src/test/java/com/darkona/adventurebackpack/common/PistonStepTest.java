package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The Piston Boots put the step height back when they come off, and leave other mods' step height alone. */
class PistonStepTest {
    @Test
    void sprintingInTheBootsApplies() {
        assertEquals(PistonStep.APPLY, PistonStep.action(true, true, false));
        assertEquals(PistonStep.APPLY, PistonStep.action(true, true, true));
    }

    @Test
    void takingTheBootsOffMidSprintRestores() {
        assertEquals(PistonStep.RESTORE, PistonStep.action(false, true, true));
        assertEquals(PistonStep.RESTORE, PistonStep.action(false, false, true));
    }

    @Test
    void stoppingTheSprintRestoresOnce() {
        assertEquals(PistonStep.RESTORE, PistonStep.action(true, false, true));
        assertEquals(PistonStep.NONE, PistonStep.action(true, false, false));
    }

    @Test
    void neverTouchesAStepHeightItDidNotSet() {
        assertEquals(PistonStep.NONE, PistonStep.action(false, false, false));
        assertEquals(PistonStep.NONE, PistonStep.action(false, true, false));
    }

    @Test
    void speedIsRenewedOnlyNearItsEnd() {
        assertTrue(PistonStep.needsSpeed(false, 0, 0));
        assertFalse(PistonStep.needsSpeed(true, 0, PistonStep.SPEED_TICKS));
        assertTrue(PistonStep.needsSpeed(true, 0, PistonStep.SPEED_REFRESH_BELOW - 1));
        assertFalse(PistonStep.needsSpeed(true, 2, 1), "a stronger Speed from elsewhere stays");
    }

    @Test
    void onlyAShortLevelOneSpeedLooksLikeTheBoots() {
        assertTrue(PistonStep.isBootsSpeed(0, PistonStep.SPEED_TICKS));
        assertFalse(PistonStep.isBootsSpeed(0, 3600));
        assertFalse(PistonStep.isBootsSpeed(1, 10));
    }

    @Test
    void onlyWhileSprintingDecidesWhenTheBootsStepUp() {
        assertTrue(PistonStep.stepping(true, true, true));
        assertFalse(PistonStep.stepping(true, false, true), "walking does not climb with onlyWhileSprinting (#126)");
        assertTrue(PistonStep.stepping(true, false, false), "without it the boots step up at any pace");
        assertTrue(PistonStep.stepping(true, true, false));
        assertFalse(PistonStep.stepping(false, true, true), "enabled.pistonBoots off");
        assertFalse(PistonStep.stepping(false, false, false));
    }
}
