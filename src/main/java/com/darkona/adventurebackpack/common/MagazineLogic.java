package com.darkona.adventurebackpack.common;

public final class MagazineLogic
{
    public static final int LEGACY_CHARGES = 6;
    public static final String LEGACY_AMMO = "minecraft:arrow";
    public static final String BOLT_AMMO = "adventurebackpack:bolt";

    private MagazineLogic() {}

    public static int charges(boolean hasChargesTag, int storedCharges, int capacity)
    {
        if (!hasChargesTag) return LEGACY_CHARGES < capacity ? LEGACY_CHARGES : capacity;
        return storedCharges < 0 ? 0 : (storedCharges > capacity ? capacity : storedCharges);
    }

    public static String ammoType(String stored)
    {
        return stored == null || stored.isEmpty() ? LEGACY_AMMO : stored;
    }

    public static boolean accepts(int charges, String storedType, String ammo)
    {
        return ammo != null && (charges <= 0 || ammoType(storedType).equals(ammo));
    }

    public static int chargesToLoad(int charges, int capacity, int available, int ticksPerCharge)
    {
        int room = capacity - charges;
        if (room <= 0 || available <= 0) return 0;
        int want = ticksPerCharge <= 0 ? room : 1;
        return want < available ? want : available;
    }

    public static boolean isLoading(long now, long readyAt, int maxWait)
    {
        return CrossbowLogic.isWaiting(now, readyAt, maxWait);
    }

    public static int recipeCharges(int charges, int capacity, int ammo)
    {
        if (ammo <= 0 || charges < 0 || charges + ammo > capacity) return -1;
        return charges + ammo;
    }

    public static boolean hasMagazine(boolean hasFlag, boolean flag, int ammo)
    {
        return hasFlag ? flag : ammo > 0;
    }

    public static boolean ejectOnDry(int ammo, boolean hasMagazine)
    {
        return hasMagazine && ammo <= 0;
    }

    public static double damageMultiplier(String ammo, double boltMultiplier)
    {
        return BOLT_AMMO.equals(ammoType(ammo)) ? boltMultiplier : 1.0;
    }

    public static double emptyFraction(int charges, int capacity)
    {
        if (capacity <= 0) return 1.0;
        double full = (double) charges / capacity;
        return full >= 1.0 ? 0.0 : (full <= 0.0 ? 1.0 : 1.0 - full);
    }
}
