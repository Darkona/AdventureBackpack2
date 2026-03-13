package com.darkona.adventurebackpack.common;

/**
 * Vertical speed of the Copter Pack and the Steam Jetpack. Pure functions, so the config values can be tested
 * without the game. Above the ceiling the lift fades by 0.01 blocks per tick for each block of height, until the
 * wearer sinks back.
 */
public final class Flight
{
    private Flight() {}

    /** Copter Pack climbing: at least {@code lift} below the ceiling, fading above it. */
    public static double liftSpeed(double posY, double motionY, double lift, int ceiling)
    {
        if (posY > ceiling) return lift - (posY - ceiling) / 100.0;
        return Math.max(motionY, lift);
    }

    /** Steam Jetpack thrust: adds {@code thrust} per tick up to {@code maxRise} below the ceiling, fading above it. */
    public static double thrustSpeed(double posY, double motionY, double thrust, double maxRise, int ceiling)
    {
        if (posY > ceiling) return maxRise - (posY - ceiling) / 100.0;
        return motionY <= maxRise ? motionY + thrust : motionY;
    }
}
