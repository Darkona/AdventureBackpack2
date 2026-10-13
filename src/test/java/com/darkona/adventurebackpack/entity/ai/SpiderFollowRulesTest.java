package com.darkona.adventurebackpack.entity.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpiderFollowRulesTest
{
    @Test
    void followsFromStartDistanceOnlyWhenFree()
    {
        assertTrue(SpiderFollowRules.shouldFollow(36, 6, false, false, true, true));
        assertFalse(SpiderFollowRules.shouldFollow(35, 6, false, false, true, true));
        assertFalse(SpiderFollowRules.shouldFollow(100, 6, true, false, true, true), "sitting");
        assertFalse(SpiderFollowRules.shouldFollow(100, 6, false, true, true, true), "ridden");
        assertFalse(SpiderFollowRules.shouldFollow(100, 6, false, false, false, true), "owner gone");
        assertFalse(SpiderFollowRules.shouldFollow(100, 6, false, false, true, false), "other world");
    }

    @Test
    void continuesDownToStopDistanceAndWhileClimbing()
    {
        assertTrue(SpiderFollowRules.shouldContinue(16, 3, false, false, true, true, false), "between stop and start");
        assertFalse(SpiderFollowRules.shouldContinue(9, 3, false, false, true, true, false));
        assertTrue(SpiderFollowRules.shouldContinue(4, 3, false, false, true, true, true), "never let go of a wall halfway");
        assertFalse(SpiderFollowRules.shouldContinue(100, 3, true, false, true, true, true), "sitting");
        assertFalse(SpiderFollowRules.shouldContinue(100, 3, false, true, true, true, true), "ridden");
        assertFalse(SpiderFollowRules.shouldContinue(100, 3, false, false, false, true, true), "owner gone");
        assertFalse(SpiderFollowRules.shouldContinue(100, 3, false, false, true, false, true), "other world");
    }

    @Test
    void progressNeedsTheMinimumGain()
    {
        assertTrue(SpiderFollowRules.progressed(50, 49, 1.0));
        assertFalse(SpiderFollowRules.progressed(50, 49.5, 1.0));
        assertFalse(SpiderFollowRules.progressed(50, 60, 1.0));
    }

    @Test
    void stuckLevels()
    {
        assertEquals(SpiderFollowRules.STUCK_NONE, SpiderFollowRules.stuckLevel(40, 60));
        assertEquals(SpiderFollowRules.STUCK_REPLAN, SpiderFollowRules.stuckLevel(60, 60));
        assertEquals(SpiderFollowRules.STUCK_REPLAN, SpiderFollowRules.stuckLevel(119, 60));
        assertEquals(SpiderFollowRules.STUCK_GIVE_UP, SpiderFollowRules.stuckLevel(120, 60));
        assertEquals(SpiderFollowRules.STUCK_NONE, SpiderFollowRules.stuckLevel(1000, 0));
    }

    @Test
    void teleportMatrix()
    {
        int give = SpiderFollowRules.STUCK_GIVE_UP, none = SpiderFollowRules.STUCK_NONE;
        assertTrue(SpiderFollowRules.shouldTeleport(25 * 25, 24, none, 8, 500, 100, false, false), "far behind");
        assertFalse(SpiderFollowRules.shouldTeleport(20 * 20, 24, none, 8, 500, 100, false, false), "close and moving");
        assertTrue(SpiderFollowRules.shouldTeleport(9 * 9, 24, give, 8, 500, 100, false, false), "stuck at 9 blocks");
        assertFalse(SpiderFollowRules.shouldTeleport(5 * 5, 24, give, 8, 500, 100, false, false), "stuck but close");
        assertFalse(SpiderFollowRules.shouldTeleport(9 * 9, 24, SpiderFollowRules.STUCK_REPLAN, 8, 500, 100, false, false),
                "first try another wall");
        assertFalse(SpiderFollowRules.shouldTeleport(30 * 30, 24, give, 8, 50, 100, false, false), "cooldown");
        assertFalse(SpiderFollowRules.shouldTeleport(30 * 30, 24, give, 8, 500, 100, true, false), "sitting");
        assertFalse(SpiderFollowRules.shouldTeleport(30 * 30, 24, give, 8, 500, 100, false, true), "ridden");
        assertFalse(SpiderFollowRules.shouldTeleport(30 * 30, 0, none, 8, 500, 100, false, false), "0 = never for distance");
        assertTrue(SpiderFollowRules.shouldTeleport(30 * 30, 0, give, 8, 500, 100, false, false), "but still when stuck");
        assertFalse(SpiderFollowRules.shouldTeleport(20 * 20, 24, give, 0, 500, 100, false, false), "stuck distance 0 = only far");
    }

    @Test
    void retryCooldown()
    {
        assertTrue(SpiderFollowRules.retryCooldownOver(0));
        assertFalse(SpiderFollowRules.retryCooldownOver(5));
    }
}
