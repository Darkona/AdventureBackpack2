package com.darkona.adventurebackpack.entity.ai;

public final class SpiderFollowRules
{
    public static final int STUCK_NONE = 0, STUCK_REPLAN = 1, STUCK_GIVE_UP = 2;

    private SpiderFollowRules() {}

    public static boolean shouldFollow(double distSq, double start, boolean sitting, boolean ridden, boolean ownerPresent,
                                       boolean sameWorld)
    {
        return ownerPresent && sameWorld && !sitting && !ridden && distSq >= start * start;
    }

    public static boolean shouldContinue(double distSq, double stop, boolean sitting, boolean ridden, boolean ownerPresent,
                                         boolean sameWorld, boolean climbing)
    {
        if (!ownerPresent || !sameWorld || sitting || ridden) return false;
        return climbing || distSq > stop * stop;
    }

    public static boolean progressed(double bestDistSq, double distSq, double minGain)
    {
        return distSq <= bestDistSq - minGain;
    }

    public static int stuckLevel(int stuckTicks, int limit)
    {
        if (limit <= 0 || stuckTicks < limit) return STUCK_NONE;
        return stuckTicks < 2 * limit ? STUCK_REPLAN : STUCK_GIVE_UP;
    }

    public static boolean shouldTeleport(double distSq, int teleportDist, int stuckLevel, int stuckDist,
                                         int ticksSinceTeleport, int cooldown, boolean sitting, boolean ridden)
    {
        if (sitting || ridden || ticksSinceTeleport < cooldown) return false;
        if (teleportDist > 0 && distSq >= (double) teleportDist * teleportDist) return true;
        return stuckDist > 0 && stuckLevel >= STUCK_GIVE_UP && distSq >= (double) stuckDist * stuckDist;
    }

    public static boolean retryCooldownOver(int ticksLeft)
    {
        return ticksLeft <= 0;
    }
}
