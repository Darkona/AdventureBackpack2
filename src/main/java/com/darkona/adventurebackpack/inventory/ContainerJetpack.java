package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;

import static com.darkona.adventurebackpack.common.Constants.JETPACK_FUEL_SLOT;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ContainerJetpack extends ContainerAdventureBackpack /*implements IWearableContainer*/
{
    private static final int JETPACK_INV_START = PLAYER_INV_END + 1;
    private static final int JETPACK_FUEL_START = PLAYER_INV_END + 3;

    private InventoryCoalJetpack inventory;
    private EntityPlayer player;
    private boolean isWearing;

    private int waterAmount;
    private int steamAmount;
    private ItemStack fuelStack;

    public ContainerJetpack(EntityPlayer player, InventoryCoalJetpack jetpack, boolean wearing)
    {
        this.player = player;
        inventory = jetpack;
        makeSlots(player.inventory);
        inventory.openInventory();
        isWearing = wearing;
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        bindPlayerInventory(invPlayer, 8, 84);

        addSlotToContainer(new SlotFluidWater(inventory, Constants.JETPACK_BUCKET_IN, 30, 22));
        addSlotToContainer(new SlotFluidWater(inventory, Constants.JETPACK_BUCKET_OUT, 30, 52));
        addSlotToContainer(new SlotFuel(inventory, JETPACK_FUEL_SLOT, 77, 64));
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (!isWearing)
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

            if (changesDetected)
            {
                if (player instanceof EntityPlayerMP)
                {
                    ((EntityPlayerMP) player).sendContainerAndContentsToPlayer(this, this.getInventory());
                }
            }
        }
    }

    @Override
    public boolean transferStackToPack(ItemStack stack)
    {
        if (SlotFluid.isContainer(stack))
        {
            FluidTank waterTank = inventory.getWaterTank();

            boolean isOutStackEmpty = !getSlot(JETPACK_INV_START + 1).getHasStack();
            boolean isWaterTankEmpty = SlotFluid.isEmpty(waterTank);
            boolean suitableToTank = SlotFluid.isEqualAndCanFit(stack, waterTank);
            boolean isBucketsCase = SlotFluid.isEmptyBucket(getSlot(JETPACK_INV_START + 1).getStack())
                    && SlotFluid.isFilledBucket(stack);

            if (SlotFluid.isFilled(stack))
            {
                if ((isOutStackEmpty || isBucketsCase) && SlotFluidWater.isValidItem(stack))
                {
                    if (isWaterTankEmpty || suitableToTank)
                    {
                        if (!mergeBucket(stack))
                            return false;
                    }
                }
            } else if (SlotFluid.isEmpty(stack))
            {
                if (isOutStackEmpty && SlotFluidWater.isValidItem(stack))
                {
                    if (!mergeBucket(stack))
                        return false;
                }
            }

        } else if (SlotFuel.isValidItem(stack))
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

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        if (!isWearing && slot >= 0)
        {
            if (getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItem())
            {
                return null;
            }
            if (flag == 2 && getSlot(button).getStack() == player.getHeldItem())
            {
                return null;
            }
        }
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if (isWearing) //TODO
        {
            this.crafters.remove(player);
        }

        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack itemstack = this.inventory.getStackInSlotOnClosing(i);
                if (itemstack != null)
                {
                    inventory.setInventorySlotContents(i, null);
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    /*@Override
    public void refresh()
    {
        inventory.openInventory();
    }*/
}
