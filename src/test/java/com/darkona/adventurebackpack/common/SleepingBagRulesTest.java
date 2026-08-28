package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SleepingBagRulesTest
{
    /**
     * What ServerActions.canDeploySleepingBag does for a backpack at (0, 0) with each facing: the foot's offset and the
     * bag's metadata. The backpack must be found again one step behind that foot.
     */
    private static final int[][] DEPLOY = {
            // backpack meta, foot dx, foot dz, bag meta
            {0, 0, -1, 2},
            {1, 1, 0, 3},
            {2, 0, 1, 0},
            {3, -1, 0, 1},
    };

    @Test
    void theBackpackIsOneStepBehindTheFootOfItsBag()
    {
        for (int[] d : DEPLOY)
        {
            int bagMeta = d[3];
            assertEquals(0, SleepingBagRules.backpackX(d[1], bagMeta), "x for backpack meta " + d[0]);
            assertEquals(0, SleepingBagRules.backpackZ(d[2], bagMeta), "z for backpack meta " + d[0]);
            // the head lies on the far side of the foot, away from the backpack
            assertEquals(2 * d[1], d[1] + SleepingBagRules.headDx(bagMeta));
            assertEquals(2 * d[2], d[2] + SleepingBagRules.headDz(bagMeta));
        }
    }

    @Test
    void theFootIsFoundFromEitherHalfOccupiedOrNot()
    {
        for (int dir = 0; dir < 4; dir++)
        {
            int headX = 10 + SleepingBagRules.headDx(dir);
            int headZ = 20 + SleepingBagRules.headDz(dir);
            for (int occupied : new int[]{0, 4})
            {
                assertEquals(10, SleepingBagRules.footX(10, dir | occupied));
                assertEquals(20, SleepingBagRules.footZ(20, dir | occupied));
                assertEquals(10, SleepingBagRules.footX(headX, dir | 8 | occupied));
                assertEquals(20, SleepingBagRules.footZ(headZ, dir | 8 | occupied));
            }
        }
    }

    @Test
    void onlyTheFootOfABagOfItsOwnDropsTheItem()
    {
        assertTrue(SleepingBagRules.dropsItem(1, false));
        assertTrue(SleepingBagRules.dropsItem(1 | 4, false), "occupied foot");
        assertFalse(SleepingBagRules.dropsItem(1 | 8, false), "head");
        assertFalse(SleepingBagRules.dropsItem(1, true), "a backpack's bag folds back into it");
    }

    @Test
    void thePlacedBagPointsAwayFromThePlayer()
    {
        // yaw 0 looks south (+z): the head goes one block further south, like a vanilla bed
        assertEquals(0, SleepingBagRules.directionFromYaw(0f));
        assertEquals(1, SleepingBagRules.headDz(0));
        assertEquals(1, SleepingBagRules.directionFromYaw(90f));
        assertEquals(-1, SleepingBagRules.headDx(1));
        assertEquals(2, SleepingBagRules.directionFromYaw(-180f));
        assertEquals(3, SleepingBagRules.directionFromYaw(-90f));
        assertEquals(0, SleepingBagRules.directionFromYaw(360f + 30f));
    }
}
