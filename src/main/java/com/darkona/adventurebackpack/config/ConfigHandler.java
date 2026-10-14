package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.common.AbilityEffects;
import com.darkona.adventurebackpack.common.BoatMotor;
import com.darkona.adventurebackpack.common.BoatRecipeRules;
import com.darkona.adventurebackpack.common.FuelRules;
import com.darkona.adventurebackpack.common.NightVisionRefresh;
import com.darkona.adventurebackpack.common.ToolCycleFilter;
import com.darkona.adventurebackpack.common.ToolCycleInput;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.lwjgl.input.Keyboard;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Reads adventurebackpack.cfg once at load (and again when the in-game config screen saves) into static fields.
 * Nothing reads the {@link Configuration} during play: tick and render code only reads the fields (Tier 1).
 * <p>
 * The file is one tree under {@link #ROOT}: first {@code enabled} (one flag per system), then one category per
 * system with its own flags and values. A disabled system stays registered and is only made inert and hidden,
 * so a client and a server with different files can still play together.
 *
 * @author Javier Darkona
 */
public class ConfigHandler
{
    public static Configuration config;

    // ---------------------------------------------------------------- categories
    public static final String ROOT = "adventurebackpack";
    public static final String ENABLED = ROOT + ".enabled";
    public static final String BACKPACK = ROOT + ".backpack";
    public static final String ABILITIES = ROOT + ".abilities";
    public static final String HOSE = ROOT + ".hose";
    public static final String COPTERPACK = ROOT + ".copterpack";
    public static final String STEAMJETPACK = ROOT + ".steamjetpack";
    public static final String SCUBA = ROOT + ".scuba";
    public static final String SCUBA_COMPRESSOR = SCUBA + ".compressor";
    public static final String FLAMETHROWER = ROOT + ".flamethrower";
    public static final String INFLATABLEBOAT = ROOT + ".inflatableboat";
    public static final String INFLATABLEBOAT_MOTOR = INFLATABLEBOAT + ".motor";
    public static final String FUELS_CATEGORY = ROOT + ".fuels";
    public static final String FUELSTATION = ROOT + ".fuelstation";
    public static final String CROSSBOW = ROOT + ".crossbow";
    public static final String CROSSBOW_GRAPPLE = CROSSBOW + ".grapple";
    public static final String CROSSBOW_SNIPER = CROSSBOW + ".sniper";
    public static final String CROSSBOW_MAGAZINE = CROSSBOW + ".magazine";
    public static final String CROSSBOW_AMMO = CROSSBOW + ".ammo";
    public static final String PISTONBOOTS = ROOT + ".pistonboots";
    public static final String SLEEPINGBAG = ROOT + ".sleepingbag";
    public static final String CAMPFIRE = ROOT + ".campfire";
    public static final String CAMPFIRE_COOKING = CAMPFIRE + ".cooking";
    public static final String RENDER = ROOT + ".render";
    public static final String KEYS = ROOT + ".keys";
    public static final String TOOL_CYCLE = KEYS + ".toolcycle";
    public static final String WORLDGEN = ROOT + ".worldgen";
    public static final String RECIPES = ROOT + ".recipes";
    public static final String RECIPES_BOAT = RECIPES + ".boat";
    public static final String RECIPES_FUELSTATION = RECIPES + ".fuelstation";
    public static final String RECIPES_FLAMETHROWER = RECIPES + ".flamethrower";
    public static final String TWEAKS = ROOT + ".tweaks";
    public static final String TWEAKS_TOOL_CYCLE = TWEAKS + ".toolcycle";
    public static final String COMPAT = ROOT + ".compat";
    public static final String COMPAT_GRAVES = COMPAT + ".graves";
    public static final String COMPAT_BAUBLES = COMPAT + ".baubles";

    /** Keys that lived in the old flat "general" category: old name, new category, new name. */
    static final String[][] LEGACY_KEYS = {
            {"BackpackAbilities", ENABLED, "abilities"},
            {"BackpackDeathPlace", BACKPACK, "placeOnDeath"},
            {"CopterPackSound", COPTERPACK, "sound"},
            {"TankRenderType", RENDER, "tankRenderType"},
            {"StatusOverlay", RENDER, "statusOverlay"},
            {"BackpackOverlay", RENDER, "wearableOverlay"},
            {"HoveringText", RENDER, "tankHoverText"},
            {"RenderBackpackOnPlayer", RENDER, "backpackOnPlayer"},
            {"BonusBackpack", WORLDGEN, "bonusChestBackpack"},
            {"PigmanBackpacks", WORLDGEN, "pigmanBackpacks"},
            {"SaddleRecipe", RECIPES, "saddle"},
            {"FixVanillaLead", TWEAKS, "fixVanillaLead"},
    };

    // ---------------------------------------------------------------- detected mods (not config, set in postInit)
    public static boolean IS_BUILDCRAFT = false;
    public static boolean IS_BAUBLES = false;
    public static boolean IS_TINKERS = false;
    public static boolean IS_THAUM = false;
    public static boolean IS_TWILIGHT = false;
    public static boolean IS_ENVIROMINE = false;
    public static boolean IS_RAILCRAFT = false;

    // ---------------------------------------------------------------- enabled
    public static boolean BACKPACK_ABILITIES = true;
    public static boolean HOSE_ENABLED = true;
    public static boolean COPTERPACK_ENABLED = true;
    public static boolean STEAMJETPACK_ENABLED = true;
    public static boolean SCUBA_ENABLED = true;
    public static boolean FLAMETHROWER_ENABLED = true;
    public static boolean INFLATABLEBOAT_ENABLED = true;
    public static boolean CROSSBOW_ENABLED = true;
    public static boolean SLEEPINGBAG_ENABLED = true;
    public static boolean PISTONBOOTS_ENABLED = true;
    public static boolean CAMPFIRE_ENABLED = true;
    public static boolean FUELSTATION_ENABLED = true;

    // ---------------------------------------------------------------- backpack
    public static boolean BACKPACK_DEATH_PLACE = true;
    public static int BACKPACK_TANK_CAPACITY = 4000;
    public static float BACKPACK_BLAST_RESISTANCE = 2000f;
    public static float BACKPACK_HARDNESS = 1.0f;
    public static boolean BACKPACK_DEATH_MESSAGE = true;

    // ---------------------------------------------------------------- abilities
    public static int ABILITY_EFFECT_REFRESH_TICKS = AbilityEffects.EFFECT_REFRESH_TICKS;
    public static boolean ABILITY_TOGGLE_MESSAGE = true;

    public static boolean BAT_ENABLED = true;
    public static int BAT_NIGHT_VISION_TICKS = NightVisionRefresh.DURATION;
    public static int BAT_NIGHT_VISION_REFRESH_BELOW = NightVisionRefresh.REFRESH_BELOW;

    public static boolean SQUID_ENABLED = true;
    public static boolean SQUID_WORKS_ON_LAND = false;

    public static boolean PIGMAN_ENABLED = true;
    public static int PIGMAN_FIRE_RESISTANCE_TICKS = 5000;

    public static boolean DRAGON_ENABLED = true;
    public static int DRAGON_EFFECT_TICKS = AbilityEffects.DRAGON_EFFECT_TICKS;
    public static int DRAGON_STRENGTH_AMPLIFIER = AbilityEffects.DRAGON_STRENGTH_AMPLIFIER;
    public static int DRAGON_REGENERATION_AMPLIFIER = AbilityEffects.DRAGON_REGENERATION_AMPLIFIER;

    public static boolean CACTUS_ENABLED = true;
    public static int CACTUS_WORN_INTERVAL_TICKS = 5;
    public static int CACTUS_WATER_IN_WATER = 2;
    public static int CACTUS_WATER_IN_RAIN = 1;
    public static int CACTUS_PLACED_INTERVAL_TICKS = 5;
    public static int CACTUS_PLACED_RAIN_AMOUNT = 2;

    public static boolean MELON_ENABLED = true;
    public static int MELON_WORN_INTERVAL_TICKS = 5;
    public static int MELON_JUICE_IN_WATER = 2;
    public static int MELON_JUICE_IN_RAIN = 1;
    public static int MELON_PLACED_INTERVAL_TICKS = 5;
    public static int MELON_PLACED_RAIN_AMOUNT = 2;

    public static boolean PIG_ENABLED = true;
    public static int PIG_OINK_MAX_SECONDS = 60;

    public static boolean SLIME_ENABLED = true;
    public static int SLIME_TRAIL_INTERVAL_TICKS = 5;
    public static boolean SLIME_FULL_MOON_SPEED = true;

    public static boolean CHICKEN_ENABLED = true;
    public static int CHICKEN_EGG_MIN_SECONDS = 200;
    public static int CHICKEN_EGG_RANDOM_SECONDS = 90;

    public static boolean CREEPER_ENABLED = true;
    public static int CREEPER_SCARE_COOLDOWN_SECONDS = 120;
    public static int CREEPER_CHECK_INTERVAL_TICKS = 20;
    public static double CREEPER_SCARE_RANGE = 3.0;
    public static boolean CREEPER_DEATH_EXPLOSION = true;
    public static float CREEPER_DEATH_EXPLOSION_POWER = 4.0f;

    public static boolean COW_ENABLED = true;
    public static int COW_WHEAT_PER_BATCH = 16;
    public static int COW_MILK_PER_BATCH = 1000;
    public static int COW_EAT_INTERVAL_TICKS = 20;

    public static boolean MOOSHROOM_ENABLED = true;
    public static int MOOSHROOM_WHEAT_PER_BATCH = 16;
    public static int MOOSHROOM_STEW_PER_BATCH = 1000;
    public static int MOOSHROOM_EAT_INTERVAL_TICKS = 20;

    public static boolean WOLF_ENABLED = true;
    public static int WOLF_CHECK_INTERVAL_TICKS = 20;
    public static double WOLF_RANGE = 16.0;

    public static boolean OCELOT_ENABLED = true;
    public static int OCELOT_CHECK_INTERVAL_TICKS = 20;
    public static double OCELOT_RANGE = 16.0;

    public static boolean RAINBOW_ENABLED = true;
    public static int RAINBOW_DURATION_SECONDS = 150;
    public static int RAINBOW_SPEED_AMPLIFIER = 2;
    public static int RAINBOW_JUMP_AMPLIFIER = 2;
    public static boolean RAINBOW_SOUND = true;

    public static boolean SUNFLOWER_ENABLED = true;
    public static int SUNFLOWER_INTERVAL_SECONDS = 120;
    public static int SUNFLOWER_FOOD = 2;
    public static float SUNFLOWER_SATURATION = 0.2f;

    public static boolean SKELETON_ENABLED = true;
    public static boolean IRONGOLEM_ENABLED = true;

    public static boolean SPIDER_ENABLED = true;
    public static String SPIDER_TAME_ITEM = "minecraft:spider_eye";
    public static double SPIDER_TAME_CHANCE = 0.33;
    public static int SPIDER_MAX_TAMED = 2;
    public static double SPIDER_FOLLOW_START_DISTANCE = 6.0;
    public static double SPIDER_FOLLOW_STOP_DISTANCE = 3.0;
    public static int SPIDER_TELEPORT_DISTANCE = 24;
    public static double SPIDER_TAMED_HEALTH = 20.0;
    public static String SPIDER_HEAL_ITEM = "minecraft:rotten_flesh";
    public static int SPIDER_HEAL_AMOUNT = 4;
    public static boolean SPIDER_VERIFY_LOADS_CHUNKS = true;
    public static boolean SPIDER_SMART_CLIMB = true;
    public static int SPIDER_CLIMB_MAX_HEIGHT = 32;
    public static int SPIDER_SAFE_DROP_HEIGHT = 3;
    public static int SPIDER_REPATH_TICKS = 10;
    public static int SPIDER_STUCK_TICKS = 60;
    public static int SPIDER_TELEPORT_STUCK_DISTANCE = 8;
    public static int SPIDER_TELEPORT_COOLDOWN_TICKS = 100;

    public static boolean BLAZE_ENABLED = true;
    public static boolean BLAZE_FIRE_IMMUNITY = true;
    public static int BLAZE_FIRE_RESISTANCE_TICKS = 5000;
    public static int BLAZE_PUNCH_FIRE_SECONDS = 4;
    public static boolean BLAZE_PUNCH_WITH_NON_WEAPONS = true;

    // ---------------------------------------------------------------- hose
    public static int HOSE_TRANSFER_AMOUNT = 1000;
    public static int HOSE_DRINK_AMOUNT = 1000;
    public static int HOSE_DRINK_TICKS = 24;
    public static int HOSE_MILK_AMOUNT = 1000;

    // ---------------------------------------------------------------- copter pack
    public static boolean ALLOW_COPTER_SOUND = true;
    public static int COPTER_TANK_CAPACITY = 6000;
    public static int COPTER_FUEL_INTERVAL_TICKS = 3;
    public static int COPTER_FUEL_PER_TICK = 2;
    public static int COPTER_HOVER_EXTRA_FUEL = 2;
    public static int COPTER_CLIMB_EXTRA_FUEL = 2;
    public static int COPTER_START_FUEL = 100;
    public static double COPTER_LIFT_SPEED = 0.15;
    public static double COPTER_DESCEND_SPEED = 0.3;
    public static double COPTER_GLIDE_FACTOR = 0.6;
    public static float COPTER_AIR_CONTROL = 0.03f;
    public static int COPTER_MAX_ALTITUDE = 90;
    public static double COPTER_PUSH_RANGE = 4.0;
    public static int COPTER_PUSH_INTERVAL_TICKS = 2;

    // ---------------------------------------------------------------- steam jetpack
    public static boolean ALLOW_JETPACK_SOUNDS = true;
    public static boolean JETPACK_OWN_STEAM = true;
    public static int JETPACK_WATER_CAPACITY = 6000;
    public static int JETPACK_STEAM_CAPACITY = 12000;
    public static int JETPACK_STEAM_PER_TICK = 20;
    public static int JETPACK_IGNITION_STEAM = 150;
    public static int JETPACK_THRUST_FUEL_BURN = 3;
    public static int JETPACK_THRUST_HEAT_LOSS_TICKS = 10;
    public static double JETPACK_HEATING_SPEED = 1.0;
    public static double JETPACK_COOLING_SPEED = 1.5;
    public static int JETPACK_WATER_INTAKE = 2;
    public static int JETPACK_STEAM_PER_WATER = 4;
    public static int JETPACK_BOILING_TEMPERATURE = 100;
    public static double JETPACK_THRUST = 0.08;
    public static double JETPACK_MAX_RISE_SPEED = 0.28;
    public static float JETPACK_AIR_CONTROL = 0.02f;
    public static int JETPACK_MAX_ALTITUDE = 90;

    // ---------------------------------------------------------------- scuba gear and air compressor
    public static final String[] SCUBA_BREATHABLE_DEFAULT = {"air", "oxygen"};
    public static final String[] SCUBA_AIR_ITEMS_DEFAULT = {"ic2:airCell=1000>ic2:cell"};
    public static int SCUBA_TANK_CAPACITY = 3000;
    public static int SCUBA_AIR_PER_SECOND = 10;
    public static int SCUBA_AIR_INTERVAL_TICKS = 20;
    public static int SCUBA_LOW_AIR_WARNING = 600;
    public static boolean SCUBA_WARNING_SOUND = true;
    public static String[] SCUBA_BREATHABLE_FLUIDS = SCUBA_BREATHABLE_DEFAULT;
    public static boolean SCUBA_OWN_AIR = true;
    public static String[] SCUBA_AIR_ITEMS = SCUBA_AIR_ITEMS_DEFAULT;
    public static int COMPRESSOR_TANK_CAPACITY = 16000;
    public static int COMPRESSOR_HAND_PUMP_AMOUNT = 100;
    public static int COMPRESSOR_HAND_PUMP_COOLDOWN_TICKS = 4;
    public static int COMPRESSOR_AIR_PER_FUEL_TICK = 10;
    public static int COMPRESSOR_FILL_PER_TICK = 50;
    public static int COMPRESSOR_INTERVAL_TICKS = 10;
    public static boolean COMPRESSOR_NEEDS_OPEN_AIR = true;

    // ---------------------------------------------------------------- flamethrower
    public static int FLAMETHROWER_TANK_CAPACITY = 4000;
    public static int FLAMETHROWER_FUEL_PER_BURST = 50;
    public static int FLAMETHROWER_FIRE_INTERVAL_TICKS = 4;
    public static double FLAMETHROWER_RANGE = 6.0;
    public static double FLAMETHROWER_CONE_DEGREES = 25.0;
    public static double FLAMETHROWER_DAMAGE = 2.0;
    public static int FLAMETHROWER_FIRE_SECONDS = 5;
    public static boolean FLAMETHROWER_PVP = false;
    public static boolean FLAMETHROWER_IGNITES_BLOCKS = false;
    public static int FLAMETHROWER_PILOT_FUEL_PER_MINUTE = 0;
    public static boolean FLAMETHROWER_SOUND = true;

    // ---------------------------------------------------------------- inflatable boat
    public static double BOAT_MAX_SPEED = 0.35;
    public static boolean BOAT_MOTOR_ENABLED = true;
    public static BoatMotor.Mode BOAT_MOTOR_MODE = BoatMotor.Mode.TOGGLE;
    public static double BOAT_MOTOR_MAX_SPEED = 0.6;
    public static double BOAT_MOTOR_ACCELERATION = 2.0;
    public static int BOAT_MOTOR_FUEL_PER_TICK = 1;
    public static int BOAT_MOTOR_FUEL_INTERVAL = 10;
    public static int BOAT_MOTOR_TANK_CAPACITY = 6000;
    public static int BOAT_MOTOR_OUT_OF_WATER_TICKS = 10;
    public static boolean BOAT_MOTOR_SOUND = true;
    public static double BOAT_MOTOR_SOUND_VOLUME = 0.6;
    public static boolean BOAT_MOTOR_MESSAGES = false;

    // ---------------------------------------------------------------- fuels and fuel station
    public static String[] FUELS = FuelRules.DEFAULTS;
    public static int FUELSTATION_TANK_CAPACITY = 16000;

    // ---------------------------------------------------------------- sleeping bag
    public static boolean SLEEPINGBAG_PLACEABLE = true;
    public static boolean SLEEPINGBAG_SETS_SPAWN = true;

    // ---------------------------------------------------------------- crossbow
    /** Charges of a full Crossbow Magazine (key crossbow.magazine.capacity; the field keeps its old name for the renderer). */
    public static int CROSSBOW_SHOTS_PER_MAGAZINE = 6;
    public static int MAGAZINE_LOAD_TICKS = 4;
    public static boolean MAGAZINE_RELOAD_RECIPE = true;
    public static boolean MAGAZINE_LEGACY_RECIPE = true;
    public static final String[] CROSSBOW_AMMO_DEFAULT = {"adventurebackpack:bolt", "minecraft:arrow"};
    public static String[] CROSSBOW_AMMO_LIST = CROSSBOW_AMMO_DEFAULT;
    public static double CROSSBOW_BOLT_DAMAGE_MULTIPLIER = 1.5;
    public static int CROSSBOW_SHOT_DELAY_TICKS = 4;
    public static int CROSSBOW_RELOAD_TICKS = 30;
    public static float CROSSBOW_BOLT_VELOCITY = 3.0f;
    public static double CROSSBOW_BOLT_DAMAGE = 4.0;
    public static boolean CROSSBOW_IGNORE_HURT_COOLDOWN = true;
    public static boolean CROSSBOW_BOLTS_RECOVERABLE = false;
    public static int CROSSBOW_DURABILITY = 0;
    public static boolean GRAPPLE_ENABLED = true;
    public static double GRAPPLE_MAX_DISTANCE = 32.0;
    public static double GRAPPLE_HOOK_SPEED = 2.5;
    public static double GRAPPLE_PULL_SPEED = 0.8;
    public static double GRAPPLE_RELEASE_DISTANCE = 1.5;
    public static int GRAPPLE_MAX_PULL_TICKS = 60;
    public static int GRAPPLE_COOLDOWN_TICKS = 20;
    public static int GRAPPLE_CHARGES = 1;
    public static boolean GRAPPLE_NO_FALL_DAMAGE = true;
    public static boolean SNIPER_ENABLED = true;
    public static double SNIPER_DAMAGE = 12.0;
    public static float SNIPER_VELOCITY = 5.0f;
    public static int SNIPER_SHOT_DELAY_TICKS = 30;
    public static double SNIPER_STILL_TOLERANCE = 0.02;
    public static float SNIPER_ZOOM = 0.35f;

    // ---------------------------------------------------------------- piston boots
    public static float PISTONBOOTS_STEP_HEIGHT = 1.001f;
    public static boolean PISTONBOOTS_SPRINT_SPEED = true;
    public static boolean PISTONBOOTS_ONLY_WHILE_SPRINTING = true;
    public static int PISTONBOOTS_SAFE_FALL_DISTANCE = 8;

    // ---------------------------------------------------------------- campfire
    public static boolean CAMPFIRE_COOKING_ENABLED = true;
    public static int CAMPFIRE_TICKS_PER_ITEM = 200;
    public static boolean CAMPFIRE_ONLY_FOOD = true;
    public static int CAMPFIRE_MAX_PER_USE = 8;
    public static double CAMPFIRE_RETURN_RANGE = 4.0;

    // ---------------------------------------------------------------- render (client)
    public static boolean RENDER_BACKPACK_ON_PLAYER = true;
    public static int GUI_TANK_RENDER = 3;
    public static int RENDER_BOAT_SMOKE_TICKS = 8;
    public static boolean HOVERING_TEXT_TANKS = false;
    public static boolean STATUS_OVERLAY = true;
    public static boolean TANKS_OVERLAY = true;
    public static int RENDER_SCUBA_BUBBLE_TICKS = 10;
    public static int RENDER_FLAMETHROWER_PARTICLES = 6;

    // ---------------------------------------------------------------- keys (client, defaults only)
    public static int KEY_OPEN_BACKPACK = Keyboard.KEY_B;
    public static int KEY_TOGGLE = Keyboard.KEY_N;
    public static int KEY_CROSSBOW_MODE = Keyboard.KEY_V;
    public static int KEY_TOGGLE_ABILITIES = 0;
    public static int KEY_BOAT_MOTOR_TOGGLE = 0;
    public static boolean TOOL_CYCLE_ENABLED = true;
    public static ToolCycleInput.Mode TOOL_CYCLE_MODE = ToolCycleInput.Mode.BOTH;
    public static boolean TOOL_CYCLE_INVERT_WHEEL = false;
    public static int KEY_TOOL_MODIFIER = 0;
    public static int KEY_NEXT_TOOL = 0;
    public static int KEY_PREVIOUS_TOOL = 0;

    // ---------------------------------------------------------------- worldgen, recipes, tweaks
    public static boolean BONUS_CHEST_ALLOWED = false;
    public static boolean PIGMAN_ALLOWED = false;
    public static boolean SADDLE_RECIPE = true;
    public static BoatRecipeRules.Mode BOAT_RECIPE_MODE = BoatRecipeRules.Mode.AUTO;
    public static final String[] BOAT_RUBBER_DEFAULT = {"itemRubber", "plateRubber", "ingotRubber"};
    public static String[] BOAT_RUBBER = BOAT_RUBBER_DEFAULT;
    public static final String[] BOAT_MOTORS_DEFAULT = {"ic2:elemotor", "item:gregtech:gt.metaitem.01:32600", "ore:craftingElectricMotor", "stack:ThermalExpansion:powerCoilGold"};
    public static String[] BOAT_MOTORS = BOAT_MOTORS_DEFAULT;
    public static BoatRecipeRules.Mode FUELSTATION_RECIPE_MODE = BoatRecipeRules.Mode.AUTO;
    public static final String[] FUELSTATION_PLATES_DEFAULT = {"plateSteel", "plateIron"};
    public static String[] FUELSTATION_PLATES = FUELSTATION_PLATES_DEFAULT;
    public static BoatRecipeRules.Mode FLAMETHROWER_RECIPE_MODE = BoatRecipeRules.Mode.AUTO;
    public static final String[] FLAMETHROWER_PLATES_DEFAULT = {"plateSteel", "plateIron"};
    public static String[] FLAMETHROWER_PLATES = FLAMETHROWER_PLATES_DEFAULT;
    public static boolean FIX_LEAD = true;
    public static boolean HORSES_FOLLOW_OWNER = true;
    public static ToolCycleFilter.Mode TOOL_CYCLE_LIST_MODE = ToolCycleFilter.Mode.NONE;
    public static ToolCycleFilter TOOL_CYCLE_FILTER = ToolCycleFilter.EMPTY;

    // ---------------------------------------------------------------- compat (each bridge only acts when its mod is loaded)
    public static boolean COMPAT_GRAVES_ENABLED = true;
    public static boolean COMPAT_BAUBLES_ENABLED = true;

    /** Order in which each category writes its keys: insertion order, so {@code enabled} flags come first. */
    private static final Map<String, List<String>> ORDER = new HashMap<String, List<String>>();

    public static void init(File configFile)
    {
        if (config == null)
        {
            config = new Configuration(configFile);
            load(config);
        }
    }

    /**
     * Builds the category tree, moves the old flat keys into it, reads every value into the fields and saves the
     * file when something was added or moved. Public so the unit test can run it on a temporary file.
     */
    public static void load(Configuration cfg)
    {
        ORDER.clear();
        declareCategories(cfg);
        migrateLegacy(cfg);
        readEnabled(cfg);
        readBackpack(cfg);
        readAbilities(cfg);
        readHose(cfg);
        readCopterPack(cfg);
        readSteamJetpack(cfg);
        readScuba(cfg);
        readFlamethrower(cfg);
        readInflatableBoat(cfg);
        readFuels(cfg);
        readFuelStation(cfg);
        readCrossbow(cfg);
        readPistonBoots(cfg);
        readSleepingBag(cfg);
        readCampfire(cfg);
        readRender(cfg);
        readKeys(cfg);
        readWorldgenRecipesTweaks(cfg);
        readCompat(cfg);
        validate();
        for (Map.Entry<String, List<String>> e : ORDER.entrySet())
        {
            cfg.setCategoryPropertyOrder(e.getKey(), e.getValue());
        }
        if (cfg.hasChanged())
        {
            cfg.save();
        }
    }

    /** Creates the categories in the order they are written: children of a category keep their creation order. */
    private static void declareCategories(Configuration cfg)
    {
        cfg.setCategoryComment(ROOT, "Adventure Backpack configuration. Change a value and restart the game; nothing needs recompiling.");
        cfg.setCategoryComment(ENABLED, "One switch per system. A disabled system stays registered (worlds and servers still load it) but it is hidden, has no recipe and does nothing.");
        cfg.setCategoryComment(BACKPACK, "The Adventure Backpack itself, worn or placed.");
        cfg.setCategoryComment(ABILITIES, "Special abilities of the backpacks while they are worn (or placed, for Cactus and Melon). Each backpack has its own switch. Ticks: 20 per second. Abilities run on the server and on the client, so use the same values on both.");
        String[] abilities = {"bat", "squid", "pigman", "dragon", "cactus", "melon", "pig", "slime", "chicken", "creeper", "cow", "mooshroom", "wolf", "ocelot", "rainbow", "sunflower", "skeleton", "irongolem", "spider", "blaze"};
        for (String a : abilities)
        {
            cfg.getCategory(ABILITIES + "." + a);
        }
        cfg.setCategoryComment(HOSE, "The Backpack Hose: suck, spill and drink from the worn backpack's tanks. Amounts in millibuckets (1000 = one bucket).");
        cfg.setCategoryComment(COPTERPACK, "The Copter Pack. It burns the liquid fuels of fuels.list, so it works with any mod that adds one of them (BuildCraft, Forestry, Railcraft, GregTech...). Speeds in blocks per tick.");
        cfg.setCategoryComment(STEAMJETPACK, "The Steam Jetpack. Its boiler needs a 'steam' fluid: another mod's, or its own (ownSteamFluid). Speeds in blocks per tick.");
        cfg.setCategoryComment(SCUBA, "The Scuba Gear: two air tanks on the back and a diving mask, worn in the back slot like the jetpack. While the head is under water it keeps the air bar full and spends the air of its tanks (left tank first). Air is the 'air' fluid (GregTech's when present, its own otherwise, see ownAirFluid) or any other fluid of breathableFluids. Ticks: 20 per second; amounts in millibuckets.");
        cfg.setCategoryComment(SCUBA_COMPRESSOR, "The Air Compressor block: fills the Scuba Gear in its slot, air cells and other air containers. It makes air with a hand pump (right click with an empty hand) and with a furnace that burns any furnace fuel, and takes air or oxygen from pipes of other mods. No electricity.");
        cfg.setCategoryComment(FLAMETHROWER, "The Flamethrower: the Flamethrower Pack (two fuel tanks and a pyro mask, worn in the back slot like the jetpack) and the Flamethrower nozzle held in hand. Sneak + the toggle key (keys.toggle) lights or puts out the pack's pilot; while it is lit, holding right click with the nozzle fires bursts of flame in a cone that set mobs on fire. The tanks take the fuels of fuels.list (the Fuel Station fills them). The wearer is never hurt. Ticks: 20 per second; amounts in millibuckets.");
        cfg.setCategoryComment(INFLATABLEBOAT, "The Inflatable Boat.");
        cfg.setCategoryComment(INFLATABLEBOAT_MOTOR, "The outboard motor of the Motorized Inflatable Boat. It burns a liquid fuel from fuels.list, put in its tank at the Fuel Station (right click the station while riding the boat). With the defaults the boat goes almost twice as fast and a full tank lasts about 5 minutes of running. Without fuel, or with the motor off, the boat moves like the plain one. Picking the boat up keeps its fuel in the item. Speeds in blocks per tick; ticks: 20 per second.");
        cfg.setCategoryComment(FUELS_CATEGORY, "Liquid fuels, one list for everything that burns them (the Copter Pack, the Flamethrower Pack, the motor of the Motorized Inflatable Boat, the Fuel Station and what it fills). A fuel is known by the fluid name the mod that adds it registers, so no fuel mod is needed: BuildCraft oil and fuel, Forestry bioethanol and biomass, Railcraft creosote, Thermal Foundation coal, GregTech fuel (diesel), bioethanol and creosote.");
        cfg.setCategoryComment(FUELSTATION, "The Fuel Station: a block with a fuel tank that fills the Copter Pack and the Flamethrower Pack (in its slot, worn or held), the tank of the motorized boat the player rides, and buckets, cans or cells. Pipes of any mod fill it from any side. It does no work on its own between actions.");
        cfg.setCategoryComment(CROSSBOW, "The Clockwork Crossbow: a repeating crossbow fed with reusable Crossbow Magazines. Hold right click to fire the magazine one bolt after another; when it is empty the next click puts in the first loaded magazine of the inventory (hotbar first) and the empty one comes back to the inventory (or drops at the feet when the inventory is full). Sneak + right click swaps a partly used magazine, which comes back with the charges it has left. Without a loaded magazine a click gives back the empty one. The crossbow mode key (keys.crossbowMode, V by default) changes the firing mode; the mode is kept on the crossbow. Ticks: 20 per second.");
        cfg.setCategoryComment(CROSSBOW_GRAPPLE, "Grappling hook mode of the crossbow. Right click fires a hook that catches the first solid block in range and pulls the player to it. Sneak or jump to let go; it also lets go on arrival, after maxPullTicks, or when the crossbow leaves the hand. The server decides everything; the client only moves its own player with the speed the server sent.");
        cfg.setCategoryComment(CROSSBOW_SNIPER, "Sniper mode of the crossbow. Sneak on the ground and stand still to deploy the bipod: the view zooms in, and right click fires one bolt with more damage and speed, no spread and a slower rate. Moving, jumping or standing up folds the bipod; folded, the crossbow fires its normal bolts. With the bipod down sneak + right click fires instead of swapping the magazine (an empty magazine still reloads by itself).");
        cfg.setCategoryComment(CROSSBOW_MAGAZINE, "The Crossbow Magazine (the old Bolt Cartridge): a reusable magazine. Right click it (hold the click) to load it with ammunition of crossbow.ammo.list from the inventory, one charge per click sound, until it is full. A magazine holds one kind of ammunition; an empty one takes any. Old cartridges without a charge count load as full magazines of arrows. Ticks: 20 per second.");
        cfg.setCategoryComment(CROSSBOW_AMMO, "What the Crossbow Magazine is loaded with. Bolts fired from a magazine of Crossbow Bolts hit harder than those from a magazine of arrows.");
        cfg.setCategoryComment(PISTONBOOTS, "The Piston Boots.");
        cfg.setCategoryComment(SLEEPINGBAG, "The sleeping bag, deployed from a placed backpack or placed on its own.");
        cfg.setCategoryComment(CAMPFIRE, "The Campfire: a respawn point for the sleeping bag, and a fire to cook on.");
        cfg.setCategoryComment(CAMPFIRE_COOKING, "Right click the campfire with raw food to cook it. The campfire holds one kind of item at a time and cooks it one by one; the cooked items go into the inventory of the player who put them there when that player is close, or pop out on top of the campfire. No fuel. Breaking the campfire drops what is still raw. Ticks: 20 per second.");
        cfg.setCategoryComment(RENDER, "Client only: how the mod draws things. The server ignores this category.");
        cfg.setCategoryComment(KEYS, "Client only: default keys, as LWJGL key codes (B = 48, N = 49, see the LWJGL Keyboard class). They are only defaults: once a key is changed in Options > Controls, Minecraft keeps that choice.");
        cfg.setCategoryComment(WORLDGEN, "Backpacks found in the world.");
        cfg.setCategoryComment(RECIPES, "Extra recipes.");
        cfg.setCategoryComment(RECIPES_BOAT, "Recipes of the Inflatable Boat (only when enabled.inflatableBoat is on). Vanilla: Inflatable Chambers (leather, slime ball, leather), five of them with string and a Hose Nozzle as the valve make the boat; a piston, a furnace and Hydro Blades make it a motorized boat. With technical mods, rubber makes the chambers (three rubber, two chambers) and an electric motor plus Hydro Blades makes the motorized boat. The recipes are read once at startup: needs a restart.");
        cfg.getCategory(RECIPES_BOAT).setRequiresMcRestart(true);
        cfg.setCategoryComment(RECIPES_FUELSTATION, "Recipe of the Fuel Station (only when enabled.fuelStation is on). Vanilla: iron ingots, glass panes, a Hose Nozzle and a Backpack Tank. With technical mods, metal plates take the place of the iron ingots. Read once at startup: needs a restart.");
        cfg.getCategory(RECIPES_FUELSTATION).setRequiresMcRestart(true);
        cfg.setCategoryComment(RECIPES_FLAMETHROWER, "Recipes of the Flamethrower Pack and the Flamethrower nozzle (only when enabled.flamethrower is on). Vanilla: iron ingots, leather, a Pyro Mask, two Backpack Tanks and flint and steel for the pilot. With technical mods, metal plates take the place of the iron ingots, and the Pyro Mask takes rubber instead of leather like the Inflatable Chamber (recipes.boat.mode and recipes.boat.rubber). Read once at startup: needs a restart.");
        cfg.getCategory(RECIPES_FLAMETHROWER).setRequiresMcRestart(true);
        cfg.setCategoryComment(TWEAKS, "Small fixes and behaviours that are not tied to one item.");
        cfg.setCategoryComment(TWEAKS_TOOL_CYCLE, "The backpack's two tool slots: which items they take, by hand and with the tool cycle (keys.toolcycle). The server checks the same list.");
        cfg.setCategoryComment(COMPAT, "Integrations with other mods. Each one only acts when its mod is loaded; nothing here is a hard dependency.");
        cfg.setCategoryComment(COMPAT_GRAVES, "Grave mods (OpenBlocks graves, GraveStone). When one of them is loaded and makes a grave, the worn backpack (or copter pack, or jetpack) goes into the grave instead of being placed as a block. Without a grave mod, backpack.placeOnDeath decides.");
        cfg.setCategoryComment(COMPAT_BAUBLES, "Baubles and Baubles Expanded. The Adventure Backpack can be worn in a bauble slot and then works as if worn the usual way (abilities, open key, hose, model on the back). Baubles Expanded gets a new back slot; the original Baubles uses the belt slot. Only one backpack is worn at a time. The default open key (B) is the same as the Baubles inventory key: change one of them in Options > Controls.");
        cfg.getCategory(KEYS).setRequiresMcRestart(true);
        cfg.setCategoryComment(TOOL_CYCLE, "Client only: swapping the held tool with the backpack's tool slots (and the hose mode). The keys themselves are changed in Options > Controls.");
    }

    /** Moves keys from the old flat "general" category into the tree, keeping their values, then drops "general". */
    static void migrateLegacy(Configuration cfg)
    {
        if (!cfg.hasCategory(Configuration.CATEGORY_GENERAL)) return;
        ConfigCategory general = cfg.getCategory(Configuration.CATEGORY_GENERAL);
        for (String[] m : LEGACY_KEYS)
        {
            Property old = general.get(m[0]);
            ConfigCategory target = cfg.getCategory(m[1]);
            if (old != null && !target.containsKey(m[2]))
            {
                old.setName(m[2]);
                target.put(m[2], old);
            }
        }
        cfg.removeCategory(general);
    }

    private static void readEnabled(Configuration cfg)
    {
        BACKPACK_ABILITIES = bool(cfg, ENABLED, "abilities", true, "Backpack abilities. When false every backpack is only cosmetic (the lightning transformation still works). Set it the same on server and clients.");
        HOSE_ENABLED = bool(cfg, ENABLED, "hose", true, "The Backpack Hose.");
        COPTERPACK_ENABLED = bool(cfg, ENABLED, "copterPack", true, "The Copter Pack. It works when some loaded mod adds a fluid of fuels.list (BuildCraft, Forestry, Railcraft, GregTech...); with none of them it stays in the creative tab but cannot be crafted and does nothing.");
        STEAMJETPACK_ENABLED = bool(cfg, ENABLED, "steamJetpack", true, "The Steam Jetpack.");
        INFLATABLEBOAT_ENABLED = bool(cfg, ENABLED, "inflatableBoat", true, "The Inflatable Boat and the Motorized Inflatable Boat.");
        CROSSBOW_ENABLED = bool(cfg, ENABLED, "crossbow", true, "The Clockwork Crossbow, its Crossbow Magazines and the Crossbow Bolts.");
        SLEEPINGBAG_ENABLED = bool(cfg, ENABLED, "sleepingBag", true, "The sleeping bag: deploying it from a placed backpack, and placing the Sleeping Bag item on its own (sleepingbag.placeable). Bags already on the ground still work and can be picked up.");
        PISTONBOOTS_ENABLED = bool(cfg, ENABLED, "pistonBoots", true, "The Piston Boots: step assist, sprint speed and fall protection.");
        SCUBA_ENABLED = bool(cfg, ENABLED, "scuba", true, "The Scuba Gear, its Air Tanks and Diving Mask, and the Air Compressor.");
        FLAMETHROWER_ENABLED = bool(cfg, ENABLED, "flamethrower", true, "The Flamethrower Pack, the Flamethrower nozzle and the Pyro Mask. When false they have no recipe, are hidden from the creative tab, and the nozzle does not fire. Set it the same on server and clients.");
        CAMPFIRE_ENABLED = bool(cfg, ENABLED, "campfire", true, "Cooking on the Campfire (see campfire.cooking). The campfire block, its recipe and the respawn point stay either way.");
        FUELSTATION_ENABLED = bool(cfg, ENABLED, "fuelStation", true, "The Fuel Station block. When false it has no recipe, is hidden from the creative tab, and a placed one neither takes nor gives fuel (it can still be broken, and keeps its fuel).");
    }

    private static void readBackpack(Configuration cfg)
    {
        BACKPACK_DEATH_PLACE = bool(cfg, BACKPACK, "placeOnDeath", true, "Place the backpack as a block where the player dies. When false it drops as an item.");
        BACKPACK_TANK_CAPACITY = integer(cfg, BACKPACK, "tankCapacity", 4000, 1000, 64000, "Capacity of each of the two backpack tanks, in millibuckets. Use the same value on server and clients. Lowering it does not delete fluid already stored above the new limit.", true);
        BACKPACK_BLAST_RESISTANCE = (float) decimal(cfg, BACKPACK, "blastResistance", 2000.0, 0.0, 6000000.0, "Explosion resistance of a placed backpack, on the same scale as Block.setResistance: 2000 is obsidian (survives TNT and creepers), 10 is stone.", true);
        BACKPACK_HARDNESS = (float) decimal(cfg, BACKPACK, "hardness", 1.0, 0.0, 100.0, "How long a placed backpack takes to break by hand. Stone is 1.5.", true);
        BACKPACK_DEATH_MESSAGE = bool(cfg, BACKPACK, "deathMessage", true, "On death, tell the player in chat where the worn backpack, copter pack or jetpack went (placed, dropped or in the grave) with its coordinates.");
    }

    private static void readAbilities(Configuration cfg)
    {
        ABILITY_EFFECT_REFRESH_TICKS = integer(cfg, ABILITIES, "effectRefreshTicks", AbilityEffects.EFFECT_REFRESH_TICKS, 2, 200, "Ticks left on a long potion effect (fire resistance, Dragon effects) under which the ability renews it.");
        ABILITY_TOGGLE_MESSAGE = bool(cfg, ABILITIES, "toggleMessage", true, "Tell the player in chat when the abilities key (keys.toggleAbilities) turns the worn backpack's abilities off or on. Each player's choice is kept with the player; enabled.abilities false keeps them off for everyone.");

        String c = ABILITIES + ".bat";
        BAT_ENABLED = bool(cfg, c, "enabled", true, "Bat Backpack: night vision.");
        BAT_NIGHT_VISION_TICKS = integer(cfg, c, "nightVisionTicks", NightVisionRefresh.DURATION, 400, 1000000, "Length of the night vision effect it gives.");
        BAT_NIGHT_VISION_REFRESH_BELOW = integer(cfg, c, "nightVisionRefreshBelow", NightVisionRefresh.REFRESH_BELOW, 201, 1000, "Ticks left under which night vision is renewed. Keep it above 200: vanilla makes the screen flicker in the last 200 ticks.");

        c = ABILITIES + ".squid";
        SQUID_ENABLED = bool(cfg, c, "enabled", true, "Squid Backpack: water breathing and night vision while in water.");
        SQUID_WORKS_ON_LAND = bool(cfg, c, "worksOnLand", false, "Give the water breathing and the night vision out of water too, for mods that make the player breathe water (merfolk). Both are taken away when the backpack comes off or this is turned off.");

        c = ABILITIES + ".pigman";
        PIGMAN_ENABLED = bool(cfg, c, "enabled", true, "Pigman Backpack: fire resistance.");
        PIGMAN_FIRE_RESISTANCE_TICKS = integer(cfg, c, "fireResistanceTicks", 5000, 100, 1000000, "Length of the fire resistance effect it gives.");

        c = ABILITIES + ".dragon";
        DRAGON_ENABLED = bool(cfg, c, "enabled", true, "Dragon Backpack: fire resistance, water breathing, night vision, strength and regeneration.");
        DRAGON_EFFECT_TICKS = integer(cfg, c, "effectTicks", AbilityEffects.DRAGON_EFFECT_TICKS, 100, 1000000, "Length of the strength and regeneration effects.");
        DRAGON_STRENGTH_AMPLIFIER = integer(cfg, c, "strengthAmplifier", AbilityEffects.DRAGON_STRENGTH_AMPLIFIER, 0, 4, "Strength level minus one (0 = Strength I, 1 = Strength II).");
        DRAGON_REGENERATION_AMPLIFIER = integer(cfg, c, "regenerationAmplifier", AbilityEffects.DRAGON_REGENERATION_AMPLIFIER, 0, 4, "Regeneration level minus one (0 = Regeneration I).");

        c = ABILITIES + ".cactus";
        CACTUS_ENABLED = bool(cfg, c, "enabled", true, "Cactus Backpack: fills its tanks with water in water or rain.");
        CACTUS_WORN_INTERVAL_TICKS = integer(cfg, c, "wornIntervalTicks", 5, 1, 1200, "Ticks between two fills while worn.");
        CACTUS_WATER_IN_WATER = integer(cfg, c, "wornWaterInWater", 2, 0, 1000, "Millibuckets added to each tank per fill while the player is in water.");
        CACTUS_WATER_IN_RAIN = integer(cfg, c, "wornWaterInRain", 1, 0, 1000, "Millibuckets added to each tank per fill while the player is under rain. Adds up with the water amount.");
        CACTUS_PLACED_INTERVAL_TICKS = integer(cfg, c, "placedIntervalTicks", 5, 1, 1200, "Ticks between two fills of a placed backpack under rain.");
        CACTUS_PLACED_RAIN_AMOUNT = integer(cfg, c, "placedRainAmount", 2, 0, 1000, "Millibuckets added to each tank per fill of a placed backpack under rain.");

        c = ABILITIES + ".melon";
        MELON_ENABLED = bool(cfg, c, "enabled", true, "Melon Backpack: fills its tanks with melon juice in water or rain.");
        MELON_WORN_INTERVAL_TICKS = integer(cfg, c, "wornIntervalTicks", 5, 1, 1200, "Ticks between two fills while worn.");
        MELON_JUICE_IN_WATER = integer(cfg, c, "wornJuiceInWater", 2, 0, 1000, "Millibuckets added to each tank per fill while the player is in water.");
        MELON_JUICE_IN_RAIN = integer(cfg, c, "wornJuiceInRain", 1, 0, 1000, "Millibuckets added to each tank per fill while the player is under rain. Adds up with the water amount.");
        MELON_PLACED_INTERVAL_TICKS = integer(cfg, c, "placedIntervalTicks", 5, 1, 1200, "Ticks between two fills of a placed backpack under rain.");
        MELON_PLACED_RAIN_AMOUNT = integer(cfg, c, "placedRainAmount", 2, 0, 1000, "Millibuckets added to each tank per fill of a placed backpack under rain.");

        c = ABILITIES + ".pig";
        PIG_ENABLED = bool(cfg, c, "enabled", true, "Pig Backpack: oinks now and then.");
        PIG_OINK_MAX_SECONDS = integer(cfg, c, "oinkMaxSeconds", 60, 1, 3600, "Longest wait between two oinks; each wait is random between 0 and this.");

        c = ABILITIES + ".slime";
        SLIME_ENABLED = bool(cfg, c, "enabled", true, "Slime Backpack: slime trail and sound while sprinting, slippery stops.");
        SLIME_TRAIL_INTERVAL_TICKS = integer(cfg, c, "trailIntervalTicks", 5, 1, 200, "Ticks between two slime particles and sounds while sprinting.");
        SLIME_FULL_MOON_SPEED = bool(cfg, c, "fullMoonSpeed", true, "Give Speed II at night under a full moon.");

        c = ABILITIES + ".chicken";
        CHICKEN_ENABLED = bool(cfg, c, "enabled", true, "Chicken Backpack: lays an egg now and then.");
        CHICKEN_EGG_MIN_SECONDS = integer(cfg, c, "eggMinSeconds", 200, 1, 86400, "Shortest wait between two eggs.");
        CHICKEN_EGG_RANDOM_SECONDS = integer(cfg, c, "eggRandomSeconds", 90, 0, 86400, "Random extra wait added to the shortest one (0 to this).");

        c = ABILITIES + ".creeper";
        CREEPER_ENABLED = bool(cfg, c, "enabled", true, "Creeper Backpack: sneak near another player to play the creeper hiss.");
        CREEPER_SCARE_COOLDOWN_SECONDS = integer(cfg, c, "scareCooldownSeconds", 120, 1, 86400, "Wait after a hiss before the next one.");
        CREEPER_CHECK_INTERVAL_TICKS = integer(cfg, c, "checkIntervalTicks", 20, 1, 1200, "Ticks between two searches for a nearby player while sneaking.");
        CREEPER_SCARE_RANGE = decimal(cfg, c, "scareRange", 3.0, 1.0, 5.0, "Distance in blocks to the other player for the hiss.");
        CREEPER_DEATH_EXPLOSION = bool(cfg, c, "deathExplosion", true, "Explode when the wearer dies. The explosion does not break blocks.");
        CREEPER_DEATH_EXPLOSION_POWER = (float) decimal(cfg, c, "deathExplosionPower", 4.0, 0.0, 10.0, "Power of the death explosion (TNT is 4).");

        c = ABILITIES + ".cow";
        COW_ENABLED = bool(cfg, c, "enabled", true, "Cow Backpack: eats wheat from its inventory and fills a tank with milk.");
        COW_WHEAT_PER_BATCH = integer(cfg, c, "wheatPerBatch", 16, 1, 64, "Wheat eaten to make one batch of milk.");
        COW_MILK_PER_BATCH = integer(cfg, c, "milkPerBatch", 1000, 1, 64000, "Millibuckets of milk per batch, made one per tick.");
        COW_EAT_INTERVAL_TICKS = integer(cfg, c, "eatIntervalTicks", 20, 1, 1200, "Ticks between two wheat.");

        c = ABILITIES + ".mooshroom";
        MOOSHROOM_ENABLED = bool(cfg, c, "enabled", true, "Mooshroom Backpack: eats wheat from its inventory and fills a tank with mushroom stew.");
        MOOSHROOM_WHEAT_PER_BATCH = integer(cfg, c, "wheatPerBatch", 16, 1, 64, "Wheat eaten to make one batch of stew.");
        MOOSHROOM_STEW_PER_BATCH = integer(cfg, c, "stewPerBatch", 1000, 1, 64000, "Millibuckets of stew per batch, made one per tick.");
        MOOSHROOM_EAT_INTERVAL_TICKS = integer(cfg, c, "eatIntervalTicks", 20, 1, 1200, "Ticks between two wheat.");

        c = ABILITIES + ".wolf";
        WOLF_ENABLED = bool(cfg, c, "enabled", true, "Wolf Backpack: angry wolves that target the wearer calm down.");
        WOLF_CHECK_INTERVAL_TICKS = integer(cfg, c, "checkIntervalTicks", 20, 1, 1200, "Ticks between two searches for wolves.");
        WOLF_RANGE = decimal(cfg, c, "range", 16.0, 1.0, 64.0, "Horizontal search distance in blocks.");

        c = ABILITIES + ".ocelot";
        OCELOT_ENABLED = bool(cfg, c, "enabled", true, "Ocelot Backpack: creepers run away from the wearer.");
        OCELOT_CHECK_INTERVAL_TICKS = integer(cfg, c, "checkIntervalTicks", 20, 1, 1200, "Ticks between two searches for creepers.");
        OCELOT_RANGE = decimal(cfg, c, "range", 16.0, 1.0, 64.0, "Horizontal search distance in blocks.");

        c = ABILITIES + ".rainbow";
        RAINBOW_ENABLED = bool(cfg, c, "enabled", true, "Rainbow Backpack: eating a golden apple starts the Nyan run (speed, jump, particles).");
        RAINBOW_DURATION_SECONDS = integer(cfg, c, "durationSeconds", 150, 4, 3600, "Length of the Nyan run, including the 3 second intro. The song lasts 150 seconds.");
        RAINBOW_SPEED_AMPLIFIER = integer(cfg, c, "speedAmplifier", 2, 0, 9, "Speed level minus one during the run.");
        RAINBOW_JUMP_AMPLIFIER = integer(cfg, c, "jumpAmplifier", 2, 0, 9, "Jump boost level minus one during the run.");
        RAINBOW_SOUND = bool(cfg, c, "sound", true, "Play the song (client only).");

        c = ABILITIES + ".sunflower";
        SUNFLOWER_ENABLED = bool(cfg, c, "enabled", true, "Sunflower Backpack: feeds the wearer in the sun.");
        SUNFLOWER_INTERVAL_SECONDS = integer(cfg, c, "intervalSeconds", 120, 1, 3600, "Seconds between two meals.");
        SUNFLOWER_FOOD = integer(cfg, c, "food", 2, 0, 20, "Hunger points per meal (2 = one drumstick).");
        SUNFLOWER_SATURATION = (float) decimal(cfg, c, "saturation", 0.2, 0.0, 2.0, "Saturation modifier per meal.");

        c = ABILITIES + ".skeleton";
        SKELETON_ENABLED = bool(cfg, c, "enabled", true, "Skeleton Backpack: bows take arrows from the backpack.");

        c = ABILITIES + ".irongolem";
        IRONGOLEM_ENABLED = bool(cfg, c, "enabled", true, "Iron Golem Backpack: no fall damage.");

        c = ABILITIES + ".spider";
        SPIDER_ENABLED = bool(cfg, c, "enabled", true, "Spider Backpack: tame spiders and ride them. Wearing it, right click a spider with the tame item to try to tame it (like taming a wolf). A tamed spider is yours for good, like a wolf: it stays when you log out, die or change dimension, follows you (with or without the backpack) and climbs walls after you. Right click it to ride it while wearing the Spider Backpack; sneak and right click to make it sit or stand up; right click with the heal item to heal it; sneak and right click with the tame item to set it free. When false, no new spider can be tamed and nobody can ride one; tamed spiders keep following, sitting and healing.");
        SPIDER_TAME_ITEM = text(cfg, c, "tameItem", "minecraft:spider_eye", "Item used to tame a spider, as modid:name. One is used up per try (not in creative). Sneaking and right clicking your tamed spider with it sets the spider free.");
        SPIDER_TAME_CHANCE = decimal(cfg, c, "tameChance", 0.33, 0.0, 1.0, "Chance that one try tames the spider (1 = always, the wolf is about 0.33).");
        SPIDER_MAX_TAMED = integer(cfg, c, "maxTamed", 2, 0, 16, "Tamed spiders a player may have at once. 0 turns taming off.");
        SPIDER_FOLLOW_START_DISTANCE = decimal(cfg, c, "followStartDistance", 6.0, 2.0, 32.0, "A tamed spider starts walking to its owner when it is farther than this, in blocks.");
        SPIDER_FOLLOW_STOP_DISTANCE = decimal(cfg, c, "followStopDistance", 3.0, 1.0, 32.0, "It stops when it is this close to its owner, in blocks.");
        SPIDER_TELEPORT_DISTANCE = integer(cfg, c, "teleportDistance", 24, 0, 128, "When it cannot walk to its owner and is farther than this, it jumps next to the owner like a wolf. 0 = never.");
        SPIDER_TELEPORT_STUCK_DISTANCE = integer(cfg, c, "teleportStuckDistance", 8, 0, 64, "When it gets no closer for twice stuckTicks and is at least this far from its owner, it jumps next to the owner. 0 = only when farther than teleportDistance.");
        SPIDER_TELEPORT_COOLDOWN_TICKS = integer(cfg, c, "teleportCooldownTicks", 100, 20, 1200, "Ticks between two jumps next to the owner.");
        SPIDER_TAMED_HEALTH = decimal(cfg, c, "tamedHealth", 20.0, 1.0, 100.0, "Max health of a tamed spider, in half hearts (a wild one has 16). Taming heals it to this. Applied again when a spider loads, so a change reaches existing spiders.");
        SPIDER_HEAL_ITEM = text(cfg, c, "healItem", "minecraft:rotten_flesh", "Item that heals your tamed spider on right click, as modid:name. One is used up per heal (not in creative); nothing happens at full health.");
        SPIDER_HEAL_AMOUNT = integer(cfg, c, "healAmount", 4, 1, 40, "Health one heal item gives back, in half hearts.");
        SPIDER_VERIFY_LOADS_CHUNKS = bool(cfg, c, "verifyLoadsChunks", true, "On login, and when the maxTamed limit blocks a tame, check that every spider counted for the player still exists, loading its chunk for a moment if needed. Counted spiders that are gone (removed by a world edit, for example) stop counting. When false, only spiders in loaded chunks are checked.");
        SPIDER_SMART_CLIMB = bool(cfg, c, "smartClimb", true, "A tamed spider plans its way up walls and towers to an owner above, and down cliffs to an owner below, and only climbs when it needs to (it does not hang on trees or walls while wandering). When false it climbs any wall it touches, like a wild spider, and only walks and teleports to follow.");
        SPIDER_CLIMB_MAX_HEIGHT = integer(cfg, c, "climbMaxHeight", 32, 2, 128, "Highest wall it climbs up or down to follow its owner, in blocks.");
        SPIDER_SAFE_DROP_HEIGHT = integer(cfg, c, "safeDropHeight", 3, 0, 8, "It walks off a ledge this high or lower instead of climbing down it.");
        SPIDER_REPATH_TICKS = integer(cfg, c, "repathTicks", 10, 5, 40, "Ticks between two path checks while it follows its owner.");
        SPIDER_STUCK_TICKS = integer(cfg, c, "stuckTicks", 60, 20, 400, "Ticks without getting closer before it tries another way up; after twice this it jumps next to its owner (see teleportStuckDistance) or rests a while.");

        c = ABILITIES + ".blaze";
        BLAZE_ENABLED = bool(cfg, c, "enabled", true, "Blaze Backpack: fire immunity and burning punches.");
        BLAZE_FIRE_IMMUNITY = bool(cfg, c, "fireImmunity", true, "The wearer does not burn in fire or lava, like a magma cube (fire resistance, taken away when the backpack comes off).");
        BLAZE_FIRE_RESISTANCE_TICKS = integer(cfg, c, "fireResistanceTicks", 5000, 100, 1000000, "Length of the fire resistance effect it gives; it is renewed before it runs out (abilities.effectRefreshTicks).");
        BLAZE_PUNCH_FIRE_SECONDS = integer(cfg, c, "punchFireSeconds", 4, 0, 60, "Seconds a punched mob or player burns. 0 turns the burning punch off.");
        BLAZE_PUNCH_WITH_NON_WEAPONS = bool(cfg, c, "punchWithNonWeapons", true, "Hitting with an item that is not a weapon (blocks, food, sticks...) also counts as a punch. When false only the empty hand burns. Swords, tools, bows and items that add attack damage are weapons.");
    }

    private static void readHose(Configuration cfg)
    {
        HOSE_TRANSFER_AMOUNT = integer(cfg, HOSE, "transferAmount", 1000, 1, 64000, "Millibuckets moved per click between the backpack and a tank block (suck or spill mode). Fluid blocks in the world are always one bucket.");
        HOSE_DRINK_AMOUNT = integer(cfg, HOSE, "drinkAmount", 1000, 1, 64000, "Millibuckets drunk per sip in drink mode.");
        HOSE_DRINK_TICKS = integer(cfg, HOSE, "drinkTicks", 24, 1, 200, "Ticks a sip takes.");
        HOSE_MILK_AMOUNT = integer(cfg, HOSE, "milkAmount", 1000, 1, 64000, "Millibuckets of milk or stew taken from a cow or mooshroom per click.");
    }

    private static void readCopterPack(Configuration cfg)
    {
        ALLOW_COPTER_SOUND = bool(cfg, COPTERPACK, "sound", true, "Play the rotor sound (client only; other players may still hear theirs).");
        COPTER_TANK_CAPACITY = integer(cfg, COPTERPACK, "tankCapacity", 6000, 1000, 64000, "Fuel tank capacity in millibuckets. Use the same value on server and clients.", true);
        COPTER_FUEL_INTERVAL_TICKS = integer(cfg, COPTERPACK, "fuelIntervalTicks", 3, 1, 200, "Fuel is taken once every this many ticks: the cost of all those ticks at once.");
        COPTER_FUEL_PER_TICK = integer(cfg, COPTERPACK, "fuelPerTick", 2, 0, 100, "Millibuckets of fuel per tick while the engine runs, before the fuel's own multiplier in fuels.list (oil 1.5, fuel 1.0, creosote 1.8...). With the defaults a full tank lasts about 2.5 minutes of gliding, 75 seconds of hovering and 50 seconds of climbing while hovering.");
        COPTER_HOVER_EXTRA_FUEL = integer(cfg, COPTERPACK, "hoverExtraFuel", 2, 0, 100, "Extra fuel per tick while hovering (not while sinking with sneak).");
        COPTER_CLIMB_EXTRA_FUEL = integer(cfg, COPTERPACK, "climbExtraFuel", 2, 0, 100, "Extra fuel per tick while climbing (jump held).");
        COPTER_START_FUEL = integer(cfg, COPTERPACK, "startFuel", 100, 0, 64000, "Fuel the engine takes when it is turned on. It does not start with less in the tank. 0 turns it off.");
        COPTER_LIFT_SPEED = decimal(cfg, COPTERPACK, "liftSpeed", 0.15, 0.01, 2.0, "Climbing speed.");
        COPTER_DESCEND_SPEED = decimal(cfg, COPTERPACK, "descendSpeed", 0.3, 0.01, 2.0, "Descending speed while sneaking.");
        COPTER_GLIDE_FACTOR = decimal(cfg, COPTERPACK, "glideFactor", 0.6, 0.0, 1.0, "Falling speed is multiplied by this every tick in normal mode (lower = slower fall).");
        COPTER_AIR_CONTROL = (float) decimal(cfg, COPTERPACK, "airControl", 0.03, 0.0, 0.5, "Horizontal acceleration while flying. Top horizontal speed is about (0.02 + this) / 0.09 blocks per tick: 0.03 gives about 11 blocks per second.");
        COPTER_MAX_ALTITUDE = integer(cfg, COPTERPACK, "maxAltitude", 90, 1, 255, "Height above which lift fades out: climbing stops about liftSpeed x 100 blocks higher. Hover mode holds any height already reached.");
        COPTER_PUSH_RANGE = decimal(cfg, COPTERPACK, "itemPushRange", 4.0, 0.0, 16.0, "Distance in blocks at which the rotor blows dropped items away. 0 turns it off.");
        COPTER_PUSH_INTERVAL_TICKS = integer(cfg, COPTERPACK, "itemPushIntervalTicks", 2, 1, 20, "The rotor looks for dropped items to blow away once every this many ticks.");
    }

    private static void readSteamJetpack(Configuration cfg)
    {
        ALLOW_JETPACK_SOUNDS = bool(cfg, STEAMJETPACK, "sound", true, "Play the boiler and thrust sounds (client only).");
        JETPACK_OWN_STEAM = bool(cfg, STEAMJETPACK, "ownSteamFluid", true, "When no other mod (Railcraft, IC2...) registers a 'steam' fluid, register Adventure Backpack's own so the jetpack boils water without them. When another mod has steam, that one is used. Set it the same on server and clients. Needs a restart.");
        cfg.getCategory(STEAMJETPACK).get("ownSteamFluid").setRequiresMcRestart(true);
        JETPACK_WATER_CAPACITY = integer(cfg, STEAMJETPACK, "waterCapacity", 6000, 1000, 64000, "Water tank capacity in millibuckets. Use the same value on server and clients.", true);
        JETPACK_STEAM_CAPACITY = integer(cfg, STEAMJETPACK, "steamCapacity", 12000, 1000, 64000, "Steam tank capacity in millibuckets. Use the same value on server and clients.", true);
        JETPACK_STEAM_PER_TICK = integer(cfg, STEAMJETPACK, "steamPerTick", 20, 0, 1000, "Steam spent per tick of thrust. With the defaults a full steam tank gives 30 seconds of thrust, and full water and steam tanks with a hot boiler about 90 seconds.");
        JETPACK_IGNITION_STEAM = integer(cfg, STEAMJETPACK, "ignitionSteam", 150, 0, 64000, "Steam spent at once each time the thrust starts, so taking off costs more than keeping it going. 0 turns it off.");
        JETPACK_THRUST_FUEL_BURN = integer(cfg, STEAMJETPACK, "thrustFuelBurn", 3, 1, 20, "Ticks of firebox burn time used per tick of thrust (1 = as fast as idle). Coal burns 1600 ticks, so 3 makes one coal last about 27 seconds of thrust.");
        JETPACK_THRUST_HEAT_LOSS_TICKS = integer(cfg, STEAMJETPACK, "thrustHeatLossTicks", 10, 0, 1200, "While thrusting the boiler loses one degree every this many ticks, down to the ambient temperature. With the fire lit it still heats up; without fuel a long flight cools it below boiling. 0 turns it off.");
        JETPACK_HEATING_SPEED = decimal(cfg, STEAMJETPACK, "heatingSpeed", 1.0, 0.1, 10.0, "How fast the firebox heats the boiler. 1 is the built-in curve (about 62 seconds from 25 to boiling at 100, then faster); 2 is twice as fast.");
        JETPACK_COOLING_SPEED = decimal(cfg, STEAMJETPACK, "coolingSpeed", 1.5, 0.1, 10.0, "How fast the boiler cools without fire. 1 is the old curve (from 200 to 100 in 5 minutes); 1.5 cools it in about 3 minutes and 20 seconds.");
        JETPACK_WATER_INTAKE = integer(cfg, STEAMJETPACK, "waterIntake", 2, 0, 1000, "Millibuckets of water taken per tick while the wearer is in water.");
        JETPACK_STEAM_PER_WATER = integer(cfg, STEAMJETPACK, "steamPerWater", 4, 1, 100, "Millibuckets of steam made from one millibucket of water.");
        JETPACK_BOILING_TEMPERATURE = integer(cfg, STEAMJETPACK, "boilingTemperature", 100, 1, 199, "Boiler temperature at which water turns into steam (the boiler tops out at 200).");
        JETPACK_THRUST = decimal(cfg, STEAMJETPACK, "thrust", 0.08, 0.01, 1.0, "Upward acceleration per tick.");
        JETPACK_MAX_RISE_SPEED = decimal(cfg, STEAMJETPACK, "maxRiseSpeed", 0.28, 0.01, 2.0, "Top climbing speed.");
        JETPACK_AIR_CONTROL = (float) decimal(cfg, STEAMJETPACK, "airControl", 0.02, 0.0, 0.5, "Horizontal acceleration while thrusting.");
        JETPACK_MAX_ALTITUDE = integer(cfg, STEAMJETPACK, "maxAltitude", 90, 1, 255, "Height above which thrust fades out: thrust holds the wearer at about maxRiseSpeed x 100 blocks higher.");
    }

    private static void readScuba(Configuration cfg)
    {
        SCUBA_TANK_CAPACITY = integer(cfg, SCUBA, "tankCapacity", 3000, 500, 64000, "Capacity of each of the two air tanks, in millibuckets. Use the same value on server and clients.", true);
        SCUBA_AIR_PER_SECOND = integer(cfg, SCUBA, "airPerSecond", 10, 0, 1000, "Millibuckets of air spent per second while it keeps the wearer breathing. With the defaults two full tanks last 10 minutes under water. 0 makes the air free.");
        SCUBA_AIR_INTERVAL_TICKS = integer(cfg, SCUBA, "airIntervalTicks", 20, 1, 200, "The air is taken once every this many ticks: the cost of all those ticks at once.");
        SCUBA_LOW_AIR_WARNING = integer(cfg, SCUBA, "lowAirWarning", 600, 0, 64000, "When the air left goes below this many millibuckets the wearer gets a chat warning, once per dive (600 is one minute with the defaults). 0 turns the warning off.");
        SCUBA_WARNING_SOUND = bool(cfg, SCUBA, "warningSound", true, "Play a click with the low air warning.");
        order(SCUBA, "breathableFluids");
        SCUBA_BREATHABLE_FLUIDS = cfg.getStringList("breathableFluids", SCUBA, SCUBA_BREATHABLE_DEFAULT, "Fluids the tanks and the compressor take, by registered name (air of GregTech or Adventure Backpack, oxygen of GregTech, Galacticraft or Mekanism...). When the 'air' fluid exists every one of them is stored as air. Set it the same on server and clients. Needs a restart.");
        cfg.getCategory(SCUBA).get("breathableFluids").setRequiresMcRestart(true);
        SCUBA_OWN_AIR = bool(cfg, SCUBA, "ownAirFluid", true, "When no other mod (GregTech) registers an 'air' fluid, register Adventure Backpack's own so the hand pump and the furnace of the compressor can make air. When another mod has air, that one is used. Set it the same on server and clients. Needs a restart.");
        cfg.getCategory(SCUBA).get("ownAirFluid").setRequiresMcRestart(true);
        order(SCUBA, "airItems");
        SCUBA_AIR_ITEMS = cfg.getStringList("airItems", SCUBA, SCUBA_AIR_ITEMS_DEFAULT, "Items that hold air without being fluid containers (IC2's Compressed Air Cell), for the container slots of the Scuba Gear and the compressor. One per line: <item>=<millibuckets>, optionally followed by ><empty item> for the item it leaves and that the compressor fills again. Items as in recipes.boat.motors: ic2:<IC2 item name>, item:<modid>:<item name>[:<damage>], ore:<Ore Dictionary name>, stack:<modid>:<registered stack name>. GregTech's air cells are fluid containers and need no entry. Needs a restart.");
        cfg.getCategory(SCUBA).get("airItems").setRequiresMcRestart(true);
        COMPRESSOR_TANK_CAPACITY = integer(cfg, SCUBA_COMPRESSOR, "tankCapacity", 16000, 1000, 64000, "Capacity of the compressor's air tank, in millibuckets. Use the same value on server and clients.", true);
        COMPRESSOR_HAND_PUMP_AMOUNT = integer(cfg, SCUBA_COMPRESSOR, "handPumpAmount", 100, 0, 16000, "Millibuckets of air one right click with an empty hand pumps in (60 clicks fill a Scuba Gear with the defaults). 0 turns the hand pump off.");
        COMPRESSOR_HAND_PUMP_COOLDOWN_TICKS = integer(cfg, SCUBA_COMPRESSOR, "handPumpCooldownTicks", 4, 0, 200, "Ticks between two strokes of the hand pump.");
        COMPRESSOR_AIR_PER_FUEL_TICK = integer(cfg, SCUBA_COMPRESSOR, "airPerFuelTick", 10, 0, 1000, "Millibuckets of air the furnace makes per tick of burning fuel: one coal (1600 ticks) makes 16000 with the defaults. 0 turns the furnace off.");
        COMPRESSOR_FILL_PER_TICK = integer(cfg, SCUBA_COMPRESSOR, "fillPerTick", 50, 1, 64000, "Millibuckets per tick moved into the Scuba Gear in the compressor's slot (an empty gear fills in 6 seconds with the defaults).");
        COMPRESSOR_INTERVAL_TICKS = integer(cfg, SCUBA_COMPRESSOR, "intervalTicks", 10, 1, 200, "The compressor works once every this many ticks, doing the work of all those ticks at once.");
        COMPRESSOR_NEEDS_OPEN_AIR = bool(cfg, SCUBA_COMPRESSOR, "needsOpenAir", true, "The hand pump and the furnace make air only when the block above the compressor is air, so it is not an air source buried or under water. Air from pipes is taken anyway.");
    }

    private static void readFlamethrower(Configuration cfg)
    {
        FLAMETHROWER_TANK_CAPACITY = integer(cfg, FLAMETHROWER, "tankCapacity", 4000, 500, 64000, "Capacity of each of the two fuel tanks of the pack, in millibuckets. Use the same value on server and clients.", true);
        FLAMETHROWER_FUEL_PER_BURST = integer(cfg, FLAMETHROWER, "fuelPerBurst", 20, 0, 10000, "Millibuckets of fuel one burst burns, times the fuel's multiplier in fuels.list (oil 1.5 makes it 30). With the defaults two full tanks of fuel make 400 bursts, 80 seconds of continuous fire. 0 makes the fuel free.");
        FLAMETHROWER_FIRE_INTERVAL_TICKS = integer(cfg, FLAMETHROWER, "fireIntervalTicks", 4, 1, 40, "Cooldown between two bursts while right click is held, in ticks. Each burst burns fuelPerBurst and deals its damage once.");
        FLAMETHROWER_RANGE = decimal(cfg, FLAMETHROWER, "range", 6.0, 1.0, 16.0, "Reach of the flame in blocks, from the wearer's eyes.");
        FLAMETHROWER_CONE_DEGREES = decimal(cfg, FLAMETHROWER, "coneDegrees", 25.0, 5.0, 90.0, "Opening of the cone of flame in degrees, around the direction the wearer looks.");
        FLAMETHROWER_DAMAGE = decimal(cfg, FLAMETHROWER, "damage", 2.0, 0.0, 40.0, "Fire damage of one burst to each target in the cone (2 is one heart). Vanilla's half second of invulnerability after a hit means not every burst lands. Fire-immune mobs take none.");
        FLAMETHROWER_FIRE_SECONDS = integer(cfg, FLAMETHROWER, "fireSeconds", 5, 0, 60, "Seconds every target in the cone keeps burning after a burst. 0 does not set targets on fire.");
        FLAMETHROWER_PVP = bool(cfg, FLAMETHROWER, "pvp", false, "Let the flame hurt and burn other players. Even when true, the server's pvp setting and scoreboard teams still apply. Animals the wearer owns are never hurt.");
        FLAMETHROWER_IGNITES_BLOCKS = bool(cfg, FLAMETHROWER, "ignitesBlocks", false, "Let each burst set fire to the side of the block it hits, like flint and steel. The fire is vanilla fire, so it spreads and burns as usual. It respects spawn protection, adventure mode and protection mods that cancel block placing.");
        FLAMETHROWER_PILOT_FUEL_PER_MINUTE = integer(cfg, FLAMETHROWER, "pilotFuelPerMinute", 0, 0, 6000, "Millibuckets of fuel the lit pilot burns per minute while the pack is worn, times the fuel's multiplier. 0 (the default) makes the pilot free.");
        FLAMETHROWER_SOUND = bool(cfg, FLAMETHROWER, "sound", true, "Play the vanilla ignite sound when the pilot is lit (the server sends it) and the vanilla fire sound while the nozzle fires (each client plays it). Set it the same on server and clients.");
    }

    private static void readInflatableBoat(Configuration cfg)
    {
        BOAT_MAX_SPEED = decimal(cfg, INFLATABLEBOAT, "maxSpeed", 0.35, 0.07, 2.0, "Top speed in blocks per tick (the vanilla boat uses 0.35). The motorized boat without fuel, or with its motor off, uses it too.");
        BOAT_MOTOR_ENABLED = bool(cfg, INFLATABLEBOAT_MOTOR, "enabled", true, "The motor of the Motorized Inflatable Boat. False: the motor is only a look, and the boat moves like the plain one.");
        BOAT_MOTOR_MODE = BoatMotor.Mode.parse(choice(cfg, INFLATABLEBOAT_MOTOR, "mode", "toggle", BoatMotor.Mode.NAMES,
                "toggle: the motor key (keys.boatMotorToggle, set it in Options > Controls) turns the motor on and off, and the boat goes ahead on its own while the pilot steers; holding back still brakes. hold: the motor pushes while the pilot holds the forward key. Either way it stops without fuel, when the pilot gets off and out of the water. Read on the server."));
        BOAT_MOTOR_MAX_SPEED = decimal(cfg, INFLATABLEBOAT_MOTOR, "maxSpeed", 0.6, 0.05, 2.0, "Top speed while the motor runs, in blocks per tick (0.6 = 12 blocks per second; the boat without motor uses inflatableboat.maxSpeed). Use the same value on server and clients.");
        BOAT_MOTOR_ACCELERATION = decimal(cfg, INFLATABLEBOAT_MOTOR, "acceleration", 2.0, 1.0, 10.0, "How much faster than the plain boat the boat speeds up while the motor runs: 1.0 = as fast as the plain boat, 2.0 = twice as fast. Use the same value on server and clients.");
        BOAT_MOTOR_FUEL_PER_TICK = integer(cfg, INFLATABLEBOAT_MOTOR, "fuelPerTick", 1, 0, 100, "Millibuckets of fuel per tick while the motor runs, before the fuel's own multiplier (fuels.list). With the defaults a full tank of fuel lasts 5 minutes. 0 makes the motor free (it still needs some fuel in the tank).");
        BOAT_MOTOR_FUEL_INTERVAL = integer(cfg, INFLATABLEBOAT_MOTOR, "fuelIntervalTicks", 10, 1, 200, "Fuel is taken once every this many ticks of running: the cost of all those ticks at once.");
        BOAT_MOTOR_TANK_CAPACITY = integer(cfg, INFLATABLEBOAT_MOTOR, "tankCapacity", 6000, 1000, 64000, "Capacity of the motor's fuel tank in millibuckets. Use the same value on server and clients. Lowering it drops the fuel above the new limit when a boat is loaded or placed.", true);
        BOAT_MOTOR_OUT_OF_WATER_TICKS = integer(cfg, INFLATABLEBOAT_MOTOR, "outOfWaterTicks", 10, 1, 200, "The motor stops after the boat has been out of the water for this many ticks in a row (on land, or in the air over a wave or a drop).");
        BOAT_MOTOR_SOUND = bool(cfg, INFLATABLEBOAT_MOTOR, "sound", true, "Sound of the boat motor, on the client: a looping putt-putt while the motor runs, a cord pull when it starts, a cough when it stops and a dry click when the motor key cannot start it (no fuel or not on water; the server sends the click to the pilot). It replaces the chat messages as the motor's notice. False: the motor is silent.");
        BOAT_MOTOR_SOUND_VOLUME = decimal(cfg, INFLATABLEBOAT_MOTOR, "soundVolume", 0.6, 0.0, 1.0, "Volume of the boat motor's sounds, from 0.0 (silent) to 1.0, on the client. The running loop is a little quieter at low speed and louder at full speed.");
        BOAT_MOTOR_MESSAGES = bool(cfg, INFLATABLEBOAT_MOTOR, "messages", false, "Chat messages about the boat motor (on, off, out of fuel, not on water), read on the server. Off by default: the sound is the notice. Turn it on for players who cannot hear.");
    }

    private static void readFuels(Configuration cfg)
    {
        migrateFuels(cfg);
        order(FUELS_CATEGORY, "list");
        FUELS = cfg.getStringList("list", FUELS_CATEGORY, FuelRules.DEFAULTS, "Liquid fuels as fluidName=multiplier. The fuel cost of each user (copterpack.fuelPerTick...) is multiplied by it: higher burns faster. Only these fluids go into fuel tanks. Use the fluid names the mods register (see the category comment); add lava=4.0, for example, to burn lava. Moved here from copterpack.fuels, keeping its value. Set it the same on server and clients. Needs a restart.");
        cfg.getCategory(FUELS_CATEGORY).get("list").setRequiresMcRestart(true);
    }

    /** Moves the old copterpack.fuels value into fuels.list (see FuelRules.migrate) and drops the old key. */
    static void migrateFuels(Configuration cfg)
    {
        ConfigCategory copter = cfg.getCategory(COPTERPACK);
        if (!copter.containsKey("fuels")) return;
        Property old = copter.remove("fuels");
        ConfigCategory fuels = cfg.getCategory(FUELS_CATEGORY);
        if (fuels.containsKey("list")) return;
        String[] moved = FuelRules.migrate(old.isList() ? old.getStringList() : new String[]{old.getString()});
        if (moved != null) cfg.get(FUELS_CATEGORY, "list", FuelRules.DEFAULTS).set(moved);
    }

    private static void readFuelStation(Configuration cfg)
    {
        FUELSTATION_TANK_CAPACITY = integer(cfg, FUELSTATION, "tankCapacity", 16000, 1000, 64000, "Capacity of the station's tank in millibuckets. Use the same value on server and clients. Lowering it does not delete fuel already stored above the new limit.", true);
    }

    /** Moves the old crossbow.shotsPerMagazine value into crossbow.magazine.capacity and drops the old key. */
    static void migrateMagazineCapacity(Configuration cfg)
    {
        ConfigCategory crossbow = cfg.getCategory(CROSSBOW);
        if (!crossbow.containsKey("shotsPerMagazine")) return;
        Property old = crossbow.remove("shotsPerMagazine");
        ConfigCategory magazine = cfg.getCategory(CROSSBOW_MAGAZINE);
        if (!magazine.containsKey("capacity")) cfg.get(CROSSBOW_MAGAZINE, "capacity", old.getInt(6));
    }

    private static void readCrossbow(Configuration cfg)
    {
        ConfigCategory crossbow = cfg.getCategory(CROSSBOW);
        for (String gone : new String[]{"arrowVelocity", "arrowDamage"})
        {
            // Keys of the single-shot crossbow; their scale does not match the bolt keys, so they are dropped, not moved.
            if (crossbow.containsKey(gone)) crossbow.remove(gone);
        }
        // The elemental bolts were removed in 1.1: drop their category from older files.
        String elemental = CROSSBOW + ".elemental";
        if (cfg.hasCategory(elemental)) cfg.removeCategory(cfg.getCategory(elemental));
        migrateMagazineCapacity(cfg);
        CROSSBOW_SHOT_DELAY_TICKS = integer(cfg, CROSSBOW, "shotDelayTicks", 4, 1, 40, "Pause between two bolts. Minecraft repeats a held right click every 4 ticks, so the real pause while holding is this value rounded up to a multiple of 4 (4 = 5 bolts a second, 5 to 8 = 2.5 a second).");
        CROSSBOW_RELOAD_TICKS = integer(cfg, CROSSBOW, "reloadTicks", 30, 0, 200, "Time to put a magazine into the crossbow, during which the crossbow does not fire.");
        CROSSBOW_BOLT_VELOCITY = (float) decimal(cfg, CROSSBOW, "boltVelocity", 3.0, 0.5, 6.0, "Bolt speed in blocks per tick when it leaves the crossbow (a fully drawn bow shoots at 3). Faster bolts fly flatter and further.");
        CROSSBOW_BOLT_DAMAGE = decimal(cfg, CROSSBOW, "boltDamage", 4.0, 0.0, 100.0, "Damage of a bolt fired from a magazine of arrows as it leaves the crossbow, in half hearts (a fully drawn bow does 6 to 10). Crossbow Bolts multiply it by crossbow.ammo.boltDamageMultiplier. It drops a little with distance as the bolt slows down.");
        CROSSBOW_IGNORE_HURT_COOLDOWN = bool(cfg, CROSSBOW, "ignoreHurtCooldown", true, "Bolts ignore the short invulnerability a mob gets after a hit, so every bolt of a burst lands. When false about half of a fast burst bounces off the same target.");
        CROSSBOW_BOLTS_RECOVERABLE = bool(cfg, CROSSBOW, "boltsRecoverable", false, "Bolts stuck in a block can be picked up as arrows. Off by default: a charge of the magazine is spent ammunition.");
        CROSSBOW_DURABILITY = integer(cfg, CROSSBOW, "durability", 0, 0, 100000, "Bolts the crossbow fires before it breaks. 0 means it never breaks.");
        GRAPPLE_ENABLED = bool(cfg, CROSSBOW_GRAPPLE, "enabled", true, "The grappling hook mode. When false the mode is skipped and a crossbow left in it shoots bolts.");
        GRAPPLE_MAX_DISTANCE = decimal(cfg, CROSSBOW_GRAPPLE, "maxDistance", 32.0, 4.0, 64.0, "Range of the hook in blocks. A hook that reaches it without catching comes back.");
        GRAPPLE_HOOK_SPEED = decimal(cfg, CROSSBOW_GRAPPLE, "hookSpeed", 2.5, 0.5, 5.0, "Speed of the flying hook, in blocks per tick. It flies straight, without gravity.");
        GRAPPLE_PULL_SPEED = decimal(cfg, CROSSBOW_GRAPPLE, "pullSpeed", 0.8, 0.1, 3.0, "Speed at which the hook pulls the player, in blocks per tick (sprinting is about 0.28).");
        GRAPPLE_RELEASE_DISTANCE = decimal(cfg, CROSSBOW_GRAPPLE, "releaseDistance", 1.5, 0.5, 4.0, "The hook lets go when the player's body is this close to the anchor, in blocks.");
        GRAPPLE_MAX_PULL_TICKS = integer(cfg, CROSSBOW_GRAPPLE, "maxPullTicks", 60, 10, 200, "Longest pull; the hook lets go after it (for example when the player is stuck under a ledge). On a server with allow-flight=false keep it under 80, or a long upward pull can kick the player for flying.");
        GRAPPLE_COOLDOWN_TICKS = integer(cfg, CROSSBOW_GRAPPLE, "cooldownTicks", 20, 0, 200, "Wait after firing the hook before the crossbow can fire or reload again.");
        GRAPPLE_CHARGES = integer(cfg, CROSSBOW_GRAPPLE, "chargesPerShot", 1, 0, 24, "Bolts from the magazine a hook shot takes. 0 makes it free; more than a full magazine counts as a full magazine.");
        GRAPPLE_NO_FALL_DAMAGE = bool(cfg, CROSSBOW_GRAPPLE, "noFallDamage", true, "The pull never causes fall damage: the fall distance is reset while pulling and when the hook lets go. A fall after letting go counts from there.");
        CROSSBOW_SHOTS_PER_MAGAZINE = integer(cfg, CROSSBOW_MAGAZINE, "capacity", 6, 1, 24, "Charges a full Crossbow Magazine holds, and bolts the crossbow fires from it. Old cartridges without a charge count hold 6 (the 6 arrows they were made of), never more than this.");
        MAGAZINE_LOAD_TICKS = integer(cfg, CROSSBOW_MAGAZINE, "loadTicksPerCharge", 4, 0, 100, "Pause between two charges while loading a magazine with a held right click. A held click repeats every 4 ticks, so the real pause is this value rounded up to a multiple of 4. 0 loads the whole magazine with one click.");
        MAGAZINE_RELOAD_RECIPE = bool(cfg, CROSSBOW_MAGAZINE, "reloadRecipe", true, "Shapeless recipe: a magazine and up to its free room of ammunition of one kind (the kind it already holds) make the magazine loaded with them. Needs a restart.");
        cfg.getCategory(CROSSBOW_MAGAZINE).get("reloadRecipe").setRequiresMcRestart(true);
        MAGAZINE_LEGACY_RECIPE = bool(cfg, CROSSBOW_MAGAZINE, "legacyRecipe", true, "Keep the 1.0 Bolt Cartridge recipe (6 arrows, an iron ingot and 2 sticks): it makes a magazine already loaded with 6 arrows. The empty magazine has its own recipe (an iron ingot, 2 sticks and a string). Needs a restart.");
        cfg.getCategory(CROSSBOW_MAGAZINE).get("legacyRecipe").setRequiresMcRestart(true);
        order(CROSSBOW_AMMO, "list");
        CROSSBOW_AMMO_LIST = cfg.getStringList("list", CROSSBOW_AMMO, CROSSBOW_AMMO_DEFAULT, "Items a magazine can be loaded with, as modid:itemName (any damage value). When a magazine is empty the first ammunition found in the inventory (hotbar first) decides its kind. Names no loaded mod registers are ignored. Set it the same on server and clients.");
        CROSSBOW_BOLT_DAMAGE_MULTIPLIER = decimal(cfg, CROSSBOW_AMMO, "boltDamageMultiplier", 1.5, 0.1, 10.0, "Damage of bolts fired from a magazine of Crossbow Bolts (adventurebackpack:bolt), as a multiple of crossbow.boltDamage and crossbow.sniper.boltDamage. Other ammunition does the base damage.");
        SNIPER_ENABLED = bool(cfg, CROSSBOW_SNIPER, "enabled", true, "The sniper mode. When false the mode is skipped and a crossbow left in it shoots normal bolts.");
        SNIPER_DAMAGE = decimal(cfg, CROSSBOW_SNIPER, "boltDamage", 12.0, 0.0, 100.0, "Damage of a sniper bolt as it leaves the crossbow, in half hearts. It drops a little with distance as the bolt slows down.");
        SNIPER_VELOCITY = (float) decimal(cfg, CROSSBOW_SNIPER, "boltVelocity", 5.0, 0.5, 8.0, "Speed of a sniper bolt in blocks per tick. Faster bolts fly flatter and further.");
        SNIPER_SHOT_DELAY_TICKS = integer(cfg, CROSSBOW_SNIPER, "shotDelayTicks", 30, 4, 200, "Pause after a sniper shot. A held right click repeats every 4 ticks, so the real pause is this value rounded up to a multiple of 4.");
        SNIPER_STILL_TOLERANCE = decimal(cfg, CROSSBOW_SNIPER, "stillTolerance", 0.02, 0.001, 0.2, "Horizontal movement in blocks per tick under which the player counts as still (sneak walking is about 0.065).");
        SNIPER_ZOOM = (float) decimal(cfg, CROSSBOW_SNIPER, "zoom", 0.35, 0.1, 1.0, "Client only: field of view multiplier while the bipod is down. 1 = no zoom, 0.35 = about three times closer.");
    }

    private static void readSleepingBag(Configuration cfg)
    {
        SLEEPINGBAG_PLACEABLE = bool(cfg, SLEEPINGBAG, "placeable", true, "Right click the top of a block with the Sleeping Bag item to lay it on the ground as a bed, without a backpack. Breaking it gives the item back; sneak + right click rolls it up into the inventory.");
        SLEEPINGBAG_SETS_SPAWN = bool(cfg, SLEEPINGBAG, "setsSpawn", true, "Sleeping in a sleeping bag (placed on its own or from a backpack) moves the player's spawn point there, like a bed. When false the player keeps the spawn point they had.");
    }

    private static void readPistonBoots(Configuration cfg)
    {
        PISTONBOOTS_ONLY_WHILE_SPRINTING = bool(cfg, PISTONBOOTS, "onlyWhileSprinting", true, "Step up full blocks only while sprinting, so walking up to a block does not climb it. False steps up at any pace while the boots are on. The sprint speed is only given while sprinting either way.");
        PISTONBOOTS_STEP_HEIGHT = (float) decimal(cfg, PISTONBOOTS, "stepHeight", 1.001, 0.5, 3.0, "Step height while the boots step up (vanilla is 0.5).");
        PISTONBOOTS_SPRINT_SPEED = bool(cfg, PISTONBOOTS, "sprintSpeed", true, "Give Speed I while sprinting.");
        PISTONBOOTS_SAFE_FALL_DISTANCE = integer(cfg, PISTONBOOTS, "safeFallDistance", 8, 0, 256, "Falls shorter than this many blocks do no damage. 0 turns it off.");
    }

    private static void readCampfire(Configuration cfg)
    {
        CAMPFIRE_COOKING_ENABLED = bool(cfg, CAMPFIRE_COOKING, "enabled", true, "Cook items by right clicking the campfire with them.");
        CAMPFIRE_TICKS_PER_ITEM = integer(cfg, CAMPFIRE_COOKING, "ticksPerItem", 200, 1, 12000, "Time to cook one item (the furnace takes 200).");
        CAMPFIRE_ONLY_FOOD = bool(cfg, CAMPFIRE_COOKING, "onlyFood", true, "Only cook items whose furnace result is food. False takes any furnace recipe (ores, sand, logs...).");
        CAMPFIRE_MAX_PER_USE = integer(cfg, CAMPFIRE_COOKING, "maxPerUse", 8, 1, 64, "Most items the campfire holds at once; a right click puts on as many as fit, the rest stays in the hand.");
        CAMPFIRE_RETURN_RANGE = decimal(cfg, CAMPFIRE_COOKING, "returnRange", 4.0, 0.0, 16.0, "Distance in blocks within which the cooked items go straight into the inventory of the player who put them on the fire. Farther away, or with 0, they pop out on top of the campfire.");
    }

    private static void readRender(Configuration cfg)
    {
        RENDER_BACKPACK_ON_PLAYER = bool(cfg, RENDER, "backpackOnPlayer", true, "Draw the worn backpack on the player. Turn it off with mods that already draw the player's back, like Smart Moving.");
        GUI_TANK_RENDER = integer(cfg, RENDER, "tankRenderType", 3, 1, 3, "How fluids are drawn in the backpack GUI tanks: 1, 2 or 3.");
        HOVERING_TEXT_TANKS = bool(cfg, RENDER, "tankHoverText", false, "Show the tank contents as hovering text instead of under the tanks.");
        STATUS_OVERLAY = bool(cfg, RENDER, "statusOverlay", true, "Show the player's potion effects on screen.");
        RENDER_SCUBA_BUBBLE_TICKS = integer(cfg, RENDER, "scubaBubbleTicks", 10, 1, 200, "Ticks between two puffs of bubbles from the mask of a player breathing from the Scuba Gear.");
        RENDER_FLAMETHROWER_PARTICLES = integer(cfg, RENDER, "flamethrowerParticles", 6, 0, 30, "Flame particles drawn along the cone for each burst of a Flamethrower (smoke comes with every second burst). 0 draws none.");
        TANKS_OVERLAY = bool(cfg, RENDER, "wearableOverlay", true, "Show the overlays of the worn backpack, copter pack or jetpack on screen, and the fuel tank of the motorized boat the player rides.");
        RENDER_BOAT_SMOKE_TICKS = integer(cfg, RENDER, "boatSmokeTicks", 8, 0, 200, "The running motor of the motorized boat puffs smoke once every this many ticks. 0 = no smoke.");
    }

    private static void readKeys(Configuration cfg)
    {
        KEY_OPEN_BACKPACK = integer(cfg, KEYS, "openBackpack", Keyboard.KEY_B, 0, 255, "Default key that opens the worn backpack, copter pack or jetpack (48 = B).", true);
        KEY_TOGGLE = integer(cfg, KEYS, "toggle", Keyboard.KEY_N, 0, 255, "Default key that switches the hose tank and drives the worn copter pack or jetpack (49 = N). With the copter pack, the key changes its mode (normal or hover) and Sneak + the key turns it on or off; with the jetpack, Sneak + the key turns it on or off; Sneak + N lights the Flamethrower Pack.", true);
        KEY_CROSSBOW_MODE = integer(cfg, KEYS, "crossbowMode", Keyboard.KEY_V, 0, 255, "Default key that changes the firing mode of the held Clockwork Crossbow (47 = V).", true);
        KEY_TOGGLE_ABILITIES = integer(cfg, KEYS, "toggleAbilities", 0, 0, 255, "Default key that turns the abilities of the worn backpack off and on, for this player only (night vision, water breathing, fire resistance, milk...). 0 = not bound: pick one in Options > Controls.", true);
        KEY_BOAT_MOTOR_TOGGLE = integer(cfg, KEYS, "boatMotorToggle", 0, 0, 255, "Default key that turns the motor of the Motorized Inflatable Boat on and off while riding it (inflatableboat.motor.mode toggle). 0 = not bound: pick one in Options > Controls.", true);
        TOOL_CYCLE_ENABLED = bool(cfg, TOOL_CYCLE, "enabled", true, "Swap the held tool with the backpack's tool slots, and change the hose mode, from the keyboard or mouse wheel. False turns both off, so the wheel always scrolls the hotbar.");
        TOOL_CYCLE_MODE = ToolCycleInput.Mode.parse(choice(cfg, TOOL_CYCLE, "mode", "both", ToolCycleInput.Mode.NAMES,
                "wheel: hold the modifier key and turn the mouse wheel. keys: only the next and previous tool keys. both: either one."));
        TOOL_CYCLE_INVERT_WHEEL = bool(cfg, TOOL_CYCLE, "invertWheel", false, "Turn the mouse wheel the other way to cycle.");
        KEY_TOOL_MODIFIER = integer(cfg, TOOL_CYCLE, "modifierKey", 0, 0, 255, "Default key to hold while turning the wheel. 0 means the sneak key (Left Shift by default): a separate binding on the same key as sneak would stop one of the two from working in 1.7.10.", true);
        KEY_NEXT_TOOL = integer(cfg, TOOL_CYCLE, "nextToolKey", 0, 0, 255, "Default key for the next tool. 0 = not bound.", true);
        KEY_PREVIOUS_TOOL = integer(cfg, TOOL_CYCLE, "previousToolKey", 0, 0, 255, "Default key for the previous tool. 0 = not bound.", true);
    }

    private static void readWorldgenRecipesTweaks(Configuration cfg)
    {
        BONUS_CHEST_ALLOWED = bool(cfg, WORLDGEN, "bonusChestBackpack", false, "Put a Standard Adventure Backpack in the bonus chest.");
        PIGMAN_ALLOWED = bool(cfg, WORLDGEN, "pigmanBackpacks", false, "Let Pigman Backpacks appear in dungeon loot and villager trades.");
        SADDLE_RECIPE = bool(cfg, RECIPES, "saddle", true, "Add a crafting recipe for the saddle.");
        BOAT_RECIPE_MODE = BoatRecipeRules.Mode.parse(choice(cfg, RECIPES_BOAT, "mode", "auto", BoatRecipeRules.Mode.NAMES,
                "vanilla: only the vanilla recipes. auto: the vanilla recipes, plus a rubber or motor variant when a loaded mod provides rubber or an electric motor. technical: the rubber or motor variant replaces the vanilla recipe of each part a loaded mod provides; a part no loaded mod provides keeps its vanilla recipe. Needs a restart."));
        order(RECIPES_BOAT, "rubber");
        BOAT_RUBBER = cfg.getStringList("rubber", RECIPES_BOAT, BOAT_RUBBER_DEFAULT, "Ore Dictionary names of rubber (IC2 itemRubber, GregTech plateRubber and ingotRubber...). Each one some loaded mod provides gets its own chamber recipe. Needs a restart.");
        order(RECIPES_BOAT, "motors");
        BOAT_MOTORS = cfg.getStringList("motors", RECIPES_BOAT, BOAT_MOTORS_DEFAULT, "Electric motors for the motorized boat, each one found gets its own recipe. Entries: ic2:<IC2 item name> (IC2 API, elemotor is the Electric Motor), item:<modid>:<item name>[:<damage>] (gregtech:gt.metaitem.01:32600 is GregTech's LV Motor), ore:<Ore Dictionary name>, stack:<modid>:<registered stack name> (Thermal Expansion's powerCoilGold is the Redstone Reception Coil). Needs a restart.");
        FUELSTATION_RECIPE_MODE = BoatRecipeRules.Mode.parse(choice(cfg, RECIPES_FUELSTATION, "mode", "auto", BoatRecipeRules.Mode.NAMES,
                "vanilla: only the iron ingot recipe. auto: the iron ingot recipe, plus one with plates for each plate a loaded mod provides. technical: the plate recipes replace the iron ingot one when a loaded mod provides a plate; without plates the iron ingot recipe stays. Needs a restart."));
        order(RECIPES_FUELSTATION, "plates");
        FUELSTATION_PLATES = cfg.getStringList("plates", RECIPES_FUELSTATION, FUELSTATION_PLATES_DEFAULT, "Ore Dictionary names of the metal plates for the technical recipe (GregTech, IC2, Thermal Foundation, Railcraft plateSteel and plateIron...). Each one some loaded mod provides gets its own recipe. Needs a restart.");
        FLAMETHROWER_RECIPE_MODE = BoatRecipeRules.Mode.parse(choice(cfg, RECIPES_FLAMETHROWER, "mode", "auto", BoatRecipeRules.Mode.NAMES,
                "vanilla: only the iron ingot recipes. auto: the iron ingot recipes, plus ones with plates for each plate a loaded mod provides. technical: the plate recipes replace the iron ingot ones when a loaded mod provides a plate; without plates the iron ingot recipes stay. Needs a restart."));
        order(RECIPES_FLAMETHROWER, "plates");
        FLAMETHROWER_PLATES = cfg.getStringList("plates", RECIPES_FLAMETHROWER, FLAMETHROWER_PLATES_DEFAULT, "Ore Dictionary names of the metal plates for the technical recipes of the pack and the nozzle (GregTech, IC2, Thermal Foundation, Railcraft plateSteel and plateIron...). Each one some loaded mod provides gets its own recipe. Needs a restart.");
        FIX_LEAD = bool(cfg, TWEAKS, "fixVanillaLead", true, "Mobs on a lead held by a player take no fall damage unless they fall fast.");
        HORSES_FOLLOW_OWNER = bool(cfg, TWEAKS, "namedHorsesFollowOwner", true, "Tamed horses with a name tag follow their owner.");
        TOOL_CYCLE_LIST_MODE = ToolCycleFilter.Mode.parse(choice(cfg, TWEAKS_TOOL_CYCLE, "mode", "none", ToolCycleFilter.Mode.NAMES,
                "Which items may go into the backpack's two tool slots, by hand or with the tool cycle. none: the built-in rules (vanilla tools, shears, hoes, fishing rods, flint and steel, and tools of Tinkers' Construct, BuildCraft, IndustrialCraft and Thermal Expansion). whitelist: only the items on the list. blacklist: the built-in rules minus the items on the list. Stackable items, hoses and backpacks never go in. Set it the same on server and clients."));
        order(TWEAKS_TOOL_CYCLE, "list");
        TOOL_CYCLE_FILTER = ToolCycleFilter.parse(cfg.getStringList("list", TWEAKS_TOOL_CYCLE, new String[0],
                "Items for the whitelist or blacklist mode, one per line, as registry names: modid:name (minecraft:shears, TConstruct:pickaxe; the names /give takes), or modid:* for every item of a mod. A name without a mod id means minecraft:. Case does not matter. A blacklisted bow also stops the Skeleton Backpack from cycling it."));
    }

    private static void readCompat(Configuration cfg)
    {
        COMPAT_BAUBLES_ENABLED = bool(cfg, COMPAT_BAUBLES, "enabled", true, "Let the Adventure Backpack go into a bauble slot and work from there. When false the backpack does not fit a bauble slot and one already there does nothing. The Baubles Expanded back slot exists anyway (so client and server always match); its layout can be changed in the Baubles Expanded config.");
        COMPAT_GRAVES_ENABLED = bool(cfg, COMPAT_GRAVES, "enabled", true, "Send the worn wearable to the grave of OpenBlocks or GraveStone when one of them is loaded. When false, backpack.placeOnDeath decides as if no grave mod were there.");
    }

    /** Fixes values that are valid alone but not together. Pure: the unit test calls it. */
    static void validate()
    {
        if (BAT_NIGHT_VISION_TICKS <= BAT_NIGHT_VISION_REFRESH_BELOW)
        {
            BAT_NIGHT_VISION_TICKS = BAT_NIGHT_VISION_REFRESH_BELOW + 100;
        }
        if (JETPACK_STEAM_PER_TICK > JETPACK_STEAM_CAPACITY)
        {
            JETPACK_STEAM_PER_TICK = JETPACK_STEAM_CAPACITY;
        }
        if (SPIDER_FOLLOW_STOP_DISTANCE >= SPIDER_FOLLOW_START_DISTANCE)
        {
            // otherwise the spider would start and stop following in the same tick
            SPIDER_FOLLOW_STOP_DISTANCE = Math.max(1.0, SPIDER_FOLLOW_START_DISTANCE - 1.0);
        }
        if (SPIDER_TELEPORT_DISTANCE > 0 && SPIDER_TELEPORT_STUCK_DISTANCE >= SPIDER_TELEPORT_DISTANCE)
        {
            // a stuck spider should jump sooner than one that is just far behind
            SPIDER_TELEPORT_STUCK_DISTANCE = SPIDER_TELEPORT_DISTANCE / 2;
        }
    }

    // ---------------------------------------------------------------- helpers

    private static void order(String category, String key)
    {
        List<String> keys = ORDER.get(category);
        if (keys == null)
        {
            keys = new ArrayList<String>();
            ORDER.put(category, keys);
        }
        keys.add(key);
    }

    private static boolean bool(Configuration cfg, String category, String key, boolean def, String comment)
    {
        order(category, key);
        return cfg.getBoolean(key, category, def, comment);
    }

    private static String text(Configuration cfg, String category, String key, String def, String comment)
    {
        order(category, key);
        return cfg.getString(key, category, def, comment).trim();
    }

    private static String choice(Configuration cfg, String category, String key, String def, String[] valid, String comment)
    {
        order(category, key);
        return cfg.getString(key, category, def, comment + " Values: " + String.join(", ", valid) + ".", valid);
    }

    private static int integer(Configuration cfg, String category, String key, int def, int min, int max, String comment)
    {
        return integer(cfg, category, key, def, min, max, comment, false);
    }

    private static int integer(Configuration cfg, String category, String key, int def, int min, int max, String comment, boolean restart)
    {
        order(category, key);
        int value = cfg.getInt(key, category, def, min, max, restart ? comment + " Needs a restart." : comment);
        cfg.getCategory(category).get(key).setRequiresMcRestart(restart);
        return value;
    }

    private static double decimal(Configuration cfg, String category, String key, double def, double min, double max, String comment)
    {
        return decimal(cfg, category, key, def, min, max, comment, false);
    }

    private static double decimal(Configuration cfg, String category, String key, double def, double min, double max, String comment, boolean restart)
    {
        order(category, key);
        String text = (restart ? comment + " Needs a restart." : comment) + " [range: " + min + " ~ " + max + ", default: " + def + "]";
        Property p = cfg.get(category, key, def, text, min, max);
        p.comment = text;
        p.setRequiresMcRestart(restart);
        return clamp(p.getDouble(def), min, max);
    }

    static double clamp(double value, double min, double max)
    {
        if (Double.isNaN(value)) return min;
        return value < min ? min : (value > max ? max : value);
    }

    @SubscribeEvent
    public void onConfigChangeEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.modID.equalsIgnoreCase(ModInfo.MOD_ID))
        {
            load(config);
        }
    }
}
