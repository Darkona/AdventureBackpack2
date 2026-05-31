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
        assertEquals(0, ToolCycleInput.keyDirection(false, Mode.BOTH, true, false));
    }

    @Test
    void keysNeedTheKeysModeAndOneKey() {
        assertEquals(1, ToolCycleInput.keyDirection(true, Mode.KEYS, true, false));
        assertEquals(-1, ToolCycleInput.keyDirection(true, Mode.BOTH, false, true));
        assertEquals(0, ToolCycleInput.keyDirection(true, Mode.WHEEL, true, false));
        assertEquals(0, ToolCycleInput.keyDirection(true, Mode.BOTH, true, true));
        assertEquals(0, ToolCycleInput.keyDirection(true, Mode.BOTH, false, false));
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
        ServerActions.rotateTools(tools, ToolCycleInput.keyDirection(true, Mode.KEYS, true, false));
        assertEquals("lower", tools[0]);
    }
}
