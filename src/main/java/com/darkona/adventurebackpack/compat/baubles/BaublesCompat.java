package com.darkona.adventurebackpack.compat.baubles;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class BaublesCompat
{
    private BaublesCompat()
    {
    }

    static void registerBackSlot()
    {
        String back = BackpackBauble.TYPES[0];
        if (BaubleExpandedSlots.tryRegisterType(back) && BaubleExpandedSlots.tryAssignSlotsUpToMinimum(back, 1))
        {
            LogHelper.info("Baubles Expanded found: added a back slot for the Adventure Backpack.");
        }
    }

    private static int slotOf(EntityPlayer player, ItemStack stack)
    {
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null || stack == null) return -1;
        for (int i = 0; i < baubles.getSizeInventory(); i++)
        {
            if (baubles.getStackInSlot(i) == stack) return i;
        }
        return -1;
    }

    public static boolean holds(EntityPlayer player, ItemStack stack)
    {
        return slotOf(player, stack) >= 0;
    }

    public static boolean remove(EntityPlayer player, ItemStack stack)
    {
        int slot = slotOf(player, stack);
        if (slot < 0) return false;
        BaublesApi.getBaubles(player).setInventorySlotContents(slot, null);
        return true;
    }

    public static void eject(EntityPlayer player, ItemStack stack)
    {
        if (!remove(player, stack)) return;
        if (!player.inventory.addItemStackToInventory(stack))
        {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }
    }
}
