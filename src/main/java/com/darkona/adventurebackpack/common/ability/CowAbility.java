package com.darkona.adventurebackpack.common.ability;

import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class CowAbility implements IBackpackAbility
{
    private FluidStack milkStack;

    @Override
    public void onWornTick(EntityPlayer player, World world, ItemStack backpack)
    {
        if (!ConfigHandler.COW_ENABLED) return;
        if (milkStack == null) milkStack = new FluidStack(FluidRegistry.getFluid("milk"), 1);
        AbilityHelper.makeFromWheat(player, world, backpack, milkStack,
                ConfigHandler.COW_EAT_INTERVAL_TICKS, ConfigHandler.COW_WHEAT_PER_BATCH, ConfigHandler.COW_MILK_PER_BATCH);
    }
}
