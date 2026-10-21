package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The crossbow fed from a worn Skeleton quiver (gatling mode): which slot each shot takes from, the burst timing, the
 * write-back to the backpack, and the speed bonus of the Skeleton wearer's arrows.
 */
class QuiverFeedRulesTest
{
    private static final int NO = QuiverFeedRules.NOT_AMMO;

    @Test
    void boltsGoBeforeArrowsWhateverTheirSlot()
    {
        // crossbow.ammo.list = bolt (rank 0), arrow (rank 1); slot 2 holds something the crossbow does not fire
        int[] rank = {1, 0, NO, 0};
        int[] left = {10, 3, 64, 2};
        assertEquals(1, QuiverFeedRules.pick(rank, left, 4));
        left[1] = 0;
        assertEquals(3, QuiverFeedRules.pick(rank, left, 4), "the next bolt slot");
        left[3] = 0;
        assertEquals(0, QuiverFeedRules.pick(rank, left, 4), "arrows once the bolts are gone");
        left[0] = 0;
        assertEquals(-1, QuiverFeedRules.pick(rank, left, 4), "never the item the crossbow does not fire");
    }

    @Test
    void onTheSameRankTheFirstSlotGoesFirstAndOnlyTheQuiverSlotsCount()
    {
        int[] rank = {1, 1, 0, 0, 0, 0};
        int[] left = {5, 5, 0, 0, 0, 7};
        assertEquals(0, QuiverFeedRules.pick(rank, left, 4), "slot 5 is past the quiver's 4 slots");
        assertEquals(10, QuiverFeedRules.usable(rank, left, 4));
        assertEquals(17, QuiverFeedRules.usable(rank, left, 6));
        // Arrays shorter than the slot count are never read past their end
        assertEquals(0, QuiverFeedRules.pick(new int[]{1}, new int[]{1}, 6));
    }

    @Test
    void usableCountsOnlyAmmunition()
    {
        int[] rank = {NO, 0, 1};
        int[] left = {64, 2, -3};
        assertEquals(2, QuiverFeedRules.usable(rank, left, 3));
    }

    @Test
    void takingMovesItemsToSpentInPickOrderAndTheHookTakesItsCost()
    {
        int[] rank = {1, 0, 1};
        int[] left = {4, 1, 4};
        int[] spent = new int[3];
        // One bolt, then arrows from the first arrow slot
        assertEquals(1, QuiverFeedRules.take(rank, left, spent, 3, 1));
        assertEquals(0, QuiverFeedRules.take(rank, left, spent, 3, 3), "the last item taken came from slot 0");
        assertArrayEquals(new int[]{1, 0, 4}, left);
        assertArrayEquals(new int[]{3, 1, 0}, spent);
        // A hook that costs more than the quiver holds takes nothing
        assertEquals(-1, QuiverFeedRules.take(rank, left, spent, 3, 6));
        assertArrayEquals(new int[]{1, 0, 4}, left);
        assertEquals(-1, QuiverFeedRules.take(rank, left, spent, 3, 0), "a free shot takes nothing");
        // It crosses slots when one runs out
        assertEquals(2, QuiverFeedRules.take(rank, left, spent, 3, 2));
        assertArrayEquals(new int[]{0, 0, 3}, left);
    }

    @Test
    void theBurstFiresWhileTheTriggerIsHeldAndOnItsOwnClock()
    {
        long click = 1000;
        long heldUntil = click + 6;
        assertTrue(QuiverFeedRules.triggerHeld(click + 4, heldUntil), "the next repeated click comes at +4");
        assertTrue(QuiverFeedRules.triggerHeld(click + 6, heldUntil));
        assertFalse(QuiverFeedRules.triggerHeld(click + 7, heldUntil), "let go: the burst stops");

        // Interval 3: shots at 1000, 1003, 1006... not tied to the 4-tick click
        long next = click;
        int shots = 0;
        for (long now = click; now < click + 30; now++)
        {
            if (QuiverFeedRules.shotDue(now, next))
            {
                shots++;
                next = now + 3;
            }
        }
        assertEquals(10, shots, "10 bolts in 30 ticks, faster than the magazine's 4-tick repeat (8)");
    }

    @Test
    void spentItemsAreWrittenBackEveryFewShots()
    {
        assertFalse(QuiverFeedRules.flushDue(0, 8));
        assertFalse(QuiverFeedRules.flushDue(7, 8));
        assertTrue(QuiverFeedRules.flushDue(8, 8));
        assertTrue(QuiverFeedRules.flushDue(9, 8));
        assertTrue(QuiverFeedRules.flushDue(1, 0), "a broken value writes back every shot");
        assertFalse(QuiverFeedRules.flushDue(0, 0));
    }

    @Test
    void writingBackNeverTakesMoreThanTheSlotHolds()
    {
        assertEquals(3, QuiverFeedRules.toRemove(3, 10));
        assertEquals(10, QuiverFeedRules.toRemove(12, 10), "someone took some meanwhile: the rest is not created");
        assertEquals(0, QuiverFeedRules.toRemove(0, 10));
        assertEquals(0, QuiverFeedRules.toRemove(4, 0));
    }

    @Test
    void theSkeletonWearersArrowsLeaveFaster()
    {
        assertEquals(1.3, QuiverFeedRules.speedFactor(0.30), 1e-12);
        assertEquals(1.0, QuiverFeedRules.speedFactor(0.0), 1e-12, "0 turns it off");
        assertEquals(1.0, QuiverFeedRules.speedFactor(-0.5), 1e-12);
        assertEquals(1.0, QuiverFeedRules.speedFactor(Double.NaN), 1e-12);
        assertEquals(1.0 + QuiverFeedRules.MAX_SPEED_BONUS, QuiverFeedRules.speedFactor(50), 1e-12);
        // A fully drawn bow (3 blocks per tick) and the default bolt (3) leave at 3.9
        assertEquals(3.9, 3.0 * QuiverFeedRules.speedFactor(0.30), 1e-9);
    }

    @Test
    void theSkeletonWearerFiresThirtyPercentMoreOften()
    {
        assertEquals(1.3, QuiverFeedRules.rateFactor(0.30), 1e-12);
        assertEquals(1.0, QuiverFeedRules.rateFactor(0.0), 1e-12, "0 turns it off");
        assertEquals(1.0, QuiverFeedRules.rateFactor(Double.NaN), 1e-12);
        assertEquals(1.0 + QuiverFeedRules.MAX_RATE_BONUS, QuiverFeedRules.rateFactor(9), 1e-12);
        assertEquals(3.0 / 1.3, QuiverFeedRules.exactInterval(3, 0.30), 1e-12);
        assertEquals(3.0, QuiverFeedRules.exactInterval(3, 0.0), 1e-12);
    }

    @Test
    void theCarryKeepsTheAverageInsteadOfRoundingThreeDownToTwo()
    {
        double exact = QuiverFeedRules.exactInterval(3, 0.30);
        double carry = 0;
        long t = 0;
        int shots = 1300;
        int twos = 0, threes = 0;
        for (int i = 0; i < shots; i++)
        {
            int pause = QuiverFeedRules.ticksFor(carry, exact);
            carry = QuiverFeedRules.carryAfter(carry, exact);
            assertTrue(pause == 2 || pause == 3, "only 2 or 3 tick pauses, got " + pause);
            if (pause == 2) twos++; else threes++;
            t += pause;
        }
        // 1300 shots at 3 / 1.3 ticks each take 3000 ticks: 30 % more bolts than 1000 shots at 3 ticks
        assertTrue(Math.abs(t - 3000) <= 1, "average pause 3 / 1.3, took " + t);
        assertTrue(twos > threes, "mostly 2 tick pauses (" + twos + " vs " + threes + ")");
    }

    @Test
    void withoutBonusThePaceIsUnchangedAndNeverBelowOneTick()
    {
        double carry = 0;
        for (int i = 0; i < 10; i++)
        {
            assertEquals(3, QuiverFeedRules.ticksFor(carry, 3.0));
            carry = QuiverFeedRules.carryAfter(carry, 3.0);
        }
        double fast = QuiverFeedRules.exactInterval(1, 2.0);
        carry = 0;
        long t = 0;
        for (int i = 0; i < 9; i++)
        {
            int pause = QuiverFeedRules.ticksFor(carry, fast);
            carry = QuiverFeedRules.carryAfter(carry, fast);
            assertEquals(1, pause, "one bolt a tick at most");
            t += pause;
        }
        assertEquals(9, t);
    }
}
