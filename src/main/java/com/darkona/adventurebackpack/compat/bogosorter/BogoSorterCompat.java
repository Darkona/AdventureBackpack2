package com.darkona.adventurebackpack.compat.bogosorter;

import cpw.mods.fml.common.Loader;

public final class BogoSorterCompat
{
    private static boolean active;

    private BogoSorterCompat()
    {
    }

    public static void init()
    {
        if (!Loader.isModLoaded("bogosorter")) return;
        Registration.install();
        active = true;
    }

    public static void locksChanged()
    {
        if (active) Registration.refresh();
    }

    private static final class Registration
    {
        static void install()
        {
            BackpackGroups.register();
        }

        static void refresh()
        {
            BackpackGroups.invalidate();
        }
    }
}
