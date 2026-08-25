package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class GrappleLogicTest {
    private static final double EPS = 1e-9;

    @Test
    void pullPointsAtTheAnchorAtTheGivenSpeed() {
        double[] out = new double[3];
        double distance = GrappleLogic.pull(0, 0, 0, 0, 3, 4, 0.5, out);
        assertEquals(5.0, distance, EPS);
        assertEquals(0.0, out[0], EPS);
        assertEquals(0.3, out[1], EPS);
        assertEquals(0.4, out[2], EPS);
    }

    @Test
    void pullNeverOvershootsTheAnchor() {
        double[] out = new double[3];
        GrappleLogic.pull(0, 0, 0, 0.2, 0, 0, 3.0, out);
        assertEquals(0.2, out[0], EPS);
        double distance = GrappleLogic.pull(1, 1, 1, 1, 1, 1, 1.0, out);
        assertEquals(0.0, distance, EPS);
        assertEquals(0.0, out[0] + out[1] + out[2], EPS);
    }

    @Test
    void sneakingLetsGoFirst() {
        assertEquals(GrappleLogic.SNEAKING, GrappleLogic.release(true, 0.5, 1.5, 999, 60, 32));
    }

    @Test
    void arrivingTimingOutAndBeingTornAwayLetGo() {
        assertEquals(GrappleLogic.ARRIVED, GrappleLogic.release(false, 1.0, 1.5, 5, 60, 32));
        assertEquals(GrappleLogic.TIMED_OUT, GrappleLogic.release(false, 10, 1.5, 61, 60, 32));
        assertEquals(GrappleLogic.TOO_FAR, GrappleLogic.release(false, 32 + GrappleLogic.SLACK + 1, 1.5, 5, 60, 32));
        assertEquals(GrappleLogic.HOLD, GrappleLogic.release(false, 10, 1.5, 60, 60, 32));
    }

    @Test
    void aFlyingHookComesBackPastItsRange() {
        assertFalse(GrappleLogic.outOfRange(31.9 * 31.9, 32));
        assertTrue(GrappleLogic.outOfRange(32.1 * 32.1, 32));
    }

    @Test
    void lookFollowsMinecraftsYawAndPitch() {
        double[] out = new double[3];
        GrappleLogic.look(0, 0, out);
        assertEquals(0, out[0], EPS);
        assertEquals(0, out[1], EPS);
        assertEquals(1, out[2], EPS);
        GrappleLogic.look(90, 0, out);
        assertEquals(-1, out[0], EPS);
        assertEquals(0, out[2], EPS);
        GrappleLogic.look(0, -90, out);
        assertEquals(1, out[1], EPS);
        GrappleLogic.look(37, 21, out);
        assertEquals(1.0, out[0] * out[0] + out[1] * out[1] + out[2] * out[2], EPS);
    }
}
