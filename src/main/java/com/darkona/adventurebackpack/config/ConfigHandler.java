package com.darkona.adventurebackpack.config;

import java.io.File;

import com.darkona.adventurebackpack.reference.ModInfo;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;


/**
 * Created on 10/10/2014.
 * @author Javier Darkona
 */
public class ConfigHandler
{

    //public static final String configVersion = "1.0.0";
    public static Configuration config;

    public static boolean IS_BUILDCRAFT = false;
    public static boolean IS_BAUBLES = false;
    public static boolean IS_TINKERS = false;
    public static boolean IS_THAUM = false;
    public static boolean IS_TWILIGHT = false;
    public static boolean IS_ENVIROMINE = false;
    public static boolean IS_RAILCRAFT = false;

    public static boolean backpackDeathPlace = true;
    public static boolean backpackAbilities = true;
    public static boolean enableToolsCycling = true;
    public static boolean enableHoseDrink = true;
    public static boolean fixLead = true;

    public static boolean enableToolsRender = true;
    public static int typeTankRender = 2;
    public static boolean tanksHoveringText = false;

    public static boolean statusOverlay = true;
    public static boolean statusOverlayLeft = true;
    public static boolean statusOverlayTop = true;
    public static int statusOverlayIndentH = 2;
    public static int statusOverlayIndentV = 2;

    public static boolean tanksOverlay = true;
    public static boolean tanksOverlayRight = true;
    public static boolean tanksOverlayBottom = true;
    public static int tanksOverlayIndentH = 4;
    public static int tanksOverlayIndentV = 2;

    public static boolean allowSoundCopter = true;
    public static boolean allowSoundJetpack = true;
    public static boolean allowSoundPiston = true;

    public static boolean enableItemFilters = false;

    public static String[] nameLocalized;
    public static String[] nameInternalID;
    public static String[] nameInternalIDs;
    public static String[] nameUnlocalized;
    public static String[] nameDefault = {};

    public static boolean recipeAdventuresSet = true;
    public static boolean recipeClockCrossbow = true;
    public static boolean recipeCoalJetpack = true;
    public static boolean recipeCopterPack = true;
    public static boolean recipePitonBoots = true;
    public static boolean recipeSaddle = true;
    public static boolean recipeMachete = true;

    public static boolean allowBonusGen = false;
    public static boolean allowPigmanGen = false;

    public static void init(File configFile)
    {
        if (config == null)
        {
            config = new Configuration(configFile);
            loadConfiguration();
        }
    }

    @SuppressWarnings("static-access")
    private static void loadConfiguration()
    {
	// Gameplay
	backpackAbilities = config.getBoolean("Backpack Abilities", "gameplay", true, "Allow the backpacks to execute their special abilities, or be only cosmetic (Doesn't affect lightning transformation) Must be " +
		"disabled in both Client and Server to work properly");
	backpackDeathPlace = config.getBoolean("Backpack Death Place", "gameplay", true,"Place backpacks as a block when you die?");
	fixLead = config.getBoolean("Fix Vanilla Lead", "gameplay", true, "Fix the vanilla Lead? (Checks mobs falling on a leash to not die of fall damage if they're not falling so fast)");
	enableHoseDrink = config.getBoolean("Enable Hose Drink", "gameplay", true, "Enable/Disable hose drink mode");
	enableToolsCycling = config.getBoolean("Enable Tools Cycling", "gameplay", true, "Enable/Disable tool cycling");

	// Graphics
        typeTankRender = config.getInt("Tank Render Type", "graphics", 3, 1, 3, "1,2 or 3 for different rendering of fluids in the Backpack GUI");
        enableToolsRender = config.getBoolean("Enable Tools Render", "graphics", true, "Enable rendering for tools in the backpack tool slots. May cause visual glitches with Gregtech tools");
        tanksHoveringText = config.getBoolean("Hovering Text", "graphics", false, "Show hovering text on fluid tanks?");

        // Graphics.Status
        statusOverlay = config.getBoolean("Enable Overlay", "graphics.status", true, "Show player status effects on screen?");
        statusOverlayLeft = config.getBoolean("Stick To Left", "graphics.status", true, "Stick to left? Icons will appears from left to right. If false: stick to right, icons will appears from right to left");
        statusOverlayTop = config.getBoolean("Stick To Top", "graphics.status", true, "Stick to top?");
        statusOverlayIndentH = config.getInt("Indent Horizontal", "graphics.status", 2, 0, 1000, "Horizontal indent from the window border");
        statusOverlayIndentV = config.getInt("Indent Vertical", "graphics.status", 2, 0, 500, "Vertical indent from the window border");

        // Graphics.Tanks
        tanksOverlay = config.getBoolean("Enable Overlay", "graphics.tanks", true, "Show the different wearable overlays on screen?");
        tanksOverlayRight = config.getBoolean("Stick To Right", "graphics.tanks", true, "Stick to right?");
        tanksOverlayBottom = config.getBoolean("Stick To Bottom", "graphics.tanks", true, "Stick to bottom?");
        tanksOverlayIndentH = config.getInt("Indent Horizontal", "graphics.tanks", 4, -10, 1000, "Horizontal indent from the window border");
        tanksOverlayIndentV = config.getInt("Indent Vertical", "graphics.tanks", 2, 0, 500, "Vertical indent from the window border");

        // Sound
        allowSoundCopter = config.getBoolean("Copter Pack", "sound", true, "Allow playing the CopterPack sound (Client Only, other players may hear it)");
        allowSoundJetpack = config.getBoolean("Coal Jetpack", "sound", true, "Allow playing the CoalJetpack sound (Client Only, other players may hear it)");
        allowSoundPiston = config.getBoolean("Piston Boots", "sound", true, "Allow playing the PistonBoots sound");

        // Items
        enableItemFilters  = config.getBoolean("Enable Item Filters", "items", true, "Enable filters from Disallow category");

        // Items.Recipes
        recipeAdventuresSet = config.getBoolean("Adventures Set", "items.recipes", true, "Enable/Disable recipe for Adventure's Hat, Suit and Pants");
        recipeClockCrossbow = config.getBoolean("Clockwork Crossbow", "items.recipes", true, "Enable/Disable Clockwork Crossbow recipe");
        recipeCopterPack = config.getBoolean("Copter Pack", "items.recipes", true, "Enable/Disable CopterPack recipe");
        recipeCoalJetpack = config.getBoolean("Coal Jetpack", "items.recipes", true, "Enable/Disable CoalJetpack recipe");
        recipePitonBoots = config.getBoolean("Piston Boots", "items.recipes", true, "Enable/Disable PistonBoots recipe");
        recipeSaddle = config.getBoolean("Saddle", "items.recipes", true, "Add recipe for saddle?");
        recipeMachete = config.getBoolean("Machete", "items.recipes", true, "Enable/Disable Machete recipe");

        // Items.Disallowed
        nameLocalized = config.getStringList("By Displayed Name", "items.disallowed", nameDefault, "Disallow items by displayed (localized) name. Not case sensitive. Worst option, use only when there is no choice. Example: Dirt");
        nameInternalID = config.getStringList("By Internal ID", "items.disallowed", nameDefault, "Disallow items by internal ID. Case sensitive. Example: minecraft:dirt");
        nameInternalIDs = config.getStringList("By Internal IDs", "items.disallowed", nameDefault, "Disallow items by internal ID. Case sensitive. Will be disallowed all items containing that word in their IDs. Use with caution. Example: minecraft:di");
        nameUnlocalized = config.getStringList("By Internal Name", "items.disallowed", nameDefault, "Disallow items by internal (unlocalized) name. Not case sensitive. Example: tile.dirt");

        // WorldGen
        allowBonusGen = config.getBoolean("Bonus Backpack", "worldgen", false, "Include a Standard Adventure Backpack in bonus chest?");
        allowPigmanGen = config.getBoolean("Pigman Backpacks", "worldgen", false, "Allow generation of Pigman Backpacks in dungeon loot and villager trades");

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

    /*private static boolean isConfigVersionWrong(Configuration configuration)
    {
	return !configuration.getLoadedConfigVersion().equals(configuration.getDefinedConfigVersion());
    }*/

}
