package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

/** Horse owner strings that are not UUIDs must not crash the server (#99). */
class ParseUUIDTest {
    @Test
    void emptyNullAndPlayerNamesGiveNull() {
        assertNull(Utils.parseUUIDOrNull(null));
        assertNull(Utils.parseUUIDOrNull(""));
        assertNull(Utils.parseUUIDOrNull("Notch"));
    }

    @Test
    void validUuidIsParsed() {
        UUID id = UUID.randomUUID();
        assertEquals(id, Utils.parseUUIDOrNull(id.toString()));
    }
}
