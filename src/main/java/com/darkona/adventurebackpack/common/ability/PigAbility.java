package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityTimer;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PigAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.PIG_ENABLED || world.isRemote) return;
        long now = world.getTotalWorldTime();
        if (!TickThrottle.every(now, player.getEntityId(), AbilityTimer.SECOND_CHECK_TICKS)) return;
        long next = BackpackUtils.getTimeMark(backpack, BackpackAbilities.NEXT_ABILITY);
        if (next >= 0 && !AbilityTimer.due(now, next, Utils.secondsToTicks(ConfigHandler.PIG_OINK_MAX_SECONDS))) return;
        if (next >= 0) world.playSoundAtEntity(player, "mob.pig.say", 0.8f, 1f);
        BackpackUtils.setTimeMark(backpack, BackpackAbilities.NEXT_ABILITY, now + Utils.secondsToTicks(world.rand.nextInt(ConfigHandler.PIG_OINK_MAX_SECONDS + 1)));
    }
}
