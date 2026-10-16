package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

class DistilleryRulesTest
{
    // defaults of the distillery keys
    private static final int OUTPUT = 8000;
    private static final int WATER_PER_BATCH = 1000;
    private static final int TICKS_PER_BATCH = 400;
    private static final int INTERVAL = 10;

    @Test
    void defaultsInTheConfigFieldsMatchTheseNumbers()
    {
        assertTrue(ConfigHandler.DISTILLERY_ENABLED);
        assertEquals(OUTPUT, ConfigHandler.DISTILLERY_OUTPUT_CAPACITY);
        assertEquals(8000, ConfigHandler.DISTILLERY_WATER_CAPACITY);
        assertEquals(WATER_PER_BATCH, ConfigHandler.DISTILLERY_WATER_PER_BATCH);
        assertEquals(TICKS_PER_BATCH, ConfigHandler.DISTILLERY_TICKS_PER_BATCH);
        assertEquals(INTERVAL, ConfigHandler.DISTILLERY_INTERVAL_TICKS);
        assertEquals(1, ConfigHandler.DISTILLERY_CATALYST_PER_BATCH);
        assertEquals("item:minecraft:sugar", ConfigHandler.DISTILLERY_CATALYSTS[0]);
        assertEquals("bioethanol", ConfigHandler.DISTILLERY_OUTPUT_FLUID);
    }

    // ---------------------------------------------------------------- batches

    @Test
    void aBatchTakesOneItemOfEverySlotWithAYield()
    {
        int[] yields = {100, 0, 150, 0, 0, 0, 0, 0, 40};
        int mask = DistilleryRules.planBatch(yields, OUTPUT);
        assertEquals(0b100000101, mask);
        assertEquals(290, DistilleryRules.batchYield(yields, mask));
    }

    @Test
    void ninePotatoesMakeNineTimesOne()
    {
        int[] yields = {150, 150, 150, 150, 150, 150, 150, 150, 150};
        int mask = DistilleryRules.planBatch(yields, OUTPUT);
        assertEquals(0x1FF, mask);
        assertEquals(1350, DistilleryRules.batchYield(yields, mask));
    }

    @Test
    void aNearlyFullTankTakesOnlyWhatFits()
    {
        int[] yields = {150, 150, 100, 0, 0, 0, 0, 0, 20};
        // room 270: the first 150 fits, the second does not (300), the 100 does (250), the 20 does (270)
        int mask = DistilleryRules.planBatch(yields, 270);
        assertEquals(0b100000101, mask);
        assertEquals(270, DistilleryRules.batchYield(yields, mask));
    }

    @Test
    void aFullTankOrAnEmptyGridPlansNothing()
    {
        int[] yields = {150, 0, 0, 0, 0, 0, 0, 0, 0};
        assertEquals(0, DistilleryRules.planBatch(yields, 0));
        assertEquals(0, DistilleryRules.planBatch(yields, 149));
        assertEquals(0, DistilleryRules.planBatch(new int[9], OUTPUT));
        assertEquals(0, DistilleryRules.planBatch(null, OUTPUT));
        assertEquals(0, DistilleryRules.batchYield(null, 0x1FF));
    }

    @Test
    void itStopsWithoutOrganicsCatalystOrWater()
    {
        assertTrue(DistilleryRules.canStart(1, 1, 1, WATER_PER_BATCH, WATER_PER_BATCH));
        assertFalse(DistilleryRules.canStart(0, 1, 1, WATER_PER_BATCH, WATER_PER_BATCH), "no organics");
        assertFalse(DistilleryRules.canStart(1, 0, 1, WATER_PER_BATCH, WATER_PER_BATCH), "no catalyst");
        assertFalse(DistilleryRules.canStart(1, 1, 2, WATER_PER_BATCH, WATER_PER_BATCH), "not enough catalyst");
        assertFalse(DistilleryRules.canStart(1, 1, 1, WATER_PER_BATCH - 1, WATER_PER_BATCH), "not enough water");
    }

    @Test
    void aCostOfZeroMakesThatInputFree()
    {
        assertTrue(DistilleryRules.canStart(1, 0, 0, 0, 0));
    }

    // ---------------------------------------------------------------- heat and time

    @Test
    void aNewFuelIsLitOnlyWhenTheFlameIsOutAndThereIsABatch()
    {
        assertTrue(DistilleryRules.shouldLightFuel(0, true));
        assertFalse(DistilleryRules.shouldLightFuel(0, false), "idle keeps its coal");
        assertFalse(DistilleryRules.shouldLightFuel(5, true), "still burning");
    }

    @Test
    void theFlameNeverBurnsMoreThanItHas()
    {
        assertEquals(INTERVAL, DistilleryRules.burnTicksUsed(3200, INTERVAL));
        assertEquals(4, DistilleryRules.burnTicksUsed(4, INTERVAL));
        assertEquals(0, DistilleryRules.burnTicksUsed(0, INTERVAL));
        assertEquals(0, DistilleryRules.burnTicksUsed(100, 0));
    }

    @Test
    void theTimerMovesOnlyWithFlameAndABatchAndResetsWithoutOne()
    {
        assertEquals(10, DistilleryRules.advance(0, 10, true, TICKS_PER_BATCH));
        assertEquals(10, DistilleryRules.advance(10, 0, true, TICKS_PER_BATCH), "no flame: the timer waits");
        assertEquals(0, DistilleryRules.advance(200, 10, false, TICKS_PER_BATCH), "nothing to make: back to 0");
        assertEquals(TICKS_PER_BATCH, DistilleryRules.advance(395, 10, true, TICKS_PER_BATCH), "stops at the end");
        assertTrue(DistilleryRules.batchDone(TICKS_PER_BATCH, TICKS_PER_BATCH));
        assertFalse(DistilleryRules.batchDone(TICKS_PER_BATCH - 1, TICKS_PER_BATCH));
    }

    @Test
    void oneCoalRunsEightBatchesWithTheDefaults()
    {
        int burn = 3200, progress = 0, batches = 0;
        while (burn > 0)
        {
            int used = DistilleryRules.burnTicksUsed(burn, INTERVAL);
            burn -= used;
            progress = DistilleryRules.advance(progress, used, true, TICKS_PER_BATCH);
            if (DistilleryRules.batchDone(progress, TICKS_PER_BATCH))
            {
                batches++;
                progress = 0;
            }
        }
        assertEquals(8, batches);
    }

    // ---------------------------------------------------------------- push and water sources

    @Test
    void thePushOffersWhatIsStoredUpToTheConfiguredAmount()
    {
        assertEquals(1000, DistilleryRules.pushAmount(8000, 1000));
        assertEquals(300, DistilleryRules.pushAmount(300, 1000));
        assertEquals(0, DistilleryRules.pushAmount(0, 1000));
        assertEquals(0, DistilleryRules.pushAmount(8000, 0), "0 turns the push off");
    }

    @Test
    void thePushIsDueOnItsIntervalOnlyWithFluidAndANeighbour()
    {
        assertTrue(DistilleryRules.pushDue(40, 20, 100, true));
        assertFalse(DistilleryRules.pushDue(41, 20, 100, true), "between pushes");
        assertFalse(DistilleryRules.pushDue(40, 20, 0, true), "nothing stored");
        assertFalse(DistilleryRules.pushDue(40, 20, 100, false), "nobody next to it");
        assertFalse(DistilleryRules.pushDue(40, 0, 100, true));
    }

    @Test
    void thePushStartsFromTheNextSideEachTime()
    {
        assertEquals(1, DistilleryRules.nextStartSide(0, 4));
        assertEquals(0, DistilleryRules.nextStartSide(3, 4));
        assertEquals(0, DistilleryRules.nextStartSide(2, 0));
    }

    @Test
    void waterSourcesFillTheTankAsFarAsItHasRoom()
    {
        // two sources at 50 mB per second for 10 ticks (half a second): 50 mB
        assertEquals(50, DistilleryRules.sourceWater(2, 50, INTERVAL, 0, 8000));
        assertEquals(10, DistilleryRules.sourceWater(2, 50, INTERVAL, 7990, 8000));
        assertEquals(0, DistilleryRules.sourceWater(2, 50, INTERVAL, 8000, 8000));
        assertEquals(0, DistilleryRules.sourceWater(0, 50, INTERVAL, 0, 8000));
        assertEquals(0, DistilleryRules.sourceWater(2, 0, INTERVAL, 0, 8000));
    }

    @Test
    void twoSourcesKeepUpWithTheDefaults()
    {
        // one batch lasts 400 ticks and uses 1000 mB; two sources give 50 x 2 x 20 s = 2000 mB in that time
        int perBatch = 0;
        for (int t = 0; t < TICKS_PER_BATCH; t += INTERVAL)
        {
            perBatch += DistilleryRules.sourceWater(2, ConfigHandler.DISTILLERY_WATER_PER_SOURCE, INTERVAL, 0, 64000);
        }
        assertTrue(perBatch >= WATER_PER_BATCH, "got " + perBatch);
    }

    // ---------------------------------------------------------------- config entries

    @Test
    void amountEntriesParse()
    {
        DistilleryRules.Amount a = DistilleryRules.parseAmount(" item:minecraft:potato = 150 ");
        assertNotNull(a);
        assertEquals("item:minecraft:potato", a.item);
        assertEquals(150, a.amount);
        DistilleryRules.Amount meta = DistilleryRules.parseAmount("item:minecraft:coal:1=1600");
        assertNotNull(meta);
        assertEquals("item:minecraft:coal:1", meta.item);
        assertEquals("ore:cropCorn", DistilleryRules.parseAmount("ore:cropCorn=150").item);
    }

    @Test
    void malformedAmountEntriesAreRejected()
    {
        assertNull(DistilleryRules.parseAmount(null));
        assertNull(DistilleryRules.parseAmount("item:minecraft:potato"));
        assertNull(DistilleryRules.parseAmount("item:minecraft:potato="));
        assertNull(DistilleryRules.parseAmount("=150"));
        assertNull(DistilleryRules.parseAmount("item:minecraft:potato=lots"));
        assertNull(DistilleryRules.parseAmount("item:minecraft:potato=0"));
        assertNull(DistilleryRules.parseAmount("item:minecraft:potato=-5"));
    }

    @Test
    void anItemEntryWithoutDamageMatchesEveryDamage()
    {
        assertTrue(DistilleryRules.anyDamage("item:minecraft:coal"));
        assertFalse(DistilleryRules.anyDamage("item:minecraft:coal:1"));
        assertFalse(DistilleryRules.anyDamage("ore:cropCorn"));
        assertFalse(DistilleryRules.anyDamage(null));
    }

    @Test
    void everyDefaultEntryParses()
    {
        for (String e : ConfigHandler.DISTILLERY_ORGANICS_DEFAULT)
        {
            DistilleryRules.Amount a = DistilleryRules.parseAmount(e);
            assertNotNull(a, e);
            assertNotNull(BoatRecipeRules.parse(a.item), e);
        }
        for (String e : ConfigHandler.DISTILLERY_SOLID_FUELS_DEFAULT)
        {
            DistilleryRules.Amount a = DistilleryRules.parseAmount(e);
            assertNotNull(a, e);
            assertNotNull(BoatRecipeRules.parse(a.item), e);
        }
        for (String e : ConfigHandler.DISTILLERY_CATALYSTS_DEFAULT)
        {
            assertNotNull(BoatRecipeRules.parse(e), e);
        }
    }
}
