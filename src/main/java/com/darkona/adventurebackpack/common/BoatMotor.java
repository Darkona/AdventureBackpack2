package com.darkona.adventurebackpack.common;

import java.util.Locale;

/**
 * The outboard motor of the motorized Inflatable Boat, as pure functions (inflatableboat.motor.*). The server decides
 * whether the motor runs and sends it to the clients with the boat's DataWatcher; both sides then move the boat with
 * the same numbers. A boat whose motor does not run (no fuel, motor off, motor disabled, plain boat) moves exactly
 * like before the motor existed: top speed inflatableboat.maxSpeed and the vanilla acceleration.
 */
public final class BoatMotor
{
    private BoatMotor() {}

    /** Divisor of the vanilla boat acceleration: the speed multiplier closes 1/35 of its gap to the top each tick. */
    public static final double VANILLA_ACCELERATION_DIVISOR = 35.0D;

    /** DataWatcher flag bits of the boat: it has a motor, and the motor runs. */
    public static final int FLAG_MOTORIZED = 1;
    public static final int FLAG_RUNNING = 2;

    /** How the pilot drives the motor (inflatableboat.motor.mode). */
    public enum Mode
    {
        /** The motor key turns forward thrust on and off; the boat goes ahead on its own. */
        TOGGLE,
        /** The motor pushes while the pilot holds the forward key, like the copter's throttle. */
        HOLD;

        public static final String[] NAMES = {"toggle", "hold"};

        /** Unknown text falls back to toggle. */
        public static Mode parse(String text)
        {
            if (text != null && text.trim().toLowerCase(Locale.ROOT).equals("hold")) return HOLD;
            return TOGGLE;
        }
    }

    /** True when the tank holds some fuel that is on fuels.list. */
    public static boolean hasFuel(int amount, boolean validFuel)
    {
        return amount > 0 && validFuel;
    }

    /** Top speed in blocks per tick: the motor's while it runs, the plain boat's otherwise. */
    public static double maxSpeed(boolean running, double base, double motor)
    {
        return running ? motor : base;
    }

    /**
     * Divisor of the acceleration toward the top speed: vanilla's 35, divided by the motor's acceleration factor while
     * it runs (2.0 halves it, so the boat reaches its speed twice as fast). A factor below 1 counts as 1.
     */
    public static double accelerationDivisor(boolean running, double acceleration)
    {
        if (!running) return VANILLA_ACCELERATION_DIVISOR;
        return VANILLA_ACCELERATION_DIVISOR / Math.max(1.0D, acceleration);
    }

    /**
     * Forward input of the boat: the pilot's own while the motor is off; full ahead while it runs, unless the pilot
     * holds back, which still brakes. The steering stays with the pilot in both cases.
     */
    public static double forwardInput(boolean running, double moveForward)
    {
        if (!running) return moveForward;
        return moveForward < 0 ? moveForward : 1.0D;
    }

    /** True on the ticks the running motor pays its fuel: every {@code interval} ticks of running. */
    public static boolean fuelDue(int runTicks, int interval)
    {
        return interval > 0 && runTicks > 0 && runTicks % interval == 0;
    }

    /** Fuel taken on a due tick: the cost of the whole interval, times the fuel's multiplier, rounded up. */
    public static int fuelCost(int perTick, int interval, double multiplier)
    {
        return FlightCost.copterFuelPerInterval(perTick, interval, multiplier);
    }

    /** New state after the pilot presses the motor key (toggle mode). It only starts with fuel and on water. */
    public static boolean toggled(boolean running, boolean enabled, boolean hasFuel, boolean onWater)
    {
        if (running) return false;
        return enabled && hasFuel && onWater;
    }

    /**
     * Whether the motor keeps (or, in hold mode, starts) running this tick. It stops on its own without a pilot,
     * without fuel, with the motor disabled, and after {@code dryLimit} ticks out of the water. In hold mode it runs
     * only while the pilot holds forward.
     */
    public static boolean running(boolean wasRunning, Mode mode, boolean enabled, boolean hasPilot, boolean hasFuel,
                                  int dryTicks, int dryLimit, double moveForward)
    {
        if (!enabled || !hasPilot || !hasFuel || dryTicks >= Math.max(1, dryLimit)) return false;
        if (mode == Mode.HOLD) return moveForward > 0 && (wasRunning || dryTicks == 0);
        return wasRunning;
    }

    /** DataWatcher byte of the boat from its two flags. */
    public static byte flags(boolean motorized, boolean running)
    {
        return (byte) ((motorized ? FLAG_MOTORIZED : 0) | (motorized && running ? FLAG_RUNNING : 0));
    }

    public static boolean isMotorized(int flags)
    {
        return (flags & FLAG_MOTORIZED) != 0;
    }

    public static boolean isRunning(int flags)
    {
        return (flags & FLAG_RUNNING) != 0;
    }

    /** Fuel restored into a boat from its item: what was saved, never below 0 nor above the tank. */
    public static int restoredAmount(int saved, int capacity)
    {
        return Math.max(0, Math.min(saved, capacity));
    }
}
