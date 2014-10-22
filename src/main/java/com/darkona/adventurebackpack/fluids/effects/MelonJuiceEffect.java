package com.darkona.adventurebackpack.fluids.effects;

import com.darkona.adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.api.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModFluids;
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
        super(ModFluids.melonJuice, 7, "Ahhh, refreshing!");
    }

    /**
     * This method determines what will happen to the player when drinking the
     * corresponding fluid. For example set potion effects, set player on fire,
     * heal, fill hunger, etc. You can use the world parameter to make
     * conditions based on where the player is.
     *
     * @param world  The World.
     * @param player The Player.
     */
    @Override
    public void affectDrinker(World world, EntityPlayer player)
    {
        player.addPotionEffect(new PotionEffect(Potion.digSpeed.getId(), 600, 0));
        FluidEffectRegistry.WATER_EFFECT.affectDrinker(world, player);
    }
}
