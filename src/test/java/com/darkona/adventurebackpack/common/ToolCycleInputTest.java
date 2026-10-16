package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.darkona.adventurebackpack.common.ToolCycleInput.Mode;
import org.junit.jupiter.api.Test;

/** Which input cycles the backpack tools, and in which direction (#148). */
class ToolCycleInputTest {
    @Test
    void wheelNeedsTheModifierAndTheWheelMode() {
        assertEquals(1, ToolCycleInput.wheelDirection(true, Mode.WHEEL, true, 120, false));
        assertEquals(-1, ToolCycleInput.wheelDirection(true, Mode.BOTH, true, -120, false));
        assertEquals(0, ToolCycleInput.wheelDirection(true, Mode.WHEEL, false, 120, false));
        assertEquals(0, ToolCycleInput.wheelDirection(true, Mode.KEYS, true, 120, false));
        assertEquals(0, ToolCycleInput.wheelDirection(true, Mode.BOTH, true, 0, false));
    }

    @Test
    void invertedWheelSwapsTheDirection() {
        assertEquals(-1, ToolCycleInput.wheelDirection(true, Mode.WHEEL, true, 120, true));
        assertEquals(1, ToolCycleInput.wheelDirection(true, Mode.WHEEL, true, -1, true));
    }

    @Test
    void disabledIgnoresEverything() {
        assertEquals(0, ToolCycleInput.wheelDirection(false, Mode.BOTH, true, 120, false));
        assertEquals(false, ToolCycleInput.keyAllowed(false, Mode.BOTH));
    }

    @Test
    void theKeyNeedsTheKeysMode() {
        assertEquals(true, ToolCycleInput.keyAllowed(true, Mode.KEYS));
        assertEquals(true, ToolCycleInput.keyAllowed(true, Mode.BOTH));
        assertEquals(false, ToolCycleInput.keyAllowed(true, Mode.WHEEL));
    }

    @Test
    void modeParsingIsLenientAndDefaultsToBoth() {
        assertEquals(Mode.WHEEL, Mode.parse(" Wheel "));
        assertEquals(Mode.KEYS, Mode.parse("keys"));
        assertEquals(Mode.BOTH, Mode.parse("nonsense"));
        assertEquals(Mode.BOTH, Mode.parse(null));
    }

    @Test
    void nextMatchesTheOldScrollUp() {
        // Scroll up (+) used to take the lower tool; "next" keeps that meaning.
        Object[] tools = {"held", "upper", "lower"};
        ServerActions.rotateTools(tools, KeyActions.direction(false));
        assertEquals("lower", tools[0]);
    }
}
