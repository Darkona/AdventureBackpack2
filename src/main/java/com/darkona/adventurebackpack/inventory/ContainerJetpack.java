package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidTank;

import com.darkona.adventurebackpack.common.Constants;

import static com.darkona.adventurebackpack.common.Constants.JETPACK_FUEL_SLOT;

/**
 * Created on 15/01/2015
 *
 * @author Darkona
 */
public class ContainerJetpack extends ContainerAdventureBackpack implements IWearableContainer
{
    private static final int PLAYER_HOT_START = 0;
    private static final int PLAYER_HOT_END = PLAYER_HOT_START + 8;
    private static final int PLAYER_INV_START = PLAYER_HOT_END + 1;
    private static final int PLAYER_INV_END = PLAYER_INV_START + 26;
    private static final int JETPACK_INV_START = PLAYER_INV_END + 1;
    private static final int JETPACK_FUEL_START = PLAYER_INV_END + 3;

    private InventoryCoalJetpack inventory;
    private EntityPlayer player;
    private boolean wearing;

    private int waterAmount;
    private int steamAmount;
    private ItemStack fuelStack;

    public ContainerJetpack(EntityPlayer player, InventoryCoalJetpack jetpack, boolean wearing)
    {
        this.player = player;
        inventory = jetpack;
        makeSlots(player.inventory);
        inventory.openInventory();
        this.wearing = wearing;
    }

    private void bindPlayerInventory(InventoryPlayer invPlayer)
    {
        int startX = 8;
        int startY = 84;

        // Player's Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, startX + 18 * x, 142));
        }

        // Player's Inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlotToContainer(new Slot(invPlayer, (x + y * 9 + 9), (startX + 18 * x), (startY + y * 18)));
            }
        }
        //Total 36 slots
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        bindPlayerInventory(invPlayer);
        //Bucket Slots
        // bucket in
        addSlotToContainer(new SlotFluidWater(inventory, Constants.JETPACK_BUCKET_IN, 30, 22));
        // bucket out
        addSlotToContainer(new SlotFluidWater(inventory, Constants.JETPACK_BUCKET_OUT, 30, 52));
        // fuel
        addSlotToContainer(new SlotFuel(inventory, JETPACK_FUEL_SLOT, 77, 64));
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        if (!wearing)
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
    public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot)
    {
        refresh();
        Slot slot = getSlot(fromSlot);
        ItemStack result = null;

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (fromSlot >= 36)
            {
                if (!mergeItemStack(stack, PLAYER_HOT_START, PLAYER_INV_END + 1, false))
                {
                    return null;
                }
            }
            if (fromSlot < 36)
            {
                if (SlotFluid.isContainer(stack))
                {
                    ItemStack outStack = getSlot(JETPACK_INV_START + 1).getStack();

                    FluidTank waterTank = inventory.getWaterTank();
                    int maxAmount = waterTank.getCapacity();
                    int tankAmount = waterTank.getFluidAmount();
                    int tankFluid = SlotFluid.getFluidID(waterTank);

                    int containerCapacity = SlotFluid.getCapacity(stack);
                    int containerFluid = SlotFluid.getFluidID(stack);

                    if (SlotFluid.isFilled(stack))
                    {
                        if ((outStack == null || (SlotFluid.isEmptyBucket(outStack) && SlotFluid.isBucket(stack)))
                                && SlotFluidWater.isValidItem(stack))
                        {
                            if ((tankAmount == 0 || (tankAmount > 0 && tankFluid == containerFluid))
                                    && tankAmount + containerCapacity <= maxAmount)
                            {
                                if (!mergeItemStack(stack, JETPACK_INV_START, JETPACK_INV_START + 1, false))
                                {
                                    return null;
                                }
                            }
                        }
                    } else if (SlotFluid.isEmpty(stack))
                    {
                        if (outStack == null && tankAmount >= containerCapacity && SlotFluidWater.isValidItem(stack))
                        {
                            if (!mergeItemStack(stack, JETPACK_INV_START, JETPACK_INV_START + 1, false))
                            {
                                return null;
                            }
                        }
                    }

                } else if (SlotFuel.isValidItem(stack))
                {
                    if (!mergeItemStack(stack, JETPACK_FUEL_START, JETPACK_FUEL_START + 1, false))
                    {
                        return null;
                    }
                }
            }

            if (stack.stackSize == 0)
            {
                slot.putStack(null);
            } else
            {
                slot.onSlotChanged();
            }

            if (stack.stackSize == result.stackSize)
            {
                return null;
            }
            slot.onPickupFromSlot(player, stack);
        }
        return result;
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        if (!wearing && slot >= 0)
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
        if (wearing)
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

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_)
    {
        return true;
    }

    @Override
    public void refresh()
    {
        inventory.openInventory();
    }
}
