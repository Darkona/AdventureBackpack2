package com.darkona.adventurebackpack.common;

public final class CrossbowLogic
{
    public enum Action
    {
        FIRE,
        RELOAD,
        DRY,
        WAIT,
        NONE
    }

    public static final int MODE_REPEATING = 0;
    public static final int MODE_GRAPPLE = 1;
    public static final int MODE_SNIPER = 2;
    public static final int MODES = 3;

    public static final int RECOIL_TICKS = 4;

    private CrossbowLogic() {}

    public static Action decide(int ammo, int capacity, long now, long readyAt, int maxWait, boolean sneaking, boolean hasLoaded)
    {
        return decide(ammo, 1, capacity, now, readyAt, maxWait, sneaking, hasLoaded);
    }

    public static Action decide(int ammo, int cost, int capacity, long now, long readyAt, int maxWait, boolean sneaking, boolean hasLoaded)
    {
        if (isWaiting(now, readyAt, maxWait)) return Action.WAIT;
        if (sneaking) return ammo < capacity && hasLoaded ? Action.RELOAD : Action.NONE;
        if (ammo >= shotCost(cost, capacity)) return Action.FIRE;
        return hasLoaded ? Action.RELOAD : Action.DRY;
    }

    public static int shotCost(int cost, int capacity)
    {
        return cost < 0 ? 0 : (cost > capacity ? capacity : cost);
    }

    public static int effectiveMode(int stored, boolean grappleEnabled, boolean sniperEnabled)
    {
        return isModeEnabled(stored, grappleEnabled, sniperEnabled) ? stored : MODE_REPEATING;
    }

    public static boolean isModeEnabled(int mode, boolean grappleEnabled, boolean sniperEnabled)
    {
        switch (mode)
        {
            case MODE_REPEATING:
                return true;
            case MODE_GRAPPLE:
                return grappleEnabled;
            case MODE_SNIPER:
                return sniperEnabled;
            default:
                return false;
        }
    }

    public static int nextMode(int current, boolean grappleEnabled, boolean sniperEnabled)
    {
        int mode = effectiveMode(current, grappleEnabled, sniperEnabled);
        for (int i = 0; i < MODES; i++)
        {
            mode = (mode + 1) % MODES;
            if (isModeEnabled(mode, grappleEnabled, sniperEnabled)) return mode;
        }
        return MODE_REPEATING;
    }

    public static boolean bipodDeployed(boolean sneaking, boolean onGround, boolean riding, double dx, double dz, double tolerance)
    {
        return sneaking && onGround && !riding && dx * dx + dz * dz <= tolerance * tolerance;
    }

    public static boolean isWaiting(long now, long readyAt, int maxWait)
    {
        long left = readyAt - now;
        return left > 0 && left <= maxWait;
    }

    public static boolean isRecoiling(long now, long lastShot)
    {
        long since = now - lastShot;
        return lastShot > 0 && since >= 0 && since < RECOIL_TICKS;
    }

    public static double damageFactor(double muzzleDamage, double speed)
    {
        double target = muzzleDamage > DAMAGE_MARGIN ? muzzleDamage - DAMAGE_MARGIN : muzzleDamage;
        return speed > 0 ? target / speed : target;
    }

    private static final double DAMAGE_MARGIN = 0.01;

    public static int clampAmmo(int ammo, int capacity)
    {
        return ammo < 0 ? 0 : (ammo > capacity ? capacity : ammo);
    }

    public static boolean breaks(int wear, int durability)
    {
        return durability > 0 && wear >= durability;
    }

    public static boolean shouldWarnEmpty(long now, long lastDry, int quietTicks)
    {
        return lastDry <= 0 || now - lastDry > quietTicks || now < lastDry;
    }
}
