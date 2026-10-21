package com.darkona.adventurebackpack.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RopeRulesTest
{
    @Test
    void attachFollowsTheClickedFace()
    {
        assertEquals(RopeRules.HANGING, RopeRules.attachMeta(0));
        assertEquals(RopeRules.NONE, RopeRules.attachMeta(1));
        for (int side = 2; side <= 5; side++)
        {
            int meta = RopeRules.attachMeta(side);
            assertEquals(-RopeRules.startDx(side), RopeRules.wallDx(meta), "side " + side);
            assertEquals(-RopeRules.startDz(side), RopeRules.wallDz(meta), "side " + side);
        }
        assertEquals(-1, RopeRules.startDy(0));
        assertEquals(0, RopeRules.startDy(3));
    }

    @Test
    void supportComesFromTheRopeAboveTheWallOrTheCeiling()
    {
        assertTrue(RopeRules.supported(RopeRules.HANGING, false, true, false));
        assertFalse(RopeRules.supported(RopeRules.HANGING, false, false, true));
        assertTrue(RopeRules.supported(0, false, false, true));
        assertFalse(RopeRules.supported(0, false, true, false));
        assertTrue(RopeRules.supported(2, true, false, false));
        assertTrue(RopeRules.supported(RopeRules.HANGING, true, false, false));
    }

    @Test
    void placesUntilSolidOrMaxLengthAndOnlyWhatIsAvailable()
    {
        assertEquals(1, RopeRules.segmentsToPlace(0, 5, 3, false));
        assertEquals(0, RopeRules.segmentsToPlace(4, 0, 3, false));
        assertEquals(0, RopeRules.segmentsToPlace(RopeRules.MAX_LENGTH, 5, 3, false));
        assertEquals(0, RopeRules.segmentsToPlace(2, 5, 0, false));
        assertEquals(1, RopeRules.segmentsToPlace(2, 5, 0, true));
    }

    @Test
    void creativeConsumesNothing()
    {
        assertEquals(0, RopeRules.consumed(3, true));
        assertEquals(3, RopeRules.consumed(3, false));
    }

    @Test
    void withoutBackpackNothingGoesToTheSlot()
    {
        assertEquals(0, RopeRules.toSlot(false, 0, 7));
        assertEquals(7, RopeRules.toSlot(true, 0, 7));
        assertEquals(5, RopeRules.toSlot(true, RopeRules.SLOT_LIMIT - 5, 9));
        assertEquals(0, RopeRules.toSlot(true, RopeRules.SLOT_LIMIT, 9));
        assertEquals(1, RopeRules.segmentsToPlace(0, 3, 1, false));
    }
}
