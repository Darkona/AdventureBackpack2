package com.darkona.adventurebackpack.common;

import java.util.Locale;

public final class BoatMotor
{
    private BoatMotor() {}

    public static final double VANILLA_ACCELERATION_DIVISOR = 35.0D;

    public static final int FLAG_MOTORIZED = 1;
    public static final int FLAG_RUNNING = 2;

    public enum Mode
    {
        TOGGLE,
        HOLD;

        public static final String[] NAMES = {"toggle", "hold"};

        public static Mode parse(String text)
        {
            if (text != null && text.trim().toLowerCase(Locale.ROOT).equals("hold")) return HOLD;
            return TOGGLE;
        }
    }

    public static boolean hasFuel(int amount, boolean validFuel)
    {
        return amount > 0 && validFuel;
    }

    public static double maxSpeed(boolean running, double base, double motor)
    {
        return running ? motor : base;
    }

    public static double accelerationDivisor(boolean running, double acceleration)
    {
        if (!running) return VANILLA_ACCELERATION_DIVISOR;
        return VANILLA_ACCELERATION_DIVISOR / Math.max(1.0D, acceleration);
    }

    public static double forwardInput(boolean running, double moveForward)
    {
        if (!running) return moveForward;
        return moveForward < 0 ? moveForward : 1.0D;
    }

    public static boolean fuelDue(int runTicks, int interval)
    {
        return interval > 0 && runTicks > 0 && runTicks % interval == 0;
    }

    public static int fuelCost(int perTick, int interval, double multiplier)
    {
        return FlightCost.copterFuelPerInterval(perTick, interval, multiplier);
    }

    public static boolean toggled(boolean running, boolean enabled, boolean hasFuel, boolean onWater)
    {
        if (running) return false;
        return enabled && hasFuel && onWater;
    }

    public static boolean running(boolean wasRunning, Mode mode, boolean enabled, boolean hasPilot, boolean hasFuel,
                                  int dryTicks, int dryLimit, double moveForward)
    {
        if (!enabled || !hasPilot || !hasFuel || dryTicks >= Math.max(1, dryLimit)) return false;
        if (mode == Mode.HOLD) return moveForward > 0 && (wasRunning || dryTicks == 0);
        return wasRunning;
    }

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

    public static int restoredAmount(int saved, int capacity)
    {
        return Math.max(0, Math.min(saved, capacity));
    }
}
