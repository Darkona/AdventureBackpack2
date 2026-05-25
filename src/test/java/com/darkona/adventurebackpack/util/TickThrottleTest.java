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

    @Test
    void everyFiresOncePerInterval() {
        int hits = 0;
        for (long t = 1000; t < 1012; t++) if (TickThrottle.every(t, 7, 3)) hits++;
        assertTrue(hits == 4, "hits " + hits);
    }

    @Test
    void everyIsShiftedByTheEntityId() {
        assertTrue(TickThrottle.every(9, 0, 3));
        assertFalse(TickThrottle.every(9, 1, 3));
        assertTrue(TickThrottle.every(8, 1, 3));
    }

    @Test
    void everyWithIntervalOneOrNegativeIdsIsSafe() {
        assertTrue(TickThrottle.every(12345, 99, 1));
        assertTrue(TickThrottle.every(0, -3, 3));
        assertFalse(TickThrottle.every(0, -2, 3));
    }
}
