package com.darkona.adventurebackpack.fluids.effects;

import com.darkona.adventurebackpack.api.FluidEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Darkona on 12/10/2014.
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
            EntityPlayer player = (EntityPlayer) entity;
            if (!world.isRemote)
            {
                world.playSoundAtEntity(player, "mob.cow.say", 0.3F, 1);
            }
            player.clearActivePotions();
            //player.curePotionEffects(new ItemStack(new ItemBucketMilk()));
        }
    }
}
