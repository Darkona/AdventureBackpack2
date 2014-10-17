package com.darkona.adventurebackpack.fluids.effects;

import com.darkona.adventurebackpack.api.FluidEffect;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class MilkEffect extends FluidEffect
{
    public MilkEffect()
    {
        super(FluidRegistry.getFluid("milk"), 7, "Milk makes you feel good");
    }

    @Override
    public void affectDrinker(World world, EntityPlayer player)
    {
        if (!world.isRemote)
        {
            world.playSoundAtEntity(player, "mob.cow.say", 0.3F, 1);
        }
        player.curePotionEffects(new ItemStack(new ItemBucketMilk()));
    }
}
