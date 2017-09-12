package com.darkona.adventurebackpack.inventory;

import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.item.ItemHose;
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
        if (tank == null) return false;
        int slotOut = slotIn + 1;
        ItemStack stackHose = inventory.getStackInSlot(slotOut);

        if (stackHose != null && stackHose.getItem() instanceof ItemHose)
        {
            if (tank.getFluidAmount() > 0)
            {
                tank.drain(tank.getFluidAmount(), true); //Hose in the bucketOut slot will empty the tank
                return true;
            }
            //return false;
        }

        ItemStack stackIn = inventory.getStackInSlot(slotIn);
        if (stackIn == null) return false;

        //CONTAINER ===========> TANK
        if (FluidContainerRegistry.isFilledContainer(stackIn))
        {
            int fill = tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), false); //See if the tank can accept moar fluid

            if (fill > 0) //If can accept the fluid
            {
                if (FluidContainerRegistry.getContainerCapacity(stackIn) + tank.getFluidAmount() <= tank.getCapacity())
                {
                    ItemStack stackOut = FluidContainerRegistry.drainFluidContainer(stackIn); //Get the empty container for the input, if there's any

                    if (inventory.getStackInSlot(slotOut) == null || stackOut == null)
                    {
                        tank.fill(FluidContainerRegistry.getFluidForFilledItem(stackIn), true);
                        inventory.decrStackSizeNoSave(slotIn, 1);
                        inventory.setInventorySlotContentsNoSave(slotOut, stackOut);
                        return true;
                    }
                    else if (inventory.getStackInSlot(slotOut).getItem().equals(stackOut.getItem())
                            && stackOut.getItemDamage() == inventory.getStackInSlot(slotOut).getItemDamage())
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
        }

        //TANK =====> CONTAINER
        else if (tank.getFluid() != null && tank.getFluidAmount() > 0 && FluidUtils.isEmptyContainerForFluid(stackIn, tank.getFluid().getFluid()))
        {
            int amount = FluidContainerRegistry.getContainerCapacity(tank.getFluid(), stackIn); //How much fluid can this container hold
            FluidStack drain = tank.drain(amount, false); //Let's see how much can we drain this tank

            if (amount > 0 && drain.amount == amount)
            {
                ItemStack stackOut = FluidContainerRegistry.fillFluidContainer(drain, stackIn);

                if (inventory.getStackInSlot(slotOut) == null || stackOut == null)
                {
                    tank.drain(amount, true);
                    inventory.decrStackSizeNoSave(slotIn, 1);
                    inventory.setInventorySlotContentsNoSave(slotOut, stackOut);
                    return true;
                }
                else if (stackOut.getItem().equals(inventory.getStackInSlot(slotOut).getItem())
                        && stackOut.getItemDamage() == inventory.getStackInSlot(slotOut).getItemDamage())
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

    public static boolean areStacksCompatible(ItemStack stackA, ItemStack stackB)
    {
        return stackA.getItem() == stackB.getItem()
                && (!stackA.getHasSubtypes() || stackA.getItemDamage() == stackB.getItemDamage())
                && ItemStack.areItemStackTagsEqual(stackA, stackB);
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
        for (ItemStack slotStack : inventory)
        {
            if (slotStack != null && slotStack.getItem().equals(item))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasBlockItem(IInventoryAdventureBackpack backpack, Block item)
    {
        ItemStack[] inventory = backpack.getInventory();
        for (ItemStack slotStack : inventory)
        {
            if (slotStack != null && slotStack.getItem().equals(Item.getItemFromBlock(item)))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean areContainersOfSameType(ItemStack stackIn, ItemStack stackOut)
    {
        if (stackIn == null || stackOut == null || stackIn.getItem() == null || stackOut.getItem() == null)
            return false;

        if (SlotFluid.isFilled(stackIn) && SlotFluid.isEmpty(stackOut))
        {
            ItemStack emptyIn = SlotFluid.getEmptyContainer(stackIn);
            return stackOut.isStackable() && areStacksCompatible(emptyIn, stackOut);
        }
        else if (SlotFluid.isEmpty(stackIn) && SlotFluid.isFilled(stackOut))
        {
            ItemStack emptyOut = SlotFluid.getEmptyContainer(stackOut);
            return stackOut.isStackable() && areStacksCompatible(stackIn, emptyOut);
        }
        return false;
    }
}
