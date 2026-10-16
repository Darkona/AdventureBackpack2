package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Taking the Rainbow backpack off removes only its own Speed and Jump, not a potion's or a beacon's. */
class RainbowEffectsTest {
    private static final int AMP = 2;

    @Test
    void ownShortEffectIsRecognised() {
        assertTrue(AbilityEffects.isRainbowEffect(AMP, 40, AMP));
        assertTrue(AbilityEffects.isRainbowEffect(AMP, 1, AMP));
    }

    @Test
    void longerEffectOfTheSameLevelIsSomeoneElses() {
        assertFalse(AbilityEffects.isRainbowEffect(AMP, 41, AMP));
        assertFalse(AbilityEffects.isRainbowEffect(AMP, 3600, AMP));
    }

    @Test
    void otherLevelIsSomeoneElses() {
        assertFalse(AbilityEffects.isRainbowEffect(1, 20, AMP));
    }

    @Test
    void theRunRenewsItsEffectsNearTheirEndOnly() {
        assertTrue(AbilityEffects.needsRainbowRefresh(false, 0, 0, AMP), "missing");
        assertTrue(AbilityEffects.needsRainbowRefresh(true, AMP - 1, 3600, AMP), "weaker: replaced, as addPotionEffect did");
        assertTrue(AbilityEffects.needsRainbowRefresh(true, AMP, 19, AMP), "own, close to its end");
        assertFalse(AbilityEffects.needsRainbowRefresh(true, AMP, 20, AMP), "own, time left: no packet");
        assertFalse(AbilityEffects.needsRainbowRefresh(true, AMP, 39, AMP));
        assertFalse(AbilityEffects.needsRainbowRefresh(true, AMP + 1, 5, AMP), "stronger: kept");
    }

    @Test
    void onlyTheOldMinusFiveMarkerIsStale() {
        assertTrue(AbilityEffects.isStaleRainbowMarker(-5));
        assertFalse(AbilityEffects.isStaleRainbowMarker(AMP));
        assertFalse(AbilityEffects.isStaleRainbowMarker(0));
    }
}
