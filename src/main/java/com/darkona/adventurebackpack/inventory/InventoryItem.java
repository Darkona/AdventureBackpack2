package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public class InventoryItem implements IAdvBackpack
{

    private ItemStack[] inventory;
    private FluidTank leftTank;
    private FluidTank rightTank;
    private String name;
    private ItemStack containerStack;
    private String color;
    private String colorName;
    private int lastTime = 0;
    private boolean special;
    //

    public InventoryItem(ItemStack stack)
    {
        containerStack = stack;
        rightTank = new FluidTank(Constants.basicTankCapacity);
        leftTank = new FluidTank(Constants.basicTankCapacity);
        inventory = new ItemStack[Constants.inventorySize];
        name = "Adventure Backpack";
        readFromNBT();
        special = BackpackAbilities.hasAbility(getColorName());
    }

    // =============================================== GETTERS ====================================================== //
    @Override
    public int getSizeInventory()
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inventory[slot];
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        switch (slot)
        {
            case 6:
            case 7:
            case 8:
            case 9:
                return inventory[slot];
        }
        return null;
    }

    @Override
    public String getInventoryName()
    {
        return "gui.backpackInventory.name";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public FluidTank getLeftTank()
    {
        //leftTank.readFromNBT(containerStack.stackTagCompound.getCompoundTag("leftTank"));
        return leftTank;
    }

    @Override
    public FluidTank getRightTank()
    {
        //rightTank.readFromNBT(containerStack.stackTagCompound.getCompoundTag("rightTank"));
        return rightTank;
    }

    @Override
    public ItemStack getInventoryItem()
    {
        return containerStack;
    }


    // =============================================== SETTERS ====================================================== //
    @Override
    public void setInventorySlotContents(int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        onInventoryChanged();
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack itemstack)
    {
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    public void setLeftTank(FluidTank leftTank)
    {
        this.leftTank = leftTank;
        saveChanges();
    }

    public void setRightTank(FluidTank rightTank)
    {
        this.rightTank = rightTank;
        saveChanges();
    }

    // ================================================ ORDER ======================================================= //
    @Override
    public void openInventory()
    {
        readFromNBT();
    }

    @Override
    public void closeInventory()
    {
        saveChanges();
    }

    @Override
    public void updateTankSlots(FluidTank tank, int slotIn)
    {
        InventoryActions.transferContainerTank(this, tank, slotIn);
    }

    @Override
    public String getColor()
    {
        return color;
    }

    @Override
    public String getColorName()
    {
        return colorName;
    }

    @Override
    public ItemStack[] getInventory()
    {
        return this.inventory;
    }

    @Override
    public boolean isSpecial()
    {
        return special;
    }

    @Override
    public void onInventoryChanged()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (i == 6 && inventory[i] != null)
            {
                updateTankSlots(getLeftTank(), i);
            }

            if (i == 8 && inventory[i] != null)
            {
                updateTankSlots(getRightTank(), i);
            }
        }
        saveChanges();
    }

    public void saveChanges()
    {
        this.containerStack.setTagCompound(writeToNBT());
    }

    // ================================================ STACKS ====================================================== //
    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null)
        {
            if (stack.stackSize <= amount)
            {
                setInventorySlotContents(slot, null);
            } else
            {
                stack = stack.splitStack(amount);
            }
        }
        onInventoryChanged();
        return stack;
    }

    @Override
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= amount)
            {
                setInventorySlotContents(slot, null);
            } else
            {
                stack = stack.splitStack(amount);
            }
        }
        return stack;
    }

    @Override
    public boolean hasItem(Item item)
    {
        return InventoryActions.hasItem(this, item);
    }

    @Override
    public void consumeInventoryItem(Item item)
    {
        InventoryActions.consumeItemInBackpack(this, item);
    }

    // =============================================== BOOLEANS ===================================================== //
    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer)
    {
        return true;
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, ItemStack stacky)
    {
        if (stacky.getItem() instanceof ItemAdventureBackpack) return false;
        if (Block.getBlockFromItem(stacky.getItem()) instanceof BlockAdventureBackpack) return false;
        return true;
    }
    // ================================================ NBT ========================================================= //

    public boolean readFromNBT()
    {
        if (containerStack != null)
        {
            NBTTagCompound compound = containerStack.hasTagCompound() ? containerStack.getTagCompound() : new NBTTagCompound();
            NBTTagList items = compound.getTagList("ABPItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount(); i++)
            {
                NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
                byte slot = item.getByte("Slot");
                if (slot >= 0 && slot < inventory.length)
                {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(item);
                }
            }
            leftTank.readFromNBT(compound.getCompoundTag("leftTank"));
            rightTank.readFromNBT(compound.getCompoundTag("rightTank"));
            color = compound.getString("color");
            colorName = compound.getString("colorName");
            lastTime = compound.getInteger("lastTime");
            special = compound.getBoolean("special");
            return true;
        }
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        NBTTagCompound tankLeft = new NBTTagCompound();
        NBTTagCompound tankRight = new NBTTagCompound();

        NBTTagList items = new NBTTagList();
        for (int i = 0; i < inventory.length; i++)
        {
            ItemStack stack = inventory[i];
            if (stack != null)
            {
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
        compound.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        return compound;
    }

    // ============================================== OTHERS ======================================================== //

    /**
     * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
     * hasn't changed and skip it.
     */
    @Override
    public void markDirty()
    {

    }

    @Override
    public TileAdventureBackpack getTile()
    {
        return null;
    }
}

