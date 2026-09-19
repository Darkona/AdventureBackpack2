package com.darkona.adventurebackpack.fluids;

import com.darkona.adventurebackpack.client.Icons;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

/**
 * Compressed air for the Scuba Gear and the Air Compressor, registered only when no other mod (GregTech) registers
 * an "air" fluid (see ModFluids.registerAirIfMissing). It has no block: it only lives in tanks.
 */
public class FluidAir extends Fluid
{
    public FluidAir()
    {
        super("air");
        setGaseous(true);
        setDensity(-10);
        setViscosity(100);
        setTemperature(295);
    }

    @Override
    public IIcon getStillIcon()
    {
        return Icons.airStill;
    }

    @Override
    public IIcon getIcon()
    {
        return Icons.airStill;
    }

    @Override
    public IIcon getFlowingIcon()
    {
        return Icons.airStill;
    }
}
