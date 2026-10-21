package com.darkona.adventurebackpack.common;

public final class FlamethrowerSound
{
    private FlamethrowerSound() {}

    public static final int IGNITE = 1;
    public static final int PUT_OUT = 2;
    public static final int FIRE_START = 4;

    public static final String IGNITE_SOUND = "fire.ignite";
    public static final float IGNITE_VOLUME = 1.0F;
    public static final float IGNITE_PITCH = 1.0F;

    public static final String PUT_OUT_SOUND = "random.fizz";
    public static final float PUT_OUT_VOLUME = 0.5F;
    public static final float PUT_OUT_PITCH = 1.4F;

    public static final String NO_FUEL_SOUND = "random.fizz";
    public static final float NO_FUEL_VOLUME = 0.8F;
    public static final float NO_FUEL_PITCH = 0.5F;

    public static final String DRY_SOUND = "random.click";
    public static final float DRY_VOLUME = 0.5F;
    public static final float DRY_PITCH = 0.5F;

    public static final String FIRE_START_SOUND = "mob.ghast.fireball";
    public static final float FIRE_START_VOLUME = 0.5F;
    public static final float FIRE_START_PITCH = 0.9F;

    public static final String LOOP_SOUND = "flamethrowerloop";
    public static final float LOOP_VOLUME = 0.8F;
    public static final float LOOP_PITCH = 1.0F;

    public static final class State
    {
        private boolean seen;
        private int lastTick;
        private boolean lit;
        private boolean firing;

        public int update(int tick, boolean litNow, boolean firingNow)
        {
            boolean continuous = seen && tick == lastTick + 1;
            int events = continuous ? events(lit, litNow, firing, firingNow) : (firingNow ? FIRE_START : 0);
            seen = true;
            lastTick = tick;
            lit = litNow;
            firing = firingNow;
            return events;
        }
    }

    public static int events(boolean wasLit, boolean lit, boolean wasFiring, boolean firing)
    {
        int events = 0;
        if (!wasLit && lit) events |= IGNITE;
        if (wasLit && !lit) events |= PUT_OUT;
        if (!wasFiring && firing) events |= FIRE_START;
        return events;
    }

    public static boolean firing(boolean lit, boolean usingItem, boolean holdingNozzle)
    {
        return lit && usingItem && holdingNozzle;
    }

    public static boolean audible(boolean soundEnabled, float masterVolume)
    {
        return soundEnabled && masterVolume > 0.0F;
    }

    public static boolean loopContinues(boolean soundEnabled, float masterVolume, boolean alive, boolean firing)
    {
        return audible(soundEnabled, masterVolume) && alive && firing;
    }

    public static boolean dryTrigger(boolean usable, boolean wearingPack, boolean lit)
    {
        return usable && wearingPack && !lit;
    }

    public static float master(double configured)
    {
        if (!(configured > 0.0D)) return 0.0F;
        return configured >= 1.0D ? 1.0F : (float) configured;
    }
}
