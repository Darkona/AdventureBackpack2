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
import org.lwjgl.input.Keyboard;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class ContainerBackpack extends ContainerAdventureBackpack implements IWearableContainer
{

    public static final byte SOURCE_TILE = 0;
    public static final byte SOURCE_WEARING = 1;
    public static final byte SOURCE_HOLDING = 2;
    private final int
            PLAYER_HOT_START = 0,
            PLAYER_HOT_END = PLAYER_HOT_START + 8,
            PLAYER_INV_START = PLAYER_HOT_END + 1,
            PLAYER_INV_END = PLAYER_INV_START + 26,
            BACK_INV_START = PLAYER_INV_END + 1,
            BACK_INV_END = BACK_INV_START + 38,
            TOOL_START = BACK_INV_END + 1,
            TOOL_END = TOOL_START + 1,
            BUCKET_LEFT = TOOL_END + 1,
            BUCKET_RIGHT = BUCKET_LEFT + 2;
    private IInventoryAdventureBackpack inventory;
    private byte source;
    private InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    private IInventory craftResult = new InventoryCraftResult();
    private EntityPlayer player;

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

    private void bindPlayerInventory(InventoryPlayer invPlayer)
    {
        int startX = 44;
        int startY = 125;

        // Player's Hotbar
        for (int x = 0; x < 9; x++)
        {
            addSlotToContainer(new Slot(invPlayer, x, startX + 18 * x, 183));
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

        // Backpack Inventory
        int startX = 62;
        int startY = 7;
        int slot = 0;

        // 24 Slots
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 8; j++)
            {
                int offsetX = startX + (18 * j);
                int offsetY = startY + (18 * i);
                addSlotToContainer(new SlotBackpack(inventory, slot++, offsetX, offsetY));
            }
        }

        // 15 Slots
        startY = 61;
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 5; j++)
            {
                int offsetX = startX + (18 * j);
                int offsetY = startY + (18 * i);
                addSlotToContainer(new SlotBackpack(inventory, slot++, offsetX, offsetY));
            }
        }

        //Upper Tool Slot
        addSlotToContainer(new SlotTool(inventory, UPPER_TOOL, 44, 79));// Upper Tool 16
        //Lower Tool slot
        addSlotToContainer(new SlotTool(inventory, LOWER_TOOL, 44, 97));// Lower Tool 17

        //Bucket Slots

        // bucket in left 18
        addSlotToContainer(new SlotFluid(inventory, BUCKET_IN_LEFT, 6, 7));
        // bucket out left 19
        addSlotToContainer(new SlotFluid(inventory, BUCKET_OUT_LEFT, 6, 37));
        // bucket in right  20
        addSlotToContainer(new SlotFluid(inventory, BUCKET_IN_RIGHT, 226, 7));
        // bucket out right 21
        addSlotToContainer(new SlotFluid(inventory, BUCKET_OUT_RIGHT, 226, 37));

        //Craft Matrix
        startX = 152;
        for (int y = 0; y < 3; y++)
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

    /**
     * Looks for changes made in the container, sends them to every listener.
     */
    @Override
    public void detectAndSendChanges()
    {
        refresh();
        super.detectAndSendChanges();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int fromSlot)
    {
        if (source == SOURCE_WEARING) refresh(); //TODO
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
                if (SlotTool.isValidTool(stack))
                {
                    if (!mergeItemStack(stack, TOOL_START, TOOL_END + 1, false))
                    {
                        if (SlotBackpack.isValidItem(stack) && (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false)))
                        {
                            return null;
                        }
                    }

                } else if (SlotFluid.isContainer(stack) && !Keyboard.isKeyDown(Keyboard.KEY_SPACE))
                {
                    ItemStack rightOutStack = getSlot(BUCKET_RIGHT + 1).getStack();
                    ItemStack leftOutStack = getSlot(BUCKET_LEFT + 1).getStack();

                    FluidTank leftTank = inventory.getLeftTank();
                    FluidTank rightTank = inventory.getRightTank();
                    int maxAmount = leftTank.getCapacity();
                    int leftAmount = leftTank.getFluidAmount();
                    int rightAmount = rightTank.getFluidAmount();
                    int leftFluid = SlotFluid.getFluidID(leftTank);
                    int rightFluid = SlotFluid.getFluidID(rightTank);

                    int containerCapacity = SlotFluid.getCapacity(stack);
                    int containerFluid = SlotFluid.getFluidID(stack);

                    if (SlotFluid.isFilled(stack))
                    {
                        if (leftAmount == 0)
                        {
                            if (rightAmount > 0 && (rightAmount + containerCapacity <= maxAmount) && rightFluid == containerFluid)
                            {
                                if (!mergeItemStack(stack, BUCKET_RIGHT, BUCKET_RIGHT + 1, false))
                                {
                                    return null;
                                }
                            } else if (leftOutStack == null)
                            {
                                if (!mergeItemStack(stack, BUCKET_LEFT, BUCKET_LEFT + 1, false))
                                {
                                    return null;
                                }
                            }
                        } else if ((leftAmount + containerCapacity <= maxAmount) && leftFluid == containerFluid)
                        {
                            if (!mergeItemStack(stack, BUCKET_LEFT, BUCKET_LEFT + 1, false))
                            {
                                return null;
                            }
                        } else if (rightAmount == 0 || (rightAmount + containerCapacity <= maxAmount) && rightFluid == containerFluid)
                        {
                            if (!mergeItemStack(stack, BUCKET_RIGHT, BUCKET_RIGHT + 1, false))
                            {
                                return null;
                            }
                        } else if (leftOutStack == null && rightOutStack == null && SlotBackpack.isValidItem(stack))
                        {
                            if (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                            {
                                return null;
                            }
                        }
                    } else if (SlotFluid.isEmpty(stack))
                    {
                        if (leftAmount == 0)
                        {
                            if (rightAmount != 0 && rightOutStack == null)
                            {
                                if (!mergeItemStack(stack, BUCKET_RIGHT, BUCKET_RIGHT + 1, false))
                                {
                                    return null;
                                }
                            } else if (leftOutStack == null && rightOutStack == null && SlotBackpack.isValidItem(stack))
                            {
                                if (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
                                {
                                    return null;
                                }
                            }
                        } else if (leftOutStack == null)
                        {
                            if (!mergeItemStack(stack, BUCKET_LEFT, BUCKET_LEFT + 1, false))
                            {
                                return null;
                            }
                        }
                    }

                } else if (SlotBackpack.isValidItem(stack))
                {
                    if (!mergeItemStack(stack, BACK_INV_START, BACK_INV_END + 1, false))
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
        if (source == SOURCE_HOLDING && slot >= 0)
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
        if (source == SOURCE_WEARING)
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
            for (int i = 0; i < 9; i++) //TODO crafters
            {
                ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

                if (itemstack != null)
                {
                    player.dropPlayerItemWithRandomChoice(itemstack, false);
                }
            }
        }
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj));
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void refresh()
    {
        inventory.openInventory();
        this.onCraftMatrixChanged(craftMatrix);
    }
}
