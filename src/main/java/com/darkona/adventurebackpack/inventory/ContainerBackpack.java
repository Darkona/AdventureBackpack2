package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IWearableContainer;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.compat.invtweaks.BackpackSections;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.Wearing;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSectionCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import java.util.Map;

@ChestContainer(showButtons = false, rowSize = 8)
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
    public final BackpackLayout layout;
    private Map<?, ?> invTweaksSections;

    public ContainerBackpack(EntityPlayer player, IInventoryAdventureBackpack backpack, byte source)
    {
        this.player = player;
        inventory = backpack;
        layout = BackpackLayouts.layout(backpack.getColorName(), backpack.getStorageSize());
        makeSlots(player.inventory);
        inventory.openInventory();
        this.source = source;
    }

    public IInventoryAdventureBackpack getInventoryBackpack()
    {
        return inventory;
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        BackpackLayout l = layout;
        for (int k = 0; k < l.storage(); k++)
        {
            addSlotToContainer(new SlotBackpack(inventory, BackpackLayout.inventoryIndex(k), l.storageX(k), l.storageY(k)));
        }
        addSlotToContainer(new SlotTool(inventory, BackpackLayout.UPPER_TOOL, l.toolX(), l.toolY(0)));
        addSlotToContainer(new SlotTool(inventory, BackpackLayout.LOWER_TOOL, l.toolX(), l.toolY(1)));
        for (int i = 0; i < l.bucketSlots(); i++)
        {
            addSlotToContainer(new SlotFluid(inventory, BackpackLayout.bucketIndex(i), l.bucketX(i), l.bucketY(i)));
        }
        for (int q = 0; q < l.quiver(); q++)
        {
            addSlotToContainer(new SlotQuiver(inventory, BackpackLayout.quiverIndex(l.storage(), q), l.quiverX(q), l.quiverY(q)));
        }
        for (int i = 0; i < 9; i++)
        {
            addSlotToContainer(new Slot(craftMatrix, i, l.craftX(i), l.craftY(i)));
        }
        addSlotToContainer(new SlotCrafting(invPlayer.player, craftMatrix, craftResult, 0, l.resultX(), l.resultY()));
        for (int row = 0; row < 3; row++)
        {
            for (int col = 0; col < 9; col++)
            {
                addSlotToContainer(new Slot(invPlayer, 9 + col + row * 9, l.playerX() + BackpackLayout.SLOT * col, l.playerY() + BackpackLayout.SLOT * row));
            }
        }
        for (int col = 0; col < 9; col++)
        {
            addSlotToContainer(new Slot(invPlayer, col, l.playerX() + BackpackLayout.SLOT * col, l.hotbarY()));
        }
        this.onCraftMatrixChanged(craftMatrix);
    }

    @ChestContainer.RowSizeCallback
    public int invTweaksRowSize()
    {
        return layout.columns();
    }

    @ContainerSectionCallback
    public Map<?, ?> invTweaksSections()
    {
        if (invTweaksSections == null) invTweaksSections = BackpackSections.of(this);
        return invTweaksSections;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        if (source == SOURCE_HOLDING)
        {
            return player.getCurrentEquippedItem() == inventory.getParentItemStack();
        }
        if (source == SOURCE_WEARING)
        {
            return Wearing.getWearingBackpack(player) == inventory.getParentItemStack();
        }
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
        boolean clickedIsHeld = slot >= 0 && slot < inventorySlots.size() && player.getHeldItem() != null
                && getSlot(slot).getStack() == player.getHeldItem();
        if (HeldContainerGuard.refuses(source == SOURCE_HOLDING, clickedIsHeld, flag, button, player.inventory.currentItem))
        {
            return null;
        }
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int minSlot, int maxSlot, boolean direction)
    {
        if (direction)
        {
            return super.mergeItemStack(stack, minSlot, maxSlot, true);
        }
        boolean changesMade = stackOnto(stack, minSlot, maxSlot);
        if (stack.stackSize > 0)
        {
            changesMade |= fillEmpty(stack, minSlot, maxSlot);
        }
        return changesMade;
    }

    private boolean stackOnto(ItemStack stack, int minSlot, int maxSlot)
    {
        boolean changesMade = false;
        if (!stack.isStackable()) return false;
        for (int i = minSlot; stack.stackSize > 0 && i < maxSlot; i++)
        {
            Slot slot = (Slot) this.inventorySlots.get(i);
            ItemStack newItemStack = slot.getStack();

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
        }
        return changesMade;
    }

    private boolean fillEmpty(ItemStack stack, int minSlot, int maxSlot)
    {
        for (int i = minSlot; i < maxSlot; i++)
        {
            Slot slot = (Slot) this.inventorySlots.get(i);
            if (slot.getStack() == null)
            {
                slot.putStack(stack.copy());
                slot.onSlotChanged();
                stack.stackSize = 0;
                return true;
            }
        }
        return false;
    }

    private boolean mergeIntoPlayer(ItemStack stack)
    {
        int hot = layout.hotbarStart();
        int main = layout.playerStart();
        boolean changesMade = stackOnto(stack, hot, hot + 9);
        changesMade |= stackOnto(stack, main, main + 27);
        if (stack.stackSize > 0)
        {
            changesMade |= fillEmpty(stack, hot, hot + 9) || fillEmpty(stack, main, main + 27);
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
            BackpackLayout l = layout;
            int storageEnd = l.storage();
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (i < l.playerStart())
            {
                if (!mergeIntoPlayer(stack))
                {
                    return null;
                }
            } else
            {
                if (l.quiver() > 0 && Quiver.accepts(stack))
                {
                    if (!mergeItemStack(stack, l.quiverStart(), l.quiverStart() + l.quiver(), false))
                    {
                        if (!mergeItemStack(stack, 0, storageEnd, false))
                        {
                            return null;
                        }
                    }
                } else if (SlotTool.isValidTool(stack))
                {
                    if (!mergeItemStack(stack, l.toolStart(), l.toolStart() + 2, false))
                    {
                        if (!mergeItemStack(stack, 0, storageEnd, false))
                        {
                            return null;
                        }
                    }
                } else if (SlotFluid.valid(stack))
                {
                    int bucketInLeft = l.bucketStart();
                    int bucketInRight = l.bucketStart() + 2;
                    if (!mergeItemStack(stack, bucketInLeft, bucketInLeft + 1, false))
                    {
                        if (l.tanks() < 2 || !mergeItemStack(stack, bucketInRight, bucketInRight + 1, false))
                        {
                            if (!mergeItemStack(stack, 0, storageEnd, false))
                            {
                                return null;
                            }
                        }
                    }
                } else if (!(stack.getItem() instanceof ItemAdventureBackpack))
                {
                    if (!mergeItemStack(stack, 0, storageEnd, false))
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
    }

    @Override
    public void refresh()
    {
        if (inventory instanceof InventoryBackpack) ((InventoryBackpack) inventory).reloadIfChanged();
    }
}
