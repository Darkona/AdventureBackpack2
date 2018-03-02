package com.darkona.adventurebackpack.fluids.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.util.Utils;
import adventurebackpack.api.FluidEffect;

/**
 * Created by Darkona on 12/10/2014.
 */
public class MelonJuiceEffect extends FluidEffect
{
    public MelonJuiceEffect()
    {
        super(ModFluids.melonJuice, 30);
    }

    @Override
    public void affectDrinker(World world, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            player.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), timeInTicks, 0));
            player.addPotionEffect(new PotionEffect(Potion.regeneration.id, Utils.secondsToTicks(timeInSeconds), 0));
        }
    }
}