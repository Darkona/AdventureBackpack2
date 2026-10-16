package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModFluids;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MelonAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.MELON_ENABLED) AbilityHelper.fillWhenWet(player, world, backpack, ModFluids.melonJuice,
                ConfigHandler.MELON_WORN_INTERVAL_TICKS, ConfigHandler.MELON_JUICE_IN_WATER, ConfigHandler.MELON_JUICE_IN_RAIN);
    }

    @Override
    public void onPlacedTick(World world, TileEntity backpackTile)
    {
        if (ConfigHandler.MELON_ENABLED) AbilityHelper.fillWithRain(world, (TileAdventureBackpack) backpackTile, ModFluids.melonJuice,
                ConfigHandler.MELON_PLACED_RAIN_AMOUNT, ConfigHandler.MELON_PLACED_INTERVAL_TICKS);
    }
}
