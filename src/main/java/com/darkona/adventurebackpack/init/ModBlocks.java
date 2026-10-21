package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.block.*;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModBlocks
{
    public static BlockAdventureBackpack blockBackpack = new BlockAdventureBackpack();
    public static BlockSleepingBag blockSleepingBag = new BlockSleepingBag();
    public static BlockCampFire blockCampFire = new BlockCampFire();
    public static BlockFuelStation blockFuelStation = new BlockFuelStation();
    public static BlockAirCompressor blockAirCompressor = new BlockAirCompressor();
    public static BlockDistillery blockDistillery = new BlockDistillery();
    public static BlockGear blockGear = new BlockGear();
    public static BlockRope blockRope = new BlockRope();

    public static void init()
    {
        GameRegistry.registerBlock(blockBackpack, "blockBackpack");
        GameRegistry.registerBlock(blockSleepingBag, "blockSleepingBag");
        GameRegistry.registerBlock(blockCampFire, "blockCampFire");
        GameRegistry.registerBlock(blockFuelStation, ItemBlockStation.class, "blockFuelStation");
        GameRegistry.registerBlock(blockAirCompressor, "blockAirCompressor");
        GameRegistry.registerBlock(blockDistillery, "blockDistillery");
        GameRegistry.registerBlock(blockGear, null, "blockGear");
        GameRegistry.registerBlock(blockRope, null, "blockRope");

        GameRegistry.registerTileEntity(TileCampfire.class, "tileCampFire");
        GameRegistry.registerTileEntity(TileAdventureBackpack.class, "adventureBackpackTileEntity");
        GameRegistry.registerTileEntity(TileFuelStation.class, "tileFuelStation");
        GameRegistry.registerTileEntity(TileAirCompressor.class, "tileAirCompressor");
        GameRegistry.registerTileEntity(TileDistillery.class, "tileDistillery");
        GameRegistry.registerTileEntity(TileGear.class, "tileGear");
    }
}
