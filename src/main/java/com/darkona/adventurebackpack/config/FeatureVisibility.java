package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.inventory.AirContainers;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
import net.minecraft.item.Item;

public final class FeatureVisibility
{
    private FeatureVisibility() {}

    public static void hideDisabledItems()
    {
        hideUnless(ModItems.hose, ConfigHandler.HOSE_ENABLED);
        hideUnless(ModItems.copterPack, ConfigHandler.COPTERPACK_ENABLED);
        hideUnless(ModItems.steamJetpack, ConfigHandler.STEAMJETPACK_ENABLED && ModFluids.steam != null);
        hideUnless(ModItems.cwxbow, ConfigHandler.CROSSBOW_ENABLED);
        hideUnless(ModItems.boltCartridge, ConfigHandler.CROSSBOW_ENABLED);
        hideUnless(ModItems.bolt, ConfigHandler.CROSSBOW_ENABLED);
        hideUnless(ModItems.pistonBoots, ConfigHandler.PISTONBOOTS_ENABLED);
        if (!ConfigHandler.FUELSTATION_ENABLED) ModBlocks.blockFuelStation.setCreativeTab(null);
        hideUnless(ModItems.scubaGear, AirContainers.usable());
        if (!AirContainers.usable()) ModBlocks.blockAirCompressor.setCreativeTab(null);
        if (!ConfigHandler.DISTILLERY_ENABLED || ModFluids.ethanol == null) ModBlocks.blockDistillery.setCreativeTab(null);
        hideUnless(ModItems.flamethrowerPack, ItemFlamethrowerPack.isUsable());
        hideUnless(ModItems.flamethrower, ItemFlamethrowerPack.isUsable());
    }

    private static void hideUnless(Item item, boolean enabled)
    {
        if (!enabled) item.setCreativeTab(null);
    }
}
