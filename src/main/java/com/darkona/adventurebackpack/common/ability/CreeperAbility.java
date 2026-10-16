package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityTimer;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.List;

public class CreeperAbility implements IBackpackAbility
{
    @Override
    @SuppressWarnings("unchecked")
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.CREEPER_ENABLED || world.isRemote || !player.isSneaking()) return;
        long now = world.getTotalWorldTime();
        if (!TickThrottle.every(now, player.getEntityId(), ConfigHandler.CREEPER_CHECK_INTERVAL_TICKS)) return;
        int cooldown = Utils.secondsToTicks(ConfigHandler.CREEPER_SCARE_COOLDOWN_SECONDS);
        if (!AbilityTimer.due(now, BackpackUtils.getTimeMark(backpack, BackpackAbilities.NEXT_ABILITY), cooldown)) return;
        List<Entity> entities = player.worldObj.getEntitiesWithinAABBExcludingEntity(player,
                AxisAlignedBB.getBoundingBox(player.posX, player.posY, player.posZ,
                        player.posX + 1.0D, player.posY + 1.0D,
                        player.posZ + 1.0D).expand(5.0D, 1.0D, 5.0D));
        for (Entity entity : entities)
        {
            if (entity instanceof EntityPlayer && player.getDistanceToEntity(entity) <= ConfigHandler.CREEPER_SCARE_RANGE)
            {
                world.playSoundAtEntity(player, "creeper.primed", 1.2F, 0.5F);
                BackpackUtils.setTimeMark(backpack, BackpackAbilities.NEXT_ABILITY, now + cooldown);
                return;
            }
        }
    }
}
