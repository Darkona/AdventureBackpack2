package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

class ScubaRulesTest
{
    // defaults of the scuba keys
    private static final int TANK = 3000;
    private static final int PER_SECOND = 10;
    private static final int INTERVAL = 20;
    private static final int WARNING = 600;

    @Test
    void defaultsInTheConfigFieldsMatchTheseNumbers()
    {
        assertEquals(TANK, ConfigHandler.SCUBA_TANK_CAPACITY);
        assertEquals(PER_SECOND, ConfigHandler.SCUBA_AIR_PER_SECOND);
        assertEquals(INTERVAL, ConfigHandler.SCUBA_AIR_INTERVAL_TICKS);
        assertEquals(WARNING, ConfigHandler.SCUBA_LOW_AIR_WARNING);
    }

    @Test
    void twoFullTanksLastTenMinutesWithTheDefaults()
    {
        int beats = 0;
        int left = TANK, right = TANK;
        int due = ScubaRules.airPerBeat(PER_SECOND, INTERVAL);
        while (left + right > 0)
        {
            int a = ScubaRules.fromFirst(left, due);
            int b = ScubaRules.fromSecond(left, right, due);
            left -= a;
            right -= b;
            beats++;
        }
        assertEquals(600, beats * INTERVAL / 20, "seconds");
    }

    @Test
    void paysOnlyUnderWaterWithAirAndWhenTheBarWentDown()
    {
        assertTrue(ScubaRules.consumesNow(true, true, true));
        assertFalse(ScubaRules.consumesNow(false, true, true), "out of water");
        assertFalse(ScubaRules.consumesNow(true, false, true), "water breathing keeps the bar full: free");
        assertFalse(ScubaRules.consumesNow(true, true, false), "empty tanks");
    }

    @Test
    void costPerBeatRoundsUpAndNeverGoesNegative()
    {
        assertEquals(10, ScubaRules.airPerBeat(10, 20));
        assertEquals(5, ScubaRules.airPerBeat(10, 10));
        assertEquals(1, ScubaRules.airPerBeat(10, 1), "half a millibucket still costs one");
        assertEquals(0, ScubaRules.airPerBeat(0, 20));
        assertEquals(0, ScubaRules.airPerBeat(-5, 20));
        assertEquals(0, ScubaRules.airPerBeat(10, 0));
    }

    @Test
    void leftTankEmptiesFirst()
    {
        assertEquals(10, ScubaRules.fromFirst(100, 10));
        assertEquals(0, ScubaRules.fromSecond(100, 100, 10));
        assertEquals(4, ScubaRules.fromFirst(4, 10));
        assertEquals(6, ScubaRules.fromSecond(4, 100, 10), "the right tank pays the rest");
        assertEquals(3, ScubaRules.fromSecond(4, 3, 10), "never more than it holds");
        assertEquals(0, ScubaRules.fromFirst(0, 10));
        assertEquals(10, ScubaRules.fromSecond(0, 100, 10));
        assertEquals(0, ScubaRules.fromSecond(-1, -1, 10));
    }

    @Test
    void warnsOnceWhenCrossingTheThreshold()
    {
        assertTrue(ScubaRules.shouldWarn(605, 595, WARNING));
        assertTrue(ScubaRules.shouldWarn(600, 590, WARNING));
        assertFalse(ScubaRules.shouldWarn(595, 585, WARNING), "already below: no second warning");
        assertFalse(ScubaRules.shouldWarn(700, 690, WARNING));
        assertFalse(ScubaRules.shouldWarn(605, 595, 0), "0 turns the warning off");
    }

    @Test
    void emptyOnlyOnTheBeatThatUsesTheLastAir()
    {
        assertTrue(ScubaRules.becameEmpty(5, 0));
        assertFalse(ScubaRules.becameEmpty(0, 0));
        assertFalse(ScubaRules.becameEmpty(20, 10));
    }
}
