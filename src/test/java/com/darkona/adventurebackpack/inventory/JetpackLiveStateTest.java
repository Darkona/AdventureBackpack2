package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * The worn steam jetpack's inventory lives between server ticks (performance contract, rule 1): a tick where nothing
 * changed touches no NBT, a tick writes only the keys that changed, and a write by anyone else is noticed by the
 * identity of the tags. The NBT after a run of ticks is the same the old tick (read everything, write everything)
 * left. Set AB_BENCH=1 to also print the time and the bytes of both ways.
 */
class JetpackLiveStateTest
{
    private static ItemStack jetpack()
    {
        return new ItemStack(new Item());
    }

    @Test
    void aTickWithoutChangesWritesNothing()
    {
        ItemStack stack = jetpack();
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        Object data = stack.stackTagCompound.getTag("jetpackData");
        assertEquals(0, inv.flushChanges());
        assertTrue(inv.isCurrentFor(stack));
        assertTrue(data == stack.stackTagCompound.getTag("jetpackData"));
    }

    @Test
    void aTickWritesOnlyWhatChanged()
    {
        ItemStack stack = jetpack();
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        NBTTagCompound data = stack.stackTagCompound.getCompoundTag("jetpackData");
        Object water = data.getTag("waterTank");
        inv.setTemperature(80);
        inv.setBoiling(true);
        assertEquals(2, inv.flushChanges());
        assertEquals(80, data.getInteger("temperature"));
        assertTrue(data.getBoolean("boiling"));
        assertTrue(water == data.getTag("waterTank"), "an unchanged tank is not written again");
        assertTrue(inv.isCurrentFor(stack), "its own writes do not make it stale");
        assertEquals(0, inv.flushChanges());
    }

    @Test
    void aWriteByAnotherInstanceIsNoticed()
    {
        ItemStack stack = jetpack();
        InventorySteamJetpack live = new InventorySteamJetpack(stack);
        InventorySteamJetpack packet = new InventorySteamJetpack(stack);
        packet.setStatus(true);
        packet.markDirty();
        assertFalse(live.isCurrentFor(stack));
        assertTrue(new InventorySteamJetpack(stack).getStatus());
    }

    @Test
    void anotherStackOrAReplacedTagIsNotCurrent()
    {
        ItemStack stack = jetpack();
        InventorySteamJetpack live = new InventorySteamJetpack(stack);
        assertFalse(live.isCurrentFor(jetpack()));
        stack.stackTagCompound = (NBTTagCompound) stack.stackTagCompound.copy();
        assertFalse(live.isCurrentFor(stack));
    }

    @Test
    void readingAgainEmptiesASlotMissingFromTheList()
    {
        ItemStack stack = jetpack();
        InventorySteamJetpack window = new InventorySteamJetpack(stack);
        window.setInventorySlotContentsNoSave(InventorySteamJetpack.FUEL_SLOT, new ItemStack(new Item(), 1));
        // the tick burnt the last piece of fuel and wrote a list without it
        window.openInventory();
        assertNull(window.getStackInSlot(InventorySteamJetpack.FUEL_SLOT), "the window would write the fuel back");
    }

    @Test
    void theCopterNoticesTheEngineSwitchAndTheStation()
    {
        ItemStack stack = jetpack();
        InventoryCopterPack live = new InventoryCopterPack(stack);
        assertTrue(live.isCurrentFor(stack));
        live.setStatus((byte) 1);
        live.dirtyStatus();
        assertTrue(live.isCurrentFor(stack), "its own writes do not make it stale");
        stack.stackTagCompound.setByte("status", (byte) 2);
        assertFalse(live.isCurrentFor(stack), "ServerActions writes the status straight into the stack");
        live = new InventoryCopterPack(stack);
        new InventoryCopterPack(stack).dirtyTanks();
        assertFalse(live.isCurrentFor(stack), "a fill by the Fuel Station");
    }

    /** The same ticks, the old way and the new way, leave the same NBT. */
    @Test
    void theNbtAfterTicksIsTheSameAsBefore()
    {
        ItemStack oldStack = jetpack();
        ItemStack newStack = jetpack();
        new InventorySteamJetpack(oldStack);
        InventorySteamJetpack live = new InventorySteamJetpack(newStack);
        for (int t = 0; t < 50; t++)
        {
            InventorySteamJetpack old = new InventorySteamJetpack(oldStack);
            old.openInventory();
            step(old, t);
            old.dirtyInventory();
            old.dirtyBoiler();
            old.closeInventory();

            step(live, t);
            live.flushChanges();
            // the burn and cool counters are written on their beat: the stack lags by less than one step
            NBTTagCompound d = newStack.stackTagCompound.getCompoundTag("jetpackData");
            assertTrue(Math.abs(d.getInteger("burnTicks") - live.getBurnTicks()) < InventorySteamJetpack.COUNTER_STEP * 7);
            assertTrue(Math.abs(d.getInteger("coolTicks") - live.getCoolTicks()) < InventorySteamJetpack.COUNTER_STEP);
        }
        // a save, a reload or an unequip writes them exactly first
        live.flushCounters();
        assertEquals(oldStack.stackTagCompound, newStack.stackTagCompound);
        assertNotNull(newStack.stackTagCompound.getCompoundTag("jetpackData").getTag("temperature"));
    }

    @Test
    void burnCountersAreWrittenOnTheirBeatOnly()
    {
        ItemStack stack = jetpack();
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        inv.setBurnTicks(1600);
        inv.flushChanges();
        int writes = 0;
        for (int t = 1; t <= 100; t++)
        {
            inv.setBurnTicks(1600 - t);
            Object before = stack.stackTagCompound.getCompoundTag("jetpackData").getTag("burnTicks");
            inv.flushChanges();
            if (before != stack.stackTagCompound.getCompoundTag("jetpackData").getTag("burnTicks")) writes++;
        }
        assertEquals(100 / InventorySteamJetpack.COUNTER_STEP, writes, "one write per step, not one per tick");
        inv.setBurnTicks(0);
        inv.flushChanges();
        assertEquals(0, stack.stackTagCompound.getCompoundTag("jetpackData").getInteger("burnTicks"), "running out is written at once");
        inv.setBurnTicks(1600);
        inv.flushChanges();
        assertEquals(1600, stack.stackTagCompound.getCompoundTag("jetpackData").getInteger("burnTicks"), "new fuel is written at once");
    }

    @Test
    void counterDueDecides()
    {
        assertFalse(InventorySteamJetpack.counterDue(5, 5));
        assertTrue(InventorySteamJetpack.counterDue(6, 5), "up");
        assertTrue(InventorySteamJetpack.counterDue(0, 5), "out");
        assertFalse(InventorySteamJetpack.counterDue(41, 42));
        assertTrue(InventorySteamJetpack.counterDue(39, 40), "past a step");
        assertTrue(InventorySteamJetpack.counterDue(35, 42), "past a step by more than one");
    }

    @Test
    void aReloadOfTheLiveInstanceKeepsItsCounters()
    {
        ItemStack stack = jetpack();
        InventorySteamJetpack inv = new InventorySteamJetpack(stack);
        inv.setBurnTicks(1610);
        inv.flushChanges();
        inv.setBurnTicks(1605);
        inv.flushChanges();
        assertEquals(1610, stack.stackTagCompound.getCompoundTag("jetpackData").getInteger("burnTicks"), "between beats");
        inv.openInventory();
        assertEquals(1605, inv.getBurnTicks(), "a window opening on the live instance does not set it back");
        assertEquals(1605, stack.stackTagCompound.getCompoundTag("jetpackData").getInteger("burnTicks"));
    }

    private static void step(InventorySteamJetpack inv, int t)
    {
        if (t % 3 == 0) inv.setTemperature(inv.getTemperature() + 1);
        inv.setBurnTicks(Math.max(0, 400 - t * 7));
        inv.setCoolTicks(inv.getCoolTicks() - 1);
        inv.setBoiling(inv.getTemperature() > 40);
        inv.setLeaking(t > 45);
        if (t == 10) inv.setStatus(true);
        if (t == 20) inv.setInUse(true);
        if (t == 30) inv.setInUse(false);
    }

    @Test
    void benchmark()
    {
        Assumptions.assumeTrue(System.getenv("AB_BENCH") != null, "set AB_BENCH=1 to measure");
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        ItemStack stack = jetpack();
        InventorySteamJetpack seed = new InventorySteamJetpack(stack);
        // three slots in use, like a jetpack with a bucket and fuel (unregistered items: the cost of the tags is real)
        Item item = new Item();
        String setup = "";
        try
        {
            Item.itemRegistry.addObject(4000, "abtest:fuel", item);
            setup += "item registered (id " + Item.getIdFromItem(item) + ")";
        } catch (Throwable e)
        {
            setup += "item not registered: " + e;
        }
        seed.setInventorySlotContentsNoSave(0, new ItemStack(item, 1));
        seed.setInventorySlotContentsNoSave(1, new ItemStack(item, 1));
        seed.setInventorySlotContentsNoSave(2, new ItemStack(item, 12));
        try
        {
            seed.getWaterTank().fill(new net.minecraftforge.fluids.FluidStack(net.minecraftforge.fluids.FluidRegistry.WATER, 3000), true);
            seed.getSteamTank().fill(new net.minecraftforge.fluids.FluidStack(net.minecraftforge.fluids.FluidRegistry.WATER, 2000), true);
            setup += ", tanks filled";
        } catch (Throwable e)
        {
            setup += ", tanks empty: " + e;
        }
        seed.markDirty();
        System.out.println("bench setup: " + setup + "; NBT " + stack.stackTagCompound);
        int n = 200_000;
        for (int round = 0; round < 3; round++)
        {
            long b0 = mx.getThreadAllocatedBytes(tid), t0 = System.nanoTime();
            for (int i = 0; i < n; i++)
            {
                InventorySteamJetpack old = new InventorySteamJetpack(stack);
                old.openInventory();
                old.dirtyInventory();
                old.dirtyBoiler();
                old.closeInventory();
            }
            long t1 = System.nanoTime(), b1 = mx.getThreadAllocatedBytes(tid);
            InventorySteamJetpack live = new InventorySteamJetpack(stack);
            long b2 = mx.getThreadAllocatedBytes(tid), t2 = System.nanoTime();
            for (int i = 0; i < n; i++)
            {
                if (!live.isCurrentFor(stack)) live = new InventorySteamJetpack(stack);
                live.flushChanges();
            }
            long t3 = System.nanoTime(), b3 = mx.getThreadAllocatedBytes(tid);
            long b4 = b3, t4 = System.nanoTime();
            for (int i = 0; i < n; i++)
            {
                if (!live.isCurrentFor(stack)) live = new InventorySteamJetpack(stack);
                live.setTemperature(i & 255);
                live.flushChanges();
            }
            long t5 = System.nanoTime(), b5 = mx.getThreadAllocatedBytes(tid);
            System.out.printf("jetpack tick NBT, round %d: old %.0f ns %d B | new, nothing changed %.1f ns %d B | new, temperature changed %.0f ns %d B%n",
                    round, (t1 - t0) / (double) n, (b1 - b0) / n, (t3 - t2) / (double) n, (b3 - b2) / n, (t5 - t4) / (double) n, (b5 - b4) / n);
        }
    }
}
