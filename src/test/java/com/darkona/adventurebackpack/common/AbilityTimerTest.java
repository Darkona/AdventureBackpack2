package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.util.TickThrottle;
import org.junit.jupiter.api.Test;

class AbilityTimerTest {
    @Test
    void notDueBeforeTheMark() {
        assertFalse(AbilityTimer.due(1000, 1001, 1200));
        assertFalse(AbilityTimer.due(1000, 2200, 1200));
    }

    @Test
    void dueAtAndAfterTheMark() {
        assertTrue(AbilityTimer.due(1000, 1000, 1200));
        assertTrue(AbilityTimer.due(5000, 1000, 1200));
    }

    @Test
    void aMarkFurtherThanTheLongestWaitIsDue() {
        // the backpack comes from a save whose clock was far ahead
        assertTrue(AbilityTimer.due(1000, 1_000_000, 1200));
        assertFalse(AbilityTimer.due(1000, 2200, 1200));
        assertTrue(AbilityTimer.due(1000, 2201, 1200));
    }

    @Test
    void runCountsDownOnWorldTime() {
        assertEquals(3000, AbilityTimer.remaining(1000, 4000, 3000));
        assertEquals(1, AbilityTimer.remaining(3999, 4000, 3000));
        assertEquals(0, AbilityTimer.remaining(4000, 4000, 3000));
        assertEquals(0, AbilityTimer.remaining(9000, 4000, 3000));
    }

    @Test
    void noRunWithoutAMark() {
        assertEquals(0, AbilityTimer.remaining(1000, 0, 3000));
    }

    @Test
    void aRunLongerThanItsLengthIsOver() {
        assertEquals(0, AbilityTimer.remaining(1000, 50_000, 3000));
    }

    @Test
    void cooldownFiresOncePerWaitWhenCheckedEverySecond() {
        // Pig/Chicken: checked once a second, fire, then wait 200 s: exactly one firing in 200 s of ticks
        long next = 0;
        int fired = 0;
        int wait = 200 * 20;
        for (long now = 1; now <= wait; now++) {
            if (!TickThrottle.every(now, 0, AbilityTimer.SECOND_CHECK_TICKS)) continue;
            if (AbilityTimer.due(now, next, wait)) {
                fired++;
                next = now + wait;
            }
        }
        assertEquals(1, fired);
    }

    @Test
    void intervalAbilityKeepsItsRate() {
        // Cactus, Melon, Wolf, Ocelot, Slime: one firing every interval ticks, like the old countdown
        int interval = 5;
        int fired = 0;
        for (long now = 0; now < 100; now++) {
            if (TickThrottle.every(now, 7, interval)) fired++;
        }
        assertEquals(20, fired);
    }
}
