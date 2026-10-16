package com.darkona.adventurebackpack.common;

public final class BoatMotorSound
{
    private BoatMotorSound() {}

    public static final int PULL_MIN_AGE_TICKS = 5;

    public static final float LOOP_VOLUME_IDLE = 0.7F;
    public static final float LOOP_VOLUME_FULL = 1.0F;
    public static final float LOOP_PITCH_IDLE = 0.85F;
    public static final float LOOP_PITCH_FULL = 1.25F;

    public enum Change
    {
        NONE, START, STOP
    }

    public static Change change(boolean soundEnabled, boolean wasRunning, boolean running)
    {
        if (!soundEnabled || wasRunning == running) return Change.NONE;
        return running ? Change.START : Change.STOP;
    }

    public static final String FAILED_START_SOUND = "random.click";
    public static final float FAILED_START_VOLUME = 0.5F;
    public static final float FAILED_START_PITCH = 0.5F;

    public static boolean failedStartClick(boolean soundEnabled, float masterVolume, boolean wasRunning, boolean running)
    {
        return soundEnabled && masterVolume > 0.0F && !wasRunning && !running;
    }

    public static boolean pullAudible(int ticksExisted)
    {
        return ticksExisted >= PULL_MIN_AGE_TICKS;
    }

    public static boolean loopContinues(boolean soundEnabled, boolean running, boolean boatAlive)
    {
        return soundEnabled && running && boatAlive;
    }

    public static final int STALE_LIMIT = 3;

    public static int nextStale(int lastAge, int age, int stale)
    {
        return age != lastAge ? 0 : Math.min(stale + 1, STALE_LIMIT);
    }

    public static boolean gone(int stale)
    {
        return stale >= STALE_LIMIT;
    }

    public static float speedFraction(double speed, double topSpeed)
    {
        if (!(topSpeed > 0.0D) || !(speed > 0.0D)) return 0.0F;
        return speed >= topSpeed ? 1.0F : (float) (speed / topSpeed);
    }

    public static float master(double configured)
    {
        if (!(configured > 0.0D)) return 0.0F;
        return configured >= 1.0D ? 1.0F : (float) configured;
    }

    public static float loopVolume(double configured, float speedFraction)
    {
        return master(configured) * (LOOP_VOLUME_IDLE + (LOOP_VOLUME_FULL - LOOP_VOLUME_IDLE) * clamp01(speedFraction));
    }

    public static float loopPitch(float speedFraction)
    {
        return LOOP_PITCH_IDLE + (LOOP_PITCH_FULL - LOOP_PITCH_IDLE) * clamp01(speedFraction);
    }

    private static float clamp01(float v)
    {
        return v <= 0.0F ? 0.0F : Math.min(v, 1.0F);
    }
}
