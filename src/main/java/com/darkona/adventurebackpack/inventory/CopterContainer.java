package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created on 03/01/2015
 *
 * @author Darkona
 */
public class CopterContainer extends Container
{

    public InventoryCopterPack inventory;
    private final int
            PLAYER_HOT_START = 0,
            PLAYER_HOT_END = PLAYER_HOT_START + 8,
            PLAYER_INV_START = PLAYER_HOT_END + 1,
            PLAYER_INV_END = PLAYER_INV_START + 26,
            COPTER_INV_START = PLAYER_INV_END + 1;

    public CopterContainer(EntityPlayer player,InventoryCopterPack copterPack)
    {
        this.inventory = copterPack;
        makeSlots(player.inventory);
        inventory.openInventory();
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
        int slot = 0;
        //Bucket Slots
        // bucket in
        addSlotToContainer(new SlotFluid(inventory, slot++, 99, 22));
        // bucket out
        addSlotToContainer(new SlotFluid(inventory, slot++, 99, 52));
    }

      @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
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
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem()) {
            return null;
        }
        inventory.onInventoryChanged();
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int minSlot, int maxSlot, boolean par4)
    {
        boolean flag1 = false;
        int slotInit = minSlot;

        if (par4)
        {
            slotInit = maxSlot - 1;
        }

        Slot slot;
        ItemStack itemstack1;

        if (stack.isStackable())
        {
            while (stack.stackSize > 0 && (!par4 && slotInit < maxSlot || par4 && slotInit >= minSlot))
            {
                slot = (Slot) this.inventorySlots.get(slotInit);
                itemstack1 = slot.getStack();

                if (itemstack1 != null && itemstack1.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == itemstack1.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, itemstack1))
                {

                    int newStackSize = itemstack1.stackSize + stack.stackSize;

                    if (newStackSize <= stack.getMaxStackSize())
                    {
                        stack.stackSize = 0;
                        itemstack1.stackSize = newStackSize;
                        slot.onSlotChanged();
                        flag1 = true;
                    } else if (itemstack1.stackSize < stack.getMaxStackSize())
                    {
                        stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
                        itemstack1.stackSize = stack.getMaxStackSize();
                        slot.onSlotChanged();
                        flag1 = true;
                    }
                }

                if (par4)
                {
                    --slotInit;
                } else
                {
                    ++slotInit;
                }
            }
        }

        if (stack.stackSize > 0)
        {
            if (par4)
            {
                slotInit = maxSlot - 1;
            } else
            {
                slotInit = minSlot;
            }

            while (!par4 && slotInit < maxSlot || par4 && slotInit >= minSlot)
            {
                slot = (Slot) this.inventorySlots.get(slotInit);
                itemstack1 = slot.getStack();

                if (itemstack1 == null)
                {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.stackSize = 0;
                    flag1 = true;
                    break;
                }

                if (par4)
                {
                    --slotInit;
                } else
                {
                    ++slotInit;
                }
            }
        }

        return flag1;
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
        // TODO Fix the shit disrespecting slot accepting itemstack.
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
            inventory.onInventoryChanged();
        }
        return result;
    }
}
