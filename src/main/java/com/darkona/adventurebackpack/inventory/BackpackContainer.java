package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

/**
 * Created by Darkona on 12/10/2014.
 */
public class BackpackContainer extends Container
{

    public IAdvBackpack inventory;
    public static byte SOURCE_TILE = 0;
    public static byte SOURCE_WEARING = 1;
    public static byte SOURCE_HOLDING = 2;
    public byte source;
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    private World world;


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
            BUCKET_RIGHT = BUCKET_LEFT +2;

    public BackpackContainer(EntityPlayer player, IAdvBackpack backpack, byte source)
    {
        inventory = backpack;
        makeSlots(player.inventory);
        inventory.openInventory();
        this.source = source;
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
    public void putStacksInSlots(ItemStack[] itemStacks)
    {
        for (int i = 0; i < itemStacks.length; i++)
        {
            this.getSlot(i).putStack(itemStacks[i]);
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }


    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.world));
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
                if (SlotTool.isValidTool(stack))
                {
                    if (!mergeItemStack(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                        {
                            return null;
                        }
                    }
                } else if (SlotFluid.valid(stack))
                {
                    if (!mergeItemStack(stack, BUCKET_LEFT, BUCKET_LEFT + 1, false))
                    {
                        if(!mergeItemStack(stack, BUCKET_RIGHT, BUCKET_RIGHT + 1, false))
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
            inventory.onInventoryChanged();
        }
        return result;
    }


    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
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

            for (int i = 0; i < 9; ++i)
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
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
    }

    @Override
    public Slot getSlotFromInventory(IInventory iInventory, int index)
    {
        return super.getSlotFromInventory(iInventory, index);
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
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem() && source == SOURCE_HOLDING) {
            return null;
        }
        inventory.onInventoryChanged();
        return super.slotClick(slot, button, flag, player);
    }


}
