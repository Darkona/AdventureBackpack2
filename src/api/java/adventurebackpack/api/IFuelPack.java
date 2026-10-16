package adventurebackpack.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * A wearable with a liquid fuel tank that the Fuel Station fills: the Copter Pack now, the Flamethrower Pack later.
 * Server side only, on a player action or a change of the station's slots (never every tick).
 */
public interface IFuelPack
{
    /**
     * Puts up to {@code offered.amount} of {@code offered} into the pack's fuel tank and returns how much it took (or
     * would take, when {@code doFill} is false). Takes nothing when the fluid is not on fuels.list or the tank holds
     * another fuel. With {@code doFill} the new contents are written into the stack's NBT.
     */
    int fillFuel(ItemStack pack, FluidStack offered, boolean doFill);
}
