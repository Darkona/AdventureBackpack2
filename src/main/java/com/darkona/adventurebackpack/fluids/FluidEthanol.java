package com.darkona.adventurebackpack.fluids;

import com.darkona.adventurebackpack.client.Icons;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;

public class FluidEthanol extends Fluid
{
    public static final String NAME = "bioethanol";

    public FluidEthanol()
    {
        super(NAME);
        setDensity(790);
        setViscosity(1200);
    }

    @Override
    public IIcon getStillIcon()
    {
        return Icons.ethanolStill;
    }

    @Override
    public IIcon getIcon()
    {
        return Icons.ethanolStill;
    }

    @Override
    public IIcon getFlowingIcon()
    {
        return Icons.ethanolStill;
    }
}
