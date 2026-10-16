package adventurebackpack.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * The static entry point of the Adventure Backpack API for addons. Declare the dependency in your {@code @Mod}
 * ({@code dependencies = "required-after:adventurebackpack"}, or {@code "after:adventurebackpack"} when the addon
 * works without it) and call these methods from your preInit or later: the mod installs its implementation in its own
 * preInit, which runs before yours.
 * <p>
 * Semantic versioning: adding a method or a class raises the minor version, removing or changing one raises the major
 * version. {@link #getVersion()} is a method and not a constant on purpose: the compiler copies a constant into the
 * addon, and then the addon would report the version it was compiled with and not the one that runs.
 */
public final class AdventureBackpackAPI
{
    private static volatile IApiProvider provider;
    /** Written by an addon in preInit or init, read by the mod once per worn-ability tick. */
    private static volatile boolean abilityEvents;
    /**
     * Fluid effects constructed before the mod installed its implementation (an old addon that loads first and makes
     * its effects in its preInit): the old API registered them at any time, so they wait here and go in at install.
     */
    private static final List<FluidEffect> earlyFluidEffects = new ArrayList<FluidEffect>();

    private AdventureBackpackAPI()
    {
    }

    /** The version of the API that is running, for example {@code "1.0.0"}. */
    public static String getVersion()
    {
        return ApiVersion.VALUE;
    }

    /**
     * Installs the mod's implementation. Only Adventure Backpack calls this, once, in its preInit; a second call
     * throws.
     *
     * @throws IllegalStateException if an implementation is already installed
     */
    public static synchronized void install(IApiProvider implementation)
    {
        if (implementation == null) throw new IllegalArgumentException("The Adventure Backpack API implementation is null");
        if (provider != null) throw new IllegalStateException("The Adventure Backpack API is already installed");
        provider = implementation;
        for (FluidEffect effect : earlyFluidEffects) implementation.registerFluidEffect(effect);
        earlyFluidEffects.clear();
    }

    /** The wearable the player wears on the back (a copy), or {@code null}. Server and client. */
    public static ItemStack getWornWearable(EntityPlayer player)
    {
        return provider().getWornWearable(player);
    }

    /**
     * A read-only snapshot of the backpack the player wears (tanks and slots), or {@code null} when the player wears
     * no backpack. It costs a read of the backpack's NBT: call it on a player action, not every tick.
     */
    public static IBackpackView getWornBackpack(EntityPlayer player)
    {
        return provider().getWornBackpack(player);
    }

    /**
     * Registers what happens to a player who drinks the effect's fluid. Constructing a {@link FluidEffect} already
     * registers it, so addons written for the old API keep working; calling this for an effect that is already
     * registered does nothing. An effect made before the mod's preInit is kept and registered when the mod starts.
     */
    public static void registerFluidEffect(FluidEffect effect)
    {
        IApiProvider current = provider;
        if (current == null)
        {
            synchronized (AdventureBackpackAPI.class)
            {
                current = provider;
                if (current == null)
                {
                    if (effect == null) throw new IllegalArgumentException("The fluid effect is null");
                    earlyFluidEffects.add(effect);
                    return;
                }
            }
        }
        current.registerFluidEffect(effect);
    }

    /**
     * Registers the ability of a backpack colour. Do it in preInit or init: after postInit the registry is frozen and
     * this throws {@link IllegalStateException}, as it does when the colour already has an ability.
     */
    public static void registerAbility(String colorName, IBackpackAbility ability)
    {
        provider().registerAbility(colorName, ability);
    }

    /**
     * Registers a liquid fuel by the name the mod that adds it gives its fluid ({@code FluidRegistry} name, for example
     * {@code "oil"}), with a cost multiplier: every fuel user (Copter Pack, Fuel Station, boat motor, Flamethrower Pack)
     * multiplies its own cost by it, so a higher value burns faster. The fuel is added to the fuels of the config key
     * {@code fuels.list}; the config always has the last word: when the name is already in the list the config's
     * multiplier stays. The code can only add: a name the config removes cannot be detected, so it is not removed
     * from what an addon registers. A name already registered by another addon keeps the first multiplier.
     * <p>
     * Do it in preInit or init: after the mod's postInit starts the fuel table is frozen and this throws
     * {@link IllegalStateException}. The fluid itself does not have to exist: a fuel of a mod that is not loaded is
     * simply never used.
     *
     * @param fluidName  the registered name of the fluid, not empty
     * @param multiplier cost multiplier, greater than 0 and finite
     * @throws IllegalArgumentException if the name is empty or the multiplier is not a positive finite number
     * @throws IllegalStateException    when called after the table froze
     */
    public static void registerFuel(String fluidName, double multiplier)
    {
        provider().registerFuel(fluidName, multiplier);
    }

    /**
     * Registers a fluid the Scuba Gear tanks and the Air Compressor take as air, by its registered name (for example
     * {@code "oxygen"}). It is added to the fluids of the config key {@code scuba.breathableFluids}; the code can only
     * add, a name the config removes cannot be detected. Do it in preInit or init: after the mod's postInit starts the
     * table is frozen and this throws {@link IllegalStateException}.
     *
     * @param fluidName the registered name of the fluid, not empty
     * @throws IllegalArgumentException if the name is empty
     * @throws IllegalStateException    when called after the table froze
     */
    public static void registerAirFluid(String fluidName)
    {
        provider().registerAirFluid(fluidName);
    }

    /**
     * Asks the mod to post {@link BackpackAbilityEvent}. The event can be posted every tick for every player that wears
     * a backpack, so the mod posts it only after an addon that listens to it calls this, once, in preInit or init. It
     * cannot be turned off again. Does not need the mod's implementation: it can be called before it is installed.
     */
    public static void enableAbilityEvents()
    {
        abilityEvents = true;
    }

    /** True once an addon called {@link #enableAbilityEvents()}. The mod reads it before building the event. */
    public static boolean areAbilityEventsEnabled()
    {
        return abilityEvents;
    }

    private static IApiProvider provider()
    {
        IApiProvider current = provider;
        if (current == null)
        {
            throw new IllegalStateException("The Adventure Backpack API is not ready: declare "
                    + "required-after:adventurebackpack in your @Mod and call it from preInit or later");
        }
        return current;
    }
}
