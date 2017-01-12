package com.darkona.adventurebackpack.reference;

import java.util.HashMap;

import com.darkona.adventurebackpack.config.ConfigHandler;

import net.minecraftforge.fluids.Fluid;

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
        liquidFuels.put("biofuel", ConfigHandler.fuelRateBioFuel);
        liquidFuels.put("bioethanol", ConfigHandler.fuelRateBioEthanol);
        liquidFuels.put("coal", ConfigHandler.fuelRateCoal);
        liquidFuels.put("creosote", ConfigHandler.fuelRateCreosote);
        liquidFuels.put("fuel", ConfigHandler.fuelRateFuel);
        liquidFuels.put("lava", ConfigHandler.fuelRateLava);
        liquidFuels.put("nitrofuel", ConfigHandler.fuelRateNitroFuel);
        liquidFuels.put("oil", ConfigHandler.fuelRateOil);
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
