package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.AirCompressorRules;
import com.darkona.adventurebackpack.common.CodeRegistrations;
import com.darkona.adventurebackpack.compat.tech.TechParts;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public final class AirContainers
{
    private AirContainers() {}

    private static final class AirItem
    {
        final Object full;
        final int amount;
        final Object empty;

        AirItem(Object full, int amount, Object empty)
        {
            this.full = full;
            this.amount = amount;
            this.empty = empty;
        }
    }

    private static AirItem[] airItems = new AirItem[0];
    private static Fluid[] breathable = new Fluid[0];
    private static String[] breathableNames;

    public static void init()
    {
        breathableNames = CodeRegistrations.mergeAirFluids(ConfigHandler.SCUBA_BREATHABLE_FLUIDS, CodeRegistrations.INSTANCE.airFluids());
        List<Fluid> fluids = new ArrayList<Fluid>();
        for (String name : breathableNames)
        {
            Fluid fluid = name == null ? null : FluidRegistry.getFluid(name.trim().toLowerCase());
            if (fluid != null && !fluids.contains(fluid)) fluids.add(fluid);
        }
        breathable = fluids.toArray(new Fluid[fluids.size()]);

        List<AirItem> items = new ArrayList<AirItem>();
        for (String text : ConfigHandler.SCUBA_AIR_ITEMS)
        {
            AirCompressorRules.AirItem entry = AirCompressorRules.parseAirItem(text);
            if (entry == null)
            {
                if (text != null && !text.trim().isEmpty()) LogHelper.error("Ignoring the malformed scuba.airItems entry '" + text + "'");
                continue;
            }
            Object full = first(TechParts.find(new String[]{entry.full}));
            if (full == null) continue;
            Object empty = entry.empty == null ? null : first(TechParts.find(new String[]{entry.empty}));
            items.add(new AirItem(full, entry.amount, empty));
        }
        airItems = items.toArray(new AirItem[items.size()]);
        LogHelper.info("Scuba Gear: air fluid " + (ModFluids.air != null ? "present" : "missing") + ", " + breathable.length
                + " breathable fluid(s), " + airItems.length + " air item(s) found.");
    }

    private static Object first(List<Object> found)
    {
        return found.isEmpty() ? null : found.get(0);
    }

    public static boolean usable()
    {
        return ConfigHandler.SCUBA_ENABLED && (ModFluids.air != null || breathable.length > 0);
    }

    public static boolean isBreathable(Fluid fluid)
    {
        return fluid != null && AirCompressorRules.isBreathable(fluid.getName(), breathableNames != null ? breathableNames : ConfigHandler.SCUBA_BREATHABLE_FLUIDS);
    }

    public static FluidStack toStored(FluidStack in)
    {
        if (in == null || in.getFluid() == null || in.amount <= 0 || !isBreathable(in.getFluid())) return null;
        return ModFluids.air != null ? new FluidStack(ModFluids.air, in.amount) : in.copy();
    }

    public static boolean isAirContainer(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return false;
        try
        {
            FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (contained != null) return isBreathable(contained.getFluid());
            if (FluidContainerRegistry.isEmptyContainer(stack))
            {
                if (ModFluids.air != null && FluidContainerRegistry.getContainerCapacity(new FluidStack(ModFluids.air, 1), stack) > 0) return true;
                for (Fluid fluid : breathable)
                {
                    if (FluidContainerRegistry.getContainerCapacity(new FluidStack(fluid, 1), stack) > 0) return true;
                }
            }
        } catch (RuntimeException foreign)
        {
            return false;
        }
        for (AirItem item : airItems)
        {
            if (matches(item.full, stack) || (item.empty != null && item.full instanceof ItemStack && matches(item.empty, stack))) return true;
        }
        return false;
    }

    public static boolean process(ItemStack[] slots, int in, int out, FluidTank tank)
    {
        ItemStack stack = slots[in];
        if (stack == null || stack.getItem() == null || tank == null) return false;
        try
        {
            FluidStack contained = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (contained != null)
            {
                FluidStack stored = toStored(contained);
                if (stored == null || tank.fill(stored, false) != stored.amount) return false;
                ItemStack emptied = FluidContainerRegistry.drainFluidContainer(stack);
                if (!fits(slots[out], emptied)) return false;
                tank.fill(stored, true);
                move(slots, in, out, emptied);
                return true;
            }
            for (AirItem item : airItems)
            {
                if (!matches(item.full, stack)) continue;
                if (ModFluids.air == null) return false;
                FluidStack stored = new FluidStack(ModFluids.air, item.amount);
                ItemStack emptied = item.empty instanceof ItemStack ? ((ItemStack) item.empty).copy() : null;
                if (tank.fill(stored, false) != item.amount || !fits(slots[out], emptied)) return false;
                tank.fill(stored, true);
                move(slots, in, out, emptied);
                return true;
            }
            FluidStack inTank = tank.getFluid();
            if (inTank == null || inTank.amount <= 0) return false;
            if (FluidContainerRegistry.isEmptyContainer(stack))
            {
                int capacity = FluidContainerRegistry.getContainerCapacity(inTank, stack);
                if (capacity <= 0 || inTank.amount < capacity) return false;
                ItemStack filled = FluidContainerRegistry.fillFluidContainer(new FluidStack(inTank.getFluid(), capacity), stack);
                if (filled == null || !fits(slots[out], filled)) return false;
                tank.drain(capacity, true);
                move(slots, in, out, filled);
                return true;
            }
            if (inTank.getFluid() != ModFluids.air) return false;
            for (AirItem item : airItems)
            {
                if (item.empty == null || !(item.full instanceof ItemStack) || !matches(item.empty, stack)) continue;
                ItemStack filled = ((ItemStack) item.full).copy();
                if (inTank.amount < item.amount || !fits(slots[out], filled)) return false;
                tank.drain(item.amount, true);
                move(slots, in, out, filled);
                return true;
            }
        } catch (RuntimeException foreign)
        {
            LogHelper.error("Could not move air through " + stack + ": " + foreign);
        }
        return false;
    }

    public static boolean processAll(ItemStack[] slots, int in, int out, FluidTank tank)
    {
        boolean moved = false;
        for (int i = 0; i < 64 && process(slots, in, out, tank); i++)
        {
            moved = true;
        }
        return moved;
    }

    private static boolean matches(Object part, ItemStack stack)
    {
        if (part instanceof ItemStack) return OreDictionary.itemMatches((ItemStack) part, stack, false);
        if (part instanceof String)
        {
            int id = OreDictionary.getOreID((String) part);
            for (int have : OreDictionary.getOreIDs(stack))
            {
                if (have == id) return true;
            }
        }
        return false;
    }

    private static boolean fits(ItemStack inOut, ItemStack result)
    {
        if (result == null || inOut == null) return true;
        return inOut.isItemEqual(result) && ItemStack.areItemStackTagsEqual(inOut, result)
                && inOut.stackSize + 1 <= inOut.getMaxStackSize();
    }

    private static void move(ItemStack[] slots, int in, int out, ItemStack result)
    {
        if (--slots[in].stackSize <= 0) slots[in] = null;
        if (result == null) return;
        if (slots[out] == null)
        {
            ItemStack one = result.copy();
            one.stackSize = 1;
            slots[out] = one;
        } else
        {
            slots[out].stackSize++;
        }
    }
}
