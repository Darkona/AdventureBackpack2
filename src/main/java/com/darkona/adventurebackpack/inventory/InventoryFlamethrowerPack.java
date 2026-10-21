package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.common.ScubaRules;
import com.darkona.adventurebackpack.common.StationTransfer;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.GeneralReference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class InventoryFlamethrowerPack implements IInventoryTanks
{
    public static final String TAG = "flamethrowerData";
    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;

    private ItemStack containerStack;
    private final FluidTank leftTank = new FluidTank(ConfigHandler.FLAMETHROWER_TANK_CAPACITY);
    private final FluidTank rightTank = new FluidTank(ConfigHandler.FLAMETHROWER_TANK_CAPACITY);
    private final FluidTank[] tanks = {leftTank, rightTank};
    private final ItemStack[] inventory = new ItemStack[2];
    private boolean on;

    public InventoryFlamethrowerPack(ItemStack pack)
    {
        containerStack = pack;
        if (pack.stackTagCompound == null) pack.stackTagCompound = new NBTTagCompound();
        openInventory();
    }

    public static boolean isLit(ItemStack pack)
    {
        return pack != null && pack.stackTagCompound != null && pack.stackTagCompound.getCompoundTag(TAG).getBoolean("on");
    }

    public static boolean hasFuel(ItemStack pack)
    {
        if (pack == null || pack.stackTagCompound == null) return false;
        NBTTagCompound data = pack.stackTagCompound.getCompoundTag(TAG);
        return data.getCompoundTag("leftTank").getInteger("Amount") > 0 || data.getCompoundTag("rightTank").getInteger("Amount") > 0;
    }

    public ItemStack getParentItemStack()
    {
        return containerStack;
    }

    public void setContainerStack(ItemStack stack)
    {
        containerStack = stack;
        if (stack.stackTagCompound == null) stack.stackTagCompound = new NBTTagCompound();
        openInventory();
    }

    public FluidTank getLeftTank()
    {
        return leftTank;
    }

    public FluidTank getRightTank()
    {
        return rightTank;
    }

    public boolean isOn()
    {
        return on;
    }

    public void setOn(boolean on)
    {
        this.on = on;
    }

    public int getFuel()
    {
        return leftTank.getFluidAmount() + rightTank.getFluidAmount();
    }

    public int getCapacity()
    {
        return leftTank.getCapacity() + rightTank.getCapacity();
    }

    public Fluid getFuelFluid()
    {
        FluidStack kind = leftTank.getFluidAmount() > 0 ? leftTank.getFluid() : rightTank.getFluid();
        return kind == null || kind.amount <= 0 ? null : kind.getFluid();
    }

    public int takeFuel(int due)
    {
        int fromLeft = ScubaRules.fromFirst(leftTank.getFluidAmount(), due);
        int fromRight = ScubaRules.fromSecond(leftTank.getFluidAmount(), rightTank.getFluidAmount(), due);
        if (fromLeft > 0) leftTank.drain(fromLeft, true);
        if (fromRight > 0) rightTank.drain(fromRight, true);
        return fromLeft + fromRight;
    }

    public int fill(FluidStack fuel, boolean doFill)
    {
        if (!GeneralReference.isValidFuel(fuel)) return 0;
        Fluid stored = getFuelFluid();
        if (!StationTransfer.sameOrEmpty(stored == null ? null : stored.getName(), fuel.getFluid().getName())) return 0;
        int intoLeft = leftTank.fill(fuel, doFill);
        if (intoLeft >= fuel.amount) return intoLeft;
        FluidStack rest = fuel.copy();
        rest.amount = fuel.amount - intoLeft;
        return intoLeft + rightTank.fill(rest, doFill);
    }

    @Override
    public int getSizeInventory()
    {
        return inventory.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return slot >= 0 && slot < inventory.length ? inventory[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int quantity)
    {
        return decrStackSizeNoSave(slot, quantity);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return getStackInSlot(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        setInventorySlotContentsNoSave(slot, stack);
    }

    @Override
    public void setInventorySlotContentsNoSave(int slot, ItemStack stack)
    {
        if (slot < 0 || slot >= inventory.length) return;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) stack.stackSize = getInventoryStackLimit();
        inventory[slot] = stack;
    }

    @Override
    public ItemStack decrStackSizeNoSave(int slot, int amount)
    {
        if (slot < 0 || slot >= inventory.length || inventory[slot] == null) return null;
        if (inventory[slot].stackSize > amount) return inventory[slot].splitStack(amount);
        ItemStack stack = inventory[slot];
        inventory[slot] = null;
        return stack;
    }

    @Override
    public String getInventoryName()
    {
        return "container.adventurebackpack:flamethrowerPack";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
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
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return slot == SLOT_IN && FuelContainers.isFuelContainer(stack);
    }

    @Override
    public boolean updateTankSlots()
    {
        if (inventory[SLOT_IN] == null) return false;
        openInventory();
        FluidTank both = new FluidTank(getCapacity())
        {
            @Override
            public int fill(FluidStack resource, boolean doFill)
            {
                return InventoryFlamethrowerPack.this.fill(resource, doFill);
            }

            @Override
            public FluidStack drain(int maxDrain, boolean doDrain)
            {
                Fluid kind = getFuelFluid();
                if (kind == null) return null;
                int amount = Math.min(maxDrain, getFuel());
                if (doDrain) takeFuel(amount);
                return new FluidStack(kind, amount);
            }

            @Override
            public FluidStack getFluid()
            {
                Fluid kind = getFuelFluid();
                return kind == null ? null : new FluidStack(kind, getFuel());
            }

            @Override
            public int getFluidAmount()
            {
                return getFuel();
            }
        };
        boolean moved = FuelContainers.processAll(inventory, SLOT_IN, SLOT_OUT, both);
        if (moved) markDirty();
        return moved;
    }

    @Override
    public void loadFromNBT(NBTTagCompound compound)
    {
        NBTTagCompound data = compound.getCompoundTag(TAG);
        leftTank.setFluid(null);
        rightTank.setFluid(null);
        leftTank.readFromNBT(data.getCompoundTag("leftTank"));
        rightTank.readFromNBT(data.getCompoundTag("rightTank"));
        on = data.getBoolean("on");
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
        data.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        data.setBoolean("on", on);
        compound.setTag(TAG, data);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return tanks;
    }

    @Override
    public void dirtyInventory()
    {
    }

    @Override
    public void dirtyTanks()
    {
        markDirty();
    }
}
