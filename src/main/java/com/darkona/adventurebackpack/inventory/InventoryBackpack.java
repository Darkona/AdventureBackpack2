package com.darkona.adventurebackpack.inventory;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.util.CoordsUtils;

import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.LEFT_TANK;
import static com.darkona.adventurebackpack.common.Constants.RIGHT_TANK;
import static com.darkona.adventurebackpack.common.Constants.WEARABLE_TAG;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class InventoryBackpack extends InventoryAdventureBackpack implements IInventoryAdventureBackpack
{
    public NBTTagCompound extendedProperties = new NBTTagCompound();

    private ItemStack[] inventory = new ItemStack[Constants.INVENTORY_SIZE];
    private FluidTank leftTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
    private FluidTank rightTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);

    private BackpackTypes type = BackpackTypes.STANDARD;

    private boolean sleepingBagDeployed = false;
    private int sbx;
    private int sby;
    private int sbz;
    //private int sbDimension; //TODO?

    private boolean disableNVision = false;
    private boolean disableCycling = false;
    private int lastTime = 0;

    public InventoryBackpack(ItemStack backpack)
    {
        containerStack = backpack;
        detectAndConvertFromOldNBTFormat(containerStack.stackTagCompound);
        openInventory();
    }

    private void detectAndConvertFromOldNBTFormat(NBTTagCompound compound) // backwards compatibility
    {
        if (compound == null || !compound.hasKey("backpackData"))
            return;

        NBTTagCompound oldBackpackTag = compound.getCompoundTag("backpackData");
        NBTTagList oldItems = oldBackpackTag.getTagList("ABPItems", NBT.TAG_COMPOUND);
        leftTank.readFromNBT(oldBackpackTag.getCompoundTag(LEFT_TANK));
        rightTank.readFromNBT(oldBackpackTag.getCompoundTag(RIGHT_TANK));
        type = BackpackTypes.getType(oldBackpackTag.getString("colorName"));

        NBTTagCompound newBackpackTag = new NBTTagCompound();
        newBackpackTag.setTag(INVENTORY, oldItems);
        newBackpackTag.setTag(RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        newBackpackTag.setTag(LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        newBackpackTag.setByte("type", BackpackTypes.getMeta(type));

        compound.setTag(WEARABLE_TAG, newBackpackTag);
        compound.removeTag("backpackData");
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
    public BackpackTypes getType()
    {
        return type;
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
    public boolean isSleepingBagDeployed()
    {
        return sleepingBagDeployed;
    }

    public boolean deploySleepingBag(EntityPlayer player, World world, int meta, int cX, int cY, int cZ)
    {
        if (world.isRemote)
            return false;

        if (sleepingBagDeployed)
            removeSleepingBag(world);

        sleepingBagDeployed = CoordsUtils.spawnSleepingBag(player, world, meta, cX, cY, cZ);
        if (sleepingBagDeployed)
        {
            sbx = cX;
            sby = cY;
            sbz = cZ;
            markDirty();
        }
        return sleepingBagDeployed;
    }

    public void removeSleepingBag(World world)
    {
        if (this.sleepingBagDeployed)
        {
            if (world.getBlock(sbx, sby, sbz) == ModBlocks.blockSleepingBag)
                world.func_147480_a(sbx, sby, sbz, false);
        }
        this.sleepingBagDeployed = false;
        markDirty();
    }

    @Override
    public void setLastTime(int time)
    {
        this.lastTime = time;
    }

    @Override
    public void dirtyTime()
    {
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setInteger("lastTime", lastTime);
    }

    @Override
    public void dirtyExtended()
    {
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).removeTag("extendedProperties");
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setTag("extendedProperties", extendedProperties);
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack stack)
    {
        if (slot > inventory.length)
            return;

        inventory[slot] = stack;
        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }
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
            }
            else
            {
                stack = stack.splitStack(quantity);
            }
        }
        return stack;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if (compound == null)
            return; //this need for NEI trying to render tile.backpack and comes here w/o nbt

        NBTTagCompound backpackTag = compound.getCompoundTag(WEARABLE_TAG);
        type = BackpackTypes.getType(backpackTag.getByte("type"));
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
        extendedProperties = backpackTag.getCompoundTag("extendedProperties");
        sleepingBagDeployed = extendedProperties.getBoolean("sleepingbag");
        if (sleepingBagDeployed)
        {
            sbx = extendedProperties.getInteger("sbx");
            sby = extendedProperties.getInteger("sby");
            sbz = extendedProperties.getInteger("sbz");
        }
        disableCycling = backpackTag.getBoolean("disableCycling");
        disableNVision = backpackTag.getBoolean("disableNVision");
        lastTime = backpackTag.getInteger("lastTime");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound backpackTag = new NBTTagCompound();
        backpackTag.setByte("type", BackpackTypes.getMeta(type));
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
        backpackTag.setTag(RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag(LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag("extendedProperties", extendedProperties);
        extendedProperties.setBoolean("sleepingbag", sleepingBagDeployed);
        if (sleepingBagDeployed)
        {
            extendedProperties.setInteger("sbx", sbx);
            extendedProperties.setInteger("sby", sby);
            extendedProperties.setInteger("sbz", sbz);
        }
        backpackTag.setBoolean("disableCycling", disableCycling);
        backpackTag.setBoolean("disableNVision", disableNVision);
        backpackTag.setInteger("lastTime", lastTime);

        compound.setTag(WEARABLE_TAG, backpackTag);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{leftTank, rightTank};
    }

    @Override
    public boolean updateTankSlots()
    {
        boolean result = false;
        while (InventoryActions.transferContainerTank(this, getLeftTank(), BUCKET_IN_LEFT))
            result = true;
        while (InventoryActions.transferContainerTank(this, getRightTank(), BUCKET_IN_RIGHT))
            result = true;
        return result;
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
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).removeTag(INVENTORY);
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setTag(INVENTORY, items);
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setTag(LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag(WEARABLE_TAG).setTag(RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
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
            }
            else
            {
                itemstack = itemstack.splitStack(quantity);
            }
        }
        return itemstack;
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
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (slot > inventory.length)
            return;

        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
        dirtyInventory();
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