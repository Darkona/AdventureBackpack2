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
    void spentMagazineComesUpOutOfTheSlotThenFliesOffToTheRight() {
        float[] pose = new float[CrossbowAnimation.MAG_SIZE];
        assertFalse(CrossbowAnimation.spentMagazine(-1F, pose));
        assertTrue(CrossbowAnimation.spentMagazine(0F, pose));
        // starts inside the drum, in the slot column, full size
        assertEquals(-CrossbowAnimation.MAGAZINE_HEIGHT_EMPTY, pose[CrossbowAnimation.MAG_Y], EPS);
        assertEquals(0F, pose[CrossbowAnimation.MAG_X], EPS);
        assertEquals(1F, pose[CrossbowAnimation.MAG_CLIPPED], EPS);
        assertEquals(1F, pose[CrossbowAnimation.MAG_SCALE], EPS);
        // a third of the way it is clear of the slot (bottom above it) and still straight above it
        CrossbowAnimation.spentMagazine(CrossbowAnimation.MAGAZINE_EJECTED_AT * 0.34F, pose);
        assertTrue(pose[CrossbowAnimation.MAG_Y] > 0F);
        assertEquals(0F, pose[CrossbowAnimation.MAG_X], EPS);
        // it leaves the slot column only once clear, so the clip plane never cuts it beside the drum
        CrossbowAnimation.spentMagazine(CrossbowAnimation.MAGAZINE_EJECTED_AT * 0.36F, pose);
        assertTrue(pose[CrossbowAnimation.MAG_X] > 0F);
        assertTrue(pose[CrossbowAnimation.MAG_Y] > 0F);
        assertEquals(0F, pose[CrossbowAnimation.MAG_CLIPPED], EPS);
        // ends low on the right, rolled, shrunk to nothing
        CrossbowAnimation.spentMagazine(CrossbowAnimation.MAGAZINE_EJECTED_AT - 1e-6F, pose);
        assertEquals(CrossbowAnimation.EJECT_SIDE, pose[CrossbowAnimation.MAG_X], 1e-3F);
        assertEquals(CrossbowAnimation.EJECT_LOW_Y, pose[CrossbowAnimation.MAG_Y], 1e-3F);
        assertEquals(-CrossbowAnimation.EJECT_ROLL, pose[CrossbowAnimation.MAG_ROLL], 1e-3F);
        assertEquals(0F, pose[CrossbowAnimation.MAG_SCALE], 1e-3F);
        assertFalse(CrossbowAnimation.spentMagazine(CrossbowAnimation.MAGAZINE_EJECTED_AT, pose));
    }

    @Test
    void newMagazineRisesFromTheLeftHandAndGoesInThroughTheSlot() {
        float[] pose = new float[CrossbowAnimation.MAG_SIZE];
        assertFalse(CrossbowAnimation.newMagazine(0F, pose));
        assertTrue(CrossbowAnimation.newMagazine(CrossbowAnimation.MAGAZINE_EJECTED_AT, pose));
        assertEquals(CrossbowAnimation.INSERT_FROM_X, pose[CrossbowAnimation.MAG_X], EPS);
        assertEquals(CrossbowAnimation.INSERT_FROM_Y, pose[CrossbowAnimation.MAG_Y], EPS);
        assertEquals(1F, pose[CrossbowAnimation.MAG_SCALE], EPS);
        // beside the drum it is never cut by the slot plane; once cut it is straight over the slot and above it
        boolean wasClipped = false;
        for (float p = CrossbowAnimation.MAGAZINE_EJECTED_AT; p < CrossbowAnimation.MAGAZINE_SEATS_AT; p += 0.005F) {
            CrossbowAnimation.newMagazine(p, pose);
            boolean clipped = pose[CrossbowAnimation.MAG_CLIPPED] > 0F;
            if (clipped && !wasClipped) {
                assertEquals(CrossbowAnimation.INSERT_OVER_Y, pose[CrossbowAnimation.MAG_Y], 0.05F);
            }
            if (clipped) {
                assertEquals(0F, pose[CrossbowAnimation.MAG_X], EPS);
            } else {
                assertFalse(wasClipped);
            }
            wasClipped = clipped;
        }
        assertTrue(wasClipped);
        CrossbowAnimation.newMagazine(CrossbowAnimation.MAGAZINE_SEATS_AT - 1e-6F, pose);
        assertEquals(-CrossbowAnimation.MAGAZINE_HEIGHT_LOADED, pose[CrossbowAnimation.MAG_Y], 1e-3F);
        assertFalse(CrossbowAnimation.newMagazine(CrossbowAnimation.MAGAZINE_SEATS_AT, pose));
        assertFalse(CrossbowAnimation.newMagazine(-1F, pose));
    }

    @Test
    void drumShowsItsBoltsOnceTheNewMagazineIsIn() {
        assertTrue(CrossbowAnimation.boltsVisible(-1F));
        assertFalse(CrossbowAnimation.boltsVisible(0F));
        assertFalse(CrossbowAnimation.boltsVisible(CrossbowAnimation.MAGAZINE_SEATS_AT - 0.01F));
        assertTrue(CrossbowAnimation.boltsVisible(CrossbowAnimation.MAGAZINE_SEATS_AT));
        assertTrue(CrossbowAnimation.boltsVisible(0.9F));
    }

    @Test
    void newMagazineCarriesAtMostThreeBolts() {
        assertEquals(0, CrossbowAnimation.magazineBolts(0));
        assertEquals(2, CrossbowAnimation.magazineBolts(2));
        assertEquals(3, CrossbowAnimation.magazineBolts(24));
        assertEquals(0, CrossbowAnimation.magazineBolts(-1));
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

    @Test
    void firingChamberFollowsTheShots() {
        assertEquals(0, CrossbowAnimation.firingChamber(6, CAP));
        assertEquals(1, CrossbowAnimation.firingChamber(5, CAP));
        assertEquals(5, CrossbowAnimation.firingChamber(1, CAP));
        assertEquals(0, CrossbowAnimation.firingChamber(0, CAP));
        assertEquals(0, CrossbowAnimation.firingChamber(9, CAP));
    }

    @Test
    void bipodLegsLieBackWhenFoldedAndHangSplayedWhenDown() {
        assertEquals(CrossbowAnimation.LEG_FOLDED_X, CrossbowAnimation.legAngleX(0F), EPS);
        assertEquals(0F, CrossbowAnimation.legAngleX(1F), EPS);
        assertEquals(CrossbowAnimation.LEG_FOLDED_X / 2F, CrossbowAnimation.legAngleX(0.5F), EPS);
        assertEquals(0F, CrossbowAnimation.legAngleZ(0F, true), EPS);
        assertEquals(CrossbowAnimation.LEG_SPLAY, CrossbowAnimation.legAngleZ(1F, true), EPS);
        assertEquals(-CrossbowAnimation.LEG_SPLAY, CrossbowAnimation.legAngleZ(1F, false), EPS);
        assertEquals(0F, CrossbowAnimation.legAngleX(2F), EPS);
        assertEquals(CrossbowAnimation.LEG_FOLDED_X, CrossbowAnimation.legAngleX(-1F), EPS);
    }

    @Test
    void bipodPoseMovesLinearlyToItsTargetAndHolds() {
        assertEquals(0F, CrossbowAnimation.bipodPose(0F, true, 0F), EPS);
        assertEquals(0.5F, CrossbowAnimation.bipodPose(0F, true, CrossbowAnimation.BIPOD_TICKS / 2F), EPS);
        assertEquals(1F, CrossbowAnimation.bipodPose(0F, true, CrossbowAnimation.BIPOD_TICKS), EPS);
        assertEquals(1F, CrossbowAnimation.bipodPose(0F, true, 1000F), EPS);
        assertEquals(0.75F, CrossbowAnimation.bipodPose(1F, false, CrossbowAnimation.BIPOD_TICKS / 4F), EPS);
        assertEquals(0F, CrossbowAnimation.bipodPose(0.3F, false, CrossbowAnimation.BIPOD_TICKS), EPS);
        assertEquals(0.3F, CrossbowAnimation.bipodPose(0.3F, false, -5F), EPS);
    }

    private static float[] hold(float pitch, float yaw, boolean steady, float recoil, float age) {
        float[] out = new float[CrossbowAnimation.POSE_SIZE];
        CrossbowAnimation.holdPose(pitch, yaw, steady, recoil, age, out);
        return out;
    }

    @Test
    void holdRaisesBothArmsAlongTheLookAndTheWeaponPointsWhereTheHeadLooks() {
        float halfPi = (float) (Math.PI / 2D);
        float[] level = hold(0F, 0F, false, 0F, 0F);
        assertEquals(-halfPi - CrossbowAnimation.HOLD_RAISE, level[CrossbowAnimation.POSE_RIGHT_X], EPS);
        assertEquals(level[CrossbowAnimation.POSE_RIGHT_X], level[CrossbowAnimation.POSE_LEFT_X], EPS);
        assertEquals(0F, level[CrossbowAnimation.POSE_WEAPON_PITCH], EPS);
        assertEquals(0F, level[CrossbowAnimation.POSE_KICK_BACK], EPS);

        float[] down = hold(0.5F, 0.3F, false, 0F, 0F);
        assertEquals(0.5F, down[CrossbowAnimation.POSE_RIGHT_X] - level[CrossbowAnimation.POSE_RIGHT_X], EPS);
        assertEquals(0.5F, down[CrossbowAnimation.POSE_LEFT_X] - level[CrossbowAnimation.POSE_LEFT_X], EPS);
        assertEquals(0.5F, down[CrossbowAnimation.POSE_WEAPON_PITCH], EPS);
        assertEquals(0.3F, down[CrossbowAnimation.POSE_WEAPON_YAW], EPS);
    }

    @Test
    void holdTurnsTheRightArmInToTheGripAndTheLeftArmAcrossToTheTiller() {
        float[] pose = hold(0F, 0.4F, false, 0F, 0F);
        assertEquals(0.4F + CrossbowAnimation.HOLD_RIGHT_YAW, pose[CrossbowAnimation.POSE_RIGHT_Y], EPS);
        assertEquals(0.4F + CrossbowAnimation.HOLD_LEFT_YAW, pose[CrossbowAnimation.POSE_LEFT_Y], EPS);
        assertTrue(CrossbowAnimation.HOLD_RIGHT_YAW < 0F, "right arm turns in (negative Y)");
        assertTrue(CrossbowAnimation.HOLD_LEFT_YAW > 0F, "left arm turns in (positive Y)");
        assertTrue(pose[CrossbowAnimation.POSE_LEFT_SHOULDER_Z] < 0F, "left shoulder comes forward");
        assertTrue(pose[CrossbowAnimation.POSE_RIGHT_SHOULDER_Z] > 0F, "right shoulder goes back");
    }

    @Test
    void sniperOnTheBipodHoldsLowerAndStillWithTheWeaponOnTheLook() {
        float[] free = hold(0.2F, 0F, false, 0F, 0F);
        for (float age = 0F; age < 200F; age += 7.3F) {
            float[] steady = hold(0.2F, 0F, true, 0F, age);
            assertEquals(-(float) (Math.PI / 2D) - CrossbowAnimation.HOLD_RAISE + 0.2F + CrossbowAnimation.SNIPER_LOWER,
                    steady[CrossbowAnimation.POSE_RIGHT_X], EPS);
            assertEquals(0F, steady[CrossbowAnimation.POSE_RIGHT_Z], EPS);
            assertEquals(0F, steady[CrossbowAnimation.POSE_LEFT_Z], EPS);
            assertEquals(0.2F, steady[CrossbowAnimation.POSE_WEAPON_PITCH], EPS);
        }
        assertTrue(free[CrossbowAnimation.POSE_RIGHT_X] < hold(0.2F, 0F, true, 0F, 0F)[CrossbowAnimation.POSE_RIGHT_X]);
    }

    @Test
    void idleSwayIsSmall() {
        for (float age = 0F; age < 500F; age += 3.7F) {
            float[] pose = hold(0F, 0F, false, 0F, age);
            assertTrue(Math.abs(pose[CrossbowAnimation.POSE_RIGHT_Z]) <= CrossbowAnimation.SWAY + EPS);
            assertTrue(Math.abs(pose[CrossbowAnimation.POSE_WEAPON_PITCH]) <= CrossbowAnimation.SWAY + EPS);
        }
    }

    @Test
    void aShotKicksTheArmsAndMuzzleUpAndTheWeaponBackThenSettles() {
        float[] rest = hold(0F, 0F, true, 0F, 0F);
        float[] shot = hold(0F, 0F, true, 1F, 0F);
        assertEquals(-CrossbowAnimation.KICK_ARMS, shot[CrossbowAnimation.POSE_RIGHT_X] - rest[CrossbowAnimation.POSE_RIGHT_X], EPS);
        assertEquals(-(CrossbowAnimation.KICK_ARMS + CrossbowAnimation.KICK_PITCH), shot[CrossbowAnimation.POSE_WEAPON_PITCH], EPS);
        assertEquals(CrossbowAnimation.KICK_BACK, shot[CrossbowAnimation.POSE_KICK_BACK], EPS);
        float[] over = hold(0F, 0F, true, 3F, 0F);
        assertEquals(CrossbowAnimation.KICK_BACK, over[CrossbowAnimation.POSE_KICK_BACK], EPS);
        float[] half = hold(0F, 0F, true, 0.5F, 0F);
        assertEquals(CrossbowAnimation.KICK_BACK / 2F, half[CrossbowAnimation.POSE_KICK_BACK], EPS);
    }

    /** Normalises an angle to [0, 360). */
    private static float norm(float degrees)
    {
        float d = degrees % 360F;
        return d < 0 ? d + 360F : d;
    }

    @Test
    void fedFromTheQuiverTheDrumStaysFullAndTurnsOneChamberPerBolt()
    {
        int ammo = CrossbowAnimation.feedAmmo();
        for (int chamber = 0; chamber < CrossbowAnimation.CHAMBERS; chamber++)
        {
            assertTrue(CrossbowAnimation.chamberLoaded(chamber, ammo, CrossbowAnimation.feedCapacity(7)), "every chamber drawn loaded");
        }
        assertEquals(0F, CrossbowAnimation.drumDegrees(ammo, CrossbowAnimation.feedCapacity(0), CrossbowAnimation.NEVER, -1F), EPS);
        for (int shots = 1; shots <= 13; shots++)
        {
            int capacity = CrossbowAnimation.feedCapacity(shots);
            // Right after the shot (recoil 1) the drum is where the previous shot left it...
            float start = CrossbowAnimation.drumDegrees(ammo, capacity, 0F, -1F);
            float before = CrossbowAnimation.drumDegrees(ammo, CrossbowAnimation.feedCapacity(shots - 1), CrossbowAnimation.NEVER, -1F);
            assertEquals(norm(before), norm(start), 0.01F, "no jump at shot " + shots);
            // ...and when the recoil is over it is one chamber further
            float end = CrossbowAnimation.drumDegrees(ammo, capacity, CrossbowAnimation.NEVER, -1F);
            assertEquals(norm(start - CrossbowAnimation.STEP_DEGREES), norm(end), 0.01F, "one step at shot " + shots);
        }
        assertEquals(CrossbowAnimation.firingChamber(ammo, CrossbowAnimation.feedCapacity(8)), 2);
    }

    @Test
    void drumTurnFollowsThePauseOfEachShot()
    {
        assertEquals(CrossbowLogic.RECOIL_TICKS, CrossbowAnimation.drumTurnTicks(4, false, 1F), EPS, "magazine shot: as before");
        assertEquals(CrossbowLogic.RECOIL_TICKS, CrossbowAnimation.drumTurnTicks(12, false, 1F), EPS, "slow magazine: the usual quick turn");
        assertEquals(3F, CrossbowAnimation.drumTurnTicks(3, false, 1F), EPS, "gatling: done by the next shot");
        assertEquals(2F, CrossbowAnimation.drumTurnTicks(2, false, 1F), EPS, "gatling with the Skeleton bonus");
        assertEquals(48F, CrossbowAnimation.drumTurnTicks(48, true, 1F), EPS, "sniper: the whole pause");
        assertEquals(24F, CrossbowAnimation.drumTurnTicks(48, true, 2F), EPS, "sniper drumSpeed 2: half of it");
        assertEquals(CrossbowLogic.RECOIL_TICKS, CrossbowAnimation.drumTurnTicks(0, true, 1F), EPS, "no pause known");
    }

    @Test
    void drumTurnLeftRunsFromOneToZero()
    {
        assertEquals(1F, CrossbowAnimation.drumTurnLeft(100, 0F, 100, 48F), EPS);
        assertEquals(0.5F, CrossbowAnimation.drumTurnLeft(124, 0F, 100, 48F), EPS);
        assertEquals(0F, CrossbowAnimation.drumTurnLeft(148, 0F, 100, 48F), EPS);
        assertEquals(0F, CrossbowAnimation.drumTurnLeft(100, 0F, 0, 48F), EPS, "never fired");
        assertEquals(0F, CrossbowAnimation.drumTurnLeft(90, 0F, 100, 48F), EPS, "clock went back");
        assertEquals(CrossbowAnimation.drumDegrees(1, CAP, 2F, -1F), CrossbowAnimation.drumAt(1, CAP, CrossbowAnimation.drumTurnLeft(102, 0F, 100, CrossbowLogic.RECOIL_TICKS), -1F), EPS, "same as the recoil turn");
    }

    @Test
    void gatlingDrumNeverJumpsBetweenShots()
    {
        double exact = QuiverFeedRules.exactInterval(3, 0.30);
        double carry = 0;
        long lastShot = 1000;
        int quiverShots = 1;
        for (int i = 0; i < 40; i++)
        {
            int pause = QuiverFeedRules.ticksFor(carry, exact);
            carry = QuiverFeedRules.carryAfter(carry, exact);
            float turn = CrossbowAnimation.drumTurnTicks(pause, false, 1F);
            int cap = CrossbowAnimation.feedCapacity(quiverShots);
            float before = CrossbowAnimation.drumAt(CrossbowAnimation.feedAmmo(), cap, CrossbowAnimation.drumTurnLeft(lastShot + pause, 0F, lastShot, turn), -1F);
            long next = lastShot + pause;
            quiverShots++;
            int nextCap = CrossbowAnimation.feedCapacity(quiverShots);
            float after = CrossbowAnimation.drumAt(CrossbowAnimation.feedAmmo(), nextCap, CrossbowAnimation.drumTurnLeft(next, 0F, next, turn), -1F);
            assertEquals(norm(before), norm(after), 0.01F, "no jump at shot " + i);
            lastShot = next;
        }
    }

    @Test
    void sniperDrumTurnsSlowlyAcrossThePause()
    {
        float turn = CrossbowAnimation.drumTurnTicks(48, true, 1F);
        float start = CrossbowAnimation.drumAt(5, CAP, CrossbowAnimation.drumTurnLeft(0 + 100, 0F, 100, turn), -1F);
        float mid = CrossbowAnimation.drumAt(5, CAP, CrossbowAnimation.drumTurnLeft(124, 0F, 100, turn), -1F);
        float end = CrossbowAnimation.drumAt(5, CAP, CrossbowAnimation.drumTurnLeft(148, 0F, 100, turn), -1F);
        assertEquals(0F, start, EPS);
        assertEquals(-30F, mid, EPS);
        assertEquals(-60F, end, EPS);
    }
}
