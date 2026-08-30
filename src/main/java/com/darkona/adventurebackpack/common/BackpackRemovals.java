package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

/**
 * Created on 09/01/2015
 *
 * @author Darkona
 */
public class BackpackRemovals
{
    public void itemBat(EntityPlayer player, World world, ItemStack backpack)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.nightVision.id)) {
            potion = player.getActivePotionEffect(Potion.nightVision);

            if (potion != null && potion.getAmplifier() == -4) {
                if (player.worldObj.isRemote) {
                    player.removePotionEffectClient(Potion.nightVision.id);
                } else {
                    player.removePotionEffect(Potion.nightVision.id);
                }
            }
        }
    }

    public void itemSquid(EntityPlayer player, World world, ItemStack backpack)
    {
        itemBat(player, world, backpack);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.waterBreathing.id)) {
            potion = player.getActivePotionEffect(Potion.waterBreathing);

            if (potion != null && AbilityEffects.isSquidWaterBreathing(potion.getAmplifier())) {
                if (player.worldObj.isRemote) {
                    player.removePotionEffectClient(Potion.waterBreathing.id);
                } else {
                    player.removePotionEffect(Potion.waterBreathing.id);
                }
            }
        }
    }

    public void itemPigman(EntityPlayer player, World world, ItemStack backpack)
    {
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.fireResistance.id)) {
            potion = player.getActivePotionEffect(Potion.fireResistance);

            if (potion != null && potion.getAmplifier() == -5) {
                if (player.worldObj.isRemote) {
                    player.removePotionEffectClient(Potion.fireResistance.id);
                } else {
                    player.removePotionEffect(Potion.fireResistance.id);
                }
            }
        }
    }

    /** The Blaze gives the same marked fire resistance as the Pigman. */
    public void itemBlaze(EntityPlayer player, World world, ItemStack backpack)
    {
        itemPigman(player, world, backpack);
    }

    public void itemDragon(EntityPlayer player, World world, ItemStack backpack)
    {
        itemBat(player, world, backpack);
        itemPigman(player,world,backpack);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.damageBoost.id)) {
            potion = player.getActivePotionEffect(Potion.damageBoost);
            if (potion != null && AbilityEffects.isDragonEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.DRAGON_STRENGTH_AMPLIFIER)) {
                if (player.worldObj.isRemote) {
                    player.removePotionEffectClient(Potion.damageBoost.id);
                } else {
                    player.removePotionEffect(Potion.damageBoost.id);
                }
            }
        }
        potion = null;
        if (player.isPotionActive(Potion.regeneration.id)) {
            potion = player.getActivePotionEffect(Potion.regeneration);

            if (potion != null && AbilityEffects.isDragonEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.DRAGON_REGENERATION_AMPLIFIER)) {
                if (player.worldObj.isRemote) {
                    player.removePotionEffectClient(Potion.regeneration.id);
                } else {
                    player.removePotionEffect(Potion.regeneration.id);
                }
            }
        }
    }

    public void itemSlime(EntityPlayer player, World world, ItemStack backpack)
    {
        PotionEffect potion = player.getActivePotionEffect(Potion.moveSpeed);
        if (potion != null && AbilityEffects.isSlimeSpeed(potion.getAmplifier(), potion.getDuration()))
        {
            if (player.worldObj.isRemote) player.removePotionEffectClient(Potion.moveSpeed.id);
            else player.removePotionEffect(Potion.moveSpeed.id);
        }
    }

    public void itemRainbow(EntityPlayer player, World world, ItemStack backpack)
    {
        // a -5 marker speed effect left by older versions must go even while the song still plays
        PotionEffect marker = player.getActivePotionEffect(Potion.moveSpeed);
        if (marker != null && AbilityEffects.isStaleRainbowMarker(marker.getAmplifier()))
        {
            if (player.worldObj.isRemote) player.removePotionEffectClient(Potion.moveSpeed.id);
            else player.removePotionEffect(Potion.moveSpeed.id);
        }
        if (BackpackAbilities.isRainbowRunning(world, backpack)) return;
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.moveSpeed.id)) {
            potion = player.getActivePotionEffect(Potion.moveSpeed);
            if (potion != null && AbilityEffects.isRainbowEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.RAINBOW_SPEED_AMPLIFIER)) {
                if (player.worldObj.isRemote) {
                    player.removePotionEffectClient(Potion.moveSpeed.id);
                } else {
                    player.removePotionEffect(Potion.moveSpeed.id);
                }
            }
        }
        potion = null;
        if (player.isPotionActive(Potion.jump.id)) {
            potion = player.getActivePotionEffect(Potion.jump);
            if (potion != null && AbilityEffects.isRainbowEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.RAINBOW_JUMP_AMPLIFIER)) {
                if (player.worldObj.isRemote) {
                    player.removePotionEffectClient(Potion.jump.id);
                } else {
                    player.removePotionEffect(Potion.jump.id);
                }
            }
        }
    }
}
