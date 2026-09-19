package com.darkona.adventurebackpack.init;


import com.darkona.adventurebackpack.block.*;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ModBlocks
{

    public static BlockAdventureBackpack blockBackpack = new BlockAdventureBackpack();
    public static BlockSleepingBag blockSleepingBag = new BlockSleepingBag();
    public static BlockCampFire blockCampFire = new BlockCampFire();
    public static BlockAirCompressor blockAirCompressor = new BlockAirCompressor();

    public static void init()
    {
        GameRegistry.registerBlock(blockBackpack, "blockBackpack");
        GameRegistry.registerBlock(blockSleepingBag, "blockSleepingBag");
        GameRegistry.registerBlock(blockCampFire, "blockCampFire");
        // Always registered; hidden, without recipe and inert with enabled.scuba off
        GameRegistry.registerBlock(blockAirCompressor, "blockAirCompressor");

        GameRegistry.registerTileEntity(TileCampfire.class, "tileCampFire");
        GameRegistry.registerTileEntity(TileAdventureBackpack.class, "adventureBackpackTileEntity");
        GameRegistry.registerTileEntity(TileAirCompressor.class, "tileAirCompressor");
    }

}
