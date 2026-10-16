package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;

public final class AbilityEffects
{
    public static final int DRAGON_STRENGTH_AMPLIFIER = 1;
    public static final int DRAGON_REGENERATION_AMPLIFIER = 0;
    public static final int DRAGON_EFFECT_TICKS = 5000;
    public static final int EFFECT_REFRESH_TICKS = 40;

    private AbilityEffects()
    {
    }

    public static boolean needsRefresh(int ticksLeft)
    {
        return ticksLeft < ConfigHandler.ABILITY_EFFECT_REFRESH_TICKS;
    }

    public static boolean isDragonEffect(int amplifier, int ticksLeft, int dragonAmplifier)
    {
        return amplifier == dragonAmplifier && ticksLeft <= ConfigHandler.DRAGON_EFFECT_TICKS;
    }

    public static final int RAINBOW_EFFECT_TICKS = 40;
    public static final int RAINBOW_REFRESH_BELOW = RAINBOW_EFFECT_TICKS / 2;

    public static boolean needsRainbowRefresh(boolean active, int amplifier, int ticksLeft, int rainbowAmplifier)
    {
        if (!active || amplifier < rainbowAmplifier) return true;
        return amplifier == rainbowAmplifier && ticksLeft < RAINBOW_REFRESH_BELOW;
    }
    public static final int STALE_RAINBOW_MARKER = -5;

    public static boolean isRainbowEffect(int amplifier, int ticksLeft, int rainbowAmplifier)
    {
        return amplifier == rainbowAmplifier && ticksLeft <= RAINBOW_EFFECT_TICKS;
    }

    public static boolean isStaleRainbowMarker(int amplifier)
    {
        return amplifier == STALE_RAINBOW_MARKER;
    }

    public static final int SHORT_EFFECT_TICKS = 100;
    public static final int SHORT_EFFECT_REFRESH_BELOW = 40;
    public static final int SQUID_WATER_BREATHING_MARKER = -5;
    public static final int SLIME_SPEED_AMPLIFIER = 1;

    public static boolean needsShortRefresh(boolean active, int ticksLeft, int amplifier, int ownAmplifier)
    {
        if (!active) return true;
        return amplifier == ownAmplifier && ticksLeft < SHORT_EFFECT_REFRESH_BELOW;
    }

    public static boolean squidActive(boolean inWater, boolean worksOnLand)
    {
        return inWater || worksOnLand;
    }

    public static boolean isSquidWaterBreathing(int amplifier)
    {
        return amplifier == SQUID_WATER_BREATHING_MARKER;
    }

    public static boolean isSlimeSpeed(int amplifier, int ticksLeft)
    {
        return amplifier == SLIME_SPEED_AMPLIFIER && ticksLeft <= SHORT_EFFECT_TICKS;
    }

    public static boolean blazePunchIgnites(boolean handEmpty, boolean heldIsWeapon, boolean nonWeaponsCount)
    {
        return handEmpty || (nonWeaponsCount && !heldIsWeapon);
    }
}
