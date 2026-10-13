package com.darkona.adventurebackpack.entity.ai.climb;

/**
 * A vertical move the spider is making: which wall column it climbs (or which edge it goes over), where it starts and
 * the heights. Mutable and reused, so planning allocates nothing.
 */
public final class ClimbPlan
{
    public enum Mode
    {
        /** No vertical move: the ground path finder walks. */
        NONE,
        /** Walking to the foot of the wall. */
        APPROACH,
        /** Climbing the wall. */
        ASCEND,
        /** Getting over the top edge. */
        LIP,
        /** Climbing down a cliff face, slowly. */
        DESCEND,
        /** Walking off a low edge (no higher than abilities.spider.safeDropHeight). */
        DROP,
        /** Not a state: a transition result meaning "this column failed, forget the plan and avoid it". */
        FAILED
    }

    public Mode mode = Mode.NONE;
    /** The wall column climbed, or for a descent the column of the cliff face. */
    public int wallX, wallZ;
    /** Where the spider stands to start: the foot of the wall, or for a descent the cell over the edge. */
    public int approachX, approachZ;
    /** The spider's feet height when planned. */
    public int startY;
    /** First free height on top of the wall (where the feet end up). */
    public int topY;
    /** Feet height at the bottom of a descent. */
    public int bottomY;
    /** The straight way to the approach cell is not safe: let the ground path finder take the spider there. */
    public boolean approachByPath;

    public void reset()
    {
        mode = Mode.NONE;
        wallX = wallZ = approachX = approachZ = startY = topY = bottomY = 0;
        approachByPath = false;
    }

    /** The key of the climbed column, for the list of columns that failed. */
    public long columnKey()
    {
        return key(wallX, wallZ, mode == Mode.DESCEND || mode == Mode.DROP ? bottomY : topY);
    }

    public static long key(int x, int z, int y)
    {
        return ((long) (x & 0x3FFFFFF) << 38) | ((long) (z & 0x3FFFFFF) << 12) | (y & 0xFFF);
    }
}
