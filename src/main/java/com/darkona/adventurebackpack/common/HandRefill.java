package com.darkona.adventurebackpack.common;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public final class HandRefill
{
    private HandRefill() {}

    public static boolean sameKind(ItemStack a, ItemStack b)
    {
        return a != null && b != null && a.getItem() == b.getItem() && a.getItemDamage() == b.getItemDamage()
                && ItemStack.areItemStackTagsEqual(a, b);
    }

    public static int refill(ItemStack hand, IInventory source, int slot)
    {
        if (hand == null || source == null || slot < 0 || slot >= source.getSizeInventory()) return 0;
        ItemStack stored = source.getStackInSlot(slot);
        if (!sameKind(hand, stored)) return 0;
        int moved = RefillRules.amount(hand.stackSize, hand.getMaxStackSize(), stored.stackSize);
        if (moved <= 0) return 0;
        hand.stackSize += moved;
        source.decrStackSize(slot, moved);
        return moved;
    }
}
