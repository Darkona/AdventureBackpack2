package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_OUT;

/**
 * Created on 03/01/2015
 *
 * @author Darkona
 */
public class ContainerCopter extends ContainerAdventureBackpack implements IWearableContainer
{
    private final int PLAYER_HOT_START = 0;
    private final int PLAYER_HOT_END = PLAYER_HOT_START + 8;
    private final int PLAYER_INV_START = PLAYER_HOT_END + 1;
    private final int PLAYER_INV_END = PLAYER_INV_START + 26;
    private final int COPTER_INV_START = PLAYER_INV_END + 1;
    InventoryCopterPack inventory;
    private EntityPlayer player;
    private boolean wearing;

    public ContainerCopter(EntityPlayer player, InventoryCopterPack copter, boolean wearing)
    {
        this.player = player;
        inventory = copter;
        makeSlots(player.inventory);
        inventory.openInventory();
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
        addSlotToContainer(new SlotFluidFuel(inventory, COPTER_BUCKET_IN, 44, 23));
        // bucket out
        addSlotToContainer(new SlotFluidFuel(inventory, COPTER_BUCKET_OUT, 44, 53));
    }

    @Override
    public void detectAndSendChanges()
    {
        refresh();
        super.detectAndSendChanges();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot)
    {
        Slot slot = getSlot(fromSlot);
        ItemStack result = null;

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (fromSlot >= 36)
            {
                if (!mergeItemStack(stack, PLAYER_HOT_START, PLAYER_INV_END + 1, false))
                {
                    return null;
                }
            }
            if (fromSlot < 36)
            {
                if (SlotFluid.isContainer(stack))
                {
                    ItemStack outStack = getSlot(COPTER_INV_START + 1).getStack();

                    FluidTank fuelTank = inventory.getFuelTank();
                    int maxAmount = fuelTank.getCapacity();
                    int tankAmount = fuelTank.getFluidAmount();
                    int tankFluid = SlotFluid.getFluidID(fuelTank);

                    int containerCapacity = SlotFluid.getCapacity(stack);
                    int containerFluid = SlotFluid.getFluidID(stack);

                    if (SlotFluid.isFilled(stack))
                    {
                        if ((outStack == null || (SlotFluid.isEmptyBucket(outStack) && SlotFluid.isBucket(stack)))
                                && SlotFluidFuel.isValidItem(stack))
                        {
                            if ((tankAmount == 0 || (tankAmount > 0 && tankFluid == containerFluid))
                                    && tankAmount + containerCapacity <= maxAmount)
                            {
                                if (!mergeItemStack(stack, COPTER_INV_START, COPTER_INV_START + 1, false))
                                {
                                    return null;
                                }
                            }
                        }
                    } else if (SlotFluid.isEmpty(stack))
                    {
                        if (outStack == null && tankAmount >= containerCapacity && SlotFluidFuel.isValidItem(stack))
                        {
                            if (!mergeItemStack(stack, COPTER_INV_START, COPTER_INV_START + 1, false))
                            {
                                return null;
                            }
                        }
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
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        if (!wearing && slot >= 0)
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
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    public void refresh()
    {
        inventory.openInventory();
    }
}
