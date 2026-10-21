package com.darkona.adventurebackpack.inventory;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.darkona.adventurebackpack.common.sort.SlotLocks;
import net.minecraft.item.ItemStack;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

class ContainerLocksTest
{
    private static ContainerBackpack build(SlotLocks locks) throws Exception
    {
        return ContainerBackpackSlotsTest.build("Standard", new HashMap<Integer, ItemStack>(), 0, locks);
    }

    @Test
    void onlyStorageSlotsCanBeLocked() throws Exception
    {
        SlotLocks locks = new SlotLocks();
        ContainerBackpack c = build(locks);
        assertTrue(c.toggleLock(0));
        assertTrue(c.isLocked(0));
        int tool = firstSlotOf(c, SlotTool.class);
        assertFalse(c.toggleLock(tool));
        assertFalse(c.isLocked(tool));
        assertFalse(c.toggleLock(-1));
        assertFalse(c.toggleLock(c.inventorySlots.size()));
        assertTrue(locks.isLocked(0));
        assertTrue(locks.count() == 1);
    }

    @Test
    void theLockFollowsTheInventoryIndexNotTheWindowSlot() throws Exception
    {
        SlotLocks locks = new SlotLocks();
        ContainerBackpack c = build(locks);
        int last = firstSlotOf(c, SlotBackpack.class);
        for (int s = 0; s < c.inventorySlots.size(); s++)
        {
            if (c.getSlot(s) instanceof SlotBackpack) last = s;
        }
        c.toggleLock(last);
        assertTrue(locks.isLocked(c.getSlot(last).getSlotIndex()));
    }

    @Test
    void theLockStateTravelsInProgressBarChunks() throws Exception
    {
        SlotLocks server = new SlotLocks();
        ContainerBackpack s = build(server);
        s.toggleLock(3);
        s.toggleLock(30);
        SlotLocks client = new SlotLocks();
        ContainerBackpack c = build(client);
        for (int i = 0; i < SlotLocks.CHUNKS; i++) c.updateProgressBar(ContainerBackpack.LOCK_PROPERTY + i, (short) server.chunk(i));
        assertTrue(c.isLocked(3));
        assertTrue(c.isLocked(30));
        assertFalse(c.isLocked(4));
    }

    private static int firstSlotOf(ContainerBackpack c, Class<?> type)
    {
        for (int s = 0; s < c.inventorySlots.size(); s++)
        {
            if (type.isInstance(c.getSlot(s))) return s;
        }
        throw new AssertionError(type.getSimpleName());
    }
}
