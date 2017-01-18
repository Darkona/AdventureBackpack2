package com.darkona.adventurebackpack.fluids.effects;

import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.util.Utils;

import adventurebackpack.api.FluidEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Created by Darkona on 12/10/2014.
 */
public class MelonJuiceEffect extends FluidEffect
{

    public MelonJuiceEffect()
    {
        super(ModFluids.melonJuice, 30);
    }

    /**
     * This method determines what will happen to the player when drinking the
     * corresponding fluid. For example set potion effects, set player on fire,
     * heal, fill hunger, etc. You can use the world parameter to make
     * conditions based on where the player is.
     *
     * @param world
     *            The World.
     * @param player
     *            The Player.
     */
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