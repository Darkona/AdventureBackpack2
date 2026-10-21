package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

class FlamethrowerBalanceTest
{
    private static final int TANKS = 2 * 4000;

    private static int simulatedLandedHits(int interval, int ticks)
    {
        int hurtResistant = 0;
        int landed = 0;
        for (int t = 0; t < ticks; t++)
        {
            if (t % interval == 0 && hurtResistant <= FlamethrowerLogic.INVULNERABLE_TICKS)
            {
                landed++;
                hurtResistant = 2 * FlamethrowerLogic.INVULNERABLE_TICKS;
            }
            if (hurtResistant > 0) hurtResistant--;
        }
        return landed;
    }

    @Test
    void theLandedPeriodMatchesVanillaInvulnerability()
    {
        for (int interval = 1; interval <= 40; interval++)
        {
            int ticks = 600;
            assertEquals(simulatedLandedHits(interval, ticks), FlamethrowerLogic.landedHits(ticks, interval), "interval " + interval);
        }
        assertEquals(12, FlamethrowerLogic.landedPeriodTicks(4), "the old interval wastes two bursts of three");
        assertEquals(10, FlamethrowerLogic.landedPeriodTicks(5));
        assertEquals(10, FlamethrowerLogic.landedPeriodTicks(1));
        assertEquals(20, FlamethrowerLogic.landedPeriodTicks(20));
    }

    @Test
    void theOldDefaultsDealtThreeAndAThirdASecondForEightySeconds()
    {
        assertEquals(3.333, FlamethrowerLogic.directDamagePerSecond(2.0, 4), 1e-3);
        assertEquals(100.0, FlamethrowerLogic.fuelPerSecond(20, 4, 1.0), 1e-9);
        assertEquals(400, FlamethrowerLogic.burstsPerFill(TANKS, 20, 1.0));
        assertEquals(80.0, FlamethrowerLogic.autonomySeconds(TANKS, 20, 4, 1.0), 1e-9);
        assertEquals(5.0, FlamethrowerLogic.afterburnDamage(5), 1e-9);
    }

    @Test
    void theNewDefaultsDealEightASecondPlusTenOfFireForEightySeconds()
    {
        double damage = ConfigHandler.FLAMETHROWER_DAMAGE;
        int interval = ConfigHandler.FLAMETHROWER_FIRE_INTERVAL_TICKS;
        int perBurst = ConfigHandler.FLAMETHROWER_FUEL_PER_BURST;
        assertEquals(8.0, FlamethrowerLogic.directDamagePerSecond(damage, interval), 1e-9);
        assertEquals(10.0, FlamethrowerLogic.afterburnDamage(ConfigHandler.FLAMETHROWER_FIRE_SECONDS), 1e-9);
        assertEquals(100.0, FlamethrowerLogic.fuelPerSecond(perBurst, interval, 1.0), 1e-9);
        assertEquals(320, FlamethrowerLogic.burstsPerFill(TANKS, perBurst, 1.0));
        assertEquals(80.0, FlamethrowerLogic.autonomySeconds(TANKS, perBurst, interval, 1.0), 1e-9);
        assertEquals(52.75, FlamethrowerLogic.autonomySeconds(TANKS, perBurst, interval, 1.5), 1e-9, "oil");
        assertEquals(10.0, FlamethrowerLogic.autonomySeconds(1000, perBurst, interval, 1.0), 1e-9, "a bucket is ten seconds");
    }

    @Test
    void aOneSecondSweepDoesMoreFireDamageThanFlameDamage()
    {
        int fireSeconds = ConfigHandler.FLAMETHROWER_FIRE_SECONDS;
        double damage = ConfigHandler.FLAMETHROWER_DAMAGE;
        int interval = ConfigHandler.FLAMETHROWER_FIRE_INTERVAL_TICKS;
        double sweep = FlamethrowerLogic.sweepDamage(damage, interval, fireSeconds, 20);
        double fire = FlamethrowerLogic.afterburnDamage(fireSeconds);
        assertEquals(18.0, sweep, 1e-9);
        assertTrue(fire > sweep - fire, "fire is most of a short sweep");
        assertTrue(FlamethrowerLogic.sweepDamage(damage, interval, fireSeconds, 40) >= 20.0, "two seconds kill a zombie");
    }

    @Test
    void itStaysNearAnIronSwordAndBelowTechguns()
    {
        double ironSwordCap = 6.0 * 20 / FlamethrowerLogic.INVULNERABLE_TICKS;
        double techguns = FlamethrowerLogic.directDamagePerSecond(5.0, 2);
        double dps = FlamethrowerLogic.directDamagePerSecond(ConfigHandler.FLAMETHROWER_DAMAGE, ConfigHandler.FLAMETHROWER_FIRE_INTERVAL_TICKS);
        assertEquals(12.0, ironSwordCap, 1e-9);
        assertEquals(10.0, techguns, 1e-9);
        assertTrue(dps >= 6.0 && dps < techguns);
    }

    @Test
    void freeFuelNeverRunsOutAndEmptyTanksDoNothing()
    {
        assertEquals(Double.POSITIVE_INFINITY, FlamethrowerLogic.autonomySeconds(TANKS, 0, 5, 1.0));
        assertEquals(0.0, FlamethrowerLogic.autonomySeconds(0, 25, 5, 1.0), 1e-9);
        assertEquals(0, FlamethrowerLogic.landedHits(0, 5));
        assertEquals(0.0, FlamethrowerLogic.afterburnDamage(-3), 1e-9);
    }
}
