package adventurebackpack.api;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fluids.Fluid;

/**
 * A player is about to drink a fluid from a tank of the backpack (with the Backpack Hose in drink mode). It is posted on
 * {@code MinecraftForge.EVENT_BUS}, on the logical server only, once per drink and only when the fluid has at least one
 * {@link FluidEffect}: it is a player action and never a per-tick event.
 * <p>
 * The event is posted before any {@link FluidEffect} runs. It is cancelable: when a listener cancels it, no effect is
 * applied and nothing is taken from the tank (the drink did not happen). When it is not canceled the effects run as
 * usual and the hose takes {@link #amount} from the tank.
 */
@Cancelable
public class FluidDrinkEvent extends PlayerEvent
{
    /** The world of the player. */
    public final World world;
    /** The fluid that is about to be drunk. */
    public final Fluid fluid;
    /** Millibuckets the tank loses when the drink goes through. */
    public final int amount;

    public FluidDrinkEvent(EntityPlayer player, World world, Fluid fluid, int amount)
    {
        super(player);
        this.world = world;
        this.fluid = fluid;
        this.amount = amount;
    }
}
