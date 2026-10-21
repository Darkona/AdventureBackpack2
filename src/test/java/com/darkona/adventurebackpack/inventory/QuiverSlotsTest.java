package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;
import com.darkona.adventurebackpack.common.layout.classic.ClassicSlots;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityHopper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Every way into the Skeleton Backpack's quiver takes only the items of backpacks.skeleton.quiver.items (arrows and
 * Crossbow Bolts by default): a click, a drag, a hotbar number key, a shift-click, the worn backpack's inventory, the
 * placed block's inventory and a hopper feeding the placed block. The real container, inventory and tile run; only
 * the item registry is replaced, because it needs the running game. A slot that changes calls FML (the side check of
 * the backpack slots) and a bucket check calls the FluidContainerRegistry, which do not start without the game: so
 * the container is driven with what it must refuse, and the shift-click, which merges by slot range without asking
 * the slot, is checked on the ranges it sends each item to.
 */
class QuiverSlotsTest
{
    private final Item arrow = new Item();
    private final Item bolt = new Item();
    private final Item stick = new Item().setMaxStackSize(64);
    private Quiver.Names saved;

    @BeforeEach
    void names()
    {
        saved = Quiver.names;
        Quiver.names = new Quiver.Names()
        {
            @Override
            public String of(Item item)
            {
                if (item == arrow) return "minecraft:arrow";
                if (item == bolt) return "adventurebackpack:bolt";
                if (item == stick) return "minecraft:stick";
                return null;
            }
        };
    }

    @AfterEach
    void restore()
    {
        Quiver.names = saved;
    }

    private Map<Integer, ItemStack> contents;

    private ContainerBackpack skeleton() throws Exception
    {
        contents = new HashMap<Integer, ItemStack>();
        ContainerBackpack c = ContainerBackpackSlotsTest.build("Skeleton", contents);
        assertTrue(BackpackLayout.view(c).quiver() > 0, "the Skeleton has a quiver by default");
        return c;
    }

    private static boolean quiverHolds(ContainerBackpack c, Item item)
    {
        for (int q = 0; q < BackpackLayout.view(c).quiver(); q++)
        {
            ItemStack s = c.getSlot(BackpackLayout.view(c).quiverStart() + q).getStack();
            if (s != null && s.getItem() == item) return true;
        }
        return false;
    }

    @Test
    void theQuiverSlotTakesArrowsAndBoltsOnly()
    {
        SlotQuiver slot = new SlotQuiver(null, 0, 0, 0);
        assertTrue(slot.isItemValid(new ItemStack(arrow)));
        assertTrue(slot.isItemValid(new ItemStack(bolt)));
        assertFalse(slot.isItemValid(new ItemStack(stick)));
        assertFalse(slot.isItemValid(new ItemStack(new Item())), "an item with no registry name");
        assertFalse(Quiver.accepts(null));
    }

    @Test
    void anOreFilterAcceptsListedNamesWithoutTheDictionary()
    {
        // A stack named by the list is accepted before the Ore Dictionary is asked (audit P21: the dictionary used to
        // build the stack's ore id set and names on every check). The dictionary does not start without the game,
        // so the ore lists themselves are checked in game.
        com.darkona.adventurebackpack.common.QuiverRules.Filter before = com.darkona.adventurebackpack.config.ConfigHandler.QUIVER_FILTER;
        try
        {
            com.darkona.adventurebackpack.config.ConfigHandler.QUIVER_FILTER =
                    com.darkona.adventurebackpack.common.QuiverRules.Filter.parse(new String[]{"minecraft:arrow", "ore:abtestNoSuchOre"});
            assertTrue(Quiver.accepts(new ItemStack(arrow)));
            assertTrue(Quiver.accepts(new ItemStack(arrow)), "asked again with the same filter");
        } finally
        {
            com.darkona.adventurebackpack.config.ConfigHandler.QUIVER_FILTER = before;
        }
    }

    @Test
    void aClickPutsArrowsInAndLeavesAnythingElseOnTheCursor() throws Exception
    {
        // Vanilla's left and right click (slotClick mode 0) place the cursor stack only when the slot's isItemValid
        // agrees; the click then calls onSlotChanged, which needs FML, so the slots of the real container are asked
        // directly.
        ContainerBackpack c = skeleton();
        int q = BackpackLayout.view(c).quiverStart();
        for (int k = 0; k < BackpackLayout.view(c).quiver(); k++)
        {
            Slot slot = c.getSlot(q + k);
            assertFalse(slot.isItemValid(new ItemStack(stick, 5)));
            assertTrue(slot.isItemValid(new ItemStack(arrow, 5)));
            assertTrue(slot.isItemValid(new ItemStack(bolt, 5)));
        }
    }

    @Test
    void aDragOverTheQuiverPutsNothingElseIn() throws Exception
    {
        ContainerBackpack c = skeleton();
        EntityPlayer player = c.player;
        int q = BackpackLayout.view(c).quiverStart();
        player.inventory.setItemStack(new ItemStack(stick, 8));
        c.slotClick(-999, 0, 5, player);
        for (int k = 0; k < BackpackLayout.view(c).quiver(); k++) c.slotClick(q + k, 1, 5, player);
        c.slotClick(-999, 2, 5, player);
        assertFalse(quiverHolds(c, stick));
        assertEquals(8, player.inventory.getItemStack().stackSize, "nothing left the cursor");
    }

    @Test
    void aHotbarNumberKeyCannotSwapAnythingElseIn() throws Exception
    {
        ContainerBackpack c = skeleton();
        EntityPlayer player = c.player;
        int q = BackpackLayout.view(c).quiverStart();
        player.inventory.mainInventory[0] = new ItemStack(stick, 3);
        for (int k = 0; k < BackpackLayout.view(c).quiver(); k++)
        {
            c.slotClick(q + k, 0, 2, player);
            assertNull(c.getSlot(q + k).getStack());
            assertEquals(3, player.inventory.mainInventory[0].stackSize);
        }
    }

    /**
     * Shift-click from the player: only an item the quiver accepts is merged into the quiver's range; everything else
     * goes to the tool, bucket or storage ranges. Vanilla's mergeItemStack does not ask the slot, so those ranges must
     * never overlap the quiver, in any shape the config or an ability can give.
     */
    @Test
    void shiftClickSendsAnythingElseToRangesOutsideTheQuiver()
    {
        int shapes = 0;
        for (int rows = 4; rows <= 10; rows++)
        {
            for (int columns = 8; columns <= 12; columns++)
            {
                for (int quiver = 1; quiver <= BackpackLayout.maxQuiver(rows); quiver++)
                {
                    BackpackLayout l = BackpackLayout.of(rows, columns, quiver);
                    int from = l.quiverStart();
                    int to = from + l.quiver();
                    String shape = rows + "x" + columns + "+" + quiver;
                    assertTrue(l.storage() <= from || 0 >= to, "storage " + shape);
                    assertTrue(l.toolStart() + 2 <= from || l.toolStart() >= to, "tools " + shape);
                    assertTrue(l.bucketStart() + 1 <= from || l.bucketStart() >= to, "left bucket " + shape);
                    if (l.tanks() >= 2) assertTrue(l.bucketStart() + 3 <= from || l.bucketStart() + 2 >= to, "right bucket " + shape);
                    assertTrue(to <= l.playerStart(), "the quiver is part of the backpack " + shape);
                    shapes++;
                }
            }
        }
        assertTrue(shapes > 100);
        assertTrue(Quiver.accepts(new ItemStack(arrow)), "arrows take the quiver branch");
        assertFalse(Quiver.accepts(new ItemStack(stick)), "sticks take the other branches");
    }

    @Test
    void theRealContainerShowsTheQuiverWithQuiverSlots() throws Exception
    {
        ContainerBackpack c = skeleton();
        assertEquals(BackpackLayout.view(c).quiver(), 4);
        for (int k = 0; k < BackpackLayout.view(c).quiver(); k++) assertNull(c.getSlot(BackpackLayout.view(c).quiverStart() + k).getStack());
        assertSame(SlotQuiver.class, c.getSlot(BackpackLayout.view(c).quiverStart()).getClass(), "the quiver slots are SlotQuiver");
        assertSame(SlotQuiver.class, c.getSlot(BackpackLayout.view(c).quiverStart() + BackpackLayout.view(c).quiver() - 1).getClass());
    }

    private static NBTTagCompound skeletonData()
    {
        NBTTagCompound data = new NBTTagCompound();
        data.setString("colorName", "Skeleton");
        NBTTagCompound empty = new NBTTagCompound();
        empty.setString("Empty", "");
        data.setTag("leftTank", empty);
        data.setTag("rightTank", empty.copy());
        return data;
    }

    @Test
    void theWornAndThePlacedBackpackCheckTheirQuiverSlots()
    {
        ItemStack stack = new ItemStack((Item) null);
        stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setTag("backpackData", skeletonData());
        InventoryBackpack worn = new InventoryBackpack(stack);
        TileAdventureBackpack placed = new TileAdventureBackpack();
        placed.loadFromNBT(stack.stackTagCompound);
        int first = BackpackLayout.quiverIndex(worn.getStorageSize(), 0);
        for (int q = 0; q < ClassicSlots.quiverSlots(worn.getLayout()); q++)
        {
            assertTrue(worn.isItemValidForSlot(first + q, new ItemStack(arrow)));
            assertTrue(worn.isItemValidForSlot(first + q, new ItemStack(bolt)));
            assertFalse(worn.isItemValidForSlot(first + q, new ItemStack(stick)));
            assertTrue(placed.isItemValidForSlot(first + q, new ItemStack(arrow)));
            assertFalse(placed.isItemValidForSlot(first + q, new ItemStack(stick)));
        }
    }

    @Test
    void aHopperFillsThePlacedBackpackButNeverPutsAnythingElseInTheQuiver()
    {
        ItemStack stack = new ItemStack((Item) null);
        stack.stackTagCompound = new NBTTagCompound();
        stack.stackTagCompound.setTag("backpackData", skeletonData());
        // The bucket slots ask the FluidContainerRegistry, which needs the game: here they take nothing. The quiver
        // slots and everything else run the tile's own check, and the hopper is vanilla's.
        TileAdventureBackpack placed = new TileAdventureBackpack()
        {
            @Override
            public boolean isItemValidForSlot(int slot, ItemStack s)
            {
                if (slot == ClassicArrangement.BUCKET_IN_LEFT
                        || slot == ClassicArrangement.BUCKET_IN_RIGHT) return false;
                return super.isItemValidForSlot(slot, s);
            }
        };
        placed.loadFromNBT(stack.stackTagCompound);
        int first = BackpackLayout.quiverIndex(placed.getStorageSize(), 0);
        // Far more sticks than the backpack holds: everything that takes them fills up
        ItemStack rest = null;
        for (int i = 0; i < 80; i++) rest = TileEntityHopper.func_145889_a(placed, new ItemStack(stick, 64), 0);
        assertNotNull(rest, "the backpack is full of sticks");
        for (int q = 0; q < ClassicSlots.quiverSlots(placed.getLayout()); q++) assertNull(placed.getStackInSlot(first + q));

        ItemStack left = TileEntityHopper.func_145889_a(placed, new ItemStack(arrow, 64), 0);
        assertNull(left, "arrows still fit: in the quiver");
        assertSame(arrow, placed.getStackInSlot(first).getItem());
    }
}
