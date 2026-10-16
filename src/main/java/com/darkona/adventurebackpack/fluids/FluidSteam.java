package com.darkona.adventurebackpack.fluids;

import com.darkona.adventurebackpack.client.Icons;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public class FluidSteam extends Fluid
{
    public FluidSteam()
    {
        super("steam");
        setGaseous(true);
        setDensity(-1000);
        setViscosity(500);
        setTemperature(373);
    }

    @Override
    public IIcon getStillIcon()
    {
        return Icons.steamStill;
    }

    @Override
    public IIcon getIcon()
    {
        return Icons.steamStill;
    }

    @Override
    public IIcon getFlowingIcon()
    {
        return Icons.steamStill;
    }
}
