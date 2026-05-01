package com.darkona.adventurebackpack.common;

/**
 * Vertical speed of the Copter Pack and the Steam Jetpack. Pure functions, so the config values can be tested
 * without the game. Above the ceiling the lift shrinks by 0.01 blocks per tick for each block of height until it
 * pushes the wearer down, never faster than {@link #MAX_DESCENT}. The old formula used {@code posY % 100}, so the
 * ceiling vanished again from y=200 on and the pack lifted at full power there.
 */
public final class Flight
{
    /** Never pull down faster than sneaking down with the copter. */
    public static final double MAX_DESCENT = -0.3;

    private Flight() {}

    /** Vertical motion while climbing above the ceiling with the given full lift. */
    public static double motionAbove(double posY, double lift, int ceiling)
    {
        return Math.max(MAX_DESCENT, lift - (posY - ceiling) / 100.0);
    }

    /** Copter Pack climbing: at least {@code lift} below the ceiling, fading above it. */
    public static double liftSpeed(double posY, double motionY, double lift, int ceiling)
    {
        if (posY >= ceiling) return motionAbove(posY, lift, ceiling);
        return Math.max(motionY, lift);
    }

    /** Steam Jetpack thrust: adds {@code thrust} per tick up to {@code maxRise} below the ceiling, fading above it. */
    public static double thrustSpeed(double posY, double motionY, double thrust, double maxRise, int ceiling)
    {
        if (posY >= ceiling) return motionAbove(posY, maxRise, ceiling);
        return motionY <= maxRise ? motionY + thrust : motionY;
    }
}
