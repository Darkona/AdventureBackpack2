package com.darkona.adventurebackpack.util;

/**
 * Soft altitude ceiling of the copter pack and the steam jetpack: above {@link #START} the lift shrinks by 0.01 per
 * block until it pushes the player down. The old formula used {@code posY % 100}, so the ceiling vanished again from
 * y=200 on and the pack lifted at full power there.
 */
public final class FlightCeiling
{
    public static final double START = 100;
    /** Never pull down faster than sneaking down with the copter. */
    public static final double MAX_DESCENT = -0.3;

    private FlightCeiling() {}

    /** Vertical motion while climbing above {@link #START} with the given full lift. */
    public static double motionAbove(double posY, double lift)
    {
        return Math.max(MAX_DESCENT, lift - (posY - START) / 100);
    }
}
