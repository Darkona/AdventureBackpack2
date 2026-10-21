package com.darkona.adventurebackpack.common.layout;

public final class StorageIndex
{
    public static final int LEGACY_STORAGE = 39;
    public static final int RESERVED_SLOTS = 6;
    public static final int FIRST_EXTRA_STORAGE = LEGACY_STORAGE + RESERVED_SLOTS;

    private StorageIndex()
    {
    }

    public static int inventoryIndex(int k)
    {
        return k < LEGACY_STORAGE ? k : k + RESERVED_SLOTS;
    }

    public static int inventorySize(int storageSlots)
    {
        return Math.max(FIRST_EXTRA_STORAGE, storageSlots + RESERVED_SLOTS);
    }

    public static boolean isStorageIndex(int index, int storageSlots)
    {
        if (index < 0) return false;
        if (index < LEGACY_STORAGE) return index < storageSlots;
        return index >= FIRST_EXTRA_STORAGE && index - RESERVED_SLOTS < storageSlots;
    }

    public static int storageOfInventory(int inventoryLength)
    {
        return Math.max(LEGACY_STORAGE, inventoryLength - RESERVED_SLOTS);
    }

    public static int storageOfSavedIndex(int index)
    {
        return index >= FIRST_EXTRA_STORAGE ? index - RESERVED_SLOTS + 1 : 0;
    }
}
