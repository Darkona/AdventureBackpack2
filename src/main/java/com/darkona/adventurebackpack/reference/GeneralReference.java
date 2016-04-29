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
        liquidFuels.put("water", 1.6f);
        liquidFuels.put("lava", 1.0f);

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
