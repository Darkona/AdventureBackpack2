package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;

/**
 * Created on 12/10/2014
 * @author Darkona
 * 
 *
 */
public class SlotFluid extends SlotAdventureBackpack
{

    public SlotFluid(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    public static boolean valid(ItemStack stack)
    {
        return ((stack != null) && (FluidContainerRegistry.isContainer(stack)));
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return ((stack != null) && (FluidContainerRegistry.isContainer(stack)));
    }

    @Override
    public void onSlotChanged()
    {

        if (Utils.inServer())
        {
            if (inventory instanceof IInventoryTanks)
            {
                ((IInventoryTanks) this.inventory).updateTankSlots();
            }
        }
        super.onSlotChanged();
    }

    @Override
    public void putStack(ItemStack par1ItemStack)
    {
        super.putStack(par1ItemStack);
    }
}
