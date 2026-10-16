package com.darkona.adventurebackpack.fluids.effects;

import adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModFluids;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

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
            FluidEffectRegistry.WATER_EFFECT.affectDrinker(world, player);
        }
    }
}
