package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.TickThrottle;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class SunflowerAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.SUNFLOWER_ENABLED || world.isRemote) return;
        if (!TickThrottle.every(world.getTotalWorldTime(), player.getEntityId(), Utils.secondsToTicks(ConfigHandler.SUNFLOWER_INTERVAL_SECONDS))) return;
        if (world.isDaytime()
                && world.canBlockSeeTheSky(MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY + 1), MathHelper.floor_double(player.posZ)))
        {
            player.getFoodStats().addStats(ConfigHandler.SUNFLOWER_FOOD, ConfigHandler.SUNFLOWER_SATURATION);
        }
    }
}
