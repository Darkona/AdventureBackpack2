package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_IN;
import static com.darkona.adventurebackpack.common.Constants.COPTER_BUCKET_OUT;

/**
 * Created on 03/01/2015
 *
 * @author Darkona
 */
public class ContainerCopter extends ContainerAdventureBackpack /*implements IWearableContainer*/
{
    private static final int COPTER_INV_START = PLAYER_INV_END + 1;

    private InventoryCopterPack inventory;
    private EntityPlayer player;
    private boolean isWearing;

    private int fuelAmount;

    public ContainerCopter(EntityPlayer player, InventoryCopterPack copter, boolean wearing)
    {
        this.player = player;
        inventory = copter;
        makeSlots(player.inventory);
        inventory.openInventory();
        isWearing = wearing;
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        bindPlayerInventory(invPlayer, 8, 84);

        addSlotToContainer(new SlotFluidFuel(inventory, COPTER_BUCKET_IN, 44, 23));
        addSlotToContainer(new SlotFluidFuel(inventory, COPTER_BUCKET_OUT, 44, 53));
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (!isWearing)
        {
            boolean changesDetected = false;

            if (fuelAmount != inventory.getFuelTank().getFluidAmount())
            {
                fuelAmount = inventory.getFuelTank().getFluidAmount();
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
                    if (!mergeBucket(stack))
                        return false;
                }
            }
        }
        return true;
    }

    private boolean mergeBucket(ItemStack stack)
    {
        return mergeItemStack(stack, COPTER_INV_START, COPTER_INV_START + 1, false);
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
