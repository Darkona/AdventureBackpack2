package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.DistilleryRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.inventory.DistilleryItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileDistillery extends TileEntity implements ISidedInventory, IFluidHandler
{
    public static final int SLOT_CATALYST = 9;
    public static final int SLOT_FUEL = 10;
    public static final int SLOT_WATER_IN = 11;
    public static final int SLOT_WATER_OUT = 12;
    public static final int SIZE = 13;
    public static final int LIT = 8;

    private static final int[] FROM_TOP = {SLOT_CATALYST, 0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] FROM_SIDES = {SLOT_FUEL, SLOT_WATER_IN};
    private static final int[] FROM_BOTTOM = {SLOT_WATER_OUT, SLOT_FUEL};
    private static final ForgeDirection[] SIDES = {ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};

    private final ItemStack[] slots = new ItemStack[SIZE];
    private final FluidTank water = new FluidTank(ConfigHandler.DISTILLERY_WATER_CAPACITY);
    private final FluidTank output = new FluidTank(ConfigHandler.DISTILLERY_OUTPUT_CAPACITY);
    private int burnTicks;
    private int fuelTicks;
    private int progress;

    private final int[] yields = new int[DistilleryRules.GRID];
    private int catalystCount;
    private int fuelBurn;
    private boolean slotsStale = true;
    private final IFluidHandler[] neighbours = new IFluidHandler[4];
    private boolean anyNeighbour;
    private int waterSources;
    private boolean neighboursStale = true;
    private int pushStart;
    private boolean pushBlocked;
    private boolean sleeping;
    private int ticks;
    private boolean busy;

    public boolean enabled()
    {
        return ConfigHandler.DISTILLERY_ENABLED;
    }

    @Override
    public void updateEntity()
    {
        if (sleeping || worldObj == null || worldObj.isRemote || !enabled()) return;
        if (neighboursStale) refreshNeighbours();
        ticks++;
        if (DistilleryRules.pushDue(ticks, ConfigHandler.DISTILLERY_PUSH_INTERVAL_TICKS, output.getFluidAmount(), anyNeighbour))
        {
            push();
        }
        int interval = ConfigHandler.DISTILLERY_INTERVAL_TICKS;
        if (ticks % interval != 0) return;
        if (!work(interval) && !pushPending()) sleeping = true;
    }

    public void wake()
    {
        sleeping = false;
        pushBlocked = false;
    }

    public void neighbourChanged()
    {
        neighboursStale = true;
        wake();
    }

    private boolean pushPending()
    {
        return output.getFluidAmount() > 0 && anyNeighbour && !pushBlocked && ConfigHandler.DISTILLERY_PUSH_AMOUNT > 0;
    }

    private boolean work(int interval)
    {
        if (slotsStale) refreshSlots();
        boolean changed = false;
        int perSource = ConfigHandler.DISTILLERY_WATER_PER_SOURCE;
        int fromSources = DistilleryRules.sourceWater(waterSources, perSource, interval, water.getFluidAmount(), water.getCapacity());
        if (fromSources > 0)
        {
            addWater(fromSources);
            changed = true;
        }
        int mask = DistilleryRules.planBatch(yields, outputRoom());
        boolean canStart = DistilleryRules.canStart(mask, catalystCount, ConfigHandler.DISTILLERY_CATALYST_PER_BATCH,
                water.getFluidAmount(), ConfigHandler.DISTILLERY_WATER_PER_BATCH);
        int used = DistilleryRules.burnTicksUsed(burnTicks, interval);
        if (used > 0)
        {
            burnTicks -= used;
            changed = true;
        }
        int before = progress;
        progress = DistilleryRules.advance(progress, used, canStart, ConfigHandler.DISTILLERY_TICKS_PER_BATCH);
        if (progress != before) changed = true;
        if (canStart && DistilleryRules.batchDone(progress, ConfigHandler.DISTILLERY_TICKS_PER_BATCH))
        {
            runBatch(mask);
            progress = 0;
            changed = true;
            refreshSlots();
            mask = DistilleryRules.planBatch(yields, outputRoom());
            canStart = DistilleryRules.canStart(mask, catalystCount, ConfigHandler.DISTILLERY_CATALYST_PER_BATCH,
                    water.getFluidAmount(), ConfigHandler.DISTILLERY_WATER_PER_BATCH);
        }
        if (DistilleryRules.shouldLightFuel(burnTicks, canStart) && lightFuel()) changed = true;
        showFlame(burnTicks > 0);
        if (changed) super.markDirty();
        boolean wantsWater = waterSources > 0 && perSource > 0 && water.getFluidAmount() < water.getCapacity();
        return burnTicks > 0 || canStart || wantsWater;
    }

    private int outputRoom()
    {
        Fluid ethanol = ModFluids.ethanol;
        if (ethanol == null) return 0;
        FluidStack stored = output.getFluid();
        if (stored != null && stored.amount > 0 && stored.getFluid() != ethanol) return 0;
        return output.getCapacity() - output.getFluidAmount();
    }

    private void addWater(int amount)
    {
        FluidStack stored = water.getFluid();
        if (stored == null || stored.amount <= 0) water.setFluid(new FluidStack(FluidRegistry.WATER, amount));
        else stored.amount += amount;
    }

    private void runBatch(int mask)
    {
        int made = DistilleryRules.batchYield(yields, mask);
        for (int i = 0; i < DistilleryRules.GRID; i++)
        {
            if ((mask & (1 << i)) != 0) consume(i, 1);
        }
        if (ConfigHandler.DISTILLERY_CATALYST_PER_BATCH > 0) consume(SLOT_CATALYST, ConfigHandler.DISTILLERY_CATALYST_PER_BATCH);
        if (ConfigHandler.DISTILLERY_WATER_PER_BATCH > 0) water.drain(ConfigHandler.DISTILLERY_WATER_PER_BATCH, true);
        FluidStack stored = output.getFluid();
        if (stored == null || stored.amount <= 0) output.setFluid(new FluidStack(ModFluids.ethanol, made));
        else stored.amount += made;
        slotsStale = true;
    }

    private void consume(int slot, int amount)
    {
        ItemStack stack = slots[slot];
        if (stack == null) return;
        stack.stackSize -= amount;
        if (stack.stackSize <= 0)
        {
            ItemStack left = stack.getItem().getContainerItem(stack);
            slots[slot] = left != null && left.stackSize > 0 ? left : null;
        }
    }

    private boolean lightFuel()
    {
        ItemStack fuel = slots[SLOT_FUEL];
        if (fuel == null || fuelBurn <= 0) return false;
        burnTicks = fuelTicks = fuelBurn;
        consume(SLOT_FUEL, 1);
        slotsStale = true;
        return true;
    }

    private void showFlame(boolean lit)
    {
        int meta = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        if (((meta & LIT) != 0) == lit) return;
        worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, lit ? meta | LIT : meta & ~LIT, 2);
        worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
    }

    private void push()
    {
        int offer = DistilleryRules.pushAmount(output.getFluidAmount(), ConfigHandler.DISTILLERY_PUSH_AMOUNT);
        FluidStack stored = output.getFluid();
        if (offer <= 0 || stored == null || stored.getFluid() == null) return;
        boolean moved = false;
        for (int k = 0; k < SIDES.length && offer > 0; k++)
        {
            int side = (pushStart + k) % SIDES.length;
            IFluidHandler target = neighbours[side];
            if (target == null) continue;
            if (target instanceof TileEntity && ((TileEntity) target).isInvalid())
            {
                neighboursStale = true;
                continue;
            }
            int took;
            try
            {
                took = target.fill(SIDES[side].getOpposite(), new FluidStack(stored.getFluid(), offer), true);
            } catch (RuntimeException foreign)
            {
                took = 0;
            }
            if (took <= 0) continue;
            took = Math.min(took, offer);
            output.drain(took, true);
            offer -= took;
            moved = true;
        }
        pushStart = DistilleryRules.nextStartSide(pushStart, SIDES.length);
        pushBlocked = !moved;
        if (moved) super.markDirty();
    }

    private void refreshNeighbours()
    {
        neighboursStale = false;
        anyNeighbour = false;
        int sources = 0;
        for (int i = 0; i < SIDES.length; i++)
        {
            ForgeDirection d = SIDES[i];
            int x = xCoord + d.offsetX, z = zCoord + d.offsetZ;
            neighbours[i] = null;
            if (!worldObj.blockExists(x, yCoord, z)) continue;
            TileEntity te = worldObj.getTileEntity(x, yCoord, z);
            if (te instanceof IFluidHandler && !(te instanceof TileDistillery))
            {
                neighbours[i] = (IFluidHandler) te;
                anyNeighbour = true;
            }
            if (isWaterSource(x, yCoord, z)) sources++;
        }
        if (yCoord > 0 && isWaterSource(xCoord, yCoord - 1, zCoord)) sources++;
        waterSources = sources;
    }

    private boolean isWaterSource(int x, int y, int z)
    {
        Block block = worldObj.getBlock(x, y, z);
        return block.getMaterial() == Material.water && worldObj.getBlockMetadata(x, y, z) == 0;
    }

    private void refreshSlots()
    {
        slotsStale = false;
        for (int i = 0; i < DistilleryRules.GRID; i++)
        {
            yields[i] = slots[i] == null ? 0 : DistilleryItems.organicYield(slots[i]);
        }
        ItemStack catalyst = slots[SLOT_CATALYST];
        catalystCount = catalyst != null && DistilleryItems.isCatalyst(catalyst) ? catalyst.stackSize : 0;
        fuelBurn = DistilleryItems.burnTime(slots[SLOT_FUEL]);
    }

    private boolean processWaterContainer()
    {
        ItemStack in = slots[SLOT_WATER_IN];
        if (in == null) return false;
        try
        {
            FluidStack inside = FluidContainerRegistry.getFluidForFilledItem(in);
            if (inside == null || inside.getFluid() != FluidRegistry.WATER) return false;
            if (water.getCapacity() - water.getFluidAmount() < inside.amount) return false;
            ItemStack empty = FluidContainerRegistry.drainFluidContainer(in);
            ItemStack out = slots[SLOT_WATER_OUT];
            if (empty != null && out != null && !(out.isItemEqual(empty) && ItemStack.areItemStackTagsEqual(out, empty)
                    && out.stackSize + empty.stackSize <= out.getMaxStackSize())) return false;
            addWater(inside.amount);
            if (--in.stackSize <= 0) slots[SLOT_WATER_IN] = null;
            if (empty != null)
            {
                if (out == null) slots[SLOT_WATER_OUT] = empty.copy();
                else out.stackSize += empty.stackSize;
            }
            return true;
        } catch (RuntimeException foreign)
        {
            return false;
        }
    }

    public boolean fillByHand(int amount)
    {
        if (!enabled() || amount <= 0 || water.getCapacity() - water.getFluidAmount() < amount) return false;
        addWater(amount);
        wake();
        super.markDirty();
        return true;
    }

    public FluidTank getWater()
    {
        return water;
    }

    public FluidTank getOutput()
    {
        return output;
    }

    public int getBurnTicks()
    {
        return burnTicks;
    }

    public int getFuelTicks()
    {
        return fuelTicks;
    }

    public int getProgress()
    {
        return progress;
    }

    public void setClientState(int waterAmount, int outputFluidId, int outputAmount, int burnTicks, int fuelTicks, int progress)
    {
        water.setFluid(waterAmount <= 0 ? null : new FluidStack(FluidRegistry.WATER, waterAmount));
        Fluid fluid = outputFluidId < 0 ? null : FluidRegistry.getFluid(outputFluidId);
        output.setFluid(fluid == null || outputAmount <= 0 ? null : new FluidStack(fluid, outputAmount));
        this.burnTicks = burnTicks;
        this.fuelTicks = fuelTicks;
        this.progress = progress;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        readTank(water, compound, "Water");
        readTank(output, compound, "Output");
        burnTicks = compound.getInteger("BurnTicks");
        fuelTicks = compound.getInteger("FuelTicks");
        progress = compound.getInteger("Progress");
        NBTTagList items = compound.getTagList("Items", 10);
        for (int i = 0; i < slots.length; i++) slots[i] = null;
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getByte("Slot");
            if (slot >= 0 && slot < slots.length) slots[slot] = ItemStack.loadItemStackFromNBT(item);
        }
        slotsStale = true;
        neighboursStale = true;
        sleeping = false;
    }

    private static void readTank(FluidTank tank, NBTTagCompound compound, String key)
    {
        tank.setFluid(null);
        if (compound.hasKey(key)) tank.readFromNBT(compound.getCompoundTag(key));
        if (tank.getFluid() != null && tank.getFluid().getFluid() == null) tank.setFluid(null);
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("Water", water.writeToNBT(new NBTTagCompound()));
        compound.setTag("Output", output.writeToNBT(new NBTTagCompound()));
        compound.setInteger("BurnTicks", burnTicks);
        compound.setInteger("FuelTicks", fuelTicks);
        compound.setInteger("Progress", progress);
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

    @Override
    public void markDirty()
    {
        super.markDirty();
        slotsStale = true;
        wake();
        if (busy || worldObj == null || worldObj.isRemote || !enabled()) return;
        busy = true;
        try
        {
            for (int i = 0; i < 64 && processWaterContainer(); i++) { }
        } finally
        {
            busy = false;
        }
    }

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
        if (stack != null && stack.stackSize > getInventoryStackLimit()) stack.stackSize = getInventoryStackLimit();
        slots[slot] = stack;
        markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return "container.adventurebackpack:distillery";
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
        if (slot >= 0 && slot < DistilleryRules.GRID) return DistilleryItems.organicYield(stack) > 0;
        switch (slot)
        {
            case SLOT_CATALYST:
                return DistilleryItems.isCatalyst(stack);
            case SLOT_FUEL:
                return DistilleryItems.burnTime(stack) > 0;
            case SLOT_WATER_IN:
                return DistilleryItems.isWaterContainer(stack);
            default:
                return false;
        }
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        if (side == 1) return FROM_TOP;
        return side == 0 ? FROM_BOTTOM : FROM_SIDES;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot != SLOT_WATER_OUT && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        if (slot == SLOT_WATER_OUT) return true;
        return slot == SLOT_FUEL && DistilleryItems.burnTime(stack) <= 0;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (!enabled() || resource == null || resource.amount <= 0 || resource.getFluid() != FluidRegistry.WATER) return 0;
        int filled = water.fill(resource, doFill);
        if (doFill && filled > 0)
        {
            wake();
            super.markDirty();
        }
        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource == null || !resource.isFluidEqual(output.getFluid())) return null;
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (!enabled() || maxDrain <= 0) return null;
        FluidStack drained = output.drain(maxDrain, doDrain);
        if (doDrain && drained != null && drained.amount > 0)
        {
            wake();
            super.markDirty();
        }
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return enabled() && fluid == FluidRegistry.WATER;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        FluidStack stored = output.getFluid();
        return enabled() && stored != null && stored.amount > 0 && (fluid == null || stored.getFluid() == fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[]{water.getInfo(), output.getInfo()};
    }
}
