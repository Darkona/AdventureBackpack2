package com.darkona.adventurebackpack.entity.ai.climb;

import static com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode.APPROACH;
import static com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode.ASCEND;
import static com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode.DESCEND;
import static com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode.DROP;
import static com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode.FAILED;
import static com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode.LIP;
import static com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode.NONE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode;
import org.junit.jupiter.api.Test;

/** Climb from feet 64 to a top of 70, or down from 70 to 64; safe drop 3. */
class ClimbModeTest
{
    private static final int LIMIT = ClimbPlanner.modeTimeLimit(6);

    private static Mode next(Mode mode, double posY, boolean onGround, boolean beside, double wallDistSq, int ceiling, int ticks)
    {
        boolean down = mode == DESCEND || mode == DROP;
        return ClimbPlanner.nextMode(mode, posY, down ? 70 : 64, 70, 64, 3, onGround, beside, wallDistSq, ceiling, ticks, LIMIT);
    }

    @Test
    void approachTurnsIntoClimbingNextToTheWall()
    {
        assertEquals(APPROACH, next(APPROACH, 64, true, false, 9.0, 0, 5));
        assertEquals(ASCEND, next(APPROACH, 64, true, false, 1.44, 0, 5));
        assertEquals(ASCEND, next(APPROACH, 64, true, true, 3.5, 0, 5), "touching the wall at a corner");
        assertEquals(FAILED, next(APPROACH, 64, true, false, 9.0, 0, ClimbPlanner.APPROACH_TICKS + 1));
    }

    @Test
    void climbingReachesTheLip()
    {
        assertEquals(ASCEND, next(ASCEND, 66, false, true, 1.44, 0, 20));
        assertEquals(LIP, next(ASCEND, 69.75, false, true, 1.44, 0, 50));
    }

    @Test
    void aCeilingFailsTheColumn()
    {
        assertEquals(ASCEND, next(ASCEND, 67, false, true, 1.44, ClimbPlanner.CEILING_TICKS - 1, 30));
        assertEquals(FAILED, next(ASCEND, 67, false, true, 1.44, ClimbPlanner.CEILING_TICKS, 30));
    }

    @Test
    void standingAtTheFootWithoutTouchingTheWallFails()
    {
        assertEquals(ASCEND, next(ASCEND, 64, true, false, 2.0, 0, 10), "a moment to reach it");
        assertEquals(FAILED, next(ASCEND, 64, true, false, 2.0, 0, 21));
    }

    @Test
    void lipEndsOnTopOrFails()
    {
        assertEquals(NONE, next(LIP, 70, true, false, 0.3, 0, 4));
        assertEquals(LIP, next(LIP, 70.2, false, false, 0.3, 0, 4));
        assertEquals(FAILED, next(LIP, 70.2, false, false, 0.3, 0, ClimbPlanner.LIP_TICKS + 1));
        assertEquals(FAILED, next(LIP, 68, false, false, 0.3, 0, 3), "fell back down");
    }

    @Test
    void dropEndsOnTheGroundBelow()
    {
        assertEquals(DROP, next(DROP, 70, true, false, 2.0, 0, 5));
        assertEquals(DROP, next(DROP, 67, false, false, 2.0, 0, 8));
        assertEquals(NONE, next(DROP, 64, true, false, 2.0, 0, 12));
        assertEquals(FAILED, next(DROP, 70, true, false, 2.0, 0, 61), "never went over the edge");
    }

    @Test
    void descendEndsAtTheBottomOrALedge()
    {
        assertEquals(DESCEND, next(DESCEND, 70, true, false, 2.0, 0, 5), "still walking to the edge");
        assertEquals(DESCEND, next(DESCEND, 67, false, true, 1.44, 0, 30));
        assertEquals(NONE, next(DESCEND, 64, true, true, 1.44, 0, 50));
        assertEquals(NONE, next(DESCEND, 67, true, true, 1.44, 0, 30), "a ledge halfway: the path finder goes on");
        assertEquals(FAILED, next(DESCEND, 70, true, false, 2.0, 0, 61));
    }

    @Test
    void losingTheFaceHighUpJustFalls()
    {
        assertEquals(DESCEND, next(DESCEND, 68, false, false, 4.0, 0, 15), "a few ticks of grace");
        assertEquals(NONE, next(DESCEND, 68, false, false, 4.0, 0, 21));
        assertEquals(DESCEND, next(DESCEND, 66, false, false, 4.0, 0, 21), "low enough to fall safely: keep going");
    }

    @Test
    void everyModeHasATimeLimit()
    {
        for (Mode mode : new Mode[] {ASCEND, LIP, DESCEND, DROP})
        {
            assertEquals(FAILED, next(mode, 67, false, true, 1.44, 0, LIMIT + 1), mode.name());
        }
        assertTrue(ClimbPlanner.modeTimeLimit(32) > 32 / 0.12, "long enough for the highest climb");
    }

    @Test
    void noneStaysNone()
    {
        assertEquals(NONE, next(NONE, 64, true, true, 1.0, 20, 500));
        assertEquals(NONE, next(FAILED, 64, true, true, 1.0, 20, 500));
    }
}
