package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.fluids.FluidAir;
import com.darkona.adventurebackpack.fluids.FluidEthanol;
import com.darkona.adventurebackpack.fluids.FluidMelonJuice;
import com.darkona.adventurebackpack.fluids.FluidMilk;
import com.darkona.adventurebackpack.fluids.FluidMushroomStew;
import com.darkona.adventurebackpack.fluids.FluidSteam;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.GeneralReference;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class ModFluids
{
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

    public static Fluid steam;

    public static void registerSteamIfMissing()
    {
        if (!FluidRegistry.isFluidRegistered("steam") && ConfigHandler.JETPACK_OWN_STEAM)
        {
            FluidRegistry.registerFluid(new FluidSteam());
            LogHelper.info("No other mod registers steam: using Adventure Backpack's own for the Steam Jetpack.");
        }
        steam = FluidRegistry.getFluid("steam");
    }

    public static Fluid air;

    public static void registerAirIfMissing()
    {
        if (!FluidRegistry.isFluidRegistered("air") && ConfigHandler.SCUBA_OWN_AIR)
        {
            FluidRegistry.registerFluid(new FluidAir());
            LogHelper.info("No other mod registers air: using Adventure Backpack's own for the Scuba Gear.");
        }
        air = FluidRegistry.getFluid("air");
    }

    public static Fluid ethanol;

    public static void registerEthanolIfMissing()
    {
        if (!FluidRegistry.isFluidRegistered(FluidEthanol.NAME) && ConfigHandler.DISTILLERY_ENABLED && ConfigHandler.DISTILLERY_OWN_ETHANOL)
        {
            FluidRegistry.registerFluid(new FluidEthanol());
            LogHelper.info("No other mod registers bioethanol: using Adventure Backpack's own for the Distillery.");
        }
        String wanted = ConfigHandler.DISTILLERY_OUTPUT_FLUID;
        ethanol = FluidRegistry.getFluid(wanted);
        if (ethanol == null && !FluidEthanol.NAME.equals(wanted))
        {
            LogHelper.warn("distillery.outputFluid '" + wanted + "' is not a registered fluid; the Distillery makes bioethanol.");
            ethanol = FluidRegistry.getFluid(FluidEthanol.NAME);
        }
    }

    static Fluid registerOrReuse(Fluid ours)
    {
        if (FluidRegistry.registerFluid(ours)) return ours;
        Fluid theirs = FluidRegistry.getFluid(ours.getName());
        LogHelper.info("Fluid '" + ours.getName() + "' is already registered by another mod; using that one.");
        return theirs != null ? theirs : ours;
    }
}
