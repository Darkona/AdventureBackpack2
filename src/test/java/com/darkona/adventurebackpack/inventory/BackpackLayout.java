package com.darkona.adventurebackpack.inventory;

import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.*;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;

public final class BackpackLayout
{
    public static final int SLOT = ClassicArrangement.SLOT;
    public static final int CLASSIC_ROWS = ClassicArrangement.CLASSIC_ROWS;
    public static final int CLASSIC_COLUMNS = ClassicArrangement.CLASSIC_COLUMNS;
    public static final int MIN_ROWS = ClassicArrangement.MIN_ROWS;
    public static final int MAX_ROWS = ClassicArrangement.MAX_ROWS;
    public static final int MIN_COLUMNS = ClassicArrangement.MIN_COLUMNS;
    public static final int MAX_COLUMNS = ClassicArrangement.MAX_COLUMNS;
    public static final int CRAFTING_CELLS = ClassicArrangement.CRAFTING_CELLS;
    public static final int MAX_STORAGE = ClassicArrangement.MAX_STORAGE;
    public static final int LEGACY_STORAGE = ClassicArrangement.LEGACY_STORAGE;
    public static final int UPPER_TOOL = ClassicArrangement.UPPER_TOOL;
    public static final int LOWER_TOOL = ClassicArrangement.LOWER_TOOL;
    public static final int BUCKET_IN_LEFT = ClassicArrangement.BUCKET_IN_LEFT;
    public static final int BUCKET_OUT_LEFT = ClassicArrangement.BUCKET_OUT_LEFT;
    public static final int BUCKET_IN_RIGHT = ClassicArrangement.BUCKET_IN_RIGHT;
    public static final int BUCKET_OUT_RIGHT = ClassicArrangement.BUCKET_OUT_RIGHT;
    public static final int SPECIAL_SLOTS = ClassicArrangement.SPECIAL_SLOTS;
    public static final int ROPE_SLOTS = ClassicArrangement.ROPE_SLOTS;
    public static final int FIRST_EXTRA_STORAGE = ClassicArrangement.FIRST_EXTRA_STORAGE;
    public static final int HEADER = ClassicArrangement.HEADER;

    private final int rows;
    private final int columns;
    private final int storage;
    private final int quiver;
    private final WidgetLayout widgets;

    private BackpackLayout(int rows, int columns, int storage, int quiver)
    {
        this.rows = rows;
        this.columns = columns;
        this.storage = storage;
        this.quiver = quiver;
        widgets = ClassicArrangement.arrange(rows, columns, storage, quiver);
    }

    public static BackpackLayout view(ContainerBackpack container)
    {
        return view(container.widgets);
    }

    public static BackpackLayout view(WidgetLayout l)
    {
        int rows = (l.upperHeight() - HEADER - 6) / SLOT;
        return new BackpackLayout(rows, l.rowSize(), l.slotsOf(STORAGE), l.slotsOf(QUIVER));
    }

    public static BackpackLayout of(int rows, int columns)
    {
        return of(rows, columns, 0);
    }

    public static BackpackLayout of(int rows, int columns, int quiver)
    {
        int c = clamp(columns, MIN_COLUMNS, MAX_COLUMNS);
        int r = clamp(rows, MIN_ROWS, MAX_ROWS);
        return new BackpackLayout(r, c, r * c - CRAFTING_CELLS, quiverFor(rows, quiver));
    }

    public static BackpackLayout forStorage(int rows, int columns, int storage)
    {
        return forStorage(rows, columns, storage, 0);
    }

    public static BackpackLayout forStorage(int rows, int columns, int storage, int quiver)
    {
        int grown = ClassicArrangement.grow(rows, columns, storage);
        return new BackpackLayout(grown >> 8, grown & 0xFF, storageFor(rows, columns, storage), quiverFor(rows, quiver));
    }

    public static int maxQuiver(int rows)
    {
        return ClassicArrangement.maxQuiver(rows);
    }

    public static int quiverFor(int rows, int asked)
    {
        return ClassicArrangement.quiverFor(rows, asked);
    }

    public static int storageFor(int rows, int columns, int saved)
    {
        return ClassicArrangement.storageFor(rows, columns, saved);
    }

    public static int clamp(int v, int min, int max)
    {
        return ClassicArrangement.clamp(v, min, max);
    }

    public static int inventoryIndex(int k)
    {
        return ClassicArrangement.inventoryIndex(k);
    }

    public static int inventorySize(int storageSlots)
    {
        return ClassicArrangement.inventorySize(storageSlots);
    }

    public static boolean isStorageIndex(int index, int storageSlots)
    {
        return ClassicArrangement.isStorageIndex(index, storageSlots);
    }

    public static int quiverIndex(int storageSlots, int q)
    {
        return ClassicArrangement.quiverIndex(storageSlots, q);
    }

    public static int inventorySize(int storageSlots, int quiverSlots)
    {
        return ClassicArrangement.inventorySize(storageSlots, quiverSlots);
    }

    public static int ropeIndex(int storageSlots, int quiverSlots)
    {
        return ClassicArrangement.ropeIndex(storageSlots, quiverSlots);
    }

    public static int storageOfInventory(int inventoryLength)
    {
        return ClassicArrangement.storageOfInventory(inventoryLength);
    }

    public static int bucketIndex(int i)
    {
        return ClassicArrangement.bucketIndex(i);
    }

    public WidgetLayout widgets()
    {
        return widgets;
    }

    public int rows()
    {
        return rows;
    }

    public int columns()
    {
        return columns;
    }

    public int storage()
    {
        return storage;
    }

    public int inventorySize()
    {
        return widgets.inventorySize();
    }

    public int quiver()
    {
        return quiver;
    }

    public int tanks()
    {
        return widgets.countOf(TANK);
    }

    public int bucketSlots()
    {
        return widgets.slotsOf(TANK);
    }

    private int start(int kind)
    {
        return widgets.slotFirst(widgets.first(kind));
    }

    public int toolStart()
    {
        return start(TOOLS);
    }

    public int bucketStart()
    {
        return start(TANK);
    }

    public int quiverStart()
    {
        return bucketStart() + bucketSlots();
    }

    public int ropeStart()
    {
        return start(ROPE);
    }

    public int craftStart()
    {
        return start(CRAFT);
    }

    public int resultSlot()
    {
        return start(RESULT);
    }

    public int playerStart()
    {
        return start(PLAYER);
    }

    public int hotbarStart()
    {
        return start(HOTBAR);
    }

    public int slotCount()
    {
        return widgets.slotCount();
    }

    public int storageX(int k)
    {
        return widgets.slotX(k);
    }

    public int storageY(int k)
    {
        return widgets.slotY(k);
    }

    public int craftTop()
    {
        return widgets.y(widgets.first(CRAFT));
    }

    public int craftLeft()
    {
        return widgets.x(widgets.first(CRAFT));
    }

    public int craftX(int i)
    {
        return widgets.slotX(craftStart() + i);
    }

    public int craftY(int i)
    {
        return widgets.slotY(craftStart() + i);
    }

    public int toolX()
    {
        return widgets.slotX(toolStart());
    }

    public int toolY(int i)
    {
        return widgets.slotY(toolStart() + i);
    }

    public int tankX(int tank)
    {
        return tank == 0 ? ClassicArrangement.TANK_LEFT_X : ClassicArrangement.GRID_X + SLOT * columns + 1;
    }

    public int tankY()
    {
        return widgets.y(widgets.first(TANK));
    }

    public int tankWidth()
    {
        return widgets.w(widgets.first(TANK));
    }

    public int tankHeight()
    {
        return widgets.h(widgets.first(TANK));
    }

    public int bucketX(int i)
    {
        return i < bucketSlots() ? widgets.slotX(bucketStart() + i) : tankX(1) + 19;
    }

    public int bucketY(int i)
    {
        return (i & 1) == 0 ? HEADER : HEADER + 30;
    }

    public int quiverX(int q)
    {
        return widgets.slotX(quiverStart() + q);
    }

    public int quiverY(int q)
    {
        return widgets.slotY(quiverStart() + q);
    }

    public int ropeX()
    {
        return widgets.slotX(ropeStart());
    }

    public int ropeY()
    {
        return widgets.slotY(ropeStart());
    }

    public int resultX()
    {
        return widgets.slotX(resultSlot());
    }

    public int resultY()
    {
        return widgets.slotY(resultSlot());
    }

    public int playerX()
    {
        return widgets.slotX(playerStart());
    }

    public int playerY()
    {
        return widgets.slotY(playerStart());
    }

    public int hotbarY()
    {
        return widgets.slotY(hotbarStart());
    }

    public int buttonX()
    {
        return widgets.x(widgets.first(BUTTON));
    }

    public int buttonY()
    {
        return widgets.y(widgets.first(BUTTON));
    }

    public int toggleX()
    {
        return widgets.x(widgets.first(TOGGLE));
    }

    public int toggleY()
    {
        return widgets.y(widgets.first(TOGGLE));
    }

    public int width()
    {
        return widgets.width();
    }

    public int height()
    {
        return widgets.height();
    }

    public int upperHeight()
    {
        return widgets.upperHeight();
    }

    public int lowerX()
    {
        return widgets.lowerX();
    }

    public int lowerWidth()
    {
        return widgets.lowerWidth();
    }
}
