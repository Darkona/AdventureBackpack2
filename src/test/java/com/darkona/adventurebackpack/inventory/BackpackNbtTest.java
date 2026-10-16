package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
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
}
