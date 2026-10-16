package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

/**
 * The Skeleton Backpack's quiver: which items its slots take, how many arrows the model draws, and what happens to
 * the fluid of the right tank it replaces.
 */
class QuiverRulesTest
{
    @Test
    void theModelDrawsMoreArrowsAsTheQuiverFills()
    {
        assertEquals(QuiverRules.LEVEL_EMPTY, QuiverRules.level(0));
        assertEquals(QuiverRules.LEVEL_EMPTY, QuiverRules.level(-4));
        assertEquals(QuiverRules.LEVEL_FEW, QuiverRules.level(1));
        assertEquals(QuiverRules.LEVEL_FEW, QuiverRules.level(8));
        assertEquals(QuiverRules.LEVEL_SOME, QuiverRules.level(9));
        assertEquals(QuiverRules.LEVEL_SOME, QuiverRules.level(32));
        assertEquals(QuiverRules.LEVEL_MANY, QuiverRules.level(33));
        assertEquals(QuiverRules.LEVEL_MANY, QuiverRules.level(256));

        assertEquals(0, QuiverRules.arrowsDrawn(QuiverRules.LEVEL_EMPTY));
        assertEquals(1, QuiverRules.arrowsDrawn(QuiverRules.LEVEL_FEW));
        assertEquals(3, QuiverRules.arrowsDrawn(QuiverRules.LEVEL_SOME));
        assertEquals(5, QuiverRules.arrowsDrawn(QuiverRules.LEVEL_MANY));
        // A level out of range (a tag written by another version) never draws more parts than the model has
        assertEquals(QuiverRules.MAX_ARROWS_DRAWN, QuiverRules.arrowsDrawn(99));
        assertEquals(0, QuiverRules.arrowsDrawn(-1));
    }

    @Test
    void theRightTankMovesIntoTheLeftOneWhenItFits()
    {
        // Left empty: everything moves (the capacity is the same for both tanks)
        assertEquals(3000, QuiverRules.movedToLeft(true, false, 0, 3000, 4000));
        // Same fluid with room: everything
        assertEquals(1500, QuiverRules.movedToLeft(false, true, 2000, 1500, 4000));
        // Same fluid, not enough room: what fits, the rest stays sealed in the right tank
        assertEquals(1000, QuiverRules.movedToLeft(false, true, 3000, 2500, 4000));
        // Another fluid on the left: nothing moves, nothing is lost (it stays sealed)
        assertEquals(0, QuiverRules.movedToLeft(false, false, 1000, 2500, 4000));
        // Left full of the same fluid
        assertEquals(0, QuiverRules.movedToLeft(false, true, 4000, 500, 4000));
        // Nothing in the right tank
        assertEquals(0, QuiverRules.movedToLeft(true, false, 0, 0, 4000));
    }

    @Test
    void theQuiverTakesTheListedItemsOnly()
    {
        QuiverRules.Filter f = QuiverRules.Filter.parse(ConfigHandler.QUIVER_ITEMS_DEFAULT);
        assertTrue(f.accepts("minecraft:arrow", 0, null));
        assertTrue(f.accepts("adventurebackpack:bolt", 0, null));
        // Any damage value when the entry gives none
        assertTrue(f.accepts("minecraft:arrow", 3, null));
        assertFalse(f.accepts("minecraft:stick", 0, null));
        assertFalse(f.accepts(null, 0, null));
        assertFalse(f.usesOres());
        assertTrue(f.rejected().isEmpty());
    }

    @Test
    void entriesCanNameADamageValueAnOreOrAPlainName()
    {
        QuiverRules.Filter f = QuiverRules.Filter.parse(new String[]{
                "item:TConstruct:arrow:2", "ore:arrowIron", "Botania:manaArrow", "  ", null,
                "stack:Foo:bar", "ic2:motor", "item::x", "nonsense"});
        assertTrue(f.accepts("TConstruct:arrow", 2, null));
        assertFalse(f.accepts("TConstruct:arrow", 0, null));
        assertTrue(f.accepts("Botania:manaArrow", 7, null));
        assertTrue(f.usesOres());
        assertTrue(f.accepts("othermod:steelArrow", 0, new String[]{"ingotFoo", "arrowIron"}));
        assertFalse(f.accepts("othermod:steelArrow", 0, new String[]{"ingotFoo"}));
        // Recipe-only kinds and malformed entries are skipped and reported for the log
        assertEquals(4, f.rejected().size());
        assertTrue(f.rejected().contains("stack:Foo:bar"));
        assertTrue(f.rejected().contains("nonsense"));
        assertFalse(QuiverRules.Filter.parse(new String[0]).accepts("minecraft:arrow", 0, null));
    }
}
