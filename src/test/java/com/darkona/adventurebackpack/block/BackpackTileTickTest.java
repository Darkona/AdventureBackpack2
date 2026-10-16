package com.darkona.adventurebackpack.block;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityRegistry;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * A placed backpack looks its colour's placed hook up when the colour changes, not on every tick (audit P10), and
 * still runs the hook on every tick of a colour that has one. Set AB_BENCH=1 to print the idle tick before and after.
 */
class BackpackTileTickTest
{
    private static final String COLOUR = "TestPlacedTick";
    private static int placedTicks;

    static
    {
        AbilityRegistry.INSTANCE.register(COLOUR, new IBackpackAbility()
        {
            @Override
            public void onPlacedTick(World world, TileEntity backpackTile)
            {
                placedTicks++;
            }
        });
    }

    @Test
    void thePlacedHookRunsEveryTickOfItsColourAndStopsWhenTheColourChanges()
    {
        TileAdventureBackpack tile = new TileAdventureBackpack();
        placedTicks = 0;
        for (int i = 0; i < 30; i++) tile.updateEntity();
        assertEquals(0, placedTicks, "Standard has no placed hook");

        tile.setColorName(COLOUR);
        for (int i = 0; i < 30; i++) tile.updateEntity();
        assertEquals(30, placedTicks);

        tile.setColorName("Standard");
        for (int i = 0; i < 30; i++) tile.updateEntity();
        assertEquals(30, placedTicks, "a new colour is looked up again");

        tile.setColorName(new String(COLOUR.toCharArray()));
        tile.updateEntity();
        assertEquals(31, placedTicks, "an equal name loaded again (another string) still finds the hook");
    }

    @Test
    void benchmark()
    {
        Assumptions.assumeTrue(System.getenv("AB_BENCH") != null, "set AB_BENCH=1 to measure");
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        TileAdventureBackpack tile = new TileAdventureBackpack();
        tile.setColorName("Cow");
        int n = 2_000_000;
        boolean sink = false;
        for (int round = 0; round < 3; round++)
        {
            long b0 = mx.getThreadAllocatedBytes(tid), t0 = System.nanoTime();
            // the old per-tick lookup, by itself (the rest of updateEntity did not change)
            for (int i = 0; i < n; i++) sink ^= !tile.getColorName().isEmpty() && BackpackAbilities.hasPlacedAbility(tile.getColorName());
            long t1 = System.nanoTime(), b1 = mx.getThreadAllocatedBytes(tid);
            for (int i = 0; i < n; i++) tile.updateEntity();
            long t2 = System.nanoTime(), b2 = mx.getThreadAllocatedBytes(tid);
            System.out.printf("placed backpack idle tick, round %d: old lookup alone %.1f ns %d B | whole updateEntity now %.1f ns %d B%n",
                    round, (t1 - t0) / (double) n, (b1 - b0) / n, (t2 - t1) / (double) n, (b2 - b1) / n);
        }
        System.out.println("sink " + sink);
    }
}
