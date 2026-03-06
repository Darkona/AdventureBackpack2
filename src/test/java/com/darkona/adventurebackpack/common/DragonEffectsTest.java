package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The Dragon backpack must give real, positive effect levels and only remove its own (#122, #105). */
class DragonEffectsTest {
    @Test
    void dragonLevelsArePositive() {
        assertTrue(AbilityEffects.DRAGON_STRENGTH_AMPLIFIER >= 0);
        assertTrue(AbilityEffects.DRAGON_REGENERATION_AMPLIFIER >= 0);
    }

    @Test
    void removalRecognisesOnlyTheDragonEffect() {
        int strength = AbilityEffects.DRAGON_STRENGTH_AMPLIFIER;
        assertTrue(AbilityEffects.isDragonEffect(strength, 4000, strength));
        assertFalse(AbilityEffects.isDragonEffect(strength + 1, 4000, strength));
        assertFalse(AbilityEffects.isDragonEffect(strength, AbilityEffects.DRAGON_EFFECT_TICKS + 1, strength));
    }

    @Test
    void effectsRenewBeforeTheyRunOut() {
        assertTrue(AbilityEffects.needsRefresh(0));
        assertTrue(AbilityEffects.needsRefresh(AbilityEffects.EFFECT_REFRESH_TICKS - 1));
        assertFalse(AbilityEffects.needsRefresh(AbilityEffects.EFFECT_REFRESH_TICKS));
    }
}
