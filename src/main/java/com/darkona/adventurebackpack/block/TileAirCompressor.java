package com.darkona.adventurebackpack.block;

import com.darkona.adventurebackpack.common.AirCompressorRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.ModFluids;
import com.darkona.adventurebackpack.inventory.AirContainers;
import com.darkona.adventurebackpack.inventory.InventoryScubaGear;
import com.darkona.adventurebackpack.item.ItemScubaGear;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

/**
 * The Air Compressor: an air tank, a furnace that burns any furnace fuel into air, a hand pump, two container slots
 * (air cells in, empty or filled cells out) and a slot that fills a Scuba Gear. Pipes of other mods can push any
 * breathable fluid in and pull the air out (IFluidHandler, by fluid name: no dependency).
 * <p>
 * Server only, on a beat of scuba.compressor.intervalTicks: between beats the tick is a counter and a return. The
 * GUI gets its numbers from the container, only while open and only when they change.
 */
public class TileAirCompressor extends TileEntity implements IInventory, IFluidHandler
{
    public static final int SLOT_FUEL = 0;
    public static final int SLOT_IN = 1;
    public static final int SLOT_OUT = 2;
    public static final int SLOT_GEAR = 3;

    private final ItemStack[] slots = new ItemStack[4];
    private final FluidTank tank = new FluidTank(ConfigHandler.COMPRESSOR_TANK_CAPACITY);
    private int burnTicks;
    private int fuelTicks;
    private int beat;
    /** Not saved: the gear in its slot was full at the last beat; cleared when the slot changes. */
    private boolean gearFull;
    private long lastStroke = Long.MIN_VALUE / 2;

    @Override
    public void updateEntity()
    {
        if (worldObj == null || worldObj.isRemote || !ConfigHandler.SCUBA_ENABLED) return;
        if (++beat < ConfigHandler.COMPRESSOR_INTERVAL_TICKS) return;
        beat = 0;
        work(ConfigHandler.COMPRESSOR_INTERVAL_TICKS);
    }

    private void work(int ticks)
    {
        boolean changed = false;
        Fluid air = ModFluids.air;
        boolean takesAir = air != null && (tank.getFluid() == null || tank.getFluid().getFluid() == air);
        boolean openAir = hasOpenAir();
        int capacity = tank.getCapacity();
        if (burnTicks > 0)
        {
            int used = AirCompressorRules.burnTicksUsed(burnTicks, ticks);
            burnTicks -= used;
            int made = takesAir ? AirCompressorRules.furnaceAir(used, ConfigHandler.COMPRESSOR_AIR_PER_FUEL_TICK, openAir, tank.getFluidAmount(), capacity) : 0;
            if (made > 0) tank.fill(new FluidStack(air, made), true);
            changed = true;
        }
        if (takesAir && AirCompressorRules.shouldLightFuel(burnTicks, ConfigHandler.COMPRESSOR_AIR_PER_FUEL_TICK, openAir, tank.getFluidAmount(), capacity))
        {
            changed |= lightFuel();
        }
        if (slots[SLOT_IN] != null && AirContainers.processAll(slots, SLOT_IN, SLOT_OUT, tank))
        {
            changed = true;
        }
        if (!gearFull && slots[SLOT_GEAR] != null && tank.getFluidAmount() > 0 && slots[SLOT_GEAR].getItem() instanceof ItemScubaGear)
        {
            InventoryScubaGear gear = new InventoryScubaGear(slots[SLOT_GEAR]);
            int amount = AirCompressorRules.fillAmount(ConfigHandler.COMPRESSOR_FILL_PER_TICK, ticks, tank.getFluidAmount(), gear.room(tank.getFluid()));
            if (amount > 0)
            {
                changed |= moveInto(gear, amount) > 0;
            } else
            {
                gearFull = true;
            }
        }
        if (changed) markDirty();
    }

    private boolean hasOpenAir()
    {
        return !ConfigHandler.COMPRESSOR_NEEDS_OPEN_AIR || worldObj.isAirBlock(xCoord, yCoord + 1, zCoord);
    }

    private boolean lightFuel()
    {
        ItemStack fuel = slots[SLOT_FUEL];
        int time = fuel == null ? 0 : TileEntityFurnace.getItemBurnTime(fuel);
        if (time <= 0) return false;
        burnTicks = fuelTicks = time;
        if (--fuel.stackSize <= 0)
        {
            slots[SLOT_FUEL] = fuel.getItem().getContainerItem(fuel);
        }
        return true;
    }

    /** Moves up to {@code max} millibuckets into the gear and saves it once. Returns what moved. */
    private int moveInto(InventoryScubaGear gear, int max)
    {
        FluidStack offer = tank.drain(max, false);
        if (offer == null || offer.amount <= 0) return 0;
        int moved = gear.fill(offer, true);
        if (moved <= 0) return 0;
        tank.drain(moved, true);
        gear.markDirty();
        return moved;
    }

    /** One stroke of the hand pump (right click with an empty hand). Returns false when the pump is turned off. */
    public boolean pump(EntityPlayer player)
    {
        if (!ConfigHandler.SCUBA_ENABLED || ConfigHandler.COMPRESSOR_HAND_PUMP_AMOUNT <= 0 || ModFluids.air == null) return false;
        long now = worldObj.getTotalWorldTime();
        if (!AirCompressorRules.pumpReady(now, lastStroke, ConfigHandler.COMPRESSOR_HAND_PUMP_COOLDOWN_TICKS)) return true;
        lastStroke = now;
        if (!hasOpenAir())
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.compressor.noair"));
            return true;
        }
        if (tank.getFluid() != null && tank.getFluid().getFluid() != ModFluids.air) return true;
        int amount = AirCompressorRules.pumpAmount(ConfigHandler.COMPRESSOR_HAND_PUMP_AMOUNT, tank.getFluidAmount(), tank.getCapacity());
        if (amount > 0)
        {
            tank.fill(new FluidStack(ModFluids.air, amount), true);
            gearFull = false;
            markDirty();
        }
        worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "tile.piston.out", 0.3F, 1.4F + worldObj.rand.nextFloat() * 0.2F);
        return true;
    }

    /** Fills a Scuba Gear (worn or held) with all the air that fits, at once, and says how much moved. */
    public void fillGearByHand(EntityPlayer player, ItemStack gearStack)
    {
        if (!ConfigHandler.SCUBA_ENABLED || gearStack == null || !(gearStack.getItem() instanceof ItemScubaGear)) return;
        if (tank.getFluidAmount() <= 0)
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.compressor.empty"));
            return;
        }
        InventoryScubaGear gear = new InventoryScubaGear(gearStack);
        int moved = moveInto(gear, tank.getFluidAmount());
        if (moved > 0)
        {
            markDirty();
            BackpackProperty props = BackpackProperty.get(player);
            if (props != null && props.getWearable() == gearStack) props.setScubaHasAir(gear.getAir() > 0);
        }
        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.compressor.filled", moved));
    }

    // ---------------------------------------------------------------- GUI numbers

    public FluidTank getTank()
    {
        return tank;
    }

    public int getBurnTicks()
    {
        return burnTicks;
    }

    public int getFuelTicks()
    {
        return fuelTicks;
    }

    /** Client side: the container sets the numbers the server sent. */
    public void setClientState(int air, int fluidId, int burnTicks, int fuelTicks)
    {
        Fluid fluid = fluidId < 0 ? null : net.minecraftforge.fluids.FluidRegistry.getFluid(fluidId);
        tank.setFluid(fluid == null || air <= 0 ? null : new FluidStack(fluid, air));
        this.burnTicks = burnTicks;
        this.fuelTicks = fuelTicks;
    }

    // ---------------------------------------------------------------- NBT

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        tank.setFluid(null);
        tank.readFromNBT(compound.getCompoundTag("tank"));
        burnTicks = compound.getInteger("burnTicks");
        fuelTicks = compound.getInteger("fuelTicks");
        NBTTagList items = compound.getTagList("items", 10);
        for (int i = 0; i < slots.length; i++) slots[i] = null;
        for (int i = 0; i < items.tagCount(); i++)
        {
            NBTTagCompound item = items.getCompoundTagAt(i);
            int slot = item.getByte("slot");
            if (slot >= 0 && slot < slots.length) slots[slot] = ItemStack.loadItemStackFromNBT(item);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setTag("tank", tank.writeToNBT(new NBTTagCompound()));
        compound.setInteger("burnTicks", burnTicks);
        compound.setInteger("fuelTicks", fuelTicks);
        NBTTagList items = new NBTTagList();
        for (int i = 0; i < slots.length; i++)
        {
            if (slots[i] == null) continue;
            NBTTagCompound item = new NBTTagCompound();
            item.setByte("slot", (byte) i);
            slots[i].writeToNBT(item);
            items.appendTag(item);
        }
        compound.setTag("items", items);
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
        if (slot < 0 || slot >= slots.length || slots[slot] == null) return null;
        ItemStack taken;
        if (slots[slot].stackSize <= amount)
        {
            taken = slots[slot];
            slots[slot] = null;
        } else
        {
            taken = slots[slot].splitStack(amount);
        }
        if (slot == SLOT_GEAR) gearFull = false;
        markDirty();
        return taken;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) slots[slot] = null;
        return stack;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (slot < 0 || slot >= slots.length) return;
        if (stack != null && stack.stackSize > getInventoryStackLimit()) stack.stackSize = getInventoryStackLimit();
        slots[slot] = stack;
        if (slot == SLOT_GEAR) gearFull = false;
        markDirty();
    }

    @Override
    public String getInventoryName()
    {
        return "container.adventurebackpack:airCompressor";
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
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null) return false;
        switch (slot)
        {
            case SLOT_FUEL:
                return TileEntityFurnace.isItemFuel(stack);
            case SLOT_IN:
                return AirContainers.isAirContainer(stack);
            case SLOT_GEAR:
                return stack.getItem() instanceof ItemScubaGear;
            default:
                return false;
        }
    }

    // ---------------------------------------------------------------- IFluidHandler (pipes of other mods)

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (!ConfigHandler.SCUBA_ENABLED) return 0;
        FluidStack stored = AirContainers.toStored(resource);
        if (stored == null) return 0;
        int filled = tank.fill(stored, doFill);
        if (doFill && filled > 0)
        {
            gearFull = false;
            markDirty();
        }
        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource == null || tank.getFluid() == null || !resource.isFluidEqual(tank.getFluid())) return null;
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        if (!ConfigHandler.SCUBA_ENABLED) return null;
        FluidStack drained = tank.drain(maxDrain, doDrain);
        if (doDrain && drained != null && drained.amount > 0) markDirty();
        return drained;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return ConfigHandler.SCUBA_ENABLED && AirContainers.isBreathable(fluid);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return ConfigHandler.SCUBA_ENABLED && fluid != null && tank.getFluid() != null && tank.getFluid().getFluid() == fluid;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return new FluidTankInfo[]{tank.getInfo()};
    }
}
