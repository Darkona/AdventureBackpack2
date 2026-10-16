package com.darkona.adventurebackpack.common;

public final class PistonStep
{
    public static final int NONE = 0;
    public static final int APPLY = 1;
    public static final int RESTORE = 2;

    public static final float VANILLA_STEP_HEIGHT = 0.5F;
    public static final int SPEED_TICKS = 20;
    public static final int SPEED_REFRESH_BELOW = 5;

    private PistonStep() {}

    public static int action(boolean wearing, boolean boosting, boolean applied)
    {
        if (wearing && boosting) return APPLY;
        return applied ? RESTORE : NONE;
    }

    public static boolean stepping(boolean enabled, boolean sprinting, boolean onlyWhileSprinting)
    {
        return enabled && (sprinting || !onlyWhileSprinting);
    }

    public static boolean needsSpeed(boolean active, int amplifier, int ticksLeft)
    {
        if (!active) return true;
        return amplifier == 0 && ticksLeft < SPEED_REFRESH_BELOW;
    }

    public static boolean isBootsSpeed(int amplifier, int ticksLeft)
    {
        return amplifier == 0 && ticksLeft <= SPEED_TICKS;
    }
}
