package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.common.QuiverRules;
import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fluids.FluidTank;
import org.junit.jupiter.api.Test;

/**
 * The quiver of the Skeleton Backpack in its NBT and in its inventory: saved apart from the item list (so the storage
 * size never counts it), never lost when the quiver is turned off or made smaller, its fill level written for the
 * model, and the right tank it replaces, whose fluid moves to the left tank or stays sealed.
 */
class QuiverTest
{
    private static NBTTagCompound quiverEntry(int slot, int count)
    {
        NBTTagCompound c = new NBTTagCompound();
        c.setByte("Slot", (byte) slot);
        c.setShort("id", (short) 262);
        c.setByte("Count", (byte) count);
        c.setShort("Damage", (short) 0);
        return c;
    }

    private static NBTTagCompound skeletonData()
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setString("colorName", "Skeleton");
        // Empty tanks: reading a fluid needs the FluidRegistry, which needs the running game
        NBTTagCompound empty = new NBTTagCompound();
        empty.setString("Empty", "");
        data.setTag("leftTank", empty);
        data.setTag("rightTank", empty.copy());
        return data;
    }

    @Test
    void theQuiverIsSavedApartFromTheStorageWithItsFillLevel()
    {
        ItemStack[] inv = new ItemStack[BackpackLayout.inventorySize(39, 4)];
        Item arrow = new Item();
        inv[3] = new ItemStack(arrow, 20);
        int base = BackpackLayout.quiverIndex(39, 0);
        inv[base] = new ItemStack(arrow, 10);
        inv[base + 2] = new ItemStack(arrow, 5);
        NBTTagCompound data = skeletonData();
        data.setTag(BackpackNbt.ITEMS, BackpackNbt.writeItems(inv, base));
        BackpackNbt.writeQuiver(data, inv, base, 4, null);

        assertEquals(1, data.getTagList(BackpackNbt.ITEMS, 10).tagCount());
        NBTTagList quiver = data.getTagList(BackpackNbt.QUIVER, 10);
        assertEquals(2, quiver.tagCount());
        assertEquals(0, quiver.getCompoundTagAt(0).getByte("Slot"));
        assertEquals(2, quiver.getCompoundTagAt(1).getByte("Slot"));
        assertEquals(QuiverRules.LEVEL_SOME, data.getByte(BackpackNbt.QUIVER_FILL));
        // The storage size read back is the classic one: the quiver is not in the item list
        assertEquals(BackpackLayout.LEGACY_STORAGE, BackpackNbt.savedStorage(data));

        // Empty quiver: level 0; a backpack that never had a quiver writes nothing
        java.util.Arrays.fill(inv, base, base + 4, null);
        BackpackNbt.writeQuiver(data, inv, base, 4, null);
        assertEquals(QuiverRules.LEVEL_EMPTY, data.getByte(BackpackNbt.QUIVER_FILL));
        NBTTagCompound plain = new NBTTagCompound();
        BackpackNbt.writeQuiver(plain, new ItemStack[45], 45, 0, null);
        assertFalse(plain.hasKey(BackpackNbt.QUIVER));
        assertFalse(plain.hasKey(BackpackNbt.QUIVER_FILL));
    }

    @Test
    void arrowsWithNoSlotNowAreKeptUntilTheSlotsComeBack()
    {
        NBTTagCompound data = skeletonData();
        NBTTagList list = new NBTTagList();
        list.appendTag(quiverEntry(0, 12));
        list.appendTag(quiverEntry(3, 40));
        data.setTag(BackpackNbt.QUIVER, list);

        // Quiver off: nothing is read into the inventory, everything is kept and written back unchanged
        ItemStack[] classic = new ItemStack[45];
        NBTTagList kept = BackpackNbt.readQuiver(data, classic, 45, 0);
        assertEquals(2, kept.tagCount());
        NBTTagCompound saved = new NBTTagCompound();
        BackpackNbt.writeQuiver(saved, classic, 45, 0, kept);
        assertEquals(2, saved.getTagList(BackpackNbt.QUIVER, 10).tagCount());
        assertEquals(40, saved.getTagList(BackpackNbt.QUIVER, 10).getCompoundTagAt(1).getByte("Count"));
        assertEquals(QuiverRules.LEVEL_EMPTY, saved.getByte(BackpackNbt.QUIVER_FILL));

        // Two slots now: slot 3 has no place and is kept, slot 0 is read
        ItemStack[] small = new ItemStack[47];
        kept = BackpackNbt.readQuiver(data, small, 45, 2);
        assertEquals(1, kept.tagCount());
        assertEquals(3, kept.getCompoundTagAt(0).getByte("Slot"));
        // Nothing kept: null, no list built
        assertNull(BackpackNbt.readQuiver(data, new ItemStack[49], 45, 4));
    }

    @Test
    void theSkeletonInventoryHasItsQuiverAtTheEndOfTheArray()
    {
        ItemStack stack = new ItemStack((Item) null);
        stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setTag("backpackData", skeletonData());
        InventoryBackpack inv = new InventoryBackpack(stack);
        assertEquals(4, inv.getQuiverSlots());
        assertEquals(39, inv.getStorageSize());
        assertEquals(49, inv.getSizeInventory());

        TileAdventureBackpack tile = new TileAdventureBackpack();
        tile.loadFromNBT(stack.stackTagCompound);
        assertEquals(4, tile.getQuiverSlots());
        assertEquals(49, tile.getSizeInventory());

        boolean was = ConfigHandler.QUIVER_ENABLED;
        try
        {
            ConfigHandler.QUIVER_ENABLED = false;
            ConfigHandler.BACKPACK_TYPES_GENERATION++;
            inv.loadFromNBT(stack.stackTagCompound);
            assertEquals(0, inv.getQuiverSlots());
            assertEquals(45, inv.getSizeInventory());
        } finally
        {
            ConfigHandler.QUIVER_ENABLED = was;
            ConfigHandler.BACKPACK_TYPES_GENERATION++;
        }
    }

    @Test
    void theQuiverCountAndLevelReadOnlyItsSlots()
    {
        ItemStack[] inv = new ItemStack[49];
        Item item = new Item();
        inv[10] = new ItemStack(item, 64);
        inv[45] = new ItemStack(item, 30);
        inv[48] = new ItemStack(item, 3);
        assertEquals(33, Quiver.count(inv, 45, 4));
        assertEquals(QuiverRules.LEVEL_MANY, Quiver.level(inv, 45, 4));
        assertEquals(QuiverRules.LEVEL_EMPTY, Quiver.level(inv, 45, 0));
        // Slots past the array are not read
        assertEquals(33, Quiver.count(inv, 45, 10));
    }

    /**
     * The right tank of a backpack with a quiver takes nothing more: its capacity is what it holds (0 when empty), and
     * it opens again with the quiver off. How much of an old right tank moves to the left one is
     * QuiverRules.movedToLeft (QuiverRulesTest): a FluidStack needs the FluidRegistry, which needs the running game.
     */
    @Test
    void theRightTankIsSealedWhileThereIsAQuiver()
    {
        FluidTank left = new FluidTank(4000);
        FluidTank right = new FluidTank(4000);
        Quiver.sealRightTank(left, right, true);
        assertEquals(0, right.getCapacity());
        assertEquals(4000, left.getCapacity());
        Quiver.sealRightTank(left, right, false);
        assertEquals(ConfigHandler.BACKPACK_TANK_CAPACITY, right.getCapacity());
    }
}
