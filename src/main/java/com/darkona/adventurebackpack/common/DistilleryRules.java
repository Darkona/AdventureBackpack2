package com.darkona.adventurebackpack.common;

public final class DistilleryRules
{
    private DistilleryRules() {}

    public static final int GRID = 9;

    public static int planBatch(int[] yields, int room)
    {
        if (yields == null || room <= 0) return 0;
        int mask = 0;
        int total = 0;
        for (int i = 0; i < yields.length && i < 31; i++)
        {
            int y = yields[i];
            if (y <= 0 || y > room - total) continue;
            total += y;
            mask |= 1 << i;
        }
        return mask;
    }

    public static int batchYield(int[] yields, int mask)
    {
        if (yields == null) return 0;
        int total = 0;
        for (int i = 0; i < yields.length && i < 31; i++)
        {
            if ((mask & (1 << i)) != 0 && yields[i] > 0) total += yields[i];
        }
        return total;
    }

    public static boolean canStart(int mask, int catalyst, int catalystPerBatch, int water, int waterPerBatch)
    {
        return mask != 0 && catalyst >= Math.max(0, catalystPerBatch) && water >= Math.max(0, waterPerBatch);
    }

    public static boolean shouldLightFuel(int burnTicks, boolean canStart)
    {
        return burnTicks <= 0 && canStart;
    }

    public static int burnTicksUsed(int burnTicks, int intervalTicks)
    {
        if (burnTicks <= 0 || intervalTicks <= 0) return 0;
        return Math.min(burnTicks, intervalTicks);
    }

    public static int advance(int progress, int burntTicks, boolean canStart, int ticksPerBatch)
    {
        if (!canStart) return 0;
        if (burntTicks <= 0) return Math.max(0, progress);
        long next = (long) Math.max(0, progress) + burntTicks;
        return (int) Math.min(next, Math.max(1, ticksPerBatch));
    }

    public static boolean batchDone(int progress, int ticksPerBatch)
    {
        return progress >= Math.max(1, ticksPerBatch);
    }

    public static int pushAmount(int stored, int perPush)
    {
        if (stored <= 0 || perPush <= 0) return 0;
        return Math.min(stored, perPush);
    }

    public static boolean pushDue(long worldTime, int intervalTicks, int stored, boolean anyNeighbour)
    {
        return stored > 0 && anyNeighbour && intervalTicks > 0 && worldTime % intervalTicks == 0;
    }

    public static int nextStartSide(int start, int sides)
    {
        return sides <= 0 ? 0 : (start + 1) % sides;
    }

    public static int sourceWater(int sources, int perSourcePerSecond, int intervalTicks, int stored, int capacity)
    {
        if (sources <= 0 || perSourcePerSecond <= 0 || intervalTicks <= 0) return 0;
        long made = (long) sources * perSourcePerSecond * intervalTicks / 20L;
        int free = capacity - Math.max(0, stored);
        if (free <= 0 || made <= 0) return 0;
        return (int) Math.min(made, free);
    }

    public static final class Amount
    {
        public final String item;
        public final int amount;

        Amount(String item, int amount)
        {
            this.item = item;
            this.amount = amount;
        }
    }

    public static Amount parseAmount(String text)
    {
        if (text == null) return null;
        String t = text.replace(" ", "").replace("\t", "");
        int eq = t.lastIndexOf('=');
        if (eq <= 0 || eq == t.length() - 1) return null;
        int amount;
        try
        {
            amount = Integer.parseInt(t.substring(eq + 1));
        } catch (NumberFormatException bad)
        {
            return null;
        }
        if (amount <= 0) return null;
        return new Amount(t.substring(0, eq), amount);
    }

    public static boolean anyDamage(String item)
    {
        if (item == null) return false;
        String t = item.trim();
        if (!t.toLowerCase(java.util.Locale.ROOT).startsWith("item:")) return false;
        return t.split(":").length == 3;
    }
}
