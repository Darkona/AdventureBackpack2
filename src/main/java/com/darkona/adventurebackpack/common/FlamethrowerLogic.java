package com.darkona.adventurebackpack.common;

public final class FlamethrowerLogic
{
    private FlamethrowerLogic() {}

    public static double cosHalfAngle(double coneDegrees)
    {
        double half = Math.max(0.0, Math.min(180.0, coneDegrees)) / 2.0;
        return Math.cos(Math.toRadians(half));
    }

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
        return dot * dot >= cosHalfAngle * cosHalfAngle * dirLen2 * dist2;
    }

    public static int fuelPerBurst(int fuelPerBurst, double multiplier)
    {
        return FlightCost.copterFuelPerInterval(fuelPerBurst, 1, multiplier);
    }

    public static float damagePerBurst(double damage)
    {
        return damage > 0 ? (float) damage : 0.0F;
    }

    public static boolean canFire(boolean enabled, boolean packOn, int fuel, int cost)
    {
        return enabled && packOn && (cost <= 0 || fuel > 0);
    }

    public static int fuelTaken(int fuel, int cost)
    {
        if (fuel <= 0 || cost <= 0) return 0;
        return Math.min(fuel, cost);
    }

    public static boolean mayHurt(boolean isSelf, boolean ownedByWearer, boolean isPlayer, boolean pvp, boolean mayAttackPlayer)
    {
        if (isSelf || ownedByWearer) return false;
        return !isPlayer || (pvp && mayAttackPlayer);
    }

    public static boolean mayIgnite(boolean ignitesBlocks, boolean spotFree, boolean spawnProtected, boolean canEdit)
    {
        return ignitesBlocks && spotFree && !spawnProtected && canEdit;
    }

    public static int pilotFuelPerBeat(int pilotFuelPerMinute, double multiplier)
    {
        if (pilotFuelPerMinute <= 0 || multiplier <= 0) return 0;
        return (int) Math.ceil(pilotFuelPerMinute * multiplier - 1e-4);
    }
}
