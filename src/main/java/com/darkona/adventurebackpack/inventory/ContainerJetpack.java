package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.Constants.Source;

import static com.darkona.adventurebackpack.common.Constants.JETPACK_FUEL_SLOT;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ContainerJetpack extends ContainerAdventureBackpack
{
    private static final int JETPACK_INV_START = PLAYER_INV_END + 1;
    private static final int JETPACK_FUEL_START = PLAYER_INV_END + 3;

    private InventoryCoalJetpack inventory;

    private int waterAmount;
    private int steamAmount;
    private ItemStack fuelStack;

    public ContainerJetpack(EntityPlayer player, InventoryCoalJetpack jetpack, Source source)
    {
        this.player = player;
        inventory = jetpack;
        makeSlots(player.inventory);
        inventory.openInventory();
        this.source = source;
    }

    @Override
    public IInventoryTanks getInventoryTanks()
    {
        return inventory;
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        bindPlayerInventory(invPlayer, 8, 84);

        addSlotToContainer(new SlotFluidWater(inventory, Constants.JETPACK_BUCKET_IN, 30, 22));
        addSlotToContainer(new SlotFluidWater(inventory, Constants.JETPACK_BUCKET_OUT, 30, 52));
        addSlotToContainer(new SlotFuel(inventory, JETPACK_FUEL_SLOT, 77, 64));
    }

    @Override
    protected boolean detectChanges()
    {
        boolean changesDetected = false;

        ItemStack[] inv = inventory.getInventory();
        if (inv[JETPACK_FUEL_SLOT] != fuelStack)
        {
            fuelStack = inv[JETPACK_FUEL_SLOT];
            changesDetected = true;
        }

        if (waterAmount != inventory.getWaterTank().getFluidAmount())
        {
            waterAmount = inventory.getWaterTank().getFluidAmount();
            changesDetected = true;
        }
        if (steamAmount != inventory.getSteamTank().getFluidAmount())
        {
            steamAmount = inventory.getSteamTank().getFluidAmount();
            changesDetected = true;
        }

        return changesDetected;
    }

    @Override
    public boolean transferStackToPack(ItemStack stack)
    {
        if (SlotFluid.isContainer(stack))
        {
            FluidTank waterTank = inventory.getWaterTank();
            ItemStack stackOut = getSlot(JETPACK_INV_START + 1).getStack();

            boolean isWaterTankEmpty = SlotFluid.isEmpty(waterTank);
            boolean suitableToTank = SlotFluid.isEqualAndCanFit(stack, waterTank);
            boolean areSameType = InventoryActions.areContainersOfSameType(stack, stackOut);

            if (SlotFluid.isFilled(stack))
            {
                if ((stackOut == null || areSameType) && SlotFluidWater.isValidItem(stack))
                {
                    if (isWaterTankEmpty || suitableToTank)
                    {
                        if (!mergeBucket(stack))
                            return false;
                    }
                }
            }
            else if (SlotFluid.isEmpty(stack))
            {
                if ((stackOut == null || areSameType) && SlotFluidWater.isValidItem(stack))
                {
                    if (!isWaterTankEmpty)
                    {
                        if (!mergeBucket(stack))
                            return false;
                    }
                }
            }
        }
        else if (SlotFuel.isValidItem(stack))
        {
            if (!mergeFuel(stack))
                return false;
        }
        return true;
    }

    private boolean mergeBucket(ItemStack stack)
    {
        return mergeItemStack(stack, JETPACK_INV_START, JETPACK_INV_START + 1, false);
    }

    private boolean mergeFuel(ItemStack stack)
    {
        return mergeItemStack(stack, JETPACK_FUEL_START, JETPACK_FUEL_START + 1, false);
    }
}
