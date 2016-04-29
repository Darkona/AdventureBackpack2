package com.darkona.adventurebackpack.fluids;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 17/01/2015
 *
 * @author Darkona
 */
public class TankWrapper extends FluidTank
{
    public TankWrapper(int capacity)
    {
        super(capacity);
    }

    public TankWrapper(FluidStack stack, int capacity)
    {
        super(stack, capacity);
    }

    public TankWrapper(Fluid fluid, int amount, int capacity)
    {
        super(fluid, amount, capacity);
    }

    public boolean isFull()
    {
        return this.fluid != null && this.getFluidAmount() == this.capacity;
    }

    public boolean isEmpty()
    {
        return this.fluid == null || (this.getFluidAmount() == this.capacity);
    }

}
