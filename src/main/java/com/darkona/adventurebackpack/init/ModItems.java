package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.item.*;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModItems
{
    public static final ItemMachete machete = new ItemMachete();
    public static final ItemComponent component = new ItemComponent();
    public static final ItemHose hose = new ItemHose();
    public static final ArmorAB adventureHat = new ItemAdventureHat();
    public static final ArmorAB pistonBoots = new ItemPistonBoots();
    public static final ItemAdventureBackpack adventureBackpack = new ItemAdventureBackpack();
    public static final ItemJuiceBottle melonJuiceBottle = new ItemJuiceBottle();
    public static final ItemCopterPack copterPack = new ItemCopterPack();
    public static final ItemCrossbow cwxbow = new ItemCrossbow();
    public static final ItemBoltCartridge boltCartridge = new ItemBoltCartridge();
    public static final ItemCrossbowBolt bolt = new ItemCrossbowBolt();
    public static final ItemSteamJetpack steamJetpack = new ItemSteamJetpack();
    public static final ItemScubaGear scubaGear = new ItemScubaGear();
    public static final ItemFlamethrowerPack flamethrowerPack = new ItemFlamethrowerPack();
    public static final ItemFlamethrower flamethrower = new ItemFlamethrower();

    public static void init()
    {
        GameRegistry.registerItem(component, "backpackComponent");
        GameRegistry.registerItem(machete, "machete");
        GameRegistry.registerItem(adventureHat, "adventureHat");
        GameRegistry.registerItem(pistonBoots, "pistonBoots");
        GameRegistry.registerItem(adventureBackpack, "adventureBackpack");
        GameRegistry.registerItem(hose, "backpackHose");
        GameRegistry.registerItem(melonJuiceBottle, "melonJuiceBottle");
        GameRegistry.registerItem(cwxbow, "clockworkCrossbow");
        GameRegistry.registerItem(steamJetpack, "steamJetpack");
        GameRegistry.registerItem(boltCartridge, "boltCartridge");
        GameRegistry.registerItem(bolt, "bolt");
        GameRegistry.registerItem(copterPack, "copterPack");
        GameRegistry.registerItem(scubaGear, "scubaGear");
        GameRegistry.registerItem(flamethrowerPack, "flamethrowerPack");
        GameRegistry.registerItem(flamethrower, "flamethrower");
    }
}
