package com.darkona.adventurebackpack.common;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

public final class TankTagMemo
{
    private final ShownKey key = new ShownKey();
    private FluidStack fluid;
    int reads;

    public FluidStack fluid(NBTTagCompound root, String tankKey)
    {
        NBTBase tank = root == null ? null : root.getTag(tankKey);
        if (key.changed(tank, 0, null))
        {
            fluid = tank instanceof NBTTagCompound ? FluidStack.loadFluidStackFromNBT((NBTTagCompound) tank) : null;
            reads++;
        }
        return fluid;
    }
}
