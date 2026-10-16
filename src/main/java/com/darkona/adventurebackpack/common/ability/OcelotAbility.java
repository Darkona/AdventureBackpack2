package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.ai.EntityAIAvoidPlayerWithBackpack;
import com.darkona.adventurebackpack.util.TickThrottle;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class OcelotAbility implements IBackpackAbility
{
    @Override
    @SuppressWarnings("unchecked")
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.OCELOT_ENABLED || world.isRemote) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), ConfigHandler.OCELOT_CHECK_INTERVAL_TICKS)) return;
        List<EntityCreeper> creepers = player.worldObj.getEntitiesWithinAABB(
                EntityCreeper.class,
                AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                        player.posX + 1.0D, player.posY + 1.0D,
                        player.posZ + 1.0D).expand(ConfigHandler.OCELOT_RANGE, 4.0D, ConfigHandler.OCELOT_RANGE));

        for (EntityCreeper creeper : creepers)
        {
            boolean set = true;
            for (Object entry : creeper.tasks.taskEntries)
            {
                if (((EntityAITasks.EntityAITaskEntry) entry).action instanceof EntityAIAvoidPlayerWithBackpack)
                {
                    set = false;
                    break;
                }
            }
            if (set)
            {
                creeper.tasks.addTask(3, new EntityAIAvoidPlayerWithBackpack(creeper, EntityPlayer.class, 10.0F, 1.0, 1.3, "Ocelot"));
            }
        }
    }
}
