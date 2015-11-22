package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created on 10/10/2014.
 * @author Javier Darkona
 */
public class ConfigHandler
{


    public static Configuration config;

    public static boolean IS_BUILDCRAFT = false;
    public static boolean IS_BAUBLES = false;
    public static boolean IS_TINKERS = false;
    public static boolean IS_THAUM = false;
    public static boolean IS_TWILIGHT = false;
    public static boolean IS_ENVIROMINE = false;
    public static boolean IS_RAILCRAFT = false;

    public static int GUI_TANK_RENDER = 2;
    public static boolean BONUS_CHEST_ALLOWED = false;
    public static boolean PIGMAN_ALLOWED = false;

    public static boolean BACKPACK_DEATH_PLACE = true;
    public static boolean BACKPACK_ABILITIES = true;

    public static boolean ALLOW_COPTER_SOUND = true;
    public static boolean ALLOW_JETPACK_SOUNDS = true;


    public static boolean STATUS_OVERLAY = true;
    public static boolean TANKS_OVERLAY = true;
    public static boolean HOVERING_TEXT_TANKS = false;
    public static boolean SADDLE_RECIPE = true;
    public static boolean FIX_LEAD = true;


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
        GUI_TANK_RENDER = config.getInt("TankRenderType", config.CATEGORY_GENERAL, 3, 1, 3, "1,2 or 3 for different rendering of fluids in the Backpack GUI");
        BONUS_CHEST_ALLOWED = config.getBoolean("BonusBackpack", config.CATEGORY_GENERAL, false, "Include a Standard Adventure Backpack in bonus chest?");
        PIGMAN_ALLOWED = config.getBoolean("PigmanBackpacks", config.CATEGORY_GENERAL, false, "Allow generation of Pigman Backpacks in dungeon loot and villager trades");
        ALLOW_COPTER_SOUND = config.getBoolean("CopterPackSound", config.CATEGORY_GENERAL, true, "Allow playing the CopterPack sound (Client Only, other players may hear it)");
        BACKPACK_ABILITIES = config.getBoolean("BackpackAbilities", config.CATEGORY_GENERAL, true, "Allow the backpacks to execute their special abilities, or be only cosmetic (Doesn't affect lightning transformation) Must be " +
                "disabled in both Client and Server to work properly");
        STATUS_OVERLAY = config.getBoolean("StatusOverlay", config.CATEGORY_GENERAL,true, "Show player status effects on screen?");
        TANKS_OVERLAY = config.getBoolean("BackpackOverlay", config.CATEGORY_GENERAL,true, "Show the different wearable overlays on screen?");
        HOVERING_TEXT_TANKS = config.getBoolean("HoveringText", config.CATEGORY_GENERAL,false, "Show hovering text on fluid tanks?");
        FIX_LEAD = config.getBoolean("FixVanillaLead", config.CATEGORY_GENERAL,true, "Fix the vanilla Lead? (Checks mobs falling on a leash to not die of fall damage if they're not falling so fast)");
        BACKPACK_DEATH_PLACE = config.getBoolean("BackpackDeathPlace", config.CATEGORY_GENERAL,true,"Place backpacks as a block when you die?");
        //RECIPES
        SADDLE_RECIPE = config.getBoolean("SaddleRecipe", config.CATEGORY_GENERAL,true, "Add recipe for saddle?");
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
