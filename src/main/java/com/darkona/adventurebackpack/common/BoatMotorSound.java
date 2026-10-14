package com.darkona.adventurebackpack.common;

/**
 * When and how loud the outboard motor of the Inflatable Boat sounds, as pure functions (inflatableboat.motor.sound).
 * The client watches the running flag of the boat's DataWatcher: one looping sound starts when it turns on and ends by
 * itself when it turns off or the boat is gone; a cough plays once at the boat when it turns off.
 */
public final class BoatMotorSound
{
    private BoatMotorSound() {}

    /** A boat that is younger than this on the client (ticks) appeared already running: no cord pull for it. */
    public static final int PULL_MIN_AGE_TICKS = 5;

    /** Volume and pitch of the loop at standstill and at the motor's top speed (the master volume multiplies them). */
    public static final float LOOP_VOLUME_IDLE = 0.7F;
    public static final float LOOP_VOLUME_FULL = 1.0F;
    public static final float LOOP_PITCH_IDLE = 0.85F;
    public static final float LOOP_PITCH_FULL = 1.25F;

    /** What the client does on a change of the running flag. */
    public enum Change
    {
        NONE, START, STOP
    }

    /** The change the client reacts to between the last flag it saw and the one it sees now. */
    public static Change change(boolean soundEnabled, boolean wasRunning, boolean running)
    {
        if (!soundEnabled || wasRunning == running) return Change.NONE;
        return running ? Change.START : Change.STOP;
    }

    /** The dry click of a failed start: vanilla random.click, quiet and low, so it does not sound like a shot. */
    public static final String FAILED_START_SOUND = "random.click";
    public static final float FAILED_START_VOLUME = 0.5F;
    public static final float FAILED_START_PITCH = 0.5F;

    /**
     * True when the pilot's motor key did not start the motor (no fuel, out of the water or motor disabled): a dry click
     * tells the pilot, with or without the chat messages. A key that stops a running motor plays the stop sound instead.
     */
    public static boolean failedStartClick(boolean soundEnabled, float masterVolume, boolean wasRunning, boolean running)
    {
        return soundEnabled && masterVolume > 0.0F && !wasRunning && !running;
    }

    /** True when the one-shot cord pull plays on a start: not for a boat that was already running when it appeared. */
    public static boolean pullAudible(int ticksExisted)
    {
        return ticksExisted >= PULL_MIN_AGE_TICKS;
    }

    /** True while the looping sound goes on: the sound is on, the motor runs and the boat is still in the world. */
    public static boolean loopContinues(boolean soundEnabled, boolean running, boolean boatAlive)
    {
        return soundEnabled && running && boatAlive;
    }

    /** Ticks in a row a boat may go without ticking before its loop counts it as gone (unloaded, other dimension). */
    public static final int STALE_LIMIT = 3;

    /** Counts the sound updates in a row in which the boat's age did not move: 0 when it did, one more when not. */
    public static int nextStale(int lastAge, int age, int stale)
    {
        return age != lastAge ? 0 : Math.min(stale + 1, STALE_LIMIT);
    }

    /** True when the boat has not ticked for {@link #STALE_LIMIT} sound updates: it left the world without dying. */
    public static boolean gone(int stale)
    {
        return stale >= STALE_LIMIT;
    }

    /** Horizontal speed over the motor's top speed, clamped to 0..1 (0 when the top speed is not positive). */
    public static float speedFraction(double speed, double topSpeed)
    {
        if (!(topSpeed > 0.0D) || !(speed > 0.0D)) return 0.0F;
        return speed >= topSpeed ? 1.0F : (float) (speed / topSpeed);
    }

    /** Master volume (inflatableboat.motor.soundVolume) clamped to 0..1; a bad value counts as the default's silence. */
    public static float master(double configured)
    {
        if (!(configured > 0.0D)) return 0.0F;
        return configured >= 1.0D ? 1.0F : (float) configured;
    }

    /** Volume of the loop: a little quieter at standstill, full at the top speed. */
    public static float loopVolume(double configured, float speedFraction)
    {
        return master(configured) * (LOOP_VOLUME_IDLE + (LOOP_VOLUME_FULL - LOOP_VOLUME_IDLE) * clamp01(speedFraction));
    }

    /** Pitch of the loop: lower at standstill, higher at the top speed. */
    public static float loopPitch(float speedFraction)
    {
        return LOOP_PITCH_IDLE + (LOOP_PITCH_FULL - LOOP_PITCH_IDLE) * clamp01(speedFraction);
    }

    private static float clamp01(float v)
    {
        return v <= 0.0F ? 0.0F : Math.min(v, 1.0F);
    }
}
