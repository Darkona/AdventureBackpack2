package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityBolt;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/**
 * What a crossbow bolt does when it hits a mob. The event fires only on a hit (Tier 2) and returns at once when the
 * damage does not come from a bolt.
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
}
