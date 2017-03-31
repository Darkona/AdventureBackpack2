package com.darkona.adventurebackpack.inventory;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.util.FluidUtils;

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
     * @param inventory The inventory type thing that will have its tank updated.
     * @param tank      The tank that's going to be updated.
     * @param slotIn    The slot in which the fluid container item must be to update the tank.
     * @return True if the tank was filled and the resulting filled or empty container item was placed in the other slot.
     */
    public static boolean transferContainerTank(IInventoryTanks inventory, FluidTank tank, int slotIn)
    {
        ItemStack stackIn = inventory.getStackInSlot(slotIn);
        if (tank == null || stackIn == null) return false;

        //Set slot out for whatever number the output slot should be.
        int slotOut = slotIn + 1;

        //CONTAINER ===========> TANK
        //if (!SlotFluid.isEmpty(stackIn))
        if (FluidContainerRegistry.isFilledContainer(stackIn))
        {
            //See if the tank can accept moar fluid.
            int fill = tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), false);

            if (fill > 0)//If can accept the fluid
            {
                //Get the empty container for the input, if there's any.
                ItemStack stackOut = FluidContainerRegistry.drainFluidContainer(stackIn);

                if (inventory.getStackInSlot(slotOut) == null || stackOut == null)
                {

                    tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), true);
                    //inventory.dirtyTanks();
                    inventory.decrStackSizeNoSave(slotIn, 1);
                    inventory.setInventorySlotContentsNoSave(slotOut, stackOut);
                    //inventory.dirtyInventory();
                    return true;
                } else if (inventory.getStackInSlot(slotOut).getItem() == stackOut.getItem())
                //} else if (inventory.getStackInSlot(slotOut).getItem().equals(stackOut.getItem()))
                {
                    int maxStack = inventory.getStackInSlot(slotOut).getMaxStackSize();
                    if (maxStack > 1 && (inventory.getStackInSlot(slotOut).stackSize + 1) <= maxStack)
                    {

                        tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), true);
                        //inventory.dirtyTanks();
                        inventory.decrStackSizeNoSave(slotIn, 1);
                        inventory.getStackInSlot(slotOut).stackSize++;
                        //inventory.dirtyInventory();
                        return true;
                    }
                }
            }
        }

        //TANK =====> CONTAINER

        if (tank.getFluid() != null && tank.getFluidAmount() > 0 && FluidUtils.isEmptyContainerForFluid(stackIn, tank.getFluid().getFluid()))
        {
            //How much fluid can this container hold.
            int amount = FluidContainerRegistry.getContainerCapacity(tank.getFluid(), stackIn);
            //Let's see how much can we drain this tank
            FluidStack drain = tank.drain(amount, false);

            ItemStack stackOut = FluidContainerRegistry.fillFluidContainer(drain, stackIn);

            if (drain.amount == amount)
            {
                if (inventory.getStackInSlot(slotOut) == null)
                {
                    tank.drain(amount, true);
                    //inventory.dirtyTanks();
                    inventory.decrStackSizeNoSave(slotIn, 1);
                    inventory.setInventorySlotContentsNoSave(slotOut, stackOut);
                    //inventory.dirtyInventory();
                    return true;
                } else if (stackOut != null && stackOut.getItem() != null  // ??!
                        && stackOut.getItem() == inventory.getStackInSlot(slotOut).getItem())
                {
                    int maxStack = inventory.getStackInSlot(slotOut).getMaxStackSize();
                    if (maxStack > 1 && (inventory.getStackInSlot(slotOut).stackSize + 1) <= maxStack)
                    {
                        tank.drain(amount, true);
                        //inventory.dirtyTanks();
                        inventory.decrStackSizeNoSave(slotIn, 1);
                        inventory.getStackInSlot(slotOut).stackSize++;
                        //inventory.dirtyInventory();
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
        for (int j = 0; j < Constants.END_OF_INVENTORY; ++j)
        {
            if (backpack.getStackInSlot(j) != null && backpack.getStackInSlot(j).getItem() == item)
            {
                i = j;
                break;
            }
        }
        if (i >= 0)
        {
            backpack.decrStackSize(i, 1);
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
            if (inventory[i] != null
                    && inventory[i].getItem().equals(Item.getItemFromBlock(item)))
            {
                return true;
            }
        }
        return false;
    }

}
