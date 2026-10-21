package com.darkona.adventurebackpack.common;

public final class BoltFlight
{
    public static final float ARROW_DRAG = 0.99F;
    public static final float ARROW_GRAVITY = 0.05F;

    private BoltFlight() {}

    public static boolean isArrowFlight(float drag, float gravity)
    {
        return drag == ARROW_DRAG && gravity == ARROW_GRAVITY;
    }

    public static boolean flewFreely(double vx, double vy, double vz, double mx, double my, double mz)
    {
        return mx == vx * (double) ARROW_DRAG && mz == vz * (double) ARROW_DRAG && my == vy * (double) ARROW_DRAG - (double) ARROW_GRAVITY;
    }

    public static double dragScale(float drag)
    {
        return drag / (double) ARROW_DRAG;
    }

    public static double correctY(double my, float drag, float gravity)
    {
        return (my + (double) ARROW_GRAVITY) * dragScale(drag) - gravity;
    }
}
