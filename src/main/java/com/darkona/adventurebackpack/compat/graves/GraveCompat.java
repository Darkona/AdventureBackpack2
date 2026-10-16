package com.darkona.adventurebackpack.compat.graves;

import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.DamageSource;

import java.lang.reflect.Field;

public final class GraveCompat
{
    private static boolean openBlocksGraves = false;
    private static boolean graveStone = false;
    private static Field gsGeneratePlayerGraves;
    private static Field gsGraveItemsCount;
    private static Field gsGenerateGravesInLava;

    private GraveCompat()
    {
    }

    public static void init()
    {
        openBlocksGraves = Loader.isModLoaded("OpenBlocks") && GameRegistry.findBlock("OpenBlocks", "grave") != null;
        graveStone = Loader.isModLoaded("GraveStone");
        if (graveStone)
        {
            try
            {
                Class<?> cfg = Class.forName("gravestone.config.GraveStoneConfig");
                gsGeneratePlayerGraves = field(cfg, "generatePlayerGraves");
                gsGraveItemsCount = field(cfg, "graveItemsCount");
                gsGenerateGravesInLava = field(cfg, "generateGravesInLava");
            } catch (Throwable t)
            {
                LogHelper.info("GraveStone config not readable, assuming its graves are on: " + t);
            }
        }
        if (openBlocksGraves) LogHelper.info("OpenBlocks graves found: the worn wearable goes to the drop list on death.");
        if (graveStone) LogHelper.info("GraveStone found: the worn wearable goes to the inventory before the grave is made.");
    }

    private static Field field(Class<?> c, String name)
    {
        try
        {
            return c.getField(name);
        } catch (Throwable t)
        {
            return null;
        }
    }

    public static boolean hasDropsGrave()
    {
        return openBlocksGraves;
    }

    public static boolean graveStoneMakesGrave(DamageSource source)
    {
        if (!graveStone) return false;
        if (!readBool(gsGeneratePlayerGraves, true)) return false;
        if (readInt(gsGraveItemsCount, 1) <= 0) return false;
        boolean lava = source != null && "lava".equals(source.damageType);
        return !lava || readBool(gsGenerateGravesInLava, true);
    }

    private static boolean readBool(Field f, boolean def)
    {
        try
        {
            return f == null ? def : f.getBoolean(null);
        } catch (Throwable t)
        {
            return def;
        }
    }

    private static int readInt(Field f, int def)
    {
        try
        {
            return f == null ? def : f.getInt(null);
        } catch (Throwable t)
        {
            return def;
        }
    }
}
