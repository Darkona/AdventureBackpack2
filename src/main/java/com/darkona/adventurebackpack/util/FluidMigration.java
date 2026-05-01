package com.darkona.adventurebackpack.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

/**
 * Rewrites fluid ids saved by older versions so stored tanks keep their content.
 * The mushroom stew fluid was called "mushroomsoup" up to 0.8c and is "mushroomstew" now.
 */
public final class FluidMigration
{
    private static final String OLD_MUSHROOM = "mushroomsoup";
    private static final String NEW_MUSHROOM = "mushroomstew";

    private FluidMigration() {}

    /** Pure: the current id for a saved fluid name (null stays null, unknown names stay as they are). */
    public static String migrateName(String name)
    {
        return OLD_MUSHROOM.equalsIgnoreCase(name) ? NEW_MUSHROOM : name;
    }

    /** Rewrites the "FluidName" of a saved FluidStack tag in place. */
    public static void migrate(NBTTagCompound fluidTag)
    {
        if (fluidTag != null && fluidTag.hasKey("FluidName", 8))
        {
            String old = fluidTag.getString("FluidName");
            String now = migrateName(old);
            if (!now.equals(old)) fluidTag.setString("FluidName", now);
        }
    }

    /** Migrates the saved tag and then loads it into the tank. */
    public static FluidTank readTank(FluidTank tank, NBTTagCompound fluidTag)
    {
        migrate(fluidTag);
        return tank.readFromNBT(fluidTag);
    }
}
