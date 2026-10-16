package com.darkona.adventurebackpack.fluids.effects;

import adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.FluidRegistry;

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
            BiomeGenBase biome = world.getWorldChunkManager().getBiomeGenAt(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posZ));

            if (
                    BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.HOT) ||
                    BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.DRY) ||
                    BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.SANDY) ||
                    BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.WASTELAND) ||
                    BiomeDictionary.isBiomeOfType(biome, BiomeDictionary.Type.NETHER)
                    )
            {
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
