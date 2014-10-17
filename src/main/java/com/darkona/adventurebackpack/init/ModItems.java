package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.item.*;
import cpw.mods.fml.common.registry.GameRegistry;

/**
 * Created by Darkona on 10/10/2014.
 */
public class ModItems
{

    public static final ItemMachete machete = new ItemMachete();
    public static final ItemComponent component = new ItemComponent();
    public static final ItemHose hose = new ItemHose();
    public static final ArmorAB adventureHat = new ItemAdventureHat();
    public static final ArmorAB pistonBoots = new ItemPistonBoots();
    public static final ArmorAB adventureSuit = new ItemAdventureSuit();
    public static final ArmorAB adventureBackpack = new ItemAdventureBackpack();

    public static void init()
    {
        GameRegistry.registerItem(component, "backpackComponent");
        GameRegistry.registerItem(machete, "machete");
        GameRegistry.registerItem(adventureHat, "adventureHat");
        GameRegistry.registerItem(pistonBoots, "pistonBoots");
        GameRegistry.registerItem(adventureSuit, "adventureSuit");
        GameRegistry.registerItem(adventureBackpack, "adventureBackpack");
        GameRegistry.registerItem(hose, "backpackHose");
    }

}
