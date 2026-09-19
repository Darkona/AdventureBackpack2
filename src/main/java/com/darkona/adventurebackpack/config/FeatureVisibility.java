package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.inventory.AirContainers;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.ItemCopterPack;
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
        // Without BuildCraft (its fuels) the copter pack is inert, like a disabled one
        hideUnless(ModItems.copterPack, ItemCopterPack.isUsable());
        // Without any steam fluid (no other mod and ownSteamFluid off) the jetpack cannot work
        hideUnless(ModItems.steamJetpack, ConfigHandler.STEAMJETPACK_ENABLED && ModFluids.steam != null);
        hideUnless(ModItems.cwxbow, ConfigHandler.CROSSBOW_ENABLED);
        hideUnless(ModItems.boltCartridge, ConfigHandler.CROSSBOW_ENABLED);
        hideUnless(ModItems.pistonBoots, ConfigHandler.PISTONBOOTS_ENABLED);
        // Without any breathable fluid (no GregTech air and ownAirFluid off, no oxygen) the gear cannot work
        hideUnless(ModItems.scubaGear, AirContainers.usable());
        if (!AirContainers.usable()) ModBlocks.blockAirCompressor.setCreativeTab(null);
    }

    private static void hideUnless(Item item, boolean enabled)
    {
        if (!enabled) item.setCreativeTab(null);
    }
}
