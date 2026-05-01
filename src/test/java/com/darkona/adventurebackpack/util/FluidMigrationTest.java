package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class FluidMigrationTest {
    @Test
    void oldSoupIdBecomesStew() {
        assertEquals("mushroomstew", FluidMigration.migrateName("mushroomsoup"));
        assertEquals("mushroomstew", FluidMigration.migrateName("mushroomSoup"));
    }

    @Test
    void otherNamesAreUntouched() {
        assertEquals("mushroomstew", FluidMigration.migrateName("mushroomstew"));
        assertEquals("water", FluidMigration.migrateName("water"));
        assertEquals("", FluidMigration.migrateName(""));
        assertNull(FluidMigration.migrateName(null));
    }
}
