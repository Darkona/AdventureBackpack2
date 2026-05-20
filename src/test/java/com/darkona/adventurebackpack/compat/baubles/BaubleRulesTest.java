package com.darkona.adventurebackpack.compat.baubles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.compat.baubles.BaubleRules.Action;
import org.junit.jupiter.api.Test;

/** A backpack in a Baubles slot is worn at most once and never next to another worn wearable. */
class BaubleRulesTest {
    @Test
    void fitsTheSlotOnlyWhenNothingIsWorn() {
        assertTrue(BaubleRules.canEquip(true, true, false));
        assertFalse(BaubleRules.canEquip(true, true, true));
    }

    @Test
    void doesNotFitWhenCompatIsOffOrNotAPlayer() {
        assertFalse(BaubleRules.canEquip(false, true, false));
        assertFalse(BaubleRules.canEquip(true, false, false));
    }

    // tick(compatEnabled, linkedToThis, wearsSomething, otherLinkStale)

    @Test
    void linksWhenNothingIsWorn() {
        assertEquals(Action.LINK, BaubleRules.tick(true, false, false, false));
    }

    @Test
    void staysLinked() {
        assertEquals(Action.NONE, BaubleRules.tick(true, true, true, false));
    }

    @Test
    void ejectsWhenAnotherWearableIsWorn() {
        assertEquals(Action.EJECT, BaubleRules.tick(true, false, true, false));
    }

    @Test
    void replacesALinkToAStackNoSlotHoldsAnyMore() {
        assertEquals(Action.LINK, BaubleRules.tick(true, false, true, true));
    }

    @Test
    void compatOffUnlinksAndLeavesTheStackInert() {
        assertEquals(Action.UNLINK, BaubleRules.tick(false, true, true, false));
        assertEquals(Action.NONE, BaubleRules.tick(false, false, false, false));
        assertEquals(Action.NONE, BaubleRules.tick(false, false, true, false));
    }
}
