package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FlightTest
{
    @Test
    void copterClimbsAtLeastAtLiftSpeedBelowTheCeiling()
    {
        assertEquals(0.18, Flight.liftSpeed(64, 0.0, 0.18, 100), 1e-9);
        assertEquals(0.5, Flight.liftSpeed(64, 0.5, 0.18, 100), 1e-9);
    }

    @Test
    void fullLiftAtTheCeiling()
    {
        assertEquals(0.18, Flight.motionAbove(100, 0.18, 100), 1e-9);
        assertEquals(0.18, Flight.liftSpeed(100, 0.0, 0.18, 100), 1e-9);
    }

    @Test
    void liftReachesZeroEighteenBlocksAbove()
    {
        assertEquals(0.0, Flight.motionAbove(118, 0.18, 100), 1e-9);
        assertEquals(0.08, Flight.liftSpeed(110, 0.0, 0.18, 100), 1e-9);
    }

    @Test
    void noFreshLiftAboveTwoHundred()
    {
        // the old posY % 100 gave full lift again at y=200 and above
        assertTrue(Flight.liftSpeed(205, 0.0, 0.18, 100) < 0);
        assertTrue(Flight.thrustSpeed(210, 0.0, 0.1, 0.32, 2.0, 100) < 0);
    }

    @Test
    void descentIsClamped()
    {
        assertEquals(Flight.MAX_DESCENT, Flight.motionAbove(400, 0.32, 100), 1e-9);
    }

    @Test
    void ceilingFollowsTheConfiguredAltitude()
    {
        assertEquals(0.18, Flight.liftSpeed(150, 0.0, 0.18, 200), 1e-9);
        assertEquals(0.08, Flight.liftSpeed(210, 0.0, 0.18, 200), 1e-9);
    }

    @Test
    void jetpackAddsThrustUntilTheTopSpeed()
    {
        assertEquals(0.1, Flight.thrustSpeed(64, 0.0, 0.1, 0.32, 2.0, 100), 1e-9);
        assertEquals(0.32, Flight.thrustSpeed(64, 0.3, 0.1, 0.32, 2.0, 100), 1e-9);
        assertEquals(0.5, Flight.thrustSpeed(64, 0.5, 0.1, 0.32, 2.0, 100), 1e-9);
    }

    @Test
    void jetpackBrakesAFallHarder()
    {
        assertEquals(-0.3 + 0.28, Flight.thrustSpeed(64, -0.3, 0.14, 0.4, 2.0, 100), 1e-9);
        assertEquals(-0.3 + 0.14, Flight.thrustSpeed(64, -0.3, 0.14, 0.4, 1.0, 100), 1e-9);
    }

    @Test
    void jetpackThrustFadesAboveTheCeiling()
    {
        assertEquals(0.22, Flight.thrustSpeed(110, 0.0, 0.1, 0.32, 2.0, 100), 1e-9);
    }
}
