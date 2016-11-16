package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

/**
 * Created on 12/10/2014
 * @author Darkona
 *
 */
public class ContainerBackpack extends Container implements IWearableContainer
{

    public IInventoryAdventureBackpack inventory;
    public static byte SOURCE_TILE = 0;
    public static byte SOURCE_WEARING = 1;
    public static byte SOURCE_HOLDING = 2;
    public byte source;
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    EntityPlayer player;

    private final int
            PLAYER_HOT_START = 0,
            PLAYER_HOT_END = PLAYER_HOT_START + 8,
            PLAYER_INV_START = PLAYER_HOT_END + 1,
            PLAYER_INV_END = PLAYER_INV_START + 26,
            BACK_INV_START = PLAYER_INV_END + 1,
            BACK_INV_END = BACK_INV_START + 38,
            TOOL_START = BACK_INV_END + 1,
            TOOL_END = TOOL_START + 1,
            BUCKET_LEFT = TOOL_END + 1,
            BUCKET_RIGHT = BUCKET_LEFT + 2;

    public ContainerBackpack(EntityPlayer player, IInventoryAdventureBackpack backpack, byte source)
    {
        this.player = player;
        inventory = backpack;
        makeSlots(player.inventory);
        inventory.openInventory();
        this.source = source;
    }

    public IInventoryAdventureBackpack getInventoryBackpack()
    {
        return inventory;
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer)
    {
        int startX = 44;
        int startY = 125;

        // Player's Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, startX + 18 * x, 183));
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

        // Backpack Inventory
        int startX = 62;
        int startY = 7;
        int slot = 0;

        // 24 Slots
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                int offsetX = startX + (18 * j);
                int offsetY = startY + (18 * i);
                addSlotToContainer(new SlotBackpack(inventory, slot++, offsetX, offsetY));
            }
        }

        // 15 Slots
        startY = 61;
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                int offsetX = startX + (18 * j);
                int offsetY = startY + (18 * i);
                addSlotToContainer(new SlotBackpack(inventory, slot++, offsetX, offsetY));
            }
        }

        //Upper Tool Slot
        addSlotToContainer(new SlotTool(inventory, Constants.upperTool, 44, 79));// Upper Tool 16
        //Lower Tool slot
        addSlotToContainer(new SlotTool(inventory, Constants.lowerTool, 44, 97));// Lower Tool 17

        //Bucket Slots

        // bucket in left 18
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketInLeft, 6, 7));
        // bucket out left 19
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketOutLeft, 6, 37));
        // bucket in right  20
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketInRight, 226, 7));
        // bucket out right 21
        addSlotToContainer(new SlotFluid(inventory, Constants.bucketOutRight, 226, 37));


        //Craft Matrix
        startX = 152;
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 3; x++)
            {
                int offsetX = startX + (18 * x);
                int offsetY = startY + (18 * y);
                addSlotToContainer(new Slot(craftMatrix, (x + y * 3), offsetX, offsetY));
            }
        }
        addSlotToContainer(new SlotCrafting(invPlayer.player, craftMatrix, craftResult, 0, 226, 97));
        this.onCraftMatrixChanged(craftMatrix);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj));
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if(source == SOURCE_WEARING)
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

            for (int i = 0; i < 9; i++)
            {
                ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem() && source == SOURCE_HOLDING)
        {
            return null;
        }
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int minSlot, int maxSlot, boolean direction)
    {
        boolean changesMade = false;
        int slotInit = minSlot;

        if (direction)
        {
            slotInit = maxSlot - 1;
        }

        Slot slot;
        ItemStack newItemStack;

        if (stack.isStackable())
        {
            while (stack.stackSize > 0 && (!direction && slotInit < maxSlot || direction && slotInit >= minSlot))
            {
                slot = (Slot) this.inventorySlots.get(slotInit);
                newItemStack = slot.getStack();

                if (newItemStack != null && newItemStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == newItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, newItemStack))
                {

                    int newStackSize = newItemStack.stackSize + stack.stackSize;

                    if (newStackSize <= stack.getMaxStackSize())
                    {
                        stack.stackSize = 0;
                        newItemStack.stackSize = newStackSize;
                        slot.onSlotChanged();
                        changesMade = true;
                    } else if (newItemStack.stackSize < stack.getMaxStackSize())
                    {
                        stack.stackSize -= stack.getMaxStackSize() - newItemStack.stackSize;
                        newItemStack.stackSize = stack.getMaxStackSize();
                        slot.onSlotChanged();
                        changesMade = true;
                    }
                }

                if (direction)
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
            if (direction)
            {
                slotInit = maxSlot - 1;
            } else
            {
                slotInit = minSlot;
            }

            while (!direction && slotInit < maxSlot || direction && slotInit >= minSlot)
            {
                slot = (Slot) this.inventorySlots.get(slotInit);
                newItemStack = slot.getStack();

                if (newItemStack == null)
                {
                    slot.putStack(stack.copy());
                    slot.onSlotChanged();
                    stack.stackSize = 0;
                    changesMade = true;
                    break;
                }

                if (direction)
                {
                    --slotInit;
                } else
                {
                    ++slotInit;
                }
            }
        }

        return changesMade;
    }

    @Override
    public Slot getSlotFromInventory(IInventory inv, int slot)
    {
        return super.getSlotFromInventory(inv, slot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
        if(source == SOURCE_WEARING)refresh();
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
                if (SlotTool.isValidTool(stack))
                {
                    if (!mergeItemStack(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                        {
                            return null;
                        }
                    }
                } else if (SlotFluid.valid(stack) && SlotFluid.isValidTool(stack))
                {
                    if (!mergeItemStack(stack, BUCKET_LEFT, BUCKET_LEFT + 1, false))
                    {
                        if (!mergeItemStack(stack, BUCKET_RIGHT, BUCKET_RIGHT + 1, false))
                        {
                            if (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                            {
                                return null;
                            }
                        }
                    }


                } else if (!(stack.getItem() instanceof ItemAdventureBackpack))
                {
                    if (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
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

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        refresh();
        super.detectAndSendChanges();
        if((source == SOURCE_WEARING || source == SOURCE_HOLDING) && player instanceof EntityPlayerMP)
         {
             ((EntityPlayerMP) player).sendContainerAndContentsToPlayer(this, inventoryItemStacks);
             //BackpackProperty.syncToNear(player);
         }
    }

    @Override
    public void refresh()
    {
        inventory.openInventory();
        this.onCraftMatrixChanged(craftMatrix);
    }
}
