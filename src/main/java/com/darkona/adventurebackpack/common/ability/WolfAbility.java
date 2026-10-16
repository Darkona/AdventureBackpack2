package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.TickThrottle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class WolfAbility implements IBackpackAbility
{
    @Override
    @SuppressWarnings("unchecked")
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.WOLF_ENABLED || world.isRemote) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.WOLF_CHECK_INTERVAL_TICKS)) return;
        List<EntityWolf> wolves = world.getEntitiesWithinAABB(
                EntityWolf.class,
                AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                        player.posX + 1.0D, player.posY + 1.0D,
                        player.posZ + 1.0D).expand(ConfigHandler.WOLF_RANGE, 4.0D, ConfigHandler.WOLF_RANGE));
        for (EntityWolf wolf : wolves)
        {
            if (wolf.isAngry() && wolf.getAttackTarget() == player)
            {
                wolf.setAngry(false);
                wolf.setAttackTarget(null);
                wolf.setRevengeTarget(null);
                resetTasks(wolf.targetTasks);
            }
        }
    }

    static void resetTasks(EntityAITasks tasks)
    {
        for (Object entry : tasks.taskEntries)
        {
            ((EntityAITasks.EntityAITaskEntry) entry).action.resetTask();
        }
    }
}
