package com.darkona.adventurebackpack.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * A Class you must extend to add player effects when drinking different fluids.
 * The time and time in ticks may serve to set the duration of the effect.
 *
 * @author Darkona
 */
public abstract class FluidEffect
{
    public Fluid fluid;
    public int time;
    public int timeInTicks;
    public int effectID;
    public String msg = "You drank something";

    public FluidEffect()
    {
        this.effectID = -1;
        this.time = 0;
        this.msg = "";
        this.timeInTicks = 0;
    }

    public FluidEffect(Fluid fluid)
    {
        this(fluid, 5);
    }

    public FluidEffect(Fluid fluid, int time)
    {
        this(fluid, time, "");
    }

    public FluidEffect(Fluid fluid, int time, String msg)
    {
        this.time = time;
        this.fluid = fluid;
        this.msg = msg;
        this.timeInTicks = this.time * 20;
    }

    public FluidEffect(String fluidName, int time, String msg)
    {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        this.time = time;
        this.fluid = fluid;
        this.msg = msg;
        this.timeInTicks = this.time * 20;
    }

    /**
     * This method determines what will happen to the player when drinking the
     * corresponding fluid. For example set potion effects, set player on fire,
     * heal, fill hunger, etc. You can use the world parameter to make
     * conditions based on where the player is.
     *
     * @param world  The World.
     * @param player The Player.
     */
    public abstract void affectDrinker(World world, EntityPlayer player);

}
