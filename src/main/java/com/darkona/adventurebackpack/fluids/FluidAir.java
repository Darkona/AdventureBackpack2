package com.darkona.adventurebackpack.fluids;

import com.darkona.adventurebackpack.client.Icons;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

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
