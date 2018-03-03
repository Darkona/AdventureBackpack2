package com.darkona.adventurebackpack.config;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import com.darkona.adventurebackpack.reference.ModInfo;

/**
 * Created on 10/10/2014.
 *
 * @author Javier Darkona
 */
public class ConfigHandler
{
    public static Configuration config;

    public static boolean allowSoulBound = true;
    public static boolean backpackDeathPlace = true;
    public static boolean backpackAbilities = true;
    public static boolean enableCampfireSpawn = false;
    public static boolean enableHoseDrink = true;
    public static boolean enableToolsCycling = true;
    public static boolean fixLead = true;
    public static boolean portableSleepingBag = true;
    public static boolean tinkerToolsMaintenance = true;

    public static boolean enableFullnessBar = false;
    public static boolean enableTemperatureBar = false;
    public static boolean enableToolsRender = true;
    public static int typeTankRender = 2;
    public static boolean tanksHoveringText = false;

    public static boolean statusOverlay = true;
    public static boolean statusOverlayLeft = true;
    public static boolean statusOverlayTop = true;
    public static int statusOverlayIndentH = 2;
    public static int statusOverlayIndentV = 2;
    public static boolean statusOverlayThaumcraft = true;

    public static boolean tanksOverlay = true;
    public static boolean tanksOverlayRight = true;
    public static boolean tanksOverlayBottom = true;
    public static int tanksOverlayIndentH = 2;
    public static int tanksOverlayIndentV = 1;

    public static boolean allowSoundCopter = true;
    public static boolean allowSoundJetpack = true;
    public static boolean allowSoundPiston = true;

    public static boolean enableItemFilters = false;
    public static String[] forbiddenDimensions;
    public static String[] copterFuels;
    private static String[] defaultFuels = {"biodiesel, 1.0", "biofuel, 1.0", "bioethanol, 1.5", "creosote, 7.0",
            "fuel, 0.8", "lava, 5.0", "liquid_light_oil, 3.0", "liquid_medium_oil, 3.0", "liquid_heavy_oil, 3.0",
            "liquid_light_fuel, 1.0", "liquid_heavy_fuel, 1.3", "nitrofuel, 0.4", "oil, 3.0", "rocket_fuel, 0.8"};

    public static String[] nameLocalized;
    public static String[] nameInternalID;
    public static String[] nameInternalIDs;
    public static String[] nameUnlocalized;
    private static String[] nameDefault = {};

    public static boolean consumeDragonEgg = false;
    public static boolean recipeAdventuresSet = true;
    public static boolean recipeInflatableBoat = true;
    public static boolean recipeInflatableBoatM = false;
    public static boolean recipeClockCrossbow = true;
    public static boolean recipeCoalJetpack = true;
    public static boolean recipeCopterPack = true;
    public static boolean recipePitonBoots = true;
    public static boolean recipeSaddle = true;
    public static boolean recipeMachete = true;

    public static boolean pistonBootsAutoStep = true;
    public static int pistonBootsJumpHeight = 3;
    public static int pistonBootsSprintBoost = 1;
    public static int dragonBackpackRegen = 1;
    public static int dragonBackpackDamage = 2;
    public static int rainbowBackpackSpeed = 1;
    public static int rainbowBackpackSSpeed = 3;
    public static int rainbowBackpackSJump = 1;

    public static boolean allowBatGen = true;
    public static boolean allowBonusGen = false;
    public static boolean allowGolemGen = true;
    public static boolean allowPigmanGen = false;

    public static int bossBarIndent = 12;

    public static void init(File configFile)
    {
        if (config == null)
        {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }

    private static void loadConfiguration()
    {
        // Gameplay
        allowSoulBound = config.getBoolean("Allow SoulBound", "gameplay", true, "Allow SoulBound enchant on wearable packs");
        backpackAbilities = config.getBoolean("Backpack Abilities", "gameplay", true, "Allow the backpacks to execute their special abilities, or be only cosmetic (Doesn't affect lightning transformation) Must be " +
                "disabled in both Client and Server to work properly");
        backpackDeathPlace = config.getBoolean("Backpack Death Place", "gameplay", true, "Place backpacks as a block when you die?");
        fixLead = config.getBoolean("Fix Vanilla Lead", "gameplay", true, "Fix the vanilla Lead? (Checks mobs falling on a leash to not die of fall damage if they're not falling so fast)");
        enableCampfireSpawn = config.getBoolean("Enable Campfire Spawn", "gameplay", false, "Enable/Disable ability to spawn at campfire");
        enableHoseDrink = config.getBoolean("Enable Hose Drink", "gameplay", true, "Enable/Disable hose drink mode");
        enableToolsCycling = config.getBoolean("Enable Tools Cycling", "gameplay", true, "Enable/Disable tool cycling");
        portableSleepingBag = config.getBoolean("Portable Sleeping Bag", "gameplay", true, "Allows to use sleeping bag directly from wearing backpacks. Sleep by one touch");
        tinkerToolsMaintenance = config.getBoolean("Maintenance Tinker Tools", "gameplay", true, "Allows to maintenance (repair/upgarde) Tinkers Construct tools in backpacks as if it's Crafting Station");

        // Graphics
        typeTankRender = config.getInt("Tank Render Type", "graphics", 3, 1, 3, "1,2 or 3 for different rendering of fluids in the Backpack GUI");
        enableFullnessBar = config.getBoolean("Enable Fullness Bar", "graphics", false, "Enable durability bar showing fullness of backpacks inventory");
        enableTemperatureBar = config.getBoolean("Enable Temperature Bar", "graphics", false, "Enable durability bar showing temperature of jetpack");
        enableToolsRender = config.getBoolean("Enable Tools Render", "graphics", true, "Enable rendering for tools in the backpack tool slots");
        tanksHoveringText = config.getBoolean("Hovering Text", "graphics", false, "Show hovering text on fluid tanks?");

        // Graphics.Status
        statusOverlay = config.getBoolean("Enable Overlay", "graphics.status", true, "Show player status effects on screen?");
        statusOverlayLeft = config.getBoolean("Stick To Left", "graphics.status", true, "Stick to left? Icons will appears from left to right. If false: stick to right, icons will appears from right to left");
        statusOverlayTop = config.getBoolean("Stick To Top", "graphics.status", true, "Stick to top?");
        statusOverlayIndentH = config.getInt("Indent Horizontal", "graphics.status", 2, 0, 1000, "Horizontal indent from the window border");
        statusOverlayIndentV = config.getInt("Indent Vertical", "graphics.status", 2, 0, 500, "Vertical indent from the window border");
        statusOverlayThaumcraft = config.getBoolean("Respect Thaumcraft", "graphics.status", true, "Take into account Thaumcraft wands GUI and do not overlap it");

        // Graphics.Tanks
        tanksOverlay = config.getBoolean("Enable Overlay", "graphics.tanks", true, "Show the different wearable overlays on screen?");
        tanksOverlayRight = config.getBoolean("Stick To Right", "graphics.tanks", true, "Stick to right?");
        tanksOverlayBottom = config.getBoolean("Stick To Bottom", "graphics.tanks", true, "Stick to bottom?");
        tanksOverlayIndentH = config.getInt("Indent Horizontal", "graphics.tanks", 2, 0, 1000, "Horizontal indent from the window border");
        tanksOverlayIndentV = config.getInt("Indent Vertical", "graphics.tanks", 1, 0, 500, "Vertical indent from the window border");

        // Sound
        allowSoundCopter = config.getBoolean("Copter Pack", "sound", true, "Allow playing the CopterPack sound (Client Only, other players may hear it)");
        allowSoundJetpack = config.getBoolean("Coal Jetpack", "sound", true, "Allow playing the CoalJetpack sound (Client Only, other players may hear it)");
        allowSoundPiston = config.getBoolean("Piston Boots", "sound", true, "Allow playing the PistonBoots sound");

        // Items
        enableItemFilters = config.getBoolean("Enable Item Filters", "items", true, "Enable filters from Disallow category");
        forbiddenDimensions = config.getStringList("Forbidden Dimensions", "items", nameDefault, "Disallow opening backpack inventory for specific dimension ID");
        copterFuels = config.getStringList("Valid Copter Fuels", "items", defaultFuels, "List of valid fuels for Copter. Consumption rate range: 0.05 ~ 20.0. Format: 'fluid, rate', ex.: 'water, 0.0'");

        // Items.Disallowed
        nameLocalized = config.getStringList("By Displayed Name", "items.disallowed", nameDefault, "Disallow items by displayed (localized) name. Not case sensitive. Worst option, use only when there is no choice. Example: Dirt");
        nameInternalID = config.getStringList("By Internal ID", "items.disallowed", nameDefault, "Disallow items by internal ID. Case sensitive. Example: minecraft:dirt");
        nameInternalIDs = config.getStringList("By Internal IDs", "items.disallowed", nameDefault, "Disallow items by internal ID. Case sensitive. Will be disallowed all items containing that word in their IDs. Use with caution. Example: minecraft:di");
        nameUnlocalized = config.getStringList("By Internal Name", "items.disallowed", nameDefault, "Disallow items by internal (unlocalized) name. Not case sensitive. Example: tile.dirt");

        // Items.Recipes
        consumeDragonEgg = config.getBoolean("Consume Dragon Egg", "items.recipes", false, "Consume Dragon Egg when Dragon backpack crafted?");
        recipeAdventuresSet = config.getBoolean("Adventures Set", "items.recipes", true, "Enable/Disable recipe for Adventure's Hat, Suit and Pants");
        recipeInflatableBoat = config.getBoolean("Inflatable Boat", "items.recipes", true, "Enable/Disable recipe for Inflatable Boat");
        recipeInflatableBoatM = config.getBoolean("Inflatable Boat Motorized", "items.recipes", false, "Enable/Disable recipe for Inflatable Boat (motorized). For aesthetic only, not fully implemented (yet?)");
        recipeClockCrossbow = config.getBoolean("Clockwork Crossbow", "items.recipes", true, "Enable/Disable Clockwork Crossbow recipe");
        recipeCopterPack = config.getBoolean("Copter Pack", "items.recipes", true, "Enable/Disable CopterPack recipe");
        recipeCoalJetpack = config.getBoolean("Coal Jetpack", "items.recipes", true, "Enable/Disable CoalJetpack recipe");
        recipePitonBoots = config.getBoolean("Piston Boots", "items.recipes", true, "Enable/Disable PistonBoots recipe");
        recipeSaddle = config.getBoolean("Saddle", "items.recipes", true, "Add recipe for saddle?");
        recipeMachete = config.getBoolean("Machete", "items.recipes", true, "Enable/Disable Machete recipe");

        // Items.Settings
        pistonBootsAutoStep = config.getBoolean("Piston Boots Auto Step", "items.settings", true, "Allow Piston Boots auto step blocks");
        pistonBootsJumpHeight = config.getInt("Piston Boots Jump Height", "items.settings", 3, 1, 8, "Piston Boots jump height in blocks");
        pistonBootsSprintBoost = config.getInt("Piston Boots Sprint", "items.settings", 1, 0, 4, "Piston Boots sprint boost. 0 - disable");
        dragonBackpackRegen = config.getInt("Dragon Regeneration", "items.settings", 1, 0, 4, "Dragon Backpack regeneration level. 0 - disable");
        dragonBackpackDamage = config.getInt("Dragon Damage Boost", "items.settings", 2, 0, 4, "Dragon Backpack damage boost. 0 - disable");
        rainbowBackpackSpeed = config.getInt("Rainbow Speed", "items.settings", 1, 0, 4, "Rainbow Backpack speed boost. 0 - disable");
        rainbowBackpackSSpeed = config.getInt("Rainbow Special Speed", "items.settings", 3, 0, 4, "Rainbow Backpack special speed. 0 - disable");
        rainbowBackpackSJump = config.getInt("Rainbow Special Jump", "items.settings", 1, 0, 4, "Rainbow Backpack special jump. 0 - disable");

        // WorldGen
        allowBatGen = config.getBoolean("Bat Backpacks", "worldgen", true, "Allow generation of Bat Backpacks in dungeon and mineshaft loot. It cannot be obtained by crafting");
        allowBonusGen = config.getBoolean("Bonus Backpack", "worldgen", false, "Include a Standard Adventure Backpack in bonus chest?");
        allowGolemGen = config.getBoolean("IronGolem Backpacks", "worldgen", true, "Allow generation of IronGolem Backpacks in village blacksmith loot. It cannot be obtained by crafting");
        allowPigmanGen = config.getBoolean("Pigman Backpacks", "worldgen", false, "Allow generation of Pigman Backpacks in dungeon loot and villager trades");

        // Experimental
        bossBarIndent = config.getInt("Boss Bar Indent", "experimental", 0, 0, 500, "Boss health bar indent from top border, 0 = standard Forge render");

        if (config.hasChanged())
        {
            config.save();
        }
    }

    @SubscribeEvent
    public void onConfigChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(ModInfo.MOD_ID))
        {
            loadConfiguration();
        }
    }

}