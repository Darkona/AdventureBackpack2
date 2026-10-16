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

    // canInteractWith of the four gear windows (copter pack, jetpack, scuba gear, flamethrower pack)

    @Test
    void wornWindowStaysOpenWhileThatStackIsWorn() {
        Object gear = new Object();
        assertTrue(HeldContainerGuard.stillOpen(true, gear, gear, null));
    }

    @Test
    void wornWindowClosesWhenUnequippedOrReplaced() {
        Object gear = new Object();
        assertFalse(HeldContainerGuard.stillOpen(true, gear, null, null));
        assertFalse(HeldContainerGuard.stillOpen(true, gear, new Object(), null));
        // holding the same stack in the hand does not count for a window opened on the worn one
        assertFalse(HeldContainerGuard.stillOpen(true, gear, null, gear));
    }

    @Test
    void heldWindowClosesWhenDroppedOrTheHotbarSlotChanges() {
        Object gear = new Object();
        assertTrue(HeldContainerGuard.stillOpen(false, gear, null, gear));
        assertFalse(HeldContainerGuard.stillOpen(false, gear, null, null));
        assertFalse(HeldContainerGuard.stillOpen(false, gear, null, new Object()));
        assertFalse(HeldContainerGuard.stillOpen(false, gear, gear, null));
    }

    @Test
    void windowWithoutAStackIsClosed() {
        assertFalse(HeldContainerGuard.stillOpen(true, null, null, null));
        assertFalse(HeldContainerGuard.stillOpen(false, null, null, null));
    }
}
