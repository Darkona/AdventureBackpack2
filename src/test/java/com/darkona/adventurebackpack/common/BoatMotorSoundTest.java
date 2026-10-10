package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.BoatMotorSound.Change;
import org.junit.jupiter.api.Test;

class BoatMotorSoundTest {
    @Test
    void theLoopStartsWhenTheFlagTurnsOnAndTheCoughPlaysWhenItTurnsOff() {
        assertEquals(Change.START, BoatMotorSound.change(true, false, true));
        assertEquals(Change.STOP, BoatMotorSound.change(true, true, false));
    }

    @Test
    void anUnchangedFlagDoesNothingSoResyncsNeverDuplicateTheSound() {
        assertEquals(Change.NONE, BoatMotorSound.change(true, true, true));
        assertEquals(Change.NONE, BoatMotorSound.change(true, false, false));
    }

    @Test
    void withTheSoundOffNothingStartsOrStops() {
        assertEquals(Change.NONE, BoatMotorSound.change(false, false, true));
        assertEquals(Change.NONE, BoatMotorSound.change(false, true, false));
    }

    @Test
    void aBoatThatAppearsAlreadyRunningHasNoCordPull() {
        assertFalse(BoatMotorSound.pullAudible(0));
        assertFalse(BoatMotorSound.pullAudible(BoatMotorSound.PULL_MIN_AGE_TICKS - 1));
        assertTrue(BoatMotorSound.pullAudible(BoatMotorSound.PULL_MIN_AGE_TICKS));
        assertTrue(BoatMotorSound.pullAudible(400));
    }

    @Test
    void theLoopGoesOnOnlyWhileTheSoundIsOnTheMotorRunsAndTheBoatLives() {
        assertTrue(BoatMotorSound.loopContinues(true, true, true));
        assertFalse(BoatMotorSound.loopContinues(false, true, true));
        assertFalse(BoatMotorSound.loopContinues(true, false, true));
        assertFalse(BoatMotorSound.loopContinues(true, true, false));
    }

    @Test
    void speedFractionIsClampedAndSafeWithoutATopSpeed() {
        assertEquals(0.0F, BoatMotorSound.speedFraction(0.0, 0.6), 0.0F);
        assertEquals(0.5F, BoatMotorSound.speedFraction(0.3, 0.6), 1e-6F);
        assertEquals(1.0F, BoatMotorSound.speedFraction(0.9, 0.6), 0.0F);
        assertEquals(0.0F, BoatMotorSound.speedFraction(-0.3, 0.6), 0.0F);
        assertEquals(0.0F, BoatMotorSound.speedFraction(0.3, 0.0), 0.0F);
        assertEquals(0.0F, BoatMotorSound.speedFraction(Double.NaN, 0.6), 0.0F);
    }

    @Test
    void volumeScalesWithTheMasterAndTheSpeed() {
        assertEquals(0.6F * 0.7F, BoatMotorSound.loopVolume(0.6, 0.0F), 1e-6F);
        assertEquals(0.6F, BoatMotorSound.loopVolume(0.6, 1.0F), 1e-6F);
        assertTrue(BoatMotorSound.loopVolume(0.6, 0.5F) > BoatMotorSound.loopVolume(0.6, 0.0F));
        assertEquals(0.0F, BoatMotorSound.loopVolume(0.0, 1.0F), 0.0F);
        assertEquals(1.0F, BoatMotorSound.loopVolume(5.0, 1.0F), 1e-6F);
        assertEquals(0.0F, BoatMotorSound.loopVolume(-1.0, 1.0F), 0.0F);
    }

    @Test
    void pitchRisesWithSpeedWithinItsRange() {
        assertEquals(BoatMotorSound.LOOP_PITCH_IDLE, BoatMotorSound.loopPitch(0.0F), 1e-6F);
        assertEquals(BoatMotorSound.LOOP_PITCH_FULL, BoatMotorSound.loopPitch(1.0F), 1e-6F);
        assertEquals(BoatMotorSound.LOOP_PITCH_FULL, BoatMotorSound.loopPitch(7.0F), 1e-6F);
        assertEquals(BoatMotorSound.LOOP_PITCH_IDLE, BoatMotorSound.loopPitch(-2.0F), 1e-6F);
        assertTrue(BoatMotorSound.loopPitch(0.5F) > BoatMotorSound.loopPitch(0.2F));
    }

    @Test
    void aBoatThatStopsTickingIsGoneAfterTheStaleLimit() {
        int stale = 0;
        int age = 10;
        for (int i = 0; i < 20; i++) {
            stale = BoatMotorSound.nextStale(age, age + 1, stale);
            age++;
            assertFalse(BoatMotorSound.gone(stale), "a ticking boat never goes");
        }
        for (int i = 1; i < BoatMotorSound.STALE_LIMIT; i++) {
            stale = BoatMotorSound.nextStale(age, age, stale);
            assertFalse(BoatMotorSound.gone(stale));
        }
        stale = BoatMotorSound.nextStale(age, age, stale);
        assertTrue(BoatMotorSound.gone(stale));
        stale = BoatMotorSound.nextStale(age, age, stale);
        assertEquals(BoatMotorSound.STALE_LIMIT, stale);
        assertEquals(0, BoatMotorSound.nextStale(age, age + 1, stale));
    }
}
