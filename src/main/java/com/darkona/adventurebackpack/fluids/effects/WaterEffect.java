package com.darkona.adventurebackpack.fluids.effects;

import adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created on 12/10/2014.
 *
 * @author Javier Darkona
 */
@SuppressWarnings("unused")
public class WaterEffect extends FluidEffect
{

    public WaterEffect()
    {
        super(FluidRegistry.WATER, 7);
    }

    @Override
    public void affectDrinker(World world, Entity entity)
    {
        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            {
                // player.getFoodStats().addStats(1, 0.1f);
                if (player.isBurning())
                {
                    player.extinguish();
                }else {
                    player.addPotionEffect(new PotionEffect(Potion.regeneration.id, Utils.secondsToTicks(timeInSeconds), 0));
                }
            }

        }
    }
}
