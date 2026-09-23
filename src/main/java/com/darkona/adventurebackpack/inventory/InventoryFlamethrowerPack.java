package com.darkona.adventurebackpack.inventory;

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

/**
 * The two fuel tanks and the pilot switch of a Flamethrower Pack stack, kept in the stack's {@code flamethrowerData}
 * tag, and the two container slots of its GUI (in and out, never saved: the container gives them back when it
 * closes). Both tanks hold the same fuel: a fill is refused when it is another fuel. They empty left tank first.
 * <p>
 * One key and one save: every change first reloads the tag (the worn pack burns fuel while its GUI is open, so a
 * view opened earlier is stale) and then writes the whole tag once.
 */
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

    /** True when the stack's tag says the pilot is lit, without building an inventory (one byte read). */
    public static boolean isLit(ItemStack pack)
    {
        return pack != null && pack.stackTagCompound != null && pack.stackTagCompound.getCompoundTag(TAG).getBoolean("on");
    }

    public ItemStack getParentItemStack()
    {
        return containerStack;
    }

    /** Points this view at another stack (the synced copy that replaced the old one) and reads it. */
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

    /** The fuel in the tanks (left first), or null when both are empty. */
    public Fluid getFuelFluid()
    {
        FluidStack kind = leftTank.getFluidAmount() > 0 ? leftTank.getFluid() : rightTank.getFluid();
        return kind == null || kind.amount <= 0 ? null : kind.getFluid();
    }

    /** Takes up to {@code due} millibuckets, left tank first. Returns what it took. Does not save. */
    public int takeFuel(int due)
    {
        int fromLeft = ScubaRules.fromFirst(leftTank.getFluidAmount(), due);
        int fromRight = ScubaRules.fromSecond(leftTank.getFluidAmount(), rightTank.getFluidAmount(), due);
        if (fromLeft > 0) leftTank.drain(fromLeft, true);
        if (fromRight > 0) rightTank.drain(fromRight, true);
        return fromLeft + fromRight;
    }

    /**
     * Fills with {@code fuel} when it is on fuels.list and the tanks hold nothing or the same fuel, left tank first.
     * Returns what fit (or would fit). Does not save.
     */
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

    // ---------------------------------------------------------------- IInventory

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

    /** Only stores the stack: the server runs {@link #updateTankSlots} from the slot (SlotFuelContainer), never the client. */
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

    /** Nothing to save: every change already saved itself (a stale save would give back fuel already burned). */
    @Override
    public void closeInventory()
    {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return slot == SLOT_IN && FuelContainers.isFuelContainer(stack);
    }

    // ---------------------------------------------------------------- IInventoryTanks

    /** Empties or fills the containers of the in slot through the tanks, reading the tag first and saving once. */
    @Override
    public boolean updateTankSlots()
    {
        if (inventory[SLOT_IN] == null) return false;
        openInventory();
        // the slot array goes through one tank view that spans both tanks
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
