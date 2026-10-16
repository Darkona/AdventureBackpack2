package com.darkona.adventurebackpack.fluids;

import com.darkona.adventurebackpack.client.Icons;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidMushroomStew extends Fluid
{
    public FluidMushroomStew()
    {
        super("mushroomstew");
        setDensity(1200);
        setViscosity(1200);
        setLuminosity(0);
    }

    @Override
    public IIcon getStillIcon()
    {
        return Icons.mushRoomStewStill;
    }

    @Override
    public IIcon getIcon()
    {
        return Icons.mushRoomStewStill;
    }

    @Override
    public IIcon getFlowingIcon()
    {
        return Icons.mushRoomStewFlowing;
    }

    @Override
    public int getColor(FluidStack stack)
    {
        return 0xcd8c6f;
    }

    @Override
    public boolean isGaseous(World world, int x, int y, int z)
    {
        return false;
    }
}
