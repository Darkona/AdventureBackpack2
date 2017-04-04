package com.darkona.adventurebackpack.reference;

import java.util.HashMap;

import net.minecraftforge.fluids.Fluid;

import com.darkona.adventurebackpack.config.ConfigHandler;

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
        liquidFuels.put("coal", ConfigHandler.fuelRateCoal);  //liquid coal? o_O
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
