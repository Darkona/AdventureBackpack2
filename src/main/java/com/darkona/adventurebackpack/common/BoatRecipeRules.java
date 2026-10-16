package com.darkona.adventurebackpack.common;

import java.util.Locale;

public final class BoatRecipeRules
{
    public enum Mode
    {
        AUTO, VANILLA, TECHNICAL;

        public static final String[] NAMES = {"auto", "vanilla", "technical"};

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

    public enum Source
    {
        ORE,
        IC2,
        STACK,
        ITEM
    }

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

    public static boolean vanillaRecipe(Mode mode, boolean technicalFound)
    {
        return mode != Mode.TECHNICAL || !technicalFound;
    }

    public static boolean technicalRecipe(Mode mode, boolean technicalFound)
    {
        return mode != Mode.VANILLA && technicalFound;
    }

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
