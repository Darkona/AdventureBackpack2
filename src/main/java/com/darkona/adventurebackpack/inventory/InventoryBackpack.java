package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.widget.WidgetInventory;
import com.darkona.adventurebackpack.common.sort.SlotLocks;
import com.darkona.adventurebackpack.inventory.widget.WidgetStore;
import com.darkona.adventurebackpack.common.layout.StorageIndex;
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
import net.minecraftforge.fluids.FluidTank;

public class InventoryBackpack implements IInventoryAdventureBackpack
{
    public ItemStack[] inventory = new ItemStack[StorageIndex.FIRST_EXTRA_STORAGE];
    private int storage = StorageIndex.LEGACY_STORAGE;
    private WidgetLayout layout;
    private NBTTagList[] kept;
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
    private final SlotLocks locks = new SlotLocks();

    public InventoryBackpack(ItemStack backpack)
    {
        containerStack = backpack;
        if(!backpack.hasTagCompound())
        {
            backpack.stackTagCompound = new NBTTagCompound();
            storage = WidgetLayouts.storage(colorName, 0);
            layout = WidgetLayouts.widgets(colorName, storage);
            inventory = new ItemStack[layout.inventorySize()];
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
    public WidgetLayout getLayout()
    {
        if (layout == null) layout = WidgetLayouts.widgets(colorName, storage);
        return layout;
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
        HiddenTank.seal(leftTank, rightTank, WidgetInventory.showsTank(getLayout(), 1));
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
        if (stack != null && stack.stackSize > limitFor(slot))
        {
            stack.stackSize = limitFor(slot);
        }
        dirtyInventory();
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack stack)
    {
        if (slot < 0 || slot >= inventory.length) return;
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > limitFor(slot))
        {
            stack.stackSize = limitFor(slot);
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
        return WidgetInventory.fillTanks(this, getLayout());
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
            if (HiddenTank.seal(leftTank, rightTank, WidgetInventory.showsTank(layout, 1)))
            {
                backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
                backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
            }
            lastTime = backpackData.getInteger("lastTime");
            special = backpackData.getBoolean("special");
            extendedProperties = BackpackNbt.readExtended(backpackData);
            BackpackNbt.readLocks(backpackData, locks);
        }
        if (compound == containerStack.stackTagCompound) rememberTags();
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound backpackData = new NBTTagCompound();
        writeContents(backpackData);
        backpackData.setString("colorName", colorName);
        backpackData.setInteger("lastTime", lastTime);
        backpackData.setBoolean("special", BackpackAbilities.hasAbility(colorName));
        BackpackNbt.writeExtended(backpackData, extendedProperties);
        BackpackNbt.writeLocks(backpackData, locks);
        backpackData.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        backpackData.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));

        WidgetStore.keepUnknown(compound.getCompoundTag("backpackData"), backpackData, getLayout());
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
        return WidgetInventory.dropsOnClose(widgets(), slot) ? inventory[slot] : null;
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

    @Override
    public SlotLocks getLocks()
    {
        return locks;
    }

    @Override
    public void dirtyLocks()
    {
        boolean current = isCurrent();
        BackpackNbt.writeLocks(containerStack.stackTagCompound.getCompoundTag("backpackData"), locks);
        if (current) rememberTags();
    }

    public void dirtyInventory()
    {
        boolean current = isCurrent();
        if(updateTankSlots()){
            dirtyTanks();
        }
        writeContents(containerStack.stackTagCompound.getCompoundTag("backpackData"));
        if (current) rememberTags();
    }

    private WidgetLayout keysOf;
    private String[] readKeys = BackpackNbt.CORE_KEYS;
    private NBTTagCompound tagRef;
    private NBTBase dataRef;
    private NBTBase[] keyRefs = new NBTBase[BackpackNbt.CORE_KEYS.length];

    public boolean isCurrent()
    {
        NBTTagCompound tag = containerStack.stackTagCompound;
        if (tag == null || tag != tagRef) return false;
        NBTBase data = tag.getTag("backpackData");
        if (data != dataRef) return false;
        if (!(data instanceof NBTTagCompound)) return true;
        NBTTagCompound d = (NBTTagCompound) data;
        for (int i = 0; i < readKeys.length; i++)
        {
            if (d.getTag(readKeys[i]) != keyRefs[i]) return false;
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
        WidgetLayout l = getLayout();
        if (l != keysOf)
        {
            keysOf = l;
            readKeys = WidgetStore.readKeys(l);
            keyRefs = new NBTBase[readKeys.length];
        }
        for (int i = 0; i < readKeys.length; i++)
        {
            keyRefs[i] = d == null ? null : d.getTag(readKeys[i]);
        }
    }

    private void writeContents(NBTTagCompound backpackData)
    {
        WidgetStore.write(backpackData, getLayout(), inventory, kept, storage);
    }

    @Override
    public void consumeInventoryItem(Item item)
    {
        WidgetInventory.consume(this, widgets(), item);
    }

    public boolean hasBlock(Block block)
    {
        return InventoryActions.hasBlockItem(this, block);
    }
}
