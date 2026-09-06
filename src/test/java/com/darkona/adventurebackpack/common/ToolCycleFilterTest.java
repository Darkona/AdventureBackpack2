package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.ToolCycleFilter.Mode;
import org.junit.jupiter.api.Test;

/** The config list of items for the backpack's tool slots (#49). */
class ToolCycleFilterTest {
    private static final ToolCycleFilter LIST = ToolCycleFilter.parse(new String[]{
            " minecraft:shears ", "TConstruct:pickaxe", "stick", "Thaumcraft:*", "", "   "});

    @Test
    void matchesIdsWholeModsAndBareVanillaNames() {
        assertTrue(LIST.listed("minecraft:shears"));
        assertTrue(LIST.listed("tconstruct:PickAxe"), "case does not matter");
        assertTrue(LIST.listed("minecraft:stick"), "a bare name means minecraft:");
        assertTrue(LIST.listed("Thaumcraft:WandCasting"), "modid:* takes every item of the mod");
        assertFalse(LIST.listed("minecraft:iron_pickaxe"));
        assertFalse(LIST.listed("tconstruct:hatchet"));
        assertFalse(LIST.listed("thaumcraftaddon:wand"), "a mod id is matched whole");
        assertFalse(LIST.listed(null), "an unregistered item is never listed");
    }

    @Test
    void anEmptyListMatchesNothing() {
        assertFalse(ToolCycleFilter.parse(new String[0]).listed("minecraft:shears"));
        assertFalse(ToolCycleFilter.parse(null).listed("minecraft:shears"));
        assertFalse(ToolCycleFilter.EMPTY.listed("minecraft:shears"));
    }

    @Test
    void noneKeepsTheBuiltInRules() {
        assertTrue(ToolCycleFilter.allows(Mode.NONE, false, true));
        assertTrue(ToolCycleFilter.allows(Mode.NONE, true, true), "the list is ignored");
        assertFalse(ToolCycleFilter.allows(Mode.NONE, true, false));
    }

    @Test
    void whitelistTakesOnlyTheListedItems() {
        assertTrue(ToolCycleFilter.allows(Mode.WHITELIST, true, false), "even an item the built-in rules refuse");
        assertFalse(ToolCycleFilter.allows(Mode.WHITELIST, false, true), "even a vanilla tool");
    }

    @Test
    void blacklistRemovesTheListedItemsFromTheBuiltInRules() {
        assertTrue(ToolCycleFilter.allows(Mode.BLACKLIST, false, true));
        assertFalse(ToolCycleFilter.allows(Mode.BLACKLIST, true, true));
        assertFalse(ToolCycleFilter.allows(Mode.BLACKLIST, false, false), "it never adds an item");
    }

    @Test
    void modeParsesConfigTextAndFallsBackToNone() {
        assertEquals(Mode.WHITELIST, Mode.parse(" Whitelist "));
        assertEquals(Mode.BLACKLIST, Mode.parse("blacklist"));
        assertEquals(Mode.NONE, Mode.parse("none"));
        assertEquals(Mode.NONE, Mode.parse("graylist"));
        assertEquals(Mode.NONE, Mode.parse(null));
    }
}
