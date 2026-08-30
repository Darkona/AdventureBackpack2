package com.darkona.adventurebackpack.common;

import java.util.Locale;

/**
 * Which Inflatable Boat recipes are registered (recipes.boat.mode), as pure decisions. Each technical part (rubber for
 * the chambers, an electric motor for the motorized boat) is decided on its own, by whether another mod provides it.
 * <ul>
 * <li>vanilla: only the vanilla recipes (leather and slime chambers, a piston and furnace engine).</li>
 * <li>auto: the vanilla recipes, plus a rubber or motor variant for each part another mod provides.</li>
 * <li>technical: the rubber or motor variant replaces the vanilla recipe of each part another mod provides; a part
 * that no loaded mod provides keeps its vanilla recipe, so the boat can always be made.</li>
 * </ul>
 */
public final class BoatRecipeRules
{
    public enum Mode
    {
        AUTO, VANILLA, TECHNICAL;

        public static final String[] NAMES = {"auto", "vanilla", "technical"};

        /** Unknown text falls back to auto. */
        public static Mode parse(String text)
        {
            if (text != null)
            {
                String t = text.trim().toLowerCase(Locale.ROOT);
                if (t.equals("vanilla")) return VANILLA;
                if (t.equals("technical")) return TECHNICAL;
            }
            return AUTO;
        }
    }

    /** Kind of a recipes.boat.motors entry. */
    public enum Source
    {
        /** {@code ore:name}: an Ore Dictionary name. */
        ORE,
        /** {@code ic2:name}: an IC2 item through its API (IC2Items.getItem). */
        IC2,
        /** {@code stack:modid:name}: an item stack a mod registered by name (GameRegistry.findItemStack). */
        STACK,
        /** {@code item:modid:name} or {@code item:modid:name:meta}: a registered item, with an optional damage value. */
        ITEM
    }

    /** One parsed entry: kind, mod id (null for ore and ic2), name and damage (0 when not given). */
    public static final class Entry
    {
        public final Source source;
        public final String modId;
        public final String name;
        public final int meta;

        Entry(Source source, String modId, String name, int meta)
        {
            this.source = source;
            this.modId = modId;
            this.name = name;
            this.meta = meta;
        }
    }

    private BoatRecipeRules() {}

    /** True when the vanilla recipe of a part is registered. */
    public static boolean vanillaRecipe(Mode mode, boolean technicalFound)
    {
        return mode != Mode.TECHNICAL || !technicalFound;
    }

    /** True when the technical variant of a part is registered. */
    public static boolean technicalRecipe(Mode mode, boolean technicalFound)
    {
        return mode != Mode.VANILLA && technicalFound;
    }

    /** Parses one recipes.boat.motors entry, or returns null when it is malformed. */
    public static Entry parse(String text)
    {
        if (text == null) return null;
        String[] p = text.trim().split(":");
        if (p.length < 2) return null;
        String kind = p[0].toLowerCase(Locale.ROOT);
        if (kind.equals("ore") && p.length == 2 && !p[1].isEmpty()) return new Entry(Source.ORE, null, p[1], 0);
        if (kind.equals("ic2") && p.length == 2 && !p[1].isEmpty()) return new Entry(Source.IC2, null, p[1], 0);
        if (kind.equals("stack") && p.length == 3 && !p[1].isEmpty() && !p[2].isEmpty()) return new Entry(Source.STACK, p[1], p[2], 0);
        if (kind.equals("item") && (p.length == 3 || p.length == 4) && !p[1].isEmpty() && !p[2].isEmpty())
        {
            int meta = 0;
            if (p.length == 4)
            {
                try
                {
                    meta = Integer.parseInt(p[3]);
                } catch (NumberFormatException e)
                {
                    return null;
                }
                if (meta < 0) return null;
            }
            return new Entry(Source.ITEM, p[1], p[2], meta);
        }
        return null;
    }
}
