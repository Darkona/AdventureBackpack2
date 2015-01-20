package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ContainerJetpack extends Container implements IWearableContainer
{
    InventorySteamJetpack inventory;
    EntityPlayer player;
    private final int
    PLAYER_HOT_START = 0;
    private final int PLAYER_HOT_END = PLAYER_HOT_START + 8;
    private final int PLAYER_INV_START = PLAYER_HOT_END + 1;
    private final int PLAYER_INV_END = PLAYER_INV_START + 26;
    boolean wearing;

    public ContainerJetpack(EntityPlayer player, InventorySteamJetpack inventory, boolean wearing)
    {
        this.player = player;
        this.inventory = inventory;
        makeSlots(player.inventory);
        this.inventory.openInventory();
        this.wearing = wearing;
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer)
    {
        int startX = 8;
        int startY = 84;

        // Player's Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, startX + 18 * x, 142));
        }

        // Player's Inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, (x + y * 9 + 9), (startX + 18 * x), (startY + y * 18)));
            }
        }
        //Total 36 slots
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {

        bindPlayerInventory(invPlayer);

        //Bucket Slots
        // bucket in
        addSlotToContainer(new SlotFluid(inventory, InventorySteamJetpack.BUCKET_IN_SLOT, 30, 22));
        // bucket out
        addSlotToContainer(new SlotFluid(inventory, InventorySteamJetpack.BUCKET_OUT_SLOT, 30, 52));
        // fuel
        addSlotToContainer(new SlotFuel(inventory,InventorySteamJetpack.FUEL_SLOT, 77, 64));

    }
    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    public void detectAndSendChanges()
    {
        refresh();
        super.detectAndSendChanges();

    }

    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < 3; i++)
            {
                ItemStack itemstack = this.inventory.getStackInSlotOnClosing(i);
                if (itemstack != null)
                {
                    inventory.setInventorySlotContents(i, null);
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
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
                    int JETPACK_INV_START = PLAYER_INV_END + 1;
                    if (!mergeItemStack(stack, JETPACK_INV_START, JETPACK_INV_START + 1, false))
                    {

                    }
                }
                else
                {
                    int JETPACK_FUEL_START = PLAYER_INV_END + 3;
                    if (inventory.isFuel(stack) && !mergeItemStack(stack, JETPACK_FUEL_START, JETPACK_FUEL_START + 1, false))
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
    public void refresh()
    {
        inventory.openInventory();
    }
}
