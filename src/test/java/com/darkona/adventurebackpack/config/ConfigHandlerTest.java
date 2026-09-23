package com.darkona.adventurebackpack.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.darkona.adventurebackpack.common.BoatMotor;
import com.darkona.adventurebackpack.common.BoatRecipeRules;
import com.darkona.adventurebackpack.common.FuelRules;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Runs the real Forge {@link Configuration} on a temporary file, without starting Minecraft: the tree layout,
 * the migration of the old flat keys, range clamping and the cross-field checks.
 */
class ConfigHandlerTest
{
    @TempDir
    static File home;

    @BeforeAll
    static void pointForgeAtATemporaryGameFolder() throws Exception
    {
        // Configuration reads the game folder from FMLInjectionData, which only the launcher fills in.
        Field f = Class.forName("cpw.mods.fml.relauncher.FMLInjectionData").getDeclaredField("minecraftHome");
        f.setAccessible(true);
        f.set(null, home);
    }

    /** The fields are static: put the defaults back so other test classes do not see this class's values. */
    @AfterEach
    void restoreDefaults() throws IOException
    {
        Files.deleteIfExists(new File(home, "defaults.cfg").toPath());
        load("defaults.cfg", null);
    }

    private static Configuration load(String fileName, String contents) throws IOException
    {
        File file = new File(home, fileName);
        if (contents != null) Files.write(file.toPath(), contents.getBytes(StandardCharsets.UTF_8));
        Configuration cfg = new Configuration(file);
        ConfigHandler.load(cfg);
        return cfg;
    }

    @Test
    void freshFileIsOneTreeWithEnabledFirst() throws IOException
    {
        Configuration cfg = load("fresh.cfg", null);
        String text = new String(Files.readAllBytes(new File(home, "fresh.cfg").toPath()), StandardCharsets.UTF_8);

        assertFalse(cfg.hasCategory(Configuration.CATEGORY_GENERAL));
        ConfigCategory root = cfg.getCategory(ConfigHandler.ROOT);
        assertTrue(root.isEmpty(), "no loose keys directly under the root");
        assertEquals("enabled", root.getChildren().iterator().next().getName());
        assertTrue(text.indexOf("enabled {") < text.indexOf("abilities {"));
        for (String name : cfg.getCategoryNames())
        {
            assertTrue(name.equals(ConfigHandler.ROOT) || name.startsWith(ConfigHandler.ROOT + "."), name);
        }
        // Each backpack's own switch is the first key of its category.
        assertEquals("enabled", cfg.getCategory(ConfigHandler.ABILITIES + ".dragon").getOrderedValues().get(0).getName());

        // Keep a copy for reading by hand and documenting the keys.
        File copy = new File("build/test-config/adventurebackpack.cfg");
        copy.getParentFile().mkdirs();
        Files.write(copy.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void everyKeyHasACommentAndDefaultsMatchTheFields() throws IOException
    {
        Configuration cfg = load("comments.cfg", null);
        int keys = 0;
        for (String name : cfg.getCategoryNames())
        {
            for (Property p : cfg.getCategory(name).getOrderedValues())
            {
                assertNotNull(p.comment, name + "." + p.getName());
                assertFalse(p.comment.trim().isEmpty(), name + "." + p.getName());
                keys++;
            }
        }
        assertTrue(keys > 100, "found " + keys + " keys");
        assertTrue(ConfigHandler.BACKPACK_ABILITIES);
        assertEquals(4000, ConfigHandler.BACKPACK_TANK_CAPACITY);
        assertEquals(48, ConfigHandler.KEY_OPEN_BACKPACK);
        assertEquals(49, ConfigHandler.KEY_TOGGLE);
        assertEquals(1, ConfigHandler.DRAGON_STRENGTH_AMPLIFIER);
        assertEquals(0.35, ConfigHandler.BOAT_MAX_SPEED, 1e-9);
        assertTrue(ConfigHandler.BAT_NIGHT_VISION_TICKS > ConfigHandler.BAT_NIGHT_VISION_REFRESH_BELOW);
    }

    @Test
    void oldFlatKeysMoveIntoTheTreeWithTheirValues() throws IOException
    {
        String legacy = "general {\n"
                + "    B:BackpackAbilities=false\n"
                + "    B:RenderBackpackOnPlayer=false\n"
                + "    I:TankRenderType=2\n"
                + "    B:SaddleRecipe=false\n"
                + "}\n";
        Configuration cfg = load("legacy.cfg", legacy);
        String text = new String(Files.readAllBytes(new File(home, "legacy.cfg").toPath()), StandardCharsets.UTF_8);

        assertFalse(ConfigHandler.BACKPACK_ABILITIES);
        assertFalse(ConfigHandler.RENDER_BACKPACK_ON_PLAYER);
        assertEquals(2, ConfigHandler.GUI_TANK_RENDER);
        assertFalse(ConfigHandler.SADDLE_RECIPE);
        assertFalse(cfg.hasCategory(Configuration.CATEGORY_GENERAL));
        assertFalse(text.contains("general {"));
        assertTrue(text.contains("B:backpackOnPlayer=false"));
    }

    @Test
    void outOfRangeValuesAreClamped() throws IOException
    {
        String file = ConfigHandler.ROOT + " {\n"
                + "    abilities {\n"
                + "        dragon {\n"
                + "            I:strengthAmplifier=99\n"
                + "        }\n"
                + "        creeper {\n"
                + "            D:scareRange=50.0\n"
                + "        }\n"
                + "    }\n"
                + "    hose {\n"
                + "        I:transferAmount=-5\n"
                + "    }\n"
                + "}\n";
        load("clamp.cfg", file);

        assertEquals(4, ConfigHandler.DRAGON_STRENGTH_AMPLIFIER);
        assertEquals(5.0, ConfigHandler.CREEPER_SCARE_RANGE, 1e-9);
        assertEquals(1, ConfigHandler.HOSE_TRANSFER_AMOUNT);
    }

    @Test
    void fuelAndFuelStationDefaults() throws IOException
    {
        Configuration cfg = load("fuels.cfg", null);
        assertTrue(ConfigHandler.FUELSTATION_ENABLED);
        assertEquals(16000, ConfigHandler.FUELSTATION_TANK_CAPACITY);
        assertArrayEquals(FuelRules.DEFAULTS, ConfigHandler.FUELS);
        assertEquals(BoatRecipeRules.Mode.AUTO, ConfigHandler.FUELSTATION_RECIPE_MODE);
        assertTrue(cfg.getCategory(ConfigHandler.FUELS_CATEGORY).containsKey("list"));
        assertTrue(cfg.getCategory(ConfigHandler.FUELSTATION).containsKey("tankCapacity"));
        assertTrue(cfg.getCategory(ConfigHandler.ENABLED).containsKey("fuelStation"));
        assertFalse(cfg.getCategory(ConfigHandler.COPTERPACK).containsKey("fuels"), "the old key is gone");
        assertTrue(cfg.getCategory(ConfigHandler.FUELS_CATEGORY).get("list").requiresMcRestart());
    }

    @Test
    void boatMotorDefaults() throws IOException
    {
        Configuration cfg = load("boatmotor.cfg", null);
        assertTrue(ConfigHandler.BOAT_MOTOR_ENABLED);
        assertEquals(BoatMotor.Mode.TOGGLE, ConfigHandler.BOAT_MOTOR_MODE);
        assertEquals(0.6, ConfigHandler.BOAT_MOTOR_MAX_SPEED, 1e-9);
        assertEquals(2.0, ConfigHandler.BOAT_MOTOR_ACCELERATION, 1e-9);
        assertEquals(1, ConfigHandler.BOAT_MOTOR_FUEL_PER_TICK);
        assertEquals(10, ConfigHandler.BOAT_MOTOR_FUEL_INTERVAL);
        assertEquals(6000, ConfigHandler.BOAT_MOTOR_TANK_CAPACITY);
        assertEquals(10, ConfigHandler.BOAT_MOTOR_OUT_OF_WATER_TICKS);
        assertEquals(8, ConfigHandler.RENDER_BOAT_SMOKE_TICKS);
        assertEquals(0, ConfigHandler.KEY_BOAT_MOTOR_TOGGLE);
        assertEquals("enabled", cfg.getCategory(ConfigHandler.INFLATABLEBOAT_MOTOR).getOrderedValues().get(0).getName());
        assertTrue(cfg.getCategory(ConfigHandler.INFLATABLEBOAT_MOTOR).get("tankCapacity").requiresMcRestart());
        assertTrue(cfg.getCategory(ConfigHandler.KEYS).containsKey("boatMotorToggle"));
    }

    private static String oldCopterFuels(String... entries)
    {
        StringBuilder b = new StringBuilder(ConfigHandler.ROOT + " {\n    copterpack {\n        S:fuels <\n");
        for (String e : entries) b.append("            ").append(e).append("\n");
        return b.append("         >\n    }\n}\n").toString();
    }

    @Test
    void untouchedCopterFuelsMoveAsTheNewDefault() throws IOException
    {
        Configuration cfg = load("fuels-untouched.cfg", oldCopterFuels(FuelRules.OLD_COPTER_DEFAULTS));
        String text = new String(Files.readAllBytes(new File(home, "fuels-untouched.cfg").toPath()), StandardCharsets.UTF_8);

        assertArrayEquals(FuelRules.DEFAULTS, ConfigHandler.FUELS);
        assertFalse(cfg.getCategory(ConfigHandler.COPTERPACK).containsKey("fuels"));
        assertTrue(text.contains("biomass=1.5"));
        assertFalse(text.contains("S:fuels <"));
    }

    @Test
    void changedCopterFuelsMoveWithTheirValue() throws IOException
    {
        Configuration cfg = load("fuels-changed.cfg", oldCopterFuels("oil=3.0", "lava=4.0"));
        String text = new String(Files.readAllBytes(new File(home, "fuels-changed.cfg").toPath()), StandardCharsets.UTF_8);

        assertArrayEquals(new String[]{"oil=3.0", "lava=4.0"}, ConfigHandler.FUELS);
        assertFalse(cfg.getCategory(ConfigHandler.COPTERPACK).containsKey("fuels"));
        assertTrue(text.contains("S:list <"));

        // a second launch reads the moved value back
        load("fuels-changed.cfg", null);
        assertArrayEquals(new String[]{"oil=3.0", "lava=4.0"}, ConfigHandler.FUELS);
    }

    @Test
    void anExistingFuelListWinsOverAnOldCopterKey() throws IOException
    {
        String both = ConfigHandler.ROOT + " {\n"
                + "    copterpack {\n        S:fuels <\n            oil=9.0\n         >\n    }\n"
                + "    fuels {\n        S:list <\n            fuel=2.0\n         >\n    }\n"
                + "}\n";
        Configuration cfg = load("fuels-both.cfg", both);
        assertArrayEquals(new String[]{"fuel=2.0"}, ConfigHandler.FUELS);
        assertFalse(cfg.getCategory(ConfigHandler.COPTERPACK).containsKey("fuels"));
    }

    @Test
    void fuelStationTankIsClamped() throws IOException
    {
        load("station-clamp.cfg", ConfigHandler.ROOT + " {\n    fuelstation {\n        I:tankCapacity=5\n    }\n}\n");
        assertEquals(1000, ConfigHandler.FUELSTATION_TANK_CAPACITY);
    }

    @Test
    void nightVisionAlwaysOutlastsItsRefreshPoint()
    {
        ConfigHandler.BAT_NIGHT_VISION_REFRESH_BELOW = 900;
        ConfigHandler.BAT_NIGHT_VISION_TICKS = 500;
        ConfigHandler.validate();
        assertTrue(ConfigHandler.BAT_NIGHT_VISION_TICKS > ConfigHandler.BAT_NIGHT_VISION_REFRESH_BELOW);
    }

    @Test
    void clampHandlesNaNAndBounds()
    {
        assertEquals(1.0, ConfigHandler.clamp(Double.NaN, 1.0, 2.0), 0);
        assertEquals(2.0, ConfigHandler.clamp(9.0, 1.0, 2.0), 0);
        assertEquals(1.5, ConfigHandler.clamp(1.5, 1.0, 2.0), 0);
    }

    @Test
    void legacyKeysPointAtDeclaredCategories() throws IOException
    {
        Configuration cfg = load("targets.cfg", null);
        for (String[] m : ConfigHandler.LEGACY_KEYS)
        {
            List<Property> values = cfg.getCategory(m[1]).getOrderedValues();
            boolean found = false;
            for (Property p : values) found |= p.getName().equals(m[2]);
            assertTrue(found, m[0] + " -> " + m[1] + "." + m[2]);
        }
    }

    @Test
    void scubaKeysLiveInTheirOwnBranchWithDefaults() throws IOException
    {
        Configuration cfg = load("scuba.cfg", null);
        assertTrue(cfg.getCategory(ConfigHandler.ENABLED).containsKey("scuba"));
        assertTrue(cfg.getCategory(ConfigHandler.SCUBA).containsKey("airPerSecond"));
        assertTrue(cfg.getCategory(ConfigHandler.SCUBA_COMPRESSOR).containsKey("handPumpAmount"));
        assertNotNull(cfg.getCategory(ConfigHandler.SCUBA_COMPRESSOR).getComment());
        assertTrue(cfg.getCategory(ConfigHandler.SCUBA).get("ownAirFluid").requiresMcRestart());
        assertTrue(ConfigHandler.SCUBA_ENABLED);
        assertEquals(3000, ConfigHandler.SCUBA_TANK_CAPACITY);
        assertEquals(10, ConfigHandler.SCUBA_AIR_PER_SECOND);
        assertEquals(600, ConfigHandler.SCUBA_LOW_AIR_WARNING);
        assertEquals(16000, ConfigHandler.COMPRESSOR_TANK_CAPACITY);
        assertEquals(10, ConfigHandler.RENDER_SCUBA_BUBBLE_TICKS);
        assertEquals("air", ConfigHandler.SCUBA_BREATHABLE_FLUIDS[0]);
        assertEquals("oxygen", ConfigHandler.SCUBA_BREATHABLE_FLUIDS[1]);
        assertEquals("ic2:airCell=1000>ic2:cell", ConfigHandler.SCUBA_AIR_ITEMS[0]);
    }

    @Test
    void flamethrowerKeysLiveInTheirOwnBranchWithSafeDefaults() throws IOException
    {
        Configuration cfg = load("flamethrower.cfg", null);
        assertTrue(cfg.getCategory(ConfigHandler.ENABLED).containsKey("flamethrower"));
        for (String key : new String[]{"tankCapacity", "fuelPerBurst", "fireIntervalTicks", "range", "coneDegrees", "damage",
                "fireSeconds", "pvp", "ignitesBlocks", "pilotFuelPerMinute", "sound"})
        {
            assertTrue(cfg.getCategory(ConfigHandler.FLAMETHROWER).containsKey(key), key);
            assertNotNull(cfg.getCategory(ConfigHandler.FLAMETHROWER).get(key).comment, key);
        }
        assertNotNull(cfg.getCategory(ConfigHandler.FLAMETHROWER).getComment());
        assertTrue(cfg.getCategory(ConfigHandler.RENDER).containsKey("flamethrowerParticles"));
        assertTrue(cfg.getCategory(ConfigHandler.RECIPES_FLAMETHROWER).containsKey("mode"));
        assertTrue(cfg.getCategory(ConfigHandler.RECIPES_FLAMETHROWER).containsKey("plates"));
        assertTrue(cfg.getCategory(ConfigHandler.FLAMETHROWER).get("tankCapacity").requiresMcRestart());
        assertTrue(ConfigHandler.FLAMETHROWER_ENABLED);
        assertFalse(ConfigHandler.FLAMETHROWER_PVP);
        assertFalse(ConfigHandler.FLAMETHROWER_IGNITES_BLOCKS);
        assertEquals(4000, ConfigHandler.FLAMETHROWER_TANK_CAPACITY);
        assertEquals(50, ConfigHandler.FLAMETHROWER_FUEL_PER_BURST);
        assertEquals(4, ConfigHandler.FLAMETHROWER_FIRE_INTERVAL_TICKS);
        assertEquals(6.0, ConfigHandler.FLAMETHROWER_RANGE, 1e-9);
        assertEquals(25.0, ConfigHandler.FLAMETHROWER_CONE_DEGREES, 1e-9);
        assertEquals(2.0, ConfigHandler.FLAMETHROWER_DAMAGE, 1e-9);
        assertEquals(5, ConfigHandler.FLAMETHROWER_FIRE_SECONDS);
        assertEquals(0, ConfigHandler.FLAMETHROWER_PILOT_FUEL_PER_MINUTE);
        assertEquals(6, ConfigHandler.RENDER_FLAMETHROWER_PARTICLES);
    }
}
