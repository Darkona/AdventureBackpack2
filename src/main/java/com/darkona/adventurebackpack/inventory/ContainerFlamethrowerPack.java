package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IWearableContainer;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.block.TileGear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerFlamethrowerPack extends Container implements IWearableContainer
{
    private TileGear tile;
    private static final int PLAYER_SLOTS = 36;

    public final InventoryFlamethrowerPack inventory;
    private final boolean wearing;

    public ContainerFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, TileGear tile)
    {
        this(player, inventory, false);
        this.tile = tile;
    }

    public ContainerFlamethrowerPack(EntityPlayer player, InventoryFlamethrowerPack inventory, boolean wearing)
    {
        this.inventory = inventory;
        this.wearing = wearing;
        InventoryPlayer invPlayer = player.inventory;
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 142));
        }
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 84 + y * 18));
            }
        }
        addSlotToContainer(new SlotFuelContainer(inventory, InventoryFlamethrowerPack.SLOT_IN, 30, 22, true));
        addSlotToContainer(new SlotFuelContainer(inventory, InventoryFlamethrowerPack.SLOT_OUT, 30, 52, false));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        if (tile != null) return tile.isUseableByPlayer(player);
        return HeldContainerGuard.stillOpen(wearing, inventory.getParentItemStack(),
                BackpackProperty.get(player).getWearable(), player.getCurrentEquippedItem());
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (tile != null) tile.afterContainerTick();
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if (wearing)
        {
            this.crafters.remove(player);
        }
        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack stack = inventory.getStackInSlotOnClosing(i);
                if (stack != null)
                {
                    inventory.setInventorySlotContentsNoSave(i, null);
                    if (!player.inventory.addItemStackToInventory(stack))
                    {
                        player.dropPlayerItemWithRandomChoice(stack, false);
                    }
                }
            }
        }
        if (tile != null) tile.afterContainerTick();
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        boolean clickedIsHeld = slot >= 0 && slot < inventorySlots.size() && player.getHeldItem() != null
                && getSlot(slot).getStack() == player.getHeldItem();
        if (HeldContainerGuard.refuses(!wearing && tile == null, clickedIsHeld, flag, button, player.inventory.currentItem))
        {
            return null;
        }
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        Slot slot = getSlot(index);
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();
        if (index >= PLAYER_SLOTS)
        {
            if (!mergeItemStack(stack, 0, PLAYER_SLOTS, false)) return null;
        } else if (FuelContainers.isFuelContainer(stack))
        {
            if (!mergeItemStack(stack, PLAYER_SLOTS + InventoryFlamethrowerPack.SLOT_IN, PLAYER_SLOTS + InventoryFlamethrowerPack.SLOT_IN + 1, false)) return null;
        } else
        {
            return null;
        }
        if (stack.stackSize == 0)
        {
            slot.putStack(null);
        } else
        {
            slot.onSlotChanged();
        }
        if (stack.stackSize == result.stackSize) return null;
        slot.onPickupFromSlot(player, stack);
        return result;
    }

    @Override
    public void refresh()
    {
        inventory.openInventory();
    }
}
