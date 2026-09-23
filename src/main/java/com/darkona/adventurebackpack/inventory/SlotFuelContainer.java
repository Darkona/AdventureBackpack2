package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Container slot of the Flamethrower Pack: takes registered fluid containers of fuels on fuels.list (and empty
 * ones, to fill). On the server a change of the slot moves the fuel at once (one save); the client only shows it.
 */
public class SlotFuelContainer extends Slot
{
    private final boolean input;

    public SlotFuelContainer(IInventory inventory, int index, int x, int y, boolean input)
    {
        super(inventory, index, x, y);
        this.input = input;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return input && FuelContainers.isFuelContainer(stack);
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
