package com.darkona.adventurebackpack.common;

/**
 * Geometry of the two-block sleeping bag and who owns it, as pure functions of the block metadata.
 * <p>
 * Metadata is the vanilla bed's: bits 0-1 the direction, bit 2 occupied, bit 3 the head. A bag deployed from a
 * placed backpack lies with its foot next to the backpack, one step behind the foot; that bag folds back into the
 * backpack and never drops anything. A bag placed on its own (the Sleeping Bag item) drops the item from its foot.
 */
public final class SleepingBagRules
{
    /** Foot-to-head step {dx, dz} for each direction, as in BlockBed. */
    private static final int[][] FOOT_TO_HEAD = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}};

    private SleepingBagRules() {}

    public static int direction(int meta)
    {
        return meta & 3;
    }

    public static boolean isHead(int meta)
    {
        return (meta & 8) != 0;
    }

    public static int headDx(int meta)
    {
        return FOOT_TO_HEAD[direction(meta)][0];
    }

    public static int headDz(int meta)
    {
        return FOOT_TO_HEAD[direction(meta)][1];
    }

    /** X of the foot block, given either half. */
    public static int footX(int x, int meta)
    {
        return isHead(meta) ? x - headDx(meta) : x;
    }

    /** Z of the foot block, given either half. */
    public static int footZ(int z, int meta)
    {
        return isHead(meta) ? z - headDz(meta) : z;
    }

    /** X where the backpack that deployed this bag stands: one step behind the foot. */
    public static int backpackX(int footX, int meta)
    {
        return footX - headDx(meta);
    }

    /** Z where the backpack that deployed this bag stands: one step behind the foot. */
    public static int backpackZ(int footZ, int meta)
    {
        return footZ - headDz(meta);
    }

    /** Direction of a bag placed by a player looking along {@code yaw}: the head points away from the player. */
    public static int directionFromYaw(float yaw)
    {
        return (int) Math.floor(yaw * 4.0F / 360.0F + 0.5D) & 3;
    }

    /** Only the foot of a bag of its own drops the item; a backpack's bag folds back into the backpack. */
    public static boolean dropsItem(int meta, boolean ownedByBackpack)
    {
        return !isHead(meta) && !ownedByBackpack;
    }
}
