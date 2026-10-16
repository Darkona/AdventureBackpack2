package com.darkona.adventurebackpack.common;

public final class StationTransfer
{
    private StationTransfer() {}

    public static int amount(int available, int stored, int capacity, boolean accepted, boolean sameOrEmpty)
    {
        if (!accepted || !sameOrEmpty || available <= 0) return 0;
        int room = capacity - Math.max(0, stored);
        if (room <= 0) return 0;
        return Math.min(available, room);
    }

    public static boolean sameOrEmpty(String stored, String incoming)
    {
        return stored == null || stored.equals(incoming);
    }
}
