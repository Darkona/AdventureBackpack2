package com.darkona.adventurebackpack.item;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.darkona.adventurebackpack.util.Utils;
import net.minecraft.nbt.NBTTagCompound;

/** The hose readout (audit P7) shows what the old code wrote, and writes nothing when the shown value is the same. */
class HoseReadoutTest
{
    @Test
    void writesWhatTheOldReadoutWrote()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        assertEquals(2, HoseReadout.show(nbt, "water", true, 1000));
        assertEquals(Utils.capitalize("water"), nbt.getString("fluid"));
        assertEquals(1000, nbt.getInteger("amount"));
        assertEquals(2, HoseReadout.show(nbt, "Empty", false, 0));
        assertEquals("Empty", nbt.getString("fluid"));
        assertEquals(0, nbt.getInteger("amount"));
    }

    @Test
    void theSameValueIsNotWrittenAgain()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        HoseReadout.show(nbt, "lava", true, 500);
        Object fluid = nbt.getTag("fluid"), amount = nbt.getTag("amount");
        assertEquals(0, HoseReadout.show(nbt, "lava", true, 500));
        assertTrue(fluid == nbt.getTag("fluid") && amount == nbt.getTag("amount"), "no new tag objects");
        assertEquals(1, HoseReadout.show(nbt, "lava", true, 499), "only the amount");
        assertEquals(1, HoseReadout.show(nbt, "oil", true, 499), "only the fluid");
    }

    @Test
    void aZeroAmountIsStillWrittenOnAFreshTag()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        assertEquals(2, HoseReadout.show(nbt, "None", false, 0));
        assertTrue(nbt.hasKey("amount"));
    }

    @Test
    void sameCapitalizedMatchesUtilsCapitalize()
    {
        for (String raw : new String[] {"water", "lava", "milk", "fuel", "Steam", "x", "mushroomstew"})
        {
            assertTrue(HoseReadout.sameCapitalized(Utils.capitalize(raw), raw), raw);
        }
        assertFalse(HoseReadout.sameCapitalized("water", "water"), "not capitalised");
        assertFalse(HoseReadout.sameCapitalized("Lava", "water"));
        assertFalse(HoseReadout.sameCapitalized("Empty", ""));
        assertFalse(HoseReadout.sameCapitalized("Wate", "water"));
    }
}
