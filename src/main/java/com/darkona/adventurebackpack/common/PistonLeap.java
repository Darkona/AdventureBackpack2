package com.darkona.adventurebackpack.common;

public final class PistonLeap
{
    public static final double MIN_SPEED = 1.0E-3;

    private PistonLeap() {}

    public static boolean leaps(boolean enabled, boolean sprinting, boolean requiresSprint)
    {
        return enabled && (sprinting || !requiresSprint);
    }

    public static double scale(double motionX, double motionZ, double boost, double maxSpeed)
    {
        double speed = Math.sqrt(motionX * motionX + motionZ * motionZ);
        if (speed < MIN_SPEED || boost <= 1.0) return 1.0;
        double target = speed * boost;
        double limit = maxSpeed > speed ? maxSpeed : speed;
        if (target > limit) target = limit;
        return target / speed;
    }
}
