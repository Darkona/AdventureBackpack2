package com.darkona.adventurebackpack.entity.ai.climb;

import com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode;

/**
 * Plans the vertical moves the ground path finder cannot make: up a wall or a tower to an owner above, down a cliff to
 * an owner below. Pure (works on a {@link BlockQuery}), allocates nothing, and is only asked every few ticks when the
 * ground path does not reach the owner.
 * <p>
 * Up: the first wall on the straight line to the owner, or else a column next to (or under) the owner, that is solid
 * from the spider's feet to within one block of the owner's feet, with room on top and nothing overhanging the side it
 * climbs. Down: the first edge on the line to the owner; a drop no higher than the safe height is just walked off,
 * a higher one is climbed down along a solid face.
 */
public final class ClimbPlanner
{
    /** Ticks for the spider to come close enough to the wall foot before the column counts as failed. */
    public static final int APPROACH_TICKS = 100;
    /** Ticks of hitting something above while climbing before the column counts as failed. */
    public static final int CEILING_TICKS = 10;
    /** Ticks to get over the top edge. */
    public static final int LIP_TICKS = 15;
    /** How far along the line to the owner an edge is looked for. */
    public static final int EDGE_SCAN = 4;

    private ClimbPlanner() {}

    /**
     * Fills {@code out} with a plan from the spider's feet ({@code sx, sy, sz}) to the owner's feet ({@code tx, ty,
     * tz}). Returns false (and leaves {@code out.mode} NONE) when the height difference is under 2 (the ground path
     * finder's job), over {@code maxClimb}, or no safe way is found.
     */
    public static boolean plan(BlockQuery q, int sx, int sy, int sz, int tx, int ty, int tz, int maxClimb, int safeDrop,
                               int scanRadius, long[] badColumns, ClimbPlan out)
    {
        out.reset();
        int dy = ty - sy;
        if (dy >= 2 && dy <= maxClimb) return planAscend(q, sx, sy, sz, tx, ty, tz, maxClimb, scanRadius, badColumns, out);
        if (dy <= -2 && -dy <= maxClimb) return planDescend(q, sx, sy, sz, tx, ty, tz, safeDrop, badColumns, out);
        return false;
    }

    // ---------------------------------------------------------------- up

    private static boolean planAscend(BlockQuery q, int sx, int sy, int sz, int tx, int ty, int tz, int maxClimb,
                                      int scanRadius, long[] bad, ClimbPlan out)
    {
        // 1) the first wall on the line to the owner
        int adx = Math.abs(tx - sx), adz = Math.abs(tz - sz);
        int stepX = tx > sx ? 1 : -1, stepZ = tz > sz ? 1 : -1;
        int x = sx, z = sz, prevX = sx, prevZ = sz, i = 0, j = 0;
        boolean straight = true;
        for (int n = 0; n < scanRadius && (i < adx || j < adz); n++)
        {
            if ((1 + 2 * i) * adz < (1 + 2 * j) * adx) { x += stepX; i++; } else { z += stepZ; j++; }
            if (!q.loaded(x, z)) break;
            if (q.solid(x, sy, z) || q.solid(x, sy + 1, z))
            {
                int top = columnTop(q, x, z, prevX, prevZ, sy, ty, maxClimb, bad);
                if (top != Integer.MIN_VALUE)
                {
                    fill(out, Mode.APPROACH, x, z, prevX, prevZ, sy, top, sy, !straight);
                    return true;
                }
                break; // nothing behind a bad wall can be seen from here: try the columns at the owner
            }
            if (!standable(q, x, sy, z)) straight = false;
            prevX = x;
            prevZ = z;
        }

        // 2) a column at or next to the owner's own column (a tower, the building under the owner)
        int bestDistSq = Integer.MAX_VALUE, bestWallX = 0, bestWallZ = 0, bestAX = 0, bestAZ = 0, bestTop = 0;
        for (int cx = tx - 2; cx <= tx + 2; cx++)
        {
            for (int cz = tz - 2; cz <= tz + 2; cz++)
            {
                if (!q.loaded(cx, cz)) continue;
                for (int side = 0; side < 4; side++)
                {
                    int ax = cx + (side == 0 ? 1 : side == 1 ? -1 : 0);
                    int az = cz + (side == 2 ? 1 : side == 3 ? -1 : 0);
                    int distSq = (ax - sx) * (ax - sx) + (az - sz) * (az - sz);
                    if (distSq >= bestDistSq || !q.loaded(ax, az) || !standable(q, ax, sy, az)) continue;
                    int top = columnTop(q, cx, cz, ax, az, sy, ty, maxClimb, bad);
                    if (top == Integer.MIN_VALUE) continue;
                    bestDistSq = distSq;
                    bestWallX = cx;
                    bestWallZ = cz;
                    bestAX = ax;
                    bestAZ = az;
                    bestTop = top;
                }
            }
        }
        if (bestDistSq == Integer.MAX_VALUE) return false;
        fill(out, Mode.APPROACH, bestWallX, bestWallZ, bestAX, bestAZ, sy, bestTop, sy,
                !lineStandable(q, sx, sy, sz, bestAX, bestAZ));
        return true;
    }

    /**
     * The first free height on top of column {@code (cx, cz)} when it is climbable from {@code (ax, az)}, else
     * {@code Integer.MIN_VALUE}. Climbable: solid without gaps from the spider's feet up (it needs contact all the
     * way), ending within one block of the owner's feet with two free blocks on top, nothing overhanging the climbing
     * side, and not on the list of failed columns.
     */
    static int columnTop(BlockQuery q, int cx, int cz, int ax, int az, int sy, int ty, int maxClimb, long[] bad)
    {
        if (!q.solid(cx, sy, cz)) return Integer.MIN_VALUE;
        int top = sy + 1;
        int limit = Math.min(sy + maxClimb + 1, ty + 2);
        while (top <= limit && q.solid(cx, top, cz)) top++;
        if (top < ty - 1 || top > ty + 1 || top - sy < 2) return Integer.MIN_VALUE;
        if (!q.passable(cx, top, cz) || !q.passable(cx, top + 1, cz)) return Integer.MIN_VALUE;
        for (int k = sy + 1; k <= top + 1; k++)
        {
            if (q.solid(ax, k, az)) return Integer.MIN_VALUE;
        }
        if (isBad(bad, ClimbPlan.key(cx, cz, top))) return Integer.MIN_VALUE;
        return top;
    }

    // ---------------------------------------------------------------- down

    private static boolean planDescend(BlockQuery q, int sx, int sy, int sz, int tx, int ty, int tz, int safeDrop,
                                       long[] bad, ClimbPlan out)
    {
        int adx = Math.abs(tx - sx), adz = Math.abs(tz - sz);
        int stepX = tx > sx ? 1 : -1, stepZ = tz > sz ? 1 : -1;
        int x = sx, z = sz, prevX = sx, prevZ = sz, i = 0, j = 0;
        boolean found = false;
        for (int n = 0; n < EDGE_SCAN && (i < adx || j < adz); n++)
        {
            if ((1 + 2 * i) * adz < (1 + 2 * j) * adx) { x += stepX; i++; } else { z += stepZ; j++; }
            if (!q.loaded(x, z)) return false;
            if (q.solid(x, sy, z) || q.solid(x, sy + 1, z) || q.hazard(x, sy, z)) return false; // a wall: the path finder's job
            if (!q.solid(x, sy - 1, z))
            {
                found = true;
                break;
            }
            if (q.hazard(x, sy - 1, z)) return false;
            prevX = x;
            prevZ = z;
        }
        if (!found) return false;

        // free fall height over the edge cell, down to the first solid block
        int maxFall = Math.max(safeDrop, sy - ty) + 1;
        int k = sy - 1;
        while (sy - k <= maxFall && !q.solid(x, k, z) && !q.hazard(x, k, z)) k--;
        if (!q.solid(x, k, z) || q.hazard(x, k, z) || q.hazard(x, k + 1, z)) return false;
        int bottom = k + 1;
        int drop = sy - bottom;
        Mode mode;
        if (drop <= safeDrop)
        {
            mode = Mode.DROP;
        }
        else
        {
            // climbing down needs a solid face all the way, and a landing not far below the owner
            if (bottom < ty - 2) return false;
            for (int y = bottom; y < sy; y++)
            {
                if (!q.solid(prevX, y, prevZ)) return false;
            }
            mode = Mode.DESCEND;
        }
        if (isBad(bad, ClimbPlan.key(prevX, prevZ, bottom))) return false;
        fill(out, mode, prevX, prevZ, x, z, sy, sy, bottom, false);
        return true;
    }

    // ---------------------------------------------------------------- while moving

    /** Ticks a single mode may last for a move of {@code height} blocks (the spider climbs about 0.12 a tick). */
    public static int modeTimeLimit(int height)
    {
        return 60 + 12 * Math.abs(height);
    }

    /**
     * The next mode of a vertical move, from what the spider feels now. {@link Mode#NONE} means done (the ground path
     * finder takes over); {@link Mode#FAILED} means this column did not work.
     *
     * @param posY              the spider's feet height
     * @param besideWall        it touches a wall (with a few ticks of grace at corners)
     * @param horizDistSqToWall squared horizontal distance from the spider's centre to the centre of the wall column
     * @param ceilingTicks      ticks in a row it hit something above while in the air
     */
    public static Mode nextMode(Mode mode, double posY, int startY, int topY, int bottomY, int safeDrop, boolean onGround,
                                boolean besideWall, double horizDistSqToWall, int ceilingTicks, int ticksInMode,
                                int timeLimit)
    {
        if (mode == Mode.NONE || mode == Mode.FAILED) return Mode.NONE;
        if (ticksInMode > timeLimit) return Mode.FAILED;
        switch (mode)
        {
            case APPROACH:
                if (horizDistSqToWall <= 2.25 || (besideWall && horizDistSqToWall <= 4.0)) return Mode.ASCEND;
                return ticksInMode > APPROACH_TICKS ? Mode.FAILED : Mode.APPROACH;
            case ASCEND:
                if (ceilingTicks >= CEILING_TICKS) return Mode.FAILED;
                if (posY >= topY - 0.3) return Mode.LIP;
                if (onGround && !besideWall && ticksInMode > 20) return Mode.FAILED;
                return Mode.ASCEND;
            case LIP:
                if (onGround && posY >= topY - 0.1) return Mode.NONE;
                if (posY < topY - 1.5 || ticksInMode > LIP_TICKS) return Mode.FAILED;
                return Mode.LIP;
            case DROP:
                if (onGround && posY <= bottomY + 0.5) return Mode.NONE;
                if (onGround && ticksInMode > 60) return Mode.FAILED;
                return Mode.DROP;
            case DESCEND:
                if (onGround && posY <= bottomY + 0.5) return Mode.NONE;
                if (onGround && posY < startY - 0.5) return Mode.NONE; // a ledge on the way: walk on from there
                if (onGround && ticksInMode > 60) return Mode.FAILED; // never went over the edge
                if (!onGround && !besideWall && ticksInMode > 20 && posY > bottomY + safeDrop) return Mode.NONE; // lost the face: it just falls
                return Mode.DESCEND;
            default:
                return Mode.NONE;
        }
    }

    // ---------------------------------------------------------------- helpers

    /** A spider can stand here: two free blocks, ground under them, nothing that burns or drowns. */
    static boolean standable(BlockQuery q, int x, int y, int z)
    {
        return q.passable(x, y, z) && q.passable(x, y + 1, z) && q.solid(x, y - 1, z) && !q.hazard(x, y, z)
                && !q.hazard(x, y - 1, z) && !q.liquid(x, y, z);
    }

    private static boolean lineStandable(BlockQuery q, int sx, int sy, int sz, int ex, int ez)
    {
        int adx = Math.abs(ex - sx), adz = Math.abs(ez - sz);
        int stepX = ex > sx ? 1 : -1, stepZ = ez > sz ? 1 : -1;
        int x = sx, z = sz, i = 0, j = 0;
        while (i < adx || j < adz)
        {
            if ((1 + 2 * i) * adz < (1 + 2 * j) * adx) { x += stepX; i++; } else { z += stepZ; j++; }
            if (!q.loaded(x, z) || !standable(q, x, sy, z)) return false;
        }
        return true;
    }

    private static boolean isBad(long[] bad, long key)
    {
        if (bad == null) return false;
        for (long b : bad)
        {
            if (b == key) return true;
        }
        return false;
    }

    private static void fill(ClimbPlan out, Mode mode, int wallX, int wallZ, int approachX, int approachZ, int startY,
                             int topY, int bottomY, boolean approachByPath)
    {
        out.mode = mode;
        out.wallX = wallX;
        out.wallZ = wallZ;
        out.approachX = approachX;
        out.approachZ = approachZ;
        out.startY = startY;
        out.topY = topY;
        out.bottomY = bottomY;
        out.approachByPath = approachByPath;
    }
}
