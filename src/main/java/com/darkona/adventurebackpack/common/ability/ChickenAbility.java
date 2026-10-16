package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityTimer;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ChickenAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.CHICKEN_ENABLED || world.isRemote) return;
        long now = world.getTotalWorldTime();
        if (!TickThrottle.every(now, player.getEntityId(), AbilityTimer.SECOND_CHECK_TICKS)) return;
        long next = BackpackUtils.getTimeMark(backpack, BackpackAbilities.NEXT_ABILITY);
        int longest = Utils.secondsToTicks(ConfigHandler.CHICKEN_EGG_MIN_SECONDS + ConfigHandler.CHICKEN_EGG_RANDOM_SECONDS);
        if (next >= 0 && !AbilityTimer.due(now, next, longest)) return;
        if (next >= 0)
        {
            world.playSoundAtEntity(player, "mob.chicken.plop", 1.0F, (world.rand.nextFloat() - world.rand.nextFloat()) * 0.3F + 1.0F);
            player.dropItem(Items.egg, 1);
        }
        BackpackUtils.setTimeMark(backpack, BackpackAbilities.NEXT_ABILITY, now + Utils.secondsToTicks(ConfigHandler.CHICKEN_EGG_MIN_SECONDS + world.rand.nextInt(ConfigHandler.CHICKEN_EGG_RANDOM_SECONDS + 1)));
    }
}
