package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.ScubaRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * The two air tanks of a Scuba Gear stack, kept in the stack's {@code scubaData} tag, and the two container slots of
 * its GUI (in and out, never saved: the container gives them back when it closes, like the copter pack).
 * <p>
 * One key and one save: every change first reloads the tag (the worn gear spends air while its GUI is open, so a
 * view opened earlier is stale) and then writes the whole tag once.
 */
public class InventoryScubaGear implements IInventoryTanks
{
    public static final String TAG = "scubaData";
    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;

    private ItemStack containerStack;
    private final FluidTank leftTank = new FluidTank(ConfigHandler.SCUBA_TANK_CAPACITY);
    private final FluidTank rightTank = new FluidTank(ConfigHandler.SCUBA_TANK_CAPACITY);
    private final ItemStack[] inventory = new ItemStack[2];

    public InventoryScubaGear(ItemStack scubaGear)
    {
        containerStack = scubaGear;
        if (scubaGear.stackTagCompound == null) scubaGear.stackTagCompound = new NBTTagCompound();
        openInventory();
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

    public int getAir()
    {
        return leftTank.getFluidAmount() + rightTank.getFluidAmount();
    }

    public int getCapacity()
    {
        return leftTank.getCapacity() + rightTank.getCapacity();
    }

    /** Takes up to {@code due} millibuckets, left tank first. Returns what it took. Does not save. */
    public int takeAir(int due)
    {
        int fromLeft = ScubaRules.fromFirst(leftTank.getFluidAmount(), due);
        int fromRight = ScubaRules.fromSecond(leftTank.getFluidAmount(), rightTank.getFluidAmount(), due);
        if (fromLeft > 0) leftTank.drain(fromLeft, true);
        if (fromRight > 0) rightTank.drain(fromRight, true);
        return fromLeft + fromRight;
    }

    /** Fills with {@code air} (already a breathable stored fluid), left tank first. Returns what fit. Does not save. */
    public int fill(FluidStack air, boolean doFill)
    {
        if (air == null || air.amount <= 0) return 0;
        int intoLeft = leftTank.fill(air, doFill);
        if (intoLeft >= air.amount) return intoLeft;
        FluidStack rest = air.copy();
        rest.amount = air.amount - intoLeft;
        return intoLeft + rightTank.fill(rest, doFill);
    }

    /** Room left for {@code air}: both tanks, as long as each holds nothing or the same fluid. */
    public int room(FluidStack air)
    {
        if (air == null) return 0;
        FluidStack all = air.copy();
        all.amount = Integer.MAX_VALUE / 2;
        return fill(all, false);
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

    /** Only stores the stack: the server runs {@link #updateTankSlots} from the slot (SlotAirContainer), never the client. */
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
        return "container.adventurebackpack:scubaGear";
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

    /** Nothing to save: every change already saved itself (a stale save would give back air already spent). */
    @Override
    public void closeInventory()
    {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return slot == SLOT_IN && AirContainers.isAirContainer(stack);
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
                return InventoryScubaGear.this.fill(resource, doFill);
            }

            @Override
            public FluidStack drain(int maxDrain, boolean doDrain)
            {
                FluidStack kind = getFluid();
                if (kind == null) return null;
                int amount = Math.min(maxDrain, getAir());
                if (doDrain) takeAir(amount);
                return new FluidStack(kind.getFluid(), amount);
            }

            @Override
            public FluidStack getFluid()
            {
                FluidStack kind = leftTank.getFluid() != null ? leftTank.getFluid() : rightTank.getFluid();
                return kind == null ? null : new FluidStack(kind.getFluid(), getAir());
            }

            @Override
            public int getFluidAmount()
            {
                return getAir();
            }
        };
        boolean moved = AirContainers.processAll(inventory, SLOT_IN, SLOT_OUT, both);
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
    }

    @Override
    public void saveToNBT(NBTTagCompound compound)
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setTag("leftTank", leftTank.writeToNBT(new NBTTagCompound()));
        data.setTag("rightTank", rightTank.writeToNBT(new NBTTagCompound()));
        compound.setTag(TAG, data);
    }

    @Override
    public FluidTank[] getTanksArray()
    {
        return new FluidTank[]{leftTank, rightTank};
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
