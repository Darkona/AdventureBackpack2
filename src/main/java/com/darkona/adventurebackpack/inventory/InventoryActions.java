package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.util.FluidUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class InventoryActions
{
    public static boolean transferContainerTank(IInventoryTanks inventory, FluidTank tank, int slotIn)
    {
        ItemStack stackIn = inventory.getStackInSlot(slotIn);
        if (tank == null || stackIn == null) return false;

        int slotOut = slotIn + 1;

        if (FluidContainerRegistry.isFilledContainer(stackIn))
        {
            int fill = tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), false);

            if (fill > 0)
            {
                ItemStack stackOut = FluidContainerRegistry.drainFluidContainer(stackIn);

                if (inventory.getStackInSlot(slotOut) == null || stackOut == null)
                {
                    tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), true);
                    inventory.decrStackSizeNoSave(slotIn, 1);
                    inventory.setInventorySlotContentsNoSave(slotOut, stackOut);
                    return true;
                } else if (inventory.getStackInSlot(slotOut).getItem() == stackOut.getItem())
                {
                    int maxStack = inventory.getStackInSlot(slotOut).getMaxStackSize();
                    if (maxStack > 1 && (inventory.getStackInSlot(slotOut).stackSize + 1) <= maxStack)
                    {
                        tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), true);
                        inventory.decrStackSizeNoSave(slotIn, 1);
                        inventory.getStackInSlot(slotOut).stackSize++;
                        return true;
                    }
                }
            }
        }

        if (tank.getFluid() != null && tank.getFluidAmount() > 0 && FluidUtils.isEmptyContainerForFluid(stackIn, tank.getFluid().getFluid()))
        {
            int amount = FluidContainerRegistry.getContainerCapacity(tank.getFluid(), stackIn);
            FluidStack drain = tank.drain(amount, false);

            ItemStack stackOut = FluidContainerRegistry.fillFluidContainer(drain, stackIn);

            if (drain.amount == amount)
            {
                if (inventory.getStackInSlot(slotOut) == null)
                {
                    tank.drain(amount, true);
                    inventory.decrStackSizeNoSave(slotIn, 1);
                    inventory.setInventorySlotContentsNoSave(slotOut, stackOut);
                    return true;
                } else
                if (stackOut.getItem() != null
                    && stackOut.getItem() == inventory.getStackInSlot(slotOut).getItem())
                {
                    int maxStack = inventory.getStackInSlot(slotOut).getMaxStackSize();
                    if (maxStack > 1 && (inventory.getStackInSlot(slotOut).stackSize + 1) <= maxStack)
                    {
                        tank.drain(amount, true);
                        inventory.decrStackSizeNoSave(slotIn, 1);
                        inventory.getStackInSlot(slotOut).stackSize++;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void consumeItemInInventory(IInventory backpack, Item item)
    {
        int i = -1;
        for (int j = 0; j < backpack.getSizeInventory(); ++j)
        {
            if (j >= BackpackLayout.LEGACY_STORAGE && j < BackpackLayout.FIRST_EXTRA_STORAGE) continue;
            if (backpack.getStackInSlot(j) != null && backpack.getStackInSlot(j).getItem() == item)
            {
                i = j;
                break;
            }
        }
        if (i >= 0)
        {
           backpack.decrStackSize(i,1);
        }
    }

    public static boolean hasItem(IInventoryAdventureBackpack backpack, Item item)
    {
        ItemStack[] inventory = backpack.getInventory();
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] != null &&
                    inventory[i].getItem().equals(item))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBlockItem(IInventoryAdventureBackpack backpack, Block item)
    {
        ItemStack[] inventory = backpack.getInventory();
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] != null &&
                    inventory[i].getItem().equals(Item.getItemFromBlock(item)))
            {
                return true;
            }
        }
        return false;
    }
}
