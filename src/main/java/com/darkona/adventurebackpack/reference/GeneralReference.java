package com.darkona.adventurebackpack.reference;

import net.minecraftforge.fluids.Fluid;

import com.darkona.adventurebackpack.config.ConfigHandler;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created on 16/01/2015
 *
 * @author Darkona
 */
public class GeneralReference
{
    public static HashMap<String, Float> liquidFuels = new HashMap<String, Float>();


    /** Copter fuels and their cost multipliers, from the copterpack.fuels config key. */
    public static void init()
    {
        liquidFuels.clear();
        liquidFuels.putAll(parseFuels(ConfigHandler.COPTER_FUELS));
    }

    /**
     * Reads "name=multiplier" entries. An entry without '=', with an empty name, or with a multiplier that is not a
     * positive number is skipped; a later entry for the same name wins.
     */
    public static Map<String, Float> parseFuels(String[] entries)
    {
        Map<String, Float> fuels = new LinkedHashMap<String, Float>();
        if (entries == null) return fuels;
        for (String entry : entries)
        {
            if (entry == null) continue;
            int eq = entry.indexOf('=');
            if (eq <= 0) continue;
            String name = entry.substring(0, eq).trim();
            if (name.isEmpty()) continue;
            try
            {
                float multiplier = Float.parseFloat(entry.substring(eq + 1).trim());
                if (multiplier > 0 && !Float.isInfinite(multiplier)) fuels.put(name, multiplier);
            } catch (NumberFormatException ignored)
            {
                // a wrong number in the config: skip the entry
            }
        }
        return fuels;
    }

    public static boolean isValidFuel(Fluid fluid)
    {
        return fluid != null && liquidFuels.containsKey(fluid.getName());
    }
}
