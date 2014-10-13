package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.fluids.FluidMilk;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.item.*;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created by Darkona on 12/10/2014.
 */
public class ModFluids {
    public static Fluid milk;

    public static void init() {

        milk = new FluidMilk();
        milk.setUnlocalizedName("milk");
        FluidContainerRegistry.registerFluidContainer(milk, new ItemStack(new ItemBucketMilk(), 1), new ItemStack(new ItemBucket(null), 1));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.WATER, new ItemStack(new ItemPotion(), 1), new ItemStack(new ItemGlassBottle(), 1));
    }
}
