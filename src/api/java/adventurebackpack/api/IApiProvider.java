package adventurebackpack.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * What the Adventure Backpack mod itself provides behind {@link AdventureBackpackAPI}. The mod installs its
 * implementation once, in its preInit; addons never implement or install this interface, they call the static facade.
 * The API package knows nothing of the mod's own classes, and this is the single seam between the two.
 */
public interface IApiProvider
{
    /** The worn wearable of the player (backpack, jetpack, copter pack, scuba gear...) or {@code null}. A copy. */
    ItemStack getWornWearable(EntityPlayer player);

    /** A read-only snapshot of the backpack the player wears, or {@code null} when it is not a backpack. */
    IBackpackView getWornBackpack(EntityPlayer player);

    /** Registers the effect; registering the same effect class twice is harmless. */
    void registerFluidEffect(FluidEffect effect);

    /** Registers the ability of a backpack colour; throws {@link IllegalStateException} after the registry froze. */
    void registerAbility(String colorName, IBackpackAbility ability);

    /** Adds a fuel to the fuel table; the config wins on a name that is in it; throws once the table froze. */
    void registerFuel(String fluidName, double multiplier);

    /** Adds a breathable fluid to the Scuba Gear's table; throws once the table froze. */
    void registerAirFluid(String fluidName);
}
