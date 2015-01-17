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
    InventorySteamJetpack inv;
    EntityPlayer player;
    private final int
    PLAYER_HOT_START = 0,
    PLAYER_HOT_END = PLAYER_HOT_START + 8,
    PLAYER_INV_START = PLAYER_HOT_END + 1,
    PLAYER_INV_END = PLAYER_INV_START + 26,
    JETPACK_INV_START = PLAYER_INV_END + 1;
    boolean wearing;

    public ContainerJetpack(EntityPlayer player, InventorySteamJetpack inv, boolean wearing)
    {
        this.player = player;
        this.inv = inv;
        makeSlots(player.inventory);
        inv.openInventory();
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
        addSlotToContainer(new SlotFluid(inv, InventorySteamJetpack.BUCKET_IN_SLOT, 30, 22));
        // bucket out
        addSlotToContainer(new SlotFluid(inv, InventorySteamJetpack.BUCKET_OUT_SLOT, 30, 52));
        // fuel
        addSlotToContainer(new Slot(inv,InventorySteamJetpack.FUEL_SLOT, 77, 64));

    }
    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
    }



    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < 1; i++)
            {
                ItemStack itemstack = this.inv.getStackInSlotOnClosing(i);
                if (itemstack != null)
                {
                    inv.setInventorySlotContents(i, null);
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
                    if (!mergeItemStack(stack, JETPACK_INV_START, JETPACK_INV_START + 3, false))
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
            //inventory.onInventoryChanged();
        }
        return result;
    }

    @Override
    public void refresh()
    {

    }
}
