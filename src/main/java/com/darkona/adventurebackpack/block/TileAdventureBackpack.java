package com.darkona.adventurebackpack.block;

import javax.annotation.Nullable;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.inventory.InventoryActions;
import com.darkona.adventurebackpack.inventory.SlotBackpack;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.reference.GeneralReference;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.CoordsUtils;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;

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
import static com.darkona.adventurebackpack.common.Constants.TOOL_LOWER;
import static com.darkona.adventurebackpack.common.Constants.TOOL_UPPER;

/**
 * Created by Darkona on 12/10/2014.
 */
public class TileAdventureBackpack extends TileEntity implements IInventoryAdventureBackpack, ISidedInventory
{
    private BackpackTypes type = BackpackTypes.STANDARD;
    private ItemStack[] inventory = new ItemStack[Constants.INVENTORY_SIZE];
    private FluidTank leftTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
    private FluidTank rightTank = new FluidTank(Constants.BASIC_TANK_CAPACITY);
    private NBTTagCompound extendedProperties = new NBTTagCompound();

    private NBTTagList ench;
    private boolean disableCycling;
    private boolean disableNVision;
    private int lastTime = 0;

    private boolean sleepingBagDeployed;
    private int sbdir;
    private int sbx;
    private int sby;
    private int sbz;
    private int luminosity = 0;

    private int checkTime = 0;

    public TileAdventureBackpack()
    {
        sleepingBagDeployed = false;
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

    public int getLuminosity()
    {
        return luminosity;
    }

    @Override
    public int getLastTime()
    {
        return lastTime;
    }

    @Override
    public void setLastTime(int lastTime)
    {
        this.lastTime = lastTime;
    }

    public boolean deploySleepingBag(EntityPlayer player, World world, int meta, int cX, int cY, int cZ)
    {
        if (world.isRemote)
            return false;

        sleepingBagDeployed = CoordsUtils.spawnSleepingBag(player, world, meta, cX, cY, cZ);
        if (sleepingBagDeployed)
        {
            sbx = cX;
            sby = cY;
            sbz = cZ;
            sbdir = meta;
            world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        return sleepingBagDeployed;
    }

    public void setSleepingBagDeployed(boolean state)
    {
        this.sleepingBagDeployed = state;
    }

    public boolean removeSleepingBag(World world)
    {
        if (sleepingBagDeployed)
        {
            if (world.getBlock(sbx, sby, sbz) == ModBlocks.blockSleepingBag)
            {
                world.func_147480_a(sbx, sby, sbz, false);
                this.sleepingBagDeployed = false;
                markDirty();
                return true;
            }
        }
        else
        {
            this.sleepingBagDeployed = false;
            markDirty();
        }
        return false;
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
    public String getInventoryName()
    {
        return "";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return getInventoryName() != null && !getInventoryName().isEmpty();
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }


    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public boolean isSleepingBagDeployed()
    {
        return this.sleepingBagDeployed;
    }

    //=======================================================NBT======================================================//
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        loadFromNBT(compound);
        sleepingBagDeployed = compound.getBoolean("sleepingbag");
        sbx = compound.getInteger("sbx");
        sby = compound.getInteger("sby");
        sbz = compound.getInteger("sbz");
        sbdir = compound.getInteger("sbdir");
        luminosity = compound.getInteger("lumen");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        saveToNBT(compound);
        compound.setBoolean("sleepingbag", sleepingBagDeployed);
        compound.setInteger("sbx", sbx);
        compound.setInteger("sby", sby);
        compound.setInteger("sbz", sbz);
        compound.setInteger("sbdir", sbdir);
        compound.setInteger("lumen", luminosity);
    }

    private void convertFromOldNBTFormat(NBTTagCompound compound) // backwards compatibility
    {
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

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey("backpackData"))
            convertFromOldNBTFormat(compound);

        if (compound.hasKey("ench"))
            ench = compound.getTagList("ench", NBT.TAG_COMPOUND);

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
        disableCycling = backpackTag.getBoolean(TAG_DISABLE_CYCLING);
        disableNVision = backpackTag.getBoolean(TAG_DISABLE_NVISION);
        lastTime = backpackTag.getInteger("lastTime");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        if (ench != null)
            compound.setTag("ench", ench);

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
        backpackTag.setTag(TAG_INVENTORY, items);
        backpackTag.setTag(TAG_RIGHT_TANK, rightTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag(TAG_LEFT_TANK, leftTank.writeToNBT(new NBTTagCompound()));
        backpackTag.setTag(TAG_EXTENDED_COMPOUND, extendedProperties);
        backpackTag.setBoolean(TAG_DISABLE_CYCLING, disableCycling);
        backpackTag.setBoolean(TAG_DISABLE_NVISION, disableNVision);
        backpackTag.setInteger("lastTime", lastTime);

        compound.setTag(TAG_WEARABLE_COMPOUND, backpackTag);
    }

    //====================================================INVENTORY===================================================//
    @Override
    public void openInventory()
    {
        //
    }

    @Override
    public void closeInventory()
    {
        markDirty();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (slot <= Constants.END_OF_INVENTORY)
        {
            return SlotBackpack.isValidItem(stack);
        }
        return (slot == TOOL_UPPER || slot == TOOL_LOWER) && SlotTool.isValidTool(stack);
    }

    @Nullable
    @Override
    public ItemStack decrStackSize(int slot, int quantity)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= quantity)
                setInventorySlotContents(slot, null);
            else
                stack = stack.splitStack(quantity);
        }
        markDirty();
        return stack;
    }


    @Override
    public void setInventorySlotContents(int slot, @Nullable ItemStack stack)
    {
        setInventorySlotContentsNoSave(slot, stack);
        markDirty();
    }

    @Nullable
    @Override
    public ItemStack decrStackSizeNoSave(int slot, int quantity)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null)
        {
            if (stack.stackSize <= quantity)
                setInventorySlotContentsNoSave(slot, null);
            else
                stack = stack.splitStack(quantity);
        }
        return stack;
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, @Nullable ItemStack stack)
    {
        if (slot >= getSizeInventory())
            return;

        if (stack != null)
        {
            if (stack.stackSize > getInventoryStackLimit())
                stack.stackSize = getInventoryStackLimit();

            if (stack.stackSize == 0)
                stack = null;
        }

        getInventory()[slot] = stack;
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
    public void markDirty()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (inventory[i] != null)
            {
                if ((i == BUCKET_IN_LEFT || i == BUCKET_IN_RIGHT)
                        || (i == BUCKET_OUT_LEFT || i == BUCKET_OUT_RIGHT) && inventory[i].getItem() instanceof ItemHose)
                {
                    updateTankSlots();
                }
            }
        }
        super.markDirty();
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
    public boolean hasItem(Item item)
    {
        return InventoryActions.hasItem(this, item);
    }

    @Override
    public void consumeInventoryItem(Item item)
    {
        InventoryActions.consumeItemInInventory(this, item);
    }

    //===================================================TILE ENTITY==================================================//

    //SEND SYNC PACKET
    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, compound);
    }

    //RECEIV SYNC PACKET - This is necessary for the TileEntity to load the nbt as soon as it is loaded and be rendered
    //properly when the custom renderer reads it
    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void updateEntity()
    {
        //Execute this backpack's ability. No, seriously. You might not infer that from the code. Just sayin'
        if (ConfigHandler.backpackAbilities && BackpackTypes.hasProperty(type, BackpackTypes.Props.TILE))
        {
            BackpackAbilities.backpackAbilities.executeTileAbility(this.worldObj, this);
        }

        //Check for backpack luminosity and a deployed sleeping bag, just in case because i'm super paranoid.
        if (checkTime == 0)
        {
            int lastLumen = luminosity;
            int left = (leftTank.getFluid() != null) ? leftTank.getFluid().getFluid().getLuminosity() : 0;
            int right = (rightTank.getFluid() != null) ? rightTank.getFluid().getFluid().getLuminosity() : 0;
            luminosity = Math.max(left, right);
            if (luminosity != lastLumen)
            {
                int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
                worldObj.setBlock(xCoord, yCoord, zCoord, ModBlocks.blockBackpack, meta, 3);
                worldObj.setLightValue(EnumSkyBlock.Block, xCoord, yCoord, zCoord, luminosity);
            }
            if (worldObj.getBlock(sbx, sby, sbz) != ModBlocks.blockSleepingBag)
            {
                sleepingBagDeployed = false;
            }
            checkTime = 20;
        }
        else
        {
            checkTime--;
        }
    }

    private ItemStack transferToItemStack(ItemStack stack)
    {
        NBTTagCompound compound = new NBTTagCompound();
        saveToNBT(compound);
        stack.setTagCompound(compound);
        return stack;
    }

    //=====================================================BACKPACK===================================================//
    public boolean equip(World world, EntityPlayer player, int x, int y, int z)
    {
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        transferToItemStack(stacky);
        removeSleepingBag(world);
        if (BackpackUtils.equipWearable(stacky, player) != BackpackUtils.Reasons.SUCCESSFUL)
        {
            if (Wearing.isWearingWearable(player))
            {
                if (Wearing.isWearingBackpack(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.backpack"));
                }
                else if (Wearing.isWearingCopter(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.copterpack"));
                }
                else if (Wearing.isWearingJetpack(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.jetpack"));
                }
            }
            if (!player.inventory.addItemStackToInventory(stacky))
            {
                return drop(world, player, x, y, z);
            }
        }
        return true;
    }

    public boolean drop(World world, EntityPlayer player, int x, int y, int z)
    {
        removeSleepingBag(world);
        if (player.capabilities.isCreativeMode) return true;
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        transferToItemStack(stacky);
        float spawnX = x + world.rand.nextFloat();
        float spawnY = y + world.rand.nextFloat();
        float spawnZ = z + world.rand.nextFloat();

        EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stacky);

        float mult = 0.05F;

        droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * mult;
        droppedItem.motionY = (4 + world.rand.nextFloat()) * mult;
        droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * mult;

        return world.spawnEntityInWorld(droppedItem);
    }

    @Override
    public void dirtyTanks()
    {

    }

    @Override
    public void dirtyTime()
    {

    }

    @Override
    public void dirtyExtended()
    {

    }

    @Override
    public void dirtyInventory()
    {

    }

    //=================================================ISidedInventory================================================//

    private static final int[] SLOTS = Utils.createSlotArray(0, Constants.INVENTORY_MAIN_SIZE);

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (GeneralReference.isDimensionAllowed(this.worldObj.provider.dimensionId))
        {
            return SLOTS;
        }
        return null;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack item, int side)
    {
        return this.isItemValidForSlot(slot, item);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack item, int side)
    {
        return true;
    }
}