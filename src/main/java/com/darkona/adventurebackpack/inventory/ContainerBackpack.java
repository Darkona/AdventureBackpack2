package com.darkona.adventurebackpack.inventory;

import adventurebackpack.api.IWearableContainer;
import com.darkona.adventurebackpack.common.IInventoryAdventureBackpack;
import com.darkona.adventurebackpack.compat.bogosorter.BogoSorterCompat;
import com.darkona.adventurebackpack.compat.invtweaks.BackpackSections;
import com.darkona.adventurebackpack.common.HandRefill;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.sort.SlotLocks;
import com.darkona.adventurebackpack.common.sort.StorageSorter;
import com.darkona.adventurebackpack.inventory.widget.SlotHost;
import com.darkona.adventurebackpack.inventory.widget.WidgetType;
import com.darkona.adventurebackpack.inventory.widget.WidgetTypes;
import com.darkona.adventurebackpack.util.Wearing;
import invtweaks.api.container.ChestContainer;
import invtweaks.api.container.ContainerSectionCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

import java.util.Map;

@ChestContainer(showButtons = false, rowSize = 8)
public class ContainerBackpack extends Container implements IWearableContainer, SlotHost
{
    public static final int LOCK_PROPERTY = 100;
    public static final int ACTION_LOCK = 1;
    public static final int ACTION_SORT = 2;
    private static final int PENDING_LIMIT = 16;
    private static final int SLOT_BITS = 16;
    private static final int SLOT_MASK = (1 << SLOT_BITS) - 1;
    public IInventoryAdventureBackpack inventory;
    public static byte SOURCE_TILE = 0;
    public static byte SOURCE_WEARING = 1;
    public static byte SOURCE_HOLDING = 2;
    public byte source;
    public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
    public IInventory craftResult = new InventoryCraftResult();
    EntityPlayer player;
    public final WidgetLayout widgets;
    private final int[] shiftOrder;
    private final int mainFirst;
    private final int mainEnd;
    private final int hotbarFirst;
    private final int hotbarEnd;
    private Map<?, ?> invTweaksSections;
    private int invTweaksSectionsVersion = -1;
    private final SlotLocks idleLocks = new SlotLocks();
    private final int[] sentLocks = new int[SlotLocks.CHUNKS];
    private final int[] pending = new int[PENDING_LIMIT];
    private volatile int pendingCount;

    public ContainerBackpack(EntityPlayer player, IInventoryAdventureBackpack backpack, byte source)
    {
        this.player = player;
        inventory = backpack;
        widgets = WidgetLayouts.widgets(backpack.getColorName(), backpack.getStorageSize());
        shiftOrder = shiftOrder(widgets);
        mainFirst = sectionBound(WidgetType.Section.PLAYER, false);
        mainEnd = sectionBound(WidgetType.Section.PLAYER, true);
        hotbarFirst = sectionBound(WidgetType.Section.HOTBAR, false);
        hotbarEnd = sectionBound(WidgetType.Section.HOTBAR, true);
        makeSlots();
        inventory.openInventory();
        this.source = source;
    }

    public IInventoryBackpack getInventoryBackpack()
    {
        return inventory instanceof IInventoryBackpack ? (IInventoryBackpack) inventory : null;
    }

    @Override
    public net.minecraft.inventory.IInventory hostInventory()
    {
        return inventory;
    }

    @Override
    public EntityPlayer getPlayer()
    {
        return player;
    }

    private void makeSlots()
    {
        WidgetLayout l = widgets;
        for (int w = 0; w < l.count(); w++)
        {
            WidgetType type = WidgetTypes.get(l.kind(w));
            int first = l.slotFirst(w);
            for (int n = 0; n < l.slots(w); n++)
            {
                int s = first + n;
                addSlotToContainer(type.createSlot(this, l, w, n, l.slotInv(s), l.slotX(s), l.slotY(s)));
            }
        }
        this.onCraftMatrixChanged(craftMatrix);
    }

    @ChestContainer.RowSizeCallback
    public int invTweaksRowSize()
    {
        return widgets.rowSize();
    }

    @ContainerSectionCallback
    public Map<?, ?> invTweaksSections()
    {
        int version = locks().version();
        if (invTweaksSections == null || version != invTweaksSectionsVersion)
        {
            invTweaksSections = BackpackSections.of(this);
            invTweaksSectionsVersion = version;
        }
        return invTweaksSections;
    }

    public SlotLocks locks()
    {
        SlotLocks locks = inventory.getLocks();
        return locks == null ? idleLocks : locks;
    }

    public boolean isSortableSlot(int slot)
    {
        return slot >= 0 && slot < widgets.slotCount() && WidgetTypes.get(widgets.kind(widgets.slotWidget(slot))).sortable();
    }

    public boolean isLocked(int slot)
    {
        return isSortableSlot(slot) && locks().isLocked(widgets.slotInv(slot));
    }

    public boolean toggleLock(int slot)
    {
        if (!isSortableSlot(slot)) return false;
        locks().toggle(widgets.slotInv(slot));
        inventory.dirtyLocks();
        BogoSorterCompat.locksChanged();
        return true;
    }

    public boolean sortStorage()
    {
        WidgetLayout l = widgets;
        int[] invs = new int[l.slotCount()];
        int n = 0;
        for (int w = 0; w < l.count(); w++)
        {
            if (!WidgetTypes.get(l.kind(w)).sortable()) continue;
            for (int s = l.slotFirst(w); s < l.slotFirst(w) + l.slots(w); s++)
            {
                if (!isLocked(s)) invs[n++] = l.slotInv(s);
            }
        }
        if (n == 0) return false;
        StorageSorter.Entry[] entries = new StorageSorter.Entry[n];
        ItemStack[] kinds = new ItemStack[n];
        int kindCount = 0;
        int count = 0;
        for (int i = 0; i < n; i++)
        {
            ItemStack stack = inventory.getStackInSlot(invs[i]);
            if (stack == null) continue;
            int group = -1;
            for (int k = 0; k < kindCount && group < 0; k++)
            {
                if (HandRefill.sameKind(kinds[k], stack)) group = k;
            }
            if (group < 0)
            {
                kinds[kindCount] = stack;
                group = kindCount++;
            }
            Item item = stack.getItem();
            CreativeTabs tab = item.getCreativeTab();
            entries[count++] = new StorageSorter.Entry(group, tab == null ? StorageSorter.NO_CATEGORY : tab.getTabIndex(),
                    stack.getDisplayName(), Item.getIdFromItem(item), stack.getItemDamage(), stack.stackSize,
                    Math.min(stack.getMaxStackSize(), inventory.getInventoryStackLimit()), stack);
        }
        StorageSorter.Plan plan = StorageSorter.arrange(java.util.Arrays.copyOf(entries, count), n);
        if (plan == null) return false;
        for (int i = 0; i < n; i++)
        {
            ItemStack put = null;
            if (plan.count[i] > 0)
            {
                put = ((ItemStack) plan.source[i]).copy();
                put.stackSize = plan.count[i];
            }
            inventory.setInventorySlotContentsNoSave(invs[i], put);
        }
        inventory.markDirty();
        return true;
    }

    public void request(int action, int slot)
    {
        synchronized (pending)
        {
            if (pendingCount >= PENDING_LIMIT) return;
            pending[pendingCount] = action << SLOT_BITS | slot & SLOT_MASK;
            pendingCount++;
        }
    }

    private void runPending()
    {
        if (pendingCount == 0 || player.worldObj.isRemote) return;
        while (true)
        {
            int next;
            synchronized (pending)
            {
                if (pendingCount == 0) return;
                next = pending[0];
                System.arraycopy(pending, 1, pending, 0, pendingCount - 1);
                pendingCount--;
            }
            if (!canInteractWith(player)) continue;
            int action = next >>> SLOT_BITS;
            if (action == ACTION_LOCK) toggleLock(next & SLOT_MASK);
            else if (action == ACTION_SORT) sortStorage();
        }
    }

    private void syncLocks()
    {
        SlotLocks locks = locks();
        for (int i = 0; i < SlotLocks.CHUNKS; i++)
        {
            int value = locks.chunk(i);
            if (value == sentLocks[i]) continue;
            sentLocks[i] = value;
            for (int c = 0; c < crafters.size(); c++) ((ICrafting) crafters.get(c)).sendProgressBarUpdate(this, LOCK_PROPERTY + i, value);
        }
    }

    @Override
    public void updateProgressBar(int id, int value)
    {
        if (id < LOCK_PROPERTY || id >= LOCK_PROPERTY + SlotLocks.CHUNKS) return;
        SlotLocks locks = locks();
        int version = locks.version();
        locks.setChunk(id - LOCK_PROPERTY, value);
        if (locks.version() != version) BogoSorterCompat.locksChanged();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        if (source == SOURCE_HOLDING)
        {
            return player.getCurrentEquippedItem() == inventory.getParentItemStack();
        }
        if (source == SOURCE_WEARING)
        {
            return Wearing.getWearingBackpack(player) == inventory.getParentItemStack();
        }
        return inventory.isUseableByPlayer(player);
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(craftMatrix, player.worldObj));
    }

    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);
        if(source == SOURCE_WEARING)
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

            for (int i = 0; i < 9; i++)
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
    public ItemStack slotClick(int slot, int button, int flag, EntityPlayer player)
    {
        boolean clickedIsHeld = slot >= 0 && slot < inventorySlots.size() && player.getHeldItem() != null
                && getSlot(slot).getStack() == player.getHeldItem();
        if (HeldContainerGuard.refuses(source == SOURCE_HOLDING, clickedIsHeld, flag, button, player.inventory.currentItem))
        {
            return null;
        }
        return super.slotClick(slot, button, flag, player);
    }

    @Override
    protected boolean mergeItemStack(ItemStack stack, int minSlot, int maxSlot, boolean direction)
    {
        if (direction)
        {
            return super.mergeItemStack(stack, minSlot, maxSlot, true);
        }
        boolean changesMade = stackOnto(stack, minSlot, maxSlot);
        if (stack.stackSize > 0)
        {
            changesMade |= fillEmpty(stack, minSlot, maxSlot);
        }
        return changesMade;
    }

    private boolean stackOnto(ItemStack stack, int minSlot, int maxSlot)
    {
        boolean changesMade = false;
        if (!stack.isStackable()) return false;
        for (int i = minSlot; stack.stackSize > 0 && i < maxSlot; i++)
        {
            Slot slot = (Slot) this.inventorySlots.get(i);
            ItemStack newItemStack = slot.getStack();

            if (newItemStack != null && newItemStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == newItemStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, newItemStack))
            {
                int newStackSize = newItemStack.stackSize + stack.stackSize;

                if (newStackSize <= stack.getMaxStackSize())
                {
                    stack.stackSize = 0;
                    newItemStack.stackSize = newStackSize;
                    slot.onSlotChanged();
                    changesMade = true;
                } else if (newItemStack.stackSize < stack.getMaxStackSize())
                {
                    stack.stackSize -= stack.getMaxStackSize() - newItemStack.stackSize;
                    newItemStack.stackSize = stack.getMaxStackSize();
                    slot.onSlotChanged();
                    changesMade = true;
                }
            }
        }
        return changesMade;
    }

    private boolean fillEmpty(ItemStack stack, int minSlot, int maxSlot)
    {
        for (int i = minSlot; i < maxSlot; i++)
        {
            Slot slot = (Slot) this.inventorySlots.get(i);
            if (slot.getStack() == null)
            {
                slot.putStack(stack.copy());
                slot.onSlotChanged();
                stack.stackSize = 0;
                return true;
            }
        }
        return false;
    }

    private boolean mergeLimited(ItemStack stack, int minSlot, int maxSlot, int limit)
    {
        boolean changesMade = false;
        for (int pass = 0; pass < 2 && stack.stackSize > 0; pass++)
        {
            for (int i = minSlot; stack.stackSize > 0 && i < maxSlot; i++)
            {
                Slot slot = (Slot) inventorySlots.get(i);
                ItemStack stored = slot.getStack();
                int cap = Math.min(limit, slot.getSlotStackLimit());
                if (pass == 0 && stored != null && HandRefill.sameKind(stack, stored))
                {
                    int moved = Math.min(stack.stackSize, cap - stored.stackSize);
                    if (moved <= 0) continue;
                    stored.stackSize += moved;
                    slot.onSlotChanged();
                    stack.stackSize -= moved;
                    changesMade = true;
                } else if (pass == 1 && stored == null)
                {
                    int moved = Math.min(stack.stackSize, cap);
                    ItemStack put = stack.copy();
                    put.stackSize = moved;
                    slot.putStack(put);
                    stack.stackSize -= moved;
                    changesMade |= moved > 0;
                }
            }
        }
        return changesMade;
    }

    private boolean mergeIntoWidget(ItemStack stack, int widget)
    {
        WidgetLayout l = widgets;
        WidgetType type = WidgetTypes.get(l.kind(widget));
        boolean changesMade = false;
        int first = l.slotFirst(widget);
        for (int n = 0; n < l.slots(widget) && stack.stackSize > 0; n++)
        {
            if (!type.shiftTarget(l, widget, n)) continue;
            if (type.stackLimit() > WidgetType.DEFAULT_LIMIT) changesMade |= mergeLimited(stack, first + n, first + n + 1, type.stackLimit());
            else changesMade |= mergeItemStack(stack, first + n, first + n + 1, false);
        }
        return changesMade;
    }

    int firstShiftWidget(ItemStack stack)
    {
        for (int i = 0; i < shiftOrder.length; i++)
        {
            if (WidgetTypes.get(widgets.kind(shiftOrder[i])).accepts(stack)) return shiftOrder[i];
        }
        return WidgetLayout.NONE;
    }

    private boolean mergeIntoBackpack(ItemStack stack)
    {
        WidgetLayout l = widgets;
        for (int i = 0; i < shiftOrder.length; i++)
        {
            int w = shiftOrder[i];
            if (WidgetTypes.get(l.kind(w)).accepts(stack) && mergeIntoWidget(stack, w)) return true;
        }
        for (int w = 0; w < l.count(); w++)
        {
            WidgetType type = WidgetTypes.get(l.kind(w));
            if (type.section() == WidgetType.Section.CHEST && type.accepts(stack)
                    && mergeItemStack(stack, l.slotFirst(w), l.slotFirst(w) + l.slots(w), false)) return true;
        }
        return false;
    }

    private boolean mergeIntoPlayer(ItemStack stack)
    {
        boolean changesMade = stackOnto(stack, hotbarFirst, hotbarEnd);
        changesMade |= stackOnto(stack, mainFirst, mainEnd);
        if (stack.stackSize > 0)
        {
            changesMade |= fillEmpty(stack, hotbarFirst, hotbarEnd) || fillEmpty(stack, mainFirst, mainEnd);
        }
        return changesMade;
    }

    private boolean isPlayerSlot(int slot)
    {
        return slot >= mainFirst && slot < mainEnd || slot >= hotbarFirst && slot < hotbarEnd;
    }

    private static int[] shiftOrder(WidgetLayout l)
    {
        int n = 0;
        int[] order = new int[l.count()];
        for (int w = 0; w < l.count(); w++)
        {
            if (WidgetTypes.get(l.kind(w)).shiftPriority() != WidgetType.NO_SHIFT) order[n++] = w;
        }
        for (int i = 1; i < n; i++)
        {
            int w = order[i];
            int p = WidgetTypes.get(l.kind(w)).shiftPriority();
            int j = i - 1;
            while (j >= 0 && WidgetTypes.get(l.kind(order[j])).shiftPriority() > p)
            {
                order[j + 1] = order[j];
                j--;
            }
            order[j + 1] = w;
        }
        return java.util.Arrays.copyOf(order, n);
    }

    private int sectionBound(WidgetType.Section section, boolean end)
    {
        WidgetLayout l = widgets;
        for (int w = 0; w < l.count(); w++)
        {
            if (WidgetTypes.get(l.kind(w)).section() == section) return end ? l.slotFirst(w) + l.slots(w) : l.slotFirst(w);
        }
        return 0;
    }

    @Override
    public Slot getSlotFromInventory(IInventory inv, int slot)
    {
        return super.getSlotFromInventory(inv, slot);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int i)
    {
        if(source == SOURCE_WEARING)refresh();
        Slot slot = getSlot(i);
        ItemStack result = null;
        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            result = stack.copy();
            if (isPlayerSlot(i) ? !mergeIntoBackpack(stack) : !mergeIntoPlayer(stack))
            {
                return null;
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
    public void detectAndSendChanges()
    {
        refresh();
        runPending();
        syncLocks();
        super.detectAndSendChanges();
    }

    @Override
    public void refresh()
    {
        if (inventory instanceof InventoryBackpack) ((InventoryBackpack) inventory).reloadIfChanged();
    }
}
