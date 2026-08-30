package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.BoatRecipeRules.Entry;
import com.darkona.adventurebackpack.common.BoatRecipeRules.Mode;
import com.darkona.adventurebackpack.common.BoatRecipeRules.Source;
import org.junit.jupiter.api.Test;

class BoatRecipeRulesTest
{
    @Test
    void vanillaModeIgnoresTechnicalParts()
    {
        assertTrue(BoatRecipeRules.vanillaRecipe(Mode.VANILLA, true));
        assertFalse(BoatRecipeRules.technicalRecipe(Mode.VANILLA, true));
    }

    @Test
    void autoModeAddsTheTechnicalVariantNextToTheVanillaOne()
    {
        assertTrue(BoatRecipeRules.vanillaRecipe(Mode.AUTO, true));
        assertTrue(BoatRecipeRules.technicalRecipe(Mode.AUTO, true));
        assertTrue(BoatRecipeRules.vanillaRecipe(Mode.AUTO, false));
        assertFalse(BoatRecipeRules.technicalRecipe(Mode.AUTO, false));
    }

    @Test
    void technicalModeReplacesOnlyWhatAModProvides()
    {
        assertFalse(BoatRecipeRules.vanillaRecipe(Mode.TECHNICAL, true));
        assertTrue(BoatRecipeRules.technicalRecipe(Mode.TECHNICAL, true));
        // without the part the vanilla recipe stays, so the boat can always be made
        assertTrue(BoatRecipeRules.vanillaRecipe(Mode.TECHNICAL, false));
        assertFalse(BoatRecipeRules.technicalRecipe(Mode.TECHNICAL, false));
    }

    @Test
    void modeTextFallsBackToAuto()
    {
        assertEquals(Mode.VANILLA, Mode.parse(" Vanilla "));
        assertEquals(Mode.TECHNICAL, Mode.parse("technical"));
        assertEquals(Mode.AUTO, Mode.parse("auto"));
        assertEquals(Mode.AUTO, Mode.parse("nonsense"));
        assertEquals(Mode.AUTO, Mode.parse(null));
    }

    @Test
    void entriesParse()
    {
        Entry ore = BoatRecipeRules.parse("ore:craftingElectricMotor");
        assertEquals(Source.ORE, ore.source);
        assertEquals("craftingElectricMotor", ore.name);

        Entry ic2 = BoatRecipeRules.parse("ic2:elemotor");
        assertEquals(Source.IC2, ic2.source);
        assertEquals("elemotor", ic2.name);

        Entry stack = BoatRecipeRules.parse("stack:ThermalExpansion:powerCoilGold");
        assertEquals(Source.STACK, stack.source);
        assertEquals("ThermalExpansion", stack.modId);
        assertEquals("powerCoilGold", stack.name);

        Entry gt = BoatRecipeRules.parse("item:gregtech:gt.metaitem.01:32600");
        assertEquals(Source.ITEM, gt.source);
        assertEquals("gregtech", gt.modId);
        assertEquals("gt.metaitem.01", gt.name);
        assertEquals(32600, gt.meta);

        Entry plain = BoatRecipeRules.parse("item:minecraft:piston");
        assertEquals(0, plain.meta);
    }

    @Test
    void malformedEntriesAreRejected()
    {
        assertNull(BoatRecipeRules.parse(null));
        assertNull(BoatRecipeRules.parse(""));
        assertNull(BoatRecipeRules.parse("craftingElectricMotor"));
        assertNull(BoatRecipeRules.parse("ore:"));
        assertNull(BoatRecipeRules.parse("stack:onlymod"));
        assertNull(BoatRecipeRules.parse("item:gregtech:gt.metaitem.01:lv"));
        assertNull(BoatRecipeRules.parse("item:gregtech:gt.metaitem.01:-1"));
        assertNull(BoatRecipeRules.parse("wrench:gregtech:wrench"));
    }
}
