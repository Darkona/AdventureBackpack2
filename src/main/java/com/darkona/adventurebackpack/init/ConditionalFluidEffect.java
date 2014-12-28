package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.api.FluidEffect;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.fluids.effects.FuelEffect;
import com.darkona.adventurebackpack.fluids.effects.OilEffect;

/**
 * Created on 28/12/2014
 *
 * @author Darkona
 */
public class ConditionalFluidEffect
{
    public static FluidEffect oilEffect;
    public static FluidEffect fuelEffect;
    public static void init()
    {
        if(ConfigHandler.IS_BUILDCRAFT)
        {
            oilEffect = new OilEffect();
            fuelEffect = new FuelEffect();
        }
    }
}
