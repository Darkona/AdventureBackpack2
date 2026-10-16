package com.darkona.adventurebackpack.fluids.effects;

import adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

public class FuelEffect extends FluidEffect
{
    public FuelEffect()
    {
        super(FluidRegistry.getFluid("fuel"), 20);
    }

    @Override
    public void affectDrinker(World world, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            player.addPotionEffect(new PotionEffect(Potion.confusion.getId(), this.timeInTicks, 2));
            player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), this.timeInTicks, 1));
            player.addPotionEffect(new PotionEffect(Potion.poison.getId(), Utils.secondsToTicks(8), 0));
        }
    }
}
