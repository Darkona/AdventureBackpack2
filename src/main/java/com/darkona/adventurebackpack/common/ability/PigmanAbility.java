package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PigmanAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.PIGMAN_ENABLED) AbilityHelper.giveFireResistance(player, ConfigHandler.PIGMAN_FIRE_RESISTANCE_TICKS);
    }

    @Override
    public void onRemoval(EntityPlayer player, World world, ItemStack backpack)
    {
        AbilityHelper.removeMarkedFireResistance(player);
    }
}
