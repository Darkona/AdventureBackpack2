package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.init.ModItems;
import net.minecraft.item.Item;

/**
 * Hides the items of disabled systems from the creative tab. The items stay registered, so a client and a server
 * with different config files can still join each other; the items themselves check their switch and do nothing.
 */
public final class FeatureVisibility
{
    private FeatureVisibility() {}

    /** Called once in postInit, after every item is registered. */
    public static void hideDisabledItems()
    {
        hideUnless(ModItems.hose, ConfigHandler.HOSE_ENABLED);
        hideUnless(ModItems.copterPack, ConfigHandler.COPTERPACK_ENABLED);
        hideUnless(ModItems.steamJetpack, ConfigHandler.STEAMJETPACK_ENABLED);
        hideUnless(ModItems.cwxbow, ConfigHandler.CROSSBOW_ENABLED);
        hideUnless(ModItems.boltCartridge, ConfigHandler.CROSSBOW_ENABLED);
        hideUnless(ModItems.pistonBoots, ConfigHandler.PISTONBOOTS_ENABLED);
    }

    private static void hideUnless(Item item, boolean enabled)
    {
        if (!enabled) item.setCreativeTab(null);
    }
}
