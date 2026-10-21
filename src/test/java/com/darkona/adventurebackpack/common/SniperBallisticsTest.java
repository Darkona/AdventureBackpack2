package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.config.ConfigHandler;
import org.junit.jupiter.api.Test;

/**
 * Numbers of the sniper shot, simulated tick by tick the way EntityArrow flies in 1.7.10: each tick the bolt moves by
 * its motion, then the motion is multiplied by the drag (0.99) and the gravity (0.05) is taken from its vertical part.
 * EntityBolt keeps that vanilla step and corrects it afterwards with {@link BoltFlight}. Damage on a hit is vanilla's
 * ceil(speed x damage), where the damage factor is set at the muzzle by {@link CrossbowLogic#damageFactor}.
 *
 * <pre>
 *                      speed  best range  drop at 64  drop at 128  damage at muzzle / 64 / 128
 * bow, full draw        3.0     117 b      12.8 b      64.8 b        6 / 6 / 6 (2 x speed)
 * old sniper (5, 12)    5.0     260 b       4.4 b      19.9 b       12 / 11 / 10
 * new sniper (9, 24)    9.0    1200 b       0.7 b       2.6 b       24 / 24 / 23
 * </pre>
 */
class SniperBallisticsTest
{
    private static final float OLD_SPEED = 5.0F;
    private static final double OLD_DAMAGE = 12.0;

    private static double newSpeed()
    {
        return ConfigHandler.CROSSBOW_BOLT_VELOCITY * ConfigHandler.SNIPER_SPEED_MULTIPLIER;
    }

    private static double newDamage()
    {
        return ConfigHandler.CROSSBOW_BOLT_DAMAGE * ConfigHandler.SNIPER_DAMAGE_MULTIPLIER;
    }

    private static void step(double[] s, float drag, float gravity)
    {
        s[0] += s[2];
        s[1] += s[3];
        double vx = s[2], vy = s[3];
        s[2] = vx * (double) BoltFlight.ARROW_DRAG;
        s[3] = vy * (double) BoltFlight.ARROW_DRAG - (double) BoltFlight.ARROW_GRAVITY;
        if (!BoltFlight.isArrowFlight(drag, gravity) && BoltFlight.flewFreely(vx, vy, 0, s[2], s[3], 0))
        {
            s[2] *= BoltFlight.dragScale(drag);
            s[3] = BoltFlight.correctY(s[3], drag, gravity);
        }
    }

    private static double range(double speed, double degrees, float drag, float gravity)
    {
        double a = Math.toRadians(degrees);
        double[] s = {0, 0, speed * Math.cos(a), speed * Math.sin(a)};
        for (int t = 0; t < 5000; t++)
        {
            step(s, drag, gravity);
            if (s[1] < 0) return s[0];
        }
        return s[0];
    }

    private static double bestRange(double speed, float drag, float gravity)
    {
        double best = 0;
        for (double deg = 0.5; deg < 90; deg += 0.5) best = Math.max(best, range(speed, deg, drag, gravity));
        return best;
    }

    /** Horizontal shot: {height reached (negative = below the muzzle), speed} when the bolt has covered the distance. */
    private static double[] at(double speed, double distance, float drag, float gravity)
    {
        double[] s = {0, 0, speed, 0};
        while (s[0] < distance) step(s, drag, gravity);
        return new double[]{s[1], Math.sqrt(s[2] * s[2] + s[3] * s[3])};
    }

    private static int hit(double speed, double muzzleDamage, double muzzleSpeed)
    {
        return (int) Math.ceil(speed * CrossbowLogic.damageFactor(muzzleDamage, muzzleSpeed));
    }

    @Test
    void correctionTurnsTheVanillaStepIntoTheSniperStep()
    {
        float drag = 0.995F, gravity = 0.025F;
        double vx = 7.3, vy = -1.2;
        double mx = vx * (double) BoltFlight.ARROW_DRAG, my = vy * (double) BoltFlight.ARROW_DRAG - (double) BoltFlight.ARROW_GRAVITY;
        assertTrue(BoltFlight.flewFreely(vx, vy, 0, mx, my, 0));
        assertEquals(vx * drag, mx * BoltFlight.dragScale(drag), 1e-6);
        assertEquals(vy * drag - gravity, BoltFlight.correctY(my, drag, gravity), 1e-6);
        assertFalse(BoltFlight.flewFreely(vx, vy, 0, vx * 0.8, vy * 0.8 - 0.05, 0), "in water vanilla flight is kept");
        assertFalse(BoltFlight.flewFreely(vx, vy, 0, vx, vy, 0), "stuck in a block");
        assertTrue(BoltFlight.isArrowFlight(BoltFlight.ARROW_DRAG, BoltFlight.ARROW_GRAVITY));
    }

    @Test
    void defaultsMatchTheDocumentedNumbers()
    {
        assertEquals(9.0, newSpeed(), 1e-9);
        assertEquals(24.0, newDamage(), 1e-9);
        assertEquals(48, ConfigHandler.SNIPER_FIRE_INTERVAL_TICKS);
        assertEquals(0, ConfigHandler.SNIPER_FIRE_INTERVAL_TICKS % 4, "a multiple of the 4 tick click repeat");
    }

    @Test
    void sniperFliesMuchFurtherAndFlatter()
    {
        float drag = ConfigHandler.SNIPER_DRAG, gravity = ConfigHandler.SNIPER_GRAVITY;
        float ad = BoltFlight.ARROW_DRAG, ag = BoltFlight.ARROW_GRAVITY;
        double bow = bestRange(3.0, ad, ag);
        double old = bestRange(OLD_SPEED, ad, ag);
        double now = bestRange(newSpeed(), drag, gravity);
        assertEquals(117, bow, 2);
        assertEquals(260, old, 3);
        assertTrue(now > 4 * old, "best range " + now + " vs " + old);

        assertEquals(-12.8, at(3.0, 64, ad, ag)[0], 0.2);
        assertEquals(-4.4, at(OLD_SPEED, 64, ad, ag)[0], 0.2);
        assertTrue(at(newSpeed(), 64, drag, gravity)[0] > -1.0, "under a block of drop at 64 blocks");
        assertTrue(at(newSpeed(), 128, drag, gravity)[0] > -3.0, "under three blocks of drop at 128 blocks");
    }

    @Test
    void sniperHitsMuchHarderAndKeepsItAtRange()
    {
        float drag = ConfigHandler.SNIPER_DRAG, gravity = ConfigHandler.SNIPER_GRAVITY;
        float ad = BoltFlight.ARROW_DRAG, ag = BoltFlight.ARROW_GRAVITY;
        assertEquals(6, (int) Math.ceil(3.0 * 2.0), "vanilla bow at full draw, no critical");
        assertEquals(12, hit(OLD_SPEED, OLD_DAMAGE, OLD_SPEED));
        assertEquals(11, hit(at(OLD_SPEED, 64, ad, ag)[1], OLD_DAMAGE, OLD_SPEED));
        assertEquals(10, hit(at(OLD_SPEED, 128, ad, ag)[1], OLD_DAMAGE, OLD_SPEED));

        double v = newSpeed(), d = newDamage();
        assertEquals(24, hit(v, d, v));
        assertTrue(hit(at(v, 64, drag, gravity)[1], d, v) >= 23);
        assertTrue(hit(at(v, 128, drag, gravity)[1], d, v) >= 22, "still kills a zombie (20) at 128 blocks");
    }
}
