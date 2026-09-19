package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container slot of the Scuba Gear and the Air Compressor: takes fluid containers of breathable fluids and the items
 * of scuba.airItems. On the server a change of the slot moves the air at once (one save); the client only shows it.
 */
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
