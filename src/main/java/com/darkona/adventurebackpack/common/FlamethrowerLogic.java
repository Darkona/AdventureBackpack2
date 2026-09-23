package com.darkona.adventurebackpack.common;

/**
 * Pure decisions of the Flamethrower: which targets a burst reaches, what it costs, when it may fire, whom it may
 * hurt and when it may set a block on fire. No Minecraft types: the unit test calls it directly.
 */
public final class FlamethrowerLogic
{
    private FlamethrowerLogic() {}

    /** Cosine of half the cone's opening: a target is inside when the angle to it is at most half the opening. */
    public static double cosHalfAngle(double coneDegrees)
    {
        double half = Math.max(0.0, Math.min(180.0, coneDegrees)) / 2.0;
        return Math.cos(Math.toRadians(half));
    }

    /**
     * True when the point {@code (dx, dy, dz)}, relative to the nozzle, lies inside the cone along the look
     * {@code (dirX, dirY, dirZ)} (any length) no farther than {@code range}. A point on the nozzle itself is inside.
     */
    public static boolean inCone(double dirX, double dirY, double dirZ, double dx, double dy, double dz, double range, double cosHalfAngle)
    {
        if (range <= 0) return false;
        double dist2 = dx * dx + dy * dy + dz * dz;
        if (dist2 > range * range) return false;
        if (dist2 < 1.0E-6) return true;
        double dirLen2 = dirX * dirX + dirY * dirY + dirZ * dirZ;
        if (dirLen2 < 1.0E-12) return false;
        double dot = dirX * dx + dirY * dy + dirZ * dz;
        if (dot <= 0) return false;
        // dot / (|dir| |d|) >= cos  <=>  dot^2 >= cos^2 |dir|^2 |d|^2 (dot is positive here, cos of a half angle up to 90 too)
        return dot * dot >= cosHalfAngle * cosHalfAngle * dirLen2 * dist2;
    }

    /** Fuel one burst costs: the base cost times the fuel's multiplier, rounded up (same rounding as the copter). */
    public static int fuelPerBurst(int fuelPerBurst, double multiplier)
    {
        return FlightCost.copterFuelPerInterval(fuelPerBurst, 1, multiplier);
    }

    /** Damage one burst deals: the configured value, never negative. */
    public static float damagePerBurst(double damage)
    {
        return damage > 0 ? (float) damage : 0.0F;
    }

    /**
     * The nozzle fires when the system is enabled, the worn pack is lit and there is fuel for the burst (a free burst,
     * cost 0, needs no fuel). The last drops of fuel still make one burst.
     */
    public static boolean canFire(boolean enabled, boolean packOn, int fuel, int cost)
    {
        return enabled && packOn && (cost <= 0 || fuel > 0);
    }

    /** What one burst takes from the tanks: its cost, or what is left when that is less. */
    public static int fuelTaken(int fuel, int cost)
    {
        if (fuel <= 0 || cost <= 0) return 0;
        return Math.min(fuel, cost);
    }

    /**
     * Whom a burst may hurt or set on fire: never the wearer, never an animal the wearer owns, and a player only with
     * pvp on (and only when the server and the teams allow it, {@code mayAttackPlayer}).
     */
    public static boolean mayHurt(boolean isSelf, boolean ownedByWearer, boolean isPlayer, boolean pvp, boolean mayAttackPlayer)
    {
        if (isSelf || ownedByWearer) return false;
        return !isPlayer || (pvp && mayAttackPlayer);
    }

    /**
     * A burst sets fire to the free side of the block it hits only with ignitesBlocks on, when that spot is empty,
     * outside spawn protection and editable by the wearer.
     */
    public static boolean mayIgnite(boolean ignitesBlocks, boolean spotFree, boolean spawnProtected, boolean canEdit)
    {
        return ignitesBlocks && spotFree && !spawnProtected && canEdit;
    }

    /** Fuel the pilot flame costs on its once-a-minute beat (0 = free pilot, the default). */
    public static int pilotFuelPerBeat(int pilotFuelPerMinute, double multiplier)
    {
        if (pilotFuelPerMinute <= 0 || multiplier <= 0) return 0;
        return (int) Math.ceil(pilotFuelPerMinute * multiplier - 1e-4);
    }
}
