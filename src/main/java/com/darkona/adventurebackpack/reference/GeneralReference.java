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
    public static HashMap<String, Float> liquidFuels = new HashMap<>();

    private static final float MAX_RATE = 20.0f;
    private static final float MIN_RATE = 0.05f;

    public static void init()
    {
        parseFuelConfig();

        if (ConfigHandler.IS_DEVENV)
            liquidFuels.put("holywater", 0.0f); // shhh.. you did not see anything. hallelujah!
    }

    private static void parseFuelConfig()
    {
        int wrongCount = 0;
        int unregCount = 0;

        for (String fuel : ConfigHandler.copterFuels)
        {
            String[] arrFuel = fuel
                    .replaceAll(" ", "")
                    .split(",");

            if (arrFuel.length != 2)
            {
                wrongCount++;
                continue;
            }

            String fluid = arrFuel[0];
            float rate;

            if (FluidRegistry.isFluidRegistered(fluid))
            {
                try
                {
                    rate = Float.parseFloat(arrFuel[1]);
                }
                catch (NumberFormatException nfe)
                {
                    rate = MAX_RATE;
                    LogHelper.error("Cannot parse consumption rate for " + fluid + ". Setting MAX rate");
                }
                rate = rate < MIN_RATE ? MIN_RATE : (rate > MAX_RATE ? MAX_RATE : rate);
                liquidFuels.put(fluid, rate);
                LogHelper.info("Registered " + fluid + " as Copter fuel with consumption rate " + rate);
            }
            else
            {
                unregCount++;
            }
        }

        if (wrongCount > 0 || unregCount > 0)
        {
            LogHelper.info("Skipped "
                    + (wrongCount > 0 ? (wrongCount + " incorrect entr" + (wrongCount > 1 ? "ies" : "y")) : "")
                    + (wrongCount > 0 && unregCount > 0 ? " and " : "")
                    + (unregCount > 0 ? (unregCount + " unregistered fluid" + (unregCount > 1 ? "s" : "")) : ""));
        }
    }

    public static boolean isValidFuel(Fluid fluid)
    {
        if (fluid != null)
        {
            for (String fuel : liquidFuels.keySet())
            {
                if (fuel.equals(fluid.getName()))
                    return true;
            }
        }
        return false;
    }
}
