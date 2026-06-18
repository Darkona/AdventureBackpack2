package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityBolt;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

/**
 * What a crossbow bolt does when it hits a mob. Both events fire only on a hit (Tier 2), and both return at once
 * when the damage does not come from a bolt.
 */
public class CrossbowEventHandler
{
    private static EntityBolt bolt(DamageSource source)
    {
        if (source == null) return null;
        Entity direct = source.getSourceOfDamage();
        return direct instanceof EntityBolt ? (EntityBolt) direct : null;
    }

    /** Fires before the invulnerability check, so clearing it lets every bolt of a burst land. */
    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event)
    {
        if (ConfigHandler.CROSSBOW_IGNORE_HURT_COOLDOWN && bolt(event.source) != null)
        {
            event.entityLiving.hurtResistantTime = 0;
        }
    }

    /** Fires when the damage really lands: apply the effect of an elemental bolt. Blazing is handled by the arrow fire. */
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        EntityBolt bolt = bolt(event.source);
        EntityLivingBase target = event.entityLiving;
        if (bolt == null || target.worldObj.isRemote || !ConfigHandler.CROSSBOW_ELEMENTAL_ENABLED) return;
        switch (bolt.getBoltType())
        {
            case CrossbowLogic.FREEZING:
                target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, ConfigHandler.CROSSBOW_FREEZING_TICKS, ConfigHandler.CROSSBOW_FREEZING_AMPLIFIER));
                break;
            case CrossbowLogic.POISON:
                target.addPotionEffect(new PotionEffect(Potion.poison.id, ConfigHandler.CROSSBOW_POISON_TICKS, ConfigHandler.CROSSBOW_POISON_AMPLIFIER));
                break;
            case CrossbowLogic.STUNNING:
                target.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, ConfigHandler.CROSSBOW_STUNNING_TICKS, ConfigHandler.CROSSBOW_STUNNING_AMPLIFIER));
                if (target instanceof EntityPlayer)
                {
                    // Nausea only shows on a player's screen; mobs ignore it.
                    target.addPotionEffect(new PotionEffect(Potion.confusion.id, ConfigHandler.CROSSBOW_STUNNING_TICKS + 40, 0));
                }
                break;
            default:
                break;
        }
    }
}
