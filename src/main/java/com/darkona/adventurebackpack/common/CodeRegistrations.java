package com.darkona.adventurebackpack.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.hash.TObjectFloatHashMap;
import java.util.Set;

public final class CodeRegistrations
{
    public static final CodeRegistrations INSTANCE = new CodeRegistrations();

    private final TObjectFloatHashMap<String> fuels = new TObjectFloatHashMap<String>();
    private final Set<String> airFluids = new LinkedHashSet<String>();
    private volatile boolean frozen;

    public synchronized void addFuel(String fluidName, double multiplier)
    {
        String name = normalize(fluidName, "fuel");
        float value = (float) multiplier;
        if (!(value > 0) || Float.isInfinite(value) || Float.isNaN(value))
        {
            throw new IllegalArgumentException("The multiplier of the fuel " + name + " must be a positive finite number: " + multiplier);
        }
        checkOpen("fuel " + name);
        if (!fuels.containsKey(name)) fuels.put(name, value);
    }

    public synchronized void addAirFluid(String fluidName)
    {
        String name = normalize(fluidName, "air fluid");
        checkOpen("air fluid " + name);
        airFluids.add(name);
    }

    public void freeze()
    {
        frozen = true;
    }

    public boolean isFrozen()
    {
        return frozen;
    }

    public synchronized TObjectFloatHashMap<String> fuels()
    {
        return new TObjectFloatHashMap<String>(fuels);
    }

    public synchronized List<String> airFluids()
    {
        return new ArrayList<String>(airFluids);
    }

    public static TObjectFloatHashMap<String> mergeFuels(TObjectFloatHashMap<String> fromConfig, TObjectFloatHashMap<String> fromCode)
    {
        TObjectFloatHashMap<String> merged = new TObjectFloatHashMap<String>(fromConfig);
        for (TObjectFloatIterator<String> e = fromCode.iterator(); e.hasNext(); )
        {
            e.advance();
            if (!merged.containsKey(e.key())) merged.put(e.key(), e.value());
        }
        return merged;
    }

    public static String[] mergeAirFluids(String[] fromConfig, Collection<String> fromCode)
    {
        List<String> merged = new ArrayList<String>();
        if (fromConfig != null)
        {
            for (String name : fromConfig)
            {
                if (name != null) merged.add(name);
            }
        }
        for (String name : fromCode)
        {
            boolean known = false;
            for (String have : merged)
            {
                if (have.trim().equalsIgnoreCase(name)) { known = true; break; }
            }
            if (!known) merged.add(name);
        }
        return merged.toArray(new String[merged.size()]);
    }

    private void checkOpen(String what)
    {
        if (frozen)
        {
            throw new IllegalStateException("Cannot register the " + what
                    + ": the tables are frozen (register in preInit or init, before the mod's postInit starts)");
        }
    }

    private static String normalize(String fluidName, String what)
    {
        String name = fluidName == null ? "" : fluidName.trim().toLowerCase(Locale.ENGLISH);
        if (name.isEmpty()) throw new IllegalArgumentException("The name of the " + what + " is empty");
        return name;
    }
}
