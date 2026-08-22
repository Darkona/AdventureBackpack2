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

    /** Ticks after a shot during which the model shows the released string. */
    public static final int RECOIL_TICKS = 4;

    private CrossbowLogic() {}

    /**
     * Decides what a right click does.
     *
     * @param ammo         bolts in the magazine
     * @param capacity     bolts in a full magazine
     * @param now          current world tick
     * @param readyAt      world tick from which the crossbow can act again
     * @param maxWait      longest pause the config allows; a {@code readyAt} further away than this is stale
     *                     (the item came from another world) and is ignored
     * @param sneaking     the player sneaks (manual cartridge swap)
     * @param hasCartridge a cartridge is in the inventory, or the player is in creative mode
     */
    public static Action decide(int ammo, int capacity, long now, long readyAt, int maxWait, boolean sneaking, boolean hasCartridge)
    {
        if (isWaiting(now, readyAt, maxWait)) return Action.WAIT;
        if (sneaking) return ammo < capacity && hasCartridge ? Action.RELOAD : Action.NONE;
        if (ammo > 0) return Action.FIRE;
        return hasCartridge ? Action.RELOAD : Action.DRY;
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
