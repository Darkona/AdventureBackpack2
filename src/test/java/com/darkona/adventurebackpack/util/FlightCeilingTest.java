package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FlightCeilingTest {
    @Test
    void fullLiftAtTheStart() {
        assertEquals(0.18, FlightCeiling.motionAbove(100, 0.18), 1e-9);
    }

    @Test
    void liftReachesZeroEighteenBlocksAbove() {
        assertEquals(0.0, FlightCeiling.motionAbove(118, 0.18), 1e-9);
    }

    @Test
    void noFreshLiftAboveTwoHundred() {
        // the old posY % 100 gave full lift again at y=200 and above
        assertTrue(FlightCeiling.motionAbove(205, 0.18) < 0);
        assertTrue(FlightCeiling.motionAbove(210, 0.32) < 0);
    }

    @Test
    void descentIsClamped() {
        assertEquals(FlightCeiling.MAX_DESCENT, FlightCeiling.motionAbove(400, 0.32), 1e-9);
    }
}
