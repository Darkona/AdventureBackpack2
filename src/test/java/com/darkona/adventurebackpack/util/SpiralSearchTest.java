package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

class SpiralSearchTest {
    @Test
    void packRoundTripsNegativeCoordinates() {
        long p = SpiralSearch.pack(-123456, -7);
        assertEquals(-123456, SpiralSearch.unpackX(p));
        assertEquals(-7, SpiralSearch.unpackZ(p));
    }

    @Test
    void startsAtTheOrigin() {
        long p = SpiralSearch.find(10, -4, 12, (x, z) -> true);
        assertEquals(10, SpiralSearch.unpackX(p));
        assertEquals(-4, SpiralSearch.unpackZ(p));
    }

    @Test
    void visitsEveryCellAroundTheOrigin() {
        Set<Long> seen = new HashSet<>();
        SpiralSearch.find(0, 0, 12, (x, z) -> { seen.add(SpiralSearch.pack(x, z)); return false; });
        for (int x = -3; x <= 3; x++)
            for (int z = -3; z <= 3; z++)
                assertTrue(seen.contains(SpiralSearch.pack(x, z)), "missed " + x + "," + z);
    }

    @Test
    void findsTheOnlyFreeCell() {
        long p = SpiralSearch.find(0, 0, 12, (x, z) -> x == 2 && z == -3);
        assertEquals(2, SpiralSearch.unpackX(p));
        assertEquals(-3, SpiralSearch.unpackZ(p));
    }

    @Test
    void noneWhenNothingFits() {
        assertEquals(SpiralSearch.NONE, SpiralSearch.find(0, 0, 12, (x, z) -> false));
    }
}
