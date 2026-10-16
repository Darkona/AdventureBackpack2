package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import gnu.trove.map.hash.TObjectFloatHashMap;
import org.junit.jupiter.api.Test;

class CodeRegistrationsTest {
    @Test
    void fuelsAreRegisteredByLowerCaseNameAndTheFirstMultiplierStays() {
        CodeRegistrations r = new CodeRegistrations();
        r.addFuel(" Lava ", 4.0);
        r.addFuel("lava", 9.0);
        r.addFuel("oil", 2.5);
        assertEquals(2, r.fuels().size());
        assertEquals(4.0f, r.fuels().get("lava"));
        assertEquals(2.5f, r.fuels().get("oil"));
    }

    @Test
    void badFuelsAreRefused() {
        CodeRegistrations r = new CodeRegistrations();
        assertThrows(IllegalArgumentException.class, () -> r.addFuel(null, 1));
        assertThrows(IllegalArgumentException.class, () -> r.addFuel("  ", 1));
        assertThrows(IllegalArgumentException.class, () -> r.addFuel("oil", 0));
        assertThrows(IllegalArgumentException.class, () -> r.addFuel("oil", -1));
        assertThrows(IllegalArgumentException.class, () -> r.addFuel("oil", Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> r.addFuel("oil", Double.POSITIVE_INFINITY));
        assertThrows(IllegalArgumentException.class, () -> r.addFuel("oil", 1e-60));
        assertTrue(r.fuels().isEmpty());
    }

    @Test
    void airFluidsKeepOrderAndDropRepeats() {
        CodeRegistrations r = new CodeRegistrations();
        r.addAirFluid("Oxygen");
        r.addAirFluid("oxygen");
        r.addAirFluid("breathable");
        assertEquals(Arrays.asList("oxygen", "breathable"), r.airFluids());
        assertThrows(IllegalArgumentException.class, () -> r.addAirFluid(""));
    }

    @Test
    void nothingCanBeAddedAfterTheFreeze() {
        CodeRegistrations r = new CodeRegistrations();
        r.addFuel("oil", 1.5);
        assertFalse(r.isFrozen());
        r.freeze();
        assertTrue(r.isFrozen());
        assertThrows(IllegalStateException.class, () -> r.addFuel("lava", 4));
        assertThrows(IllegalStateException.class, () -> r.addAirFluid("oxygen"));
        assertEquals(1, r.fuels().size());
    }

    @Test
    void theReturnedViewsAreCopies() {
        CodeRegistrations r = new CodeRegistrations();
        r.addFuel("oil", 1.5);
        r.addAirFluid("oxygen");
        r.fuels().clear();
        r.airFluids().clear();
        assertEquals(1, r.fuels().size());
        assertEquals(1, r.airFluids().size());
    }

    @Test
    void theConfigWinsOnAClashAndCodeOnlyAdds() {
        TObjectFloatHashMap<String> config = new TObjectFloatHashMap<String>();
        config.put("oil", 1.5f);
        config.put("fuel", 1.0f);
        TObjectFloatHashMap<String> code = new TObjectFloatHashMap<String>();
        code.put("oil", 9.0f);
        code.put("lava", 4.0f);
        TObjectFloatHashMap<String> merged = CodeRegistrations.mergeFuels(config, code);
        assertEquals(3, merged.size());
        assertEquals(1.5f, merged.get("oil"));
        assertEquals(4.0f, merged.get("lava"));
        // a fuel the config removed is not told apart from one it never had: the code brings it back
        config.remove("fuel");
        code.put("fuel", 2.0f);
        assertEquals(2.0f, CodeRegistrations.mergeFuels(config, code).get("fuel"));
        // the config map is left untouched
        assertEquals(1, config.size());
    }

    @Test
    void breathableNamesMergeWithoutRepeatsIgnoringCase() {
        String[] config = {"air", " Oxygen "};
        assertArrayEquals(new String[]{"air", " Oxygen ", "breathable"},
                CodeRegistrations.mergeAirFluids(config, Arrays.asList("oxygen", "breathable")));
        assertArrayEquals(new String[]{"oxygen"}, CodeRegistrations.mergeAirFluids(null, Arrays.asList("oxygen")));
        assertArrayEquals(new String[]{"air"}, CodeRegistrations.mergeAirFluids(new String[]{"air", null}, Arrays.<String>asList()));
    }
}
