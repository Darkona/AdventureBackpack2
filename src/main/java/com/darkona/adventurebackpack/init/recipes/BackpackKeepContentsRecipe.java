package com.darkona.adventurebackpack.init.recipes;

import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.reference.BackpackNames;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * A backpack recipe that keeps the items, tanks and data of the backpack used as ingredient and only
 * changes its type. A plain ShapedOreRecipe returns a new empty backpack and the contents are lost (#140).
 */
public class BackpackKeepContentsRecipe extends ShapedOreRecipe
{
    public BackpackKeepContentsRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting grid)
    {
        ItemStack result = super.getCraftingResult(grid);
        for (int i = 0; i < grid.getSizeInventory(); i++)
        {
            ItemStack ingredient = grid.getStackInSlot(i);
            if (ingredient != null && ingredient.getItem() instanceof ItemAdventureBackpack && ingredient.hasTagCompound())
            {
                String colorName = BackpackNames.getBackpackColorName(result);
                result.setTagCompound((NBTTagCompound) ingredient.getTagCompound().copy());
                BackpackNames.setBackpackColorName(result, colorName);
                break;
            }
        }
        return result;
    }
}
