package com.darkona.adventurebackpack.inventory;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.darkona.adventurebackpack.common.Constants.Source;

/**
 * Created on 10.04.2017
 *
 * @author Ugachaga
 */
@SuppressWarnings("WeakerAccess")
public abstract class ContainerAdventure extends Container
{
    protected static final int PLAYER_INV_ROWS = 3;
    protected static final int PLAYER_INV_COLUMNS = 9;
    protected static final int PLAYER_HOT_START = 0;
    protected static final int PLAYER_HOT_END = PLAYER_HOT_START + PLAYER_INV_COLUMNS - 1;
    protected static final int PLAYER_INV_END = PLAYER_HOT_END + PLAYER_INV_COLUMNS * PLAYER_INV_ROWS;
    protected static final int PLAYER_INV_LENGTH = PLAYER_INV_END + 1;

    protected final EntityPlayer player;
    protected final IInventoryTanks inventory;
    protected final Source source;

    private final int[] fluidsAmount;
    private int itemsCount;
    private boolean requestedUpdate;

    protected ContainerAdventure(EntityPlayer player, IInventoryTanks inventory, Source source)
    {
        this.player = player;
        this.inventory = inventory;
        this.source = source;
        this.fluidsAmount = new int[this.inventory.getTanksArray().length];
    }

    protected void bindPlayerInventory(InventoryPlayer invPlayer, int startX, int startY)
    {
        for (int col = 0; col < PLAYER_INV_COLUMNS; col++) // hotbar - 9 slots
            addSlotToContainer(new Slot(invPlayer, col, (startX + 18 * col), (58 + startY)));

        for (int row = 0; row < PLAYER_INV_ROWS; row++) // inventory - 3*9, 27 slots
            for (int col = 0; col < PLAYER_INV_COLUMNS; col++)
                addSlotToContainer(new Slot(invPlayer, (PLAYER_INV_COLUMNS + row * PLAYER_INV_COLUMNS + col), (startX + 18 * col), (startY + row * 18)));
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (source == Source.HOLDING) // used for refresh tooltips and redraw tanks content while GUI is open
        {
            // intentionally update container with 1 tick delay after detect changes due to visual glitches
            // in rare cases on some modded items, ex.: shift+q on blood magic lava crystals
            if (requestedUpdate && player instanceof EntityPlayerMP)
            {
                ((EntityPlayerMP) player).sendContainerAndContentsToPlayer(this, this.getInventory());
                requestedUpdate = false;
            }

            if (detectItemChanges() | detectFluidChanges())
            {
                requestedUpdate = true;
            }
        }
    }

    protected boolean detectItemChanges()
    {
        ItemStack[] inv = inventory.getInventory();
        int tempCount = 0;
        for (int i = 0; i < inv.length - inventory.getSlotsOnClosing().length; i++)
        {
            if (inv[i] != null)
                tempCount++;
        }
        if (itemsCount != tempCount)
        {
            itemsCount = tempCount;
            return true;
        }
        return false;
    }

    private boolean detectFluidChanges()
    {
        boolean changesDetected = false;
        for (int i = 0; i < fluidsAmount.length; i++)
        {
            int amount = inventory.getTanksArray()[i].getFluidAmount();
            if (fluidsAmount[i] != amount)
            {
                fluidsAmount[i] = amount;
                changesDetected = true;
            }
        }
        return changesDetected;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot)
    {
        Slot slot = getSlot(fromSlot);

        if (slot == null || slot.getStack() == null)
            return null;

        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();

        if (fromSlot < PLAYER_INV_LENGTH)
        {
            if (!transferStackToPack(stack))
                return null;
        }
        else
        {
            if (!mergePlayerInv(stack))
                return null;
        }

        if (stack.stackSize == 0)
        {
            slot.putStack(null);
        }
        else
        {
            slot.onSlotChanged();
        }

        if (stack.stackSize == result.stackSize)
            return null;

        slot.onPickupFromSlot(player, stack);
        return result;
    }

    protected boolean mergePlayerInv(ItemStack stack)
    {
        return mergeItemStack(stack, PLAYER_HOT_START, PLAYER_INV_END + 1, false);
    }

    protected abstract boolean transferStackToPack(ItemStack stack);

    @Nullable
    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        if (source == Source.HOLDING && slot >= 0)
        {
            if (getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem())
            {
                return null;
            }
            if (flag == 2 && getSlot(button).getStack() == player.getHeldItem())
            {
                return null;
            }
        }
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    protected boolean mergeItemStack(ItemStack initStack, int minIndex, int maxIndex, boolean backward)
    {
        boolean changesMade = false;
        int activeIndex = (backward ? maxIndex - 1 : minIndex);
        Slot activeSlot;
        ItemStack activeStack;

        if (initStack.isStackable())
        {
            while (initStack.stackSize > 0 && (!backward && activeIndex < maxIndex || backward && activeIndex >= minIndex))
            {
                activeSlot = (Slot) this.inventorySlots.get(activeIndex);
                activeStack = activeSlot.getStack();

                if (activeStack != null && activeStack.getItem() == initStack.getItem()
                        && (!initStack.getHasSubtypes() || initStack.getItemDamage() == activeStack.getItemDamage())
                        && ItemStack.areItemStackTagsEqual(initStack, activeStack))
                {
                    int mergedSize = activeStack.stackSize + initStack.stackSize;
                    int maxStackSize = Math.min(initStack.getMaxStackSize(), activeSlot.getSlotStackLimit());

                    if (mergedSize <= maxStackSize)
                    {
                        initStack.stackSize = 0;
                        activeStack.stackSize = mergedSize;
                        activeSlot.onSlotChanged();
                        changesMade = true;
                    }
                    else if (activeStack.stackSize < maxStackSize && !(activeSlot instanceof SlotFluid))
                    {
                        initStack.stackSize -= maxStackSize - activeStack.stackSize;
                        activeStack.stackSize = maxStackSize;
                        activeSlot.onSlotChanged();
                        changesMade = true;
                    }
                }
                activeIndex += (backward ? -1 : 1);
            }
        }

        if (initStack.stackSize > 0)
        {
            activeIndex = (backward ? maxIndex - 1 : minIndex);

            while (!backward && activeIndex < maxIndex || backward && activeIndex >= minIndex)
            {
                activeSlot = (Slot) this.inventorySlots.get(activeIndex);
                activeStack = activeSlot.getStack();

                if (activeStack == null /*&& activeSlot.isItemValid(initStack)*/)
                {
                    ItemStack copyStack = initStack.copy();
                    int mergedSize = copyStack.stackSize = Math.min(copyStack.stackSize, activeSlot.getSlotStackLimit());

                    activeSlot.putStack(copyStack);
                    if (mergedSize >= initStack.stackSize)
                    {
                        initStack.stackSize = 0;
                    }
                    else
                    {
                        initStack.stackSize -= mergedSize;
                    }
                    changesMade = true;
                    break;
                }
                activeIndex += (backward ? -1 : 1);
            }
        }

        return changesMade;
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if (source == Source.WEARING) //TODO no idea why this is here (preventing dupe on closing?), and why only for wearing
        {
            this.crafters.remove(player);
        }

        if (!player.worldObj.isRemote)
        {
            dropContentOnClose();
        }
    }

    protected void dropContentOnClose()
    {
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            ItemStack itemstack = inventory.getStackInSlotOnClosing(i);
            if (itemstack != null)
            {
                inventory.setInventorySlotContents(i, null);
                player.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }
    }
}