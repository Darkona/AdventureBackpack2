package com.darkona.adventurebackpack.common;

/**
 * Pure decisions of the Clockwork Crossbow and its Bolt Cartridges. No game classes here, so the unit tests run
 * without Minecraft. Times are world ticks ({@code World.getTotalWorldTime()}), never wall-clock time.
 */
public final class CrossbowLogic
{
    /** What one right click does. */
    public enum Action
    {
        /** Shoot one bolt from the magazine. */
        FIRE,
        /** Load a cartridge (the magazine is empty, or the player sneaks to swap it). */
        RELOAD,
        /** Empty magazine and no cartridge: click and tell the player. */
        DRY,
        /** Still in the pause after a shot or a reload. */
        WAIT,
        /** Nothing to do (sneak with a full magazine or without a cartridge). */
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
    public static Action decide(int ammo, int capacity, long now, long readyAt, int maxWait, boolean sneaking, boolean hasCartridge)
    {
        return decide(ammo, 1, capacity, now, readyAt, maxWait, sneaking, hasCartridge);
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
     * @param sneaking     the player sneaks (manual cartridge swap)
     * @param hasCartridge a cartridge is in the inventory, or the player is in creative mode
     */
    public static Action decide(int ammo, int cost, int capacity, long now, long readyAt, int maxWait, boolean sneaking, boolean hasCartridge)
    {
        if (isWaiting(now, readyAt, maxWait)) return Action.WAIT;
        if (sneaking) return ammo < capacity && hasCartridge ? Action.RELOAD : Action.NONE;
        if (ammo >= shotCost(cost, capacity)) return Action.FIRE;
        return hasCartridge ? Action.RELOAD : Action.DRY;
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

    /** Bolts loaded from a cartridge with {@code charges} left: never more than the magazine holds, never negative. */
    public static int clampAmmo(int ammo, int capacity)
    {
        return ammo < 0 ? 0 : (ammo > capacity ? capacity : ammo);
    }

    /** Charges of a cartridge: a stack without the tag is full; a tagged one has between 1 and {@code capacity}. */
    public static int cartridgeCharges(boolean hasTag, int storedCharges, int capacity)
    {
        if (!hasTag) return capacity;
        return storedCharges < 1 ? 1 : (storedCharges > capacity ? capacity : storedCharges);
    }

    /**
     * Charges to write on a cartridge ejected with {@code ammo} bolts in it, or -1 when it is full and gets no tag
     * (so it stacks with crafted cartridges). Callers only eject when {@code ammo > 0}.
     */
    public static int ejectedCharges(int ammo, int capacity)
    {
        return ammo >= capacity ? -1 : ammo;
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
