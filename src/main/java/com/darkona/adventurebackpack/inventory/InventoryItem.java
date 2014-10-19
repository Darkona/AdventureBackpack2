package com.darkona.adventurebackpack.inventory;


import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.LogHelper;
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
 * @author Darkona
 *
 */
public class InventoryItem implements IAdvBackpack
{

    public ItemStack[] inventory;
    public boolean needsUpdate = false;
    private FluidTank leftTank;
    private FluidTank rightTank;
    private ItemStack containerStack;
    private String color;
    private String colorName;
    private int lastTime = 0;
    private boolean special;
    private NBTTagCompound extendedProperties;

    public InventoryItem(ItemStack backpack)
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
    public void setLeftTank(FluidTank tank)
    {
        this.leftTank = tank;
    }

    @Override
    public void setRightTank(FluidTank tank)
    {
        this.leftTank = tank;
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
    public ItemStack getInventoryItem()
    {
        return this.containerStack;
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
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        if (slot < inventory.length && inventory[slot] != null)
        {
            if (inventory[slot].stackSize > amount)
            {
                ItemStack result = inventory[slot].splitStack(amount);
                saveChanges();
                LogHelper.info("Saved in decrStackSize");
                return result;
            }
            ItemStack stack = inventory[slot];
            setInventorySlotContentsNoSave(slot, null);
            return stack;
        }
        return null;
    }

    @Override
    public void updateTankSlots(FluidTank tank, int slotIn)
    {
        InventoryActions.transferContainerTank(this, tank, slotIn);
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
        compound.setString("color", color);
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
        color = compound.getString("color");
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

        boolean changed = false;
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
        markDirty();
        saveChanges();
    }

    @Override
    public void saveChanges()
    {
        this.containerStack.setTagCompound(writeToNBT());
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
            }
        }
        onInventoryChanged();
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (slot == 6 || slot == 7 || slot == 8 || slot == 9)
        {
            return inventory[slot];
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
        onInventoryChanged();
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
        this.needsUpdate = true;
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
        needsUpdate = false;
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
        if (slot == 6 || slot == 8)
        {
            return FluidContainerRegistry.isContainer(stack);
        }

        return !(slot == 0 || slot == 3) || SlotTool.isValidTool(stack);
    }


}

