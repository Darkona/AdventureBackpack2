package com.darkona.adventurebackpack.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import net.minecraft.nbt.NBTTagCompound;

/**
 * The tooltip memo deserialises a tank only when its tag is another object. The tags here are not tank compounds
 * (a named fluid needs the FluidRegistry, which needs the running game), so the fluid stays null; what is checked is
 * when it reads.
 */
class TankTagMemoTest
{
    @Test
    void readsOnlyWhenTheTankTagChanged()
    {
        TankTagMemo memo = new TankTagMemo();
        NBTTagCompound root = new NBTTagCompound();
        root.setString("Tank", "not a compound");
        assertNull(memo.fluid(root, "Tank"));
        assertEquals(1, memo.reads);
        for (int i = 0; i < 100; i++) memo.fluid(root, "Tank");
        assertEquals(1, memo.reads, "same tag: no read per frame");

        root.setString("Tank", "written again"); // a write puts a new tag object
        memo.fluid(root, "Tank");
        assertEquals(2, memo.reads);

        NBTTagCompound other = new NBTTagCompound();
        other.setTag("Tank", root.getTag("Tank").copy());
        memo.fluid(other, "Tank");
        assertEquals(3, memo.reads, "another stack's tag");

        assertNull(memo.fluid(null, "Tank"));
        assertNull(memo.fluid(new NBTTagCompound(), "Tank"));
        assertEquals(4, memo.reads, "no tag at all is one key (null), read once");
    }
}
