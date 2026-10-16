package com.darkona.adventurebackpack.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public final class ToolCycleFilter
{
    public enum Mode
    {
        NONE,
        WHITELIST,
        BLACKLIST;

        public static final String[] NAMES = {"none", "whitelist", "blacklist"};

        public static Mode parse(String text)
        {
            if (text != null)
            {
                String t = text.trim();
                for (Mode m : values())
                {
                    if (m.name().equalsIgnoreCase(t)) return m;
                }
            }
            return NONE;
        }
    }

    public static final ToolCycleFilter EMPTY = new ToolCycleFilter(Collections.<String>emptySet(), Collections.<String>emptySet());

    private final Set<String> ids;
    private final Set<String> mods;

    private ToolCycleFilter(Set<String> ids, Set<String> mods)
    {
        this.ids = ids;
        this.mods = mods;
    }

    public static ToolCycleFilter parse(String[] entries)
    {
        if (entries == null || entries.length == 0) return EMPTY;
        Set<String> ids = new HashSet<String>();
        Set<String> mods = new HashSet<String>();
        for (String entry : entries)
        {
            if (entry == null) continue;
            String e = entry.trim().toLowerCase(Locale.ROOT);
            if (e.isEmpty()) continue;
            int colon = e.indexOf(':');
            if (colon < 0)
            {
                ids.add("minecraft:" + e);
            } else if (e.substring(colon + 1).equals("*"))
            {
                mods.add(e.substring(0, colon));
            } else
            {
                ids.add(e);
            }
        }
        return new ToolCycleFilter(ids, mods);
    }

    public boolean listed(String registryName)
    {
        if (registryName == null || (ids.isEmpty() && mods.isEmpty())) return false;
        String id = registryName.toLowerCase(Locale.ROOT);
        if (ids.contains(id)) return true;
        int colon = id.indexOf(':');
        return mods.contains(colon < 0 ? "minecraft" : id.substring(0, colon));
    }

    public static boolean allows(Mode mode, boolean listed, boolean builtIn)
    {
        switch (mode)
        {
            case WHITELIST:
                return listed;
            case BLACKLIST:
                return builtIn && !listed;
            default:
                return builtIn;
        }
    }
}
