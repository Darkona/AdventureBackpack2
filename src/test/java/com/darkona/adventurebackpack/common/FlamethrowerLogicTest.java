package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

class FlamethrowerLogicTest
{
    // defaults of the flamethrower keys
    private static final double RANGE = 6.0;
    private static final double CONE = 25.0;
    private static final int PER_BURST = 50;
    private static final int INTERVAL = 4;
    private static final double COS = FlamethrowerLogic.cosHalfAngle(CONE);

    @Test
    void defaultsInTheConfigFieldsMatchTheseNumbers()
    {
        assertEquals(RANGE, ConfigHandler.FLAMETHROWER_RANGE, 1e-9);
        assertEquals(CONE, ConfigHandler.FLAMETHROWER_CONE_DEGREES, 1e-9);
        assertEquals(PER_BURST, ConfigHandler.FLAMETHROWER_FUEL_PER_BURST);
        assertEquals(INTERVAL, ConfigHandler.FLAMETHROWER_FIRE_INTERVAL_TICKS);
        assertEquals(2.0, ConfigHandler.FLAMETHROWER_DAMAGE, 1e-9);
        assertFalse(ConfigHandler.FLAMETHROWER_PVP);
        assertFalse(ConfigHandler.FLAMETHROWER_IGNITES_BLOCKS);
    }

    // ---------------------------------------------------------------- the cone

    @Test
    void aTargetStraightAheadInRangeIsHit()
    {
        assertTrue(FlamethrowerLogic.inCone(0, 0, 1, 0, 0, 3, RANGE, COS));
        assertTrue(FlamethrowerLogic.inCone(0, 0, 1, 0, 0, 6, RANGE, COS), "exactly at the range");
    }

    @Test
    void aTargetFartherThanTheRangeIsMissed()
    {
        assertFalse(FlamethrowerLogic.inCone(0, 0, 1, 0, 0, 6.01, RANGE, COS));
        assertFalse(FlamethrowerLogic.inCone(0, 0, 1, 1, 0, 6, RANGE, COS));
    }

    @Test
    void aTargetToTheSideOrBehindIsMissed()
    {
        assertFalse(FlamethrowerLogic.inCone(0, 0, 1, 3, 0, 0, RANGE, COS), "to the side");
        assertFalse(FlamethrowerLogic.inCone(0, 0, 1, 0, 0, -3, RANGE, COS), "behind");
        assertFalse(FlamethrowerLogic.inCone(0, 0, 1, 0, 3, 0.1, RANGE, COS), "above");
    }

    @Test
    void theEdgeOfTheConeIsHalfTheOpening()
    {
        double inside = Math.toRadians(12.0);
        double outside = Math.toRadians(13.0);
        assertTrue(FlamethrowerLogic.inCone(0, 0, 1, Math.sin(inside) * 4, 0, Math.cos(inside) * 4, RANGE, COS));
        assertFalse(FlamethrowerLogic.inCone(0, 0, 1, Math.sin(outside) * 4, 0, Math.cos(outside) * 4, RANGE, COS));
    }

    @Test
    void theLookNeedsNoUnitLengthAndWorksInAnyDirection()
    {
        // looking down and west (not normalised)
        assertTrue(FlamethrowerLogic.inCone(-2, -2, 0, -2, -2, 0, RANGE, COS));
        assertFalse(FlamethrowerLogic.inCone(-2, -2, 0, 2, 2, 0, RANGE, COS));
        assertFalse(FlamethrowerLogic.inCone(0, 0, 0, 0, 0, 1, RANGE, COS), "no direction");
    }

    @Test
    void aWiderConeReachesFartherToTheSide()
    {
        double wide = FlamethrowerLogic.cosHalfAngle(90.0);
        assertTrue(FlamethrowerLogic.inCone(0, 0, 1, 2.9, 0, 3, RANGE, wide));
        assertFalse(FlamethrowerLogic.inCone(0, 0, 1, 2.9, 0, 3, RANGE, COS));
    }

    // ---------------------------------------------------------------- cost and damage

    @Test
    void oneBurstCostsFiftyWithTheDefaultsAndScalesWithTheFuel()
    {
        assertEquals(50, FlamethrowerLogic.fuelPerBurst(PER_BURST, 1.0));
        assertEquals(75, FlamethrowerLogic.fuelPerBurst(PER_BURST, 1.5), "oil");
        assertEquals(60, FlamethrowerLogic.fuelPerBurst(PER_BURST, 1.2f), "bioethanol, a float multiplier");
        assertEquals(40, FlamethrowerLogic.fuelPerBurst(PER_BURST, 0.8f), "liquid coal");
        assertEquals(0, FlamethrowerLogic.fuelPerBurst(0, 1.0), "free fuel");
    }

    @Test
    void twoFullTanksMakeOneHundredSixtyBurstsOfThirtyTwoSeconds()
    {
        int fuel = 2 * 4000;
        int bursts = 0;
        while (FlamethrowerLogic.canFire(true, true, fuel, 50))
        {
            fuel -= FlamethrowerLogic.fuelTaken(fuel, 50);
            bursts++;
        }
        assertEquals(160, bursts);
        assertEquals(32, bursts * INTERVAL / 20);
    }

    @Test
    void theLastDropsMakeOneMoreBurstAndNeverGoNegative()
    {
        assertTrue(FlamethrowerLogic.canFire(true, true, 10, 50));
        assertEquals(10, FlamethrowerLogic.fuelTaken(10, 50));
        assertEquals(0, FlamethrowerLogic.fuelTaken(0, 50));
        assertFalse(FlamethrowerLogic.canFire(true, true, 0, 50));
    }

    @Test
    void itOnlyFiresEnabledAndLit()
    {
        assertFalse(FlamethrowerLogic.canFire(false, true, 1000, 50));
        assertFalse(FlamethrowerLogic.canFire(true, false, 1000, 50));
        assertTrue(FlamethrowerLogic.canFire(true, true, 0, 0), "free fuel fires with empty tanks");
    }

    @Test
    void damageIsTheConfiguredValueNeverNegative()
    {
        assertEquals(2.0F, FlamethrowerLogic.damagePerBurst(2.0), 1e-6);
        assertEquals(0.0F, FlamethrowerLogic.damagePerBurst(-1.0), 1e-6);
    }

    @Test
    void thePilotIsFreeByDefault()
    {
        assertEquals(0, FlamethrowerLogic.pilotFuelPerBeat(0, 1.5));
        assertEquals(15, FlamethrowerLogic.pilotFuelPerBeat(10, 1.5));
        assertEquals(12, FlamethrowerLogic.pilotFuelPerBeat(10, 1.2f));
    }

    // ---------------------------------------------------------------- who and what burns

    @Test
    void theWearerIsNeverHurt()
    {
        assertFalse(FlamethrowerLogic.mayHurt(true, false, true, true, true));
        assertFalse(FlamethrowerLogic.mayHurt(true, false, false, true, true));
    }

    @Test
    void playersAreSafeWithoutPvp()
    {
        assertFalse(FlamethrowerLogic.mayHurt(false, false, true, false, true));
        assertFalse(FlamethrowerLogic.mayHurt(false, false, true, true, false), "the server or the teams forbid it");
        assertTrue(FlamethrowerLogic.mayHurt(false, false, true, true, true));
    }

    @Test
    void mobsBurnButTheWearersPetsDoNot()
    {
        assertTrue(FlamethrowerLogic.mayHurt(false, false, false, false, false));
        assertFalse(FlamethrowerLogic.mayHurt(false, true, false, true, true));
    }

    @Test
    void blocksNeverBurnWithIgnitesBlocksOff()
    {
        for (int bits = 0; bits < 8; bits++)
        {
            assertFalse(FlamethrowerLogic.mayIgnite(false, (bits & 1) != 0, (bits & 2) != 0, (bits & 4) != 0));
        }
    }

    @Test
    void withIgnitesBlocksOnTheSpotMustBeFreeUnprotectedAndEditable()
    {
        assertTrue(FlamethrowerLogic.mayIgnite(true, true, false, true));
        assertFalse(FlamethrowerLogic.mayIgnite(true, false, false, true), "occupied");
        assertFalse(FlamethrowerLogic.mayIgnite(true, true, true, true), "spawn protection");
        assertFalse(FlamethrowerLogic.mayIgnite(true, true, false, false), "adventure mode or no permission");
    }
}
