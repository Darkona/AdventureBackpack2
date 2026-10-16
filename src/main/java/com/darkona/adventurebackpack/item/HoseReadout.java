package com.darkona.adventurebackpack.item;

import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.nbt.NBTTagCompound;

public final class HoseReadout
{
    private HoseReadout()
    {
    }

    public static int show(NBTTagCompound nbt, String name, boolean fluidName, int amount)
    {
        int written = 0;
        if (!nbt.hasKey("amount") || nbt.getInteger("amount") != amount)
        {
            nbt.setInteger("amount", amount);
            written++;
        }
        String shown = nbt.getString("fluid");
        if (!nbt.hasKey("fluid") || !(fluidName ? sameCapitalized(shown, name) : shown.equals(name)))
        {
            nbt.setString("fluid", fluidName ? Utils.capitalize(name) : name);
            written++;
        }
        return written;
    }

    static boolean sameCapitalized(String shown, String raw)
    {
        int n = raw.length();
        if (n == 0 || shown.length() != n) return false;
        return shown.charAt(0) == Character.toUpperCase(raw.charAt(0)) && shown.regionMatches(1, raw, 1, n - 1);
    }
}
