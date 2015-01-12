package com.darkona.adventurebackpack.fluids;

import com.darkona.adventurebackpack.client.Icons;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created on 11/01/2015
 *
 * @author Darkona
 */
public class FluidMushroomStew extends Fluid
{
    public FluidMushroomStew()
    {
        super("mushroomStew");
        setDensity(1200);
        setViscosity(1200);
        setUnlocalizedName("mushroomStew");
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
        //Color color1 = new Color(205,140,111);
        return 0xcd8c6f;
    }

    @Override
    public boolean isGaseous(World world, int x, int y, int z)
    {
        return false;
    }
}
