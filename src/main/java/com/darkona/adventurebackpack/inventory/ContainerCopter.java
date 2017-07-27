package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants.Source;

import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_OUT;

/**
 * Created on 03/01/2015
 *
 * @author Darkona
 */
public class ContainerCopter extends ContainerAdventureBackpack
{
    private static final int COPTER_INV_START = PLAYER_INV_END + 1;

    private InventoryCopterPack inventory;
    private int fuelAmount;

    public ContainerCopter(EntityPlayer player, InventoryCopterPack copter, Source source)
    {
        this.player = player;
        inventory = copter;
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

        addSlotToContainer(new SlotFluidFuel(inventory, COPTER_BUCKET_IN, 44, 23));
        addSlotToContainer(new SlotFluidFuel(inventory, COPTER_BUCKET_OUT, 44, 53));
    }

    @Override
    protected boolean detectChanges()
    {
        boolean changesDetected = false;

        if (fuelAmount != inventory.getFuelTank().getFluidAmount())
        {
            fuelAmount = inventory.getFuelTank().getFluidAmount();
            changesDetected = true;
        }

        return changesDetected;
    }

    @Override
    public boolean transferStackToPack(ItemStack stack)
    {
        if (SlotFluid.isContainer(stack))
        {
            FluidTank fuelTank = inventory.getFuelTank();
            ItemStack stackOut = getSlot(COPTER_INV_START + 1).getStack();

            boolean isFuelTankEmpty = SlotFluid.isEmpty(fuelTank);
            boolean suitableToTank = SlotFluid.isEqualAndCanFit(stack, fuelTank);
            boolean areSameType = InventoryActions.areContainersOfSameType(stack, stackOut);

            if (SlotFluid.isFilled(stack))
            {
                if ((stackOut == null || areSameType) && SlotFluidFuel.isValidItem(stack))
                {
                    if (isFuelTankEmpty || suitableToTank)
                    {
                        if (!mergeBucket(stack))
                            return false;
                    }
                }
            } else if (SlotFluid.isEmpty(stack))
            {
                if ((stackOut == null || areSameType) && SlotFluidFuel.isValidItem(stack))
                {
                    if (!isFuelTankEmpty)
                    {
                        if (!mergeBucket(stack))
                            return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean mergeBucket(ItemStack stack)
    {
        return mergeItemStack(stack, COPTER_INV_START, COPTER_INV_START + 1, false);
    }
}
