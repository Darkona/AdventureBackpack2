package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * The backpack window (ContainerBackpack) reads the worn or held backpack again only when somebody else wrote its
 * data (audit P1, performance contract rule 1): it used to reload the whole backpack from NBT on every server tick
 * while the window was open. Abilities, the hose and the sync write through their own InventoryBackpack; every write
 * in 1.7.10 puts a new tag object under its key, so the identity of the tags tells. Set AB_BENCH=1 to also print the
 * cost of both ways for a full backpack.
 */
class BackpackReloadTest
{
    private static ItemStack backpack()
    {
        return new ItemStack((Item) null);
    }

    @Test
    void nothingWrittenNothingReloaded()
    {
        ItemStack stack = backpack();
        InventoryBackpack window = new InventoryBackpack(stack);
        ItemStack[] slots = window.getInventory();
        assertTrue(window.isCurrent());
        window.reloadIfChanged();
        assertSame(slots, window.getInventory());
        assertTrue(window.isCurrent());
    }

    @Test
    void itsOwnWritesKeepItCurrent()
    {
        ItemStack stack = backpack();
        InventoryBackpack window = new InventoryBackpack(stack);
        window.setLastTime(77);
        window.dirtyTime();
        assertTrue(window.isCurrent());
        window.setInventorySlotContents(0, null);
        assertTrue(window.isCurrent());
        window.dirtyTanks();
        window.dirtyExtended();
        assertTrue(window.isCurrent());
        window.markDirty();
        assertTrue(window.isCurrent());
        window.closeInventory();
        assertTrue(window.isCurrent());
    }

    @Test
    void aWriteByAnotherInstanceIsReadOnTheNextRefresh()
    {
        ItemStack stack = backpack();
        InventoryBackpack window = new InventoryBackpack(stack);
        InventoryBackpack cow = new InventoryBackpack(stack);
        cow.setLastTime(123);
        cow.dirtyTime();
        assertFalse(window.isCurrent());
        window.reloadIfChanged();
        assertEquals(123, window.getLastTime());
        assertTrue(window.isCurrent());

        NBTTagCompound state = new NBTTagCompound();
        state.setInteger("milkTime", 9);
        cow.setExtendedProperties(state);
        cow.markDirty();
        assertFalse(window.isCurrent());
        window.reloadIfChanged();
        assertEquals(9, window.getExtendedProperties().getInteger("milkTime"));
    }

    @Test
    void aForeignWriteStaysVisibleAfterAnOwnPartialWrite()
    {
        // The Cow writes the tanks, then the window writes the items before its next refresh: the window must still
        // read the stack again, or its next full save would put its old tanks back.
        ItemStack stack = backpack();
        InventoryBackpack window = new InventoryBackpack(stack);
        InventoryBackpack cow = new InventoryBackpack(stack);
        cow.setLastTime(5);
        cow.dirtyTime();
        window.setInventorySlotContents(1, null);
        assertFalse(window.isCurrent());
        window.reloadIfChanged();
        assertEquals(5, window.getLastTime());
    }

    @Test
    void aReplacedRootOrDataTagIsNoticed()
    {
        ItemStack stack = backpack();
        InventoryBackpack window = new InventoryBackpack(stack);
        stack.stackTagCompound = (NBTTagCompound) stack.stackTagCompound.copy();
        assertFalse(window.isCurrent());
        window.reloadIfChanged();
        assertTrue(window.isCurrent());
        stack.stackTagCompound.setTag("backpackData", stack.stackTagCompound.getCompoundTag("backpackData").copy());
        assertFalse(window.isCurrent());
    }

    @Test
    void theCopterWindowReadsAgainOnlyAfterAForeignWrite()
    {
        // ContainerCopter.refresh asks isCurrentFor before reading the pack again (audit P6)
        ItemStack stack = backpack();
        InventoryCopterPack window = new InventoryCopterPack(stack);
        assertTrue(window.isCurrentFor(stack));
        window.dirtyTanks();
        assertTrue(window.isCurrentFor(stack), "its own writes keep it current");
        InventoryCopterPack station = new InventoryCopterPack(stack);
        station.markDirty();
        assertFalse(window.isCurrentFor(stack));
        window.openInventory();
        assertTrue(window.isCurrentFor(stack));
    }

    @Test
    void benchmark()
    {
        Assumptions.assumeTrue(System.getenv("AB_BENCH") != null, "set AB_BENCH=1 to measure");
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        Item item = new Item();
        String setup;
        try
        {
            Item.itemRegistry.addObject(4001, "abtest:stuff", item);
            setup = "item registered (id " + Item.getIdFromItem(item) + ")";
        } catch (Throwable e)
        {
            setup = "item not registered: " + e;
        }
        ItemStack stack = backpack();
        InventoryBackpack seed = new InventoryBackpack(stack);
        for (int i = 0; i < seed.getStorageSize(); i++) seed.setInventorySlotContentsNoSave(i, new ItemStack(item, 1 + i));
        seed.markDirty();
        System.out.println("bench setup: " + setup + "; storage " + seed.getStorageSize());
        InventoryBackpack window = new InventoryBackpack(stack);
        int n = 200_000;
        for (int round = 0; round < 3; round++)
        {
            long b0 = mx.getThreadAllocatedBytes(tid), t0 = System.nanoTime();
            for (int i = 0; i < n; i++) window.openInventory();
            long t1 = System.nanoTime(), b1 = mx.getThreadAllocatedBytes(tid);
            for (int i = 0; i < n; i++) window.reloadIfChanged();
            long t2 = System.nanoTime(), b2 = mx.getThreadAllocatedBytes(tid);
            System.out.printf("backpack window refresh, round %d: old (openInventory) %.0f ns %d B | new, nothing changed %.1f ns %d B%n",
                    round, (t1 - t0) / (double) n, (b1 - b0) / n, (t2 - t1) / (double) n, (b2 - b1) / n);
        }
    }
}
