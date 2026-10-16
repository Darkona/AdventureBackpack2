package com.darkona.adventurebackpack.common;

public final class SpiderCare
{
    public enum Hand
    {
        EMPTY, HEAL_ITEM, TAME_ITEM, OTHER
    }

    public enum Action
    {
        NONE, RELEASE, HEAL, TOGGLE_SIT, MOUNT
    }

    private SpiderCare() {}

    public static Action interactAction(boolean owner, boolean ridden, boolean sneaking, Hand hand, boolean canRide,
                                        boolean belowMaxHealth)
    {
        if (!owner || ridden) return Action.NONE;
        if (sneaking) return hand == Hand.TAME_ITEM ? Action.RELEASE : Action.TOGGLE_SIT;
        if (hand == Hand.HEAL_ITEM) return belowMaxHealth ? Action.HEAL : Action.NONE;
        return canRide ? Action.MOUNT : Action.NONE;
    }

    public static float healedHealth(float health, float max, float amount)
    {
        return Math.min(max, health + Math.max(0F, amount));
    }

    public static boolean canHeal(float health, float max)
    {
        return health < max;
    }

    public static float tamedHealth(float tamedMax)
    {
        return tamedMax;
    }

    public static float clampLoadedHealth(float health, float max)
    {
        return health > max ? max : health;
    }

    public static byte withBit(byte flags, int mask, boolean on)
    {
        return (byte) (on ? (flags | mask) : (flags & ~mask));
    }
}
