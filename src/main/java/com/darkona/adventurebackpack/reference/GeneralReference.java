package com.darkona.adventurebackpack.reference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.DimensionManager;
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
    private static HashMap<String, Float> liquidFuels = new HashMap<>();
    private static Set<Integer> dimensionBlacklist = new HashSet<>();

    private static final float MAX_RATE = 20.0f;
    private static final float MIN_RATE = 0.05f;

    public static void init()
    {
        parseLiquidFuelsConfig();
        parseDimensionBlacklistConfig();

        if (ConfigHandler.IS_DEVENV)
            liquidFuels.put("holywater", 0.0f); // shhh.. you did not see anything. hallelujah!
    }

    private static void parseDimensionBlacklistConfig()
    {
        for (String dim : ConfigHandler.forbiddenDimensions)
        {
            int dimID;
            try
            {
                dimID = Integer.parseInt(dim);
            }
            catch (NumberFormatException nfe)
            {
                LogHelper.error("Cannot parse Forbidden Dimension ID for " + dim + ". Ignored");
                continue;
            }

            if (DimensionManager.isDimensionRegistered(dimID))
            {
                dimensionBlacklist.add(dimID);
                LogHelper.info("Dimension " + dimID + " is registered as Forbidden for Adventure Backpack");
            }
            else
            {
                LogHelper.info("Not found dimension " + dimID + ". Skipped");
            }
        }
    }

    private static void parseLiquidFuelsConfig()
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

    public static boolean isDimensionAllowed(@Nonnull EntityPlayer player)
    {
        return isDimensionAllowed(player.worldObj.provider.dimensionId);
    }

    public static boolean isDimensionAllowed(int dimID)
    {
        return !dimensionBlacklist.contains(dimID);
    }

    public static boolean isValidFuel(String fluidName)
    {
        return liquidFuels.containsKey(fluidName);
    }

    @Nullable
    public static Float getFuelRate(String fluidName)
    {
        return liquidFuels.get(fluidName);
    }

}
