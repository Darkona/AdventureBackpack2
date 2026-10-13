package com.darkona.adventurebackpack.entity.ai;

/**
 * When a tamed spider follows its owner, when it is stuck and when it jumps next to the owner. Pure: no Minecraft
 * classes. Distances are squared, in blocks.
 */
public final class SpiderFollowRules
{
    /** Not getting closer: 0 fine, 1 try another way, 2 teleport or give up for a while. */
    public static final int STUCK_NONE = 0, STUCK_REPLAN = 1, STUCK_GIVE_UP = 2;

    private SpiderFollowRules() {}

    public static boolean shouldFollow(double distSq, double start, boolean sitting, boolean ridden, boolean ownerPresent,
                                       boolean sameWorld)
    {
        return ownerPresent && sameWorld && !sitting && !ridden && distSq >= start * start;
    }

    /**
     * Keeps following while farther than {@code stop}, and also closer while it hangs on a wall ({@code climbing}),
     * or it would let go halfway up.
     */
    public static boolean shouldContinue(double distSq, double stop, boolean sitting, boolean ridden, boolean ownerPresent,
                                         boolean sameWorld, boolean climbing)
    {
        if (!ownerPresent || !sameWorld || sitting || ridden) return false;
        return climbing || distSq > stop * stop;
    }

    /** True when the spider got at least {@code minGain} (squared blocks) closer than its best so far. */
    public static boolean progressed(double bestDistSq, double distSq, double minGain)
    {
        return distSq <= bestDistSq - minGain;
    }

    public static int stuckLevel(int stuckTicks, int limit)
    {
        if (limit <= 0 || stuckTicks < limit) return STUCK_NONE;
        return stuckTicks < 2 * limit ? STUCK_REPLAN : STUCK_GIVE_UP;
    }

    /**
     * Teleport next to the owner: when farther than {@code teleportDist} (0 = never this way), or when stuck for good and
     * at least {@code stuckDist} away (0 = never this way). Never while sitting or ridden, never twice within the
     * cooldown.
     */
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
