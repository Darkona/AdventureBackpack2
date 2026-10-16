package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.TileStation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFuelStation extends Container
{
    private static final int PLAYER_SLOTS = 36;
    private static final int PROP_FLUID = 0;
    private static final int PROP_AMOUNT_LOW = 1;
    private static final int PROP_AMOUNT_HIGH = 2;

    private final TileStation station;
    private int lastFluid = -1;
    private int lastAmount = -1;
    private int clientFluid;
    private int clientAmountLow;
    private int clientAmountHigh;

    public ContainerFuelStation(EntityPlayer player, TileStation station)
    {
        this.station = station;
        bindPlayerInventory(player.inventory);
        addSlotToContainer(new StationSlot(station, TileStation.SLOT_IN, 44, 23, 64));
        addSlotToContainer(new StationSlot(station, TileStation.SLOT_OUT, 44, 53, 64));
        addSlotToContainer(new StationSlot(station, TileStation.SLOT_PACK, 152, 53, 1));
    }

    public TileStation getStation()
    {
        return station;
    }

    private void bindPlayerInventory(InventoryPlayer inv)
    {
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(inv, x, 8 + 18 * x, 142));
        }
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(inv, x + y * 9 + 9, 8 + 18 * x, 84 + y * 18));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return station.isUseableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        int fluid = station.syncedFluidId();
        int amount = fluid == 0 ? 0 : station.getTank().getFluidAmount();
        if (fluid == lastFluid && amount == lastAmount) return;
        for (Object o : crafters)
        {
            ICrafting crafter = (ICrafting) o;
            crafter.sendProgressBarUpdate(this, PROP_FLUID, fluid);
            crafter.sendProgressBarUpdate(this, PROP_AMOUNT_LOW, amount & 0x7FFF);
            crafter.sendProgressBarUpdate(this, PROP_AMOUNT_HIGH, amount >>> 15);
        }
        lastFluid = fluid;
        lastAmount = amount;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter)
    {
        super.addCraftingToCrafters(crafter);
        lastFluid = -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        if (id == PROP_FLUID) clientFluid = value;
        else if (id == PROP_AMOUNT_LOW) clientAmountLow = value & 0x7FFF;
        else if (id == PROP_AMOUNT_HIGH) clientAmountHigh = value;
        else return;
        station.setClientTank(clientFluid, (clientAmountHigh << 15) | clientAmountLow);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        Slot slot = index >= 0 && index < inventorySlots.size() ? (Slot) inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();
        if (index >= PLAYER_SLOTS)
        {
            if (!mergeItemStack(stack, 0, PLAYER_SLOTS, true)) return null;
        } else if (station.isItemValidForSlot(TileStation.SLOT_PACK, stack))
        {
            if (!mergeItemStack(stack, PLAYER_SLOTS + TileStation.SLOT_PACK, PLAYER_SLOTS + TileStation.SLOT_PACK + 1, false)) return null;
        } else if (station.isItemValidForSlot(TileStation.SLOT_IN, stack))
        {
            if (!mergeItemStack(stack, PLAYER_SLOTS + TileStation.SLOT_IN, PLAYER_SLOTS + TileStation.SLOT_IN + 1, false)) return null;
        } else
        {
            return null;
        }
        if (stack.stackSize == 0) slot.putStack(null);
        else slot.onSlotChanged();
        if (stack.stackSize == result.stackSize) return null;
        slot.onPickupFromSlot(player, stack);
        return result;
    }

    private static class StationSlot extends Slot
    {
        private final int limit;

        StationSlot(TileStation station, int index, int x, int y, int limit)
        {
            super(station, index, x, y);
            this.limit = limit;
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return inventory.isItemValidForSlot(getSlotIndex(), stack);
        }

        @Override
        public int getSlotStackLimit()
        {
            return limit;
        }
    }
}
