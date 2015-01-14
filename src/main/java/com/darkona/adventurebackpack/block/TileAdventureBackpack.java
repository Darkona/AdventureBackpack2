package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.inventory.IAsynchronousInventory;
import com.darkona.adventurebackpack.inventory.IInventoryTanks;
import com.darkona.adventurebackpack.inventory.InventoryActions;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.BackpackUtils;
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
public class TileAdventureBackpack extends TileEntity implements IAdvBackpack, IAsynchronousInventory, IInventoryTanks
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

    public int getLuminosity()
    {
        return luminosity;
    }

    public void setLuminosity(int luminosity)
    {
        this.luminosity = luminosity;
    }

    public int getLastTime()
    {
        return lastTime;
    }

    public void setLastTime(int lastTime)
    {
        this.lastTime = lastTime;
    }

    public NBTTagCompound getExtendedProperties()
    {
        return extendedProperties;
    }

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
                //LogHelper.info("removeSleepingBag() ==> SleepingBagDeployed is:" + sleepingBagDeployed);
                saveChanges();
                return true;
            }
        } else
        {
            this.sleepingBagDeployed = false;
            saveChanges();
        }
        return false;
    }


    //=====================================================GETTERS====================================================//

    public String getColorName()
    {
        return this.colorName;
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

    public void setLeftTank(FluidTank leftTank)
    {
        this.leftTank = leftTank;
        markDirty();
    }

    public void setRightTank(FluidTank rightTank)
    {
        this.rightTank = rightTank;
        markDirty();
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

    public boolean isSBDeployed()
    {
        return this.sleepingBagDeployed;
    }

    public boolean isSpecial()
    {
        return special;
    }

    //=======================================================NBT======================================================//
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        sleepingBagDeployed = compound.getBoolean("sleepingbag");
        sbx = compound.getInteger("sbx");
        sby = compound.getInteger("sby");
        sbz = compound.getInteger("sbz");
        sbdir = compound.getInteger("sbdir");
        luminosity = compound.getInteger("lumen");
        special = compound.getBoolean("special");
        loadFromNBT(compound);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
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
        compound.setBoolean("sleepingbag", sleepingBagDeployed);
        compound.setInteger("sbx", sbx);
        compound.setInteger("sby", sby);
        compound.setInteger("sbz", sbz);
        compound.setInteger("lumen", luminosity);
        compound.setInteger("sbdir", sbdir);
        compound.setString("colorName", colorName);
        compound.setTag("rightTank", rightTank.writeToNBT(tankRight));
        compound.setTag("leftTank", leftTank.writeToNBT(tankLeft));
        compound.setInteger("lastTime", lastTime);
        compound.setBoolean("special", BackpackAbilities.hasAbility(colorName));

        compound.setTag("extended", extendedProperties);
        super.writeToNBT(compound);
    }

    public void loadFromNBT(NBTTagCompound compound)
    {
        if (compound != null)
        {
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
            colorName = compound.getString("colorName");
            lastTime = compound.getInteger("lastTime");
            extendedProperties = compound.getCompoundTag("extended");
        }
    }

    @Override
    public NBTTagCompound writeToNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return compound;
    }

    @Override
    public void readFromNBT()
    {

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
        onInventoryChanged();
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
        onInventoryChanged();
    }

    public void onInventoryChanged()
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
        markDirty();
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
    public void saveChanges()
    {
        markDirty();
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
        InventoryActions.consumeItemInBackpack(this, item);
    }

    //===================================================TILE ENTITY==================================================//

    //SEND SYNC PACKET
    @Override
    public Packet getDescriptionPacket()
    {
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, writeToNBT());
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
    public void saveTanks()
    {

    }

    @Override
    public void loadTanks()
    {

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

    //=====================================================BACKPACK===================================================//
    public boolean equip(World world, EntityPlayer player, int x, int y, int z)
    {
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        stacky.setTagCompound(this.writeToNBT());
        removeSleepingBag(world);
        if (BackpackUtils.equipWearable(stacky, player) != BackpackUtils.reasons.SUCCESFUL)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:already.equipped"));
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
        stacky.setTagCompound(this.writeToNBT());

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
}
