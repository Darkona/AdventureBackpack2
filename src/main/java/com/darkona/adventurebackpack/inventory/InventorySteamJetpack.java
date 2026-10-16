package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.FluidUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidTank;

public class InventorySteamJetpack implements IInventoryTanks
{
    public static final boolean OFF = false;
    public static final boolean ON = true;
    private FluidTank waterTank = new FluidTank(ConfigHandler.JETPACK_WATER_CAPACITY);
    private FluidTank steamTank = new FluidTank(ConfigHandler.JETPACK_STEAM_CAPACITY);
    private ItemStack[] inventory = new ItemStack[3];
    private int temperature = 25;
    private boolean status = OFF;
    private int burnTicks = 0;
    private ItemStack containerStack;
    private long systemTime = 0;
    private boolean boiling = false;
    private boolean leaking = false;
    private boolean inUse = false;
    public int currentItemBurnTime = 0;

    public static final int MAX_TEMPERATURE = 200;
    public static final int BUCKET_IN_SLOT = 0;
    public static final int BUCKET_OUT_SLOT = 1;
    public static final int FUEL_SLOT = 2;
    private int coolTicks = 5000;

    public InventorySteamJetpack(final ItemStack jetpack)
    {
        this.containerStack = jetpack;
        if (!containerStack.hasTagCompound())
        {
            containerStack.stackTagCompound = new NBTTagCompound();
            closeInventory();
        }
       openInventory();
    }

    public int getBurnTimeRemainingScaled(int scale)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = 200;
        }

        return this.burnTicks * scale / this.currentItemBurnTime;
    }

    @Override
    public boolean updateTankSlots()
    {
        return false;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        if(compound.hasKey("jetpackData"))
        {
            NBTTagCompound jetpackData = compound.getCompoundTag("jetpackData");
            waterTank.readFromNBT(jetpackData.getCompoundTag("waterTank"));
            steamTank.readFromNBT(jetpackData.getCompoundTag("steamTank"));
            temperature = jetpackData.getInteger("temperature");
            status = jetpackData.getBoolean("status");
            burnTicks = jetpackData.getInteger("burnTicks");
            coolTicks = jetpackData.getInteger("coolTicks");
            systemTime = jetpackData.getLong("systemTime");
            inUse = jetpackData.getBoolean("inUse");
            boiling = jetpackData.getBoolean("boiling");
            leaking = jetpackData.getBoolean("leaking");
            currentItemBurnTime = jetpackData.getInteger("currentBurn");
            java.util.Arrays.fill(inventory, null);
            NBTTagList items = jetpackData.getTagList("inventory", net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < items.tagCount(); i++)
            {
                NBTTagCompound item = items.getCompoundTagAt(i);
                byte slot = item.getByte("Slot");
                if (slot >= 0 && slot < inventory.length)
                {
                    inventory[slot] = ItemStack.loadItemStackFromNBT(item);
                }
            }
        }
        if (compound == containerStack.stackTagCompound) remember();
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound jetpackData;
        if(compound.hasKey("jetpackData"))
        {
            jetpackData = compound.getCompoundTag("jetpackData");
        }else
        {
            jetpackData = new NBTTagCompound();
        }

        jetpackData.setTag("waterTank",waterTank.writeToNBT(new NBTTagCompound()));
        jetpackData.setTag("steamTank",steamTank.writeToNBT(new NBTTagCompound()));
        jetpackData.setInteger("temperature", temperature);
        jetpackData.setBoolean("status", status);
        jetpackData.setInteger("burnTicks", burnTicks);
        jetpackData.setInteger("coolTicks", coolTicks);
        jetpackData.setLong("systemTime",systemTime);
        jetpackData.setBoolean("inUse", inUse);
        jetpackData.setBoolean("boiling",boiling);
        jetpackData.setBoolean("leaking",leaking);
        jetpackData.setInteger("currentBurn",currentItemBurnTime);
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
        jetpackData.setTag("inventory", items);
        compound.setTag("jetpackData",jetpackData);
        if (compound == containerStack.stackTagCompound) remember();
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        FluidTank[] tanks = {waterTank,steamTank};
        return tanks;
    }

    @Override
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
        containerStack.stackTagCompound.getCompoundTag("jetpackData").setTag("inventory", items);
        inventoryChanged = false;
        rememberTags();
    }

    @Override
    public void dirtyTanks()
    {
        containerStack.stackTagCompound.getCompoundTag("jetpackData").setTag("waterTank",waterTank.writeToNBT(new NBTTagCompound()));
        containerStack.stackTagCompound.getCompoundTag("jetpackData").setTag("steamTank",steamTank.writeToNBT(new NBTTagCompound()));
        flushedWater = tankKey(waterTank);
        flushedSteam = tankKey(steamTank);
        rememberTags();
    }

    public void dirtyBoiler()
    {
        NBTTagCompound jetpackData = containerStack.stackTagCompound.getCompoundTag("jetpackData");
        jetpackData.setBoolean("boiling", boiling);
        jetpackData.setBoolean("leaking", leaking);
        jetpackData.setInteger("temperature", temperature);
        jetpackData.setInteger("burnTicks", burnTicks);
        jetpackData.setInteger("coolTicks", coolTicks);
        jetpackData.setInteger("currentBurn",currentItemBurnTime);
        flushedBoiling = boiling;
        flushedLeaking = leaking;
        flushedTemperature = temperature;
        flushedBurnTicks = burnTicks;
        flushedCoolTicks = coolTicks;
        flushedCurrentBurn = currentItemBurnTime;
        rememberTags();
    }

    public int consumeFuel()
    {
        int result = 0;
        if(isFuel(inventory[FUEL_SLOT]))
        {
            result = TileEntityFurnace.getItemBurnTime(inventory[FUEL_SLOT]);
            --inventory[FUEL_SLOT].stackSize;
            if(inventory[FUEL_SLOT].stackSize == 0)
            {
                inventory[FUEL_SLOT] = inventory[FUEL_SLOT].getItem().getContainerItem(inventory[FUEL_SLOT]);
            }
            inventoryChanged = true;
        }
        return result;
    }

    public boolean isFuel(ItemStack stack)
    {
        return TileEntityFurnace.isItemFuel(stack);
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
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        if (slot < inventory.length && inventory[slot] != null)
        {
            if (inventory[slot].stackSize > amount)
            {
                ItemStack result = inventory[slot].splitStack(amount);
                return result;
            }
            ItemStack stack = inventory[slot];
            setInventorySlotContentsNoSave(slot, null);
            return stack;
        }
        return null;
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
                if(slot == FUEL_SLOT)
                {
                    setInventorySlotContents(slot, itemstack.getItem().getContainerItem(itemstack));
                }
                else
                {
                    setInventorySlotContents(slot, null);
                }
            } else
            {
                itemstack = itemstack.splitStack(quantity);
            }
        }
        return itemstack;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i)
    {
        return (i == 0 || i== 1) ? inventory[i] : null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inventory[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit())
        {
            stack.stackSize = getInventoryStackLimit();
        }
        if(slot < FUEL_SLOT)onInventoryChanged();
        dirtyInventory();
}

    public void onInventoryChanged()
    {
        for (int i = 0; i < inventory.length; i++)
        {
            if (i == 0)
            {
                ItemStack container = getStackInSlot(i);
                if(FluidContainerRegistry.isFilledContainer(container) && FluidUtils.isContainerForFluid(container, FluidRegistry.WATER))
                {
                    InventoryActions.transferContainerTank(this, waterTank, i);
                }else
                if(FluidContainerRegistry.isEmptyContainer(container) && waterTank.getFluid()!=null && FluidUtils.isContainerForFluid(container, FluidRegistry.WATER))
                {
                    InventoryActions.transferContainerTank(this, waterTank, i);
                }
            }
        }
        markDirty();
    }
    @Override
    public String getInventoryName()
    {
        return "Steam Jetpack";
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
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return true;
    }

    @Override
    public void openInventory()
    {
        if (isCurrentFor(containerStack)) flushCounters();
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
        if(slot == BUCKET_IN_SLOT)return SlotFluid.valid(stack) && FluidUtils.isContainerForFluid(stack, FluidRegistry.WATER);
        if(slot == FUEL_SLOT)return TileEntityFurnace.isItemFuel(stack);
        return false;
    }

    public ItemStack getParentItemStack()
    {
        return containerStack;
    }

    public FluidTank getWaterTank()
    {
        return waterTank;
    }

    public FluidTank getSteamTank()
    {
        return steamTank;
    }

    public boolean getStatus()
    {
        return status;
    }

    public void setStatus(boolean status)
    {
        this.status = status;
    }

    public int getTemperature()
    {
        return temperature;
    }

    public void setTemperature(int temperature)
    {
        this.temperature = temperature;
    }

    public boolean isInUse()
    {
        return inUse;
    }

    public void setInUse(boolean inUse)
    {
        this.inUse = inUse;
    }

    public boolean isBoiling()
    {
        return boiling;
    }

    public void setBoiling(boolean boiling)
    {
        this.boiling = boiling;
    }

    public boolean isLeaking()
    {
        return leaking;
    }

    public void setLeaking(boolean leaking)
    {
        this.leaking = leaking;
    }

    public long getSystemTime()
    {
        return systemTime;
    }

    public void setSystemTime(long systemTime)
    {
        this.systemTime = systemTime;
    }

    public int getBurnTicks()
    {
        return burnTicks;
    }

    public void setBurnTicks(int burnTicks)
    {
        this.burnTicks = burnTicks;
    }

    public ItemStack[] getInventory()
    {
        return inventory;
    }

    public void setInventory(ItemStack[] inventory)
    {
        this.inventory = inventory;
    }

    public int getCoolTicks()
    {
        return coolTicks;
    }

    public void setCoolTicks(int coolTicks)
    {
        this.coolTicks = coolTicks;
    }

    public void setContainerStack(ItemStack containerStack)
    {
        this.containerStack = containerStack;
    }

    public ItemStack getContainerStack()
    {
        return containerStack;
    }

    public void calculateLostTime()
    {
        long elapsedTimesince = System.currentTimeMillis() - systemTime;
    }

    private boolean inventoryChanged = false;
    private NBTTagCompound tagRef;
    private Object dataRef, waterRef, steamRef, inventoryRef, statusRef, inUseRef;
    private int flushedTemperature, flushedBurnTicks, flushedCoolTicks, flushedCurrentBurn;
    private boolean flushedBoiling, flushedLeaking, flushedInUse, flushedStatus;
    private int flushedWater = -1, flushedSteam = -1;

    public boolean isCurrentFor(ItemStack stack)
    {
        if (stack != containerStack || stack.stackTagCompound != tagRef || tagRef == null) return false;
        NBTBase data = tagRef.getTag("jetpackData");
        if (data != dataRef || !(data instanceof NBTTagCompound)) return false;
        NBTTagCompound d = (NBTTagCompound) data;
        return d.getTag("waterTank") == waterRef && d.getTag("steamTank") == steamRef
                && d.getTag("inventory") == inventoryRef && d.getTag("status") == statusRef && d.getTag("inUse") == inUseRef;
    }

    private void remember()
    {
        rememberTags();
        flushedTemperature = temperature;
        flushedBurnTicks = burnTicks;
        flushedCoolTicks = coolTicks;
        flushedCurrentBurn = currentItemBurnTime;
        flushedBoiling = boiling;
        flushedLeaking = leaking;
        flushedInUse = inUse;
        flushedStatus = status;
        flushedWater = tankKey(waterTank);
        flushedSteam = tankKey(steamTank);
    }

    private void rememberTags()
    {
        tagRef = containerStack.stackTagCompound;
        NBTTagCompound d = tagRef == null ? null : (NBTTagCompound) (tagRef.getTag("jetpackData") instanceof NBTTagCompound ? tagRef.getTag("jetpackData") : null);
        dataRef = d;
        waterRef = d == null ? null : d.getTag("waterTank");
        steamRef = d == null ? null : d.getTag("steamTank");
        inventoryRef = d == null ? null : d.getTag("inventory");
        statusRef = d == null ? null : d.getTag("status");
        inUseRef = d == null ? null : d.getTag("inUse");
    }

    private static int tankKey(FluidTank tank)
    {
        return tank.getFluid() == null ? -1 : tank.getFluidAmount();
    }

    static final int COUNTER_STEP = 20;

    static boolean counterDue(int current, int flushed)
    {
        if (current == flushed) return false;
        if (current > flushed || current <= 0) return true;
        return Math.floorDiv(current, COUNTER_STEP) != Math.floorDiv(flushed, COUNTER_STEP);
    }

    public void flushCounters()
    {
        if (containerStack == null || !(containerStack.stackTagCompound != null
                && containerStack.stackTagCompound.getTag("jetpackData") instanceof NBTTagCompound)) return;
        NBTTagCompound d = containerStack.stackTagCompound.getCompoundTag("jetpackData");
        boolean written = false;
        if (burnTicks != flushedBurnTicks) { d.setInteger("burnTicks", flushedBurnTicks = burnTicks); written = true; }
        if (coolTicks != flushedCoolTicks) { d.setInteger("coolTicks", flushedCoolTicks = coolTicks); written = true; }
        if (written) rememberTags();
    }

    public int flushChanges()
    {
        if (containerStack.stackTagCompound == null) containerStack.stackTagCompound = new NBTTagCompound();
        if (!(containerStack.stackTagCompound.getTag("jetpackData") instanceof NBTTagCompound))
        {
            saveToNBT(containerStack.stackTagCompound);
            inventoryChanged = false;
            return 1;
        }
        int written = 0;
        NBTTagCompound d = containerStack.stackTagCompound.getCompoundTag("jetpackData");
        if (temperature != flushedTemperature) { d.setInteger("temperature", flushedTemperature = temperature); written++; }
        if (counterDue(burnTicks, flushedBurnTicks)) { d.setInteger("burnTicks", flushedBurnTicks = burnTicks); written++; }
        if (counterDue(coolTicks, flushedCoolTicks)) { d.setInteger("coolTicks", flushedCoolTicks = coolTicks); written++; }
        if (currentItemBurnTime != flushedCurrentBurn) { d.setInteger("currentBurn", flushedCurrentBurn = currentItemBurnTime); written++; }
        if (boiling != flushedBoiling) { d.setBoolean("boiling", flushedBoiling = boiling); written++; }
        if (leaking != flushedLeaking) { d.setBoolean("leaking", flushedLeaking = leaking); written++; }
        if (inUse != flushedInUse) { d.setBoolean("inUse", flushedInUse = inUse); written++; }
        if (status != flushedStatus) { d.setBoolean("status", flushedStatus = status); written++; }
        if (tankKey(waterTank) != flushedWater)
        {
            d.setTag("waterTank", waterTank.writeToNBT(new NBTTagCompound()));
            flushedWater = tankKey(waterTank);
            written++;
        }
        if (tankKey(steamTank) != flushedSteam)
        {
            d.setTag("steamTank", steamTank.writeToNBT(new NBTTagCompound()));
            flushedSteam = tankKey(steamTank);
            written++;
        }
        if (inventoryChanged)
        {
            dirtyInventory();
            written++;
        }
        if (written > 0) rememberTags();
        return written;
    }
}
