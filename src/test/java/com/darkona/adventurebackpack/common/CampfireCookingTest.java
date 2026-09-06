package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Cooking on the campfire: what it takes, how much, when an item is done and where it goes. */
class CampfireCookingTest {
    @Test
    void cooksFoodAndOtherRecipesOnlyWhenAllowed() {
        assertTrue(CampfireCooking.canCook(true, true, true, true), "raw beef");
        assertFalse(CampfireCooking.canCook(true, true, false, true), "iron ore with onlyFood");
        assertTrue(CampfireCooking.canCook(true, true, false, false), "iron ore without onlyFood");
        assertFalse(CampfireCooking.canCook(true, false, false, false), "no furnace recipe");
        assertFalse(CampfireCooking.canCook(false, true, true, true), "cooking turned off");
    }

    @Test
    void anIdleFireTakesUpToItsCapacity() {
        assertEquals(8, CampfireCooking.take(64, 0, false, 8));
        assertEquals(3, CampfireCooking.take(3, 0, false, 8));
        assertEquals(0, CampfireCooking.take(0, 0, false, 8));
    }

    @Test
    void aBusyFireTopsUpTheSameItemOnly() {
        assertEquals(5, CampfireCooking.take(64, 3, true, 8));
        assertEquals(0, CampfireCooking.take(64, 8, true, 8), "full");
        assertEquals(0, CampfireCooking.take(64, 3, false, 8), "another item waits");
        assertEquals(0, CampfireCooking.take(64, 9, true, 8), "a smaller capacity after a config change takes nothing");
    }

    @Test
    void anItemIsDoneWhenTheClockReachesItsTime() {
        assertFalse(CampfireCooking.ready(99, 100));
        assertTrue(CampfireCooking.ready(100, 100));
        assertTrue(CampfireCooking.ready(5000, 100), "after a chunk reload");
    }

    @Test
    void anEarlyUpdateWaitsForTheTimeLeft() {
        assertEquals(40, CampfireCooking.delay(60, 100));
        assertEquals(1, CampfireCooking.delay(100, 100));
        assertEquals(1, CampfireCooking.delay(200, 100));
        assertEquals(Integer.MAX_VALUE, CampfireCooking.delay(0, Long.MAX_VALUE));
    }

    @Test
    void cookedItemsGoToAPlayerInRange() {
        assertTrue(CampfireCooking.inReturnRange(16.0, 4.0));
        assertFalse(CampfireCooking.inReturnRange(16.01, 4.0));
        assertFalse(CampfireCooking.inReturnRange(0.0, 0.0), "0 always drops");
    }
}
