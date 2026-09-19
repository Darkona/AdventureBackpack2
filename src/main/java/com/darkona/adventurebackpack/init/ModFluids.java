package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.fluids.FluidAir;
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

    /** The "steam" the jetpack boils: another mod's, ours, or null when there is none (see registerSteamIfMissing). */
    public static Fluid steam;

    /**
     * Called in postInit, once every mod had its chance to register "steam" (Railcraft, IC2...): only then is
     * Adventure Backpack's own registered, so it never replaces theirs.
     */
    public static void registerSteamIfMissing()
    {
        if (!FluidRegistry.isFluidRegistered("steam") && ConfigHandler.JETPACK_OWN_STEAM)
        {
            FluidRegistry.registerFluid(new FluidSteam());
            LogHelper.info("No other mod registers steam: using Adventure Backpack's own for the Steam Jetpack.");
        }
        steam = FluidRegistry.getFluid("steam");
    }

    /** The "air" the Scuba Gear breathes: GregTech's, ours, or null when there is none (see registerAirIfMissing). */
    public static Fluid air;

    /**
     * Called in postInit like registerSteamIfMissing: GregTech registers "air" (its Air Cells hold 2000 mB of it)
     * and then that one is used; otherwise Adventure Backpack's own, unless scuba.ownAirFluid is off.
     */
    public static void registerAirIfMissing()
    {
        if (!FluidRegistry.isFluidRegistered("air") && ConfigHandler.SCUBA_OWN_AIR)
        {
            FluidRegistry.registerFluid(new FluidAir());
            LogHelper.info("No other mod registers air: using Adventure Backpack's own for the Scuba Gear.");
        }
        air = FluidRegistry.getFluid("air");
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
