package com.darkona.adventurebackpack.block;

import adventurebackpack.api.IInventoryTanks;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.GearType;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.inventory.InventoryFlamethrowerPack;
import com.darkona.adventurebackpack.inventory.InventoryScubaGear;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import com.darkona.adventurebackpack.inventory.AirContainers;
import com.darkona.adventurebackpack.common.StationTransfer;
import com.darkona.adventurebackpack.reference.GeneralReference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileGear extends TileEntity implements IFluidHandler
{
    private static final String GEAR_KEY = "gear";

    private ItemStack gear;
    private IInventoryTanks inventory;
    private boolean syncPending;
    private Object syncedData, syncedTankA, syncedTankB, syncedSlots;

    public ItemStack getGear()
    {
        return gear;
    }

    public GearType type()
    {
        return GearType.of(gear);
    }

    public void setGear(ItemStack stack)
    {
        gear = stack;
        inventory = null;
        GearType type = GearType.of(stack);
        if (type != null) type.stopRunning(stack);
        rememberSynced();
    }

    public ItemStack takeGear()
    {
        ItemStack taken = gear;
        gear = null;
        inventory = null;
        if (taken != null) markDirty();
        return taken;
    }

    public boolean enabled()
    {
        GearType type = type();
        return type != null && type.placeable();
    }

    public IInventoryTanks inventory()
    {
        if (gear == null) return null;
        if (inventory == null) inventory = type().newInventory(gear);
        return inventory;
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return gear != null && worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64;
    }

    public void afterContainerTick()
    {
        if (gear == null || worldObj == null || worldObj.isRemote || sameAsSynced()) return;
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        rememberSynced();
    }

    private void changedByPipe()
    {
        markDirty();
        if (!syncPending && worldObj != null && !worldObj.isRemote)
        {
            syncPending = true;
            worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), ConfigHandler.PLACEDGEAR_SYNC_INTERVAL_TICKS);
        }
    }

    public void flushSync()
    {
        if (!syncPending) return;
        syncPending = false;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        rememberSynced();
    }

    private NBTTagCompound dataTag()
    {
        if (gear == null || gear.stackTagCompound == null) return null;
        GearType type = type();
        if (type == null) return null;
        if (type.dataKey == null) return gear.stackTagCompound;
        NBTBase data = gear.stackTagCompound.getTag(type.dataKey);
        return data instanceof NBTTagCompound ? (NBTTagCompound) data : null;
    }

    private boolean sameAsSynced()
    {
        NBTTagCompound data = dataTag();
        if (data != syncedData) return false;
        if (data == null) return true;
        return data.getTag(tankKey(0)) == syncedTankA && data.getTag(tankKey(1)) == syncedTankB && data.getTag("inventory") == syncedSlots;
    }

    private void rememberSynced()
    {
        NBTTagCompound data = dataTag();
        syncedData = data;
        syncedTankA = data == null ? null : data.getTag(tankKey(0));
        syncedTankB = data == null ? null : data.getTag(tankKey(1));
        syncedSlots = data == null ? null : data.getTag("inventory");
    }

    private String tankKey(int tank)
    {
        switch (type())
        {
            case JETPACK:
                return tank == 0 ? "waterTank" : "steamTank";
            case COPTER:
                return tank == 0 ? "fuelTank" : "status";
            default:
                return tank == 0 ? "leftTank" : "rightTank";
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        gear = compound.hasKey(GEAR_KEY, Constants.NBT.TAG_COMPOUND) ? ItemStack.loadItemStackFromNBT(compound.getCompoundTag(GEAR_KEY)) : null;
        inventory = null;
        rememberSynced();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        if (gear != null) compound.setTag(GEAR_KEY, gear.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        writeToNBT(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public boolean canUpdate()
    {
        return false;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource == null || resource.amount <= 0 || resource.getFluid() == null || !enabled()) return 0;
        IInventoryTanks inv = inventory();
        int filled;
        switch (type())
        {
            case JETPACK:
                filled = resource.getFluid() == FluidRegistry.WATER ? ((InventorySteamJetpack) inv).getWaterTank().fill(resource, doFill) : 0;
                break;
            case COPTER:
                FluidTank tank = ((InventoryCopterPack) inv).getFuelTank();
                FluidStack stored = tank.getFluid();
                filled = GeneralReference.isValidFuel(resource)
                        && StationTransfer.sameOrEmpty(stored == null ? null : stored.getFluid().getName(), resource.getFluid().getName())
                        ? tank.fill(resource, doFill) : 0;
                break;
            case SCUBA:
                FluidStack air = AirContainers.toStored(resource);
                filled = air == null ? 0 : ((InventoryScubaGear) inv).fill(air, doFill);
                break;
            default:
                filled = ((InventoryFlamethrowerPack) inv).fill(resource, doFill);
                break;
        }
        if (doFill && filled > 0)
        {
            inv.dirtyTanks();
            changedByPipe();
        }
        return filled;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        FluidTank steam = steamTank();
        if (resource == null || steam == null || !resource.isFluidEqual(steam.getFluid())) return null;
        return drain(from, resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        FluidTank steam = steamTank();
        if (steam == null || maxDrain <= 0) return null;
        FluidStack drained = steam.drain(maxDrain, doDrain);
        if (doDrain && drained != null && drained.amount > 0)
        {
            inventory().dirtyTanks();
            changedByPipe();
        }
        return drained;
    }

    private FluidTank steamTank()
    {
        return type() == GearType.JETPACK && enabled() ? ((InventorySteamJetpack) inventory()).getSteamTank() : null;
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        if (fluid == null || !enabled()) return false;
        switch (type())
        {
            case JETPACK:
                return fluid == FluidRegistry.WATER;
            case SCUBA:
                return AirContainers.isBreathable(fluid);
            default:
                return GeneralReference.isValidFuel(fluid);
        }
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        FluidTank steam = steamTank();
        FluidStack stored = steam == null ? null : steam.getFluid();
        return stored != null && stored.amount > 0 && (fluid == null || stored.getFluid() == fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        IInventoryTanks inv = inventory();
        if (inv == null) return new FluidTankInfo[0];
        FluidTank[] tanks = inv.getTanksArray();
        FluidTankInfo[] info = new FluidTankInfo[tanks.length];
        for (int i = 0; i < tanks.length; i++) info[i] = tanks[i].getInfo();
        return info;
    }
}
