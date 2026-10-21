package com.darkona.adventurebackpack.common;

public final class RopeRules
{
    public static final int MAX_LENGTH = 48;
    public static final int PER_USE = 1;
    public static final int HANGING = 4;
    public static final int NONE = -1;
    public static final int SLOT_LIMIT = 128;

    private static final int[] WALL_DX = {0, 0, 1, -1};
    private static final int[] WALL_DZ = {1, -1, 0, 0};

    private RopeRules() {}

    public static int attachMeta(int side)
    {
        if (side == 0) return HANGING;
        if (side >= 2 && side <= 5) return side - 2;
        return NONE;
    }

    public static int startDx(int side)
    {
        return side == 4 ? -1 : side == 5 ? 1 : 0;
    }

    public static int startDy(int side)
    {
        return side == 0 ? -1 : 0;
    }

    public static int startDz(int side)
    {
        return side == 2 ? -1 : side == 3 ? 1 : 0;
    }

    public static int wallDx(int meta)
    {
        return meta >= 0 && meta < HANGING ? WALL_DX[meta] : 0;
    }

    public static int wallDz(int meta)
    {
        return meta >= 0 && meta < HANGING ? WALL_DZ[meta] : 0;
    }

    public static boolean hasWall(int meta)
    {
        return meta >= 0 && meta < HANGING;
    }

    public static boolean supported(int meta, boolean ropeAbove, boolean solidAbove, boolean solidWall)
    {
        if (ropeAbove) return true;
        return hasWall(meta) ? solidWall : solidAbove;
    }

    public static int segmentsToPlace(int existingLength, int freeBelow, int available, boolean creative)
    {
        int room = Math.min(freeBelow, MAX_LENGTH - Math.max(0, existingLength));
        int n = Math.min(PER_USE, room);
        if (!creative) n = Math.min(n, available);
        return Math.max(0, n);
    }

    public static int toSlot(boolean hasSlot, int slotCount, int returned)
    {
        return hasSlot ? slotRoom(slotCount, returned) : 0;
    }

    public static int slotRoom(int slotCount, int returned)
    {
        return Math.max(0, Math.min(returned, SLOT_LIMIT - Math.max(0, slotCount)));
    }

    public static int consumed(int placed, boolean creative)
    {
        return creative ? 0 : Math.max(0, placed);
    }
}
