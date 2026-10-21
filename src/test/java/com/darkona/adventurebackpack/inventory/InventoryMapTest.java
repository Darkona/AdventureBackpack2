package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.inventory.widget.WidgetInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Test;

/** The item and the tile ask the widget map, not fixed indices, for limits, drops on closing and consumption. */
class InventoryMapTest
{
    @Test
    void theClassicIndicesKeepTheirOldRules()
    {
        WidgetLayout l = WidgetLayouts.widgets("Standard", 0);
        int rope = BackpackLayout.ropeIndex(39, 0);
        assertEquals(128, WidgetInventory.limit(l, rope, 64));
        assertEquals(64, WidgetInventory.limit(l, 0, 64));
        for (int i = 0; i < l.inventorySize(); i++)
        {
            boolean bucket = i >= BackpackLayout.BUCKET_IN_LEFT && i <= BackpackLayout.BUCKET_OUT_RIGHT;
            boolean special = i >= BackpackLayout.UPPER_TOOL && i <= BackpackLayout.BUCKET_OUT_RIGHT;
            assertEquals(bucket, WidgetInventory.dropsOnClose(l, i), "drops " + i);
            assertEquals(!special, WidgetInventory.consumable(l, i), "consumable " + i);
        }
        assertTrue(WidgetInventory.accepts(l, 0, new ItemStack(new Item())));
        assertTrue(WidgetInventory.accepts(l, BackpackLayout.BUCKET_OUT_LEFT, new ItemStack(new Item())), "output buckets take anything, as before");
    }

    @Test
    void theSkeletonHasNoRightTankSlots()
    {
        WidgetLayout l = WidgetLayouts.widgets("Skeleton", 0);
        assertFalse(WidgetInventory.accepts(l, BackpackLayout.BUCKET_IN_RIGHT, new ItemStack(new Item())));
        assertFalse(WidgetInventory.dropsOnClose(l, BackpackLayout.BUCKET_IN_RIGHT));
        assertEquals(128, WidgetInventory.limit(l, BackpackLayout.ropeIndex(39, 4), 64));
    }
}
