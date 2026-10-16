package com.darkona.adventurebackpack.inventory;

public final class BackpackLayout
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

    public static final int LEGACY_STORAGE = 39;
    public static final int UPPER_TOOL = 39;
    public static final int LOWER_TOOL = 40;
    public static final int BUCKET_IN_LEFT = 41;
    public static final int BUCKET_OUT_LEFT = 42;
    public static final int BUCKET_IN_RIGHT = 43;
    public static final int BUCKET_OUT_RIGHT = 44;
    public static final int SPECIAL_SLOTS = 6;
    public static final int FIRST_EXTRA_STORAGE = LEGACY_STORAGE + SPECIAL_SLOTS;

    public static final int HEADER = 7;
    private static final int BUCKET_LEFT_X = 6;
    private static final int TANK_LEFT_X = 25;
    private static final int TOOL_X = 44;
    private static final int GRID_X = 62;
    private static final int TANK_WIDTH = 16;
    private static final int PLAYER_WIDTH = 9 * SLOT;

    private final int rows;
    private final int columns;
    private final int storage;
    private final int quiver;
    private final int[] storageX;
    private final int[] storageY;

    private BackpackLayout(int rows, int columns, int storage, int quiver)
    {
        this.rows = rows;
        this.columns = columns;
        this.storage = storage;
        this.quiver = quiver;
        storageX = new int[storage];
        storageY = new int[storage];
        int k = 0;
        for (int r = 0; r < rows && k < storage; r++)
        {
            for (int c = 0; c < columns && k < storage; c++)
            {
                if (r >= rows - 3 && c >= columns - 3) continue;
                storageX[k] = GRID_X + SLOT * c;
                storageY[k] = HEADER + SLOT * r;
                k++;
            }
        }
    }

    public static BackpackLayout of(int rows, int columns)
    {
        int c = clamp(columns, MIN_COLUMNS, MAX_COLUMNS);
        int r = clamp(rows, MIN_ROWS, MAX_ROWS);
        return new BackpackLayout(r, c, r * c - CRAFTING_CELLS, 0);
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
        int q = quiverFor(rows, quiver);
        int c = clamp(columns, MIN_COLUMNS, MAX_COLUMNS);
        int r = clamp(rows, MIN_ROWS, MAX_ROWS);
        int s = storageFor(r, c, storage);
        while (r * c - CRAFTING_CELLS < s)
        {
            if (r < MAX_ROWS) r++;
            else c++;
        }
        return new BackpackLayout(r, c, s, q);
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
        return k < LEGACY_STORAGE ? k : k + SPECIAL_SLOTS;
    }

    public static int inventorySize(int storageSlots)
    {
        return Math.max(FIRST_EXTRA_STORAGE, storageSlots + SPECIAL_SLOTS);
    }

    public static boolean isStorageIndex(int index, int storageSlots)
    {
        if (index < 0) return false;
        if (index < LEGACY_STORAGE) return index < storageSlots;
        return index >= FIRST_EXTRA_STORAGE && index - SPECIAL_SLOTS < storageSlots;
    }

    public static int quiverIndex(int storageSlots, int q)
    {
        return inventorySize(storageSlots) + q;
    }

    public static int inventorySize(int storageSlots, int quiverSlots)
    {
        return inventorySize(storageSlots) + quiverSlots;
    }

    public static int storageOfInventory(int inventoryLength)
    {
        return Math.max(LEGACY_STORAGE, inventoryLength - SPECIAL_SLOTS);
    }

    public static int bucketIndex(int i)
    {
        return BUCKET_IN_LEFT + i;
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
        return inventorySize(storage, quiver);
    }

    public int quiver()
    {
        return quiver;
    }

    public int tanks()
    {
        return quiver > 0 ? 1 : 2;
    }

    public int bucketSlots()
    {
        return 2 * tanks();
    }

    public int toolStart()
    {
        return storage;
    }

    public int bucketStart()
    {
        return storage + 2;
    }

    public int quiverStart()
    {
        return bucketStart() + bucketSlots();
    }

    public int craftStart()
    {
        return quiverStart() + quiver;
    }

    public int resultSlot()
    {
        return craftStart() + 9;
    }

    public int playerStart()
    {
        return craftStart() + 10;
    }

    public int hotbarStart()
    {
        return playerStart() + 27;
    }

    public int slotCount()
    {
        return hotbarStart() + 9;
    }

    public int storageX(int k)
    {
        return storageX[k];
    }

    public int storageY(int k)
    {
        return storageY[k];
    }

    public int craftTop()
    {
        return HEADER + SLOT * (rows - 3);
    }

    public int craftLeft()
    {
        return GRID_X + SLOT * (columns - 3);
    }

    public int craftX(int i)
    {
        return craftLeft() + SLOT * (i % 3);
    }

    public int craftY(int i)
    {
        return craftTop() + SLOT * (i / 3);
    }

    public int toolX()
    {
        return TOOL_X;
    }

    public int toolY(int i)
    {
        return craftTop() + SLOT * (i + 1);
    }

    public int tankX(int tank)
    {
        return tank == 0 ? TANK_LEFT_X : GRID_X + SLOT * columns + 1;
    }

    public int tankY()
    {
        return HEADER;
    }

    public int tankWidth()
    {
        return TANK_WIDTH;
    }

    public int tankHeight()
    {
        return SLOT * rows - 8;
    }

    public int bucketX(int i)
    {
        return i < 2 ? BUCKET_LEFT_X : tankX(1) + 19;
    }

    public int bucketY(int i)
    {
        return (i & 1) == 0 ? HEADER : HEADER + 30;
    }

    public int quiverX(int q)
    {
        return q < rows - 1 ? tankX(1) : bucketX(2);
    }

    public int quiverY(int q)
    {
        return HEADER + SLOT * (q % (rows - 1));
    }

    public int resultX()
    {
        return bucketX(2);
    }

    public int resultY()
    {
        return craftTop() + 2 * SLOT;
    }

    public int playerX()
    {
        return GRID_X + SLOT * columns - PLAYER_WIDTH;
    }

    public int playerY()
    {
        return HEADER + SLOT * rows + 10;
    }

    public int hotbarY()
    {
        return playerY() + 58;
    }

    public int buttonX()
    {
        return 5;
    }

    public int buttonY()
    {
        return HEADER + SLOT * rows - 24;
    }

    public int toggleX()
    {
        return TOOL_X - 1;
    }

    public int toggleY()
    {
        return craftTop() + 2;
    }

    public int width()
    {
        return bucketX(2) + 22;
    }

    public int height()
    {
        return hotbarY() + 24;
    }

    public int upperHeight()
    {
        return HEADER + SLOT * rows + 6;
    }

    public int lowerX()
    {
        return playerX() - 8;
    }

    public int lowerWidth()
    {
        return PLAYER_WIDTH + 14;
    }
}
