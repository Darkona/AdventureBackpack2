package com.darkona.adventurebackpack.common;

import com.darkona.adventurebackpack.config.ConfigHandler;

public final class NightVisionRefresh
{
    public static final int DURATION = 5000;
    public static final int REFRESH_BELOW = 260;

    private NightVisionRefresh() {}

    public static boolean needsRefresh(boolean active, int duration, int amplifier, int ourAmplifier)
    {
        if (!active) return true;
        return amplifier == ourAmplifier && duration < ConfigHandler.BAT_NIGHT_VISION_REFRESH_BELOW;
    }
}
