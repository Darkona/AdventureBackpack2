package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.util.FluidMigration;
import com.darkona.adventurebackpack.block.BlockAdventureBackpack;
import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidTank;

public class InventoryBackpack implements IInventoryAdventureBackpack
{
    public ItemStack[] inventory = new ItemStack[Constants.inventorySize];
    private int storage = BackpackLayout.LEGACY_STORAGE;
    private int quiver;
    private NBTTagList keptQuiver;
    private FluidTank leftTank = new FluidTank(ConfigHandler.BACKPACK_TANK_CAPACITY);
    private FluidTank rightTank = new FluidTank(ConfigHandler.BACKPACK_TANK_CAPACITY);

    public ItemStack getContainerStack()
    {
        return containerStack;
    }

    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
    }

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
            storage = BackpackLayouts.storage(colorName, 0);
            inventory = new ItemStack[BackpackLayout.inventorySize(storage)];
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
    public int getStorageSize()
    {
        return storage;
    }

    @Override
    public int getQuiverSlots()
    {
        return quiver;
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
        FluidMigration.readTank(leftTank, compound.getCompoundTag("leftTank"));
        FluidMigration.readTank(rightTank, compound.getCompoundTag("rightTank"));
        Quiver.sealRightTank(leftTank, rightTank, quiver > 0);
    }

    @Override
    public boolean hasItem(Item item)
    {
        return InventoryActions.hasItem(this, item);
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
        if (slot < 0 || slot >= inventory.length) return;
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
        if (slot < 0 || slot >= inventory.length) return;
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
        return InventoryActions.transferContainerTank(this, getLeftTank(), Constants.bucketInLeft) ||
                InventoryActions.transferContainerTank(this, getRightTank(), Constants.bucketInRight);
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if(compound.hasKey("backpackData"))
        {
            NBTTagCompound backpackData = compound.getCompoundTag("backpackData");
            colorName = backpackData.getString("colorName");
            storage = BackpackLayouts.storage(colorName, BackpackNbt.savedStorage(backpackData));
            quiver = BackpackLayouts.quiver(colorName);
            inventory = BackpackNbt.readItems(backpackData, inventory, BackpackLayout.inventorySize(storage, quiver));
            keptQuiver = BackpackNbt.readQuiver(backpackData, inventory, BackpackLayout.quiverIndex(storage, 0), quiver);

            FluidMigration.readTank(leftTank, backpackData.getCompoundTag("leftTank"));
            FluidMigration.readTank(rightTank, backpackData.getCompoundTag("rightTank"));
            if (Quiver.sealRightTank(leftTank, rightTank, quiver > 0))
            {
                backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
                backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
            }
            lastTime = backpackData.getInteger("lastTime");
            special = backpackData.getBoolean("special");
            extendedProperties = BackpackNbt.readExtended(backpackData);
        }
        if (compound == containerStack.stackTagCompound) rememberTags();
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound backpackData = new NBTTagCompound();
        writeItemsAndQuiver(backpackData);
        backpackData.setString("colorName", colorName);
        backpackData.setInteger("lastTime", lastTime);
        backpackData.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        BackpackNbt.writeExtended(backpackData, extendedProperties);
        backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));

        compound.setTag("backpackData",backpackData);
        if (compound == containerStack.stackTagCompound) rememberTags();
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] array = {leftTank,rightTank};
        return array;
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
        saveToNBT(containerStack.stackTagCompound);
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
            return (quiver == 0 || slot == Constants.bucketInLeft) && FluidContainerRegistry.isContainer(stack);
        }
        if (slot >= BackpackLayout.quiverIndex(storage, 0))
        {
            return Quiver.accepts(stack);
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
        boolean current = isCurrent();
        containerStack.stackTagCompound.getCompoundTag("backpackData").setTag("leftTank",leftTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag("backpackData").setTag("rightTank",rightTank.writeToNBT(new NBTTagCompound()));
        if (current) rememberTags();
    }

    public void dirtyTime()
    {
        boolean current = isCurrent();
        containerStack.stackTagCompound.getCompoundTag("backpackData").setInteger("lastTime",lastTime);
        if (current) rememberTags();
    }

    public void dirtyExtended()
    {
        boolean current = isCurrent();
        BackpackNbt.writeExtended(containerStack.stackTagCompound.getCompoundTag("backpackData"), extendedProperties);
        if (current) rememberTags();
    }

    public void dirtyInventory()
    {
        boolean current = isCurrent();
        if(updateTankSlots()){
            dirtyTanks();
        }
        writeItemsAndQuiver(containerStack.stackTagCompound.getCompoundTag("backpackData"));
        if (current) rememberTags();
    }

    private static final String[] READ_KEYS = {BackpackNbt.ITEMS, BackpackNbt.STORAGE_SIZE, BackpackNbt.QUIVER, BackpackNbt.QUIVER_FILL,
            "colorName", "lastTime", "special", BackpackNbt.EXTENDED, BackpackNbt.LEGACY_EXTENDED, "leftTank", "rightTank"};
    private NBTTagCompound tagRef;
    private NBTBase dataRef;
    private final NBTBase[] keyRefs = new NBTBase[READ_KEYS.length];

    public boolean isCurrent()
    {
        NBTTagCompound tag = containerStack.stackTagCompound;
        if (tag == null || tag != tagRef) return false;
        NBTBase data = tag.getTag("backpackData");
        if (data != dataRef) return false;
        if (!(data instanceof NBTTagCompound)) return true;
        NBTTagCompound d = (NBTTagCompound) data;
        for (int i = 0; i < READ_KEYS.length; i++)
        {
            if (d.getTag(READ_KEYS[i]) != keyRefs[i]) return false;
        }
        return true;
    }

    public void reloadIfChanged()
    {
        if (!isCurrent()) openInventory();
    }

    private void rememberTags()
    {
        NBTTagCompound tag = containerStack.stackTagCompound;
        tagRef = tag;
        NBTBase data = tag == null ? null : tag.getTag("backpackData");
        dataRef = data;
        NBTTagCompound d = data instanceof NBTTagCompound ? (NBTTagCompound) data : null;
        for (int i = 0; i < READ_KEYS.length; i++)
        {
            keyRefs[i] = d == null ? null : d.getTag(READ_KEYS[i]);
        }
    }

    private void writeItemsAndQuiver(NBTTagCompound backpackData)
    {
        int base = BackpackLayout.quiverIndex(storage, 0);
        backpackData.setTag(BackpackNbt.ITEMS, BackpackNbt.writeItems(inventory, base));
        backpackData.setInteger(BackpackNbt.STORAGE_SIZE, storage);
        BackpackNbt.writeQuiver(backpackData, inventory, base, quiver, keptQuiver);
    }

    @Override
    public void consumeInventoryItem(Item item)
    {
        int base = BackpackLayout.quiverIndex(storage, 0);
        for (int q = 0; q < quiver; q++)
        {
            ItemStack stack = inventory[base + q];
            if (stack != null && stack.getItem() == item)
            {
                decrStackSize(base + q, 1);
                return;
            }
        }
        InventoryActions.consumeItemInInventory(this, item);
    }

    public boolean hasBlock(Block block)
    {
        return InventoryActions.hasBlockItem(this, block);
    }
}
