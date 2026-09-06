package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** The key that switches the worn backpack's abilities off and on for one player (#57). */
class AbilityToggleTest {
    @Test
    void abilitiesWorkOnlyWhenBothSwitchesAllowThem() {
        assertTrue(AbilityToggle.active(true, false));
        assertFalse(AbilityToggle.active(true, true), "the player switched them off");
        assertFalse(AbilityToggle.active(false, false), "enabled.abilities wins");
        assertFalse(AbilityToggle.active(false, true));
    }

    @Test
    void theKeyFlipsThePlayerSwitch() {
        assertEquals(AbilityToggle.TURNED_OFF, AbilityToggle.toggle(true, false));
        assertEquals(AbilityToggle.TURNED_ON, AbilityToggle.toggle(true, true));
    }

    @Test
    void theKeyDoesNothingWhenTheConfigTurnsThemOff() {
        assertEquals(AbilityToggle.GLOBALLY_DISABLED, AbilityToggle.toggle(false, false));
        assertEquals(AbilityToggle.GLOBALLY_DISABLED, AbilityToggle.toggle(false, true));
    }
}
