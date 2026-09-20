package com.darkona.adventurebackpack.common;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * The one list of liquid fuels (fuels.list), as pure functions: every fuel user (Copter Pack, Fuel Station, boat motor, and later
 * the flamethrower) reads the same names and cost multipliers. A fuel is known by its registered
 * fluid name, never by a class of the mod that adds it, so no fuel mod is a dependency: BuildCraft {@code oil} and
 * {@code fuel}, Forestry {@code bioethanol} and {@code biomass}, Railcraft {@code creosote}, Thermal Foundation and
 * Railcraft {@code coal}, GregTech {@code fuel} (diesel) and {@code bioethanol}.
 */
public final class FuelRules
{
    private FuelRules() {}

    /** Default fuels.list: the old copterpack.fuels defaults plus Forestry biomass. */
    public static final String[] DEFAULTS = {"fuel=1.0", "oil=1.5", "bioethanol=1.2", "biofuel=1.0", "creosote=1.8", "coal=0.8", "biomass=1.5"};

    /** Default of the old copterpack.fuels key, to tell an untouched value from one the player changed. */
    public static final String[] OLD_COPTER_DEFAULTS = {"fuel=1.0", "oil=1.5", "bioethanol=1.2", "biofuel=1.0", "creosote=1.8", "coal=0.8"};

    /**
     * Reads "name=multiplier" entries. An entry without '=', with an empty name, or with a multiplier that is not a
     * positive number is skipped; a later entry for the same name wins.
     */
    public static Map<String, Float> parse(String[] entries)
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

    /** True when the fluid with this registered name is on the list. */
    public static boolean accepts(Map<String, Float> fuels, String fluidName)
    {
        return fluidName != null && fuels.containsKey(fluidName);
    }

    /**
     * True when at least one fuel of the list is a registered fluid: the fuel users (the Copter Pack) work with
     * whichever fuel mod is loaded, and are inert with none.
     */
    public static boolean anyRegistered(Collection<String> fuelNames, Set<String> registeredFluids)
    {
        if (fuelNames == null || registeredFluids == null) return false;
        for (String name : fuelNames)
        {
            if (name != null && registeredFluids.contains(name)) return true;
        }
        return false;
    }

    /** Cost multiplier of a fuel: 1 for a name that is not on the list (it is never burnt anyway). */
    public static float multiplier(Map<String, Float> fuels, String fluidName)
    {
        Float m = fluidName == null ? null : fuels.get(fluidName);
        return m == null ? 1.0f : m;
    }

    /**
     * Value of fuels.list on the first launch after the move from copterpack.fuels. Without an old value there is
     * nothing to move: null, and the new key takes its default. An old value equal to the old default (the player
     * never changed it) becomes the new default, so the player also gets the fuels added since. A value the player
     * changed is kept as it is.
     */
    public static String[] migrate(String[] oldValue)
    {
        if (oldValue == null) return null;
        if (sameEntries(oldValue, OLD_COPTER_DEFAULTS)) return DEFAULTS.clone();
        return oldValue.clone();
    }

    /** Same entries in the same order, ignoring spaces around each one. */
    static boolean sameEntries(String[] a, String[] b)
    {
        if (a.length != b.length) return false;
        String[] ta = trimmed(a);
        return Arrays.equals(ta, trimmed(b));
    }

    private static String[] trimmed(String[] values)
    {
        String[] out = new String[values.length];
        for (int i = 0; i < values.length; i++)
        {
            out[i] = values[i] == null ? null : values[i].replace(" ", "");
        }
        return out;
    }
}
