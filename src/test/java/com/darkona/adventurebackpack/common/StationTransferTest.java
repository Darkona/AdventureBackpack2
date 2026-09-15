package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StationTransferTest {
    @Test
    void emptyTargetTakesEverythingThatFits() {
        assertEquals(6000, StationTransfer.amount(16000, 0, 6000, true, true));
        assertEquals(1000, StationTransfer.amount(1000, 0, 6000, true, true));
    }

    @Test
    void partlyFullTargetTakesOnlyTheFreeRoom() {
        assertEquals(2500, StationTransfer.amount(16000, 3500, 6000, true, true));
        assertEquals(700, StationTransfer.amount(700, 3500, 6000, true, true));
    }

    @Test
    void fullTargetTakesNothing() {
        assertEquals(0, StationTransfer.amount(16000, 6000, 6000, true, true));
    }

    @Test
    void targetAboveALoweredCapacityTakesNothingAndNeverGoesNegative() {
        assertEquals(0, StationTransfer.amount(16000, 9000, 6000, true, true));
    }

    @Test
    void anotherFluidOrARefusedFluidMovesNothing() {
        assertEquals(0, StationTransfer.amount(16000, 100, 6000, true, false));
        assertEquals(0, StationTransfer.amount(16000, 0, 6000, false, true));
    }

    @Test
    void emptySourceMovesNothing() {
        assertEquals(0, StationTransfer.amount(0, 0, 6000, true, true));
        assertEquals(0, StationTransfer.amount(-5, 0, 6000, true, true));
    }

    @Test
    void neverMoreThanTheCapacityNorThanTheSource() {
        for (int available = 0; available <= 20000; available += 1250) {
            for (int stored = 0; stored <= 7000; stored += 500) {
                int moved = StationTransfer.amount(available, stored, 6000, true, true);
                assertTrue(moved >= 0);
                assertTrue(moved <= available);
                assertTrue(stored + moved <= Math.max(6000, stored));
            }
        }
    }

    @Test
    void sameOrEmptyMatchesByFluidName() {
        assertTrue(StationTransfer.sameOrEmpty(null, "oil"));
        assertTrue(StationTransfer.sameOrEmpty("oil", "oil"));
        assertFalse(StationTransfer.sameOrEmpty("fuel", "oil"));
    }
}
