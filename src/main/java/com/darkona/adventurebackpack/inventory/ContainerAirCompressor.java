package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.TileAirCompressor;
import com.darkona.adventurebackpack.item.ItemScubaGear;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.FluidStack;

public class ContainerAirCompressor extends Container
{
    private static final int PLAYER_SLOTS = 36;
    private static final int BAR_AIR_LOW = 0;
    private static final int BAR_AIR_HIGH = 1;
    private static final int BAR_FLUID = 2;
    private static final int BAR_BURN = 3;
    private static final int BAR_FUEL = 4;

    private final TileAirCompressor tile;
    private int sentAir = -1;
    private int sentFluid = -2;
    private int sentBurn = -1;
    private int sentFuel = -1;
    private int clientAir, clientFluid = -1, clientBurn, clientFuel;

    public ContainerAirCompressor(InventoryPlayer invPlayer, TileAirCompressor tile)
    {
        this.tile = tile;
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 142));
        }
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 84 + y * 18));
            }
        }
        addSlotToContainer(new Slot(tile, TileAirCompressor.SLOT_FUEL, 77, 64)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return TileEntityFurnace.isItemFuel(stack);
            }
        });
        addSlotToContainer(new SlotAirContainer(tile, TileAirCompressor.SLOT_IN, 30, 22, true));
        addSlotToContainer(new SlotAirContainer(tile, TileAirCompressor.SLOT_OUT, 30, 52, false));
        addSlotToContainer(new Slot(tile, TileAirCompressor.SLOT_GEAR, 126, 34)
        {
            @Override
            public boolean isItemValid(ItemStack stack)
            {
                return stack != null && stack.getItem() instanceof ItemScubaGear;
            }

            @Override
            public int getSlotStackLimit()
            {
                return 1;
            }
        });
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return tile.isUseableByPlayer(player);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        FluidStack fluid = tile.getTank().getFluid();
        int air = fluid == null ? 0 : fluid.amount;
        int fluidId = fluid == null ? -1 : fluid.getFluidID();
        int burn = tile.getBurnTicks();
        int fuel = tile.getFuelTicks();
        for (Object crafter : crafters)
        {
            ICrafting c = (ICrafting) crafter;
            if (air != sentAir)
            {
                c.sendProgressBarUpdate(this, BAR_AIR_LOW, air & 0x7FFF);
                c.sendProgressBarUpdate(this, BAR_AIR_HIGH, air >> 15);
            }
            if (fluidId != sentFluid) c.sendProgressBarUpdate(this, BAR_FLUID, fluidId);
            if (burn != sentBurn) c.sendProgressBarUpdate(this, BAR_BURN, Math.min(burn, Short.MAX_VALUE));
            if (fuel != sentFuel) c.sendProgressBarUpdate(this, BAR_FUEL, Math.min(fuel, Short.MAX_VALUE));
        }
        sentAir = air;
        sentFluid = fluidId;
        sentBurn = burn;
        sentFuel = fuel;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter)
    {
        super.addCraftingToCrafters(crafter);
        sentAir = -1;
        sentFluid = -2;
        sentBurn = -1;
        sentFuel = -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        switch (id)
        {
            case BAR_AIR_LOW:
                clientAir = (clientAir & ~0x7FFF) | (value & 0x7FFF);
                break;
            case BAR_AIR_HIGH:
                clientAir = (clientAir & 0x7FFF) | (value << 15);
                break;
            case BAR_FLUID:
                clientFluid = value;
                break;
            case BAR_BURN:
                clientBurn = value;
                break;
            case BAR_FUEL:
                clientFuel = value;
                break;
            default:
                return;
        }
        tile.setClientState(clientAir, clientFluid, clientBurn, clientFuel);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        Slot slot = getSlot(index);
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();
        int first = PLAYER_SLOTS;
        if (index >= PLAYER_SLOTS)
        {
            if (!mergeItemStack(stack, 0, PLAYER_SLOTS, false)) return null;
        } else if (stack.getItem() instanceof ItemScubaGear)
        {
            Slot gear = getSlot(first + TileAirCompressor.SLOT_GEAR);
            if (gear.getHasStack()) return null;
            gear.putStack(stack.splitStack(1));
        } else if (AirContainers.isAirContainer(stack))
        {
            if (!mergeItemStack(stack, first + TileAirCompressor.SLOT_IN, first + TileAirCompressor.SLOT_IN + 1, false)) return null;
        } else if (TileEntityFurnace.isItemFuel(stack))
        {
            if (!mergeItemStack(stack, first + TileAirCompressor.SLOT_FUEL, first + TileAirCompressor.SLOT_FUEL + 1, false)) return null;
        } else
        {
            return null;
        }
        if (stack.stackSize == 0)
        {
            slot.putStack(null);
        } else
        {
            slot.onSlotChanged();
        }
        if (stack.stackSize == result.stackSize) return null;
        slot.onPickupFromSlot(player, stack);
        return result;
    }
}
