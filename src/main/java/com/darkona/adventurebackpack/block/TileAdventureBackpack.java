package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.widget.WidgetInventory;
import com.darkona.adventurebackpack.util.FluidMigration;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.common.layout.StorageIndex;
import com.darkona.adventurebackpack.inventory.HiddenTank;
import com.darkona.adventurebackpack.common.sort.SlotLocks;
import com.darkona.adventurebackpack.inventory.widget.WidgetStore;
import com.darkona.adventurebackpack.inventory.WidgetLayouts;
import com.darkona.adventurebackpack.inventory.BackpackNbt;
import com.darkona.adventurebackpack.inventory.InventoryActions;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.reference.BackpackNames;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

public class TileAdventureBackpack extends TileEntity implements IInventoryAdventureBackpack
{
    public ItemStack[] inventory;
    private int storage = StorageIndex.LEGACY_STORAGE;
    private WidgetLayout layout;
    private NBTTagList[] kept;
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
    private String placedFor;
    private boolean hasPlaced;
    private NBTTagCompound extendedProperties;
    private final SlotLocks locks = new SlotLocks();

    public int getLuminosity()
    {
        return luminosity;
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
        leftTank = new FluidTank(ConfigHandler.BACKPACK_TANK_CAPACITY);
        rightTank = new FluidTank(ConfigHandler.BACKPACK_TANK_CAPACITY);
        inventory = new ItemStack[StorageIndex.FIRST_EXTRA_STORAGE];
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
            world.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            return sleepingBagDeployed;
        }
        return false;
    }

    public boolean ownsSleepingBagAt(int footX, int footY, int footZ)
    {
        return sbx == footX && sby == footY && sbz == footZ;
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
    public int getStorageSize()
    {
        return storage;
    }

    @Override
    public WidgetLayout getLayout()
    {
        if (layout == null) layout = WidgetLayouts.widgets(colorName, storage);
        return layout;
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

    public void setColorName(String string)
    {
        this.colorName = string;
        layout = null;
    }

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
            colorName = backpackData.getString("colorName");
            storage = WidgetLayouts.storage(colorName, BackpackNbt.savedStorage(backpackData));
            layout = WidgetLayouts.widgets(colorName, storage);
            kept = WidgetStore.keptFor(layout, kept);
            inventory = WidgetStore.read(backpackData, layout, inventory, kept);
            FluidMigration.readTank(leftTank, backpackData.getCompoundTag("leftTank"));
            FluidMigration.readTank(rightTank, backpackData.getCompoundTag("rightTank"));
            HiddenTank.seal(leftTank, rightTank, WidgetInventory.showsTank(getLayout(), 1));
            lastTime = backpackData.getInteger("lastTime");
            special = backpackData.getBoolean("special");
            extendedProperties = BackpackNbt.readExtended(backpackData);
            BackpackNbt.readLocks(backpackData, locks);
        }
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound backpackData = new NBTTagCompound();
        WidgetStore.write(backpackData, getLayout(), inventory, kept, storage);
        backpackData.setString("colorName", colorName);
        backpackData.setInteger("lastTime", lastTime);
        backpackData.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        BackpackNbt.writeExtended(backpackData, extendedProperties);
        BackpackNbt.writeLocks(backpackData, locks);
        backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));

        WidgetStore.keepUnknown(compound.getCompoundTag("backpackData"), backpackData, getLayout());
        compound.setTag("backpackData",backpackData);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] tanks = {leftTank,rightTank};
        return tanks;
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
        markDirty();
    }

    private int limitFor(int slot)
    {
        return WidgetInventory.limit(widgets(), slot, getInventoryStackLimit());
    }

    private WidgetLayout widgets()
    {
        return getLayout();
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack.getItem() instanceof ItemAdventureBackpack || Block.getBlockFromItem(stack.getItem()) instanceof BlockAdventureBackpack)
        {
            return false;
        }
        return WidgetInventory.accepts(widgets(), slot, stack);
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
        return WidgetInventory.dropsOnClose(widgets(), slot) ? inventory[slot] : null;
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack itemstack)
    {
        inventory[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > limitFor(i))
        {
            itemstack.stackSize = limitFor(i);
        }
        markDirty();
    }

    @Override
    public void markDirty()
    {
        WidgetInventory.fillTanks(this, getLayout());
        super.markDirty();
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack itemstack)
    {
        if (slot > inventory.length) return;
        inventory[slot] = itemstack;
        if (itemstack != null && itemstack.stackSize > limitFor(slot))
        {
            itemstack.stackSize = limitFor(slot);
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
        WidgetInventory.consume(this, widgets(), item);
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void updateEntity()
    {
        if (colorName != placedFor)
        {
            placedFor = colorName;
            hasPlaced = !colorName.isEmpty() && BackpackAbilities.hasPlacedAbility(colorName);
        }
        if (hasPlaced)
        {
            BackpackAbilities.backpackAbilities.executeAbility(null, this.worldObj, this);
        }

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
            if (sleepingBagDeployed && worldObj.blockExists(sbx, sby, sbz)
                    && worldObj.getBlock(sbx, sby, sbz) != ModBlocks.blockSleepingBag)
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
            FluidMigration.readTank(leftTank, backpackData.getCompoundTag("leftTank"));
            FluidMigration.readTank(rightTank, backpackData.getCompoundTag("rightTank"));
            HiddenTank.seal(leftTank, rightTank, WidgetInventory.showsTank(getLayout(), 1));
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
        stack.setItemDamage(BackpackNames.getBackpackDamageFromName(colorName));
        return stack;
    }

    public boolean equip(World world, EntityPlayer player, int x, int y, int z)
    {
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        transferToItemStack(stacky);
        removeSleepingBag(world);
        return PlacedWearables.equipOrGive(world, player, x, y, z, stacky);
    }

    public boolean drop(World world, EntityPlayer player, int x, int y, int z)
    {
        removeSleepingBag(world);
        if (player.capabilities.isCreativeMode) return true;
        ItemStack stacky = new ItemStack(ModItems.adventureBackpack, 1);
        transferToItemStack(stacky);
        return PlacedWearables.spawnItem(world, x, y, z, stacky);
    }

    @Override
    public boolean updateTankSlots()
    {
        return false;
    }

    public void dirtyTanks()
    {
    }

    public void dirtyTime()
    {
    }

    public void dirtyExtended()
    {
    }

    public void dirtyInventory()
    {
    }

    @Override
    public SlotLocks getLocks()
    {
        return locks;
    }

    @Override
    public void dirtyLocks()
    {
        markDirty();
    }
}
