package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created by Darkona on 12/10/2014.
 */
public class BackpackContainer extends Container
{

    public IAdvBackpack inventory;
    public static boolean SOURCE_TILE = true;
    public static boolean SOURCE_ITEM = false;

    public BackpackContainer(InventoryPlayer invPlayer, IAdvBackpack backpack, boolean source)
    {
        inventory = backpack;
        makeSlots(invPlayer);
    }


    // =============================================== GETTERS ====================================================== //
    // =============================================== SETTERS ====================================================== //
    // ================================================ ORDER ======================================================= //
    // ================================================ STACKS ====================================================== //


    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer){
        // Player's Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 142));
        }

        // Player's Inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, (x + y * 9 + 9), (8 + 18 * x), (84 + y * 18)));
            }
        }
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {

        bindPlayerInventory(invPlayer);
        int thing = 0;

        // Backpack Inventory

        int startX = 62;
        int startY = 7;
        for(int i = 0; i < 4; i++){
            for (int j = 0; j < 4; j++){
                int offsetX = startX + (18 * j);
                int offsetY = startY + (18 * i);
                addSlotToContainer(new SlotBackpack(inventory, thing++, offsetX, offsetY));// 0
            }
        }

        //Upper Tool Slot
        addSlotToContainer(new SlotTool(inventory, Constants.upperTool, 44, 25));// Upper Tool 16
        //Lower Tool slot
        addSlotToContainer(new SlotTool(inventory, Constants.lowerTool, 44, 43));// Lower Tool 17

        //Bucket Slots

        // bucket in left 18
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketInLeft, 6, 25));
        // bucket out left 19
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketOutLeft, 6, 55));
        // bucket in right  20
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketInRight, 153, 25));
        // bucket out right 21
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketOutRight, 153, 55));
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
        // TODO Fix the shit disrespecting slot accepting itemstack.
        Slot slot = getSlot(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            ItemStack result = stack.copy();
            if (i >= 36)
            {
                if (!mergeItemStack(stack, 0, 36, false))
                {
                    return null;
                }
            } else if (!mergeItemStack(stack, 36, 36 + inventory.getSizeInventory(), false))
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

            slot.onPickupFromSlot(player, stack);
            inventory.onInventoryChanged();
            return result;
        }
        return null;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < inventory.getSizeInventory(); ++i)
            {
                if (i == Constants.bucketInRight || i == Constants.bucketInLeft || i == Constants.bucketOutLeft || i == Constants.bucketOutRight)
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
    }

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (int i = 0; i < inventory.getInventory().length; ++i)
        {
            ItemStack itemstack = ((Slot) this.inventorySlots.get(i)).getStack();
            ItemStack itemstack1 = this.inventory.getInventory()[i];

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack))
            {
                itemstack1 = itemstack == null ? null : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);
            }
        }
    }

    @Override
    public Slot getSlotFromInventory(IInventory p_75147_1_, int p_75147_2_)
    {
        return super.getSlotFromInventory(p_75147_1_, p_75147_2_);
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
    public void putStackInSlot(int slot, ItemStack stack)
    {
        super.putStackInSlot(slot, stack);
    }

    @Override
    public ItemStack slotClick(int par1, int par2, int par3, EntityPlayer player)
    {
        inventory.onInventoryChanged();
        return super.slotClick(par1, par2, par3, player);
    }

}
