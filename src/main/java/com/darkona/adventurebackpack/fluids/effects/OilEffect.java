package com.darkona.adventurebackpack.fluids.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import adventurebackpack.api.FluidEffect;

/**
 * Created by Darkona on 12/10/2014.
 */
public class OilEffect extends FluidEffect
{
    public OilEffect()
    {
        super(FluidRegistry.getFluid("oil"), 20);
    }

    @Override
    public void affectDrinker(World world, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), this.timeInTicks, 2));
            player.addPotionEffect(new PotionEffect(Potion.poison.getId(), this.timeInTicks / 2, 1));
        }
    }
}
