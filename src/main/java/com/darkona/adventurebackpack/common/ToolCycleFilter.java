package com.darkona.adventurebackpack.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * The config list of items that may or may not go into the backpack's tool slots (tweaks.toolcycle, #49). Built once
 * when the config is read; the decision is pure, so the unit test covers it. The server runs the same check when a
 * cycle packet arrives, so a client with another list cannot put a forbidden item in the slots.
 * <p>
 * Entries are item registry names, {@code modid:name} (the names /give takes), or {@code modid:*} for every item of a
 * mod. A name without a mod id means {@code minecraft:}. Case does not matter.
 */
public final class ToolCycleFilter
{
    public enum Mode
    {
        /** Only the built-in rules (vanilla tools and the known tool names and mods). */
        NONE,
        /** Only the listed items, whatever the built-in rules say. */
        WHITELIST,
        /** The built-in rules, minus the listed items. */
        BLACKLIST;

        public static final String[] NAMES = {"none", "whitelist", "blacklist"};

        /** Reads a config value; anything unknown falls back to {@link #NONE}. */
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

    /** Builds the filter from the config entries; blank entries are skipped. */
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

    /** True when the item with this registry name is on the list. A null name (unregistered item) never is. */
    public boolean listed(String registryName)
    {
        if (registryName == null || (ids.isEmpty() && mods.isEmpty())) return false;
        String id = registryName.toLowerCase(Locale.ROOT);
        if (ids.contains(id)) return true;
        int colon = id.indexOf(':');
        return mods.contains(colon < 0 ? "minecraft" : id.substring(0, colon));
    }

    /**
     * The final word on an item that is not stackable, not a hose and not a backpack.
     *
     * @param listed  the item is on the config list
     * @param builtIn the built-in rules accept it as a tool
     */
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
