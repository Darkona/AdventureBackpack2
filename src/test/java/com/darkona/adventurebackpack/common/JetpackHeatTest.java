package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
