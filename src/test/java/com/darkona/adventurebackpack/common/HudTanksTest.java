package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.WeakHashMap;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;

import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.inventory.InventoryScubaGear;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

/**
 * The HUD draws the worn gear's tanks and the backpack's tools from the stack's RenderInfo now (audit P8) instead of
 * building the gear's inventory and reloading it every 5 ticks. These check that RenderInfo gives the same tanks, in
 * the same order and with the same capacities, as each inventory's getTanksArray, and the same tools. Tanks are empty
 * (a named fluid needs the FluidRegistry, which needs the running game). Set AB_BENCH=1 to print the cost of both ways.
 */
class HudTanksTest
{
    private static final Item TOOL = new Item();
    private static Throwable registryError;

    static
    {
        try
        {
            Item.itemRegistry.addObject(4101, "abtest:hudtool", TOOL);
        } catch (Throwable e)
        {
            registryError = e;
        }
    }

    private static ItemStack empty()
    {
        return new ItemStack((Item) null);
    }

    private static NBTTagCompound emptyTank()
    {
        NBTTagCompound t = new NBTTagCompound();
        t.setString("Empty", "");
        return t;
    }

    /** A gear stack whose tanks (under {@code dataKey}, or the root when null) are written as empty. */
    private static ItemStack gear(String dataKey, String... tanks)
    {
        NBTTagCompound root = new NBTTagCompound();
        NBTTagCompound data = dataKey == null ? root : new NBTTagCompound();
        for (String t : tanks) data.setTag(t, emptyTank());
        if (dataKey != null) root.setTag(dataKey, data);
        ItemStack stack = empty();
        stack.stackTagCompound = root;
        return stack;
    }

    private static void assertSameTanks(FluidTank[] tanks, RenderInfo info)
    {
        assertEquals(tanks.length, info.tankCount());
        for (int i = 0; i < tanks.length; i++)
        {
            assertEquals(tanks[i].getCapacity(), info.tankCapacity(i), "capacity of tank " + i);
            assertNull(tanks[i].getFluid());
            assertNull(info.tankFluid(i));
        }
    }

    @Test
    void sameTanksAsEveryGearInventory()
    {
        ItemStack backpack = empty();
        InventoryBackpack bp = new InventoryBackpack(backpack);
        assertSameTanks(bp.getTanksArray(), new RenderInfo().read(backpack.stackTagCompound, RenderInfo.KIND_BACKPACK));

        ItemStack jetpack = gear("jetpackData", "waterTank", "steamTank");
        InventorySteamJetpack jp = new InventorySteamJetpack(jetpack);
        jp.saveToNBT(jetpack.stackTagCompound);
        assertSameTanks(jp.getTanksArray(), new RenderInfo().read(jetpack.stackTagCompound, RenderInfo.KIND_JETPACK));

        ItemStack copter = gear(null, "fuelTank");
        InventoryCopterPack cp = new InventoryCopterPack(copter);
        cp.saveToNBT(copter.stackTagCompound);
        assertSameTanks(cp.getTanksArray(), new RenderInfo().read(copter.stackTagCompound, RenderInfo.KIND_COPTER));

        ItemStack scuba = gear(InventoryScubaGear.TAG, "leftTank", "rightTank");
        InventoryScubaGear sg = new InventoryScubaGear(scuba);
        sg.saveToNBT(scuba.stackTagCompound);
        assertSameTanks(sg.getTanksArray(), new RenderInfo().read(scuba.stackTagCompound, RenderInfo.KIND_SCUBA));

        ItemStack flamer = gear(InventoryFlamethrowerPack.TAG, "leftTank", "rightTank");
        InventoryFlamethrowerPack fp = new InventoryFlamethrowerPack(flamer);
        fp.saveToNBT(flamer.stackTagCompound);
        assertSameTanks(fp.getTanksArray(), new RenderInfo().read(flamer.stackTagCompound, RenderInfo.KIND_FLAMETHROWER));

        assertEquals(0, new RenderInfo().read(new NBTTagCompound(), RenderInfo.KIND_OTHER).tankCount(), "unknown gear: no HUD");
    }

    @Test
    void sameToolsAsTheBackpackInventory()
    {
        Assumptions.assumeTrue(Item.getIdFromItem(TOOL) > 0, "the item registry takes no test item here: " + registryError);
        ItemStack backpack = empty();
        InventoryBackpack bp = new InventoryBackpack(backpack);
        bp.setInventorySlotContentsNoSave(ClassicArrangement.UPPER_TOOL, new ItemStack(TOOL, 1, 3));
        bp.saveToNBT(backpack.stackTagCompound);
        RenderInfo info = new RenderInfo().read(backpack.stackTagCompound, RenderInfo.KIND_BACKPACK);
        assertNotNull(info.upperTool);
        assertSame(TOOL, info.upperTool.getItem());
        assertEquals(3, info.upperTool.getItemDamage());
        assertNull(info.lowerTool);
        assertSame(bp.getStackInSlot(ClassicArrangement.UPPER_TOOL).getItem(), info.upperTool.getItem());
    }

    @Test
    void scubaAndFlamethrowerAreReadAgainOnlyWhenATankIsWritten()
    {
        ItemStack scuba = gear(InventoryScubaGear.TAG, "leftTank", "rightTank");
        InventoryScubaGear sg = new InventoryScubaGear(scuba);
        sg.saveToNBT(scuba.stackTagCompound);
        NBTTagCompound root = scuba.stackTagCompound;
        RenderInfo info = new RenderInfo().read(root, RenderInfo.KIND_SCUBA);
        assertTrue(info.matches(root, RenderInfo.KIND_SCUBA));
        root.getCompoundTag(InventoryScubaGear.TAG).setTag("rightTank", emptyTank());
        assertFalse(info.matches(root, RenderInfo.KIND_SCUBA), "a tank written");
        info.read(root, RenderInfo.KIND_SCUBA);
        sg.saveToNBT(root); // a new data compound
        assertFalse(info.matches(root, RenderInfo.KIND_SCUBA));
        assertFalse(info.matches(root, RenderInfo.KIND_FLAMETHROWER), "another kind");

        ItemStack flamer = gear(InventoryFlamethrowerPack.TAG, "leftTank", "rightTank");
        InventoryFlamethrowerPack fp = new InventoryFlamethrowerPack(flamer);
        fp.saveToNBT(flamer.stackTagCompound);
        RenderInfo f = new RenderInfo().read(flamer.stackTagCompound, RenderInfo.KIND_FLAMETHROWER);
        assertTrue(f.matches(flamer.stackTagCompound, RenderInfo.KIND_FLAMETHROWER));
        flamer.stackTagCompound.getCompoundTag(InventoryFlamethrowerPack.TAG).setBoolean("on", true);
        assertTrue(f.matches(flamer.stackTagCompound, RenderInfo.KIND_FLAMETHROWER), "the switch is not drawn by the HUD");
        flamer.stackTagCompound.getCompoundTag(InventoryFlamethrowerPack.TAG).setTag("leftTank", emptyTank());
        assertFalse(f.matches(flamer.stackTagCompound, RenderInfo.KIND_FLAMETHROWER));
    }

    @Test
    void benchmark()
    {
        Assumptions.assumeTrue(System.getenv("AB_BENCH") != null, "set AB_BENCH=1 to measure");
        com.sun.management.ThreadMXBean mx = (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        long tid = Thread.currentThread().getId();
        ItemStack stack = empty();
        InventoryBackpack seed = new InventoryBackpack(stack);
        for (int i = 0; i < seed.getStorageSize(); i++) seed.setInventorySlotContentsNoSave(i, new ItemStack(TOOL, 64, i));
        seed.setInventorySlotContentsNoSave(ClassicArrangement.UPPER_TOOL, new ItemStack(TOOL, 1, 1));
        seed.setInventorySlotContentsNoSave(ClassicArrangement.LOWER_TOOL, new ItemStack(TOOL, 1, 2));
        seed.saveToNBT(stack.stackTagCompound);
        Map<ItemStack, RenderInfo> cache = new WeakHashMap<ItemStack, RenderInfo>();
        cache.put(stack, new RenderInfo().read(stack.stackTagCompound, RenderInfo.KIND_BACKPACK));
        int n = 200_000;
        int sink = 0;
        for (int round = 0; round < 3; round++)
        {
            long b0 = mx.getThreadAllocatedBytes(tid), t0 = System.nanoTime();
            for (int i = 0; i < n; i++) sink += new InventoryBackpack(stack).getTanksArray().length;
            long t1 = System.nanoTime(), b1 = mx.getThreadAllocatedBytes(tid);
            InventoryBackpack hud = new InventoryBackpack(stack);
            long b2 = mx.getThreadAllocatedBytes(tid), t2 = System.nanoTime();
            for (int i = 0; i < n; i++)
            {
                hud.openInventory();
                sink += hud.getTanksArray().length;
            }
            long t3 = System.nanoTime(), b3 = mx.getThreadAllocatedBytes(tid);
            for (int i = 0; i < n; i++)
            {
                RenderInfo info = cache.get(stack);
                if (!info.matches(stack.stackTagCompound, RenderInfo.KIND_BACKPACK)) info.read(stack.stackTagCompound, RenderInfo.KIND_BACKPACK);
                sink += info.tankCount() + info.tankCapacity(1);
            }
            long t4 = System.nanoTime(), b4 = mx.getThreadAllocatedBytes(tid);
            System.out.printf("HUD, full backpack, round %d: old rebuild per sync %.0f ns %d B | old reload every 5 ticks %.0f ns %d B"
                            + " | RenderInfo per frame %.1f ns %d B%n",
                    round, (t1 - t0) / (double) n, (b1 - b0) / n, (t3 - t2) / (double) n, (b3 - b2) / n,
                    (t4 - t3) / (double) n, (b4 - b3) / n);
        }
        System.out.println("sink " + sink);
    }
}
