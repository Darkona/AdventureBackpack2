package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.block.TileDistillery;
import com.darkona.adventurebackpack.common.DistilleryRules;
import com.darkona.adventurebackpack.compat.invtweaks.DistillerySections;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSectionCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Map;

@ChestContainer(showButtons = false, rowSize = 3)
public class ContainerDistillery extends Container
{
    public static final int MACHINE_SLOTS = TileDistillery.SIZE;
    public static final int PLAYER_START = MACHINE_SLOTS;
    public static final int HOTBAR_START = PLAYER_START + 27;
    public static final int SLOT_COUNT = HOTBAR_START + 9;

    private static final int BAR_WATER_LOW = 0;
    private static final int BAR_WATER_HIGH = 1;
    private static final int BAR_OUT_FLUID = 2;
    private static final int BAR_OUT_LOW = 3;
    private static final int BAR_OUT_HIGH = 4;
    private static final int BAR_BURN = 5;
    private static final int BAR_FUEL = 6;
    private static final int BAR_PROGRESS = 7;

    private final TileDistillery tile;
    private int sentWater = -1, sentFluid = -2, sentOut = -1, sentBurn = -1, sentFuel = -1, sentProgress = -1;
    private int clientWater, clientFluid = -1, clientOut, clientBurn, clientFuel, clientProgress;
    private Map<?, ?> invTweaksSections;

    public ContainerDistillery(InventoryPlayer invPlayer, TileDistillery tile)
    {
        this.tile = tile;
        for (int i = 0; i < DistilleryRules.GRID; i++)
        {
            addSlotToContainer(new MachineSlot(tile, i, 54 + 18 * (i % 3), 17 + 18 * (i / 3)));
        }
        addSlotToContainer(new MachineSlot(tile, TileDistillery.SLOT_CATALYST, 112, 17));
        addSlotToContainer(new MachineSlot(tile, TileDistillery.SLOT_FUEL, 112, 53));
        addSlotToContainer(new MachineSlot(tile, TileDistillery.SLOT_WATER_IN, 30, 22));
        addSlotToContainer(new MachineSlot(tile, TileDistillery.SLOT_WATER_OUT, 30, 52));
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, x + y * 9 + 9, 8 + 18 * x, 84 + y * 18));
            }
        }
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, 8 + 18 * x, 142));
        }
    }

    public TileDistillery tile()
    {
        return tile;
    }

    @ContainerSectionCallback
    public Map<?, ?> invTweaksSections()
    {
        if (invTweaksSections == null) invTweaksSections = DistillerySections.of(this);
        return invTweaksSections;
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
        int water = tile.getWater().getFluidAmount();
        FluidStack out = tile.getOutput().getFluid();
        int outAmount = out == null || out.getFluid() == null ? 0 : out.amount;
        int fluidId = outAmount <= 0 ? -1 : out.getFluidID();
        int burn = Math.min(tile.getBurnTicks(), Short.MAX_VALUE);
        int fuel = Math.min(tile.getFuelTicks(), Short.MAX_VALUE);
        int progress = Math.min(tile.getProgress(), Short.MAX_VALUE);
        for (Object crafter : crafters)
        {
            ICrafting c = (ICrafting) crafter;
            if (water != sentWater)
            {
                c.sendProgressBarUpdate(this, BAR_WATER_LOW, water & 0x7FFF);
                c.sendProgressBarUpdate(this, BAR_WATER_HIGH, water >> 15);
            }
            if (fluidId != sentFluid) c.sendProgressBarUpdate(this, BAR_OUT_FLUID, fluidId);
            if (outAmount != sentOut)
            {
                c.sendProgressBarUpdate(this, BAR_OUT_LOW, outAmount & 0x7FFF);
                c.sendProgressBarUpdate(this, BAR_OUT_HIGH, outAmount >> 15);
            }
            if (burn != sentBurn) c.sendProgressBarUpdate(this, BAR_BURN, burn);
            if (fuel != sentFuel) c.sendProgressBarUpdate(this, BAR_FUEL, fuel);
            if (progress != sentProgress) c.sendProgressBarUpdate(this, BAR_PROGRESS, progress);
        }
        sentWater = water;
        sentFluid = fluidId;
        sentOut = outAmount;
        sentBurn = burn;
        sentFuel = fuel;
        sentProgress = progress;
    }

    @Override
    public void addCraftingToCrafters(ICrafting crafter)
    {
        super.addCraftingToCrafters(crafter);
        sentWater = -1;
        sentFluid = -2;
        sentOut = -1;
        sentBurn = -1;
        sentFuel = -1;
        sentProgress = -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int value)
    {
        switch (id)
        {
            case BAR_WATER_LOW:
                clientWater = (clientWater & ~0x7FFF) | (value & 0x7FFF);
                break;
            case BAR_WATER_HIGH:
                clientWater = (clientWater & 0x7FFF) | (value << 15);
                break;
            case BAR_OUT_FLUID:
                clientFluid = value;
                break;
            case BAR_OUT_LOW:
                clientOut = (clientOut & ~0x7FFF) | (value & 0x7FFF);
                break;
            case BAR_OUT_HIGH:
                clientOut = (clientOut & 0x7FFF) | (value << 15);
                break;
            case BAR_BURN:
                clientBurn = value;
                break;
            case BAR_FUEL:
                clientFuel = value;
                break;
            case BAR_PROGRESS:
                clientProgress = value;
                break;
            default:
                return;
        }
        tile.setClientState(clientWater, clientFluid, clientOut, clientBurn, clientFuel, clientProgress);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index)
    {
        Slot slot = index >= 0 && index < inventorySlots.size() ? (Slot) inventorySlots.get(index) : null;
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();
        if (index < MACHINE_SLOTS)
        {
            if (!mergeItemStack(stack, PLAYER_START, SLOT_COUNT, true)) return null;
        } else if (!toMachine(stack))
        {
            return null;
        }
        if (stack.stackSize == 0) slot.putStack(null);
        else slot.onSlotChanged();
        if (stack.stackSize == result.stackSize) return null;
        slot.onPickupFromSlot(player, stack);
        return result;
    }

    private boolean toMachine(ItemStack stack)
    {
        if (tile.isItemValidForSlot(TileDistillery.SLOT_CATALYST, stack))
        {
            return mergeItemStack(stack, TileDistillery.SLOT_CATALYST, TileDistillery.SLOT_CATALYST + 1, false);
        }
        if (tile.isItemValidForSlot(0, stack)) return mergeItemStack(stack, 0, DistilleryRules.GRID, false);
        if (tile.isItemValidForSlot(TileDistillery.SLOT_FUEL, stack))
        {
            return mergeItemStack(stack, TileDistillery.SLOT_FUEL, TileDistillery.SLOT_FUEL + 1, false);
        }
        if (tile.isItemValidForSlot(TileDistillery.SLOT_WATER_IN, stack))
        {
            return mergeItemStack(stack, TileDistillery.SLOT_WATER_IN, TileDistillery.SLOT_WATER_IN + 1, false);
        }
        return false;
    }

    private static class MachineSlot extends Slot
    {
        MachineSlot(TileDistillery tile, int index, int x, int y)
        {
            super(tile, index, x, y);
        }

        @Override
        public boolean isItemValid(ItemStack stack)
        {
            return inventory.isItemValidForSlot(getSlotIndex(), stack);
        }
    }
}
