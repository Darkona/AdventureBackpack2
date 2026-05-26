package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.fluids.FluidMelonJuice;
import com.darkona.adventurebackpack.fluids.FluidMilk;
import com.darkona.adventurebackpack.fluids.FluidMushroomStew;
import com.darkona.adventurebackpack.reference.GeneralReference;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

/**
 * Created on 12/10/2014.
 *
 * @author Javier Darkona
 */
public class ModFluids
{
    /**
     * The fluid in use for each name: ours, or the one another mod registered first under the same name (Forge keeps
     * only the first). A FluidStack of an unregistered Fluid throws, so code must use these fields, never "new".
     */
    public static Fluid milk;
    public static Fluid melonJuice;
    public static Fluid mushroomStew;
    public static void init()
    {
        milk = registerOrReuse(new FluidMilk());
        melonJuice = registerOrReuse(new FluidMelonJuice());
        mushroomStew = registerOrReuse(new FluidMushroomStew());

        FluidContainerRegistry.registerFluidContainer(milk, new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);

        FluidContainerRegistry.registerFluidContainer(melonJuice, new ItemStack(ModItems.melonJuiceBottle), FluidContainerRegistry.EMPTY_BOTTLE);

        FluidContainerRegistry.registerFluidContainer(mushroomStew, new ItemStack(Items.mushroom_stew), new ItemStack(Items.bowl));

        GeneralReference.init();
    }

    /** Registers our fluid, or returns the fluid another mod already registered under that name. */
    static Fluid registerOrReuse(Fluid ours)
    {
        if (FluidRegistry.registerFluid(ours)) return ours;
        Fluid theirs = FluidRegistry.getFluid(ours.getName());
        LogHelper.info("Fluid '" + ours.getName() + "' is already registered by another mod; using that one.");
        return theirs != null ? theirs : ours;
    }

}
