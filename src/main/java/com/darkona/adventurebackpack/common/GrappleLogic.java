package com.darkona.adventurebackpack.common;

public final class GrappleLogic
{
    public static final int HOLD = 0;
    public static final int ARRIVED = 1;
    public static final int SNEAKING = 2;
    public static final int TIMED_OUT = 3;
    public static final int TOO_FAR = 4;

    public static final double EYE_HEIGHT = 1.62;
    public static final double BODY_CENTER = 0.9;
    public static final double SLACK = 8.0;

    private GrappleLogic() {}

    public static double pull(double x, double y, double z, double anchorX, double anchorY, double anchorZ, double speed, double[] out)
    {
        double dx = anchorX - x;
        double dy = anchorY - y;
        double dz = anchorZ - z;
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 1.0E-6)
        {
            out[0] = 0;
            out[1] = 0;
            out[2] = 0;
            return 0;
        }
        double scale = (speed < distance ? speed : distance) / distance;
        out[0] = dx * scale;
        out[1] = dy * scale;
        out[2] = dz * scale;
        return distance;
    }

    public static int release(boolean sneaking, double distance, double releaseDistance, int pullTicks, int maxPullTicks, double maxDistance)
    {
        if (sneaking) return SNEAKING;
        if (distance < releaseDistance) return ARRIVED;
        if (pullTicks > maxPullTicks) return TIMED_OUT;
        if (distance > maxDistance + SLACK) return TOO_FAR;
        return HOLD;
    }

    public static boolean outOfRange(double distanceSq, double maxDistance)
    {
        return distanceSq > maxDistance * maxDistance;
    }

    public static void look(float yaw, float pitch, double[] out)
    {
        double yawRad = Math.toRadians(yaw);
        double pitchRad = Math.toRadians(pitch);
        double cosPitch = Math.cos(pitchRad);
        out[0] = -Math.sin(yawRad) * cosPitch;
        out[1] = -Math.sin(pitchRad);
        out[2] = Math.cos(yawRad) * cosPitch;
    }
}
