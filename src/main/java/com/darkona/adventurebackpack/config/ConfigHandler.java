package com.darkona.adventurebackpack.config;

import com.darkona.adventurebackpack.common.AbilityEffects;
import com.darkona.adventurebackpack.common.NightVisionRefresh;
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
    public static final String INFLATABLEBOAT = ROOT + ".inflatableboat";
    public static final String CROSSBOW = ROOT + ".crossbow";
    public static final String CROSSBOW_ELEMENTAL = CROSSBOW + ".elemental";
    public static final String PISTONBOOTS = ROOT + ".pistonboots";
    public static final String RENDER = ROOT + ".render";
    public static final String KEYS = ROOT + ".keys";
    public static final String TOOL_CYCLE = KEYS + ".toolcycle";
    public static final String WORLDGEN = ROOT + ".worldgen";
    public static final String RECIPES = ROOT + ".recipes";
    public static final String TWEAKS = ROOT + ".tweaks";
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
    public static boolean INFLATABLEBOAT_ENABLED = true;
    public static boolean CROSSBOW_ENABLED = true;
    public static boolean SLEEPINGBAG_ENABLED = true;
    public static boolean PISTONBOOTS_ENABLED = true;

    // ---------------------------------------------------------------- backpack
    public static boolean BACKPACK_DEATH_PLACE = true;
    public static int BACKPACK_TANK_CAPACITY = 4000;
    public static float BACKPACK_BLAST_RESISTANCE = 2000f;
    public static float BACKPACK_HARDNESS = 1.0f;
    public static boolean BACKPACK_DEATH_MESSAGE = true;

    // ---------------------------------------------------------------- abilities
    public static int ABILITY_EFFECT_REFRESH_TICKS = AbilityEffects.EFFECT_REFRESH_TICKS;

    public static boolean BAT_ENABLED = true;
    public static int BAT_NIGHT_VISION_TICKS = NightVisionRefresh.DURATION;
    public static int BAT_NIGHT_VISION_REFRESH_BELOW = NightVisionRefresh.REFRESH_BELOW;

    public static boolean SQUID_ENABLED = true;

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
    public static final String[] COPTER_FUELS_DEFAULT = {"fuel=1.0", "oil=1.5", "bioethanol=1.2", "biofuel=1.0", "creosote=1.8", "coal=0.8"};
    public static String[] COPTER_FUELS = COPTER_FUELS_DEFAULT;
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

    // ---------------------------------------------------------------- inflatable boat
    public static double BOAT_MAX_SPEED = 0.35;

    // ---------------------------------------------------------------- crossbow
    public static int CROSSBOW_SHOTS_PER_MAGAZINE = 6;
    public static int CROSSBOW_SHOT_DELAY_TICKS = 4;
    public static int CROSSBOW_RELOAD_TICKS = 30;
    public static float CROSSBOW_BOLT_VELOCITY = 3.0f;
    public static double CROSSBOW_BOLT_DAMAGE = 4.0;
    public static boolean CROSSBOW_IGNORE_HURT_COOLDOWN = true;
    public static boolean CROSSBOW_BOLTS_RECOVERABLE = false;
    public static int CROSSBOW_DURABILITY = 0;
    public static boolean CROSSBOW_ELEMENTAL_ENABLED = true;
    public static int CROSSBOW_FREEZING_TICKS = 60;
    public static int CROSSBOW_FREEZING_AMPLIFIER = 2;
    public static int CROSSBOW_POISON_TICKS = 100;
    public static int CROSSBOW_POISON_AMPLIFIER = 0;
    public static int CROSSBOW_STUNNING_TICKS = 30;
    public static int CROSSBOW_STUNNING_AMPLIFIER = 4;

    // ---------------------------------------------------------------- piston boots
    public static float PISTONBOOTS_STEP_HEIGHT = 1.001f;
    public static boolean PISTONBOOTS_SPRINT_SPEED = true;
    public static int PISTONBOOTS_SAFE_FALL_DISTANCE = 8;

    // ---------------------------------------------------------------- render (client)
    public static boolean RENDER_BACKPACK_ON_PLAYER = true;
    public static int GUI_TANK_RENDER = 3;
    public static boolean HOVERING_TEXT_TANKS = false;
    public static boolean STATUS_OVERLAY = true;
    public static boolean TANKS_OVERLAY = true;

    // ---------------------------------------------------------------- keys (client, defaults only)
    public static int KEY_OPEN_BACKPACK = Keyboard.KEY_B;
    public static int KEY_TOGGLE = Keyboard.KEY_N;
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
    public static boolean FIX_LEAD = true;
    public static boolean HORSES_FOLLOW_OWNER = true;

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
        readInflatableBoat(cfg);
        readCrossbow(cfg);
        readPistonBoots(cfg);
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
        String[] abilities = {"bat", "squid", "pigman", "dragon", "cactus", "melon", "pig", "slime", "chicken", "creeper", "cow", "mooshroom", "wolf", "ocelot", "rainbow", "sunflower", "skeleton", "irongolem", "spider"};
        for (String a : abilities)
        {
            cfg.getCategory(ABILITIES + "." + a);
        }
        cfg.setCategoryComment(HOSE, "The Backpack Hose: suck, spill and drink from the worn backpack's tanks. Amounts in millibuckets (1000 = one bucket).");
        cfg.setCategoryComment(COPTERPACK, "The Copter Pack (works only with BuildCraft loaded). Speeds in blocks per tick.");
        cfg.setCategoryComment(STEAMJETPACK, "The Steam Jetpack. Its boiler needs a 'steam' fluid: another mod's, or its own (ownSteamFluid). Speeds in blocks per tick.");
        cfg.setCategoryComment(INFLATABLEBOAT, "The Inflatable Boat.");
        cfg.setCategoryComment(CROSSBOW, "The Clockwork Crossbow: a repeating crossbow fed with Bolt Cartridges. Hold right click to fire the magazine one bolt after another; when it is empty the next click loads the first cartridge in the inventory (hotbar first). Sneak + right click swaps a partly used magazine, which comes back as a partly used cartridge. Ticks: 20 per second.");
        cfg.setCategoryComment(CROSSBOW_ELEMENTAL, "Blazing, Freezing, Poison and Stunning Bolt Cartridges. Blazing bolts burn and set the target on fire for 5 seconds, like a Flame bow. The other effects are potion effects; an amplifier is the effect level minus one (0 = level I).");
        cfg.setCategoryComment(PISTONBOOTS, "The Piston Boots.");
        cfg.setCategoryComment(RENDER, "Client only: how the mod draws things. The server ignores this category.");
        cfg.setCategoryComment(KEYS, "Client only: default keys, as LWJGL key codes (B = 48, N = 49, see the LWJGL Keyboard class). They are only defaults: once a key is changed in Options > Controls, Minecraft keeps that choice.");
        cfg.setCategoryComment(WORLDGEN, "Backpacks found in the world.");
        cfg.setCategoryComment(RECIPES, "Extra recipes.");
        cfg.setCategoryComment(TWEAKS, "Small fixes and behaviours that are not tied to one item.");
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
        COPTERPACK_ENABLED = bool(cfg, ENABLED, "copterPack", true, "The Copter Pack. It is always registered, but it only works (and shows in the creative tab) when BuildCraft is loaded.");
        STEAMJETPACK_ENABLED = bool(cfg, ENABLED, "steamJetpack", true, "The Steam Jetpack.");
        INFLATABLEBOAT_ENABLED = bool(cfg, ENABLED, "inflatableBoat", true, "The Inflatable Boat and the Motorized Inflatable Boat.");
        CROSSBOW_ENABLED = bool(cfg, ENABLED, "crossbow", true, "The Clockwork Crossbow and its Bolt Cartridges.");
        SLEEPINGBAG_ENABLED = bool(cfg, ENABLED, "sleepingBag", true, "Deploying the sleeping bag from a placed backpack.");
        PISTONBOOTS_ENABLED = bool(cfg, ENABLED, "pistonBoots", true, "The Piston Boots: step assist, sprint speed and fall protection.");
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

        String c = ABILITIES + ".bat";
        BAT_ENABLED = bool(cfg, c, "enabled", true, "Bat Backpack: night vision.");
        BAT_NIGHT_VISION_TICKS = integer(cfg, c, "nightVisionTicks", NightVisionRefresh.DURATION, 400, 1000000, "Length of the night vision effect it gives.");
        BAT_NIGHT_VISION_REFRESH_BELOW = integer(cfg, c, "nightVisionRefreshBelow", NightVisionRefresh.REFRESH_BELOW, 201, 1000, "Ticks left under which night vision is renewed. Keep it above 200: vanilla makes the screen flicker in the last 200 ticks.");

        c = ABILITIES + ".squid";
        SQUID_ENABLED = bool(cfg, c, "enabled", true, "Squid Backpack: water breathing and night vision while in water.");

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
        SPIDER_ENABLED = bool(cfg, c, "enabled", true, "Spider Backpack: tame spiders and ride them. Wearing it, right click a spider with the tame item to try to tame it (like taming a wolf). A tamed spider follows its owner and only its owner rides it, both only while the owner wears the Spider Backpack. It is released (back to a normal spider) when its owner dies, logs out or changes dimension, or sneaks and right clicks it. When false, tamed spiders neither follow nor carry anyone, and no new spider can be tamed.");
        SPIDER_TAME_ITEM = text(cfg, c, "tameItem", "minecraft:spider_eye", "Item used to tame a spider, as modid:name. One is used up per try (not in creative).");
        SPIDER_TAME_CHANCE = decimal(cfg, c, "tameChance", 0.33, 0.0, 1.0, "Chance that one try tames the spider (1 = always, the wolf is about 0.33).");
        SPIDER_MAX_TAMED = integer(cfg, c, "maxTamed", 2, 0, 16, "Tamed spiders a player may have at once. 0 turns taming off.");
        SPIDER_FOLLOW_START_DISTANCE = decimal(cfg, c, "followStartDistance", 6.0, 2.0, 32.0, "A tamed spider starts walking to its owner when it is farther than this, in blocks.");
        SPIDER_FOLLOW_STOP_DISTANCE = decimal(cfg, c, "followStopDistance", 3.0, 1.0, 32.0, "It stops when it is this close to its owner, in blocks.");
        SPIDER_TELEPORT_DISTANCE = integer(cfg, c, "teleportDistance", 24, 0, 128, "When it cannot walk to its owner and is farther than this, it jumps next to the owner like a wolf. 0 = never.");
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
        COPTER_FUEL_PER_TICK = integer(cfg, COPTERPACK, "fuelPerTick", 2, 0, 100, "Millibuckets of fuel per tick while the engine runs, before the fuel's own multiplier (oil 1.5, fuel 1.0, creosote 1.8...). With the defaults a full tank lasts about 2.5 minutes of gliding, 75 seconds of hovering and 50 seconds of climbing while hovering.");
        COPTER_HOVER_EXTRA_FUEL = integer(cfg, COPTERPACK, "hoverExtraFuel", 2, 0, 100, "Extra fuel per tick while hovering (not while sinking with sneak).");
        COPTER_CLIMB_EXTRA_FUEL = integer(cfg, COPTERPACK, "climbExtraFuel", 2, 0, 100, "Extra fuel per tick while climbing (jump held).");
        COPTER_START_FUEL = integer(cfg, COPTERPACK, "startFuel", 100, 0, 64000, "Fuel the engine takes when it is turned on. It does not start with less in the tank. 0 turns it off.");
        order(COPTERPACK, "fuels");
        COPTER_FUELS = cfg.getStringList("fuels", COPTERPACK, COPTER_FUELS_DEFAULT, "Fluids the copter pack burns, as name=multiplier: the fuel cost is multiplied by it (higher burns faster). Fluid names as registered by the mods (BuildCraft fuel and oil, Railcraft creosote...). Needs a restart.");
        cfg.getCategory(COPTERPACK).get("fuels").setRequiresMcRestart(true);
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

    private static void readInflatableBoat(Configuration cfg)
    {
        BOAT_MAX_SPEED = decimal(cfg, INFLATABLEBOAT, "maxSpeed", 0.35, 0.07, 2.0, "Top speed in blocks per tick (the vanilla boat uses 0.35).");
    }

    private static void readCrossbow(Configuration cfg)
    {
        ConfigCategory crossbow = cfg.getCategory(CROSSBOW);
        for (String gone : new String[]{"arrowVelocity", "arrowDamage"})
        {
            // Keys of the single-shot crossbow; their scale does not match the bolt keys, so they are dropped, not moved.
            if (crossbow.containsKey(gone)) crossbow.remove(gone);
        }
        CROSSBOW_SHOTS_PER_MAGAZINE = integer(cfg, CROSSBOW, "shotsPerMagazine", 6, 1, 24, "Bolts in a full magazine, and in a full Bolt Cartridge (the recipe still takes 6 arrows).");
        CROSSBOW_SHOT_DELAY_TICKS = integer(cfg, CROSSBOW, "shotDelayTicks", 4, 1, 40, "Pause between two bolts. Minecraft repeats a held right click every 4 ticks, so the real pause while holding is this value rounded up to a multiple of 4 (4 = 5 bolts a second, 5 to 8 = 2.5 a second).");
        CROSSBOW_RELOAD_TICKS = integer(cfg, CROSSBOW, "reloadTicks", 30, 0, 200, "Time to load a cartridge, during which the crossbow does not fire.");
        CROSSBOW_BOLT_VELOCITY = (float) decimal(cfg, CROSSBOW, "boltVelocity", 3.0, 0.5, 6.0, "Bolt speed in blocks per tick when it leaves the crossbow (a fully drawn bow shoots at 3). Faster bolts fly flatter and further.");
        CROSSBOW_BOLT_DAMAGE = decimal(cfg, CROSSBOW, "boltDamage", 4.0, 0.0, 100.0, "Damage of a bolt as it leaves the crossbow, in half hearts (a fully drawn bow does 6 to 10). It drops a little with distance as the bolt slows down.");
        CROSSBOW_IGNORE_HURT_COOLDOWN = bool(cfg, CROSSBOW, "ignoreHurtCooldown", true, "Bolts ignore the short invulnerability a mob gets after a hit, so every bolt of a burst lands. When false about half of a fast burst bounces off the same target.");
        CROSSBOW_BOLTS_RECOVERABLE = bool(cfg, CROSSBOW, "boltsRecoverable", false, "Bolts stuck in a block can be picked up as arrows. Off by default: a cartridge is spent ammunition.");
        CROSSBOW_DURABILITY = integer(cfg, CROSSBOW, "durability", 0, 0, 100000, "Bolts the crossbow fires before it breaks. 0 means it never breaks.");
        CROSSBOW_ELEMENTAL_ENABLED = bool(cfg, CROSSBOW_ELEMENTAL, "enabled", true, "Elemental cartridges: their recipes, their place in the creative tab and their effects. When false they still load, and fire plain bolts.");
        CROSSBOW_FREEZING_TICKS = integer(cfg, CROSSBOW_ELEMENTAL, "freezingTicks", 60, 1, 1200, "Length of the Slowness a Freezing bolt gives.");
        CROSSBOW_FREEZING_AMPLIFIER = integer(cfg, CROSSBOW_ELEMENTAL, "freezingAmplifier", 2, 0, 6, "Slowness level minus one of a Freezing bolt (2 = Slowness III).");
        CROSSBOW_POISON_TICKS = integer(cfg, CROSSBOW_ELEMENTAL, "poisonTicks", 100, 1, 1200, "Length of the Poison a Poison bolt gives.");
        CROSSBOW_POISON_AMPLIFIER = integer(cfg, CROSSBOW_ELEMENTAL, "poisonAmplifier", 0, 0, 4, "Poison level minus one of a Poison bolt (0 = Poison I).");
        CROSSBOW_STUNNING_TICKS = integer(cfg, CROSSBOW_ELEMENTAL, "stunningTicks", 30, 1, 1200, "Length of the stun of a Stunning bolt: Slowness, and Nausea on players.");
        CROSSBOW_STUNNING_AMPLIFIER = integer(cfg, CROSSBOW_ELEMENTAL, "stunningAmplifier", 4, 0, 6, "Slowness level minus one of a Stunning bolt (4 = Slowness V, almost still).");
    }

    private static void readPistonBoots(Configuration cfg)
    {
        PISTONBOOTS_STEP_HEIGHT = (float) decimal(cfg, PISTONBOOTS, "stepHeight", 1.001, 0.5, 3.0, "Step height while sprinting (vanilla is 0.5).");
        PISTONBOOTS_SPRINT_SPEED = bool(cfg, PISTONBOOTS, "sprintSpeed", true, "Give Speed I while sprinting.");
        PISTONBOOTS_SAFE_FALL_DISTANCE = integer(cfg, PISTONBOOTS, "safeFallDistance", 8, 0, 256, "Falls shorter than this many blocks do no damage. 0 turns it off.");
    }

    private static void readRender(Configuration cfg)
    {
        RENDER_BACKPACK_ON_PLAYER = bool(cfg, RENDER, "backpackOnPlayer", true, "Draw the worn backpack on the player. Turn it off with mods that already draw the player's back, like Smart Moving.");
        GUI_TANK_RENDER = integer(cfg, RENDER, "tankRenderType", 3, 1, 3, "How fluids are drawn in the backpack GUI tanks: 1, 2 or 3.");
        HOVERING_TEXT_TANKS = bool(cfg, RENDER, "tankHoverText", false, "Show the tank contents as hovering text instead of under the tanks.");
        STATUS_OVERLAY = bool(cfg, RENDER, "statusOverlay", true, "Show the player's potion effects on screen.");
        TANKS_OVERLAY = bool(cfg, RENDER, "wearableOverlay", true, "Show the overlays of the worn backpack, copter pack or jetpack on screen.");
    }

    private static void readKeys(Configuration cfg)
    {
        KEY_OPEN_BACKPACK = integer(cfg, KEYS, "openBackpack", Keyboard.KEY_B, 0, 255, "Default key that opens the worn backpack, copter pack or jetpack (48 = B).", true);
        KEY_TOGGLE = integer(cfg, KEYS, "toggle", Keyboard.KEY_N, 0, 255, "Default key that switches the hose tank and toggles the copter pack or jetpack (49 = N). Sneak + toggle changes the copter mode.", true);
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
        FIX_LEAD = bool(cfg, TWEAKS, "fixVanillaLead", true, "Mobs on a lead held by a player take no fall damage unless they fall fast.");
        HORSES_FOLLOW_OWNER = bool(cfg, TWEAKS, "namedHorsesFollowOwner", true, "Tamed horses with a name tag follow their owner.");
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
