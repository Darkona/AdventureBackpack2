package com.darkona.adventurebackpack.common;

public final class CrossbowAnimation
{
    public static final int CHAMBERS = 6;
    public static final float STEP_DEGREES = 360F / CHAMBERS;

    public static final float STRING_Y = 4.0F;
    public static final float STRING_Z_FIRED = -3.0F;
    public static final float STRING_Z_COCKED = 1.5F;
    public static final float TIP_X = 14.1856F;
    public static final float TIP_Y = 1.5F;
    public static final float TIP_Z = -4.8026F;
    public static final float STRING_BOX_LENGTH = 10F;

    public static final float MAGAZINE_EJECTED_AT = 0.25F;
    public static final float MAGAZINE_SEATS_AT = 0.7F;
    public static final float SLOT_Y = 11F;
    public static final float MAGAZINE_HEIGHT_EMPTY = 2F;
    public static final float MAGAZINE_HEIGHT_LOADED = 4F;
    public static final float INSERT_FROM_X = -7F;
    public static final float INSERT_FROM_Y = -10F;
    public static final float INSERT_OVER_Y = 1F;
    public static final float EJECT_SIDE = 12F;
    public static final float EJECT_ROLL = 70F;
    public static final float EJECT_LOW_Y = -12F;
    private static final float EJECT_CLEAR = 0.35F;
    private static final float EJECT_FADE = 0.75F;
    private static final float INSERT_RISE = 0.45F;
    private static final float INSERT_SLIDE = 0.65F;

    public static final int MAG_X = 0;
    public static final int MAG_Y = 1;
    public static final int MAG_ROLL = 2;
    public static final int MAG_SCALE = 3;
    public static final int MAG_CLIPPED = 4;
    public static final int MAG_SIZE = 5;

    public static final float NEVER = 1.0E9F;

    public static final int FLETCH_COLOR = 0xD8D8D8;

    private CrossbowAnimation() {}

    public static float sinceShot(long now, float partialTick, long lastShot)
    {
        if (lastShot <= 0) return NEVER;
        long since = now - lastShot;
        if (since < 0 || since > CrossbowLogic.RECOIL_TICKS) return NEVER;
        return since + partialTick;
    }

    public static float recoil(float sinceShot)
    {
        if (sinceShot < 0 || sinceShot >= CrossbowLogic.RECOIL_TICKS) return 0F;
        return 1F - sinceShot / CrossbowLogic.RECOIL_TICKS;
    }

    public static float stringZ(float sinceShot)
    {
        return STRING_Z_COCKED + (STRING_Z_FIRED - STRING_Z_COCKED) * recoil(sinceShot);
    }

    public static float drumDegrees(int ammo, int capacity, float sinceShot, float reloadProgress)
    {
        if (reloadProgress >= 0F && reloadProgress < 1F) return -360F * (1F - reloadProgress);
        int shots = shotsFired(ammo, capacity);
        float step = shots % CHAMBERS;
        if (shots > 0) step -= recoil(sinceShot);
        return -step * STEP_DEGREES;
    }

    public static boolean chamberLoaded(int chamber, int ammo, int capacity)
    {
        int shots = shotsFired(ammo, capacity);
        int visible = ammo < CHAMBERS ? ammo : CHAMBERS;
        int index = ((chamber - shots) % CHAMBERS + CHAMBERS) % CHAMBERS;
        return index < visible;
    }

    public static int firingChamber(int ammo, int capacity)
    {
        return shotsFired(ammo, capacity) % CHAMBERS;
    }

    public static int feedAmmo()
    {
        return CHAMBERS;
    }

    public static int feedCapacity(int quiverShots)
    {
        if (quiverShots <= 0) return CHAMBERS;
        return 2 * CHAMBERS + quiverShots % CHAMBERS;
    }

    public static final float LEG_FOLDED_X = -90F;
    public static final float LEG_SPLAY = 15F;
    public static final float BIPOD_TICKS = 6F;

    public static float legAngleX(float pose)
    {
        return LEG_FOLDED_X * (1F - clamp01(pose));
    }

    public static float legAngleZ(float pose, boolean right)
    {
        return (right ? LEG_SPLAY : -LEG_SPLAY) * clamp01(pose);
    }

    public static float bipodPose(float from, boolean deployed, float elapsed)
    {
        float target = deployed ? 1F : 0F;
        if (elapsed <= 0F) return clamp01(from);
        if (elapsed >= BIPOD_TICKS) return target;
        return clamp01(from) + (target - clamp01(from)) * (elapsed / BIPOD_TICKS);
    }

    public static final int POSE_RIGHT_X = 0, POSE_RIGHT_Y = 1, POSE_RIGHT_Z = 2;
    public static final int POSE_LEFT_X = 3, POSE_LEFT_Y = 4, POSE_LEFT_Z = 5;
    public static final int POSE_RIGHT_SHOULDER_Z = 6, POSE_LEFT_SHOULDER_Z = 7;
    public static final int POSE_WEAPON_PITCH = 8, POSE_WEAPON_YAW = 9;
    public static final int POSE_KICK_BACK = 10;
    public static final int POSE_SIZE = 11;

    public static final float HOLD_RIGHT_YAW = -0.3F;
    public static final float HOLD_LEFT_YAW = 0.45F;
    public static final float HOLD_RAISE = 0.2F;
    public static final float HOLD_LEFT_SHOULDER_Z = -2.5F;
    public static final float HOLD_RIGHT_SHOULDER_Z = 1F;
    public static final float SNIPER_LOWER = 0.25F;
    public static final float KICK_ARMS = 0.18F;
    public static final float KICK_PITCH = 0.12F;
    public static final float KICK_BACK = 1.5F;
    public static final float SWAY = 0.03F;

    public static void holdPose(float headPitch, float headYaw, boolean steady, float recoil, float ageTicks, float[] out)
    {
        float kick = clamp01(recoil);
        float pitch = -HALF_PI - HOLD_RAISE + headPitch - kick * KICK_ARMS + (steady ? SNIPER_LOWER : 0F);
        float swayZ = steady ? 0F : (float) Math.cos(ageTicks * 0.09F) * SWAY;
        float swayX = steady ? 0F : (float) Math.sin(ageTicks * 0.067F) * SWAY;
        out[POSE_RIGHT_X] = pitch + swayX;
        out[POSE_RIGHT_Y] = headYaw + HOLD_RIGHT_YAW;
        out[POSE_RIGHT_Z] = swayZ;
        out[POSE_LEFT_X] = pitch + swayX;
        out[POSE_LEFT_Y] = headYaw + HOLD_LEFT_YAW;
        out[POSE_LEFT_Z] = swayZ;
        out[POSE_RIGHT_SHOULDER_Z] = HOLD_RIGHT_SHOULDER_Z;
        out[POSE_LEFT_SHOULDER_Z] = HOLD_LEFT_SHOULDER_Z;
        out[POSE_WEAPON_PITCH] = headPitch + swayX - kick * (KICK_ARMS + KICK_PITCH);
        out[POSE_WEAPON_YAW] = headYaw;
        out[POSE_KICK_BACK] = kick * KICK_BACK;
    }

    private static final float HALF_PI = (float) (Math.PI / 2D);

    private static float clamp01(float value)
    {
        return value < 0F ? 0F : (value > 1F ? 1F : value);
    }

    public static int shotsFired(int ammo, int capacity)
    {
        int shots = capacity - ammo;
        return shots < 0 ? 0 : shots;
    }

    public static float reloadProgress(long now, float partialTick, long lastReload, int reloadTicks)
    {
        if (lastReload <= 0 || reloadTicks <= 0) return -1F;
        long since = now - lastReload;
        if (since < 0 || since >= reloadTicks) return -1F;
        float progress = (since + partialTick) / reloadTicks;
        return progress >= 1F ? -1F : progress;
    }

    public static boolean spentMagazine(float reloadProgress, float[] out)
    {
        if (reloadProgress < 0F || reloadProgress >= MAGAZINE_EJECTED_AT) return false;
        float t = reloadProgress / MAGAZINE_EJECTED_AT;
        float h = MAGAZINE_HEIGHT_EMPTY;
        float clearTop = 1F;
        float c2 = EJECT_CLEAR * EJECT_CLEAR;
        float b = (h + clearTop - (h + EJECT_LOW_Y) * EJECT_CLEAR) / (EJECT_CLEAR - c2);
        float a = h + EJECT_LOW_Y + b;
        float side = t <= EJECT_CLEAR ? 0F : (t - EJECT_CLEAR) / (1F - EJECT_CLEAR);
        out[MAG_X] = EJECT_SIDE * side;
        out[MAG_Y] = -h + a * t - b * t * t;
        out[MAG_ROLL] = -EJECT_ROLL * side;
        out[MAG_SCALE] = t <= EJECT_FADE ? 1F : 1F - (t - EJECT_FADE) / (1F - EJECT_FADE);
        out[MAG_CLIPPED] = side > 0F ? 0F : 1F;
        return true;
    }

    public static boolean newMagazine(float reloadProgress, float[] out)
    {
        if (reloadProgress < MAGAZINE_EJECTED_AT || reloadProgress >= MAGAZINE_SEATS_AT) return false;
        float u = (reloadProgress - MAGAZINE_EJECTED_AT) / (MAGAZINE_SEATS_AT - MAGAZINE_EJECTED_AT);
        if (u < INSERT_RISE)
        {
            float k = u / INSERT_RISE;
            float rest = (1F - k) * (1F - k);
            out[MAG_X] = INSERT_FROM_X;
            out[MAG_Y] = INSERT_OVER_Y + (INSERT_FROM_Y - INSERT_OVER_Y) * rest;
            out[MAG_CLIPPED] = 0F;
        } else if (u < INSERT_SLIDE)
        {
            float k = (u - INSERT_RISE) / (INSERT_SLIDE - INSERT_RISE);
            out[MAG_X] = INSERT_FROM_X * (1F - k * k * (3F - 2F * k));
            out[MAG_Y] = INSERT_OVER_Y;
            out[MAG_CLIPPED] = 0F;
        } else
        {
            float k = (u - INSERT_SLIDE) / (1F - INSERT_SLIDE);
            out[MAG_X] = 0F;
            out[MAG_Y] = INSERT_OVER_Y - (INSERT_OVER_Y + MAGAZINE_HEIGHT_LOADED) * k * k;
            out[MAG_CLIPPED] = 1F;
        }
        out[MAG_ROLL] = 0F;
        out[MAG_SCALE] = 1F;
        return true;
    }

    public static boolean boltsVisible(float reloadProgress)
    {
        return reloadProgress < 0F || reloadProgress >= MAGAZINE_SEATS_AT;
    }

    public static int magazineBolts(int ammo)
    {
        return ammo < 0 ? 0 : (ammo > 3 ? 3 : ammo);
    }

    public static void stringTransform(float tipX, float tipY, float tipZ, float nockX, float nockY, float nockZ, float[] out)
    {
        float dx = nockX - tipX;
        float dy = nockY - tipY;
        float dz = nockZ - tipZ;
        float length = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        float cos = length > 0F ? dy / length : 1F;
        if (cos > 1F) cos = 1F;
        if (cos < -1F) cos = -1F;
        out[0] = (float) Math.toDegrees(Math.acos(cos));
        out[1] = dz;
        out[2] = -dx;
        out[3] = length;
    }
}
