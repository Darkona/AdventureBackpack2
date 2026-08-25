package com.darkona.adventurebackpack.common;

/**
 * Pure decisions of the crossbow's grappling hook. No game classes here, so the unit tests run without Minecraft.
 * Distances are in blocks, speeds in blocks per tick.
 */
public final class GrappleLogic
{
    /** Why a hooked grapple lets go of its owner, or {@link #HOLD} to keep pulling. */
    public static final int HOLD = 0;
    public static final int ARRIVED = 1;
    public static final int SNEAKING = 2;
    public static final int TIMED_OUT = 3;
    public static final int TOO_FAR = 4;

    /** Height of a standing player's eyes over the feet, where the hook leaves. */
    public static final double EYE_HEIGHT = 1.62;
    /** Half the height of a player: the point the hook pulls towards the anchor. */
    public static final double BODY_CENTER = 0.9;
    /** Extra slack over the maximum distance before a hooked owner counts as torn away (teleport, knockback). */
    public static final double SLACK = 8.0;

    private GrappleLogic() {}

    /**
     * Velocity that pulls a point at {@code (x, y, z)} towards the anchor at {@code speed}, never overshooting it.
     * Writes the velocity to {@code out[0..2]} (the caller reuses the array) and returns the distance to the anchor.
     */
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

    /**
     * Decides whether a hooked grapple keeps pulling. Checked in this order: the owner sneaks, the owner arrived, the
     * pull ran too long, the owner was torn far away.
     *
     * @param distance        owner's body centre to the anchor
     * @param releaseDistance the owner arrives closer than this
     * @param pullTicks       ticks since the hook caught
     * @param maxPullTicks    longest pull
     * @param maxDistance     the hook's range
     */
    public static int release(boolean sneaking, double distance, double releaseDistance, int pullTicks, int maxPullTicks, double maxDistance)
    {
        if (sneaking) return SNEAKING;
        if (distance < releaseDistance) return ARRIVED;
        if (pullTicks > maxPullTicks) return TIMED_OUT;
        if (distance > maxDistance + SLACK) return TOO_FAR;
        return HOLD;
    }

    /** A flying hook that got farther than its range from the owner (squared distance) is pulled back. */
    public static boolean outOfRange(double distanceSq, double maxDistance)
    {
        return distanceSq > maxDistance * maxDistance;
    }

    /**
     * Direction of a look with the given yaw and pitch in degrees, Minecraft's convention (yaw 0 looks to +Z, pitch
     * 90 looks down). Writes a unit vector to {@code out[0..2]}.
     */
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
