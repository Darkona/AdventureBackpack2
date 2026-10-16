package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BatAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.BAT_ENABLED) AbilityHelper.giveNightVision(player);
    }

    @Override
    public void onRemoval(EntityPlayer player, World world, ItemStack backpack)
    {
        AbilityHelper.removeNightVision(player);
    }
}
