package com.darkona.adventurebackpack.fluids.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

import com.darkona.adventurebackpack.util.Utils;
import adventurebackpack.api.FluidEffect;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */

public class FuelEffect extends FluidEffect
{

    public FuelEffect()
    {
        super(FluidRegistry.getFluid("fuel"), 20);
    }

    /**
     * This method determines what will happen to the player when drinking the
     * corresponding fluid. For example set potion effects, set player on fire,
     * heal, fill hunger, etc. You can use the world parameter to make
     * conditions based on where the player is.
     *
     * @param world  The World.
     * @param entity The Player.
     */
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
