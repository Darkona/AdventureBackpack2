package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotAirContainer extends Slot
{
    private final boolean input;

    public SlotAirContainer(IInventory inventory, int index, int x, int y, boolean input)
    {
        super(inventory, index, x, y);
        this.input = input;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return input && AirContainers.isAirContainer(stack);
    }

    @Override
    public void onSlotChanged()
    {
        if (input && Utils.inServer() && inventory instanceof IInventoryTanks)
        {
            ((IInventoryTanks) inventory).updateTankSlots();
        }
        super.onSlotChanged();
    }
}
