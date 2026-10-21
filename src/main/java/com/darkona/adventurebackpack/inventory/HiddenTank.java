package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.QuiverRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public final class HiddenTank
{
    private HiddenTank()
    {
    }

    public static boolean seal(FluidTank left, FluidTank right, boolean shown)
    {
        if (shown)
        {
            right.setCapacity(ConfigHandler.BACKPACK_TANK_CAPACITY);
            return false;
        }
        boolean movedAny = false;
        FluidStack r = right.getFluid();
        if (r != null && r.amount > 0)
        {
            FluidStack l = left.getFluid();
            boolean leftEmpty = l == null || l.amount <= 0;
            int moved = QuiverRules.movedToLeft(leftEmpty, !leftEmpty && l.isFluidEqual(r), leftEmpty ? 0 : l.amount, r.amount, left.getCapacity());
            if (moved > 0)
            {
                if (leftEmpty)
                {
                    FluidStack into = r.copy();
                    into.amount = moved;
                    left.setFluid(into);
                } else
                {
                    l.amount += moved;
                }
                r.amount -= moved;
                if (r.amount <= 0) right.setFluid(null);
                movedAny = true;
            }
        }
        FluidStack rest = right.getFluid();
        right.setCapacity(rest == null ? 0 : rest.amount);
        return movedAny;
    }
}
