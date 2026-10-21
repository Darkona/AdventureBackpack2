package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IAsynchronousInventory;
import adventurebackpack.api.IWearableContainer;
import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import com.darkona.adventurebackpack.inventory.widget.gear.GearWidgetTypes;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public abstract class ContainerGear extends Container implements IWearableContainer, SlotHost
{
    public final WidgetLayout widgets;
    protected final EntityPlayer player;
    protected final IAsynchronousInventory inventory;
    protected final TileGear tile;
    protected final boolean wearing;
    private final int playerFirst;

    protected ContainerGear(EntityPlayer player, IAsynchronousInventory inventory, WidgetLayout widgets, boolean wearing, TileGear tile)
    {
        GearWidgetTypes.install();
        this.player = player;
        this.inventory = inventory;
        this.widgets = widgets;
        this.wearing = wearing;
        this.tile = tile;
        for (int w = 0; w < widgets.count(); w++)
        {
            WidgetType type = WidgetTypes.get(widgets.kind(w));
            for (int n = 0; n < widgets.slots(w); n++)
            {
                int s = widgets.slotFirst(w) + n;
                addSlotToContainer(type.createSlot(this, widgets, w, n, widgets.slotInv(s), widgets.slotX(s), widgets.slotY(s)));
            }
        }
        playerFirst = widgets.slotFirst(widgets.first(ClassicWidgets.PLAYER));
        inventory.openInventory();
    }

    protected abstract ItemStack parentStack();

    protected boolean liveRefresh()
    {
        return false;
    }

    protected void takeOut(int slot)
    {
        inventory.setInventorySlotContentsNoSave(slot, null);
    }

    @Override
    public IInventory hostInventory()
    {
        return inventory;
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return player;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p)
    {
        if (tile != null) return tile.isUseableByPlayer(p);
        return HeldContainerGuard.stillOpen(wearing, parentStack(), BackpackProperty.get(p).getWearable(), p.getCurrentEquippedItem());
    }

    @Override
    public void detectAndSendChanges()
    {
        if (liveRefresh()) refresh();
        super.detectAndSendChanges();
        if (tile != null) tile.afterContainerTick();
    }

    @Override
    public void onContainerClosed(EntityPlayer p)
    {
        super.onContainerClosed(p);
        if (wearing) crafters.remove(p);
        if (!p.worldObj.isRemote)
        {
            for (int i = 0; i < inventory.getSizeInventory(); i++)
            {
                ItemStack stack = inventory.getStackInSlotOnClosing(i);
                if (stack == null) continue;
                takeOut(i);
                if (!p.inventory.addItemStackToInventory(stack)) p.dropPlayerItemWithRandomChoice(stack, false);
            }
        }
        if (tile != null) tile.afterContainerTick();
    }

    @Override
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer p)
    {
        boolean clickedIsHeld = slot >= 0 && slot < inventorySlots.size() && p.getHeldItem() != null
                && getSlot(slot).getStack() == p.getHeldItem();
        if (HeldContainerGuard.refuses(!wearing && tile == null, clickedIsHeld, flag, button, p.inventory.currentItem)) return null;
        return super.slotClick(slot, button, flag, p);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer p, int index)
    {
        if (liveRefresh()) refresh();
        Slot slot = index >= 0 && index < inventorySlots.size() ? getSlot(index) : null;
        if (slot == null || !slot.getHasStack()) return null;
        ItemStack stack = slot.getStack();
        ItemStack result = stack.copy();
        boolean moved = index >= playerFirst ? mergeIntoGear(stack) : mergeIntoPlayer(stack);
        if (!moved) return null;
        if (stack.stackSize == 0) slot.putStack(null);
        else slot.onSlotChanged();
        if (stack.stackSize == result.stackSize) return null;
        slot.onPickupFromSlot(p, stack);
        return result;
    }

    private boolean mergeIntoPlayer(ItemStack stack)
    {
        return mergeItemStack(stack, playerFirst, widgets.slotCount(), true);
    }

    private boolean mergeIntoGear(ItemStack stack)
    {
        int best = WidgetLayout.NONE;
        int bestPriority = Integer.MIN_VALUE;
        for (int w = 0; w < widgets.count(); w++)
        {
            int priority = WidgetTypes.get(widgets.kind(w)).shiftPriority();
            if (priority == WidgetType.NO_SHIFT || !widgets.ownsInventory(w)) continue;
            int s = targetSlot(w, stack);
            if (s < 0 || priority <= bestPriority) continue;
            best = s;
            bestPriority = priority;
        }
        return best != WidgetLayout.NONE && mergeItemStack(stack, best, best + 1, false);
    }

    private int targetSlot(int widget, ItemStack stack)
    {
        WidgetType type = WidgetTypes.get(widgets.kind(widget));
        for (int n = 0; n < widgets.slots(widget); n++)
        {
            int s = widgets.slotFirst(widget) + n;
            if (type.shiftTarget(widgets, widget, n) && getSlot(s).isItemValid(stack)) return s;
        }
        return -1;
    }
}
