package com.darkona.adventurebackpack.reference;

import net.minecraftforge.fluids.Fluid;

import java.util.HashMap;

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
        liquidFuels.put("lava", 5.0f);
        liquidFuels.put("fuel", 1.0f);
        liquidFuels.put("oil", 3.0f);
        liquidFuels.put("bioethanol", 1.5f);
        liquidFuels.put("biofuel", 1.0f);
        liquidFuels.put("creosote", 7.0f);
        liquidFuels.put("coal", 2.0f);
        liquidFuels.put("nitrofuel", 0.5f);
    }

    public static boolean isValidFuel(Fluid fluid)
    {
        if(fluid != null)
        {
            for(String fuel : liquidFuels.keySet())
            {
                if(fuel.equals(fluid.getName()))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
