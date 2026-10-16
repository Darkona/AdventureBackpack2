package com.darkona.adventurebackpack.util;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

/** The worn-stack checks read the property once now (audit P18) and answer as the old instanceof chains did. */
class WearingTest
{
    @Test
    void wornOfTypeIsTheOldInstanceofCheck()
    {
        ItemStack food = new ItemStack(new ItemFood(1, false));
        assertSame(food, Wearing.wornOfType(food, ItemFood.class));
        assertSame(food, Wearing.wornOfType(food, Item.class));
        assertNull(Wearing.wornOfType(new ItemStack(new Item()), ItemFood.class));
        assertNull(Wearing.wornOfType(new ItemStack((Item) null), Item.class), "a stack without item");
        assertNull(Wearing.wornOfType(null, Item.class));
    }
}
