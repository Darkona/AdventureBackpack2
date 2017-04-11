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
        liquidFuels.put("biofuel", ConfigHandler.fuelRateBioFuel); // ?
        liquidFuels.put("bioethanol", ConfigHandler.fuelRateBioEthanol); // ic2 ethanol
        liquidFuels.put("creosote", ConfigHandler.fuelRateCreosote);
        liquidFuels.put("fuel", ConfigHandler.fuelRateFuel);
        liquidFuels.put("lava", ConfigHandler.fuelRateLava);
        liquidFuels.put("liquid_light_oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("liquid_medium_oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("liquid_heavy_oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("liquid_light_fuel", ConfigHandler.fuelRateFuelLight);
        liquidFuels.put("liquid_heavy_fuel", ConfigHandler.fuelRateFuelHeavy);
        liquidFuels.put("nitrofuel", ConfigHandler.fuelRateFuelNitro);
        liquidFuels.put("oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("rocket_fuel", ConfigHandler.fuelRateFuel);
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
