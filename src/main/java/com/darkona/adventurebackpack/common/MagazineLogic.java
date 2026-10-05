package com.darkona.adventurebackpack.common;

/**
 * Pure decisions of the Crossbow Magazine (item {@code boltCartridge}): a reusable magazine that the player loads with
 * ammunition (arrows, Crossbow Bolts or any item of {@code crossbow.ammo.list}) and puts into the Clockwork Crossbow.
 * No game classes here, so the unit tests run without Minecraft.
 * <p>
 * One magazine holds one kind of ammunition: the type is stored once per magazine (the item's registry name), not per
 * charge. An empty magazine holds no type, so it takes any ammunition; a loaded one only takes more of its own.
 */
public final class MagazineLogic
{
    /** Charges of a 1.0/1.1 Bolt Cartridge without a {@code Charges} tag: it was crafted with 6 arrows. */
    public static final int LEGACY_CHARGES = 6;
    /** Ammunition of a loaded magazine or crossbow that stores no type: the old cartridges were made of arrows. */
    public static final String LEGACY_AMMO = "minecraft:arrow";
    /** The mod's own Crossbow Bolt: the only ammunition that gets {@code crossbow.ammo.boltDamageMultiplier}. */
    public static final String BOLT_AMMO = "adventurebackpack:bolt";

    private MagazineLogic() {}

    /**
     * Charges of a magazine. A stack without the {@code Charges} tag is an old cartridge: full, as in 1.0 and 1.1
     * ({@link #LEGACY_CHARGES}, never more than the capacity). A tagged one has between 0 and {@code capacity}.
     */
    public static int charges(boolean hasChargesTag, int storedCharges, int capacity)
    {
        if (!hasChargesTag) return LEGACY_CHARGES < capacity ? LEGACY_CHARGES : capacity;
        return storedCharges < 0 ? 0 : (storedCharges > capacity ? capacity : storedCharges);
    }

    /** The ammunition of a loaded magazine or crossbow: the stored registry name, or arrows when none is stored. */
    public static String ammoType(String stored)
    {
        return stored == null || stored.isEmpty() ? LEGACY_AMMO : stored;
    }

    /** True when ammunition {@code ammo} can go into a magazine with {@code charges} of {@code storedType}. */
    public static boolean accepts(int charges, String storedType, String ammo)
    {
        return ammo != null && (charges <= 0 || ammoType(storedType).equals(ammo));
    }

    /**
     * Charges one load action puts in: one per click, or the whole room at once when {@code ticksPerCharge} is 0.
     *
     * @param available ammunition items the player has (ignored in creative: pass {@link Integer#MAX_VALUE})
     */
    public static int chargesToLoad(int charges, int capacity, int available, int ticksPerCharge)
    {
        int room = capacity - charges;
        if (room <= 0 || available <= 0) return 0;
        int want = ticksPerCharge <= 0 ? room : 1;
        return want < available ? want : available;
    }

    /** True while the pause after loading one charge runs. A pause longer than {@code maxWait} is stale and ignored. */
    public static boolean isLoading(long now, long readyAt, int maxWait)
    {
        return CrossbowLogic.isWaiting(now, readyAt, maxWait);
    }

    /**
     * Charges of a magazine after the reload recipe (a magazine plus {@code ammo} items of one kind), or -1 when the
     * recipe does not match: no ammunition, or more than the magazine has room for.
     */
    public static int recipeCharges(int charges, int capacity, int ammo)
    {
        if (ammo <= 0 || charges < 0 || charges + ammo > capacity) return -1;
        return charges + ammo;
    }

    /**
     * True when the crossbow holds a magazine. Crossbows saved before the reusable magazines have no flag: one with
     * bolts in it holds the cartridge they came from (it comes back as a magazine), an empty one holds nothing.
     */
    public static boolean hasMagazine(boolean hasFlag, boolean flag, int ammo)
    {
        return hasFlag ? flag : ammo > 0;
    }

    /** A dry click gives back the inserted magazine only when it is empty (a partly used one stays for the next mode). */
    public static boolean ejectOnDry(int ammo, boolean hasMagazine)
    {
        return hasMagazine && ammo <= 0;
    }

    /** Damage multiplier of a bolt fired with {@code ammo}: the Crossbow Bolt hits harder than an arrow. */
    public static double damageMultiplier(String ammo, double boltMultiplier)
    {
        return BOLT_AMMO.equals(ammoType(ammo)) ? boltMultiplier : 1.0;
    }

    /** Fraction of the durability bar that is gone: 0 for a full magazine, 1 for an empty one. */
    public static double emptyFraction(int charges, int capacity)
    {
        if (capacity <= 0) return 1.0;
        double full = (double) charges / capacity;
        return full >= 1.0 ? 0.0 : (full <= 0.0 ? 1.0 : 1.0 - full);
    }
}
