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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class InventoryBackpack implements IAdvBackpack
{
    public ItemStack[] inventory;
    private FluidTank leftTank;
    private FluidTank rightTank;
    private ItemStack containerStack;
    private String colorName;
    private int lastTime = 0;
    private boolean special;
    private NBTTagCompound extendedProperties;

    public InventoryBackpack(ItemStack backpack)
    {
        this.containerStack = backpack;
        inventory = new ItemStack[Constants.inventorySize];
        leftTank = new FluidTank(Constants.basicTankCapacity);
        rightTank = new FluidTank(Constants.basicTankCapacity);
        readFromNBT();
    }

    @Override
    public FluidTank getLeftTank()
    {
        return leftTank;
    }

    @Override
    public FluidTank getRightTank()
    {
        return rightTank;
    }

    @Override
    public ItemStack[] getInventory()
    {
        return inventory;
    }

    @Override
    public TileAdventureBackpack getTile()
    {
        return null;
    }

    @Override
    public ItemStack getParentItemStack()
    {
        return this.containerStack;
    }

    @Override
    public String getColorName()
    {
        return colorName;
    }

    @Override
    public int getLastTime()
    {
        return this.lastTime;
    }

    @Override
    public NBTTagCompound getExtendedProperties()
    {
        return extendedProperties;
    }

    @Override
    public void setExtendedProperties(NBTTagCompound properties)
    {
        this.extendedProperties = properties;
    }

    @Override
    public boolean isSpecial()
    {
        return special;
    }

    @Override
    public void saveTanks()
    {
        this.containerStack.getTagCompound().setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
        this.containerStack.getTagCompound().setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void loadTanks()
    {
        this.leftTank.readFromNBT(this.containerStack.getTagCompound().getCompoundTag("leftTank"));
        this.rightTank.readFromNBT(this.containerStack.getTagCompound().getCompoundTag("rightTank"));
    }

    @Override
    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
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
        compound.setString("colorName", colorName);
        compound.setInteger("lastTime", lastTime);
        compound.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        compound.setTag("extended", extendedProperties);
        compound.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
        return compound;
    }

    @Override
    public void readFromNBT()
    {
        if (containerStack == null) return;
        NBTTagCompound compound = containerStack.hasTagCompound() ? containerStack.getTagCompound() : new NBTTagCompound();
        NBTTagList items = compound.getTagList("ABPItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
        leftTank.readFromNBT(compound.getCompoundTag("leftTank"));
        rightTank.readFromNBT(compound.getCompoundTag("rightTank"));
        colorName = compound.getString("colorName");
        lastTime = compound.getInteger("lastTime");
        special = compound.getBoolean("special");
        extendedProperties = compound.getCompoundTag("extended");
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
        onInventoryChanged();
    }

    @Override
    public void onInventoryChanged()
    {
        updateTankSlots();
        saveChanges();
    }

    @Override
    public void saveChanges()
    {
        this.containerStack.setTagCompound(writeToNBT());
    }

    @Override
    public boolean isSBDeployed()
    {
        return false;
    }

    @Override
    public void setLastTime(int time)
    {
        this.lastTime = time;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (slot > inventory.length) return;
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
        onInventoryChanged();
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack stack)
    {
        if (slot > inventory.length) return;
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity)
    {
        ItemStack itemstack = getStackInSlot(slot);
        if (itemstack != null)
        {
            if (itemstack.stackSize <= quantity)
            {
                setInventorySlotContents(slot, null);
            } else
            {
                itemstack = itemstack.splitStack(quantity);
                onInventoryChanged();
            }
        }
        return itemstack;
    }
    @Override
    public ItemStack decrStackSizeNoSave(int slot, int quantity)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= quantity)
            {
                setInventorySlotContentsNoSave(slot, null);
            } else
            {
                stack = stack.splitStack(quantity);
            }
        }
        return stack;
    }

    public boolean updateTankSlots()
    {
        boolean changed = false;
        for (int i = 0; i < inventory.length; i++)
        {
            if (i == Constants.bucketInLeft && inventory[i] != null)
            {
                changed = InventoryActions.transferContainerTank(this, getLeftTank(), i);
            }

            if (i == Constants.bucketInRight && inventory[i] != null)
            {
                changed = InventoryActions.transferContainerTank(this, getRightTank(), i);
            }
        }
        return changed;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (slot == Constants.bucketInLeft || slot == Constants.bucketInRight || slot == Constants.bucketOutLeft || slot == Constants.bucketOutRight)
        {
            return inventory[slot];
        }
        return null;
    }

    @Override
    public String getInventoryName()
    {
        return "Adventure Backpack";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public void markDirty()
    {
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

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
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack.getItem() instanceof ItemAdventureBackpack || Block.getBlockFromItem(stack.getItem()) instanceof BlockAdventureBackpack)
        {
            return false;
        }
        if (slot == Constants.bucketInRight || slot == Constants.bucketInLeft)
        {
            return FluidContainerRegistry.isContainer(stack);
        }

        return !(slot == Constants.upperTool || slot == Constants.lowerTool) || SlotTool.isValidTool(stack);
    }

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
}

