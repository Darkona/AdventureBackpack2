package com.darkona.adventurebackpack.init;


import cpw.mods.fml.common.registry.GameRegistry;

import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.BlockCampFire;
import com.darkona.adventurebackpack.block.BlockSleepingBag;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.block.TileCampfire;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ModBlocks
{

    public static BlockAdventureBackpack blockBackpack = new BlockAdventureBackpack();
    public static BlockSleepingBag blockSleepingBag = new BlockSleepingBag();
    public static BlockCampFire blockCampFire = new BlockCampFire();

    public static void init()
    {
        GameRegistry.registerBlock(blockBackpack, "blockBackpack");
        GameRegistry.registerBlock(blockSleepingBag, "blockSleepingBag");
        GameRegistry.registerBlock(blockCampFire, "blockCampFire");

        GameRegistry.registerTileEntity(TileCampfire.class, "tileCampFire");
        GameRegistry.registerTileEntity(TileAdventureBackpack.class, "adventureBackpackTileEntity");
    }

}
