package com.darkona.adventurebackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;

import java.util.HashMap;

/**
 * Created on 17/10/2014
 *
 * @author Darkona
 */
public class FluidUtils
{

    public static HashMap<String, Float> fuelValues = new HashMap<String, Float>();

    public static void init()
    {
        fuelValues.put("fuel",1.0f);
        fuelValues.put("oil",1.5f);
        fuelValues.put("bioethanol",1.2f);
        fuelValues.put("biofuel",1.0f);
        fuelValues.put("creosote",1.8f);
        fuelValues.put("coal",0.8f);
    }

    public static boolean isContainerForFluid(ItemStack container, Fluid fluid)
    {
        for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData())
        {
            if (
                    (data.fluid.getFluid().getID() == fluid.getID()) &&
                            (data.emptyContainer.getItem().equals(container.getItem()) || data.filledContainer.getItem().equals(container.getItem()))
                    )
            {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmptyContainerForFluid(ItemStack container, Fluid fluid)
    {
        for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData())
        {
            if (
                    (data.fluid.getFluid().getID() == fluid.getID()) &&
                    (data.emptyContainer.getItem().equals(container.getItem()))
                    )
            {
                return true;
            }
        }
        return false;
    }
    public static boolean isValidFuel(Fluid fluid)
    {
        if(fluid != null)
        {
            for(String fuel : fuelValues.keySet())
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
