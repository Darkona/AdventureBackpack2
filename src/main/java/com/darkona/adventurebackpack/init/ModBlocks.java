package com.darkona.adventurebackpack.init;


import com.darkona.adventurebackpack.block.BlockSleepingBag;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ModBlocks
{

    public static BlockAdventureBackpack blockBackpack = new BlockAdventureBackpack();
    public static BlockSleepingBag blockSleepingBag = new BlockSleepingBag();

    public static void init()
    {
        GameRegistry.registerBlock(blockBackpack, "blockBackpack");
        GameRegistry.registerBlock(blockSleepingBag, "blockSleepingBag");

        GameRegistry.registerTileEntity(TileAdventureBackpack.class, "adventureBackpackTileEntity");
    }

}
