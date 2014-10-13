package com.darkona.adventurebackpack.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public class HoseSpillEvent extends FluidEvent.FluidSpilledEvent {

    public final FluidTank currentTank;
    public final EntityPlayer player;
    public FluidStack fluidResult;

    public HoseSpillEvent(EntityPlayer theNoob, World world, int x, int y, int z, FluidTank tank) {
        super(tank.getFluid(), world, x, y, z);
        currentTank = tank;
        player = theNoob;
    }
}
