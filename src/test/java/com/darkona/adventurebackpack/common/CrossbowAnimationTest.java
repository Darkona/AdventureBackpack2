package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class CrossbowAnimationTest {
    private static final int CAP = 6;
    private static final float EPS = 1e-4F;

    @Test
    void recoilStartsAtOneAndSettlesWithinRecoilTicks() {
        assertEquals(1F, CrossbowAnimation.recoil(0F), EPS);
        assertEquals(0.5F, CrossbowAnimation.recoil(CrossbowLogic.RECOIL_TICKS / 2F), EPS);
        assertEquals(0F, CrossbowAnimation.recoil(CrossbowLogic.RECOIL_TICKS), EPS);
        assertEquals(0F, CrossbowAnimation.recoil(CrossbowAnimation.NEVER), EPS);
    }

    @Test
    void sinceShotUsesThePartialTickAndIgnoresStaleOrFutureShots() {
        assertEquals(1.25F, CrossbowAnimation.sinceShot(101, 0.25F, 100), EPS);
        assertEquals(CrossbowAnimation.NEVER, CrossbowAnimation.sinceShot(100, 0F, 0), EPS);
        assertEquals(CrossbowAnimation.NEVER, CrossbowAnimation.sinceShot(100, 0F, 200), EPS);
        assertEquals(CrossbowAnimation.NEVER, CrossbowAnimation.sinceShot(1000, 0F, 100), EPS);
    }

    @Test
    void stringSnapsToTheProdOnTheShotAndReturnsToTheLatch() {
        assertEquals(CrossbowAnimation.STRING_Z_FIRED, CrossbowAnimation.stringZ(0F), EPS);
        assertEquals(CrossbowAnimation.STRING_Z_COCKED, CrossbowAnimation.stringZ(CrossbowLogic.RECOIL_TICKS), EPS);
        assertEquals(CrossbowAnimation.STRING_Z_COCKED, CrossbowAnimation.stringZ(CrossbowAnimation.NEVER), EPS);
        float half = CrossbowAnimation.stringZ(CrossbowLogic.RECOIL_TICKS / 2F);
        assertTrue(half > CrossbowAnimation.STRING_Z_FIRED && half < CrossbowAnimation.STRING_Z_COCKED);
    }

    @Test
    void drumTurnsOneStepPerShotAnimatedDuringTheRecoil() {
        assertEquals(0F, CrossbowAnimation.drumDegrees(6, CAP, CrossbowAnimation.NEVER, -1F), EPS);
        assertEquals(-60F, CrossbowAnimation.drumDegrees(5, CAP, CrossbowAnimation.NEVER, -1F), EPS);
        assertEquals(-300F, CrossbowAnimation.drumDegrees(1, CAP, CrossbowAnimation.NEVER, -1F), EPS);
        // right after the fifth shot the drum is still at the fourth step and moves to the fifth
        assertEquals(-240F, CrossbowAnimation.drumDegrees(1, CAP, 0F, -1F), EPS);
        assertEquals(-270F, CrossbowAnimation.drumDegrees(1, CAP, CrossbowLogic.RECOIL_TICKS / 2F, -1F), EPS);
        assertEquals(-300F, CrossbowAnimation.drumDegrees(1, CAP, CrossbowLogic.RECOIL_TICKS, -1F), EPS);
    }

    @Test
    void drumWrapsForMagazinesLargerThanTheDrum() {
        assertEquals(0F, CrossbowAnimation.drumDegrees(6, 12, CrossbowAnimation.NEVER, -1F), EPS);
        assertEquals(-60F, CrossbowAnimation.drumDegrees(5, 12, CrossbowAnimation.NEVER, -1F), EPS);
        assertEquals(0F, CrossbowAnimation.drumDegrees(7, 1, CrossbowAnimation.NEVER, -1F), EPS);
    }

    @Test
    void reloadSpinsTheDrumBackToZero() {
        assertEquals(-360F, CrossbowAnimation.drumDegrees(6, CAP, CrossbowAnimation.NEVER, 0F), EPS);
        assertEquals(-180F, CrossbowAnimation.drumDegrees(6, CAP, CrossbowAnimation.NEVER, 0.5F), EPS);
        assertEquals(0F, CrossbowAnimation.drumDegrees(6, CAP, CrossbowAnimation.NEVER, 1F), EPS);
    }

    @Test
    void theNextChamberToFireIsAlwaysLoadedUntilTheMagazineIsEmpty() {
        for (int ammo = CAP; ammo > 0; ammo--) {
            int shots = CAP - ammo;
            assertTrue(CrossbowAnimation.chamberLoaded(shots % 6, ammo, CAP), "ammo " + ammo);
            int loaded = 0;
            for (int c = 0; c < 6; c++) {
                if (CrossbowAnimation.chamberLoaded(c, ammo, CAP)) loaded++;
            }
            assertEquals(ammo, loaded);
        }
        for (int c = 0; c < 6; c++) {
            assertFalse(CrossbowAnimation.chamberLoaded(c, 0, CAP));
        }
    }

    @Test
    void firedChambersEmptyBehindTheDrum() {
        // two shots: chambers 0 and 1 are spent, chamber 2 is in firing position
        assertFalse(CrossbowAnimation.chamberLoaded(0, 4, CAP));
        assertFalse(CrossbowAnimation.chamberLoaded(1, 4, CAP));
        assertTrue(CrossbowAnimation.chamberLoaded(2, 4, CAP));
        assertTrue(CrossbowAnimation.chamberLoaded(5, 4, CAP));
    }

    @Test
    void bigMagazinesShowAFullDrum() {
        for (int c = 0; c < 6; c++) {
            assertTrue(CrossbowAnimation.chamberLoaded(c, 20, 24));
        }
        assertEquals(3, countLoaded(3, 24));
    }

    private static int countLoaded(int ammo, int capacity) {
        int n = 0;
        for (int c = 0; c < 6; c++) {
            if (CrossbowAnimation.chamberLoaded(c, ammo, capacity)) n++;
        }
        return n;
    }

    @Test
    void reloadProgressRunsFromTheReloadTickForReloadTicks() {
        assertEquals(-1F, CrossbowAnimation.reloadProgress(100, 0F, 0, 30), EPS);
        assertEquals(-1F, CrossbowAnimation.reloadProgress(100, 0F, 100, 0), EPS);
        assertEquals(0F, CrossbowAnimation.reloadProgress(100, 0F, 100, 30), EPS);
        assertEquals(0.5F, CrossbowAnimation.reloadProgress(115, 0F, 100, 30), EPS);
        assertEquals(29.5F / 30F, CrossbowAnimation.reloadProgress(129, 0.5F, 100, 30), EPS);
        assertEquals(-1F, CrossbowAnimation.reloadProgress(130, 0F, 100, 30), EPS);
        assertEquals(-1F, CrossbowAnimation.reloadProgress(90, 0F, 100, 30), EPS);
    }

    @Test
    void cartridgeDropsInThenTheBoltsAppear() {
        assertFalse(CrossbowAnimation.cartridgeVisible(-1F));
        assertTrue(CrossbowAnimation.boltsVisible(-1F));
        assertTrue(CrossbowAnimation.cartridgeVisible(0F));
        assertFalse(CrossbowAnimation.boltsVisible(0F));
        assertEquals(CrossbowAnimation.CARTRIDGE_DROP, CrossbowAnimation.cartridgeHeight(0F), EPS);
        assertEquals(CrossbowAnimation.CARTRIDGE_DROP / 2F, CrossbowAnimation.cartridgeHeight(CrossbowAnimation.CARTRIDGE_SEATS_AT / 2F), EPS);
        assertFalse(CrossbowAnimation.cartridgeVisible(CrossbowAnimation.CARTRIDGE_SEATS_AT));
        assertTrue(CrossbowAnimation.boltsVisible(0.9F));
        assertEquals(0F, CrossbowAnimation.cartridgeHeight(0.9F), EPS);
    }

    @Test
    void stringTransformAimsTheBoxFromTheTipToTheLatch() {
        float[] out = new float[4];
        CrossbowAnimation.stringTransform(0, 0, 0, 0, 10, 0, out);
        assertEquals(0F, out[0], EPS);
        assertEquals(10F, out[3], EPS);
        CrossbowAnimation.stringTransform(0, 0, 0, 3, 0, 4, out);
        assertEquals(90F, out[0], EPS);
        assertEquals(5F, out[3], EPS);
        assertEquals(4F, out[1], EPS);
        assertEquals(-3F, out[2], EPS);
        CrossbowAnimation.stringTransform(CrossbowAnimation.TIP_X, CrossbowAnimation.TIP_Y, CrossbowAnimation.TIP_Z, 0, CrossbowAnimation.STRING_Y, CrossbowAnimation.STRING_Z_COCKED, out);
        assertTrue(out[3] > CrossbowAnimation.TIP_X && out[3] < CrossbowAnimation.TIP_X + 3F);
    }
}
