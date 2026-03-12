package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * When the Bat backpack must re-apply its night vision (#102). Vanilla fades the effect in its last 200 ticks,
 * which made the sky flicker, so it is renewed before that window and never in between.
 */
public final class NightVisionRefresh
{
    // Defaults of the abilities.bat config keys; the values in use live in ConfigHandler.
    public static final int DURATION = 5000;
    public static final int REFRESH_BELOW = 260;

    private NightVisionRefresh() {}

    public static boolean needsRefresh(boolean active, int duration, int amplifier, int ourAmplifier)
    {
        if (!active) return true;
        return amplifier == ourAmplifier && duration < ConfigHandler.BAT_NIGHT_VISION_REFRESH_BELOW;
    }
}
