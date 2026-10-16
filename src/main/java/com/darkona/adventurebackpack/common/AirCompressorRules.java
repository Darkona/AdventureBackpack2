package com.darkona.adventurebackpack.common;

public final class AirCompressorRules
{
    private AirCompressorRules() {}

    public static int pumpAmount(int perStroke, int stored, int capacity)
    {
        return room(perStroke, stored, capacity);
    }

    public static boolean pumpReady(long now, long lastStroke, int cooldownTicks)
    {
        return now < lastStroke || now - lastStroke >= cooldownTicks;
    }

    public static int burnTicksUsed(int burnTicks, int intervalTicks)
    {
        if (burnTicks <= 0 || intervalTicks <= 0) return 0;
        return Math.min(burnTicks, intervalTicks);
    }

    public static int furnaceAir(int burnTicksUsed, int airPerFuelTick, boolean openAir, int stored, int capacity)
    {
        if (!openAir || burnTicksUsed <= 0 || airPerFuelTick <= 0) return 0;
        long made = (long) burnTicksUsed * airPerFuelTick;
        return room((int) Math.min(Integer.MAX_VALUE, made), stored, capacity);
    }

    public static boolean shouldLightFuel(int burnTicks, int airPerFuelTick, boolean openAir, int stored, int capacity)
    {
        return burnTicks <= 0 && airPerFuelTick > 0 && openAir && stored < capacity;
    }

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

    public static final class AirItem
    {
        public final String full;
        public final int amount;
        public final String empty;

        AirItem(String full, int amount, String empty)
        {
            this.full = full;
            this.amount = amount;
            this.empty = empty;
        }
    }

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
