package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NightVisionRefreshTest {
    private static final int AMP = -4;

    @Test
    void appliesWhenAbsent() {
        assertTrue(NightVisionRefresh.needsRefresh(false, 0, 0, AMP));
    }

    @Test
    void doesNotRefreshWhileDurationIsLong() {
        assertFalse(NightVisionRefresh.needsRefresh(true, 5000, AMP, AMP));
        assertFalse(NightVisionRefresh.needsRefresh(true, 300, AMP, AMP));
    }

    @Test
    void refreshesBeforeTheVanillaFadeWindow() {
        assertTrue(NightVisionRefresh.needsRefresh(true, 250, AMP, AMP));
        assertTrue(NightVisionRefresh.needsRefresh(true, 10, AMP, AMP));
    }

    @Test
    void neverOverridesAnotherSourcesEffect() {
        assertFalse(NightVisionRefresh.needsRefresh(true, 10, 0, AMP));
    }
}
