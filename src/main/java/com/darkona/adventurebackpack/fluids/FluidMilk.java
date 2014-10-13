package com.darkona.adventurebackpack.fluids;

import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by Darkona on 12/10/2014.
 */
public class FluidMilk extends Fluid {

    public FluidMilk() {
        super("milk");
        setDensity(1200);
        setViscosity(1200);
        setUnlocalizedName("milk");
        setLuminosity(0);
    }

    @Override
    public IIcon getStillIcon() {
        return super.getStillIcon();
    }

    @Override
    public IIcon getIcon() {
        return super.getIcon();
    }

    @Override
    public IIcon getFlowingIcon() {
        return super.getFlowingIcon();
    }

    @Override
    public int getColor(FluidStack stack) {
        return 0xfffaf0;
    }

    @Override
    public boolean isGaseous(World world, int x, int y, int z) {
        return false;
    }
}