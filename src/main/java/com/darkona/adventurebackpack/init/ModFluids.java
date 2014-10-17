package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.fluids.FluidMilk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ModFluids
{
    public static FluidMilk milk;

    public static void init()
    {
        milk = new FluidMilk();
        FluidContainerRegistry.registerFluidContainer(milk, new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);
    }
}
