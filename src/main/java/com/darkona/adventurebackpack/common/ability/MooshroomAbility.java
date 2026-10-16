package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class MooshroomAbility implements IBackpackAbility
{
    private FluidStack soupStack;

    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.MOOSHROOM_ENABLED) return;
        if (soupStack == null) soupStack = new FluidStack(FluidRegistry.getFluid("mushroomstew"), 1);
        AbilityHelper.makeFromWheat(player, world, backpack, soupStack,
                ConfigHandler.MOOSHROOM_EAT_INTERVAL_TICKS, ConfigHandler.MOOSHROOM_WHEAT_PER_BATCH, ConfigHandler.MOOSHROOM_STEW_PER_BATCH);
    }
}
