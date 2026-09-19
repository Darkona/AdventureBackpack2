package com.darkona.adventurebackpack.common;

/**
 * Pure decisions of the Air Compressor: how much air the hand pump and the furnace make, when it lights a new fuel
 * item, and how much it moves into the Scuba Gear. Also parses the entries of scuba.airItems. No Minecraft types.
 */
public final class AirCompressorRules
{
    private AirCompressorRules() {}

    /** Millibuckets one pump stroke adds: the configured stroke, as far as the tank has room. 0 turns the pump off. */
    public static int pumpAmount(int perStroke, int stored, int capacity)
    {
        return room(perStroke, stored, capacity);
    }

    /** True when the cooldown since the last stroke is over (or the world clock went backwards). */
    public static boolean pumpReady(long now, long lastStroke, int cooldownTicks)
    {
        return now < lastStroke || now - lastStroke >= cooldownTicks;
    }

    /** Burn ticks spent by one beat of {@code intervalTicks}: the fire never burns more than it has. */
    public static int burnTicksUsed(int burnTicks, int intervalTicks)
    {
        if (burnTicks <= 0 || intervalTicks <= 0) return 0;
        return Math.min(burnTicks, intervalTicks);
    }

    /** Air the furnace makes from {@code burnTicksUsed} ticks of fire, only with open air above and room in the tank. */
    public static int furnaceAir(int burnTicksUsed, int airPerFuelTick, boolean openAir, int stored, int capacity)
    {
        if (!openAir || burnTicksUsed <= 0 || airPerFuelTick <= 0) return 0;
        long made = (long) burnTicksUsed * airPerFuelTick;
        return room((int) Math.min(Integer.MAX_VALUE, made), stored, capacity);
    }

    /**
     * Whether to burn a new fuel item now: the last one is spent, the furnace makes air at all, there is open air
     * above and the tank is not full. A full or buried compressor keeps its coal.
     */
    public static boolean shouldLightFuel(int burnTicks, int airPerFuelTick, boolean openAir, int stored, int capacity)
    {
        return burnTicks <= 0 && airPerFuelTick > 0 && openAir && stored < capacity;
    }

    /** Millibuckets moved into the gear in one beat: {@code perTick} for each tick, as far as there is air and room. */
    public static int fillAmount(int perTick, int ticks, int available, int room)
    {
        if (perTick <= 0 || ticks <= 0 || available <= 0 || room <= 0) return 0;
        long wanted = (long) perTick * ticks;
        return (int) Math.min(Math.min(wanted, available), room);
    }

    private static int room(int amount, int stored, int capacity)
    {
        if (amount <= 0) return 0;
        int free = capacity - Math.max(0, stored);
        if (free <= 0) return 0;
        return Math.min(amount, free);
    }

    /** One entry of scuba.airItems: the filled item, how much air it holds and, optionally, the item it leaves. */
    public static final class AirItem
    {
        /** The filled item, in the recipe part format (ic2:airCell, item:modid:name:meta, ore:name...). */
        public final String full;
        public final int amount;
        /** The empty item it leaves and that the compressor can fill again, or null. */
        public final String empty;

        AirItem(String full, int amount, String empty)
        {
            this.full = full;
            this.amount = amount;
            this.empty = empty;
        }
    }

    /** Parses {@code <item>=<millibuckets>[><empty item>]}; null when malformed. Whitespace is ignored. */
    public static AirItem parseAirItem(String text)
    {
        if (text == null) return null;
        String t = text.replace(" ", "").replace("\t", "");
        int eq = t.indexOf('=');
        if (eq <= 0 || eq == t.length() - 1) return null;
        String full = t.substring(0, eq);
        String rest = t.substring(eq + 1);
        String empty = null;
        int arrow = rest.indexOf('>');
        if (arrow >= 0)
        {
            empty = rest.substring(arrow + 1);
            rest = rest.substring(0, arrow);
            if (empty.isEmpty()) return null;
        }
        int amount;
        try
        {
            amount = Integer.parseInt(rest);
        } catch (NumberFormatException bad)
        {
            return null;
        }
        if (amount <= 0) return null;
        return new AirItem(full, amount, empty);
    }

    /** True when {@code fluidName} is one of the breathable names (case does not matter). */
    public static boolean isBreathable(String fluidName, String[] breathable)
    {
        if (fluidName == null || breathable == null) return false;
        for (String name : breathable)
        {
            if (name != null && fluidName.equalsIgnoreCase(name.trim())) return true;
        }
        return false;
    }
}
