package com.darkona.adventurebackpack.common;

public final class Flight
{
    public static final double MAX_DESCENT = -0.3;

    private Flight() {}

    public static double motionAbove(double posY, double lift, int ceiling)
    {
        return Math.max(MAX_DESCENT, lift - (posY - ceiling) / 100.0);
    }

    public static double liftSpeed(double posY, double motionY, double lift, int ceiling)
    {
        if (posY >= ceiling) return motionAbove(posY, lift, ceiling);
        return Math.max(motionY, lift);
    }

    public static final double GRAVITY = 0.08;
    public static final double DRAG = 0.9800000190734863;

    public static double thrustSpeed(double posY, double motionY, double thrust, double maxRise, double fallBrake, int ceiling)
    {
        if (posY >= ceiling) return motionAbove(posY, maxRise, ceiling);
        if (motionY >= maxRise) return motionY;
        double accel = motionY < 0 ? thrust * fallBrake : thrust;
        return Math.min(motionY + accel, maxRise);
    }

    public static double afterGravity(double motionY)
    {
        return (motionY - GRAVITY) * DRAG;
    }
}
