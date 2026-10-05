package com.darkona.adventurebackpack.common;

/**
 * Pure decisions of the Clockwork Crossbow (the magazine itself is {@link MagazineLogic}). No game classes here, so the unit tests run
 * without Minecraft. Times are world ticks ({@code World.getTotalWorldTime()}), never wall-clock time.
 */
public final class CrossbowLogic
{
    /** What one right click does. */
    public enum Action
    {
        /** Shoot one bolt from the magazine. */
        FIRE,
        /** Put in a loaded magazine (the inserted one is empty, or the player sneaks to swap it); the old one comes back. */
        RELOAD,
        /** Empty magazine and no loaded one: click, give back the empty magazine and tell the player. */
        DRY,
        /** Still in the pause after a shot or a reload. */
        WAIT,
        /** Nothing to do (sneak with a full magazine or without a loaded one). */
        NONE
    }

    /** Firing modes, stored in the crossbow NBT. Unknown values (edited NBT, a later version) read as repeating. */
    public static final int MODE_REPEATING = 0;
    public static final int MODE_GRAPPLE = 1;
    public static final int MODE_SNIPER = 2;
    public static final int MODES = 3;

    /** Ticks after a shot during which the model shows the released string. */
    public static final int RECOIL_TICKS = 4;

    private CrossbowLogic() {}

    /** Same as {@link #decide(int, int, int, long, long, int, boolean, boolean)} for a shot that costs one bolt. */
    public static Action decide(int ammo, int capacity, long now, long readyAt, int maxWait, boolean sneaking, boolean hasLoaded)
    {
        return decide(ammo, 1, capacity, now, readyAt, maxWait, sneaking, hasLoaded);
    }

    /**
     * Decides what a right click does.
     *
     * @param ammo         bolts in the magazine
     * @param cost         bolts one shot takes (1 for a bolt, the configured charges for the grappling hook); 0 fires
     *                     even with an empty magazine, and a cost above {@code capacity} counts as a full magazine
     * @param capacity     bolts in a full magazine
     * @param now          current world tick
     * @param readyAt      world tick from which the crossbow can act again
     * @param maxWait      longest pause the config allows; a {@code readyAt} further away than this is stale
     *                     (the item came from another world) and is ignored
     * @param sneaking     the player sneaks (manual magazine swap)
     * @param hasLoaded    a loaded magazine is in the inventory, or the player is in creative mode
     */
    public static Action decide(int ammo, int cost, int capacity, long now, long readyAt, int maxWait, boolean sneaking, boolean hasLoaded)
    {
        if (isWaiting(now, readyAt, maxWait)) return Action.WAIT;
        if (sneaking) return ammo < capacity && hasLoaded ? Action.RELOAD : Action.NONE;
        if (ammo >= shotCost(cost, capacity)) return Action.FIRE;
        return hasLoaded ? Action.RELOAD : Action.DRY;
    }

    /** Bolts a shot really takes: never negative and never more than a full magazine, so it can always fire. */
    public static int shotCost(int cost, int capacity)
    {
        return cost < 0 ? 0 : (cost > capacity ? capacity : cost);
    }

    /** A stored mode, or repeating when it is unknown or its switch is off. */
    public static int effectiveMode(int stored, boolean grappleEnabled, boolean sniperEnabled)
    {
        return isModeEnabled(stored, grappleEnabled, sniperEnabled) ? stored : MODE_REPEATING;
    }

    /** True for a known mode whose switch is on. Repeating is always on. */
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

    /** The mode after {@code current} in the cycle, skipping the modes that are switched off. */
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

    /**
     * The sniper bipod is down while the player sneaks, stands on the ground, rides nothing and moved less than
     * {@code tolerance} blocks horizontally in the last tick. Moving, jumping or standing up folds it.
     *
     * @param dx movement along X in the last tick ({@code posX - prevPosX}); the same on server and client
     * @param dz movement along Z in the last tick
     */
    public static boolean bipodDeployed(boolean sneaking, boolean onGround, boolean riding, double dx, double dz, double tolerance)
    {
        return sneaking && onGround && !riding && dx * dx + dz * dz <= tolerance * tolerance;
    }

    /** True while the pause after a shot or a reload runs. A pause longer than {@code maxWait} is stale and ignored. */
    public static boolean isWaiting(long now, long readyAt, int maxWait)
    {
        long left = readyAt - now;
        return left > 0 && left <= maxWait;
    }

    /** True for {@link #RECOIL_TICKS} ticks after a shot: the model draws the released string. */
    public static boolean isRecoiling(long now, long lastShot)
    {
        long since = now - lastShot;
        return lastShot > 0 && since >= 0 && since < RECOIL_TICKS;
    }

    /**
     * Arrow damage factor that gives {@code muzzleDamage} when the bolt leaves at {@code speed}. Vanilla arrows deal
     * {@code ceil(speed * factor)}, so the damage drops a little as the bolt slows down. A small margin keeps a
     * float speed of 3.0000001 from rounding 4 up to 5.
     */
    public static double damageFactor(double muzzleDamage, double speed)
    {
        double target = muzzleDamage > DAMAGE_MARGIN ? muzzleDamage - DAMAGE_MARGIN : muzzleDamage;
        return speed > 0 ? target / speed : target;
    }

    private static final double DAMAGE_MARGIN = 0.01;

    /** Bolts loaded from a magazine with {@code charges} left: never more than the magazine holds, never negative. */
    public static int clampAmmo(int ammo, int capacity)
    {
        return ammo < 0 ? 0 : (ammo > capacity ? capacity : ammo);
    }

    /** True when the shot that brought the wear to {@code wear} breaks the crossbow. 0 durability never breaks. */
    public static boolean breaks(int wear, int durability)
    {
        return durability > 0 && wear >= durability;
    }

    /** The out-of-ammo chat message repeats at most once per {@code quietTicks} of dry clicking. */
    public static boolean shouldWarnEmpty(long now, long lastDry, int quietTicks)
    {
        return lastDry <= 0 || now - lastDry > quietTicks || now < lastDry;
    }
}
