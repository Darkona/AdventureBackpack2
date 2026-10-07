package com.darkona.adventurebackpack.common;

/**
 * Pure animation math of the Clockwork Crossbow model: recoil, string travel, drum step, reload. No
 * game classes, so the unit tests run without Minecraft and the renderer only does arithmetic per frame (performance
 * contract, tier 4: no allocation). Times are world ticks plus the partial tick of the frame.
 * <p>
 * Model units are sixteenths of a block, +Y up, -Z forward (muzzle), +X the shooter's right. The constants below
 * come from the model geometry in {@code ModelClockworkCrossbow}.
 */
public final class CrossbowAnimation
{
    /** Chambers in the drum. The magazine may hold more bolts than this; the drum shows at most this many. */
    public static final int CHAMBERS = 6;
    public static final float STEP_DEGREES = 360F / CHAMBERS;

    /** Where the string sits: on the rail, at the prod right after a shot, at the latch when cocked. */
    public static final float STRING_Y = 4.0F;
    public static final float STRING_Z_FIRED = -3.0F;
    public static final float STRING_Z_COCKED = 1.5F;
    /** Tip of the right limb (the left one mirrors X). */
    public static final float TIP_X = 14.1856F;
    public static final float TIP_Y = 1.5F;
    public static final float TIP_Z = -4.8026F;
    /** Length of the string box in the model; the renderer scales it to the real tip-to-latch distance. */
    public static final float STRING_BOX_LENGTH = 10F;

    /**
     * Reload, in parts of {@code crossbow.reloadTicks}: the spent magazine (if the reload took one out) pops up out of
     * the slot on top of the drum and falls away to the right until {@link #MAGAZINE_EJECTED_AT}; the new one comes in
     * from the left, sits on the slot and is pushed down into the drum until {@link #MAGAZINE_SEATS_AT}, when the
     * drum shows its bolts again. Offsets are in model units from the magazine's seat (bottom on the drum top).
     */
    public static final float MAGAZINE_EJECTED_AT = 0.3F;
    public static final float MAGAZINE_SEATS_AT = 0.65F;
    /** Model Y of the slot: the top of the drum. Whatever of a magazine is below it is inside the drum (not drawn). */
    public static final float SLOT_Y = 11F;
    /** Height of the magazine above its bottom: the brass case alone, and with the bolts and fletching lying on it. */
    public static final float MAGAZINE_HEIGHT_EMPTY = 2F;
    public static final float MAGAZINE_HEIGHT_LOADED = 4F;
    /** Where the new magazine starts: to the left of the slot and a little above it. */
    public static final float INSERT_FROM_X = -7F;
    public static final float INSERT_FROM_Y = 3F;
    /** How far right the spent magazine flies, how much it rolls (degrees) and how low it falls before it is gone. */
    public static final float EJECT_SIDE = 12F;
    public static final float EJECT_ROLL = 70F;
    public static final float EJECT_LOW_Y = -12F;
    /** Part of the eject spent coming straight up out of the slot; the rest it flies off to the side. */
    private static final float EJECT_CLEAR = 0.35F;
    /** Part of the eject after which the spent magazine shrinks away. */
    private static final float EJECT_FADE = 0.75F;
    /** Part of the insert spent moving onto the slot; the rest it is pushed down into the drum. */
    private static final float INSERT_SLIDE = 0.5F;

    /** Indices of the magazine pose written by {@link #spentMagazine} and {@link #newMagazine}. */
    public static final int MAG_X = 0;
    public static final int MAG_Y = 1;
    public static final int MAG_ROLL = 2;
    public static final int MAG_SCALE = 3;
    /** 1 while the magazine is in the slot column (cut at {@link #SLOT_Y}), 0 once it is clear of the crossbow. */
    public static final int MAG_CLIPPED = 4;
    public static final int MAG_SIZE = 5;

    /** Returned by {@link #sinceShot} when the stack never fired or its clock is from another world. */
    public static final float NEVER = 1.0E9F;

    /** Fletching tint of the bolts in the drum, packed RGB. */
    public static final int FLETCH_COLOR = 0xD8D8D8;

    private CrossbowAnimation() {}

    /** Ticks since the last shot including the partial tick, or {@link #NEVER}. */
    public static float sinceShot(long now, float partialTick, long lastShot)
    {
        if (lastShot <= 0) return NEVER;
        long since = now - lastShot;
        if (since < 0 || since > CrossbowLogic.RECOIL_TICKS) return NEVER;
        return since + partialTick;
    }

    /** 1 right after the shot, falling to 0 over {@link CrossbowLogic#RECOIL_TICKS}. */
    public static float recoil(float sinceShot)
    {
        if (sinceShot < 0 || sinceShot >= CrossbowLogic.RECOIL_TICKS) return 0F;
        return 1F - sinceShot / CrossbowLogic.RECOIL_TICKS;
    }

    /** Z of the string: it snaps to the prod on the shot and the clockwork pulls it back to the latch. */
    public static float stringZ(float sinceShot)
    {
        return STRING_Z_COCKED + (STRING_Z_FIRED - STRING_Z_COCKED) * recoil(sinceShot);
    }

    /**
     * Drum angle in degrees around Z: one step per shot, the newest step animated during the recoil, a full turn
     * backwards during a reload. Negative angles turn the drum so the next loaded chamber comes to the bottom.
     */
    public static float drumDegrees(int ammo, int capacity, float sinceShot, float reloadProgress)
    {
        if (reloadProgress >= 0F && reloadProgress < 1F) return -360F * (1F - reloadProgress);
        int shots = shotsFired(ammo, capacity);
        float step = shots % CHAMBERS;
        if (shots > 0) step -= recoil(sinceShot);
        return -step * STEP_DEGREES;
    }

    /** True when chamber {@code chamber} (0 = the one in firing position before any shot) still holds a bolt. */
    public static boolean chamberLoaded(int chamber, int ammo, int capacity)
    {
        int shots = shotsFired(ammo, capacity);
        int visible = ammo < CHAMBERS ? ammo : CHAMBERS;
        int index = ((chamber - shots) % CHAMBERS + CHAMBERS) % CHAMBERS;
        return index < visible;
    }

    /** The chamber in the firing position: in grapple mode the hook in the muzzle takes the place of its bolt. */
    public static int firingChamber(int ammo, int capacity)
    {
        return shotsFired(ammo, capacity) % CHAMBERS;
    }

    /** Bipod hinge: folded (pose 0) the legs lie back along the tiller, this many degrees around X. */
    public static final float LEG_FOLDED_X = -90F;
    /** Bipod hinge: deployed (pose 1) each leg splays this many degrees outward around Z. */
    public static final float LEG_SPLAY = 15F;
    /** Ticks a bipod takes to fold or unfold. */
    public static final float BIPOD_TICKS = 6F;

    /** Angle around X of both legs for a pose in 0..1 (0 folded, 1 down). */
    public static float legAngleX(float pose)
    {
        return LEG_FOLDED_X * (1F - clamp01(pose));
    }

    /** Angle around Z of one leg for a pose in 0..1: the right leg opens to +X, the left one to -X. */
    public static float legAngleZ(float pose, boolean right)
    {
        return (right ? LEG_SPLAY : -LEG_SPLAY) * clamp01(pose);
    }

    /**
     * Pose of the bipod {@code elapsed} ticks after it started moving from {@code from} towards down
     * ({@code deployed}) or folded: linear over {@link #BIPOD_TICKS}, then held at the target.
     */
    public static float bipodPose(float from, boolean deployed, float elapsed)
    {
        float target = deployed ? 1F : 0F;
        if (elapsed <= 0F) return clamp01(from);
        if (elapsed >= BIPOD_TICKS) return target;
        return clamp01(from) + (target - clamp01(from)) * (elapsed / BIPOD_TICKS);
    }

    /*
     * Third person hold. Angles are radians in the ModelBiped convention (rotateAngleX/Y/Z, applied Z, Y, X; X
     * negative raises the arm forward, Y negative turns the right arm in and positive turns the left arm in). The
     * arms point along the look (head pitch and yaw), the right one turned in to hold the grip in front of the right
     * side of the face and the left one reaching across to the side of the tiller; the left shoulder comes forward
     * and the right one back, as in a rifle stance, so the left hand rides ahead of the right.
     */

    /** Indices of {@link #holdPose}'s output. */
    public static final int POSE_RIGHT_X = 0, POSE_RIGHT_Y = 1, POSE_RIGHT_Z = 2;
    public static final int POSE_LEFT_X = 3, POSE_LEFT_Y = 4, POSE_LEFT_Z = 5;
    /** Shoulder pivot shifts in model pixels (Z negative = forward), added to the vanilla pivots. */
    public static final int POSE_RIGHT_SHOULDER_Z = 6, POSE_LEFT_SHOULDER_Z = 7;
    /**
     * Weapon direction as head-style angles (X positive = muzzle down, Y = yaw relative to the body): the look plus
     * sway and the kick of a shot. With the bipod the arms drop but the weapon stays on the look.
     */
    public static final int POSE_WEAPON_PITCH = 8, POSE_WEAPON_YAW = 9;
    /** How far a shot pushes the weapon back into the shoulder, in model pixels. */
    public static final int POSE_KICK_BACK = 10;
    public static final int POSE_SIZE = 11;

    /** Yaw of each arm off the look: the right hand comes in to the grip, the left one across to the tiller. */
    public static final float HOLD_RIGHT_YAW = -0.3F;
    public static final float HOLD_LEFT_YAW = 0.45F;
    /** Both arms ride this much above the look (radians), so the weapon comes up towards the eyes. */
    public static final float HOLD_RAISE = 0.2F;
    /** Shoulder shifts: left forward, right back. */
    public static final float HOLD_LEFT_SHOULDER_Z = -2.5F;
    public static final float HOLD_RIGHT_SHOULDER_Z = 1F;
    /** Sniper on the bipod: arms this much lower (radians) and no sway. */
    public static final float SNIPER_LOWER = 0.25F;
    /** Shot: the arms jump up this much (radians); the muzzle tips up this much more and the weapon slides back. */
    public static final float KICK_ARMS = 0.18F;
    public static final float KICK_PITCH = 0.12F;
    public static final float KICK_BACK = 1.5F;
    /** Idle sway of the held weapon (radians), like vanilla's arm bob. */
    public static final float SWAY = 0.03F;

    /**
     * Arm angles of a shooter holding the crossbow up to aim. {@code headPitch} and {@code headYaw} are the model's
     * head angles (radians, yaw relative to the body), {@code steady} is true for the sniper on its bipod (arms lower,
     * no sway), {@code recoil} is {@link #recoil} (1 right after a shot) and {@code ageTicks} drives the sway. Writes
     * {@link #POSE_SIZE} values into {@code out} (indices {@code POSE_*}); the caller passes the same array every
     * frame.
     */
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

    /** Bolts fired from the current magazine, never negative. */
    public static int shotsFired(int ammo, int capacity)
    {
        int shots = capacity - ammo;
        return shots < 0 ? 0 : shots;
    }

    /** Progress of the running reload in 0..1, or -1 when no reload runs. */
    public static float reloadProgress(long now, float partialTick, long lastReload, int reloadTicks)
    {
        if (lastReload <= 0 || reloadTicks <= 0) return -1F;
        long since = now - lastReload;
        if (since < 0 || since >= reloadTicks) return -1F;
        float progress = (since + partialTick) / reloadTicks;
        return progress >= 1F ? -1F : progress;
    }

    /**
     * Pose of the spent magazine during a reload that took one out: it rises out of the slot, then flies off to the
     * right, rolling and falling, and shrinks away. Writes {@link #MAG_SIZE} values into {@code out} (indices
     * {@code MAG_*}) and returns false when it is not drawn (no reload, or past {@link #MAGAZINE_EJECTED_AT}).
     */
    public static boolean spentMagazine(float reloadProgress, float[] out)
    {
        if (reloadProgress < 0F || reloadProgress >= MAGAZINE_EJECTED_AT) return false;
        float t = reloadProgress / MAGAZINE_EJECTED_AT;
        // y(t) = -h + a t - b t^2: from inside the drum, peaks just clear of the slot near EJECT_CLEAR, ends low.
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

    /**
     * Pose of the new magazine: from {@link #MAGAZINE_EJECTED_AT} it comes in from the left onto the slot, then it is
     * pushed down into the drum and is gone at {@link #MAGAZINE_SEATS_AT}. Writes {@link #MAG_SIZE} values into
     * {@code out} and returns false when it is not drawn.
     */
    public static boolean newMagazine(float reloadProgress, float[] out)
    {
        if (reloadProgress < MAGAZINE_EJECTED_AT || reloadProgress >= MAGAZINE_SEATS_AT) return false;
        float u = (reloadProgress - MAGAZINE_EJECTED_AT) / (MAGAZINE_SEATS_AT - MAGAZINE_EJECTED_AT);
        if (u < INSERT_SLIDE)
        {
            float k = u / INSERT_SLIDE;
            float rest = (1F - k) * (1F - k); // eases out onto the slot
            out[MAG_X] = INSERT_FROM_X * rest;
            out[MAG_Y] = INSERT_FROM_Y * rest;
        } else
        {
            float k = (u - INSERT_SLIDE) / (1F - INSERT_SLIDE);
            out[MAG_X] = 0F;
            out[MAG_Y] = -MAGAZINE_HEIGHT_LOADED * k * k; // pushed in, faster as it seats
        }
        out[MAG_ROLL] = 0F;
        out[MAG_SCALE] = 1F;
        out[MAG_CLIPPED] = 1F;
        return true;
    }

    /** The drum shows its bolts except from the start of a reload until the new magazine has gone in. */
    public static boolean boltsVisible(float reloadProgress)
    {
        return reloadProgress < 0F || reloadProgress >= MAGAZINE_SEATS_AT;
    }

    /** Bolts lying on the new magazine as it goes in: as many as it brings, at most three. */
    public static int magazineBolts(int ammo)
    {
        return ammo < 0 ? 0 : (ammo > 3 ? 3 : ammo);
    }

    /**
     * Rotation and scale that lay the string box (along +Y from the origin) from the limb tip to the latch.
     * Writes {@code out[0]} = angle in degrees, {@code out[1]} = axis X, {@code out[2]} = axis Z (axis Y is 0),
     * {@code out[3]} = distance; the caller passes the same array every frame.
     */
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
