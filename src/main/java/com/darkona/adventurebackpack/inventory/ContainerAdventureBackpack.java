package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Created on 10.04.2017
 *
 * @author Ugachaga
 */
abstract class ContainerAdventureBackpack extends Container
{
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
                    } else if (activeStack.stackSize < maxStackSize && !(activeSlot instanceof SlotFluid))
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
                    } else
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
}
