package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adventurebackpack.api.BackpackShape;
import adventurebackpack.api.IBackpackAbility;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.junit.jupiter.api.Test;

/**
 * Pins the colours that have an ability to the lists the old reflection tables had (BackpackAbilities.item&lt;Color&gt;,
 * tile&lt;Color&gt; and BackpackRemovals.item&lt;Color&gt; before the Strategy + Registry refactor), plus the registry rules.
 */
class AbilityRegistryTest
{
    /** validWearingBackpacks of the old class. (itemIronGolem existed but was empty and never listed: not an ability.) */
    private static final Set<String> WORN = sorted("Bat", "Squid", "Pigman", "Cactus", "Cow", "Pig", "Dragon", "Slime",
            "Chicken", "Wolf", "Ocelot", "Creeper", "Rainbow", "Melon", "Sunflower", "Mooshroom", "Blaze");

    /** The tile&lt;Color&gt; methods of the old class. */
    private static final Set<String> PLACED = sorted("Cactus", "Melon");

    /** validRemovalBackpacks of the old class, also the item&lt;Color&gt; methods of BackpackRemovals. */
    private static final Set<String> REMOVAL = sorted("Bat", "Squid", "Pigman", "Dragon", "Rainbow", "Slime", "Blaze");

    private static Set<String> sorted(String... names)
    {
        return Collections.unmodifiableSet(new TreeSet<String>(Arrays.asList(names)));
    }

    private static AbilityRegistry builtIn()
    {
        AbilityRegistry registry = new AbilityRegistry();
        registry.registerBuiltIn();
        return registry;
    }

    @Test
    void wornColoursAreTheOnesOfTheOldTable()
    {
        assertEquals(WORN, builtIn().wornColors());
    }

    @Test
    void placedColoursAreTheOnesOfTheOldTable()
    {
        assertEquals(PLACED, builtIn().placedColors());
    }

    @Test
    void removalColoursAreTheOnesOfTheOldTable()
    {
        assertEquals(REMOVAL, builtIn().removalColors());
    }

    @Test
    void theIronGolemAndStandardBackpacksHaveNoAbility()
    {
        AbilityRegistry registry = builtIn();
        assertNull(registry.worn("IronGolem"));
        assertNull(registry.worn("Standard"));
        assertNull(registry.removal("Standard"));
        assertNull(registry.placed("Bat"));
    }

    @Test
    void theModsOwnRegistryIsTheOneTheDispatcherReads()
    {
        // BackpackAbilities.hasAbility / hasRemoval read AbilityRegistry.INSTANCE: fill it as preInit does
        AbilityRegistry registry = AbilityRegistry.INSTANCE;
        if (!registry.isFrozen() && registry.wornColors().isEmpty()) registry.registerBuiltIn();
        for (String color : WORN) assertTrue(BackpackAbilities.hasAbility(color), color);
        for (String color : REMOVAL) assertTrue(BackpackAbilities.hasRemoval(color), color);
        assertFalse(BackpackAbilities.hasAbility("IronGolem"));
        assertFalse(BackpackAbilities.hasRemoval("Cactus"));
        for (String color : PLACED) assertTrue(BackpackAbilities.hasPlacedAbility(color), color);
        assertFalse(BackpackAbilities.hasPlacedAbility("Bat"));
    }

    @Test
    void anAbilityWithOnlyAPlacedHookRunsAsABlock()
    {
        // The placed backpack used to run its ability only when the colour had a worn one (the saved "special" flag)
        AbilityRegistry registry = AbilityRegistry.INSTANCE;
        if (!registry.isFrozen() && registry.wornColors().isEmpty()) registry.registerBuiltIn();
        if (registry.placed("TestPlacedOnly") == null)
        {
            registry.register("TestPlacedOnly", new IBackpackAbility()
            {
                @Override
                public void onPlacedTick(World world, TileEntity backpackTile) {}
            });
        }
        assertTrue(BackpackAbilities.hasPlacedAbility("TestPlacedOnly"));
        assertFalse(BackpackAbilities.hasAbility("TestPlacedOnly"));
    }

    @Test
    void aHookIsRegisteredOnlyIfTheAbilityOverridesIt()
    {
        AbilityRegistry registry = new AbilityRegistry();
        registry.register("Nothing", new IBackpackAbility() {});
        registry.register("OnlyRemoval", new IBackpackAbility()
        {
            @Override
            public void onRemoval(EntityPlayer player, World world, ItemStack backpack) {}
        });
        assertNull(registry.worn("Nothing"));
        assertNull(registry.placed("Nothing"));
        assertNull(registry.removal("Nothing"));
        assertNull(registry.worn("OnlyRemoval"));
        assertNotNull(registry.removal("OnlyRemoval"));
    }

    @Test
    void theRegisteredAbilityIsTheSameObject()
    {
        AbilityRegistry registry = new AbilityRegistry();
        IBackpackAbility ability = new IBackpackAbility()
        {
            @Override
            public void onWornTick(EntityPlayer player, World world, ItemStack backpack) {}
        };
        registry.register("Custom", ability);
        assertSame(ability, registry.worn("Custom"));
    }

    @Test
    void aColourCannotBeRegisteredTwice()
    {
        AbilityRegistry registry = new AbilityRegistry();
        registry.register("Bat", new IBackpackAbility() {});
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> registry.register("Bat", new IBackpackAbility() {}));
        assertTrue(e.getMessage().contains("Bat"), e.getMessage());
        assertTrue(e.getMessage().contains("already"), e.getMessage());
    }

    @Test
    void aFrozenRegistryRefusesNewAbilitiesButKeepsReading()
    {
        AbilityRegistry registry = builtIn();
        registry.freeze();
        assertTrue(registry.isFrozen());
        IllegalStateException e = assertThrows(IllegalStateException.class,
                () -> registry.register("Late", new IBackpackAbility() {}));
        assertTrue(e.getMessage().contains("frozen"), e.getMessage());
        assertEquals(WORN, registry.wornColors());
    }

    @Test
    void aNameAndAnAbilityAreRequired()
    {
        AbilityRegistry registry = new AbilityRegistry();
        assertThrows(IllegalArgumentException.class, () -> registry.register("", new IBackpackAbility() {}));
        assertThrows(IllegalArgumentException.class, () -> registry.register(null, new IBackpackAbility() {}));
        assertThrows(IllegalArgumentException.class, () -> registry.register("Bat", null));
    }

    @Test
    void theShapeHookIsOptionalAndCannotBreakTheWindow()
    {
        AbilityRegistry registry = new AbilityRegistry();
        BackpackShape classic = new BackpackShape(6, 8);
        registry.register("Plain", new IBackpackAbility() {});
        registry.register("Big", new IBackpackAbility()
        {
            @Override
            public BackpackShape getShape(BackpackShape configured) { return configured.withRows(8); }
        });
        registry.register("Null", new IBackpackAbility()
        {
            @Override
            public BackpackShape getShape(BackpackShape configured) { return null; }
        });
        registry.register("Broken", new IBackpackAbility()
        {
            @Override
            public BackpackShape getShape(BackpackShape configured) { throw new IllegalStateException("addon bug"); }
        });
        assertEquals(new TreeSet<String>(Arrays.asList("Big", "Broken", "Null")), registry.shapedColors());
        assertSame(classic, registry.shape("Plain", classic));
        assertSame(classic, registry.shape("Unknown", classic));
        assertEquals(new BackpackShape(8, 8), registry.shape("Big", classic));
        assertSame(classic, registry.shape("Null", classic));
        assertSame(classic, registry.shape("Broken", classic));
        assertEquals(39, classic.getStorageSlots());
    }
}
