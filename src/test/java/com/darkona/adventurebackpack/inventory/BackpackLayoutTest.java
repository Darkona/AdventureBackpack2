package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adventurebackpack.api.BackpackShape;
import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.common.AbilityRegistry;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.BackpackNames;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * The window of every backpack type, as the layout computes it: the classic numbers of the old fixed window for each
 * of today's types, no overlaps and nothing outside the window for any size, and a bigger example profile that comes
 * only from a shape (config or ability), with no other change.
 */
class BackpackLayoutTest
{
    /** The tanks, buttons and window size the old GuiAdvBackpack had as constants. */
    private static void assertClassicWindow(BackpackLayout l, String who)
    {
        assertEquals(248, l.width(), who);
        assertEquals(207, l.height(), who);
        assertEquals(25, l.tankX(0), who);
        assertEquals(207, l.tankX(1), who);
        assertEquals(7, l.tankY(), who);
        assertEquals(16, l.tankWidth(), who);
        assertEquals(100, l.tankHeight(), who);
        assertEquals(5, l.buttonX(), who);
        assertEquals(91, l.buttonY(), who);
        assertEquals(43, l.toggleX(), who);
        assertEquals(63, l.toggleY(), who);
        assertEquals(39, l.storage(), who);
        assertEquals(92, l.slotCount(), who);
        assertEquals(46, l.inventorySize(), who);
    }

    /** Container slot table of a layout, in the same format as ContainerBackpackSlotsTest (positions and indices). */
    private static List<String> slots(BackpackLayout l)
    {
        List<String> out = new ArrayList<String>();
        for (int k = 0; k < l.storage(); k++) out.add(out.size() + " storage " + BackpackLayout.inventoryIndex(k) + " " + l.storageX(k) + "," + l.storageY(k));
        for (int i = 0; i < 2; i++) out.add(out.size() + " tool " + (BackpackLayout.UPPER_TOOL + i) + " " + l.toolX() + "," + l.toolY(i));
        for (int i = 0; i < l.bucketSlots(); i++) out.add(out.size() + " bucket " + BackpackLayout.bucketIndex(i) + " " + l.bucketX(i) + "," + l.bucketY(i));
        for (int q = 0; q < l.quiver(); q++) out.add(out.size() + " quiver " + BackpackLayout.quiverIndex(l.storage(), q) + " " + l.quiverX(q) + "," + l.quiverY(q));
        out.add(out.size() + " rope " + BackpackLayout.ropeIndex(l.storage(), l.quiver()) + " " + l.ropeX() + "," + l.ropeY());
        for (int i = 0; i < 9; i++) out.add(out.size() + " craft " + i + " " + l.craftX(i) + "," + l.craftY(i));
        out.add(out.size() + " result 0 " + l.resultX() + "," + l.resultY());
        for (int i = 0; i < 27; i++) out.add(out.size() + " player " + (9 + i) + " " + (l.playerX() + 18 * (i % 9)) + "," + (l.playerY() + 18 * (i / 9)));
        for (int i = 0; i < 9; i++) out.add(out.size() + " player " + i + " " + (l.playerX() + 18 * i) + "," + l.hotbarY());
        return out;
    }

    @Test
    void everyBackpackTypeHasTheClassicWindow()
    {
        for (String name : BackpackNames.backpackNames)
        {
            // The Skeleton's quiver (enabled.quiver, on by default) changes its window: see theSkeletonHasAQuiver...
            if (name.equals("Skeleton")) continue;
            BackpackLayout l = BackpackLayout.view(WidgetLayouts.widgets(name, 0));
            assertClassicWindow(l, name);
            assertEquals(ContainerBackpackSlotsTest.expectedClassicSlots(false), slots(l), name);
        }
    }

    @Test
    void theSpecialSlotsKeepTheirOldInventoryIndices()
    {
        assertEquals(39, BackpackLayout.UPPER_TOOL);
        assertEquals(40, BackpackLayout.LOWER_TOOL);
        assertEquals(41, BackpackLayout.BUCKET_IN_LEFT);
        assertEquals(42, BackpackLayout.BUCKET_OUT_LEFT);
        assertEquals(43, BackpackLayout.BUCKET_IN_RIGHT);
        assertEquals(44, BackpackLayout.BUCKET_OUT_RIGHT);
        assertEquals(45, BackpackLayout.inventorySize(BackpackLayout.LEGACY_STORAGE));
        com.darkona.adventurebackpack.common.layout.WidgetLayout classic = BackpackLayout.of(6, 8).widgets();
        assertEquals(39, com.darkona.adventurebackpack.common.layout.classic.ClassicSlots.toolIndex(classic, 0));
        assertEquals(40, com.darkona.adventurebackpack.common.layout.classic.ClassicSlots.toolIndex(classic, 1));
    }

    @Test
    void storageIndicesSkipTheSpecialSlotsAndFitTheNbtByte()
    {
        assertEquals(38, BackpackLayout.inventoryIndex(38));
        assertEquals(45, BackpackLayout.inventoryIndex(39));
        assertTrue(BackpackLayout.isStorageIndex(38, 39));
        assertFalse(BackpackLayout.isStorageIndex(39, 47));
        assertFalse(BackpackLayout.isStorageIndex(44, 47));
        assertTrue(BackpackLayout.isStorageIndex(52, 47));
        assertFalse(BackpackLayout.isStorageIndex(53, 47));
        assertFalse(BackpackLayout.isStorageIndex(20, 15));
        assertEquals(45, BackpackLayout.inventorySize(15));
        assertEquals(53, BackpackLayout.inventorySize(47));
        assertEquals(47, BackpackLayout.storageOfInventory(53));
        assertTrue(BackpackLayout.inventoryIndex(BackpackLayout.MAX_STORAGE - 1) <= Byte.MAX_VALUE);
    }

    @Test
    void noPieceOverlapsAnotherOrLeavesTheWindow()
    {
        for (int rows = BackpackLayout.MIN_ROWS; rows <= BackpackLayout.MAX_ROWS; rows++)
        {
            for (int cols = BackpackLayout.MIN_COLUMNS; cols <= BackpackLayout.MAX_COLUMNS; cols++)
            {
                for (int quiver = 0; quiver <= BackpackLayout.maxQuiver(rows); quiver++)
                {
                    assertNoOverlap(BackpackLayout.of(rows, cols, quiver), rows + "x" + cols + " quiver " + quiver);
                }
            }
        }
    }

    private static void assertNoOverlap(BackpackLayout l, String who)
    {
        int rows = l.rows(), cols = l.columns();
        {
            {
                assertEquals(rows * cols - 9, l.storage(), who);
                List<int[]> rects = new ArrayList<int[]>();
                for (String s : slots(l))
                {
                    String[] xy = s.substring(s.lastIndexOf(' ') + 1).split(",");
                    rects.add(new int[]{Integer.parseInt(xy[0]) - 1, Integer.parseInt(xy[1]) - 1, 18, 18});
                }
                for (int t = 0; t < l.tanks(); t++)
                {
                    rects.add(new int[]{l.tankX(t) - 1, l.tankY() - 1, l.tankWidth() + 2, l.tankHeight() + 2});
                }
                if (l.quiver() > 0)
                {
                    // The pipe to the crafting result runs under the column of the right tank
                    rects.add(new int[]{l.tankX(1) - 2, l.resultY() + 11, 20, 6});
                }
                rects.add(new int[]{l.buttonX(), l.buttonY(), 18, 18});
                rects.add(new int[]{l.toggleX(), l.toggleY(), 18, 12});
                for (int i = 0; i < rects.size(); i++)
                {
                    int[] a = rects.get(i);
                    assertTrue(a[0] >= 0 && a[1] >= 0 && a[0] + a[2] <= l.width() && a[1] + a[3] <= l.height(), who + " piece " + i + " outside");
                    for (int j = i + 1; j < rects.size(); j++)
                    {
                        int[] b = rects.get(j);
                        boolean overlap = a[0] < b[0] + b[2] && b[0] < a[0] + a[2] && a[1] < b[1] + b[3] && b[1] < a[1] + a[3];
                        // The switch sits on the frame of the upper tool slot by design (it did before too)
                        boolean toggleOnTool = j == rects.size() - 1 && i == l.toolStart();
                        // With two tanks the pipe is not a rect here; with a quiver it touches no slot
                        assertFalse(overlap && !toggleOnTool, who + " pieces " + i + " and " + j + " overlap");
                    }
                }
                assertTrue(l.lowerX() >= 0 && l.lowerX() + l.lowerWidth() <= l.width(), who);
            }
        }
    }

    @Test
    void aBackpackNeverLosesSlotsWhenItsTypeShrinks()
    {
        assertEquals(47, BackpackLayout.storageFor(6, 8, 47));
        BackpackLayout l = BackpackLayout.forStorage(6, 8, 47);
        assertEquals(47, l.storage());
        assertEquals(7, l.rows());
        // Past the most rows, columns are added
        BackpackLayout wide = BackpackLayout.forStorage(3, 8, 100);
        assertEquals(100, wide.storage());
        assertTrue(wide.rows() * wide.columns() - 9 >= 100);
        assertEquals(39, BackpackLayout.storageFor(6, 8, 0));
        assertEquals(23, BackpackLayout.storageFor(4, 8, 0));
        assertEquals(23, BackpackLayout.storageFor(1, 8, 0));
        assertEquals(39, BackpackLayout.storageFor(4, 8, 39));
    }

    /** The example of a bigger backpack: a 7 by 9 grid asked by an ability, with nothing else changed. */
    private static final String BIG = "LayoutTestBig";

    static void registerBigExample()
    {
        if (AbilityRegistry.INSTANCE.shapedColors().contains(BIG)) return;
        AbilityRegistry.INSTANCE.register(BIG, new IBackpackAbility()
        {
            @Override
            public BackpackShape getShape(BackpackShape configured)
            {
                return configured.withRows(configured.getRows() + 1).withColumns(9);
            }
        });
    }

    @Test
    void aBiggerBackpackIsOnlyAnotherShape()
    {
        registerBigExample();
        assertEquals(com.darkona.adventurebackpack.common.layout.classic.ClassicComposition.of(7, 9, 0), WidgetLayouts.spec(BIG));
        assertEquals(com.darkona.adventurebackpack.common.layout.classic.ClassicComposition.of(6, 8, 0), WidgetLayouts.spec("Standard"));
        BackpackLayout l = BackpackLayout.view(WidgetLayouts.widgets(BIG, 0));
        assertEquals(54, l.storage());
        assertEquals(107, l.slotCount());
        assertEquals(61, l.inventorySize());
        // The grid starts where it always did; everything right of it and under it moves with it
        assertEquals(62, l.storageX(0));
        assertEquals(7, l.storageY(0));
        assertEquals(62 + 18 * 8, l.storageX(8));
        assertEquals(62 + 18 * 6, l.craftLeft());
        assertEquals(7 + 18 * 4, l.craftTop());
        assertEquals(62 + 18 * 9 + 1, l.tankX(1));
        assertEquals(18 * 7 - 8, l.tankHeight());
        assertEquals(266, l.width());
        assertEquals(225, l.height());
        assertEquals(62, l.playerX());
        assertEquals(54, l.toolStart());
        assertEquals(71, l.playerStart());
        assertEquals(98, l.hotbarStart());
        // An ability that asks for too much is kept inside the limits
        assertEquals(BackpackLayout.MAX_COLUMNS, BackpackLayout.of(20, 40).columns());
        assertEquals(BackpackLayout.MAX_ROWS, BackpackLayout.of(20, 40).rows());
    }

    @Test
    void theContainerOfTheBiggerBackpackFollowsItsShape() throws Exception
    {
        registerBigExample();
        ContainerBackpack c = ContainerBackpackSlotsTest.build(BIG);
        List<String> got = ContainerBackpackSlotsTest.describe(c);
        assertEquals(slots(BackpackLayout.view(WidgetLayouts.widgets(BIG, 0))), got);
        assertTrue(got.get(BackpackLayout.view(c).playerStart()).contains(" player 9 "));
        assertTrue(got.get(BackpackLayout.view(c).slotCount() - 1).contains(" player 8 "));
    }

    /** With the defaults (enabled.quiver on, 4 slots) the Skeleton has one tank and its quiver in the right tank's column. */
    @Test
    void theSkeletonHasAQuiverInThePlaceOfTheRightTank()
    {
        BackpackLayout l = BackpackLayout.view(WidgetLayouts.widgets("Skeleton", 0));
        assertEquals(4, l.quiver());
        assertEquals(1, l.tanks());
        assertEquals(2, l.bucketSlots());
        assertEquals(39, l.storage());
        // Storage, tools, two buckets, four quiver slots, crafting grid, result, player
        assertEquals(39 + 2 + 2 + 4 + 1 + 9 + 1 + 36, l.slotCount());
        assertEquals(43, l.quiverStart());
        assertEquals(48, l.craftStart());
        assertEquals(58, l.playerStart());
        assertEquals(45 + 4 + 1, l.inventorySize());
        // The column of the right tank (x 207), one slot per storage row from the top
        for (int q = 0; q < 4; q++)
        {
            assertEquals(207, l.quiverX(q));
            assertEquals(7 + 18 * q, l.quiverY(q));
            assertEquals(45 + q, BackpackLayout.quiverIndex(39, q));
        }
        // The window itself does not move: same size, left tank, buttons
        assertEquals(248, l.width());
        assertEquals(207, l.height());
        assertEquals(25, l.tankX(0));
        assertEquals(226, l.resultX());
        // Past the rows of the right tank's column the quiver goes on in the column of its bucket slots
        BackpackLayout six = BackpackLayout.of(6, 8, 6);
        assertEquals(207, six.quiverX(4));
        assertEquals(226, six.quiverX(5));
        assertEquals(7, six.quiverY(5));
        assertEquals(10, BackpackLayout.maxQuiver(6));
        assertEquals(6, BackpackLayout.maxQuiver(BackpackLayout.MIN_ROWS));
        assertEquals(10, BackpackLayout.of(6, 8, 50).quiver());
        assertEquals(0, BackpackLayout.of(6, 8, -3).quiver());
        assertNoOverlap(l, "Skeleton");
    }

    /** enabled.quiver off: the Skeleton has its classic window with two tanks again. */
    @Test
    void withoutTheQuiverTheSkeletonHasTheClassicWindow()
    {
        boolean was = ConfigHandler.QUIVER_ENABLED;
        try
        {
            ConfigHandler.QUIVER_ENABLED = false;
            ConfigHandler.BACKPACK_TYPES_GENERATION++;
            BackpackLayout l = BackpackLayout.view(WidgetLayouts.widgets("Skeleton", 0));
            assertEquals(0, l.quiver());
            assertClassicWindow(l, "Skeleton without quiver");
            assertEquals(ContainerBackpackSlotsTest.expectedClassicSlots(false), slots(l));
        } finally
        {
            ConfigHandler.QUIVER_ENABLED = was;
            ConfigHandler.BACKPACK_TYPES_GENERATION++;
        }
        assertEquals(4, WidgetLayouts.quiver("Skeleton"));
        assertEquals(0, WidgetLayouts.quiver("Standard"));
    }

    @Test
    void theContainerOfTheSkeletonHasQuiverSlotsAndOneTank() throws Exception
    {
        ContainerBackpack c = ContainerBackpackSlotsTest.build("Skeleton");
        List<String> got = ContainerBackpackSlotsTest.describe(c);
        assertEquals(slots(BackpackLayout.view(WidgetLayouts.widgets("Skeleton", 0))), got);
        assertTrue(got.get(BackpackLayout.view(c).quiverStart()).endsWith(" quiver 45 207,7"), got.get(BackpackLayout.view(c).quiverStart()));
        assertTrue(got.get(BackpackLayout.view(c).bucketStart() + 1).contains(" bucket 42 "));
        assertTrue(got.get(BackpackLayout.view(c).quiverStart() + 5).contains(" craft 0 "));
        assertTrue(got.get(BackpackLayout.view(c).slotCount() - 1).contains(" player 8 "));
    }
}
