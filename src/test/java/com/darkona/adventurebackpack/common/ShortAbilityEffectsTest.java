package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Squid water breathing and Slime full moon Speed: renewed near their end, not every tick, and only their own removed. */
class ShortAbilityEffectsTest {
    private static final int SQUID = AbilityEffects.SQUID_WATER_BREATHING_MARKER;
    private static final int SLIME = AbilityEffects.SLIME_SPEED_AMPLIFIER;

    @Test
    void thresholdLeavesRoomBetweenRefreshes() {
        assertTrue(AbilityEffects.SHORT_EFFECT_REFRESH_BELOW > 0);
        assertTrue(AbilityEffects.SHORT_EFFECT_TICKS - AbilityEffects.SHORT_EFFECT_REFRESH_BELOW >= 20);
    }

    @Test
    void appliesWhenAbsent() {
        assertTrue(AbilityEffects.needsShortRefresh(false, 0, 0, SQUID));
        assertTrue(AbilityEffects.needsShortRefresh(false, 0, 0, SLIME));
    }

    @Test
    void doesNotRefreshEveryTick() {
        assertFalse(AbilityEffects.needsShortRefresh(true, AbilityEffects.SHORT_EFFECT_TICKS, SQUID, SQUID));
        assertFalse(AbilityEffects.needsShortRefresh(true, AbilityEffects.SHORT_EFFECT_REFRESH_BELOW, SLIME, SLIME));
    }

    @Test
    void refreshesOwnEffectNearItsEnd() {
        assertTrue(AbilityEffects.needsShortRefresh(true, AbilityEffects.SHORT_EFFECT_REFRESH_BELOW - 1, SQUID, SQUID));
        assertTrue(AbilityEffects.needsShortRefresh(true, 1, SLIME, SLIME));
    }

    @Test
    void neverOverridesAnotherLevel() {
        // a water breathing potion, or a Speed I or Speed III from a potion or a beacon
        assertFalse(AbilityEffects.needsShortRefresh(true, 5, 0, SQUID));
        assertFalse(AbilityEffects.needsShortRefresh(true, 5, 0, SLIME));
        assertFalse(AbilityEffects.needsShortRefresh(true, 5, 2, SLIME));
    }

    @Test
    void squidRemovalKeepsTheMinusFiveMarker() {
        assertTrue(AbilityEffects.isSquidWaterBreathing(-5));
        assertFalse(AbilityEffects.isSquidWaterBreathing(0));
        assertFalse(AbilityEffects.isSquidWaterBreathing(-4));
    }

    @Test
    void slimeRemovalTakesOnlyItsOwnSpeed() {
        assertTrue(AbilityEffects.isSlimeSpeed(SLIME, AbilityEffects.SHORT_EFFECT_TICKS));
        assertTrue(AbilityEffects.isSlimeSpeed(SLIME, 1));
        assertFalse(AbilityEffects.isSlimeSpeed(SLIME, AbilityEffects.SHORT_EFFECT_TICKS + 1));
        assertFalse(AbilityEffects.isSlimeSpeed(SLIME, 1800));
        assertFalse(AbilityEffects.isSlimeSpeed(0, 20));
        assertFalse(AbilityEffects.isSlimeSpeed(2, 20));
    }
}
