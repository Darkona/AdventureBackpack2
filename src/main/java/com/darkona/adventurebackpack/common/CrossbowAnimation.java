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

    /** Part of the reload during which the cartridge is still dropping in and the drum shows no bolts. */
    public static final float CARTRIDGE_SEATS_AT = 0.6F;
    /** Height above its seat from which the cartridge drops, in model units. */
    public static final float CARTRIDGE_DROP = 10F;

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

    /** The cartridge is drawn while it drops into the drum. */
    public static boolean cartridgeVisible(float reloadProgress)
    {
        return reloadProgress >= 0F && reloadProgress < CARTRIDGE_SEATS_AT;
    }

    /** Height of the dropping cartridge above its seat, in model units. */
    public static float cartridgeHeight(float reloadProgress)
    {
        if (!cartridgeVisible(reloadProgress)) return 0F;
        return CARTRIDGE_DROP * (1F - reloadProgress / CARTRIDGE_SEATS_AT);
    }

    /** The drum shows its bolts except while the new cartridge is still dropping in. */
    public static boolean boltsVisible(float reloadProgress)
    {
        return !cartridgeVisible(reloadProgress);
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
