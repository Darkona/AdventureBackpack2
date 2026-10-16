package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;

/**
 * The built-in tool rules of the backpack's tool slots (tweaks.toolcycle.mode = none): vanilla tools, tool names,
 * never stackable items, swords or bows. Hoppers and every click reach the check (audit P5: it used to look four mod
 * classes up by name on every call, each one throwing when the mod is absent). Set AB_BENCH=1 to print its cost.
 */
class SlotToolTest
{
    private static ItemStack named(String name)
    {
        Item item = new Item().setUnlocalizedName(name).setMaxStackSize(1);
        return new ItemStack(item);
    }

    @Test
    void vanillaToolsAndToolNamesAreTools()
    {
        assertTrue(SlotTool.isValidTool(new ItemStack(new ItemShears())));
        assertTrue(SlotTool.isValidTool(named("bigWrench")));
        assertTrue(SlotTool.isValidTool(named("steelHammer")));
        assertTrue(SlotTool.isValidTool(named("Drill_Diamond")));
    }

    @Test
    void weaponsBucketsAndOtherItemsAreNot()
    {
        assertFalse(SlotTool.isValidTool(named("ironSword")));
        assertFalse(SlotTool.isValidTool(named("woodBucket")));
        assertFalse(SlotTool.isValidTool(named("longBow")));
        assertFalse(SlotTool.isValidTool(named("abtestThing")));
        assertFalse(SlotTool.isValidTool(new ItemStack(new Item().setUnlocalizedName("wrench"))), "stackable");
        assertFalse(SlotTool.isValidTool(null));
    }

    @Test
    void benchmark()
    {
        Assumptions.assumeTrue(System.getenv("AB_BENCH") != null, "set AB_BENCH=1 to measure");
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        // An item that is no tool goes through every rule, the mod classes included (none installed in the tests)
        ItemStack stack = named("abtestThing");
        int n = 50_000;
        for (int round = 0; round < 3; round++)
        {
            long b0 = mx.getThreadAllocatedBytes(tid), t0 = System.nanoTime();
            for (int i = 0; i < n; i++) SlotTool.isValidTool(stack);
            long t1 = System.nanoTime(), b1 = mx.getThreadAllocatedBytes(tid);
            System.out.printf("tool slot check, not a tool, round %d: %.0f ns %d B%n", round, (t1 - t0) / (double) n, (b1 - b0) / n);
        }
    }
}
