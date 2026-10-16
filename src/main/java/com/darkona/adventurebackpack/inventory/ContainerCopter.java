package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IWearableContainer;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.block.TileGear;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCopter extends Container implements IWearableContainer
{
    private TileGear tile;

    public InventoryCopterPack inventory;
    private final int
            PLAYER_HOT_START = 0;
    private final int PLAYER_HOT_END = PLAYER_HOT_START + 8;
    private final int PLAYER_INV_START = PLAYER_HOT_END + 1;
    private final int PLAYER_INV_END = PLAYER_INV_START + 26;
    EntityPlayer player;
    boolean wearing;

    public ContainerCopter(EntityPlayer player, InventoryCopterPack copterPack, TileGear tile)
    {
        this(player, copterPack, false);
        this.tile = tile;
    }

    public ContainerCopter(EntityPlayer player, InventoryCopterPack copterPack, boolean wearing)
    {
        this.inventory = copterPack;
        makeSlots(player.inventory);
        inventory.openInventory();
        this.player = player;
        this.wearing = wearing;
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer)
    {
        int startX = 8;
        int startY = 84;

        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, startX + 18 * x, 142));
        }

        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, (x + y * 9 + 9), (startX + 18 * x), (startY + y * 18)));
            }
        }
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        bindPlayerInventory(invPlayer);
        int slot = 0;
        addSlotToContainer(new SlotFluid(inventory, slot++, 44, 23));
        addSlotToContainer(new SlotFluid(inventory, slot++, 44, 53));
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        if (tile != null) return tile.isUseableByPlayer(p_75145_1_);
        return HeldContainerGuard.stillOpen(wearing, inventory.getParentItemStack(),
                BackpackProperty.get(p_75145_1_).getWearable(), p_75145_1_.getCurrentEquippedItem());
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
                ItemStack itemstack = this.inventory.getStackInSlotOnClosing(i);
                if (itemstack != null)
                {
                    inventory.setInventorySlotContents(i, null);
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
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
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
        Slot slot = getSlot(i);
        ItemStack result = null;

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (i >= 36)
            {
                if (!mergeItemStack(stack, PLAYER_HOT_START, PLAYER_INV_END + 1, false))
                {
                    return null;
                }
            }
            if (i < 36)
            {
                if (SlotFluid.valid(stack))
                {
                    int COPTER_INV_START = PLAYER_INV_END + 1;
                    if (!mergeItemStack(stack, COPTER_INV_START, COPTER_INV_START + 1, false))
                    {
                        return null;
                    }
                }
            }

            if (stack.stackSize == 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }

            if (stack.stackSize == result.stackSize)
            {
                return null;
            }
            slot.onPickupFromSlot(player, stack);
        }
        return result;
    }

    @Override
    public void detectAndSendChanges()
    {
        refresh();
        super.detectAndSendChanges();
        if (tile != null) tile.afterContainerTick();
    }

    @Override
    public void refresh()
    {
        if (!inventory.isCurrentFor(inventory.getParentItemStack())) inventory.openInventory();
    }
}
