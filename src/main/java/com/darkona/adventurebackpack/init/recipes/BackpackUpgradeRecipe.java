package com.darkona.adventurebackpack.init.recipes;

import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.reference.BackpackNames;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class BackpackUpgradeRecipe extends ShapedOreRecipe
{
    public BackpackUpgradeRecipe(ItemStack result, Object... recipe)
    {
        super(result, recipe);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv)
    {
        ItemStack result = super.getCraftingResult(inv);
        for (int i = 0; i < inv.getSizeInventory(); i++)
        {
            ItemStack input = inv.getStackInSlot(i);
            if (input != null && input.getItem() instanceof ItemAdventureBackpack && input.hasTagCompound())
            {
                result.setTagCompound((NBTTagCompound) input.getTagCompound().copy());
                return BackpackNames.setBackpackColorNameFromDamage(result, result.getItemDamage());
            }
        }
        return result;
    }
}
