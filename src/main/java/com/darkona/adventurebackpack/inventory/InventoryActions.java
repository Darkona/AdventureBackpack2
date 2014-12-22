package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 16/10/2014
 *
 * @author Darkona
 */
public class InventoryActions
{

    /**
     * What a complicated mess. I hated every minute of coding this.
     * This code takes a fluid container item. If its filled, it empties it out into a tank.
     * If its empty, it drains the tank into the item. Then it puts the resulting filled or empty item
     * into a different slot, consuming the first one. If there is no empty container, such as the Forestry Cells,
     * it simply fills the tank.
     *
     * @param backpack The backpack type thing that will have its tank updated.
     * @param tank     The tank that's going to be updated.
     * @param slotIn   The slot in which the fluid container item must be to update the tank.
     * @return True if the tank was filled and the resulting filled or empty container item was placed in the other slot.
     */
    public static boolean transferContainerTank(IAdvBackpack backpack, FluidTank tank, int slotIn)
    {
        ItemStack stackIn = backpack.getStackInSlot(slotIn);
        if (tank == null || stackIn == null) return false;

        //Set slot out for whatever number the output slot should be.
        int slotOut = slotIn + 1;

        //CONTAINER ===========> TANK
        if (FluidContainerRegistry.isFilledContainer(stackIn))
        {
            //See if the tank can accept moar fluid.
            int fill = tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), false);

            if (fill > 0)//If can accept the fluid
            {
                //Get the empty container for the input, if there's any.
                ItemStack stackOut = FluidContainerRegistry.drainFluidContainer(stackIn);

                if (backpack.getStackInSlot(slotOut) == null || stackOut == null)
                {
                    backpack.setInventorySlotContentsNoSave(slotOut, stackOut);
                    tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), true);
                    backpack.decrStackSizeNoSave(slotIn, 1);
                    backpack.markDirty();
                    return true;
                } else if (backpack.getStackInSlot(slotOut).getItem() == stackOut.getItem())
                {
                    int maxStack = backpack.getStackInSlot(slotOut).getMaxStackSize();
                    if (maxStack > 1 && (backpack.getStackInSlot(slotOut).stackSize + 1) <= maxStack)
                    {
                        backpack.getStackInSlot(slotOut).stackSize++;
                        tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), true);
                        backpack.decrStackSizeNoSave(slotIn, 1);
                        backpack.markDirty();
                        return true;
                    }
                }
            }
        }

        //TANK =====> CONTAINER
        if (tank.getFluid() != null && tank.getFluidAmount() > 0 && FluidContainerRegistry.isEmptyContainer(stackIn))
        {
            //How much fluid can this container hold.
            int amount = FluidContainerRegistry.getContainerCapacity(tank.getFluid(), stackIn);
            //Let's see how much can we drain this tank
            FluidStack drain = tank.drain(Constants.bucket, false);

            ItemStack stackOut = FluidContainerRegistry.fillFluidContainer(drain, stackIn);

            if (drain.amount == amount)
            {
                if (backpack.getStackInSlot(slotOut) == null)
                {
                    backpack.decrStackSizeNoSave(slotIn, 1);
                    tank.drain(amount, true);
                    backpack.setInventorySlotContentsNoSave(slotOut, stackOut);
                    return true;
                } else if (stackOut.getItem() != null && stackOut.getItem() == backpack.getStackInSlot(slotOut).getItem())
                {
                    int maxStack = backpack.getStackInSlot(slotOut).getMaxStackSize();
                    if (maxStack > 1 && (backpack.getStackInSlot(slotOut).stackSize + 1) <= maxStack)
                    {
                        backpack.decrStackSizeNoSave(slotIn, 1);
                        tank.drain(amount, true);
                        backpack.getStackInSlot(slotOut).stackSize++;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void consumeItemInBackpack(IAdvBackpack backpack, Item item)
    {
        ItemStack[] inventory = backpack.getInventory();
        int i = -1;
        for (int j = 0; j < Constants.inventorySize-7; ++j)
        {
            if (backpack.getInventory()[j] != null && backpack.getInventory()[j].getItem() == item)
            {
                i = j;
                break;
            }
        }
        if (i < 0)
        {
            return;
        } else
        {
           if (--inventory[i].stackSize <= 0)
            {
                inventory[i] = null;
            }
        }
    }

    public static boolean hasItem(IAdvBackpack backpack, Item item)
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

}
