package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class SpiralSearchTest {
    private static List<int[]> visit(int radius, int vertical) {
        List<int[]> seen = new ArrayList<>();
        SpiralSearch.find(0, 0, 0, radius, vertical, (x, y, z) -> { seen.add(new int[] {x, y, z}); return false; });
        return seen;
    }

    private static int squared(int[] p) {
        return p[0] * p[0] + p[1] * p[1] + p[2] * p[2];
    }

    @Test
    void packRoundTripsNegativeCoordinates() {
        long p = SpiralSearch.pack(-123456, -7, 98765);
        assertEquals(-123456, SpiralSearch.unpackX(p));
        assertEquals(-7, SpiralSearch.unpackY(p));
        assertEquals(98765, SpiralSearch.unpackZ(p));
    }

    @Test
    void startsAtTheOrigin() {
        long p = SpiralSearch.find(10, 64, -4, 12, 2, (x, y, z) -> true);
        assertEquals(10, SpiralSearch.unpackX(p));
        assertEquals(64, SpiralSearch.unpackY(p));
        assertEquals(-4, SpiralSearch.unpackZ(p));
    }

    @Test
    void visitsEveryCellOfTheBallExactlyOnce() {
        int radius = 9, vertical = 3;
        List<int[]> seen = visit(radius, vertical);
        Set<Long> unique = new HashSet<>();
        for (int[] p : seen) unique.add(SpiralSearch.pack(p[0], p[1], p[2]));
        int expected = 0;
        for (int x = -radius; x <= radius; x++)
            for (int y = -vertical; y <= vertical; y++)
                for (int z = -radius; z <= radius; z++)
                    if (x * x + y * y + z * z <= radius * radius) expected++;
        assertEquals(expected, seen.size());
        assertEquals(expected, unique.size());
    }

    @Test
    void neverGetsFartherBeforeGettingNearer() {
        int last = 0;
        for (int[] p : visit(12, 4)) {
            assertTrue(squared(p) >= last);
            last = squared(p);
        }
    }

    @Test
    void staysWithinTheVerticalRange() {
        for (int[] p : visit(10, 2)) assertTrue(Math.abs(p[1]) <= 2);
    }

    @Test
    void coversTheSameCellsInEveryDirection() {
        Set<Long> seen = new HashSet<>();
        for (int[] p : visit(8, 2)) seen.add(SpiralSearch.pack(p[0], p[1], p[2]));
        for (int[] p : visit(8, 2)) {
            assertTrue(seen.contains(SpiralSearch.pack(-p[0], p[1], p[2])));
            assertTrue(seen.contains(SpiralSearch.pack(p[0], p[1], -p[2])));
            assertTrue(seen.contains(SpiralSearch.pack(p[2], p[1], p[0])));
        }
    }

    @Test
    void reachesTheFullRadiusAlongAnAxis() {
        assertEquals(SpiralSearch.pack(12, 0, 0), SpiralSearch.find(0, 0, 0, 12, 0, (x, y, z) -> x == 12));
        assertEquals(SpiralSearch.NONE, SpiralSearch.find(0, 0, 0, 12, 0, (x, y, z) -> x == 13));
    }

    @Test
    void takesTheNearerOfTwoCandidatesEvenWhenTheOtherIsOnAnEarlierSquareRing() {
        long p = SpiralSearch.find(0, 0, 0, 12, 0, (x, y, z) -> x == 6 && z == 0 || x == 5 && z == 5);
        assertEquals(6, SpiralSearch.unpackX(p));
        assertEquals(0, SpiralSearch.unpackZ(p));
    }

    @Test
    void aLevelNeverCountsForMoreThanItsDistance() {
        long p = SpiralSearch.find(0, 0, 0, 12, 2, (x, y, z) -> y == 1 && x == 0 && z == 0 || y == 0 && x == 0 && z == 3);
        assertEquals(1, SpiralSearch.unpackY(p));
    }

    @Test
    void findsTheOnlyFreeCell() {
        long p = SpiralSearch.find(0, 5, 0, 12, 3, (x, y, z) -> x == 2 && y == 7 && z == -3);
        assertEquals(2, SpiralSearch.unpackX(p));
        assertEquals(7, SpiralSearch.unpackY(p));
        assertEquals(-3, SpiralSearch.unpackZ(p));
    }

    @Test
    void noneWhenNothingFits() {
        assertEquals(SpiralSearch.NONE, SpiralSearch.find(0, 0, 0, 12, 2, (x, y, z) -> false));
    }

    @Test
    void rejectsAnExtentTheTableCannotHold() {
        assertThrows(IllegalArgumentException.class, () -> SpiralSearch.find(0, 0, 0, 200, 0, (x, y, z) -> true));
        assertThrows(IllegalArgumentException.class, () -> SpiralSearch.find(0, 0, 0, -1, 0, (x, y, z) -> true));
    }
}
