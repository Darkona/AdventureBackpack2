package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;
import com.darkona.adventurebackpack.inventory.widget.WidgetStore;
import com.darkona.adventurebackpack.inventory.widget.classic.ClassicWidgetTypes;
import com.darkona.adventurebackpack.inventory.widget.classic.QuiverNbt;
import com.darkona.adventurebackpack.inventory.widget.classic.RopeWidget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Test;

/**
 * The backpack NBT shared by the item and the placed block: the abilities' state under one key (it used to be
 * "extendedProperties" on the item and "extended" on the block, so placing or picking up a backpack lost the Cow's
 * milk), old backpacks read without changes, and the storage size that never loses a slot.
 */
class BackpackNbtTest
{
    private static NBTTagCompound cowState(int wheat, int milk)
    {
        NBTTagCompound c = new NBTTagCompound();
        c.setInteger("wheatConsumed", wheat);
        c.setInteger("milkTime", milk);
        return c;
    }

    private static NBTTagCompound itemSlot(int slot)
    {
        NBTTagCompound c = new NBTTagCompound();
        c.setByte("Slot", (byte) slot);
        return c;
    }

    @Test
    void theOldKeyOfPlacedBackpacksIsReadAndDroppedOnTheNextWrite()
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setTag(BackpackNbt.LEGACY_EXTENDED, cowState(3, 40));
        NBTTagCompound read = BackpackNbt.readExtended(data);
        assertEquals(3, read.getInteger("wheatConsumed"));
        BackpackNbt.writeExtended(data, read);
        assertFalse(data.hasKey(BackpackNbt.LEGACY_EXTENDED));
        assertEquals(40, data.getCompoundTag(BackpackNbt.EXTENDED).getInteger("milkTime"));
    }

    @Test
    void theSharedKeyWinsOverTheOldOne()
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setTag(BackpackNbt.LEGACY_EXTENDED, cowState(1, 1));
        data.setTag(BackpackNbt.EXTENDED, cowState(9, 9));
        assertEquals(9, BackpackNbt.readExtended(data).getInteger("wheatConsumed"));
        assertTrue(BackpackNbt.readExtended(new NBTTagCompound()).hasNoTags());
    }

    @Test
    void theSavedStorageOfOldNewAndResizedBackpacks()
    {
        NBTTagCompound fresh = new NBTTagCompound();
        assertEquals(0, BackpackNbt.savedStorage(fresh));
        NBTTagCompound old = new NBTTagCompound();
        old.setTag(BackpackNbt.ITEMS, new NBTTagList());
        assertEquals(39, BackpackNbt.savedStorage(old));
        NBTTagCompound sized = new NBTTagCompound();
        sized.setInteger(BackpackNbt.STORAGE_SIZE, 15);
        assertEquals(15, BackpackNbt.savedStorage(sized));
        // A stack past the saved size still counts (a size written by an older save, or edited by hand)
        NBTTagList items = new NBTTagList();
        items.appendTag(itemSlot(10));
        items.appendTag(itemSlot(60));
        sized.setTag(BackpackNbt.ITEMS, items);
        assertEquals(55, BackpackNbt.savedStorage(sized));
        // The tool and bucket slots do not make a backpack bigger
        NBTTagList specials = new NBTTagList();
        specials.appendTag(itemSlot(44));
        NBTTagCompound tools = new NBTTagCompound();
        tools.setTag(BackpackNbt.ITEMS, specials);
        assertEquals(39, BackpackNbt.savedStorage(tools));
    }

    @Test
    void readingSizesTheArrayAndKeepsItWhenTheSizeIsTheSame()
    {
        ItemStack[] classic = new ItemStack[45];
        NBTTagCompound data = new NBTTagCompound();
        assertTrue(classic == BackpackNbt.readItems(data, classic, 45));
        assertEquals(53, BackpackNbt.readItems(data, classic, 53).length);
    }

    @Test
    void placingAndPickingUpABackpackKeepsTheAbilitiesState()
    {
        ItemStack stack = new ItemStack((Item) null);
        InventoryBackpack item = new InventoryBackpack(stack);
        assertEquals(39, item.getStorageSize());
        item.setExtendedProperties(cowState(5, 120));
        item.saveToNBT(stack.stackTagCompound);

        // Placed: the tile reads the item's compound (ItemAdventureBackpack.placeBackpack)
        TileAdventureBackpack tile = new TileAdventureBackpack();
        tile.loadFromNBT(stack.stackTagCompound);
        assertEquals(5, tile.getExtendedProperties().getInteger("wheatConsumed"));
        assertEquals(39, tile.getStorageSize());

        // Picked up: the tile writes the item's compound (TileAdventureBackpack.transferToItemStack)
        NBTTagCompound back = new NBTTagCompound();
        tile.saveToNBT(back);
        ItemStack picked = new ItemStack((Item) null);
        picked.stackTagCompound = back;
        InventoryBackpack again = new InventoryBackpack(picked);
        assertEquals(120, again.getExtendedProperties().getInteger("milkTime"));
        assertEquals(39, again.getStorageSize());
    }

    @Test
    void aBackpackPlacedBeforeTheFixKeepsItsState()
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setString("colorName", "Cow");
        data.setTag(BackpackNbt.ITEMS, new NBTTagList());
        data.setTag(BackpackNbt.LEGACY_EXTENDED, cowState(7, 60));
        NBTTagCompound emptyTank = new NBTTagCompound();
        emptyTank.setString("Empty", "");
        data.setTag("leftTank", emptyTank);
        data.setTag("rightTank", emptyTank.copy());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setTag("backpackData", data);
        ItemStack stack = new ItemStack((Item) null);
        stack.stackTagCompound = tag;
        InventoryBackpack inv = new InventoryBackpack(stack);
        assertEquals(7, inv.getExtendedProperties().getInteger("wheatConsumed"));
        inv.saveToNBT(stack.stackTagCompound);
        NBTTagCompound saved = stack.stackTagCompound.getCompoundTag("backpackData");
        assertFalse(saved.hasKey(BackpackNbt.LEGACY_EXTENDED));
        assertEquals(60, saved.getCompoundTag(BackpackNbt.EXTENDED).getInteger("milkTime"));
        assertEquals(39, saved.getInteger(BackpackNbt.STORAGE_SIZE));
    }

    private static WidgetLayout skeleton()
    {
        ClassicWidgetTypes.install();
        return ClassicArrangement.arrange(6, 8, 39, 4);
    }

    @Test
    void savingKeepsTheKeysItDoesNotKnow()
    {
        NBTTagCompound previous = new NBTTagCompound();
        NBTTagList addon = new NBTTagList();
        addon.appendTag(new NBTTagCompound());
        previous.setTag("ABPW:addon:pouch", addon);
        previous.setInteger("somethingNewer", 7);
        previous.setTag(RopeWidget.KEY, new NBTTagList());
        previous.setString("colorName", "Old");
        NBTTagCompound fresh = new NBTTagCompound();
        fresh.setString("colorName", "New");
        WidgetStore.keepUnknown(previous, fresh, skeleton());
        assertEquals(1, fresh.getTagList("ABPW:addon:pouch", 10).tagCount());
        assertEquals(7, fresh.getInteger("somethingNewer"));
        assertFalse(fresh.hasKey(RopeWidget.KEY), "a key the backpack removed on purpose stays removed");
        assertEquals("New", fresh.getString("colorName"));
    }

    @Test
    void aWidgetMissingFromTheLayoutKeepsItsOldList()
    {
        NBTTagCompound previous = new NBTTagCompound();
        NBTTagList arrows = new NBTTagList();
        arrows.appendTag(new NBTTagCompound());
        previous.setTag(QuiverNbt.QUIVER, arrows);
        previous.setByte(QuiverNbt.QUIVER_FILL, (byte) 2);
        NBTTagCompound fresh = new NBTTagCompound();
        WidgetStore.keepUnknown(previous, fresh, ClassicArrangement.arrange(6, 8, 39, 0));
        assertEquals(1, fresh.getTagList(QuiverNbt.QUIVER, 10).tagCount());
        assertEquals(2, fresh.getByte(QuiverNbt.QUIVER_FILL));
    }

    @Test
    void ropeAndQuiverListsKeepTheirOldFormat()
    {
        WidgetLayout layout = skeleton();
        Item item = new Item();
        ItemStack[] inv = new ItemStack[layout.inventorySize()];
        inv[49] = new ItemStack(item, 128);
        inv[46] = new ItemStack(item, 9);
        inv[39] = new ItemStack(item, 1);
        NBTTagCompound data = new NBTTagCompound();
        WidgetStore.write(data, layout, inv, null, 39);
        NBTTagCompound rope = data.getTagList(RopeWidget.KEY, 10).getCompoundTagAt(0);
        assertFalse(rope.hasKey("Slot"), "the rope entry never had a Slot byte");
        assertEquals(128, rope.getInteger(RopeWidget.COUNT_KEY));
        NBTTagCompound arrow = data.getTagList(QuiverNbt.QUIVER, 10).getCompoundTagAt(0);
        assertEquals(1, arrow.getByte("Slot"));
        assertEquals(1, data.getTagList(QuiverNbt.QUIVER, 10).tagCount());
        NBTTagList items = data.getTagList(BackpackNbt.ITEMS, 10);
        assertEquals(1, items.tagCount(), "quiver and rope never go to ABPItems");
        assertEquals(39, items.getCompoundTagAt(0).getByte("Slot"));
        assertEquals(39, data.getInteger(BackpackNbt.STORAGE_SIZE));

        inv[49] = null;
        WidgetStore.write(data, layout, inv, null, 39);
        assertFalse(data.hasKey(RopeWidget.KEY), "an empty rope slot writes no list, as before");
    }

    @Test
    void theReadKeysFollowTheWidgetsOfTheLayout()
    {
        java.util.List<String> keys = java.util.Arrays.asList(WidgetStore.readKeys(skeleton()));
        assertTrue(keys.contains(QuiverNbt.QUIVER));
        assertTrue(keys.contains(QuiverNbt.QUIVER_FILL));
        assertTrue(keys.contains(RopeWidget.KEY));
        assertTrue(keys.contains(BackpackNbt.ITEMS));
        assertFalse(java.util.Arrays.asList(WidgetStore.readKeys(ClassicArrangement.arrange(6, 8, 39, 0))).contains(QuiverNbt.QUIVER));
    }
}
