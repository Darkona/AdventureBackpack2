package com.darkona.adventurebackpack.common.layout.classic;

import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.*;

import com.darkona.adventurebackpack.common.layout.BackpackSpec;
import com.darkona.adventurebackpack.common.layout.StorageIndex;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;

public final class ClassicArrangement
{
    public static final int SLOT = 18;

    public static final int CLASSIC_ROWS = 6;
    public static final int CLASSIC_COLUMNS = 8;
    public static final int MIN_ROWS = 4;
    public static final int MAX_ROWS = 10;
    public static final int MIN_COLUMNS = 8;
    public static final int MAX_COLUMNS = 12;

    public static final int CRAFTING_CELLS = 9;
    public static final int MAX_STORAGE = MAX_ROWS * MAX_COLUMNS - CRAFTING_CELLS;

    public static final int LEGACY_STORAGE = StorageIndex.LEGACY_STORAGE;
    public static final int UPPER_TOOL = 39;
    public static final int LOWER_TOOL = 40;
    public static final int BUCKET_IN_LEFT = 41;
    public static final int BUCKET_OUT_LEFT = 42;
    public static final int BUCKET_IN_RIGHT = 43;
    public static final int BUCKET_OUT_RIGHT = 44;
    public static final int SPECIAL_SLOTS = StorageIndex.RESERVED_SLOTS;
    public static final int ROPE_SLOTS = 1;
    public static final int FIRST_EXTRA_STORAGE = LEGACY_STORAGE + SPECIAL_SLOTS;

    public static final int HEADER = 7;
    public static final int BUCKET_LEFT_X = 6;
    public static final int TANK_LEFT_X = 25;
    public static final int TOOL_X = 44;
    public static final int GRID_X = 62;
    public static final int TANK_WIDTH = 16;
    public static final int PLAYER_WIDTH = 9 * SLOT;
    public static final int BUTTON_X = 5;
    public static final int BUTTON_SIZE = 18;
    public static final int TOGGLE_SIZE = 18;
    public static final int ACTION_X = 6;
    public static final int ACTION_SIZE = 16;
    public static final int ACTION_GAP = 2;
    public static final int ACTION_TOP = 4;
    public static final int LOWER_PANEL_MARGIN = 8;
    public static final int LOWER_PANEL_WIDTH = PLAYER_WIDTH + 14;
    public static final int DOCK_COLUMN = 20;

    private ClassicArrangement()
    {
    }

    public static WidgetLayout of(int rows, int columns)
    {
        return of(rows, columns, 0);
    }

    public static WidgetLayout of(int rows, int columns, int quiver)
    {
        int c = clamp(columns, MIN_COLUMNS, MAX_COLUMNS);
        int r = clamp(rows, MIN_ROWS, MAX_ROWS);
        return arrange(r, c, r * c - CRAFTING_CELLS, quiverFor(rows, quiver));
    }

    public static WidgetLayout forStorage(int rows, int columns, int storage, int quiver)
    {
        int grown = grow(rows, columns, storage);
        return arrange(grown >> 8, grown & 0xFF, storageFor(rows, columns, storage), quiverFor(rows, quiver));
    }

    public static WidgetLayout forStorage(BackpackSpec spec, int storage)
    {
        int grown = grow(spec.rows(), spec.columns(), storage);
        return arrange(spec.resized(grown >> 8, grown & 0xFF), storageFor(spec.rows(), spec.columns(), storage));
    }

    public static int grow(int rows, int columns, int storage)
    {
        int c = clamp(columns, MIN_COLUMNS, MAX_COLUMNS);
        int r = clamp(rows, MIN_ROWS, MAX_ROWS);
        int s = storageFor(r, c, storage);
        while (r * c - CRAFTING_CELLS < s)
        {
            if (r < MAX_ROWS) r++;
            else c++;
        }
        return r << 8 | c;
    }

    public static int maxQuiver(int rows)
    {
        return 2 * (clamp(rows, MIN_ROWS, MAX_ROWS) - 1);
    }

    public static int quiverFor(int rows, int asked)
    {
        return clamp(asked, 0, maxQuiver(rows));
    }

    public static int storageFor(int rows, int columns, int saved)
    {
        int grid = clamp(rows, MIN_ROWS, MAX_ROWS) * clamp(columns, MIN_COLUMNS, MAX_COLUMNS) - CRAFTING_CELLS;
        return Math.min(Math.max(saved, grid), MAX_STORAGE);
    }

    public static int clamp(int v, int min, int max)
    {
        return v < min ? min : v > max ? max : v;
    }

    public static int inventoryIndex(int k)
    {
        return StorageIndex.inventoryIndex(k);
    }

    public static int inventorySize(int storageSlots)
    {
        return StorageIndex.inventorySize(storageSlots);
    }

    public static boolean isStorageIndex(int index, int storageSlots)
    {
        return StorageIndex.isStorageIndex(index, storageSlots);
    }

    public static int quiverIndex(int storageSlots, int q)
    {
        return inventorySize(storageSlots) + q;
    }

    public static int inventorySize(int storageSlots, int quiverSlots)
    {
        return inventorySize(storageSlots) + quiverSlots + ROPE_SLOTS;
    }

    public static int ropeIndex(int storageSlots, int quiverSlots)
    {
        return inventorySize(storageSlots) + quiverSlots;
    }

    public static int storageOfInventory(int inventoryLength)
    {
        return StorageIndex.storageOfInventory(inventoryLength);
    }

    public static int bucketIndex(int i)
    {
        return BUCKET_IN_LEFT + i;
    }

    public static WidgetLayout arrange(int rows, int columns, int storage, int quiver)
    {
        return arrange(ClassicComposition.of(rows, columns, quiver), storage);
    }

    public static WidgetLayout arrange(BackpackSpec spec, int storage)
    {
        int rows = spec.rows();
        int columns = spec.columns();
        int craftTop = HEADER + SLOT * (rows - 3);
        int craftLeft = GRID_X + SLOT * (columns - 3);
        int rightX = GRID_X + SLOT * columns + 1;
        int rightBucketX = rightX + 19;
        int tankHeight = SLOT * rows - 8;
        int playerX = GRID_X + SLOT * columns - PLAYER_WIDTH;
        int playerY = HEADER + SLOT * rows + 10;
        int hotbarY = playerY + 58;
        int next = inventorySize(storage);
        int dockY = craftTop - SLOT;
        int dockCells = rows - 3;
        int overflow = Math.max(0, dockSlots(spec) - dockCells);
        int extraColumns = (overflow + rows - 1) / rows;
        int shift = DOCK_COLUMN * extraColumns;
        int placed = 0;
        WidgetLayout.Builder b = new WidgetLayout.Builder()
                .window(rightBucketX + 22, hotbarY + 24, HEADER + SLOT * rows + 6, playerX - LOWER_PANEL_MARGIN, LOWER_PANEL_WIDTH)
                .rowSize(columns);

        for (int i = 0; i < spec.widgets(); i++)
        {
            int kind = spec.kind(i);
            int param = spec.param(i);
            switch (kind)
            {
                case STORAGE:
                    b.widget(STORAGE, columns, true);
                    int k = 0;
                    for (int r = 0; r < rows && k < storage; r++)
                    {
                        for (int c = 0; c < columns && k < storage; c++)
                        {
                            if (r >= rows - 3 && c >= columns - 3) continue;
                            b.slot(GRID_X + SLOT * c, HEADER + SLOT * r, inventoryIndex(k));
                            k++;
                        }
                    }
                    break;
                case TOOLS:
                    b.widget(TOOLS, 0, TOOL_X, craftTop + SLOT, 16, SLOT + 16, true);
                    b.slot(TOOL_X, craftTop + SLOT, UPPER_TOOL);
                    b.slot(TOOL_X, craftTop + 2 * SLOT, LOWER_TOOL);
                    break;
                case TANK:
                    int bucketX = param == 0 ? BUCKET_LEFT_X : rightBucketX;
                    b.widget(TANK, param, param == 0 ? TANK_LEFT_X : rightX, HEADER, TANK_WIDTH, tankHeight, true);
                    b.slot(bucketX, HEADER, bucketIndex(2 * param));
                    b.slot(bucketX, HEADER + 30, bucketIndex(2 * param + 1));
                    break;
                case QUIVER:
                    if (param <= 0) break;
                    b.widget(QUIVER, param, true);
                    for (int q = 0; q < param; q++)
                    {
                        b.slot(q < rows - 1 ? rightX : rightBucketX, HEADER + SLOT * (q % (rows - 1)), next++);
                    }
                    break;
                case CRAFT:
                    b.widget(CRAFT, 0, craftLeft, craftTop, 3 * SLOT - 2, 3 * SLOT - 2, false);
                    for (int c = 0; c < CRAFTING_CELLS; c++) b.slot(craftLeft + SLOT * (c % 3), craftTop + SLOT * (c / 3), c);
                    break;
                case RESULT:
                    b.widget(RESULT, 0, false);
                    b.slot(rightBucketX, craftTop + 2 * SLOT, 0);
                    break;
                case PLAYER:
                    b.widget(PLAYER, 0, false);
                    for (int c = 0; c < 27; c++) b.slot(playerX + SLOT * (c % 9), playerY + SLOT * (c / 9), 9 + c);
                    break;
                case HOTBAR:
                    b.widget(HOTBAR, 0, false);
                    for (int c = 0; c < 9; c++) b.slot(playerX + SLOT * c, hotbarY, c);
                    break;
                case BUTTON:
                    b.widget(BUTTON, 0, BUTTON_X, HEADER + SLOT * rows - 24, BUTTON_SIZE, BUTTON_SIZE, false);
                    break;
                case TOGGLE:
                    b.widget(TOGGLE, 0, TOOL_X - 1, craftTop + 2, TOGGLE_SIZE, TOGGLE_SIZE, false);
                    break;
                case SORT:
                    b.widget(SORT, 0, ACTION_X, HEADER + SLOT * rows + 6 + ACTION_TOP, ACTION_SIZE, ACTION_SIZE, false);
                    break;
                case LOCK:
                    b.widget(LOCK, 0, ACTION_X, HEADER + SLOT * rows + 6 + ACTION_TOP + ACTION_SIZE + ACTION_GAP, ACTION_SIZE, ACTION_SIZE, false);
                    break;
                default:
                    int slots = kind == ROPE ? ROPE_SLOTS : param;
                    b.widget(kind, param, true);
                    for (int n = 0; n < slots; n++, placed++)
                    {
                        if (placed < dockCells)
                        {
                            b.slot(TOOL_X, dockY, next++);
                            dockY -= SLOT;
                        } else
                        {
                            int extra = placed - dockCells;
                            b.slot(BUCKET_LEFT_X + DOCK_COLUMN * (extra / rows) - shift, HEADER + SLOT * (extra % rows), next++);
                        }
                    }
                    break;
            }
        }
        return b.inventorySize(next).shiftAll(shift).build();
    }

    private static int dockSlots(BackpackSpec spec)
    {
        int n = 0;
        for (int i = 0; i < spec.widgets(); i++)
        {
            int kind = spec.kind(i);
            if (kind == ROPE) n += ROPE_SLOTS;
            else if (kind >= FIRST_FREE) n += spec.param(i);
        }
        return n;
    }
}
