package com.darkona.adventurebackpack.compat.baubles;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * The only class that touches the Baubles API. It is reached only through {@link BackpackBauble} after
 * {@code Loader.isModLoaded("Baubles")}, or from a Baubles callback, so it is never loaded without Baubles.
 *
 * @author Javier Darkona
 */
public final class BaublesCompat
{
    private BaublesCompat()
    {
    }

    /** preInit only: Baubles Expanded accepts new slot types only then. */
    static void registerBackSlot()
    {
        // "back" is shared: a mod that registered it already gets the same slot, not a second one
        String back = BackpackBauble.TYPES[0];
        if (BaubleExpandedSlots.tryRegisterType(back) && BaubleExpandedSlots.tryAssignSlotsUpToMinimum(back, 1))
        {
            LogHelper.info("Baubles Expanded found: added a back slot for the Adventure Backpack.");
        }
    }

    /** Index of this very stack in the player's Baubles inventory, or -1. */
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

    /** The player's Baubles inventory still holds this very stack. */
    public static boolean holds(EntityPlayer player, ItemStack stack)
    {
        return slotOf(player, stack) >= 0;
    }

    /** Empties the slot that holds this very stack (Baubles then calls onUnequipped). False when no slot holds it. */
    public static boolean remove(EntityPlayer player, ItemStack stack)
    {
        int slot = slotOf(player, stack);
        if (slot < 0) return false;
        BaublesApi.getBaubles(player).setInventorySlotContents(slot, null);
        return true;
    }

    /** Takes the stack out of its slot and gives it to the player, or drops it when the inventory is full. */
    public static void eject(EntityPlayer player, ItemStack stack)
    {
        if (!remove(player, stack)) return;
        if (!player.inventory.addItemStackToInventory(stack))
        {
            player.dropPlayerItemWithRandomChoice(stack, false);
        }
    }
}
