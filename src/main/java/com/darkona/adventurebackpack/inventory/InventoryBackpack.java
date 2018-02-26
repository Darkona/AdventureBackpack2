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

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.util.CoordsUtils;

import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.TAG_DISABLE_CYCLING;
import static com.darkona.adventurebackpack.common.Constants.TAG_DISABLE_NVISION;
import static com.darkona.adventurebackpack.common.Constants.TAG_EXTENDED_COMPOUND;
import static com.darkona.adventurebackpack.common.Constants.TAG_INVENTORY;
import static com.darkona.adventurebackpack.common.Constants.TAG_LEFT_TANK;
import static com.darkona.adventurebackpack.common.Constants.TAG_RIGHT_TANK;
import static com.darkona.adventurebackpack.common.Constants.TAG_TYPE;
import static com.darkona.adventurebackpack.common.Constants.TAG_WEARABLE_COMPOUND;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class InventoryBackpack extends InventoryAdventure implements IInventoryBackpack
{
    private static final String TAG_IS_SLEEPING_BAG = "sleepingBag";
    private static final String TAG_SLEEPING_BAG_X = "sleepingBagX";
    private static final String TAG_SLEEPING_BAG_Y = "sleepingBagY";
    private static final String TAG_SLEEPING_BAG_Z = "sleepingBagZ";

    private BackpackTypes type = BackpackTypes.STANDARD;
    private ItemStack[] inventory = new ItemStack[Constants.INVENTORY_SIZE];
    private FluidTank leftTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
    private FluidTank rightTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
    private NBTTagCompound extendedProperties = new NBTTagCompound();

    private boolean sleepingBagDeployed = false;
    private int sleepingBagX;
    private int sleepingBagY;
    private int sleepingBagZ;

    private boolean disableNVision = false;
    private boolean disableCycling = false;
    private int lastTime = 0;

    public InventoryBackpack(ItemStack backpack)
    {
        containerStack = backpack;
        detectAndConvertFromOldNBTFormat(containerStack.stackTagCompound);
        openInventory();
    }

    @Override
    public BackpackTypes getType()
    {
        return type;
    }

    @Override
    public ItemStack[] getInventory()
    {
        return inventory;
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
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{leftTank, rightTank};
    }

    @Override
    public int[] getSlotsOnClosingArray()
    {
        return new int[]{BUCKET_IN_LEFT, BUCKET_IN_RIGHT, BUCKET_OUT_LEFT, BUCKET_OUT_RIGHT};
    }

    @Override
    public NBTTagCompound getExtendedProperties()
    {
        return extendedProperties;
    }



    @Override
    public int getLastTime()
    {
        return this.lastTime;
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
            sleepingBagX = cX;
            sleepingBagY = cY;
            sleepingBagZ = cZ;
            markDirty();
        }
        return sleepingBagDeployed;
    }

    public void removeSleepingBag(World world)
    {
        if (this.sleepingBagDeployed)
        {
            if (world.getBlock(sleepingBagX, sleepingBagY, sleepingBagZ) == ModBlocks.blockSleepingBag)
                world.func_147480_a(sleepingBagX, sleepingBagY, sleepingBagZ, false);
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
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).setInteger("lastTime", lastTime);
    }

    @Override
    public void dirtyExtended() //TODO is it redundant?
    {
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).removeTag(TAG_EXTENDED_COMPOUND);
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).setTag(TAG_EXTENDED_COMPOUND, extendedProperties);
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if (compound == null)
            return; //this need for NEI trying to render tile.backpack and comes here w/o nbt

        NBTTagCompound backpackTag = compound.getCompoundTag(TAG_WEARABLE_COMPOUND);
        type = BackpackTypes.getType(backpackTag.getByte(TAG_TYPE));
        NBTTagList items = backpackTag.getTagList(TAG_INVENTORY, NBT.TAG_COMPOUND);
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            byte slot = item.getByte("Slot");
            if (slot >= 0 && slot < inventory.length)
            {
                inventory[slot] = ItemStack.loadItemStackFromNBT(item);
            }
        }
        leftTank.readFromNBT(backpackTag.getCompoundTag(TAG_LEFT_TANK));
        rightTank.readFromNBT(backpackTag.getCompoundTag(TAG_RIGHT_TANK));
        extendedProperties = backpackTag.getCompoundTag(TAG_EXTENDED_COMPOUND);
        {
            sleepingBagDeployed = extendedProperties.getBoolean(TAG_IS_SLEEPING_BAG);
            if (sleepingBagDeployed)
            {
                sleepingBagX = extendedProperties.getInteger(TAG_SLEEPING_BAG_X);
                sleepingBagY = extendedProperties.getInteger(TAG_SLEEPING_BAG_Y);
                sleepingBagZ = extendedProperties.getInteger(TAG_SLEEPING_BAG_Z);
            }
        }
        disableCycling = backpackTag.getBoolean(TAG_DISABLE_CYCLING);
        disableNVision = backpackTag.getBoolean(TAG_DISABLE_NVISION);
        lastTime = backpackTag.getInteger("lastTime");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound backpackTag = new NBTTagCompound();
        backpackTag.setByte(TAG_TYPE, BackpackTypes.getMeta(type));
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
        backpackTag.removeTag(TAG_INVENTORY);
        backpackTag.setTag(TAG_INVENTORY, items);
        backpackTag.setTag(TAG_RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag(TAG_LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag(TAG_EXTENDED_COMPOUND, extendedProperties);
        {
            if (sleepingBagDeployed)
            {
                extendedProperties.setBoolean(TAG_IS_SLEEPING_BAG, sleepingBagDeployed);
                extendedProperties.setInteger(TAG_SLEEPING_BAG_X, sleepingBagX);
                extendedProperties.setInteger(TAG_SLEEPING_BAG_Y, sleepingBagY);
                extendedProperties.setInteger(TAG_SLEEPING_BAG_Z, sleepingBagZ);
            }
            else
            {
                extendedProperties.removeTag(TAG_IS_SLEEPING_BAG);
                extendedProperties.removeTag(TAG_SLEEPING_BAG_X);
                extendedProperties.removeTag(TAG_SLEEPING_BAG_Y);
                extendedProperties.removeTag(TAG_SLEEPING_BAG_Z);
            }
        }
        backpackTag.setBoolean(TAG_DISABLE_CYCLING, disableCycling);
        backpackTag.setBoolean(TAG_DISABLE_NVISION, disableNVision);
        backpackTag.setInteger("lastTime", lastTime);

        compound.setTag(TAG_WEARABLE_COMPOUND, backpackTag);
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
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).removeTag(TAG_INVENTORY);
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).setTag(TAG_INVENTORY, items);
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).setTag(TAG_LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag(TAG_WEARABLE_COMPOUND).setTag(TAG_RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
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

    private void detectAndConvertFromOldNBTFormat(NBTTagCompound compound) // backwards compatibility
    {
        if (compound == null || !compound.hasKey("backpackData"))
            return;

        NBTTagCompound oldBackpackTag = compound.getCompoundTag("backpackData");
        NBTTagList oldItems = oldBackpackTag.getTagList("ABPItems", NBT.TAG_COMPOUND);
        leftTank.readFromNBT(oldBackpackTag.getCompoundTag("leftTank"));
        rightTank.readFromNBT(oldBackpackTag.getCompoundTag("rightTank"));
        type = BackpackTypes.getType(oldBackpackTag.getString("colorName"));

        NBTTagCompound newBackpackTag = new NBTTagCompound();
        newBackpackTag.setTag(TAG_INVENTORY, oldItems);
        newBackpackTag.setTag(TAG_RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        newBackpackTag.setTag(TAG_LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        newBackpackTag.setByte(TAG_TYPE, BackpackTypes.getMeta(type));

        compound.setTag(TAG_WEARABLE_COMPOUND, newBackpackTag);
        compound.removeTag("backpackData");
    }
}