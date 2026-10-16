package com.darkona.adventurebackpack.playerProperties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 * StackSnapshot (audit P2) sees every change the old deep compare against a copy saw, by identity only. Set AB_BENCH=1
 * to print the cost of both ways on a full backpack.
 */
class StackSnapshotTest
{
    private static final Item ITEM = new Item();

    /** A worn backpack as the mod writes it: 39 stacks (a few enchanted), two tanks, colour, extended properties. */
    private static ItemStack fullBackpack()
    {
        ItemStack stack = new ItemStack(ITEM);
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound data = new NBTTagCompound();
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < 39; i++)
        {
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte) i);
            item.setShort("id", (short) (256 + i));
            item.setByte("Count", (byte) 64);
            item.setShort("Damage", (short) 0);
            if (i % 8 == 0)
            {
                NBTTagCompound tag = new NBTTagCompound();
                NBTTagList ench = new NBTTagList();
                NBTTagCompound e = new NBTTagCompound();
                e.setShort("id", (short) 16);
                e.setShort("lvl", (short) 3);
                ench.appendTag(e);
                tag.setTag("ench", ench);
                NBTTagCompound display = new NBTTagCompound();
                NBTTagList lore = new NBTTagList();
                lore.appendTag(new NBTTagString("line " + i));
                display.setTag("Lore", lore);
                tag.setTag("display", display);
                item.setTag("tag", tag);
            }
            items.appendTag(item);
        }
        data.setTag("ABPItems", items);
        data.setTag("leftTank", tank("water", 4000));
        data.setTag("rightTank", tank("lava", 2000));
        data.setString("colorName", "Cow");
        NBTTagCompound ext = new NBTTagCompound();
        ext.setInteger("wheat", 12);
        ext.setInteger("milkTime", 300);
        data.setTag("extendedProperties", ext);
        root.setTag("backpackData", data);
        stack.setTagCompound(root);
        return stack;
    }

    private static NBTTagCompound tank(String fluid, int amount)
    {
        NBTTagCompound t = new NBTTagCompound();
        t.setString("FluidName", fluid);
        t.setInteger("Amount", amount);
        return t;
    }

    private static NBTTagCompound data(ItemStack s)
    {
        return s.stackTagCompound.getCompoundTag("backpackData");
    }

    private static StackSnapshot taken(ItemStack s)
    {
        StackSnapshot snap = new StackSnapshot();
        snap.take(s);
        assertTrue(snap.matches(s), "nothing changed right after take");
        return snap;
    }

    @Test
    void nothingTakenNeverMatches()
    {
        assertFalse(new StackSnapshot().matches(null));
        assertFalse(new StackSnapshot().matches(fullBackpack()));
    }

    @Test
    void wearingNothingMatchesUntilSomethingIsWorn()
    {
        StackSnapshot snap = new StackSnapshot();
        snap.take(null);
        assertTrue(snap.matches(null));
        assertFalse(snap.matches(fullBackpack()));
    }

    @Test
    void anotherStackOrItemFieldsDoNotMatch()
    {
        ItemStack s = fullBackpack();
        StackSnapshot snap = taken(s);
        assertFalse(snap.matches(fullBackpack()), "an equal but other stack (a sync replaced it) is sent");
        s.stackSize = 2;
        assertFalse(snap.matches(s));
        snap.take(s);
        s.setItemDamage(3);
        assertFalse(snap.matches(s));
        snap.take(s);
        s.setTagCompound(null);
        assertFalse(snap.matches(s), "tag removed");
        snap.take(s);
        assertTrue(snap.matches(s));
        s.setTagCompound(new NBTTagCompound());
        assertFalse(snap.matches(s), "tag added");
    }

    @Test
    void aWriteDeepInsideIsSeen()
    {
        ItemStack s = fullBackpack();
        StackSnapshot snap = taken(s);
        data(s).getCompoundTag("extendedProperties").setInteger("wheat", 12);
        assertFalse(snap.matches(s), "the same value written again is a new tag object: one extra send at most, never a missed one");
        snap.take(s);
        data(s).getCompoundTag("leftTank").setInteger("Amount", 3999);
        assertFalse(snap.matches(s), "tank amount");
        snap.take(s);
        data(s).setString("colorName", "Pig");
        assertFalse(snap.matches(s), "root key of the data");
        snap.take(s);
        data(s).removeTag("rightTank");
        assertFalse(snap.matches(s), "key removed");
        snap.take(s);
        data(s).setBoolean("newKey", true);
        assertFalse(snap.matches(s), "key added");
    }

    @Test
    void listChangesAreSeen()
    {
        ItemStack s = fullBackpack();
        StackSnapshot snap = taken(s);
        NBTTagList items = data(s).getTagList("ABPItems", 10);
        NBTTagList lore = items.getCompoundTagAt(8).getCompoundTag("tag").getCompoundTag("display").getTagList("Lore", 8);
        lore.func_150304_a(0, new NBTTagString("other"));
        assertTrue(snap.matches(s), "inside a list element: not walked (no gear writer edits elements in place)");
        items.getCompoundTagAt(5).setByte("Count", (byte) 63);
        data(s).setByte("quiverFill", (byte) 2);
        assertFalse(snap.matches(s), "takeFromQuiver edits an element and always writes the fill level");
        snap.take(s);
        items.removeTag(7);
        assertFalse(snap.matches(s), "element removed");
        snap.take(s);
        items.appendTag(new NBTTagCompound());
        assertFalse(snap.matches(s), "element appended");
        snap.take(s);
        items.func_150304_a(0, new NBTTagCompound());
        assertFalse(snap.matches(s), "element replaced");
        snap.take(s);
        NBTTagList names = new NBTTagList();
        names.appendTag(new NBTTagString("a"));
        data(s).setTag("names", names);
        snap.take(s);
        names.func_150304_a(0, new NBTTagString("b"));
        assertFalse(snap.matches(s), "string element replaced");
        snap.take(s);
        data(s).setTag("ABPItems", new NBTTagList());
        assertFalse(snap.matches(s), "list replaced");
    }

    @Test
    void readingDoesNotChangeIt()
    {
        ItemStack s = fullBackpack();
        StackSnapshot snap = taken(s);
        data(s).getCompoundTag("leftTank").getInteger("Amount");
        data(s).getTagList("ABPItems", 10).getCompoundTagAt(3).getByte("Count");
        s.stackTagCompound.hasKey("backpackData");
        assertTrue(snap.matches(s));
        snap.clear();
        assertFalse(snap.matches(s));
    }

    @Test
    void benchmark()
    {
        Assumptions.assumeTrue(System.getenv("AB_BENCH") != null, "set AB_BENCH=1 to measure");
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        ItemStack s = fullBackpack();
        ItemStack sent = ItemStack.copyItemStack(s);
        StackSnapshot snap = new StackSnapshot();
        snap.take(s);
        int n = 200_000;
        boolean sink = false;
        for (int round = 0; round < 3; round++)
        {
            long b0 = mx.getThreadAllocatedBytes(tid), t0 = System.nanoTime();
            for (int i = 0; i < n; i++) sink ^= ItemStack.areItemStacksEqual(s, sent);
            long t1 = System.nanoTime(), b1 = mx.getThreadAllocatedBytes(tid);
            for (int i = 0; i < n; i++) sink ^= snap.matches(s);
            long t2 = System.nanoTime(), b2 = mx.getThreadAllocatedBytes(tid);
            for (int i = 0; i < n / 10; i++) sent = ItemStack.copyItemStack(s);
            long t3 = System.nanoTime(), b3 = mx.getThreadAllocatedBytes(tid);
            for (int i = 0; i < n / 10; i++) snap.take(s);
            long t4 = System.nanoTime(), b4 = mx.getThreadAllocatedBytes(tid);
            System.out.printf("sync change test, full backpack, round %d: old deep compare %.0f ns %d B | snapshot %.0f ns %d B"
                            + " | on send: old copy %.0f ns %d B, snapshot take %.0f ns %d B%n",
                    round, (t1 - t0) / (double) n, (b1 - b0) / n, (t2 - t1) / (double) n, (b2 - b1) / n,
                    (t3 - t2) / (n / 10.0), (b3 - b2) / (n / 10), (t4 - t3) / (n / 10.0), (b4 - b3) / (n / 10));
        }
        System.out.println("sink " + sink);
    }
}
