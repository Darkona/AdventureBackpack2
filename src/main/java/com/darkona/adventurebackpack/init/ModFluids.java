package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.api.FluidEffectRegistry;
import com.darkona.adventurebackpack.fluids.FluidMelonJuice;
import com.darkona.adventurebackpack.fluids.FluidMilk;
import com.darkona.adventurebackpack.fluids.effects.MelonJuiceEffect;
import com.darkona.adventurebackpack.fluids.effects.MilkEffect;
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
    public static FluidMelonJuice melonJuice;

    public static void init()
    {
        milk = new FluidMilk();
        melonJuice = new FluidMelonJuice();
        FluidRegistry.registerFluid(milk);
        FluidRegistry.registerFluid(melonJuice);

        FluidContainerRegistry.registerFluidContainer(milk, new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);
        FluidContainerRegistry.registerFluidContainer(melonJuice, new ItemStack(ModItems.melonJuiceBottle), FluidContainerRegistry.EMPTY_BOTTLE);
    }

}
