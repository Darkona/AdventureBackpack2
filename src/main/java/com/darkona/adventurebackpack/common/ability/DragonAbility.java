package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityEffects;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class DragonAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.DRAGON_ENABLED) return;
        AbilityHelper.giveFireResistance(player, ConfigHandler.PIGMAN_FIRE_RESISTANCE_TICKS);
        AbilityHelper.giveWaterBreathing(player, world);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.regeneration.id)) {
            potion = player.getActivePotionEffect(Potion.regeneration);
        }
        if (potion == null || AbilityEffects.needsRefresh(potion.getDuration()))
        {
            player.addPotionEffect(new PotionEffect(Potion.regeneration.getId(), ConfigHandler.DRAGON_EFFECT_TICKS, ConfigHandler.DRAGON_REGENERATION_AMPLIFIER));
        }
        potion = null;
        if (player.isPotionActive(Potion.damageBoost.id)) {
            potion = player.getActivePotionEffect(Potion.damageBoost);
        }
        if (potion == null || AbilityEffects.needsRefresh(potion.getDuration())) {
            player.addPotionEffect(new PotionEffect(Potion.damageBoost.getId(), ConfigHandler.DRAGON_EFFECT_TICKS, ConfigHandler.DRAGON_STRENGTH_AMPLIFIER));
        }
    }

    @Override
    public void onRemoval(EntityPlayer player, World world, ItemStack backpack)
    {
        AbilityHelper.removeNightVision(player);
        AbilityHelper.removeMarkedFireResistance(player);
        PotionEffect potion = null;
        if (player.isPotionActive(Potion.damageBoost.id)) {
            potion = player.getActivePotionEffect(Potion.damageBoost);
            if (potion != null && AbilityEffects.isDragonEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.DRAGON_STRENGTH_AMPLIFIER)) {
                AbilityHelper.removeEffect(player, Potion.damageBoost.id);
            }
        }
        potion = null;
        if (player.isPotionActive(Potion.regeneration.id)) {
            potion = player.getActivePotionEffect(Potion.regeneration);

            if (potion != null && AbilityEffects.isDragonEffect(potion.getAmplifier(), potion.getDuration(), ConfigHandler.DRAGON_REGENERATION_AMPLIFIER)) {
                AbilityHelper.removeEffect(player, Potion.regeneration.id);
            }
        }
    }
}
