package com.darkona.adventurebackpack.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidTank;

public final class FluidMigration
{
    private static final String OLD_MUSHROOM = "mushroomsoup";
    private static final String NEW_MUSHROOM = "mushroomstew";

    private FluidMigration() {}

    public static String migrateName(String name)
    {
        return OLD_MUSHROOM.equalsIgnoreCase(name) ? NEW_MUSHROOM : name;
    }

    public static void migrate(NBTTagCompound fluidTag)
    {
        if (fluidTag != null && fluidTag.hasKey("FluidName", 8))
        {
            String old = fluidTag.getString("FluidName");
            String now = migrateName(old);
            if (!now.equals(old)) fluidTag.setString("FluidName", now);
        }
    }

    public static FluidTank readTank(FluidTank tank, NBTTagCompound fluidTag)
    {
        migrate(fluidTag);
        return tank.readFromNBT(fluidTag);
    }
}
