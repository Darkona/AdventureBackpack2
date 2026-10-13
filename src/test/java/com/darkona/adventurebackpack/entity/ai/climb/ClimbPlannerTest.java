package com.darkona.adventurebackpack.entity.ai.climb;

import static com.darkona.adventurebackpack.entity.ai.climb.GridQuery.AIR;
import static com.darkona.adventurebackpack.entity.ai.climb.GridQuery.LAVA;
import static com.darkona.adventurebackpack.entity.ai.climb.GridQuery.SOLID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode;
import org.junit.jupiter.api.Test;

/** Spider feet at (0, 64, 0) on flat ground unless a test says otherwise. */
class ClimbPlannerTest
{
    private final ClimbPlan plan = new ClimbPlan();

    private boolean plan(GridQuery q, int sy, int tx, int ty, int tz)
    {
        return plan(q, sy, tx, ty, tz, 32, null);
    }

    private boolean plan(GridQuery q, int sy, int tx, int ty, int tz, int maxClimb, long[] bad)
    {
        return ClimbPlanner.plan(q, 0, sy, 0, tx, ty, tz, maxClimb, 3, 8, bad, plan);
    }

    @Test
    void towerUnderTheOwnerIsClimbedByItsNearestFace()
    {
        GridQuery q = new GridQuery().box(5, 0, 6, 1, 64, 69, SOLID);
        assertTrue(plan(q, 64, 5, 70, 0));
        assertEquals(Mode.APPROACH, plan.mode);
        assertEquals(5, plan.wallX);
        assertEquals(0, plan.wallZ);
        assertEquals(4, plan.approachX);
        assertEquals(0, plan.approachZ);
        assertEquals(70, plan.topY);
        assertFalse(plan.approachByPath);
    }

    @Test
    void tenHighPillarOfOneBlock()
    {
        GridQuery q = new GridQuery().box(5, 0, 5, 0, 64, 73, SOLID);
        assertTrue(plan(q, 64, 5, 74, 0));
        assertEquals(5, plan.wallX);
        assertEquals(74, plan.topY);
    }

    @Test
    void ownerInTheMiddleOfARoofLeadsToTheOuterWall()
    {
        GridQuery q = new GridQuery().box(5, -2, 9, 2, 64, 67, SOLID);
        assertTrue(plan(q, 64, 7, 68, 0));
        assertEquals(5, plan.wallX);
        assertEquals(4, plan.approachX);
        assertEquals(68, plan.topY);
    }

    @Test
    void aWallWithAHoleIsRejectedAndAnotherFaceChosen()
    {
        GridQuery q = new GridQuery().box(5, 0, 6, 1, 64, 69, SOLID).set(5, 66, 0, AIR);
        assertTrue(plan(q, 64, 5, 70, 0));
        assertEquals(5, plan.wallX);
        assertEquals(1, plan.wallZ, "the column next to the broken one");
        assertEquals(4, plan.approachX);
    }

    @Test
    void aPillarWithAHoleHasNoWayUp()
    {
        GridQuery q = new GridQuery().box(5, 0, 5, 0, 64, 69, SOLID).set(5, 66, 0, AIR);
        assertFalse(plan(q, 64, 5, 70, 0));
        assertEquals(Mode.NONE, plan.mode);
    }

    @Test
    void anOverhangOverTheClimbingSideMovesToAnotherSide()
    {
        GridQuery q = new GridQuery().box(5, 0, 5, 0, 64, 69, SOLID).set(4, 68, 0, SOLID);
        assertTrue(plan(q, 64, 5, 70, 0));
        assertEquals(5, plan.wallX);
        assertFalse(plan.approachX == 4 && plan.approachZ == 0, "not under the overhang");
        assertEquals(1, Math.abs(plan.approachX - 5) + Math.abs(plan.approachZ), "a side of the pillar");
    }

    @Test
    void lavaOnTheWayLetsThePathFinderLeadToTheWall()
    {
        GridQuery q = new GridQuery().box(5, 0, 6, 1, 64, 69, SOLID).set(2, 63, 0, LAVA).set(2, 64, 0, LAVA);
        assertTrue(plan(q, 64, 5, 70, 0));
        assertEquals(5, plan.wallX);
        assertTrue(plan.approachByPath);
    }

    @Test
    void aBlacklistedColumnIsSkipped()
    {
        GridQuery q = new GridQuery().box(5, 0, 6, 1, 64, 69, SOLID);
        long[] bad = {ClimbPlan.key(5, 0, 70), Long.MIN_VALUE, Long.MIN_VALUE, Long.MIN_VALUE};
        assertTrue(plan(q, 64, 5, 70, 0, 32, bad));
        assertFalse(plan.wallX == 5 && plan.wallZ == 0);
    }

    @Test
    void tooHighIsNotPlanned()
    {
        GridQuery q = new GridQuery().box(5, 0, 6, 1, 64, 69, SOLID);
        assertFalse(plan(q, 64, 5, 70, 0, 4, null));
    }

    @Test
    void oneBlockUpOrDownIsThePathFindersJob()
    {
        GridQuery q = new GridQuery().box(5, 0, 6, 1, 64, 64, SOLID);
        assertFalse(plan(q, 64, 5, 65, 0));
        assertFalse(plan(new GridQuery(), 64, 5, 63, 0));
    }

    @Test
    void aLowEdgeIsWalkedOff()
    {
        // a platform two blocks high where the spider stands; the owner below on the ground
        GridQuery q = new GridQuery().box(-5, -5, 3, 5, 64, 65, SOLID);
        assertTrue(plan(q, 66, 8, 64, 0));
        assertEquals(Mode.DROP, plan.mode);
        assertEquals(4, plan.approachX, "the cell over the edge");
        assertEquals(3, plan.wallX, "the last ground cell");
        assertEquals(64, plan.bottomY);
    }

    @Test
    void aHighCliffIsClimbedDownAlongItsFace()
    {
        GridQuery q = new GridQuery().box(-5, -5, 3, 5, 64, 69, SOLID);
        assertTrue(plan(q, 70, 8, 64, 0));
        assertEquals(Mode.DESCEND, plan.mode);
        assertEquals(3, plan.wallX);
        assertEquals(4, plan.approachX);
        assertEquals(70, plan.topY);
        assertEquals(64, plan.bottomY);
    }

    @Test
    void aFloatingPlatformHasNoFaceToClimbDown()
    {
        GridQuery q = new GridQuery().box(-5, -5, 3, 5, 69, 69, SOLID);
        assertFalse(plan(q, 70, 8, 64, 0));
    }

    @Test
    void lavaBelowTheEdgeIsNeverAWayDown()
    {
        GridQuery q = new GridQuery().box(-5, -5, 3, 5, 64, 65, SOLID).set(4, 63, 0, LAVA);
        assertFalse(plan(q, 66, 8, 64, 0));
    }

    @Test
    void anUnloadedChunkOnTheLineStopsTheSearch()
    {
        GridQuery q = new GridQuery().box(5, 0, 6, 1, 64, 69, SOLID).unloadedFrom(3);
        assertFalse(plan(q, 64, 5, 70, 0));
        GridQuery cliff = new GridQuery().box(-5, -5, 3, 5, 64, 69, SOLID).unloadedFrom(2);
        assertFalse(plan(cliff, 70, 8, 64, 0));
    }

    @Test
    void columnKeysDifferPerColumnAndHeight()
    {
        assertFalse(ClimbPlan.key(5, 0, 70) == ClimbPlan.key(5, 1, 70));
        assertFalse(ClimbPlan.key(5, 0, 70) == ClimbPlan.key(5, 0, 71));
        assertFalse(ClimbPlan.key(-5, 0, 70) == ClimbPlan.key(5, 0, 70));
        assertFalse(ClimbPlan.key(0, -1, 70) == ClimbPlan.key(0, 1, 70));
    }
}
