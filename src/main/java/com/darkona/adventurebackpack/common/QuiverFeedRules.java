package com.darkona.adventurebackpack.common;

public final class QuiverFeedRules
{
    public static final int NOT_AMMO = -1;
    public static final double MAX_SPEED_BONUS = 2.0;
    public static final double MAX_RATE_BONUS = 2.0;

    private QuiverFeedRules() {}

    public static int pick(int[] rank, int[] left, int slots)
    {
        int best = -1;
        int n = Math.min(slots, Math.min(rank.length, left.length));
        for (int q = 0; q < n; q++)
        {
            if (rank[q] == NOT_AMMO || left[q] <= 0) continue;
            if (best < 0 || rank[q] < rank[best]) best = q;
        }
        return best;
    }

    public static int usable(int[] rank, int[] left, int slots)
    {
        int total = 0;
        int n = Math.min(slots, Math.min(rank.length, left.length));
        for (int q = 0; q < n; q++)
        {
            if (rank[q] != NOT_AMMO && left[q] > 0) total += left[q];
        }
        return total;
    }

    public static int take(int[] rank, int[] left, int[] spent, int slots, int count)
    {
        if (count <= 0 || usable(rank, left, slots) < count) return -1;
        int last = -1;
        for (int i = 0; i < count; i++)
        {
            last = pick(rank, left, slots);
            left[last]--;
            spent[last]++;
        }
        return last;
    }

    public static boolean triggerHeld(long now, long heldUntil)
    {
        return now <= heldUntil;
    }

    public static boolean shotDue(long now, long nextShotAt)
    {
        return now >= nextShotAt;
    }

    public static boolean flushDue(int sinceFlush, int every)
    {
        return sinceFlush > 0 && sinceFlush >= (every < 1 ? 1 : every);
    }

    public static int toRemove(int spent, int inSlot)
    {
        if (spent <= 0 || inSlot <= 0) return 0;
        return spent < inSlot ? spent : inSlot;
    }

    public static double speedFactor(double bonus)
    {
        if (!(bonus > 0)) return 1.0;
        return 1.0 + (bonus > MAX_SPEED_BONUS ? MAX_SPEED_BONUS : bonus);
    }

    public static double rateFactor(double bonus)
    {
        if (!(bonus > 0)) return 1.0;
        return 1.0 + (bonus > MAX_RATE_BONUS ? MAX_RATE_BONUS : bonus);
    }

    public static double exactInterval(int baseTicks, double bonus)
    {
        return (baseTicks < 1 ? 1 : baseTicks) / rateFactor(bonus);
    }

    public static int ticksFor(double carry, double exact)
    {
        int whole = (int) (carry + exact);
        return whole < 1 ? 1 : whole;
    }

    public static double carryAfter(double carry, double exact)
    {
        double left = carry + exact - ticksFor(carry, exact);
        return left > 0 ? left : 0;
    }
}
