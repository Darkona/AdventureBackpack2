package com.darkona.adventurebackpack.reference;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import com.darkona.adventurebackpack.common.FuelRules;
import com.darkona.adventurebackpack.config.ConfigHandler;

import java.util.HashMap;

/**
 * Created on 16/01/2015
 *
 * @author Darkona
 */
public class GeneralReference
{
    /** The one fuel table (fuels.list): fluid name to cost multiplier. Every fuel user reads it. */
    public static HashMap<String, Float> liquidFuels = new HashMap<String, Float>();
    /** True when some loaded mod registers a fluid of the list; set once in postInit by {@link #findRegisteredFuels()}. */
    public static boolean fuelRegistered = false;

    /** Called once in postInit, when every mod has registered its fluids. */
    public static void findRegisteredFuels()
    {
        fuelRegistered = FuelRules.anyRegistered(liquidFuels.keySet(), FluidRegistry.getRegisteredFluids().keySet());
    }


    /** Liquid fuels and their cost multipliers, from the fuels.list config key. */
    public static void init()
    {
        liquidFuels.clear();
        liquidFuels.putAll(FuelRules.parse(ConfigHandler.FUELS));
    }

    public static boolean isValidFuel(Fluid fluid)
    {
        return fluid != null && FuelRules.accepts(liquidFuels, fluid.getName());
    }

    /** True for a stack of a fuel on the list; false for null, an empty stack or a fluid that is gone. */
    public static boolean isValidFuel(FluidStack stack)
    {
        return stack != null && stack.amount > 0 && isValidFuel(stack.getFluid());
    }

    /** Cost multiplier of the fuel (1 when it is not on the list). */
    public static float fuelMultiplier(Fluid fluid)
    {
        return FuelRules.multiplier(liquidFuels, fluid == null ? null : fluid.getName());
    }
}
