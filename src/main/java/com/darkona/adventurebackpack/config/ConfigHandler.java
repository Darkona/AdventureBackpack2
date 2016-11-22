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

    public static Configuration config;

    public static String CAT_GRAPHICS = "graphics";
    public static String CAT_STATUS = "graphics status";
    public static String CAT_TANKS = "graphics tanks";
    public static String CAT_SOUND = "sound";
    public static String CAT_ITEMS = "items";
    public static String CAT_DISALLOWED = "items disallowed";
    public static String CAT_RECIPES = "items recipes";
    public static String CAT_WORLDGEN = "worldgen";
    public static String CAT_COMMON = "common";

    public static boolean IS_BUILDCRAFT = false;
    public static boolean IS_BAUBLES = false;
    public static boolean IS_TINKERS = false;
    public static boolean IS_THAUM = false;
    public static boolean IS_TWILIGHT = false;
    public static boolean IS_ENVIROMINE = false;
    public static boolean IS_RAILCRAFT = false;

    public static boolean ENABLE_TOOLS_RENDER = true;
    public static int GUI_TANK_RENDER = 2;
    public static boolean HOVERING_TEXT_TANKS = false;

    public static boolean STATUS_OVERLAY = true;
    public static boolean STATUS_OVERLAY_LEFT = true;
    public static boolean STATUS_OVERLAY_TOP = true;
    public static int STATUS_OVERLAY_INDENT_H = 2;
    public static int STATUS_OVERLAY_INDENT_V = 2;

    public static boolean TANKS_OVERLAY = true;
    public static boolean TANKS_OVERLAY_RIGHT = true;
    public static boolean TANKS_OVERLAY_BOTTOM = true;
    public static int TANKS_OVERLAY_INDENT_H = 4;
    public static int TANKS_OVERLAY_INDENT_V = 2;

    public static boolean ALLOW_COPTER_SOUND = true;
    public static boolean ALLOW_JETPACK_SOUND = true;
    public static boolean ALLOW_PISTON_SOUND = true;

    public static boolean YOU_SHALL_NOT_PASS = false;

    public static String[] NAME_LOCALIZED;
    public static String[] NAME_UNLOCALIZED;
    public static String[] NAME_DEFAULT = {};
    //public static String[] DISALLOWED_CLASS;

    public static boolean RECIPE_ADVENTURES_SET = true;
    public static boolean RECIPE_CLOCKWORK_CROSSBOW = true;
    public static boolean RECIPE_COAL_JETPACK = true;
    public static boolean RECIPE_COPTER_PACK = true;
    public static boolean RECIPE_PISTON_BOOTS = true;
    public static boolean RECIPE_SADDLE = true;
    public static boolean RECIPE_MACHETE = true;

    public static boolean BONUS_CHEST_ALLOWED = false;
    public static boolean PIGMAN_ALLOWED = false;

    public static boolean BACKPACK_DEATH_PLACE = true;
    public static boolean BACKPACK_ABILITIES = true;
    public static boolean ENABLE_TOOLS = true;
    public static boolean FIX_LEAD = true;

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

        //Graphics
        GUI_TANK_RENDER = config.getInt("TankRenderType", CAT_GRAPHICS, 3, 1, 3, "1,2 or 3 for different rendering of fluids in the Backpack GUI");
        ENABLE_TOOLS_RENDER = config.getBoolean("EnableToolsRender", CAT_GRAPHICS, true, "Enable rendering for tools in the backpack tool slots. May cause visual glitches with Gregtech tools");
        HOVERING_TEXT_TANKS = config.getBoolean("HoveringText", CAT_GRAPHICS, false, "Show hovering text on fluid tanks?");

        //Graphics.Status
        STATUS_OVERLAY = config.getBoolean("EnableOverlay", CAT_STATUS, true, "Show player status effects on screen?");
        STATUS_OVERLAY_LEFT = config.getBoolean("StickToLeft", CAT_STATUS, true, "Stick to left? Icons will appears from left to right. If false: stick to right, icons will appears from right to left");
        STATUS_OVERLAY_TOP = config.getBoolean("StickToTop", CAT_STATUS, true, "Stick to top?");
        STATUS_OVERLAY_INDENT_H = config.getInt("IndentHorizontal", CAT_STATUS, 2, 0, 1000, "Horizontal indent from the window border");
        STATUS_OVERLAY_INDENT_V = config.getInt("IndentVertical", CAT_STATUS, 2, 0, 500, "Vertical indent from the window border");

        //Graphics.Tanks
        TANKS_OVERLAY = config.getBoolean("EnableOverlay", CAT_TANKS, true, "Show the different wearable overlays on screen?");
        TANKS_OVERLAY_RIGHT = config.getBoolean("StickToRight", CAT_TANKS, true, "Stick to right?");
        TANKS_OVERLAY_BOTTOM = config.getBoolean("StickToBottom", CAT_TANKS, true, "Stick to bottom?");
        TANKS_OVERLAY_INDENT_H = config.getInt("IndentHorizontal", CAT_TANKS, 4, 0, 1000, "Horizontal indent from the window border");
        TANKS_OVERLAY_INDENT_V = config.getInt("IndentVertical", CAT_TANKS, 2, 0, 500, "Vertical indent from the window border");

        //Sound
        ALLOW_COPTER_SOUND = config.getBoolean("CopterPack", CAT_SOUND, true, "Allow playing the CopterPack sound (Client Only, other players may hear it)");
        ALLOW_JETPACK_SOUND = config.getBoolean("CoalJetpack", CAT_SOUND, true, "Allow playing the CoalJetpack sound (Client Only, other players may hear it)");
        ALLOW_PISTON_SOUND = config.getBoolean("PistonBoots", CAT_SOUND, true, "Allow playing the PistonBoots sound (Client Only, other players may hear it)");

        //Items
        YOU_SHALL_NOT_PASS  = config.getBoolean("EnableItemFilters", CAT_ITEMS, false, "FOR TEST ONLY. Enable filters from Disallowed category");

        //Items.Recipes
        RECIPE_ADVENTURES_SET = config.getBoolean("AdventuresSet", CAT_RECIPES, true, "Enable/Disable recipe for Adventure's Hat, Suit and Pants");
        RECIPE_CLOCKWORK_CROSSBOW = config.getBoolean("ClockworkCrossbow", CAT_RECIPES, true, "Enable/Disable Clockwork Crossbow recipe");
        RECIPE_COPTER_PACK = config.getBoolean("CopterPack", CAT_RECIPES, true, "Enable/Disable CopterPack recipe");
        RECIPE_COAL_JETPACK = config.getBoolean("CoalJetpack", CAT_RECIPES, true, "Enable/Disable CoalJetpack recipe");
        RECIPE_PISTON_BOOTS = config.getBoolean("PistonBoots", CAT_RECIPES, true, "Enable/Disable PistonBoots recipe");
        RECIPE_SADDLE = config.getBoolean("Saddle", CAT_RECIPES, true, "Add recipe for saddle?");
        RECIPE_MACHETE = config.getBoolean("Machete", CAT_RECIPES, true, "Enable/Disable Machete recipe");

        //Items.Disallowed
        NAME_LOCALIZED = config.getStringList("ByDisplaedName", CAT_DISALLOWED, NAME_DEFAULT, "FOR TEST ONLY. Disallowed items by dispaled (localized) name. Not case sensitive");
        NAME_UNLOCALIZED = config.getStringList("ByInternalName", CAT_DISALLOWED, NAME_DEFAULT, "FOR TEST ONLY. Disallowed items by internal (unlocalized) name. Not case sensitive");

        //WorldGen
        BONUS_CHEST_ALLOWED = config.getBoolean("BonusBackpack", CAT_WORLDGEN, false, "Include a Standard Adventure Backpack in bonus chest?");
        PIGMAN_ALLOWED = config.getBoolean("PigmanBackpacks", CAT_WORLDGEN, false, "Allow generation of Pigman Backpacks in dungeon loot and villager trades");

        //Common
        BACKPACK_ABILITIES = config.getBoolean("BackpackAbilities", CAT_COMMON, true, "Allow the backpacks to execute their special abilities, or be only cosmetic (Doesn't affect lightning transformation) Must be " +
        	"disabled in both Client and Server to work properly");
       	BACKPACK_DEATH_PLACE = config.getBoolean("BackpackDeathPlace", CAT_COMMON, true,"Place backpacks as a block when you die?");
        ENABLE_TOOLS = config.getBoolean("EnableTools", CAT_COMMON, true, "Enable/Disable tool cycling");
        FIX_LEAD = config.getBoolean("FixVanillaLead", CAT_COMMON, true, "Fix the vanilla Lead? (Checks mobs falling on a leash to not die of fall damage if they're not falling so fast)");

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
