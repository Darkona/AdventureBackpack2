package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants.Source;

import static com.darkona.adventurebackpack.common.Constants.Jetpack.BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.Jetpack.BUCKET_OUT;
import static com.darkona.adventurebackpack.common.Constants.Jetpack.FUEL_SLOT;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ContainerJetpack extends ContainerAdventure
{
    private static final int JETPACK_INV_START = PLAYER_INV_END + 1;
    private static final int JETPACK_FUEL_START = PLAYER_INV_END + 3;

    private ItemStack fuelStack;

    public ContainerJetpack(EntityPlayer player, InventoryCoalJetpack jetpack, Source source)
    {
        super(player, jetpack, source);
        makeSlots(player.inventory);
        inventory.openInventory();
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        bindPlayerInventory(invPlayer, 8, 84);

        addSlotToContainer(new SlotFluidWater(inventory, BUCKET_IN, 30, 22));
        addSlotToContainer(new SlotFluidWater(inventory, BUCKET_OUT, 30, 52));
        addSlotToContainer(new SlotFuel(inventory, FUEL_SLOT, 77, 64));
    }

    @Override
    protected boolean detectItemChanges()
    {
        // determine not only the presence of item in the slot but also check if the item type same as tick before
        ItemStack[] inv = inventory.getInventory();
        if (inv[FUEL_SLOT] != fuelStack)
        {
            fuelStack = inv[FUEL_SLOT];
            return true;
        }
        return false;
    }

    @Override
    public boolean transferStackToPack(ItemStack stack)
    {
        if (SlotFluid.isContainer(stack))
        {
            FluidTank waterTank = ((InventoryCoalJetpack ) inventory).getWaterTank();
            ItemStack stackOut = getSlot(JETPACK_INV_START + 1).getStack();

            boolean isWaterTankEmpty = SlotFluid.isEmpty(waterTank);
            boolean suitableToTank = SlotFluid.isEqualAndCanFit(stack, waterTank);
            boolean areSameType = InventoryActions.areContainersOfSameType(stack, stackOut);

            if (SlotFluid.isFilled(stack))
            {
                if ((stackOut == null || areSameType) && SlotFluidWater.isValidItem(stack))
                    if (isWaterTankEmpty || suitableToTank)
                        return mergeBucket(stack);
            }
            else if (SlotFluid.isEmpty(stack))
            {
                if ((stackOut == null || areSameType) && SlotFluidWater.isValidItem(stack))
                    if (!isWaterTankEmpty)
                        return mergeBucket(stack);
            }
        }
        else if (SlotFuel.isValidItem(stack))
        {
            return mergeFuel(stack);
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
