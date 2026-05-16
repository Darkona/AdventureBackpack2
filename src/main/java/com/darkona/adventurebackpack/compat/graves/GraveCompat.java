package com.darkona.adventurebackpack.compat.graves;

import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.util.DamageSource;

import java.lang.reflect.Field;

/**
 * Finds the grave mods of 1.7.10 once, in postInit, without loading any of their classes when they are absent
 * and without a compile dependency on them.
 * <ul>
 * <li>OpenBlocks: its {@code PlayerDeathHandler} reads {@code PlayerDropsEvent.drops} at LOW priority and
 * places the grave one tick later on an air or replaceable block, so it never overwrites a placed backpack.
 * It is active when its grave block is registered (OpenBlocks can disable it).</li>
 * <li>GraveStone (NightKosh): {@code GSEventsHandler} takes {@code player.inventory} (main and armor) and the
 * slots of the mods it knows in {@code LivingDeathEvent} at HIGH priority. It does not read the drop list
 * and does not know the worn wearable, so the wearable goes into the main inventory first. Its switches are
 * static fields of {@code gravestone.config.GraveStoneConfig}, read here by reflection.</li>
 * <li>Corail Tombstone has no 1.7.10 version.</li>
 * </ul>
 *
 * @author Javier Darkona
 */
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

    /** postInit: Tier 1. */
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

    /** A grave mod that reads {@code PlayerDropsEvent.drops} is active. */
    public static boolean hasDropsGrave()
    {
        return openBlocksGraves;
    }

    /**
     * GraveStone is loaded and its own config will make a grave for this death. Tier 2 (once per death).
     * The restricted areas of GraveStone are not checked: there the backpack drops with the inventory, it is not lost.
     */
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
