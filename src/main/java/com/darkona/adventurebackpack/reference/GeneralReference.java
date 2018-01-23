package com.darkona.adventurebackpack.reference;

import java.util.HashMap;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.LogHelper;

/**
 * Created on 16/01/2015
 *
 * @author Darkona
 */
public class GeneralReference
{
    public static HashMap<String, Float> liquidFuels = new HashMap<String, Float>();

    public static void init()
    {
        parseFuels();
    }

    private static void parseFuels()
    {
        for (String fuel : ConfigHandler.copterFuels)
        {
            String[] arrFuel = fuel
                    .replaceAll(" ", "")
                    .split(",");

            if (arrFuel.length == 2)
            {
                float rate;

                try
                {
                    rate = Float.parseFloat(arrFuel[1]);
                }
                catch (NumberFormatException e)
                {
                    rate = 100.0f;
                    LogHelper.error("Cannot parse consumption rate for " + arrFuel[0] +  ". Ignored.");
                }

                if ((rate >= 0.049f && rate <= 20.001f) && FluidRegistry.isFluidRegistered(arrFuel[0]))
                {
                    liquidFuels.put(arrFuel[0], rate);
                    LogHelper.info("Registered " + arrFuel[0] + " as Copter fuel with consumption rate " + rate);
                }
            }
        }
    }

    public static boolean isValidFuel(Fluid fluid)
    {
        if (fluid != null)
        {
            for (String fuel : liquidFuels.keySet())
            {
                if (fuel.equals(fluid.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
