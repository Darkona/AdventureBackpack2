package com.darkona.adventurebackpack.fluids.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import adventurebackpack.api.FluidEffect;

/**
 * Created on 12/10/2014.
 *
 * @author Javier Darkona
 */
public class MilkEffect extends FluidEffect
{
    public MilkEffect()
    {
        super(FluidRegistry.getFluid("milk"), 7);
    }

    @Override
    public void affectDrinker(World world, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            ((EntityPlayer) entity).clearActivePotions();
        }
    }
}
