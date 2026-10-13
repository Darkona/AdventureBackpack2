package com.darkona.adventurebackpack.common;

/**
 * What a right click by a player does to a tamed spider, and the health rules. Pure: no Minecraft classes.
 * <p>
 * Gestures, first match wins: not the owner, or the spider carries someone: nothing. Sneaking with the tame item
 * (a spider eye): set it free. Sneaking with anything else: sit down or stand up. The heal item (rotten flesh) on a
 * hurt spider: heal it (at full health it does nothing, like a wolf). Wearing the Spider Backpack with the ability on:
 * ride it. Anything else: nothing.
 */
public final class SpiderCare
{
    /** What the player holds, as far as a tamed spider cares. */
    public enum Hand
    {
        EMPTY, HEAL_ITEM, TAME_ITEM, OTHER
    }

    public enum Action
    {
        NONE, RELEASE, HEAL, TOGGLE_SIT, MOUNT
    }

    private SpiderCare() {}

    /**
     * @param owner          the clicking player owns the spider
     * @param ridden         someone rides the spider
     * @param sneaking       the player sneaks
     * @param hand           what the player holds
     * @param canRide        the player wears the Spider Backpack and the ability is on
     * @param belowMaxHealth the spider is hurt
     */
    public static Action interactAction(boolean owner, boolean ridden, boolean sneaking, Hand hand, boolean canRide,
                                        boolean belowMaxHealth)
    {
        if (!owner || ridden) return Action.NONE;
        if (sneaking) return hand == Hand.TAME_ITEM ? Action.RELEASE : Action.TOGGLE_SIT;
        if (hand == Hand.HEAL_ITEM) return belowMaxHealth ? Action.HEAL : Action.NONE;
        return canRide ? Action.MOUNT : Action.NONE;
    }

    /** Health after one heal, never above the maximum. */
    public static float healedHealth(float health, float max, float amount)
    {
        return Math.min(max, health + Math.max(0F, amount));
    }

    public static boolean canHeal(float health, float max)
    {
        return health < max;
    }

    /** Health of a freshly tamed spider: always full, whatever the wild one had left. */
    public static float tamedHealth(float tamedMax)
    {
        return tamedMax;
    }

    /** Health after loading with a maximum that may have changed in the config: never above it, never zero. */
    public static float clampLoadedHealth(float health, float max)
    {
        return health > max ? max : health;
    }

    /** {@code flags} with the bits of {@code mask} set or cleared, the other bits untouched. */
    public static byte withBit(byte flags, int mask, boolean on)
    {
        return (byte) (on ? (flags | mask) : (flags & ~mask));
    }
}
