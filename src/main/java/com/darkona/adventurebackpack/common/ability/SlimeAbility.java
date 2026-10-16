package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityEffects;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.messages.EntityParticlePacket;
import com.darkona.adventurebackpack.util.TickThrottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class SlimeAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.SLIME_ENABLED) return;
        if (!world.isRemote)
        {
            if (ConfigHandler.SLIME_FULL_MOON_SPEED && world.getMoonPhase() == 0 && !world.isDaytime())
            {
                giveFullMoonSpeed(player);
            }
            else
            {
                onRemoval(player, world, backpack);
            }
        }
        if (player.onGround)
        {
            if ((player.moveForward == 0 && player.moveStrafing == 0) && (Math.abs(player.moveForward) < 3 && Math.abs(player.moveStrafing)<3) )
            {
                player.addVelocity(player.motionX *= 0.828, 0, player.motionZ *= 0.828);
            }
            if (player.isSprinting() && !world.isRemote
                    && TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.SLIME_TRAIL_INTERVAL_TICKS))
            {
                ModNetwork.sendToNearby(new EntityParticlePacket.Message(EntityParticlePacket.SLIME_PARTICLE, player), player);
                world.playSoundAtEntity(player, "mob.slime.small", 0.6F, (world.rand.nextFloat() - world.rand.nextFloat()) * 1F);
            }
        }
    }

    @Override
    public void onRemoval(EntityPlayer player, World world, ItemStack backpack)
    {
        PotionEffect potion = player.getActivePotionEffect(Potion.moveSpeed);
        if (potion != null && AbilityEffects.isSlimeSpeed(potion.getAmplifier(), potion.getDuration()))
        {
            AbilityHelper.removeEffect(player, Potion.moveSpeed.id);
        }
    }

    private static void giveFullMoonSpeed(EntityPlayer player)
    {
        PotionEffect speed = player.getActivePotionEffect(Potion.moveSpeed);
        if (AbilityEffects.needsShortRefresh(speed != null,
                speed != null ? speed.getDuration() : 0,
                speed != null ? speed.getAmplifier() : 0, AbilityEffects.SLIME_SPEED_AMPLIFIER))
        {
            player.addPotionEffect(new PotionEffect(Potion.moveSpeed.getId(),
                    AbilityEffects.SHORT_EFFECT_TICKS, AbilityEffects.SLIME_SPEED_AMPLIFIER));
        }
    }
}
