package com.darkona.adventurebackpack.init.recipes;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;

import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.BackpackUtils;
import com.darkona.adventurebackpack.util.LogHelper;

import static com.darkona.adventurebackpack.common.Constants.TAG_INVENTORY;

/**
 * Created on 03.03.2018
 *
 * @author Ugachaga
 */
public class ShapedBackpackRecipe extends ShapedOreRecipe
{
    static
    {
        RecipeSorter.register(ModInfo.MOD_ID + ":shapedbackpackrecipe", ShapedBackpackRecipe.class,
                RecipeSorter.Category.SHAPED,"after:forge:shapedore before:minecraft:shapeless");
    }

    public ShapedBackpackRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting craftMatrix)
    {
        ItemStack craftResult = super.getCraftingResult(craftMatrix);

        if (!(craftResult.getItem() == ModItems.adventureBackpack))
            return craftResult; // in case recipe is using backpack only as ingredient

        for (int i = 0; i < craftMatrix.getSizeInventory(); i++)
        {
            ItemStack matrixStack = craftMatrix.getStackInSlot(i);
            if (matrixStack != null && matrixStack.getItem() == ModItems.adventureBackpack
                    && BackpackUtils.getWearableCompound(matrixStack).hasKey(TAG_INVENTORY))
            {
                NBTTagList itemList = BackpackUtils.getWearableInventory(matrixStack);
                BackpackUtils.getWearableCompound(craftResult).setTag(TAG_INVENTORY, itemList);

                LogHelper.info("Successfully transferred inventory from the ingredient backpack ["
                        + BackpackTypes.getSkinName(matrixStack) + "] to the crafted backpack ["
                        + BackpackTypes.getSkinName(craftResult) + "]");

                break; // copy inventory content only from the 1st found backpack in the craftMatrix
            }
        }

        return craftResult;
    }
}
