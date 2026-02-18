package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TickThrottleTest {
    @Test
    void firstCallIsDue() {
        assertTrue(TickThrottle.due(Integer.MIN_VALUE, 0, 5));
    }

    @Test
    void notDueInsideTheInterval() {
        assertFalse(TickThrottle.due(100, 100, 5));
        assertFalse(TickThrottle.due(100, 104, 5));
    }

    @Test
    void dueAfterTheInterval() {
        assertTrue(TickThrottle.due(100, 105, 5));
    }

    @Test
    void dueWhenTheClockGoesBackwards() {
        assertTrue(TickThrottle.due(5000, 3, 5));
    }
}
