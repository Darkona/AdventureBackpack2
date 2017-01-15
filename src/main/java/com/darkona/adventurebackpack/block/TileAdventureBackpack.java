package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.InventoryActions;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.Wearing;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created by Darkona on 12/10/2014.
 */
public class TileAdventureBackpack extends TileEntity implements IInventoryAdventureBackpack
{

    public ItemStack[] inventory;
    private FluidTank leftTank;
    private FluidTank rightTank;
    public boolean sleepingBagDeployed;
    private boolean special;
    private int sbdir;
    private int sbx;
    private int sby;
    private int sbz;
    private int checkTime = 0;
    private String colorName;
    private int lastTime;
    private int luminosity;
    private NBTTagCompound extendedProperties;
    private boolean cyclingStatus;
    private boolean disableNightVision;


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

    @Override
    public NBTTagCompound getExtendedProperties()
    {
        return extendedProperties;
    }

    @Override
    public void setExtendedProperties(NBTTagCompound extendedProperties)
    {
        this.extendedProperties = extendedProperties;
    }

    public TileAdventureBackpack()
    {
        leftTank = new FluidTank(Constants.basicTankCapacity);
        rightTank = new FluidTank(Constants.basicTankCapacity);
        inventory = new ItemStack[Constants.inventorySize];
        sleepingBagDeployed = false;
        setColorName("Standard");
        luminosity = 0;
        lastTime = 0;
        checkTime = 0;
        extendedProperties = new NBTTagCompound();
    }

    public boolean deploySleepingBag(EntityPlayer player, World world, int x, int y, int z, int meta)
    {
        if (world.isRemote) return false;
        Block sleepingBag = ModBlocks.blockSleepingBag;
        if (world.setBlock(x, y, z, sleepingBag, meta, 3))
        {
            world.playSoundAtEntity(player, Block.soundTypeCloth.func_150496_b(), 0.5f, 1.0f);
            sbx = x;
            sby = y;
            sbz = z;
            sbdir = meta;
            switch (meta & 3)
            {
                case 0:
                    ++z;
                    break;
                case 1:
                    --x;
                    break;
                case 2:
                    --z;
                    break;
                case 3:
                    ++x;
                    break;
            }
            sleepingBagDeployed = world.setBlock(x, y, z, sleepingBag, meta + 8, 3);
            //LogHelper.info("deploySleepingBag() => SleepingBagDeployed is: " + sleepingBagDeployed);
            world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            return sleepingBagDeployed;
        }
        return false;
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
        } else
        {
            this.sleepingBagDeployed = false;
            markDirty();
        }
        return false;
    }

    //=====================================================GETTERS====================================================//

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
        return null;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
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

    //=====================================================SETTERS====================================================//


    public void setColorName(String string)
    {
        this.colorName = string;
    }

    //=====================================================BOOLEANS===================================================//
    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    @Override
    public boolean isSBDeployed()
    {
        return this.sleepingBagDeployed;
    }

    @Override
    public boolean isSpecial()
    {
        return special;
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
        compound.setInteger("lumen", luminosity);
        compound.setInteger("sbdir", sbdir);
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
            extendedProperties = backpackData.getCompoundTag("extended");
            cyclingStatus = backpackData.getBoolean("cyclingStatus");
            disableNightVision = backpackData.getBoolean("disableNightVision");
        }
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
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
        backpackData.setTag("ABPItems", items);
        backpackData.setString("colorName", colorName);
        backpackData.setInteger("lastTime", lastTime);
        backpackData.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        backpackData.setTag("extended", extendedProperties);
        backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
        backpackData.setBoolean("cyclingStatus", cyclingStatus);
        backpackData.setBoolean("disableNightVision", disableNightVision);

        compound.setTag("backpackData",backpackData);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] tanks = {leftTank,rightTank};
        return tanks;
    }

    //====================================================INVENTORY===================================================//
    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
        markDirty();
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

    @Override
    public ItemStack decrStackSize(int i, int count)
    {
        ItemStack itemstack = getStackInSlot(i);

        if (itemstack != null)
        {
            if (itemstack.stackSize <= count)
            {
                setInventorySlotContents(i, null);
            } else
            {
                itemstack = itemstack.splitStack(count);
            }
        }
        markDirty();
        return itemstack;
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
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {

        inventory[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public void markDirty()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (i == Constants.bucketInLeft && inventory[i] != null)
            {
                updateTankSlots(getLeftTank(), i);
            }

            if (i == Constants.bucketInRight && inventory[i] != null)
            {
                updateTankSlots(getRightTank(), i);
            }
        }
        super.markDirty();
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack itemstack)
    {
        if (slot > inventory.length) return;
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > getInventoryStackLimit())
        {
            itemstack.stackSize = getInventoryStackLimit();
        }
    }

    @Override
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        ItemStack stack = getStackInSlot(slot);

        if (stack != null)
        {
            if (stack.stackSize <= amount)
            {
                setInventorySlotContentsNoSave(slot, null);
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
        if (isSpecial() && !colorName.isEmpty())
        {
            BackpackAbilities.backpackAbilities.executeAbility(null, this.worldObj, this);
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
        } else
        {
            checkTime--;
        }
    }

    public void updateTankSlots(FluidTank tank, int slotIn)
    {
        InventoryActions.transferContainerTank(this, tank, slotIn);
    }

    @Override
    public void saveTanks(NBTTagCompound compound)
    {
        NBTTagCompound backpackData;
        if(compound.hasKey("backpackData"))
        {
            backpackData = compound.getCompoundTag("backpackData");
        }else{
            backpackData = new NBTTagCompound();
        }
        backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
        compound.setTag("backpackData",backpackData);
    }

    @Override
    public void loadTanks(NBTTagCompound compound)
    {
        if(compound.hasKey("backpackData"))
        {
            NBTTagCompound backpackData = compound.getCompoundTag("backpackData");
            leftTank.readFromNBT(backpackData.getCompoundTag("leftTank"));
            rightTank.readFromNBT(backpackData.getCompoundTag("rightTank"));
        }
    }

    @Override
    public TileAdventureBackpack getTile()
    {
        return this;
    }

    @Override
    public ItemStack getParentItemStack()
    {
        return null;
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
        if (BackpackUtils.equipWearable(stacky, player) != BackpackUtils.reasons.SUCCESFUL)
        {
            if (Wearing.isWearingWearable(player))
            {
                if (Wearing.isWearingBackpack(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.backpack"));
                } else if (Wearing.isWearingCopter(player))
                {
                    player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.already.equipped.copterpack"));
                } else if (Wearing.isWearingJetpack(player))
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
    public boolean updateTankSlots()
    {
        return false;
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
}
