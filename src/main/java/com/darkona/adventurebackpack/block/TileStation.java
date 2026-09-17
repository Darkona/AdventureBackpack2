package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.StationTransfer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * A station block with one tank, a container slot in, a container slot out and a slot for a pack: the base of the Fuel
 * Station (and later the Air Compressor). It never ticks: everything happens when a slot changes, when a pipe fills or
 * drains it, or when a player clicks it. Pipes of any mod fill it from any side through {@link IFluidHandler}, with
 * only the fluids {@link #accepts(Fluid)} allows. The client copy of the tank is only filled by the open GUI's
 * container, so there is no block update packet.
 */
public abstract class TileStation extends TileEntity implements ISidedInventory, IFluidHandler
{

    public static final int SLOT_IN = 0;
    public static final int SLOT_OUT = 1;
    public static final int SLOT_PACK = 2;
    private static final int[] FROM_BOTTOM = {SLOT_OUT};
    private static final int[] FROM_OTHER_SIDES = {SLOT_IN};

    protected final FluidTank tank;
    private final ItemStack[] slots = new ItemStack[3];
    /** False once the pack in its slot took less than the station offered (full, or another fuel): pipes then skip it. */
    private boolean packMayAccept = true;
    /** Set while the slots are being processed, so the changes made by the processing do not start it again. */
    private boolean busy;
    /** False when a creative player breaks the block: the slots drop, the block item does not. */
    boolean dropOnBreak = true;

    protected TileStation(int capacity)
    {
        tank = new FluidTank(capacity);
    }

    /** True when this fluid may go into the tank. */
    protected abstract boolean accepts(Fluid fluid);

    /** The station's system switch: when false it neither takes nor gives fluid. */
    public abstract boolean enabled();

    /** True for an item the pack slot takes. */
    protected abstract boolean isPack(ItemStack stack);

    /** Moves up to {@code offered.amount} into the pack; returns how much it took. */
    protected abstract int fillPack(ItemStack pack, FluidStack offered, boolean doFill);

    public FluidTank getTank()
    {
        return tank;
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    // ---------------------------------------------------------------- transfers (server side, on actions)

    /** Fills a pack the player wears or holds with everything that fits. Returns the millibuckets moved. */
    public int fillPackStack(ItemStack pack)
    {
        FluidStack stored = tank.getFluid();
        if (!enabled() || pack == null || stored == null || stored.amount <= 0) return 0;
        int moved;
        try
        {
            moved = fillPack(pack, stored.copy(), true);
        } catch (RuntimeException e)
        {
            return 0;
        }
        if (moved > 0) take(moved);
        return moved;
    }

    /** Fills another tank (the boat's) with everything that fits, when it may hold this fluid. Returns the mB moved. */
    public int fillTank(FluidTank target, boolean targetAccepts)
    {
        FluidStack stored = tank.getFluid();
        if (!enabled() || target == null || stored == null || stored.amount <= 0) return 0;
        FluidStack inTarget = target.getFluid();
        int moved = StationTransfer.amount(stored.amount, target.getFluidAmount(), target.getCapacity(), targetAccepts,
                StationTransfer.sameOrEmpty(inTarget == null ? null : nameOf(inTarget), nameOf(stored)));
        if (moved <= 0) return 0;
        if (inTarget == null || inTarget.amount <= 0)
        {
            FluidStack copy = stored.copy();
            copy.amount = moved;
            target.setFluid(copy);
        } else
        {
            inTarget.amount += moved;
        }
        take(moved);
        return moved;
    }

    private void take(int amount)
    {
        tank.drain(amount, true);
        saveChanges();
    }

    /** Adds fluid the caller already checked (accepted, same fluid, fits). */
    private void put(FluidStack fluid, int amount)
    {
        FluidStack stored = tank.getFluid();
        if (stored == null || stored.amount <= 0)
        {
            FluidStack copy = fluid.copy();
            copy.amount = amount;
            tank.setFluid(copy);
        } else
        {
            stored.amount += amount;
        }
        saveChanges();
    }

    /** Millibuckets of {@code fluid} the tank takes now. */
    private int room(FluidStack fluid)
    {
        if (fluid == null || fluid.getFluid() == null) return 0;
        FluidStack stored = tank.getFluid();
        return StationTransfer.amount(fluid.amount, tank.getFluidAmount(), tank.getCapacity(), accepts(fluid.getFluid()),
                StationTransfer.sameOrEmpty(stored == null || stored.amount <= 0 ? null : nameOf(stored), nameOf(fluid)));
    }

    private static String nameOf(FluidStack stack)
    {
        return stack.getFluid() == null ? null : stack.getFluid().getName();
    }

    private void pushToPack()
    {
        ItemStack pack = slots[SLOT_PACK];
        FluidStack stored = tank.getFluid();
        if (pack == null || stored == null || stored.amount <= 0 || !enabled()) return;
        int offered = stored.amount;
        int moved = fillPackStack(pack);
        if (moved < offered) packMayAccept = false;
    }

    /** One transfer between the container in the input slot and the tank. True when something moved. */
    private boolean processContainer()
    {
        ItemStack in = slots[SLOT_IN];
        if (in == null) return false;
        try
        {
            if (FluidContainerRegistry.isFilledContainer(in)) return emptyRegistered(in);
            if (FluidContainerRegistry.isEmptyContainer(in)) return fillRegistered(in);
            if (in.getItem() instanceof IFluidContainerItem && in.stackSize == 1) return exchangeFluidItem(in, (IFluidContainerItem) in.getItem());
        } catch (RuntimeException e)
        {
            // another mod's container misbehaved: leave it in the slot
        }
        return false;
    }

    private boolean emptyRegistered(ItemStack in)
    {
        FluidStack fluid = FluidContainerRegistry.getFluidForFilledItem(in);
        if (fluid == null || fluid.amount <= 0 || room(fluid) < fluid.amount) return false;
        ItemStack empty = FluidContainerRegistry.drainFluidContainer(in);
        if (!fitsOutput(empty)) return false;
        put(fluid, fluid.amount);
        consumeInput();
        addOutput(empty);
        return true;
    }

    private boolean fillRegistered(ItemStack in)
    {
        FluidStack stored = tank.getFluid();
        if (stored == null || stored.amount <= 0) return false;
        ItemStack filled = FluidContainerRegistry.fillFluidContainer(stored, in);
        FluidStack inside = FluidContainerRegistry.getFluidForFilledItem(filled);
        if (filled == null || inside == null || inside.amount > stored.amount || !fitsOutput(filled)) return false;
        take(inside.amount);
        consumeInput();
        addOutput(filled);
        return true;
    }

    /** A can, cell or portable tank of another mod that holds its fluid in NBT: empty it into the tank, or fill it. */
    private boolean exchangeFluidItem(ItemStack in, IFluidContainerItem item)
    {
        FluidStack inside = item.getFluid(in);
        boolean moved = false;
        if (inside != null && inside.amount > 0)
        {
            int amount = room(inside);
            if (amount > 0)
            {
                FluidStack drained = item.drain(in, amount, true);
                if (drained != null && drained.amount > 0)
                {
                    put(drained, Math.min(drained.amount, amount));
                    moved = true;
                }
            }
            inside = item.getFluid(in);
            if ((inside == null || inside.amount <= 0) && fitsOutput(in)) moveInputToOutput();
            return moved;
        }
        FluidStack stored = tank.getFluid();
        if (stored == null || stored.amount <= 0) return false;
        int filled = item.fill(in, stored.copy(), true);
        if (filled > 0)
        {
            take(Math.min(filled, stored.amount));
            moved = true;
        }
        inside = item.getFluid(in);
        if (inside != null && inside.amount >= item.getCapacity(in) && fitsOutput(in)) moveInputToOutput();
        return moved;
    }

    private boolean fitsOutput(ItemStack stack)
    {
        ItemStack out = slots[SLOT_OUT];
        if (stack == null || out == null) return true;
        return out.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(out, stack)
                && out.stackSize + stack.stackSize <= Math.min(out.getMaxStackSize(), getInventoryStackLimit());
    }

    private void addOutput(ItemStack stack)
    {
        if (stack == null) return;
        if (slots[SLOT_OUT] == null) slots[SLOT_OUT] = stack.copy();
        else slots[SLOT_OUT].stackSize += stack.stackSize;
    }

    private void consumeInput()
    {
        if (--slots[SLOT_IN].stackSize <= 0) slots[SLOT_IN] = null;
    }

    private void moveInputToOutput()
    {
        addOutput(slots[SLOT_IN]);
        slots[SLOT_IN] = null;
    }

    /** A slot changed: empty or fill the containers in the input slot, then fill the pack. Server side only. */
    private void onSlotsChanged()
    {
        if (worldObj == null || worldObj.isRemote || !enabled()) return;
        for (int i = 0; i < 64 && processContainer(); i++) { }
        packMayAccept = true;
        pushToPack();
    }

    /** Marks the chunk for saving without processing the slots again. */
    private void saveChanges()
    {
        super.markDirty();
    }

    @Override
    public void markDirty()
    {
        super.markDirty();
        if (busy) return;
        busy = true;
        try
        {
            onSlotsChanged();
        } finally
        {
            busy = false;
        }
    }

    // ---------------------------------------------------------------- IFluidHandler (pipes of any mod)

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (!enabled() || resource == null || resource.amount <= 0) return 0;
        try
        {
            int amount = room(resource);
            if (amount > 0 && doFill)
            {
                put(resource, amount);
                if (packMayAccept) pushToPack();
            }
            return amount;
        } catch (RuntimeException e)
        {
            return 0;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource == null || !resource.isFluidEqual(tank.getFluid())) return null;
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (!enabled() || maxDrain <= 0) return null;
        FluidStack drained = tank.drain(maxDrain, doDrain);
        if (doDrain && drained != null && drained.amount > 0) saveChanges();
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return enabled() && fluid != null && accepts(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        FluidStack stored = tank.getFluid();
        return enabled() && stored != null && stored.amount > 0 && (fluid == null || stored.getFluid() == fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[]{tank.getInfo()};
    }

    // ---------------------------------------------------------------- client copy of the tank (GUI only)

    /** Fluid id + 1 (0 = empty), for the GUI container. */
    public int syncedFluidId()
    {
        FluidStack stored = tank.getFluid();
        return stored == null || stored.amount <= 0 || stored.getFluid() == null ? 0 : FluidRegistry.getFluidID(stored.getFluid()) + 1;
    }

    public void setClientTank(int fluidIdPlusOne, int amount)
    {
        Fluid fluid = fluidIdPlusOne <= 0 ? null : FluidRegistry.getFluid(fluidIdPlusOne - 1);
        tank.setFluid(fluid == null || amount <= 0 ? null : new FluidStack(fluid, amount));
    }

    // ---------------------------------------------------------------- NBT

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readTank(compound);
        NBTTagList items = compound.getTagList("Items", 10);
        for (int i = 0; i < slots.length; i++) slots[i] = null;
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getByte("Slot");
            if (slot >= 0 && slot < slots.length) slots[slot] = ItemStack.loadItemStackFromNBT(item);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        writeTank(compound);
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < slots.length; i++)
        {
            if (slots[i] == null) continue;
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("Slot", (byte) i);
            slots[i].writeToNBT(item);
            items.appendTag(item);
        }
        compound.setTag("Items", items);
    }

    /** The tank alone, also kept on the dropped block item so breaking the station does not lose its fluid. */
    public void writeTank(NBTTagCompound compound)
    {
        compound.setTag("Tank", tank.writeToNBT(new NBTTagCompound()));
    }

    public void readTank(NBTTagCompound compound)
    {
        tank.setFluid(null);
        if (compound != null && compound.hasKey("Tank")) tank.readFromNBT(compound.getCompoundTag("Tank"));
        // a fluid whose mod was removed reads as null; keep the tank empty then
        if (tank.getFluid() != null && tank.getFluid().getFluid() == null) tank.setFluid(null);
    }

    // ---------------------------------------------------------------- IInventory

    @Override
    public int getSizeInventory()
    {
        return slots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return slot >= 0 && slot < slots.length ? slots[slot] : null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack == null) return null;
        ItemStack taken;
        if (stack.stackSize <= amount)
        {
            taken = stack;
            slots[slot] = null;
        } else
        {
            taken = stack.splitStack(amount);
        }
        markDirty();
        return taken;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (slot < 0 || slot >= slots.length) return;
        slots[slot] = stack;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) stack.stackSize = getInventoryStackLimit();
        markDirty();
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
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64.0;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null) return false;
        if (slot == SLOT_IN) return isFluidContainer(stack);
        if (slot == SLOT_PACK) return isPack(stack);
        return false;
    }

    // ---------------------------------------------------------------- ISidedInventory (hoppers and item pipes)

    /** Containers go in from the top and the sides and come out from the bottom; the pack slot is for players only. */
    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return side == 0 ? FROM_BOTTOM : FROM_OTHER_SIDES;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot == SLOT_IN && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return slot == SLOT_OUT;
    }

    public static boolean isFluidContainer(ItemStack stack)
    {
        return stack != null && (FluidContainerRegistry.isContainer(stack) || stack.getItem() instanceof IFluidContainerItem);
    }
}
