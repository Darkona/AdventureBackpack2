package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.blocks.BlockAdventureBackpack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.items.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public class InventoryItem implements IAdvBackpack {

    public ItemStack[] inventory;
    public FluidTank leftTank;
    public FluidTank rightTank;
    private String name;
    public ItemStack containerStack;
    private String color;
    private String colorName;
    private int lastTime = 0;

    //

    public InventoryItem(ItemStack stack) {
        containerStack = stack;
        rightTank = new FluidTank(Constants.basicTankCapacity);
        leftTank = new FluidTank(Constants.basicTankCapacity);
        inventory = new ItemStack[Constants.inventorySize];
        name = "Adventure Backpack";
        readFromNBT();
    }

    @Override
    public int getSizeInventory() {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory[slot];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null) {
            if (stack.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                stack = stack.splitStack(amount);
            }
        }
        onInventoryChanged();
        return stack;
    }

    private ItemStack decrStackSizeSafe(int slot, int amount) {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null) {
            if (stack.stackSize <= amount) {
                setInventorySlotContents(slot, null);
            } else {
                stack = stack.splitStack(amount);
            }
        }
        return stack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack) {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
        onInventoryChanged();
    }

    @Override
    public String getInventoryName() {
        return "AdventureBackpack";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public boolean updateTankSlots(FluidTank tank, int slotIn) {

        int slotOut = slotIn + 1;
        ItemStack stackIn = getStackInSlot(slotIn);
        ItemStack stackOut = getStackInSlot(slotOut);

        if (tank.getFluid() != null) {
            for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                if (data.fluid.isFluidEqual(tank.getFluid())) {

                    if (stackIn.isItemEqual(data.emptyContainer) && tank.drain(data.fluid.amount, false).amount >= data.fluid.amount) {

                        if (stackOut != null && stackOut.isItemEqual(data.filledContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                            ItemStack newCont = FluidContainerRegistry.fillFluidContainer(data.fluid, stackIn);
                            newCont.stackSize = stackOut.stackSize + 1;
                            setInventorySlotContentsSafe(slotOut, newCont);
                            decrStackSizeSafe(slotIn, 1);
                            tank.drain(data.fluid.amount, true);
                            saveChanges();

                        } else if (stackOut == null) {
                            ItemStack newCont = FluidContainerRegistry.fillFluidContainer(data.fluid, stackIn);
                            newCont.stackSize = 1;
                            setInventorySlotContentsSafe(slotOut, newCont);
                            decrStackSizeSafe(slotIn, 1);
                            tank.drain(data.fluid.amount, true);
                            saveChanges();

                        }
                    } else if (stackIn.isItemEqual(data.filledContainer) && tank.fill(data.fluid, false) >= data.fluid.amount) {

                        if (stackOut != null && stackOut.isItemEqual(data.emptyContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                            if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                                setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), stackOut.stackSize + 1));
                            }
                            decrStackSizeSafe(slotIn, 1);
                            tank.fill(data.fluid, true);
                            saveChanges();

                        } else if (stackOut == null) {
                            if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                                setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), 1));
                            }
                            decrStackSizeSafe(slotIn, 1);
                            tank.fill(data.fluid, true);
                            saveChanges();
                        }
                    }
                }
            }
        } else if (tank.getFluid() == null) {
            for (FluidContainerRegistry.FluidContainerData data : FluidContainerRegistry.getRegisteredFluidContainerData()) {
                if (stackIn.isItemEqual(data.filledContainer) && tank.fill(data.fluid, false) >= data.fluid.amount) {

                    if (stackOut != null && stackOut.isItemEqual(data.emptyContainer) && stackOut.stackSize < stackOut.getMaxStackSize()) {
                        if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                            setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), stackOut.stackSize + 1));
                        }
                        decrStackSizeSafe(slotIn, 1);
                        tank.fill(data.fluid, true);
                        saveChanges();

                    } else if (stackOut == null) {
                        if (Utils.shouldGiveEmpty(data.emptyContainer)) {
                            setInventorySlotContentsSafe(slotOut, new ItemStack(data.emptyContainer.getItem(), 1));
                        }
                        decrStackSizeSafe(slotIn, 1);
                        tank.fill(data.fluid, true);
                        saveChanges();
                    }
                }
            }
        }

        return false;
    }

    public void setInventorySlotContentsSafe(int slot, ItemStack itemstack) {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public void markDirty() {

    }

    @Override
    public void openInventory() {
        readFromNBT();
    }

    @Override
    public void closeInventory() {
        saveChanges();
    }

    public void onInventoryChanged() {
        for (int i = 0; i < inventory.length; i++) {
            if (i == 6 && inventory[i] != null) {
                updateTankSlots(getLeftTank(), i);

            }

            if (i == 8 && inventory[i] != null) {
                if (updateTankSlots(getRightTank(), i)) {
                    containerStack.stackTagCompound = writeToNBT();
                }
            }
        }
        saveChanges();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stacky) {
        if (stacky.getItem() instanceof ItemAdventureBackpack) return false;
        if (Block.getBlockFromItem(stacky.getItem()) instanceof BlockAdventureBackpack) return false;
        return true;
    }

    public boolean readFromNBT() {
        if (containerStack != null) {
            NBTTagCompound compound = containerStack.hasTagCompound() ? containerStack.getTagCompound() : new NBTTagCompound();
            NBTTagList items = compound.getTagList("ABPItems", 1);
            for (int i = 0; i < items.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
                byte slot = item.getByte("Slot");
                if (slot >= 0 && slot < inventory.length) {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(item);
                }
            }
            leftTank.readFromNBT(compound.getCompoundTag("leftTank"));
            rightTank.readFromNBT(compound.getCompoundTag("rightTank"));
            color = compound.getString("color");
            colorName = compound.getString("colorName");
            lastTime = compound.getInteger("lastTime");
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound tankLeft = new NBTTagCompound();
        NBTTagCompound tankRight = new NBTTagCompound();

        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++) {
            ItemStack stack = inventory[i];
            if (stack != null) {
                NBTTagCompound item = new NBTTagCompound();
                item.setByte("Slot", (byte) i);
                stack.writeToNBT(item);
                items.appendTag(item);
            }
        }
        compound.setTag("ABPItems", items);
        compound.setTag("rightTank", rightTank.writeToNBT(tankRight));
        compound.setTag("leftTank", leftTank.writeToNBT(tankLeft));
        compound.setString("color", color);
        compound.setString("colorName", colorName);
        compound.setInteger("lastTime", lastTime);
        return compound;
    }

    @Override
    public FluidTank getLeftTank() {
        //leftTank.readFromNBT(containerStack.stackTagCompound.getCompoundTag("leftTank"));
        return leftTank;
    }

    public void setLeftTank(FluidTank leftTank) {
        this.leftTank = leftTank;
        saveChanges();
    }

    @Override
    public FluidTank getRightTank() {
        //rightTank.readFromNBT(containerStack.stackTagCompound.getCompoundTag("rightTank"));
        return rightTank;
    }

    public void setRightTank(FluidTank rightTank) {
        this.rightTank = rightTank;
        saveChanges();
    }

    public void tellMeLeft() {
        if (leftTank.getFluid() != null) {
            System.out.println("The left tank has" + this.leftTank.getFluidAmount() + " " + this.leftTank.getFluid().getFluid().getName());
        } else {
            System.out.println("The left tank is empty");
        }
    }

    private void saveChanges() {
        this.containerStack.stackTagCompound = writeToNBT();
    }

}

