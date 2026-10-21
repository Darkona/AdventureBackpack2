package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.sort.SlotLocks;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.jupiter.api.Test;

class LockNbtTest
{
    @Test
    void lockedSlotsSurviveASaveAndALoad()
    {
        SlotLocks locks = new SlotLocks();
        locks.set(2, true);
        locks.set(45, true);
        NBTTagCompound data = new NBTTagCompound();
        BackpackNbt.writeLocks(data, locks);
        SlotLocks back = new SlotLocks();
        BackpackNbt.readLocks(data, back);
        assertTrue(back.isLocked(2));
        assertTrue(back.isLocked(45));
        assertEquals(2, back.count());
    }

    @Test
    void anOldBackpackWithoutTheKeyLoadsWithNoLocksAndAnEmptyStateWritesNoKey()
    {
        SlotLocks locks = new SlotLocks();
        locks.set(1, true);
        BackpackNbt.readLocks(new NBTTagCompound(), locks);
        assertTrue(locks.isEmpty());
        NBTTagCompound data = new NBTTagCompound();
        data.setIntArray(BackpackNbt.LOCKED, new int[]{1});
        BackpackNbt.writeLocks(data, locks);
        assertFalse(data.hasKey(BackpackNbt.LOCKED));
        assertTrue(BackpackNbt.isCoreKey(BackpackNbt.LOCKED));
    }
}
