package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Cycling tools must move each stack to exactly one place and reject bad slots (#56, #67, #78). */
class CycleToolTest {
    @Test
    void scrollDownTakesTheUpperTool() {
        Object[] tools = {"held", "upper", "lower"};
        ServerActions.rotateTools(tools, -120);
        assertArrayEquals(new Object[] {"upper", "lower", "held"}, tools);
    }

    @Test
    void scrollUpTakesTheLowerTool() {
        Object[] tools = {"held", "upper", "lower"};
        ServerActions.rotateTools(tools, 120);
        assertArrayEquals(new Object[] {"lower", "held", "upper"}, tools);
    }

    @Test
    void scrollingBackRestoresTheStart() {
        Object[] tools = {"held", null, "lower"};
        ServerActions.rotateTools(tools, 1);
        ServerActions.rotateTools(tools, -1);
        assertArrayEquals(new Object[] {"held", null, "lower"}, tools);
    }

    @Test
    void zeroDirectionChangesNothing() {
        Object[] tools = {"held", "upper", "lower"};
        ServerActions.rotateTools(tools, 0);
        assertArrayEquals(new Object[] {"held", "upper", "lower"}, tools);
    }

    @Test
    void onlyHotbarSlotsAreAccepted() {
        assertTrue(ServerActions.isHotbarSlot(0));
        assertTrue(ServerActions.isHotbarSlot(8));
        assertFalse(ServerActions.isHotbarSlot(-1));
        assertFalse(ServerActions.isHotbarSlot(9));
        assertFalse(ServerActions.isHotbarSlot(35));
    }
}
