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
        liquidFuels.put("liquid_light_oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("liquid_medium_oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("liquid_heavy_oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("liquid_light_fuel", ConfigHandler.fuelRateFuelLight);
        liquidFuels.put("liquid_heavy_fuel", ConfigHandler.fuelRateFuelLight);
        liquidFuels.put("nitrofuel", ConfigHandler.fuelRateFuelNitro);
        liquidFuels.put("oil", ConfigHandler.fuelRateOil);
        liquidFuels.put("rocket_fuel", ConfigHandler.fuelRateFuel);

        //liquidFuels.put("liquid_sulfuriclight_fuel", ConfigHandler.fuelRateFuel);
        //liquidFuels.put("liquid_cracked_light_fuel", ConfigHandler.fuelRateFuel);
        //liquidFuels.put("liquid_sulfuricheavy_fuel", ConfigHandler.fuelRateFuel);
        //liquidFuels.put("liquid_cracked_heavy_fuel", ConfigHandler.fuelRateFuel);
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
