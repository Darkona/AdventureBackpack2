package com.darkona.adventurebackpack.init;

import cpw.mods.fml.common.registry.GameRegistry;

import com.darkona.adventurebackpack.item.ArmorAB;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureHat;
import com.darkona.adventurebackpack.item.ItemAdventurePants;
import com.darkona.adventurebackpack.item.ItemAdventureSuit;
import com.darkona.adventurebackpack.item.ItemCoalJetpack;
import com.darkona.adventurebackpack.item.ItemComponent;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemCrossbow;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.item.ItemJuiceBottle;
import com.darkona.adventurebackpack.item.ItemMachete;
import com.darkona.adventurebackpack.item.ItemPistonBoots;

/**
 * Created on 10/10/2014
 *
 * @author Darkona
 */
public class ModItems
{
    public static final ItemMachete machete = new ItemMachete();
    public static final ItemCrossbow cwxbow = new ItemCrossbow();
    public static final ArmorAB adventureHat = new ItemAdventureHat();
    public static final ArmorAB adventureSuit = new ItemAdventureSuit();
    public static final ArmorAB adventurePants = new ItemAdventurePants();
    public static final ArmorAB pistonBoots = new ItemPistonBoots();
    public static final ItemCopterPack copterPack = new ItemCopterPack();
    public static final ItemCoalJetpack coalJetpack = new ItemCoalJetpack();
    public static final ItemAdventureBackpack adventureBackpack = new ItemAdventureBackpack();
    public static final ItemComponent component = new ItemComponent();
    public static final ItemHose hose = new ItemHose();
    public static final ItemJuiceBottle melonJuiceBottle = new ItemJuiceBottle();

    public static void init()
    {
        GameRegistry.registerItem(machete, "machete");
        GameRegistry.registerItem(cwxbow, "clockworkCrossbow");
        GameRegistry.registerItem(adventureHat, "adventureHat");
        GameRegistry.registerItem(adventureSuit, "adventureSuit");
        GameRegistry.registerItem(adventurePants, "adventurePants");
        GameRegistry.registerItem(pistonBoots, "pistonBoots");
        GameRegistry.registerItem(copterPack, "copterPack");
        GameRegistry.registerItem(coalJetpack, "coalJetpack");
        GameRegistry.registerItem(adventureBackpack, "adventureBackpack");
        GameRegistry.registerItem(component, "backpackComponent");
        GameRegistry.registerItem(hose, "backpackHose");
        GameRegistry.registerItem(melonJuiceBottle, "melonJuiceBottle");
    }

    /*public static void conditionalInit()
    {
        if (ConfigHandler.IS_BUILDCRAFT)
        {
            GameRegistry.registerItem(copterPack, "copterPack");
        }

        if (ConfigHandler.IS_RAILCRAFT)
        {
            GameRegistry.registerItem(steamJetpack, "steamJetpack");
        }
    }*/
}
