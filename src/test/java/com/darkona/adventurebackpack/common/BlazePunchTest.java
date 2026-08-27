package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BlazePunchTest
{
    @Test
    void anEmptyHandAlwaysBurns()
    {
        assertTrue(AbilityEffects.blazePunchIgnites(true, false, false));
        assertTrue(AbilityEffects.blazePunchIgnites(true, false, true));
    }

    @Test
    void aWeaponNeverBurns()
    {
        assertFalse(AbilityEffects.blazePunchIgnites(false, true, true));
        assertFalse(AbilityEffects.blazePunchIgnites(false, true, false));
    }

    @Test
    void anotherItemBurnsOnlyWhenTheConfigSaysSo()
    {
        assertTrue(AbilityEffects.blazePunchIgnites(false, false, true));
        assertFalse(AbilityEffects.blazePunchIgnites(false, false, false));
    }
}
