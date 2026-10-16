package com.darkona.adventurebackpack.common.ability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * The Cow and Mooshroom batch made once per eat beat (audit P4) makes the same milk and eats the same wheat as the old
 * batch, which added one millibucket per tick and read and wrote the backpack on every tick of the batch. The old way
 * is rebuilt here from the code it replaced, tick by tick, with tanks that always have room and wheat that never runs out.
 */
class MilkBatchTest
{
    private static final int INTERVAL = 20, WHEAT_PER_BATCH = 16, PER_BATCH = 1000, TICKS = 20_000;

    /** The old makeFromWheat, one call per tick: returns {milk, wheat, inventory builds} after each tick in out. */
    private static void oldWay(long[][] out)
    {
        boolean hasState = false;
        int stored = 0, storedWheat = 0;
        long milk = 0, wheat = 0, builds = 0;
        for (int t = 0; t < TICKS; t++)
        {
            boolean eatBeat = t % INTERVAL == 0;
            if (eatBeat || stored > 0)
            {
                builds++;
                int wheatConsumed = hasState ? storedWheat : 0;
                int milkTime = hasState ? stored - 1 : -1;
                if (eatBeat && milkTime <= 0)
                {
                    wheat++;
                    wheatConsumed++;
                }
                if (wheatConsumed >= WHEAT_PER_BATCH)
                {
                    wheatConsumed = 0;
                    milkTime = PER_BATCH - 1;
                }
                if (milkTime >= 0) milk++;
                if (milkTime < -1) milkTime = -1;
                stored = milkTime;
                storedWheat = wheatConsumed;
                hasState = true;
            }
            out[t][0] = milk;
            out[t][1] = wheat;
            out[t][2] = builds;
        }
    }

    /** The new makeFromWheat, with the same decisions as AbilityHelper. */
    private static void newWay(long[][] out)
    {
        int stored = 0, storedWheat = 0;
        long milk = 0, wheat = 0, builds = 0;
        for (int t = 0; t < TICKS; t++)
        {
            if (t % INTERVAL == 0)
            {
                builds++;
                int wheatConsumed = storedWheat;
                int remaining = MilkBatch.remaining(stored);
                if (MilkBatch.eats(remaining, true))
                {
                    wheat++;
                    wheatConsumed++;
                }
                if (MilkBatch.starts(wheatConsumed, WHEAT_PER_BATCH))
                {
                    wheatConsumed = 0;
                    remaining = PER_BATCH;
                }
                int chunk = MilkBatch.chunk(remaining, INTERVAL);
                milk += chunk;
                remaining -= chunk;
                stored = remaining;
                storedWheat = wheatConsumed;
            }
            out[t][0] = milk;
            out[t][1] = wheat;
            out[t][2] = builds;
        }
    }

    @Test
    void sameMilkAndWheatAsOneMillibucketPerTick()
    {
        long[][] old = new long[TICKS][3];
        long[][] now = new long[TICKS][3];
        oldWay(old);
        newWay(now);
        for (int t = 0; t < TICKS; t++)
        {
            long ahead = now[t][0] - old[t][0];
            assertTrue(ahead >= 0 && ahead < INTERVAL, "tick " + t + ": the beat makes at most the next beat's share early, was " + ahead);
            assertEquals(old[t][1], now[t][1], "wheat eaten by tick " + t);
        }
        long oldBuilds = old[TICKS - 1][2], newBuilds = now[TICKS - 1][2];
        System.out.println("cow/mooshroom, " + TICKS + " ticks: " + old[TICKS - 1][0] + " mB, inventory builds old " + oldBuilds + ", new " + newBuilds);
        assertTrue(newBuilds * 10 < oldBuilds, "about one build per beat instead of one per tick of the batch");
    }

    @Test
    void decisions()
    {
        assertEquals(0, MilkBatch.remaining(-1), "old stacks keep -1 after a batch");
        assertEquals(999, MilkBatch.remaining(999), "a batch started by the old code goes on");
        assertTrue(MilkBatch.eats(0, true));
        assertFalse(MilkBatch.eats(5, true), "no wheat while a batch runs");
        assertFalse(MilkBatch.eats(0, false));
        assertTrue(MilkBatch.starts(16, 16));
        assertFalse(MilkBatch.starts(15, 16));
        assertEquals(20, MilkBatch.chunk(1000, 20));
        assertEquals(7, MilkBatch.chunk(7, 20), "never more than the batch needs");
        assertEquals(0, MilkBatch.chunk(0, 20));
        assertEquals(1, MilkBatch.chunk(5, 0), "at least one per beat");
    }
}
