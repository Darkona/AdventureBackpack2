package adventurebackpack.api;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * A Class you must extend to add player effects when drinking different fluids.
 * The timeInSeconds and timeInSeconds in ticks may serve to set the duration of the effect.
 *
 * @author Darkona
 */
public abstract class FluidEffect
{
    public Fluid fluid;
    public int timeInSeconds;
    public int timeInTicks;
    public int effectID;

    public FluidEffect(Fluid fluid)
    {
        this(fluid, 5);
    }

    public FluidEffect(Fluid fluid, int timeInSeconds)
    {

        this.timeInSeconds = timeInSeconds;
        this.fluid = fluid;
        this.timeInTicks = this.timeInSeconds * 20;
        this.effectID = 0;
        com.darkona.adventurebackpack.fluids.FluidEffectRegistry.registerFluidEffect(this);
    }


    public FluidEffect(String fluidName, int timeInSeconds)
    {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        this.timeInSeconds = timeInSeconds;
        this.fluid = fluid;
        this.timeInTicks = this.timeInSeconds * 20;
        this.effectID = 0;

        com.darkona.adventurebackpack.fluids.FluidEffectRegistry.registerFluidEffect(this);
    }

    /**
     * This method determines what will happen to the player (or world!) when drinking the
     * corresponding fluid. For example set potion effects, set player on fire,
     * heal, fill hunger, etc. You can use the world parameter to make
     * conditions based on where the player is.
     *
     * @param world  The World.
     * @param entity The entity that will be affected.
     */
    public abstract void affectDrinker(World world, Entity entity);

}
