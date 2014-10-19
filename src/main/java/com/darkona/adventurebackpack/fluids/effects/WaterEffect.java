package com.darkona.adventurebackpack.fluids.effects;

import com.darkona.adventurebackpack.api.FluidEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenDesert;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class WaterEffect extends FluidEffect
{

    public WaterEffect()
    {
        super(FluidRegistry.WATER, 7);
        msg = "Water is refreshing";
    }

    @Override
    public void affectDrinker(World world, EntityPlayer player)
    {
        if (world.provider.isHellWorld ||
                world.provider.isSurfaceWorld() && world.provider.getBiomeGenForCoords(player.serverPosX, player.serverPosZ).biomeName.toLowerCase().contains("desert"))
        {
            player.getFoodStats().addStats(1, 0.1f);
            player.addPotionEffect(new PotionEffect(Potion.regeneration.id, time * 20, -1, false));
        }
        if (player.isBurning())
        {
            player.extinguish();
        }
    }
}
