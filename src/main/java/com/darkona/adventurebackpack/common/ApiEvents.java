package com.darkona.adventurebackpack.common;

import adventurebackpack.api.AdventureBackpackAPI;
import adventurebackpack.api.BackpackAbilityEvent;
import adventurebackpack.api.BackpackOpenEvent;
import adventurebackpack.api.FluidDrinkEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;

public final class ApiEvents
{
    private ApiEvents()
    {
    }

    public static boolean drinkCanceled(EntityPlayer player, World world, Fluid fluid, int amount)
    {
        if (world == null || world.isRemote) return false;
        return MinecraftForge.EVENT_BUS.post(new FluidDrinkEvent(player, world, fluid, amount));
    }

    public static boolean abilityCanceled(EntityPlayer player, World world, String colorName, ItemStack backpack)
    {
        if (!AdventureBackpackAPI.areAbilityEventsEnabled() || player == null || world == null || world.isRemote) return false;
        return MinecraftForge.EVENT_BUS.post(new BackpackAbilityEvent(player, world, colorName, backpack));
    }

    public static boolean openCanceled(EntityPlayer player, World world, BackpackOpenEvent.Source source, ItemStack backpack, int x, int y, int z)
    {
        if (world == null || world.isRemote) return false;
        return MinecraftForge.EVENT_BUS.post(new BackpackOpenEvent(player, world, source, backpack, x, y, z));
    }
}
