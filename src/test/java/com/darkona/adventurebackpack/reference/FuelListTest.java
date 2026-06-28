package com.darkona.adventurebackpack.reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FuelListTest {
    @Test
    void defaultListKeepsTheOldMultipliers() {
        Map<String, Float> fuels = GeneralReference.parseFuels(ConfigHandler.COPTER_FUELS_DEFAULT);
        assertEquals(6, fuels.size());
        assertEquals(1.0f, fuels.get("fuel"));
        assertEquals(1.5f, fuels.get("oil"));
        assertEquals(1.2f, fuels.get("bioethanol"));
        assertEquals(1.0f, fuels.get("biofuel"));
        assertEquals(1.8f, fuels.get("creosote"));
        assertEquals(0.8f, fuels.get("coal"));
    }

    @Test
    void wrongEntriesAreSkipped() {
        Map<String, Float> fuels = GeneralReference.parseFuels(new String[] {
                "fuel", "=1.0", "oil=abc", "lava=0", "ice=-1", null, " diesel = 0.9 "});
        assertEquals(1, fuels.size());
        assertEquals(0.9f, fuels.get("diesel"));
    }

    @Test
    void laterEntryWins() {
        Map<String, Float> fuels = GeneralReference.parseFuels(new String[] {"oil=1.5", "oil=2"});
        assertEquals(2.0f, fuels.get("oil"));
    }

    @Test
    void emptyOrMissingListGivesNoFuel() {
        assertTrue(GeneralReference.parseFuels(new String[0]).isEmpty());
        assertFalse(GeneralReference.parseFuels(null).containsKey("fuel"));
    }
}
