package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityBolt;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class CrossbowEventHandler
{
    private static EntityBolt bolt(DamageSource source)
    {
        if (source == null) return null;
        Entity direct = source.getSourceOfDamage();
        return direct instanceof EntityBolt ? (EntityBolt) direct : null;
    }

    @SubscribeEvent
    public void onLivingAttack(LivingAttackEvent event)
    {
        EntityBolt bolt = bolt(event.source);
        if (bolt == null) return;
        if (ConfigHandler.CROSSBOW_IGNORE_HURT_COOLDOWN)
        {
            event.entityLiving.hurtResistantTime = 0;
        }
        if (!event.entityLiving.worldObj.isRemote) bolt.markHit();
    }
}
