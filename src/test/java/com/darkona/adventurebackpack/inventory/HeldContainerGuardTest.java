package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The open held backpack, copter pack or jetpack cannot be moved by a click or a number key. */
class HeldContainerGuardTest {
    @Test
    void numberKeyOnTheHeldSlotIsRefused() {
        assertTrue(HeldContainerGuard.refuses(true, false, HeldContainerGuard.HOTBAR_SWAP, 4, 4));
    }

    @Test
    void numberKeyOnAnotherHotbarSlotIsAllowed() {
        assertFalse(HeldContainerGuard.refuses(true, false, HeldContainerGuard.HOTBAR_SWAP, 3, 4));
    }

    @Test
    void clickingTheHeldStackIsRefused() {
        assertTrue(HeldContainerGuard.refuses(true, true, 0, 0, 4));
    }

    @Test
    void wornContainersAreNotRestricted() {
        assertFalse(HeldContainerGuard.refuses(false, true, HeldContainerGuard.HOTBAR_SWAP, 4, 4));
    }

    @Test
    void ordinaryClicksPass() {
        assertFalse(HeldContainerGuard.refuses(true, false, 0, 4, 4));
    }
}
