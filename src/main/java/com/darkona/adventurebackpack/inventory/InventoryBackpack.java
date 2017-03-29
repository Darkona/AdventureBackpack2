package com.darkona.adventurebackpack.inventory;


import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;

import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.COMPOUND_TAG;
import static com.darkona.adventurebackpack.common.Constants.INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.LEFT_TANK;
import static com.darkona.adventurebackpack.common.Constants.LOWER_TOOL;
import static com.darkona.adventurebackpack.common.Constants.RIGHT_TANK;
import static com.darkona.adventurebackpack.common.Constants.UPPER_TOOL;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class InventoryBackpack implements IInventoryAdventureBackpack
{
    public ItemStack[] inventory = new ItemStack[Constants.INVENTORY_SIZE];
    private FluidTank leftTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
    private FluidTank rightTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);

    private static final boolean OFF = false;
    private static final boolean ON = true;
    private boolean disableNVision = OFF;
    private boolean disableCycling = OFF;

    private ItemStack containerStack;

    /*public ItemStack getContainerStack()
    {
        return containerStack;
    }
    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
    }*/

    private String colorName = "Standard";
    private int lastTime = 0;
    private boolean special = false;
    public NBTTagCompound extendedProperties = new NBTTagCompound();

    public InventoryBackpack(ItemStack backpack)
    {
        containerStack = backpack;
        openInventory();
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
        compound.setTag(RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        compound.setTag(LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void loadTanks(NBTTagCompound compound)
    {
        leftTank.readFromNBT(compound.getCompoundTag(LEFT_TANK));
        rightTank.readFromNBT(compound.getCompoundTag(RIGHT_TANK));
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

    @Override
    public boolean updateTankSlots()
    {
        return InventoryActions.transferContainerTank(this, getLeftTank(), BUCKET_IN_LEFT) ||
                InventoryActions.transferContainerTank(this, getRightTank(), BUCKET_IN_RIGHT);
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if (compound == null) return; //this need for NEI trying to render tile.backpack and comes here w/o nbt

        NBTTagCompound backpackTag = compound.getCompoundTag(COMPOUND_TAG);
        NBTTagList items = backpackTag.getTagList(INVENTORY, NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }

        leftTank.readFromNBT(backpackTag.getCompoundTag(LEFT_TANK));
        rightTank.readFromNBT(backpackTag.getCompoundTag(RIGHT_TANK));
        colorName = backpackTag.getString("colorName");
        lastTime = backpackTag.getInteger("lastTime");
        special = backpackTag.getBoolean("special");
        extendedProperties = backpackTag.getCompoundTag("extendedProperties");
        disableCycling = backpackTag.getBoolean("disableCycling");
        disableNVision = backpackTag.getBoolean("disableNVision");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound backpackTag = new NBTTagCompound();
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
        backpackTag.removeTag(INVENTORY);
        backpackTag.setTag(INVENTORY, items);
        backpackTag.setString("colorName", colorName);
        backpackTag.setInteger("lastTime", lastTime);
        backpackTag.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        backpackTag.setTag("extendedProperties", extendedProperties);
        backpackTag.setTag(RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag(LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setBoolean("disableCycling", disableCycling);
        backpackTag.setBoolean("disableNVision", disableNVision);

        compound.setTag(COMPOUND_TAG, backpackTag);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] array = {leftTank, rightTank};
        return array;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        if (slot == BUCKET_IN_LEFT || slot == BUCKET_IN_RIGHT || slot == BUCKET_OUT_LEFT || slot == BUCKET_OUT_RIGHT)
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
        /*if(Utils.inServer()) //TODO side?
        {*/
        saveToNBT(containerStack.stackTagCompound);
        //}
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack.getItem() instanceof ItemAdventureBackpack || Block.getBlockFromItem(stack.getItem()) instanceof BlockAdventureBackpack)
        {
            return false;
        }
        if (slot == BUCKET_IN_RIGHT || slot == BUCKET_IN_LEFT)
        {
            return FluidContainerRegistry.isContainer(stack);
        }

        return !(slot == UPPER_TOOL || slot == LOWER_TOOL) || SlotTool.isValidTool(stack);
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
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag(COMPOUND_TAG).setTag(LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag(COMPOUND_TAG).setTag(RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void dirtyTime()
    {
        containerStack.stackTagCompound.getCompoundTag(COMPOUND_TAG).setInteger("lastTime", lastTime);
    }

    @Override
    public void dirtyExtended()
    {
        containerStack.stackTagCompound.getCompoundTag(COMPOUND_TAG).removeTag("extendedProperties");
        containerStack.stackTagCompound.getCompoundTag(COMPOUND_TAG).setTag("extendedProperties", extendedProperties);
    }

    @Override
    public void dirtyInventory()
    {
        if (updateTankSlots())
        {
            dirtyTanks();
        }
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
        containerStack.stackTagCompound.getCompoundTag(COMPOUND_TAG).removeTag(INVENTORY);
        containerStack.stackTagCompound.getCompoundTag(COMPOUND_TAG).setTag(INVENTORY, items);
    }

    public boolean hasBlock(Block block)
    {
        return InventoryActions.hasBlockItem(this, block);
    }

    public boolean getDisableCycling()
    {
        return disableCycling;
    }

    public void setDisableCycling(boolean b)
    {
        this.disableCycling = b;
    }

    public boolean getDisableNVision()
    {
        return disableNVision;
    }

    public void setDisableNVision(boolean b)
    {
        this.disableNVision = b;
    }

}