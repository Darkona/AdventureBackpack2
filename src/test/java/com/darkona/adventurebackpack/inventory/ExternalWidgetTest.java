package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import adventurebackpack.api.BackpackShape;
import adventurebackpack.api.BackpackWidget;
import adventurebackpack.api.IBackpackAbility;
import com.darkona.adventurebackpack.client.gui.widget.BackgroundComposer;
import com.darkona.adventurebackpack.client.gui.widget.classic.ClassicSkins;
import com.darkona.adventurebackpack.client.gui.widget.WidgetSkins;
import com.darkona.adventurebackpack.common.AbilityRegistry;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;
import com.darkona.adventurebackpack.common.layout.classic.ClassicSlots;
import com.darkona.adventurebackpack.inventory.widget.ExternalWidgets;
import com.darkona.adventurebackpack.inventory.widget.SlotWidget;
import com.darkona.adventurebackpack.inventory.widget.WidgetStore;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;

class ExternalWidgetTest
{
    private static final String COLOR = "ExternalWidgetProbe";
    private static final String ID = "test:pouch";
    private static final Item SEED = new Item();
    private static int kind;

    @BeforeAll
    static void register()
    {
        kind = ExternalWidgets.register(new BackpackWidget()
        {
            public String getId()
            {
                return ID;
            }

            public int getSlots()
            {
                return 3;
            }

            public boolean accepts(ItemStack stack)
            {
                return stack.getItem() == SEED;
            }

            public int getStackLimit()
            {
                return 16;
            }

            public int getShiftPriority()
            {
                return 5;
            }
        });
        AbilityRegistry.INSTANCE.register(COLOR, new IBackpackAbility()
        {
            @Override
            public BackpackShape getShape(BackpackShape configured)
            {
                return configured.withWidget(ID);
            }
        });
        WidgetLayouts.invalidate();
    }

    @AfterAll
    static void forget()
    {
        ExternalWidgets.forget(ID);
        WidgetLayouts.invalidate();
    }

    @Test
    void theWidgetIsPlacedInTheAccessoryDockAndOverflowsIntoANewColumn()
    {
        WidgetLayout l = WidgetLayouts.widgets(COLOR, 0);
        int w = l.first(kind);
        assertEquals(3, l.slots(w));
        assertEquals(248 + ClassicArrangement.DOCK_COLUMN, l.width(), "rope and two pouch slots fill the dock, the third opens a column");
        int rope = ClassicSlots.ropeIndex(l);
        for (int n = 0; n < 3; n++) assertEquals(rope + 1 + n, l.invIndex(w, n));
        assertEquals(rope + 4, l.inventorySize());
        assertEquals(ClassicArrangement.TOOL_X + ClassicArrangement.DOCK_COLUMN, l.slotX(l.slotFirst(w)));
        assertEquals(ClassicArrangement.BUCKET_LEFT_X, l.slotX(l.slotFirst(w) + 2));
        assertEquals(ClassicArrangement.GRID_X + ClassicArrangement.DOCK_COLUMN, l.slotX(0));
    }

    @Test
    void theContainerMakesFilteredSlotsAndShiftClickFindsThem() throws Exception
    {
        ContainerBackpack c = ContainerBackpackSlotsTest.build(COLOR);
        int w = c.widgets.first(kind);
        Slot first = c.getSlot(c.widgets.slotFirst(w));
        assertSame(SlotWidget.class, first.getClass());
        assertTrue(first.isItemValid(new ItemStack(SEED)));
        assertFalse(first.isItemValid(new ItemStack(new Item())));
        assertEquals(16, first.getSlotStackLimit());
        assertEquals(w, c.firstShiftWidget(new ItemStack(SEED)));
    }

    @Test
    void itsItemsLiveInTheirOwnKeyAndSurviveABiggerStorage()
    {
        WidgetLayout small = WidgetLayouts.widgets(COLOR, 39);
        ItemStack[] inv = new ItemStack[small.inventorySize()];
        inv[small.invIndex(small.first(kind), 1)] = new ItemStack(SEED, 5);
        NBTTagCompound data = new NBTTagCompound();
        WidgetStore.write(data, small, inv, null, 39);
        assertEquals(1, data.getTagList(ExternalWidgets.KEY_PREFIX + ID, 10).tagCount());
        assertEquals(1, data.getTagList(ExternalWidgets.KEY_PREFIX + ID, 10).getCompoundTagAt(0).getByte("Slot"));
        assertEquals(0, data.getTagList(BackpackNbt.ITEMS, 10).tagCount());
        assertTrue(Arrays.asList(WidgetStore.readKeys(small)).contains(ExternalWidgets.KEY_PREFIX + ID));
        WidgetLayout big = WidgetLayouts.widgets(COLOR, 60);
        assertTrue(big.invIndex(big.first(kind), 0) > small.invIndex(small.first(kind), 0), "the pouch moves behind the bigger storage, its key does not");
    }

    @Test
    void withoutASkinItGetsTheNeutralFrame()
    {
        ClassicSkins.install();
        int neutral = WidgetSkins.texture(Resources.guiTextures("guiBackpackWidgets"), ClassicSkins.TEXTURE_SIZE);
        int[] bg = BackgroundComposer.compose(WidgetLayouts.widgets(COLOR, 0));
        int frames = 0;
        for (int i = 0; i < bg.length; i += BackgroundComposer.STRIDE)
        {
            if (bg[i + BackgroundComposer.TEX] == neutral) frames++;
        }
        assertEquals(4, frames);
    }

    @Test
    void anIdNeedsAModPrefixAndIsUnique()
    {
        assertThrows(IllegalArgumentException.class, () -> ExternalWidgets.register(widget("pouch")));
        assertThrows(IllegalStateException.class, () -> ExternalWidgets.register(widget(ID)));
    }

    private static BackpackWidget widget(final String id)
    {
        return new BackpackWidget()
        {
            public String getId()
            {
                return id;
            }

            public int getSlots()
            {
                return 1;
            }

            public boolean accepts(ItemStack stack)
            {
                return true;
            }
        };
    }
}
