package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UtilsTest {
    @Test
    void easterDates() {
        assertArrayEquals(new int[]{3, 31}, Utils.calculateEaster(2024));
        assertArrayEquals(new int[]{4, 20}, Utils.calculateEaster(2025));
        assertArrayEquals(new int[]{4, 5}, Utils.calculateEaster(2026));
        assertArrayEquals(new int[]{3, 23}, Utils.calculateEaster(2008));
    }

    @Test
    void oppositeCardinal() {
        assertEquals(2, Utils.getOppositeCardinalFromMeta(0));
        assertEquals(3, Utils.getOppositeCardinalFromMeta(1));
        assertEquals(0, Utils.getOppositeCardinalFromMeta(2));
        assertEquals(1, Utils.getOppositeCardinalFromMeta(3));
    }
}
