package com.darkona.adventurebackpack.inventory;


import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
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
public class InventoryBackpack implements IInventoryAdventureBackpack
{
    public ItemStack[] inventory = new ItemStack[Constants.inventorySize];
    private FluidTank leftTank = new FluidTank(Constants.basicTankCapacity);
    private FluidTank rightTank = new FluidTank(Constants.basicTankCapacity);
    private ItemStack containerStack;
    private String colorName = "Standard";
    private int lastTime = 0;
    private boolean special = false;
    public NBTTagCompound extendedProperties = new NBTTagCompound();

    public InventoryBackpack(ItemStack backpack)
    {
        containerStack = backpack;
        if(!backpack.hasTagCompound())
        {
            backpack.stackTagCompound = new NBTTagCompound();
            saveToNBT(backpack.stackTagCompound);
        }
        loadFromNBT(backpack.stackTagCompound);
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
    public void saveTanks(NBTTagCompound compound)
    {
        compound.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void loadTanks(NBTTagCompound compound)
    {
        leftTank.readFromNBT(compound.getCompoundTag("leftTank"));
        rightTank.readFromNBT(compound.getCompoundTag("rightTank"));
    }

    @Override
    public boolean hasItem(Item item)
    {
        return InventoryActions.hasItem(this, item);
    }

    @Override
    public void consumeInventoryItem(Item item)
    {
        InventoryActions.consumeItemInInventory(this, item);
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
        dirtyInventory();
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
        boolean answer =  InventoryActions.transferContainerTank(this, getLeftTank(), Constants.bucketInLeft) ||
                InventoryActions.transferContainerTank(this, getRightTank(), Constants.bucketInRight);
        dirtyInventory();
        dirtyTanks();
        return answer;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if(compound.hasKey("backpackData"))
        {
            NBTTagCompound backpackData = compound.getCompoundTag("backpackData");
            NBTTagList items = backpackData.getTagList("ABPItems", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount(); i++)
            {
                NBTTagCompound item = items.getCompoundTagAt(i);
                byte slot = item.getByte("Slot");
                if (slot >= 0 && slot < inventory.length)
                {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(item);
                }
            }

            leftTank.readFromNBT(backpackData.getCompoundTag("leftTank"));
            rightTank.readFromNBT(backpackData.getCompoundTag("rightTank"));
            colorName = backpackData.getString("colorName");
            lastTime = backpackData.getInteger("lastTime");
            special = backpackData.getBoolean("special");
            extendedProperties = backpackData.getCompoundTag("extendedProperties");
        }
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
       // if(Utils.inServer())
       // {
        NBTTagCompound backpackData = new NBTTagCompound();
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
        backpackData.removeTag("ABPItems");
        backpackData.setTag("ABPItems", items);
        backpackData.setString("colorName", colorName);
        backpackData.setInteger("lastTime", lastTime);
        backpackData.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        backpackData.setTag("extendedProperties", extendedProperties);
        backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));

        compound.setTag("backpackData",backpackData);
        //}
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[0];
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
        saveToNBT(containerStack.stackTagCompound);
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory()
    {
        loadFromNBT(containerStack.stackTagCompound);
    }

    @Override
    public void closeInventory()
    {
       /* if(Utils.inServer())
        {*/
        saveToNBT(containerStack.stackTagCompound);
       // }
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

    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag("backpackData").setTag("leftTank",leftTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag("backpackData").setTag("rightTank",rightTank.writeToNBT(new NBTTagCompound()));
    }

    public void dirtyTime()
    {
        containerStack.stackTagCompound.getCompoundTag("backpackData").setInteger("lastTime",lastTime);
    }

    public void dirtyExtended()
    {
        containerStack.stackTagCompound.getCompoundTag("backpackData").removeTag("extendedProperties");
        containerStack.stackTagCompound.getCompoundTag("backpackData").setTag("extendedProperties",extendedProperties);
    }

    public void dirtyInventory()
    {
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
        containerStack.stackTagCompound.getCompoundTag("backpackData").removeTag("ABPItems");
        containerStack.stackTagCompound.getCompoundTag("backpackData").setTag("ABPItems", items);
    }

    public boolean hasBlock(Block block)
    {
        return InventoryActions.hasBlockItem(this, block);
    }
}

