package com.darkona.adventurebackpack.common;

import java.util.Arrays;
import java.util.Collection;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.Set;

public final class FuelRules
{
    private FuelRules() {}

    public static final String[] DEFAULTS = {"fuel=1.0", "oil=1.5", "bioethanol=1.2", "biofuel=1.0", "creosote=1.8", "coal=0.8", "biomass=1.5"};

    public static final String[] OLD_COPTER_DEFAULTS = {"fuel=1.0", "oil=1.5", "bioethanol=1.2", "biofuel=1.0", "creosote=1.8", "coal=0.8"};

    public static TObjectFloatHashMap<String> parse(String[] entries)
    {
        TObjectFloatHashMap<String> fuels = new TObjectFloatHashMap<String>();
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
            }
        }
        return fuels;
    }

    public static boolean accepts(TObjectFloatHashMap<String> fuels, String fluidName)
    {
        return fluidName != null && fuels.containsKey(fluidName);
    }

    public static boolean anyRegistered(Collection<String> fuelNames, Set<String> registeredFluids)
    {
        if (fuelNames == null || registeredFluids == null) return false;
        for (String name : fuelNames)
        {
            if (name != null && registeredFluids.contains(name)) return true;
        }
        return false;
    }

    public static float multiplier(TObjectFloatHashMap<String> fuels, String fluidName)
    {
        return fluidName == null || !fuels.containsKey(fluidName) ? 1.0f : fuels.get(fluidName);
    }

    public static void addIfMissing(TObjectFloatHashMap<String> fuels, String fluidName, float multiplier)
    {
        if (fuels == null || fluidName == null || fluidName.trim().isEmpty()) return;
        if (!(multiplier > 0) || Float.isInfinite(multiplier)) return;
        String name = fluidName.trim();
        if (!fuels.containsKey(name)) fuels.put(name, multiplier);
    }

    public static String[] migrate(String[] oldValue)
    {
        if (oldValue == null) return null;
        if (sameEntries(oldValue, OLD_COPTER_DEFAULTS)) return DEFAULTS.clone();
        return oldValue.clone();
    }

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
