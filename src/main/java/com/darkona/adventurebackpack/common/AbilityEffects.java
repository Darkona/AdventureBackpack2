package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Levels and timing of the potion effects that backpack abilities give.
 * Strength and regeneration scale with the amplifier, so the Dragon's effects use real levels
 * (Strength II, Regeneration I) instead of the -5 marker the other abilities use (#122, #105).
 */
public final class AbilityEffects
{
    // Defaults of the abilities.dragon config keys; the values in use live in ConfigHandler.
    public static final int DRAGON_STRENGTH_AMPLIFIER = 1;
    public static final int DRAGON_REGENERATION_AMPLIFIER = 0;
    public static final int DRAGON_EFFECT_TICKS = 5000;
    /** Ticks left under which an ability renews its effect. */
    public static final int EFFECT_REFRESH_TICKS = 40;

    private AbilityEffects()
    {
    }

    public static boolean needsRefresh(int ticksLeft)
    {
        return ticksLeft < ConfigHandler.ABILITY_EFFECT_REFRESH_TICKS;
    }

    /** True when the effect is the one the Dragon backpack gives: same level and no longer than it lasts. */
    public static boolean isDragonEffect(int amplifier, int ticksLeft, int dragonAmplifier)
    {
        return amplifier == dragonAmplifier && ticksLeft <= ConfigHandler.DRAGON_EFFECT_TICKS;
    }

    /** Speed and Jump of the Rainbow's Nyan run last this long and are renewed every tick while it runs. */
    public static final int RAINBOW_EFFECT_TICKS = 40;
    /** Amplifier of the long Speed effect older versions gave the Rainbow wearer (a slowdown, see itemRainbow). */
    public static final int STALE_RAINBOW_MARKER = -5;

    /**
     * True when the Speed or Jump effect is the Rainbow's own: its level and no longer than the run renews it, so a
     * Speed III potion or a beacon is not taken away when the backpack comes off.
     */
    public static boolean isRainbowEffect(int amplifier, int ticksLeft, int rainbowAmplifier)
    {
        return amplifier == rainbowAmplifier && ticksLeft <= RAINBOW_EFFECT_TICKS;
    }

    public static boolean isStaleRainbowMarker(int amplifier)
    {
        return amplifier == STALE_RAINBOW_MARKER;
    }

    /*
     * Squid water breathing (in water) and Slime Speed II (full moon nights) used to be given for 1 tick on every
     * tick, which sent one effect packet per tick to the wearer and the players around. They now last a short while
     * and are renewed only near their end: one packet every SHORT_EFFECT_TICKS - SHORT_EFFECT_REFRESH_BELOW ticks.
     * They are taken away explicitly when the player leaves the water, when the full moon night ends and when the
     * backpack comes off, so the length does not need to be long. Neither effect fades on screen, so no flicker margin
     * is needed (night vision needs one, see NightVisionRefresh). Constants, not config: there is no analogous key in
     * abilities.squid or abilities.slime and the numbers only trade packets for how long a stray effect can linger.
     */
    /** Length of the Squid's water breathing and the Slime's full moon Speed. */
    public static final int SHORT_EFFECT_TICKS = 100;
    /** Ticks left under which those effects are renewed. Must stay below SHORT_EFFECT_TICKS. */
    public static final int SHORT_EFFECT_REFRESH_BELOW = 40;
    /** Amplifier marker of the Squid's water breathing (the effect does not scale, so the level carries the owner). */
    public static final int SQUID_WATER_BREATHING_MARKER = -5;
    /** Speed II: a real level, because Speed scales with it, so the Slime's own effect is also told apart by length. */
    public static final int SLIME_SPEED_AMPLIFIER = 1;

    /**
     * True when a short ability effect must be (re)applied: it is missing, or it is the ability's own (same amplifier)
     * and about to run out. An effect of another level from another source is never overridden.
     */
    public static boolean needsShortRefresh(boolean active, int ticksLeft, int amplifier, int ownAmplifier)
    {
        if (!active) return true;
        return amplifier == ownAmplifier && ticksLeft < SHORT_EFFECT_REFRESH_BELOW;
    }

    /**
     * True when the Squid gives its water breathing and night vision: in water, or anywhere when
     * abilities.squid.worksOnLand is on (#151, merfolk mods that let the player live in the air like in water).
     * On land the effects use the same short refresh, so they still go away soon after the backpack comes off.
     */
    public static boolean squidActive(boolean inWater, boolean worksOnLand)
    {
        return inWater || worksOnLand;
    }

    /** True for the Squid's own water breathing: only its -5 marker, never a potion's. */
    public static boolean isSquidWaterBreathing(int amplifier)
    {
        return amplifier == SQUID_WATER_BREATHING_MARKER;
    }

    /**
     * True when the Speed effect is the Slime's own: Speed II and no longer than the Slime gives it, so a Speed II
     * potion or a beacon is not taken away when the full moon night ends or the backpack comes off.
     */
    public static boolean isSlimeSpeed(int amplifier, int ticksLeft)
    {
        return amplifier == SLIME_SPEED_AMPLIFIER && ticksLeft <= SHORT_EFFECT_TICKS;
    }
}
