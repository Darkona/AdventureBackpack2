package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FuelRulesTest {
    @Test
    void defaultListKeepsTheOldMultipliersAndAddsBiomass() {
        Map<String, Float> fuels = FuelRules.parse(FuelRules.DEFAULTS);
        assertEquals(7, fuels.size());
        assertEquals(1.0f, fuels.get("fuel"));
        assertEquals(1.5f, fuels.get("oil"));
        assertEquals(1.2f, fuels.get("bioethanol"));
        assertEquals(1.0f, fuels.get("biofuel"));
        assertEquals(1.8f, fuels.get("creosote"));
        assertEquals(0.8f, fuels.get("coal"));
        assertEquals(1.5f, fuels.get("biomass"));
        // every old default is still there with the same multiplier
        Map<String, Float> old = FuelRules.parse(FuelRules.OLD_COPTER_DEFAULTS);
        for (Map.Entry<String, Float> e : old.entrySet()) assertEquals(e.getValue(), fuels.get(e.getKey()), e.getKey());
    }

    @Test
    void lavaAndWaterAreNotFuelsByDefault() {
        Map<String, Float> fuels = FuelRules.parse(FuelRules.DEFAULTS);
        assertFalse(FuelRules.accepts(fuels, "lava"));
        assertFalse(FuelRules.accepts(fuels, "water"));
        assertFalse(FuelRules.accepts(fuels, null));
        assertTrue(FuelRules.accepts(fuels, "oil"));
    }

    @Test
    void multiplierOfAnUnknownFuelIsOne() {
        Map<String, Float> fuels = FuelRules.parse(FuelRules.DEFAULTS);
        assertEquals(1.5f, FuelRules.multiplier(fuels, "oil"));
        assertEquals(1.0f, FuelRules.multiplier(fuels, "lava"));
        assertEquals(1.0f, FuelRules.multiplier(fuels, null));
    }

    @Test
    void wrongEntriesAreSkipped() {
        Map<String, Float> fuels = FuelRules.parse(new String[] {
                "fuel", "=1.0", "oil=abc", "lava=0", "ice=-1", "x=Infinity", null, " diesel = 0.9 "});
        assertEquals(1, fuels.size());
        assertEquals(0.9f, fuels.get("diesel"));
    }

    @Test
    void laterEntryWins() {
        Map<String, Float> fuels = FuelRules.parse(new String[] {"oil=1.5", "oil=2"});
        assertEquals(2.0f, fuels.get("oil"));
    }

    @Test
    void emptyOrMissingListGivesNoFuel() {
        assertTrue(FuelRules.parse(new String[0]).isEmpty());
        assertFalse(FuelRules.parse(null).containsKey("fuel"));
    }

    @Test
    void migrationWithoutAnOldValueMovesNothing() {
        assertNull(FuelRules.migrate(null));
    }

    @Test
    void untouchedOldValueBecomesTheNewDefault() {
        assertArrayEquals(FuelRules.DEFAULTS, FuelRules.migrate(FuelRules.OLD_COPTER_DEFAULTS.clone()));
        // spaces the file may add around the entries do not count as a change
        assertArrayEquals(FuelRules.DEFAULTS, FuelRules.migrate(new String[] {
                " fuel=1.0", "oil = 1.5", "bioethanol=1.2", "biofuel=1.0", "creosote=1.8", "coal=0.8 "}));
    }

    @Test
    void changedOldValueIsKeptAsItIs() {
        String[] mine = {"fuel=1.0", "oil=3.0", "lava=4.0"};
        assertArrayEquals(mine, FuelRules.migrate(mine));
        String[] fewer = {"fuel=1.0"};
        assertArrayEquals(fewer, FuelRules.migrate(fewer));
        String[] empty = {};
        assertArrayEquals(empty, FuelRules.migrate(empty));
    }

    @Test
    void migrationReturnsACopy() {
        String[] mine = {"oil=2"};
        String[] moved = FuelRules.migrate(mine);
        moved[0] = "changed";
        assertEquals("oil=2", mine[0]);
        String[] fromDefault = FuelRules.migrate(FuelRules.OLD_COPTER_DEFAULTS);
        fromDefault[0] = "changed";
        assertEquals("fuel=1.0", FuelRules.DEFAULTS[0]);
    }

    @Test
    void anyLoadedFuelModMakesTheFuelUsersWork() {
        Map<String, Float> fuels = FuelRules.parse(FuelRules.DEFAULTS);
        assertFalse(FuelRules.anyRegistered(fuels.keySet(), new HashSet<String>(Arrays.asList("water", "lava"))), "no fuel mod");
        assertTrue(FuelRules.anyRegistered(fuels.keySet(), new HashSet<String>(Arrays.asList("water", "lava", "creosote"))), "Railcraft alone");
        assertTrue(FuelRules.anyRegistered(fuels.keySet(), new HashSet<String>(Arrays.asList("water", "bioethanol"))), "Forestry alone");
        assertFalse(FuelRules.anyRegistered(Collections.<String>emptySet(), new HashSet<String>(Arrays.asList("fuel"))), "an empty list");
        assertFalse(FuelRules.anyRegistered(null, null));
    }
}
