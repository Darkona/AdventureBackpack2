package com.darkona.adventurebackpack.entity.ai.climb;

import com.darkona.adventurebackpack.entity.ai.climb.ClimbPlan.Mode;

public final class ClimbPlanner
{
    public static final int APPROACH_TICKS = 100;
    public static final int CEILING_TICKS = 10;
    public static final int LIP_TICKS = 15;
    public static final int EDGE_SCAN = 4;

    private ClimbPlanner() {}

    public static boolean plan(BlockQuery q, int sx, int sy, int sz, int tx, int ty, int tz, int maxClimb, int safeDrop,
                               int scanRadius, long[] badColumns, ClimbPlan out)
    {
        out.reset();
        int dy = ty - sy;
        if (dy >= 2 && dy <= maxClimb) return planAscend(q, sx, sy, sz, tx, ty, tz, maxClimb, scanRadius, badColumns, out);
        if (dy <= -2 && -dy <= maxClimb) return planDescend(q, sx, sy, sz, tx, ty, tz, safeDrop, badColumns, out);
        return false;
    }

    private static boolean planAscend(BlockQuery q, int sx, int sy, int sz, int tx, int ty, int tz, int maxClimb,
                                      int scanRadius, long[] bad, ClimbPlan out)
    {
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
                break;
            }
            if (!standable(q, x, sy, z)) straight = false;
            prevX = x;
            prevZ = z;
        }

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
            if (q.solid(x, sy, z) || q.solid(x, sy + 1, z) || q.hazard(x, sy, z)) return false;
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

    public static int modeTimeLimit(int height)
    {
        return 60 + 12 * Math.abs(height);
    }

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
                if (onGround && posY >= topY - 0.1 && horizDistSqToWall <= 1.0) return Mode.NONE;
                if (posY < topY - 1.5 || ticksInMode > LIP_TICKS) return Mode.FAILED;
                return Mode.LIP;
            case DROP:
                if (onGround && posY <= bottomY + 0.5) return Mode.NONE;
                if (onGround && ticksInMode > 60) return Mode.FAILED;
                return Mode.DROP;
            case DESCEND:
                if (onGround && posY <= bottomY + 0.5) return Mode.NONE;
                if (onGround && posY < startY - 0.5) return Mode.NONE;
                if (onGround && ticksInMode > 60) return Mode.FAILED;
                if (!onGround && !besideWall && posY < startY - 1.5 && posY > bottomY + safeDrop) return Mode.NONE;
                return Mode.DESCEND;
            default:
                return Mode.NONE;
        }
    }

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
