package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;

public class CactusAbility implements IBackpackAbility
{
    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (ConfigHandler.CACTUS_ENABLED) AbilityHelper.fillWhenWet(player, world, backpack, FluidRegistry.WATER,
                ConfigHandler.CACTUS_WORN_INTERVAL_TICKS, ConfigHandler.CACTUS_WATER_IN_WATER, ConfigHandler.CACTUS_WATER_IN_RAIN);
    }

    @Override
    public void onPlacedTick(World world, TileEntity backpackTile)
    {
        if (ConfigHandler.CACTUS_ENABLED) AbilityHelper.fillWithRain(world, (TileAdventureBackpack) backpackTile, FluidRegistry.WATER,
                ConfigHandler.CACTUS_PLACED_RAIN_AMOUNT, ConfigHandler.CACTUS_PLACED_INTERVAL_TICKS);
    }
}
