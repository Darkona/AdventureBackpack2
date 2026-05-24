package com.darkona.adventurebackpack.common;

/**
 * What the Piston Boots do to the wearer's step height each tick. The boots only touch the step height on a change
 * (start or stop of a sprint, boots taken off), so the vanilla 0.5 comes back when the boots come off mid-sprint and
 * another mod's step height is not overwritten every tick. Pure, so the unit test covers it.
 */
public final class PistonStep
{
    public static final int NONE = 0;
    /** Set the boots' step height (and give the sprint speed). */
    public static final int APPLY = 1;
    /** Put the vanilla step height back. */
    public static final int RESTORE = 2;

    public static final float VANILLA_STEP_HEIGHT = 0.5F;
    /** The sprint Speed lasts this long and is renewed when fewer ticks are left, instead of once per tick. */
    public static final int SPEED_TICKS = 20;
    public static final int SPEED_REFRESH_BELOW = 5;

    private PistonStep() {}

    /**
     * @param wearing  the boots are on the feet
     * @param boosting the boots are enabled and the player sprints
     * @param applied  the boots changed the step height and have not put it back yet
     */
    public static int action(boolean wearing, boolean boosting, boolean applied)
    {
        if (wearing && boosting) return APPLY;
        return applied ? RESTORE : NONE;
    }

    /** True when the sprint Speed must be (re)applied: absent, or our own one about to end. */
    public static boolean needsSpeed(boolean active, int amplifier, int ticksLeft)
    {
        if (!active) return true;
        return amplifier == 0 && ticksLeft < SPEED_REFRESH_BELOW;
    }

    /** True when a Speed effect looks like the boots' own, so stopping the sprint may remove it. */
    public static boolean isBootsSpeed(int amplifier, int ticksLeft)
    {
        return amplifier == 0 && ticksLeft <= SPEED_TICKS;
    }
}
