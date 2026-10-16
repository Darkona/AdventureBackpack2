package adventurebackpack.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * The API package is a separate artifact: it sees Minecraft and Forge and never the mod's own classes. The compiler
 * already refuses it (the api source set has no com.darkona classes); this test keeps the rule when someone moves a
 * file or changes the build.
 */
class ApiBoundaryTest
{
    private static final Path API_SOURCES = Paths.get("src", "api", "java");

    @Test
    void apiSourcesNeverNameTheModsOwnPackages() throws IOException
    {
        List<String> offenders = new ArrayList<String>();
        int files = 0;
        try (Stream<Path> walk = Files.walk(API_SOURCES))
        {
            for (Path file : (Iterable<Path>) walk.filter(p -> p.toString().endsWith(".java"))::iterator)
            {
                files++;
                for (String line : Files.readAllLines(file))
                {
                    if (line.contains("com.darkona")) offenders.add(file + ": " + line.trim());
                }
            }
        }
        assertTrue(files > 0, "no API sources found under " + API_SOURCES.toAbsolutePath());
        assertTrue(offenders.isEmpty(), "the API must not name com.darkona: " + offenders);
    }

    @Test
    void apiSourcesAllLiveInTheApiPackage() throws IOException
    {
        try (Stream<Path> walk = Files.walk(API_SOURCES))
        {
            walk.filter(p -> p.toString().endsWith(".java")).forEach(p ->
                assertTrue(p.startsWith(API_SOURCES.resolve("adventurebackpack").resolve("api")), p + " is outside adventurebackpack/api"));
        }
    }

    // One test method: the facade holds the installed implementation in a static for the whole JVM
    @Test
    void facadeFailsUntilInstalledThenDelegatesAndInstallsOnlyOnce()
    {
        assertTrue(AdventureBackpackAPI.getVersion().matches("\\d+\\.\\d+\\.\\d+.*"), AdventureBackpackAPI.getVersion());
        assertThrows(IllegalStateException.class, () -> AdventureBackpackAPI.registerAbility("X", new IBackpackAbility() { }));
        assertThrows(IllegalStateException.class, () -> AdventureBackpackAPI.getWornWearable(null));
        assertThrows(IllegalArgumentException.class, () -> AdventureBackpackAPI.install(null));
        // An old addon that loads first makes its effect before the mod's preInit: kept, and registered at install
        FluidEffect early = new FluidEffect(new Fluid("apitestearly"))
        {
            public void affectDrinker(World world, Entity entity) { }
        };

        final List<Object> calls = new ArrayList<Object>();
        AdventureBackpackAPI.install(new IApiProvider()
        {
            public ItemStack getWornWearable(EntityPlayer player) { calls.add("wearable"); return null; }
            public IBackpackView getWornBackpack(EntityPlayer player) { calls.add("backpack"); return null; }
            public void registerFluidEffect(FluidEffect effect) { calls.add(effect); }
            public void registerAbility(String colorName, IBackpackAbility ability) { calls.add(colorName); }
            public void registerFuel(String fluidName, double multiplier) { calls.add(fluidName + "=" + multiplier); }
            public void registerAirFluid(String fluidName) { calls.add("air:" + fluidName); }
        });
        assertThrows(IllegalStateException.class, () -> AdventureBackpackAPI.install(new IApiProvider()
        {
            public ItemStack getWornWearable(EntityPlayer player) { return null; }
            public IBackpackView getWornBackpack(EntityPlayer player) { return null; }
            public void registerFluidEffect(FluidEffect effect) { }
            public void registerAbility(String colorName, IBackpackAbility ability) { }
            public void registerFuel(String fluidName, double multiplier) { }
            public void registerAirFluid(String fluidName) { }
        }));

        assertNull(AdventureBackpackAPI.getWornWearable(null));
        assertNull(AdventureBackpackAPI.getWornBackpack(null));
        AdventureBackpackAPI.registerAbility("Cow", new IBackpackAbility() { });
        // The constructor of an effect registers it through the facade (the old addons rely on it)
        FluidEffect effect = new FluidEffect(new Fluid("apitest"))
        {
            public void affectDrinker(World world, Entity entity) { }
        };
        AdventureBackpackAPI.registerFuel("lava", 4.0);
        AdventureBackpackAPI.registerAirFluid("oxygen");
        // The ability events are off until an addon asks for them, and cannot be turned off again
        assertFalse(AdventureBackpackAPI.areAbilityEventsEnabled());
        AdventureBackpackAPI.enableAbilityEvents();
        assertTrue(AdventureBackpackAPI.areAbilityEventsEnabled());
        assertEquals(7, calls.size());
        assertSame(early, calls.get(0));
        assertEquals("lava=4.0", calls.get(5));
        assertEquals("air:oxygen", calls.get(6));
        assertEquals("wearable", calls.get(1));
        assertEquals("backpack", calls.get(2));
        assertEquals("Cow", calls.get(3));
        assertSame(effect, calls.get(4));
    }
}
