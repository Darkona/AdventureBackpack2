package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Tick model of the Steam Jetpack climb with the vanilla 1.7.10 order: the jetpack sets motionY in the player tick
 * (PlayerTickEvent START), then EntityLivingBase.onLivingUpdate zeroes a motion under 0.005, jumps (0.42) when the
 * wearer stands on the ground holding jump, moves by motionY and takes gravity and drag ({@link Flight#afterGravity}).
 * The wearer starts standing (motionY -0.0784, what gravity leaves on the ground) and holds jump the whole time.
 */
class JetpackClimbTest
{
    static final double JUMP = 0.41999998688697815;

    /** The formula before this pass: add thrust while motionY is at most maxRise, no cap after adding, no brake. */
    static double oldThrust(double posY, double motionY, double thrust, double maxRise, int ceiling)
    {
        if (posY >= ceiling) return Flight.motionAbove(posY, maxRise, ceiling);
        return motionY <= maxRise ? motionY + thrust : motionY;
    }

    /** Height climbed after {@code ticks} ticks of thrust from standing on the ground at {@code startY}. */
    static double climb(int ticks, boolean old, double thrust, double maxRise, double brake, int ceiling, double startY)
    {
        double y = startY, v = -0.0784;
        boolean onGround = true;
        for (int t = 0; t < ticks; t++)
        {
            v = old ? oldThrust(y, v, thrust, maxRise, ceiling) : Flight.thrustSpeed(y, v, thrust, maxRise, brake, ceiling);
            if (Math.abs(v) < 0.005) v = 0;
            if (onGround) v = JUMP;
            y += v;
            if (y <= startY) { y = startY; v = 0; onGround = true; } else onGround = false;
            v = Flight.afterGravity(v);
        }
        return y - startY;
    }

    static double climbNew(int ticks)
    {
        return climb(ticks, false, ConfigHandler.JETPACK_THRUST, ConfigHandler.JETPACK_MAX_RISE_SPEED,
                ConfigHandler.JETPACK_FALL_BRAKE, ConfigHandler.JETPACK_MAX_ALTITUDE, 64);
    }

    static double climbOld(int ticks)
    {
        return climb(ticks, true, 0.08, 0.28, 1, 90, 64);
    }

    @Test
    void theOldDefaultsOnlyFloated()
    {
        // thrust 0.08 equals gravity: the jump carries the wearer up, slows down and leaves a drift of 0.08 per tick
        double firstSecond = climbOld(20), fourthSecond = climbOld(80) - climbOld(60);
        assertTrue(fourthSecond < firstSecond / 2, "old 1st second " + firstSecond + ", 4th " + fourthSecond);
        assertTrue(climbOld(150) - climbOld(149) < 0.1, "old drift per tick " + (climbOld(150) - climbOld(149)));
    }

    @Test
    void theDefaultsClimbClearlyFasterThanAJump()
    {
        assertTrue(climbNew(20) > 6.5, "1 s climb " + climbNew(20));
        assertTrue(climbNew(60) > 22.0, "3 s climb " + climbNew(60));
        // steady climb at the top speed: one tick adds maxRiseSpeed
        assertEquals(ConfigHandler.JETPACK_MAX_RISE_SPEED, climbNew(41) - climbNew(40), 1e-6);
    }

    @Test
    void theDefaultThrustBeatsGravityWithMargin()
    {
        // to hold the top speed the thrust has to give back what gravity and drag took in one tick
        double needed = ConfigHandler.JETPACK_MAX_RISE_SPEED - Flight.afterGravity(ConfigHandler.JETPACK_MAX_RISE_SPEED);
        assertTrue(ConfigHandler.JETPACK_THRUST > needed * 1.4, "thrust " + ConfigHandler.JETPACK_THRUST + " needed " + needed);
    }

    @Test
    void thrustStopsAFallWithinAFewTicks()
    {
        double v = -1.0; // about 9 blocks of free fall
        int ticks = 0;
        while (v < 0 && ticks < 100)
        {
            v = Flight.afterGravity(Flight.thrustSpeed(64, v, ConfigHandler.JETPACK_THRUST, ConfigHandler.JETPACK_MAX_RISE_SPEED,
                    ConfigHandler.JETPACK_FALL_BRAKE, ConfigHandler.JETPACK_MAX_ALTITUDE));
            ticks++;
        }
        assertTrue(ticks <= 6, "ticks to stop a fall " + ticks);
    }

    @Test
    void theThrustHoldsAboveTheCeiling()
    {
        double hold = ConfigHandler.JETPACK_MAX_ALTITUDE + ConfigHandler.JETPACK_MAX_RISE_SPEED * 100;
        double top = 64 + climbNew(2000);
        assertEquals(hold, top, 0.5);
    }
}
