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
    void copterLiftFadesAboveTheCeilingWithoutWrapping()
    {
        assertEquals(0.08, Flight.liftSpeed(110, 0.0, 0.18, 100), 1e-9);
        // The old posY % 100 restarted the lift at 200; the fade now keeps going down.
        assertTrue(Flight.liftSpeed(205, 0.0, 0.18, 100) < 0);
    }

    @Test
    void jetpackAddsThrustUntilTheTopSpeed()
    {
        assertEquals(0.1, Flight.thrustSpeed(64, 0.0, 0.1, 0.32, 100), 1e-9);
        assertEquals(0.5, Flight.thrustSpeed(64, 0.5, 0.1, 0.32, 100), 1e-9);
    }

    @Test
    void jetpackThrustFadesAboveTheCeiling()
    {
        assertEquals(0.22, Flight.thrustSpeed(110, 0.0, 0.1, 0.32, 100), 1e-9);
    }
}
