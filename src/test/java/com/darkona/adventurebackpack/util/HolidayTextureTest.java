package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HolidayTextureTest {
    @Test
    void missingHolidayTextureFallsBackToStandard() {
        assertEquals("Standard", HolidayTexture.resolve("Fatherland", h -> false));
    }

    @Test
    void existingHolidayTextureIsKept() {
        assertEquals("Valentines", HolidayTexture.resolve("Valentines", h -> true));
    }

    @Test
    void nullOrEmptyOrStandardIsStandardWithoutLookup() {
        assertEquals("Standard", HolidayTexture.resolve(null, h -> { throw new AssertionError(); }));
        assertEquals("Standard", HolidayTexture.resolve("", h -> { throw new AssertionError(); }));
        assertEquals("Standard", HolidayTexture.resolve("Standard", h -> { throw new AssertionError(); }));
    }
}
