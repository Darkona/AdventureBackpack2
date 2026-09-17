package com.darkona.adventurebackpack.common;

/**
 * How much fluid a station (Fuel Station, later the Air Compressor) moves into a tank, as pure decisions: a pack in
 * its slot, a pack the player wears or holds, the tank of the boat the player rides, or a pipe filling the station.
 * One transfer moves everything that fits, at once.
 */
public final class StationTransfer
{
    private StationTransfer() {}

    /**
     * Millibuckets that move from a source holding {@code available} into a tank holding {@code stored} of
     * {@code capacity}. Nothing moves when the target does not accept the fluid or already holds another fluid.
     * Never negative and never more than the free room, also when {@code stored} is above the capacity (a capacity
     * lowered in the config).
     */
    public static int amount(int available, int stored, int capacity, boolean accepted, boolean sameOrEmpty)
    {
        if (!accepted || !sameOrEmpty || available <= 0) return 0;
        int room = capacity - Math.max(0, stored);
        if (room <= 0) return 0;
        return Math.min(available, room);
    }

    /** True when a tank holding {@code stored} (null when empty) can take {@code incoming}: empty, or the same fluid. */
    public static boolean sameOrEmpty(String stored, String incoming)
    {
        return stored == null || stored.equals(incoming);
    }
}
