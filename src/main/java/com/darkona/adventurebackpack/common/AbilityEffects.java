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
}
