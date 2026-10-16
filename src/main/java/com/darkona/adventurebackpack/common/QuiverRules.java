package com.darkona.adventurebackpack.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class QuiverRules
{
    public static final int LEVEL_EMPTY = 0;
    public static final int LEVEL_FEW = 1;
    public static final int LEVEL_SOME = 2;
    public static final int LEVEL_MANY = 3;
    private static final int FEW_MAX = 8;
    private static final int SOME_MAX = 32;
    private static final int[] ARROWS_DRAWN = {0, 1, 3, 5};
    public static final int MAX_ARROWS_DRAWN = 5;

    private QuiverRules() {}

    public static int level(int items)
    {
        if (items <= 0) return LEVEL_EMPTY;
        if (items <= FEW_MAX) return LEVEL_FEW;
        if (items <= SOME_MAX) return LEVEL_SOME;
        return LEVEL_MANY;
    }

    public static int arrowsDrawn(int level)
    {
        if (level <= LEVEL_EMPTY) return 0;
        return ARROWS_DRAWN[Math.min(level, LEVEL_MANY)];
    }

    public static int movedToLeft(boolean leftEmpty, boolean sameFluid, int leftAmount, int rightAmount, int capacity)
    {
        if (rightAmount <= 0) return 0;
        if (!leftEmpty && !sameFluid) return 0;
        int room = capacity - (leftEmpty ? 0 : leftAmount);
        return room <= 0 ? 0 : Math.min(room, rightAmount);
    }

    public static final class Filter
    {
        public static final Filter EMPTY = new Filter(Collections.<String>emptySet(), Collections.<String>emptySet(),
                Collections.<String>emptySet(), Collections.<String>emptySet());

        private final Set<String> anyMeta;
        private final Set<String> exactMeta;
        private final Set<String> ores;
        private final Set<String> rejected;

        private Filter(Set<String> anyMeta, Set<String> exactMeta, Set<String> ores, Set<String> rejected)
        {
            this.anyMeta = anyMeta;
            this.exactMeta = exactMeta;
            this.ores = ores;
            this.rejected = rejected;
        }

        public static Filter parse(String[] entries)
        {
            if (entries == null || entries.length == 0) return EMPTY;
            Set<String> any = new HashSet<String>();
            Set<String> exact = new HashSet<String>();
            Set<String> ores = new HashSet<String>();
            Set<String> bad = new HashSet<String>();
            for (String raw : entries)
            {
                if (raw == null || raw.trim().isEmpty()) continue;
                String text = raw.trim();
                BoatRecipeRules.Entry e = BoatRecipeRules.parse(text);
                if (e != null && e.source == BoatRecipeRules.Source.ORE)
                {
                    ores.add(e.name);
                } else if (e != null && e.source == BoatRecipeRules.Source.ITEM)
                {
                    String id = e.modId + ":" + e.name;
                    if (text.split(":").length == 4) exact.add(id + "@" + e.meta);
                    else any.add(id);
                } else if (e == null && text.split(":").length == 2 && !text.startsWith(":") && !text.endsWith(":"))
                {
                    any.add(text);
                } else
                {
                    bad.add(text);
                }
            }
            return new Filter(any, exact, ores, bad);
        }

        public String[] oreNames()
        {
            return ores.toArray(new String[ores.size()]);
        }

        public boolean usesOres()
        {
            return !ores.isEmpty();
        }

        public boolean accepts(String registryName, int meta, String[] oreNames)
        {
            if (registryName != null && (anyMeta.contains(registryName) || (!exactMeta.isEmpty() && exactMeta.contains(registryName + "@" + meta))))
            {
                return true;
            }
            if (oreNames != null)
            {
                for (String ore : oreNames)
                {
                    if (ores.contains(ore)) return true;
                }
            }
            return false;
        }

        public Set<String> rejected()
        {
            return Collections.unmodifiableSet(rejected);
        }
    }
}
