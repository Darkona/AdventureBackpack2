package com.darkona.adventurebackpack.compat.tech;

import com.darkona.adventurebackpack.common.BoatRecipeRules;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.oredict.OreDictionary;

/**
 * Finds the parts other mods provide for recipes (rubber, electric motors) without depending on them: the Ore
 * Dictionary, items looked up by name only when their mod is loaded, and the IC2 API through reflection. Called once
 * in postInit, when every mod has registered its items and ores.
 */
public final class TechParts
{
    private TechParts() {}

    /** The Ore Dictionary names of the list that some loaded mod filled. */
    public static List<String> presentOres(String[] names)
    {
        List<String> found = new ArrayList<String>();
        for (String name : names)
        {
            String n = name == null ? "" : name.trim();
            if (!n.isEmpty() && !OreDictionary.getOres(n).isEmpty() && !found.contains(n)) found.add(n);
        }
        return found;
    }

    /**
     * The ingredients the entries point at and that exist in this game: an Ore Dictionary name (String) or an
     * ItemStack. Malformed entries are logged and skipped.
     */
    public static List<Object> find(String[] entries)
    {
        List<Object> found = new ArrayList<Object>();
        for (String text : entries)
        {
            BoatRecipeRules.Entry entry = BoatRecipeRules.parse(text);
            if (entry == null)
            {
                if (text != null && !text.trim().isEmpty()) LogHelper.error("Ignoring the malformed recipe part '" + text + "'");
                continue;
            }
            Object ingredient = resolve(entry);
            if (ingredient != null) found.add(ingredient);
        }
        return found;
    }

    private static Object resolve(BoatRecipeRules.Entry entry)
    {
        try
        {
            switch (entry.source)
            {
                case ORE:
                    return OreDictionary.getOres(entry.name).isEmpty() ? null : entry.name;
                case IC2:
                    return Loader.isModLoaded("IC2") ? ic2Item(entry.name) : null;
                case STACK:
                    if (!Loader.isModLoaded(entry.modId)) return null;
                    ItemStack stack = GameRegistry.findItemStack(entry.modId, entry.name, 1);
                    return stack != null && stack.getItem() != null ? stack : null;
                case ITEM:
                    if (!Loader.isModLoaded(entry.modId)) return null;
                    Item item = GameRegistry.findItem(entry.modId, entry.name);
                    return item != null ? new ItemStack(item, 1, entry.meta) : null;
                default:
                    return null;
            }
        } catch (RuntimeException oops)
        {
            LogHelper.error("Could not look up the recipe part " + entry.source + " " + entry.name + ": " + oops);
            return null;
        }
    }

    /** ic2.api.item.IC2Items.getItem(name), by reflection so IC2 is not needed to build or run. */
    private static ItemStack ic2Item(String name)
    {
        try
        {
            Method getItem = Class.forName("ic2.api.item.IC2Items").getMethod("getItem", String.class);
            Object stack = getItem.invoke(null, name);
            if (stack instanceof ItemStack && ((ItemStack) stack).getItem() != null)
            {
                ItemStack copy = ((ItemStack) stack).copy();
                copy.stackSize = 1;
                return copy;
            }
        } catch (Exception | LinkageError oops)
        {
            LogHelper.error("IC2 is loaded but its item '" + name + "' could not be read: " + oops);
        }
        return null;
    }
}
