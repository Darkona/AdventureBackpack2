package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BoatMotorTest {
    private static final double BASE = 0.35;
    private static final double MOTOR = 0.6;
    private static final int DRY_LIMIT = 10;

    /** The plain boat's speed multiplier step, as EntityInflatableBoat had it before the motor. */
    private static double oldStep(double multiplier, boolean speedingUp) {
        if (speedingUp && multiplier < BASE) {
            multiplier += (BASE - multiplier) / 35.0D;
            if (multiplier > BASE) multiplier = BASE;
        } else {
            multiplier -= (multiplier - 0.07D) / 35.0D;
            if (multiplier < 0.07D) multiplier = 0.07D;
        }
        return multiplier;
    }

    /** The same step with the motor's numbers, as EntityInflatableBoat has it now. */
    private static double newStep(double multiplier, boolean speedingUp, boolean running, double acceleration) {
        double top = BoatMotor.maxSpeed(running, BASE, MOTOR);
        double divisor = BoatMotor.accelerationDivisor(running, acceleration);
        if (speedingUp && multiplier < top) {
            multiplier += (top - multiplier) / divisor;
            if (multiplier > top) multiplier = top;
        } else {
            multiplier -= (multiplier - 0.07D) / 35.0D;
            if (multiplier < 0.07D) multiplier = 0.07D;
        }
        return multiplier;
    }

    @Test
    void withoutARunningMotorThePhysicsIsExactlyTheOldOne() {
        assertEquals(BASE, BoatMotor.maxSpeed(false, BASE, MOTOR), 0.0);
        assertEquals(35.0, BoatMotor.accelerationDivisor(false, 2.0), 0.0);
        float[] inputs = {0.98f, -0.98f, 0.0f, 0.3f};
        for (float input : inputs) {
            assertEquals((double) input, BoatMotor.forwardInput(false, input), 0.0);
        }
        double before = 0.07, after = 0.07;
        for (int tick = 0; tick < 300; tick++) {
            boolean speedingUp = tick < 200;
            before = oldStep(before, speedingUp);
            after = newStep(after, speedingUp, false, 2.0);
            assertEquals(Double.doubleToLongBits(before), Double.doubleToLongBits(after), "tick " + tick);
        }
    }

    @Test
    void aRunningMotorRaisesTheTopSpeedAndDoublesTheAcceleration() {
        assertEquals(MOTOR, BoatMotor.maxSpeed(true, BASE, MOTOR), 0.0);
        assertEquals(17.5, BoatMotor.accelerationDivisor(true, 2.0), 1e-12);
        assertEquals(35.0, BoatMotor.accelerationDivisor(true, 0.5), 1e-12, "a factor below 1 counts as 1");
        // Closing half the gap takes about half the ticks with acceleration 2
        double plain = 0.07, motor = 0.07;
        int plainTicks = 0, motorTicks = 0;
        while (plain < 0.07 + (BASE - 0.07) / 2) { plain = newStep(plain, true, false, 2.0); plainTicks++; }
        while (motor < 0.07 + (MOTOR - 0.07) / 2) { motor = newStep(motor, true, true, 2.0); motorTicks++; }
        assertTrue(Math.abs(plainTicks - 2 * motorTicks) <= 1, plainTicks + " vs " + motorTicks);
        // It never passes the motor's top
        for (int i = 0; i < 1000; i++) motor = newStep(motor, true, true, 2.0);
        assertTrue(motor <= MOTOR);
    }

    @Test
    void aRunningMotorPushesFullAheadButBackStillBrakes() {
        assertEquals(1.0, BoatMotor.forwardInput(true, 0.0), 0.0);
        assertEquals(1.0, BoatMotor.forwardInput(true, 0.98), 0.0);
        assertEquals(-0.98, BoatMotor.forwardInput(true, -0.98), 1e-12);
    }

    @Test
    void aFullTankLastsFiveMinutesWithTheDefaults() {
        int tank = 6000;
        int ticks = 0;
        int cost = BoatMotor.fuelCost(1, 10, 1.0);
        assertEquals(10, cost);
        while (tank > 0) {
            ticks++;
            if (BoatMotor.fuelDue(ticks, 10)) tank -= cost;
        }
        assertEquals(6000, ticks, "6000 ticks = 5 minutes");
    }

    @Test
    void fuelIsPaidOncePerIntervalWithTheFuelMultiplier() {
        assertFalse(BoatMotor.fuelDue(0, 10));
        assertFalse(BoatMotor.fuelDue(9, 10));
        assertTrue(BoatMotor.fuelDue(10, 10));
        assertTrue(BoatMotor.fuelDue(20, 10));
        assertTrue(BoatMotor.fuelDue(1, 1));
        assertFalse(BoatMotor.fuelDue(10, 0));
        assertEquals(15, BoatMotor.fuelCost(1, 10, 1.5), "oil");
        assertEquals(8, BoatMotor.fuelCost(1, 10, 0.8), "coal");
        assertEquals(0, BoatMotor.fuelCost(0, 10, 1.0), "fuelPerTick 0 is free");
    }

    @Test
    void theToggleStartsOnlyWithFuelOnWaterAndAnEnabledMotor() {
        assertTrue(BoatMotor.toggled(false, true, true, true));
        assertFalse(BoatMotor.toggled(true, true, true, true), "a running motor stops");
        assertFalse(BoatMotor.toggled(false, true, false, true), "no fuel");
        assertFalse(BoatMotor.toggled(false, true, true, false), "on land");
        assertFalse(BoatMotor.toggled(false, false, true, true), "motor disabled in the config");
    }

    @Test
    void theMotorStopsOnItsOwn() {
        BoatMotor.Mode t = BoatMotor.Mode.TOGGLE;
        assertTrue(BoatMotor.running(true, t, true, true, true, 0, DRY_LIMIT, 0));
        assertFalse(BoatMotor.running(true, t, true, true, false, 0, DRY_LIMIT, 0), "out of fuel");
        assertFalse(BoatMotor.running(true, t, true, false, true, 0, DRY_LIMIT, 0), "the pilot got off");
        assertFalse(BoatMotor.running(true, t, false, true, true, 0, DRY_LIMIT, 0), "motor disabled");
        assertTrue(BoatMotor.running(true, t, true, true, true, DRY_LIMIT - 1, DRY_LIMIT, 0), "a short hop over a wave");
        assertFalse(BoatMotor.running(true, t, true, true, true, DRY_LIMIT, DRY_LIMIT, 0), "out of the water");
        assertFalse(BoatMotor.running(false, t, true, true, true, 0, DRY_LIMIT, 1.0), "toggle mode never starts on its own");
    }

    @Test
    void holdModeRunsOnlyWhileForwardIsHeld() {
        BoatMotor.Mode h = BoatMotor.Mode.HOLD;
        assertTrue(BoatMotor.running(false, h, true, true, true, 0, DRY_LIMIT, 0.98));
        assertFalse(BoatMotor.running(true, h, true, true, true, 0, DRY_LIMIT, 0.0));
        assertFalse(BoatMotor.running(true, h, true, true, true, 0, DRY_LIMIT, -0.98));
        assertFalse(BoatMotor.running(false, h, true, true, false, 0, DRY_LIMIT, 0.98), "no fuel");
        assertFalse(BoatMotor.running(false, h, true, true, true, 1, DRY_LIMIT, 0.98), "does not start on land");
    }

    @Test
    void modeNamesParseAndFallBackToToggle() {
        assertEquals(BoatMotor.Mode.HOLD, BoatMotor.Mode.parse(" Hold "));
        assertEquals(BoatMotor.Mode.TOGGLE, BoatMotor.Mode.parse("toggle"));
        assertEquals(BoatMotor.Mode.TOGGLE, BoatMotor.Mode.parse("nonsense"));
        assertEquals(BoatMotor.Mode.TOGGLE, BoatMotor.Mode.parse(null));
    }

    @Test
    void theDataWatcherFlagsCarryMotorAndRunning() {
        byte plain = BoatMotor.flags(false, true);
        assertFalse(BoatMotor.isMotorized(plain));
        assertFalse(BoatMotor.isRunning(plain), "a plain boat never runs a motor");
        byte idle = BoatMotor.flags(true, false);
        assertTrue(BoatMotor.isMotorized(idle));
        assertFalse(BoatMotor.isRunning(idle));
        byte running = BoatMotor.flags(true, true);
        assertTrue(BoatMotor.isMotorized(running));
        assertTrue(BoatMotor.isRunning(running));
    }

    @Test
    void fuelRestoredFromTheItemFitsTheTank() {
        assertEquals(3500, BoatMotor.restoredAmount(3500, 6000));
        assertEquals(6000, BoatMotor.restoredAmount(9000, 6000), "a lowered capacity drops the rest");
        assertEquals(0, BoatMotor.restoredAmount(-5, 6000));
    }
}
