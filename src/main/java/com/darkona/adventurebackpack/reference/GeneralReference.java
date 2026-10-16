package com.darkona.adventurebackpack.reference;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.darkona.adventurebackpack.common.CodeRegistrations;
import com.darkona.adventurebackpack.common.FuelRules;
import com.darkona.adventurebackpack.config.ConfigHandler;

import gnu.trove.map.hash.TObjectFloatHashMap;

public class GeneralReference
{
    public static final TObjectFloatHashMap<String> liquidFuels = new TObjectFloatHashMap<String>();
    public static boolean fuelRegistered = false;

    public static void findRegisteredFuels()
    {
        fuelRegistered = FuelRules.anyRegistered(liquidFuels.keySet(), FluidRegistry.getRegisteredFluids().keySet());
    }

    public static void init()
    {
        liquidFuels.clear();
        liquidFuels.putAll(CodeRegistrations.mergeFuels(FuelRules.parse(ConfigHandler.FUELS), CodeRegistrations.INSTANCE.fuels()));
        if (ConfigHandler.DISTILLERY_ENABLED)
        {
            FuelRules.addIfMissing(liquidFuels, ConfigHandler.DISTILLERY_OUTPUT_FLUID, (float) ConfigHandler.DISTILLERY_FUEL_MULTIPLIER);
        }
    }

    public static boolean isValidFuel(Fluid fluid)
    {
        return fluid != null && FuelRules.accepts(liquidFuels, fluid.getName());
    }

    public static boolean isValidFuel(FluidStack stack)
    {
        return stack != null && stack.amount > 0 && isValidFuel(stack.getFluid());
    }

    public static float fuelMultiplier(Fluid fluid)
    {
        return FuelRules.multiplier(liquidFuels, fluid == null ? null : fluid.getName());
    }
}
