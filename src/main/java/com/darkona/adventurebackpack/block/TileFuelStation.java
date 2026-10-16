package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.config.ConfigHandler;
import adventurebackpack.api.IFuelPack;
import com.darkona.adventurebackpack.reference.GeneralReference;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class TileFuelStation extends TileStation
{
    public TileFuelStation()
    {
        super(ConfigHandler.FUELSTATION_TANK_CAPACITY);
    }

    @Override
    protected boolean accepts(Fluid fluid)
    {
        return GeneralReference.isValidFuel(fluid);
    }

    @Override
    public boolean enabled()
    {
        return ConfigHandler.FUELSTATION_ENABLED;
    }

    @Override
    protected boolean isPack(ItemStack stack)
    {
        return stack != null && stack.getItem() instanceof IFuelPack;
    }

    @Override
    protected int fillPack(ItemStack pack, FluidStack offered, boolean doFill)
    {
        if (!isPack(pack)) return 0;
        return ((IFuelPack) pack.getItem()).fillFuel(pack, offered, doFill);
    }

    @Override
    public String getInventoryName()
    {
        return "container.adventurebackpack:fuelStation";
    }
}
