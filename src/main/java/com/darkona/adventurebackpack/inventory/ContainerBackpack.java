package com.darkona.adventurebackpack.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fluids.FluidTank;

import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_IN_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_LEFT;
import static com.darkona.adventurebackpack.common.Constants.BUCKET_OUT_RIGHT;
import static com.darkona.adventurebackpack.common.Constants.LOWER_TOOL;
import static com.darkona.adventurebackpack.common.Constants.UPPER_TOOL;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class ContainerBackpack extends ContainerAdventureBackpack
{
    private static final int BACK_INV_START = PLAYER_INV_END + 1;
    private static final int BACK_INV_END = BACK_INV_START + 38;
    private static final int TOOL_START = BACK_INV_END + 1;
    private static final int TOOL_END = TOOL_START + 1;
    private static final int BUCKET_LEFT = TOOL_END + 1;
    private static final int BUCKET_RIGHT = BUCKET_LEFT + 2;

    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    private IInventory craftResult = new InventoryCraftResult();
    private IInventoryAdventureBackpack inventory;

    private int leftAmount;
    private int rightAmount;
    private int invCount;

    //IDEA redesign container layout/craft slots behavior, so it will be rectangular and compatible with invTweaks. this also makes more slots available cuz craft ones will not drop content on close

    public ContainerBackpack(EntityPlayer player, IInventoryAdventureBackpack backpack, byte source)
    {
        this.player = player;
        inventory = backpack;
        makeSlots(player.inventory);
        inventory.openInventory();
        this.source = source;
    }

    public IInventoryAdventureBackpack getInventoryBackpack()
    {
        return inventory;
    }

    private void makeSlots(InventoryPlayer invPlayer)
    {
        bindPlayerInventory(invPlayer, 44, 125);

        int startX = 62;
        int startY = 7;
        int slot = 0;

        for (int i = 0; i < 3; i++) // upper 8*3, 24 Slots (1-24)
        {
            for (int j = 0; j < 8; j++)
            {
                int offsetX = startX + (18 * j);
                int offsetY = startY + (18 * i);
                addSlotToContainer(new SlotBackpack(inventory, slot++, offsetX, offsetY));
            }
        }

        startY = 61;
        for (int i = 0; i < 3; i++) // lower 5*3, 15 Slots (25-39)
        {
            for (int j = 0; j < 5; j++)
            {
                int offsetX = startX + (18 * j);
                int offsetY = startY + (18 * i);
                addSlotToContainer(new SlotBackpack(inventory, slot++, offsetX, offsetY));
            }
        }

        addSlotToContainer(new SlotTool(inventory, UPPER_TOOL, 44, 79)); // #40
        addSlotToContainer(new SlotTool(inventory, LOWER_TOOL, 44, 97)); // #41

        addSlotToContainer(new SlotFluid(inventory, BUCKET_IN_LEFT, 6, 7)); // #42
        addSlotToContainer(new SlotFluid(inventory, BUCKET_OUT_LEFT, 6, 37)); // #43
        addSlotToContainer(new SlotFluid(inventory, BUCKET_IN_RIGHT, 226, 7)); // #44
        addSlotToContainer(new SlotFluid(inventory, BUCKET_OUT_RIGHT, 226, 37)); // #45

        startX = 152;
        for (int y = 0; y < 3; y++) // Craft Matrix - 3*3, 9 slots
        {
            for (int x = 0; x < 3; x++)
            {
                int offsetX = startX + (18 * x);
                int offsetY = startY + (18 * y);
                addSlotToContainer(new Slot(craftMatrix, (x + y * 3), offsetX, offsetY));
            }
        }
        addSlotToContainer(new SlotCrafting(invPlayer.player, craftMatrix, craftResult, 0, 226, 97));
        this.onCraftMatrixChanged(craftMatrix);
    }

    @Override
    protected boolean detectChanges()
    {
        boolean changesDetected = false;

        ItemStack[] inv = inventory.getInventory();
        int tempCount = 0;
        for (int i = 0; i <= LOWER_TOOL; i++)
        {
            if (inv[i] != null)
                tempCount++;
        }
        if (invCount != tempCount)
        {
            invCount = tempCount;
            changesDetected = true;
        }

        if (leftAmount != inventory.getLeftTank().getFluidAmount())
        {
            leftAmount = inventory.getLeftTank().getFluidAmount();
            changesDetected = true;
        }
        if (rightAmount != inventory.getRightTank().getFluidAmount())
        {
            rightAmount = inventory.getRightTank().getFluidAmount();
            changesDetected = true;
        }

        return changesDetected;
    }

    @Override
    protected boolean transferStackToPack(ItemStack stack)
    {
        if (SlotTool.isValidTool(stack))
        {
            if (!mergeToolSlot(stack))
            {
                if (SlotBackpack.isValidItem(stack))
                {
                    if (!mergeBackpackInv(stack))
                        return false;
                }
            }

        } else if (SlotFluid.isContainer(stack) && !isHoldingSpace())
        {
            if (!transferFluidContainer(stack))
                return false;

        } else if (SlotBackpack.isValidItem(stack))
        {
            if (!mergeBackpackInv(stack))
                return false;
        }
        return true;
    }

    private boolean mergeToolSlot(ItemStack stack)
    {
        return mergeItemStack(stack, TOOL_START, TOOL_END + 1, false);
    }

    private boolean mergeBackpackInv(ItemStack stack)
    {
        return mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false);
    }

    private boolean mergeLeftBucket(ItemStack stack)
    {
        return mergeItemStack(stack, BUCKET_LEFT, BUCKET_LEFT + 1, false);
    }

    private boolean mergeRightBucket(ItemStack stack)
    {
        return mergeItemStack(stack, BUCKET_RIGHT, BUCKET_RIGHT + 1, false);
    }

    private boolean isHoldingSpace()
    {
        return inventory.getExtendedProperties().hasKey("holdingSpace");
    }

    private boolean transferFluidContainer(ItemStack container)
    {
        FluidTank leftTank = inventory.getLeftTank();
        FluidTank rightTank = inventory.getRightTank();
        ItemStack leftStackOut = getSlot(BUCKET_LEFT + 1).getStack();
        ItemStack rightStackOut = getSlot(BUCKET_RIGHT + 1).getStack();

        boolean isLeftTankEmpty = SlotFluid.isEmpty(leftTank);
        boolean isRightTankEmpty = SlotFluid.isEmpty(rightTank);
        boolean suitableToLeft = SlotFluid.isEqualAndCanFit(container, leftTank);
        boolean suitableToRight = SlotFluid.isEqualAndCanFit(container, rightTank);
        boolean areLeftSameType = InventoryActions.areContainersOfSameType(container, leftStackOut);
        boolean areRightSameType = InventoryActions.areContainersOfSameType(container, rightStackOut);

        if (SlotFluid.isFilled(container))
        {
            if (isLeftTankEmpty)
            {
                if (!isRightTankEmpty && (rightStackOut == null || areRightSameType) && suitableToRight)
                    return mergeRightBucket(container);
                else if (leftStackOut == null || areLeftSameType)
                    return mergeLeftBucket(container);

            } else
            {
                if ((leftStackOut == null || areLeftSameType) && suitableToLeft)
                    return mergeLeftBucket(container);
                else if ((rightStackOut == null || areRightSameType) && (isRightTankEmpty || suitableToRight))
                    return mergeRightBucket(container);
                else if (leftStackOut == null && rightStackOut == null && SlotBackpack.isValidItem(container))
                    return mergeBackpackInv(container);
            }

        } else if (SlotFluid.isEmpty(container))
        {
            if (isLeftTankEmpty)
            {
                if (!isRightTankEmpty && (rightStackOut == null || areRightSameType))
                    return mergeRightBucket(container);
                else if (leftStackOut == null && rightStackOut == null && SlotBackpack.isValidItem(container))
                    return mergeBackpackInv(container);

            } else
            {
                if (leftStackOut == null || areLeftSameType)
                    return mergeLeftBucket(container);
            }
        }
        return false;
    }

    @Override
    protected void dropContentOnClose()
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

        for (int i = 0; i < 9; i++)
        {
            ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

            if (itemstack != null)
            {
                player.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj));
    }
}
