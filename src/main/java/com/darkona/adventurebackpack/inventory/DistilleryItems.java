package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.DistilleryRules;
import com.darkona.adventurebackpack.compat.tech.TechParts;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.List;

public final class DistilleryItems
{
    private DistilleryItems() {}

    private static final class Entry
    {
        final Object part;
        final int amount;

        Entry(Object part, int amount)
        {
            this.part = part;
            this.amount = amount;
        }
    }

    private static Entry[] organics = new Entry[0];
    private static Entry[] catalysts = new Entry[0];
    private static Entry[] solidFuels = new Entry[0];
    private static ItemStack catalystHint;

    public static void init()
    {
        organics = resolve(ConfigHandler.DISTILLERY_ORGANICS, true, "distillery.organics");
        catalysts = resolve(ConfigHandler.DISTILLERY_CATALYSTS, false, "distillery.catalysts");
        solidFuels = resolve(ConfigHandler.DISTILLERY_SOLID_FUELS, true, "distillery.solidFuels");
        catalystHint = null;
        for (Entry e : catalysts)
        {
            catalystHint = sample(e.part);
            if (catalystHint != null) break;
        }
        LogHelper.info("Distillery: " + organics.length + " organic(s), " + catalysts.length + " catalyst(s), "
                + solidFuels.length + " solid fuel(s) found.");
    }

    private static Entry[] resolve(String[] lines, boolean withAmount, String key)
    {
        List<Entry> found = new ArrayList<Entry>();
        if (lines == null) return new Entry[0];
        for (String text : lines)
        {
            if (text == null || text.trim().isEmpty()) continue;
            String item;
            int amount;
            if (withAmount)
            {
                DistilleryRules.Amount parsed = DistilleryRules.parseAmount(text);
                if (parsed == null)
                {
                    LogHelper.error("Ignoring the malformed " + key + " entry '" + text + "'");
                    continue;
                }
                item = parsed.item;
                amount = parsed.amount;
            } else
            {
                item = text.trim();
                amount = 1;
            }
            List<Object> parts = TechParts.find(new String[]{item});
            if (parts.isEmpty()) continue;
            Object part = parts.get(0);
            if (part instanceof ItemStack && DistilleryRules.anyDamage(item))
            {
                ItemStack any = ((ItemStack) part).copy();
                any.setItemDamage(OreDictionary.WILDCARD_VALUE);
                part = any;
            }
            found.add(new Entry(part, amount));
        }
        return found.toArray(new Entry[found.size()]);
    }

    private static ItemStack sample(Object part)
    {
        ItemStack stack = null;
        if (part instanceof ItemStack) stack = ((ItemStack) part).copy();
        else if (part instanceof String)
        {
            List<ItemStack> ores = OreDictionary.getOres((String) part);
            if (!ores.isEmpty()) stack = ores.get(0).copy();
        }
        if (stack == null || stack.getItem() == null) return null;
        if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) stack.setItemDamage(0);
        stack.stackSize = 1;
        return stack;
    }

    private static int find(Entry[] entries, ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return 0;
        try
        {
            for (Entry e : entries)
            {
                if (matches(e.part, stack)) return e.amount;
            }
        } catch (RuntimeException foreign)
        {
        }
        return 0;
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

    public static int organicYield(ItemStack stack)
    {
        return find(organics, stack);
    }

    public static boolean isCatalyst(ItemStack stack)
    {
        return find(catalysts, stack) > 0;
    }

    public static ItemStack catalystHint()
    {
        return catalystHint;
    }

    public static int burnTime(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return 0;
        int listed = find(solidFuels, stack);
        if (listed > 0) return listed;
        if (ConfigHandler.DISTILLERY_FURNACE_FUEL_MULTIPLIER <= 0) return 0;
        try
        {
            int furnace = TileEntityFurnace.getItemBurnTime(stack);
            return furnace <= 0 ? 0 : (int) Math.min(Integer.MAX_VALUE, Math.round(furnace * ConfigHandler.DISTILLERY_FURNACE_FUEL_MULTIPLIER));
        } catch (RuntimeException foreign)
        {
            return 0;
        }
    }

    public static boolean isWaterContainer(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null) return false;
        try
        {
            FluidStack inside = FluidContainerRegistry.getFluidForFilledItem(stack);
            return inside != null && inside.getFluid() == FluidRegistry.WATER;
        } catch (RuntimeException foreign)
        {
            return false;
        }
    }
}
