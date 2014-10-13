package com.darkona.adventurebackpack.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public class HoseSuckEvent extends FillBucketEvent {

    public FluidStack fluidResult;
    public final FluidTank currentTank;

    public HoseSuckEvent(EntityPlayer player, ItemStack current, World world, MovingObjectPosition target, FluidTank tank) {
        super(player, current, world, target);
        currentTank = tank;
    }

}
