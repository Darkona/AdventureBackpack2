package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.CrossbowLogic.Action;
import org.junit.jupiter.api.Test;

class CrossbowLogicTest {
    private static final int CAP = 6;
    private static final int MAX_WAIT = 30;

    @Test
    void firesWhenLoadedAndReady() {
        assertEquals(Action.FIRE, CrossbowLogic.decide(6, CAP, 100, 0, MAX_WAIT, false, false));
        assertEquals(Action.FIRE, CrossbowLogic.decide(1, CAP, 100, 100, MAX_WAIT, false, false));
    }

    @Test
    void waitsDuringThePauseAfterAShot() {
        assertEquals(Action.WAIT, CrossbowLogic.decide(5, CAP, 100, 104, MAX_WAIT, false, true));
        assertEquals(Action.WAIT, CrossbowLogic.decide(0, CAP, 100, 101, MAX_WAIT, true, true));
    }

    @Test
    void heldClicksEveryFourTicksFireSixBoltsWithTheDefaultPause() {
        int ammo = CAP;
        long readyAt = 0;
        int shots = 0;
        for (long t = 0; t < 40; t += 4) {
            if (CrossbowLogic.decide(ammo, CAP, t, readyAt, MAX_WAIT, false, false) == Action.FIRE) {
                ammo--;
                readyAt = t + 4;
                shots++;
            }
        }
        assertEquals(CAP, shots);
        assertEquals(0, ammo);
    }

    @Test
    void aPauseLongerThanTheClickRepeatSkipsAClick() {
        // shotDelayTicks = 5 with clicks every 4 ticks: shots at 0, 8, 16.
        long readyAt = 0;
        int ammo = CAP;
        StringBuilder fired = new StringBuilder();
        for (long t = 0; t <= 16; t += 4) {
            if (CrossbowLogic.decide(ammo, CAP, t, readyAt, MAX_WAIT, false, false) == Action.FIRE) {
                ammo--;
                readyAt = t + 5;
                fired.append(t).append(' ');
            }
        }
        assertEquals("0 8 16 ", fired.toString());
    }

    @Test
    void emptyMagazineReloadsWhenACartridgeIsThere() {
        assertEquals(Action.RELOAD, CrossbowLogic.decide(0, CAP, 100, 0, MAX_WAIT, false, true));
    }

    @Test
    void emptyMagazineWithoutCartridgeDryFires() {
        assertEquals(Action.DRY, CrossbowLogic.decide(0, CAP, 100, 0, MAX_WAIT, false, false));
    }

    @Test
    void sneakSwapsAPartialMagazineOnly() {
        assertEquals(Action.RELOAD, CrossbowLogic.decide(3, CAP, 100, 0, MAX_WAIT, true, true));
        assertEquals(Action.NONE, CrossbowLogic.decide(CAP, CAP, 100, 0, MAX_WAIT, true, true));
        assertEquals(Action.NONE, CrossbowLogic.decide(3, CAP, 100, 0, MAX_WAIT, true, false));
    }

    @Test
    void staleReadyTimeFromAnotherWorldIsIgnored() {
        assertFalse(CrossbowLogic.isWaiting(100, 100000, MAX_WAIT));
        assertEquals(Action.FIRE, CrossbowLogic.decide(6, CAP, 100, 100000, MAX_WAIT, false, false));
        assertTrue(CrossbowLogic.isWaiting(100, 130, MAX_WAIT));
        assertFalse(CrossbowLogic.isWaiting(100, 131, MAX_WAIT));
    }

    @Test
    void recoilLastsFourTicksAfterAShot() {
        assertTrue(CrossbowLogic.isRecoiling(100, 100));
        assertTrue(CrossbowLogic.isRecoiling(103, 100));
        assertFalse(CrossbowLogic.isRecoiling(104, 100));
        assertFalse(CrossbowLogic.isRecoiling(99, 100));
        assertFalse(CrossbowLogic.isRecoiling(2, 0));
    }

    @Test
    void damageFactorGivesTheMuzzleDamage() {
        double factor = CrossbowLogic.damageFactor(4.0, 3.0);
        assertEquals(4, (int) Math.ceil(3.0 * factor));
        assertEquals(4, (int) Math.ceil(3.0001f * factor));
        assertEquals(4, (int) Math.ceil(2.97 * factor));
        assertEquals(3, (int) Math.ceil(2.2 * factor));
    }

    @Test
    void ammoStaysInsideTheMagazine() {
        assertEquals(0, CrossbowLogic.clampAmmo(-2, CAP));
        assertEquals(4, CrossbowLogic.clampAmmo(4, CAP));
        assertEquals(CAP, CrossbowLogic.clampAmmo(9, CAP));
    }

    @Test
    void zeroDurabilityNeverBreaks() {
        assertFalse(CrossbowLogic.breaks(1000000, 0));
        assertFalse(CrossbowLogic.breaks(9, 10));
        assertTrue(CrossbowLogic.breaks(10, 10));
    }

    @Test
    void outOfAmmoMessageIsNotSpammed() {
        assertTrue(CrossbowLogic.shouldWarnEmpty(100, 0, 20));
        assertFalse(CrossbowLogic.shouldWarnEmpty(104, 100, 20));
        assertTrue(CrossbowLogic.shouldWarnEmpty(121, 100, 20));
        assertTrue(CrossbowLogic.shouldWarnEmpty(50, 100, 20));
    }

    @Test
    void aShotThatCostsSeveralBoltsNeedsThemAll() {
        assertEquals(Action.FIRE, CrossbowLogic.decide(2, 2, CAP, 100, 0, MAX_WAIT, false, false));
        assertEquals(Action.DRY, CrossbowLogic.decide(1, 2, CAP, 100, 0, MAX_WAIT, false, false));
        assertEquals(Action.RELOAD, CrossbowLogic.decide(1, 2, CAP, 100, 0, MAX_WAIT, false, true));
    }

    @Test
    void aFreeShotFiresWithAnEmptyMagazine() {
        assertEquals(Action.FIRE, CrossbowLogic.decide(0, 0, CAP, 100, 0, MAX_WAIT, false, false));
    }

    @Test
    void shotCostStaysBetweenZeroAndAFullMagazine() {
        assertEquals(0, CrossbowLogic.shotCost(-3, CAP));
        assertEquals(1, CrossbowLogic.shotCost(1, CAP));
        assertEquals(CAP, CrossbowLogic.shotCost(24, CAP));
        assertEquals(Action.FIRE, CrossbowLogic.decide(CAP, CrossbowLogic.shotCost(24, CAP), CAP, 100, 0, MAX_WAIT, false, false));
    }

    @Test
    void unknownOrDisabledModesReadAsRepeating() {
        assertEquals(CrossbowLogic.MODE_GRAPPLE, CrossbowLogic.effectiveMode(CrossbowLogic.MODE_GRAPPLE, true, true));
        assertEquals(CrossbowLogic.MODE_SNIPER, CrossbowLogic.effectiveMode(CrossbowLogic.MODE_SNIPER, true, true));
        assertEquals(CrossbowLogic.MODE_REPEATING, CrossbowLogic.effectiveMode(CrossbowLogic.MODE_GRAPPLE, false, true));
        assertEquals(CrossbowLogic.MODE_REPEATING, CrossbowLogic.effectiveMode(CrossbowLogic.MODE_SNIPER, true, false));
        assertEquals(CrossbowLogic.MODE_REPEATING, CrossbowLogic.effectiveMode(-1, true, true));
        assertEquals(CrossbowLogic.MODE_REPEATING, CrossbowLogic.effectiveMode(99, true, true));
    }

    @Test
    void theModeKeyCyclesThroughTheEnabledModesOnly() {
        assertEquals(CrossbowLogic.MODE_GRAPPLE, CrossbowLogic.nextMode(CrossbowLogic.MODE_REPEATING, true, true));
        assertEquals(CrossbowLogic.MODE_SNIPER, CrossbowLogic.nextMode(CrossbowLogic.MODE_GRAPPLE, true, true));
        assertEquals(CrossbowLogic.MODE_REPEATING, CrossbowLogic.nextMode(CrossbowLogic.MODE_SNIPER, true, true));
        assertEquals(CrossbowLogic.MODE_SNIPER, CrossbowLogic.nextMode(CrossbowLogic.MODE_REPEATING, false, true));
        assertEquals(CrossbowLogic.MODE_REPEATING, CrossbowLogic.nextMode(CrossbowLogic.MODE_GRAPPLE, true, false));
        assertEquals(CrossbowLogic.MODE_REPEATING, CrossbowLogic.nextMode(CrossbowLogic.MODE_REPEATING, false, false));
        assertEquals(CrossbowLogic.MODE_GRAPPLE, CrossbowLogic.nextMode(42, true, true));
    }

    @Test
    void theBipodIsDownOnlySneakingStillOnTheGround() {
        assertTrue(CrossbowLogic.bipodDeployed(true, true, false, 0, 0, 0.02));
        assertTrue(CrossbowLogic.bipodDeployed(true, true, false, 0.01, -0.01, 0.02));
        assertFalse(CrossbowLogic.bipodDeployed(false, true, false, 0, 0, 0.02));
        assertFalse(CrossbowLogic.bipodDeployed(true, false, false, 0, 0, 0.02));
        assertFalse(CrossbowLogic.bipodDeployed(true, true, true, 0, 0, 0.02));
        // Sneak walking (about 0.065 blocks per tick) folds it.
        assertFalse(CrossbowLogic.bipodDeployed(true, true, false, 0.065, 0, 0.02));
    }

    @Test
    void withTheBipodDownSneakAndClickFires() {
        // ItemCrossbow passes sneaking = false while the bipod is down.
        assertEquals(Action.FIRE, CrossbowLogic.decide(3, 1, CAP, 100, 0, MAX_WAIT, false, true));
        assertEquals(Action.RELOAD, CrossbowLogic.decide(3, 1, CAP, 100, 0, MAX_WAIT, true, true));
    }
}
