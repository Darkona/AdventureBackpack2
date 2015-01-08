package com.darkona.adventurebackpack.init.recipes;

import net.minecraft.item.ItemStack;

/**
 * Created on 24/12/2014
 *
 * @author Darkona
 */
public class BackpackRecipe
{
    public ItemStack[] array;
    public String name;

    BackpackRecipe()
    {
    }

    BackpackRecipe(String name, ItemStack[] array)
    {
        this.name = name;
        this.array = array;
    }
}
