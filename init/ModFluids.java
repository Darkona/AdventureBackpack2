package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.fluids.FluidMelonJuice;
import com.darkona.adventurebackpack.fluids.FluidMilk;
import com.darkona.adventurebackpack.fluids.FluidMushroomStew;
import com.darkona.adventurebackpack.reference.GeneralReference;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created on 12/10/2014.
 *
 * @author Javier Darkona
 */
public class ModFluids
{
    public static FluidMilk milk;
    public static FluidMelonJuice melonJuice;
    public static FluidMushroomStew mushroomStew;
    public static void init()
    {
        milk = new FluidMilk();
        melonJuice = new FluidMelonJuice();
        mushroomStew = new FluidMushroomStew();

        FluidRegistry.registerFluid(milk);
        FluidContainerRegistry.registerFluidContainer(milk, new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);

        FluidRegistry.registerFluid(melonJuice);
        FluidContainerRegistry.registerFluidContainer(melonJuice, new ItemStack(ModItems.melonJuiceBottle), FluidContainerRegistry.EMPTY_BOTTLE);

        FluidRegistry.registerFluid(mushroomStew);
        FluidContainerRegistry.registerFluidContainer(mushroomStew, new ItemStack(Items.mushroom_stew), new ItemStack(Items.bowl));

        GeneralReference.init();
    }

}
