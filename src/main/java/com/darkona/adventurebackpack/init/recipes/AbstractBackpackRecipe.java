package com.darkona.adventurebackpack.init.recipes;

import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 20/10/2014
 *
 * @author Darkona
 */
public class AbstractBackpackRecipe implements IRecipe
{

    BackpackRecipes br = new BackpackRecipes();
    private ItemStack result;

    public boolean compareStacksForColor(ItemStack stack1, ItemStack stack2)
    {

        if (stack1 == null && stack2 == null)
        {
            return true;
        } else if (stack1 != null && stack2 != null)
        {/*
            if(stack1.stackSize != stack2.stackSize){
                return false;
            }else*/
            if (stack1.getItem() != stack2.getItem())
            {
                return false;
            } else
            {
                return (stack1.getItemDamage() == stack2.getItemDamage());
            }
        }
        return false;
    }

    public AbstractBackpackRecipe()
    {
        recipes = new HashMap<String, ItemStack[]>();
        for (Field field : BackpackRecipes.class.getFields())
        {
            try
            {
                int i = 0;
                if (field.getType() == ItemStack[].class)
                {
                    recipes.put(field.getName(), (ItemStack[]) field.get(br));
                    i++;
                }
                LogHelper.info("Loaded " + i + " recipes for backpack coloration.");
            } catch (Exception oops)
            {
                LogHelper.info("La cagaste en Reflection");
            }
        }
    }

    private HashMap<String, ItemStack[]> recipes;


    public ItemStack makeBackpack(ItemStack backpackIn, String colorName)
    {
        if (backpackIn == null) return null;
        ItemStack newBackpack = backpackIn.copy();
        NBTTagCompound compound = (NBTTagCompound) backpackIn.getTagCompound().copy();
        newBackpack.setTagCompound(compound);
        newBackpack.stackTagCompound.setString("colorName", colorName);
        return newBackpack;
    }

    public boolean match(ItemStack[] model, InventoryCrafting invC)
    {
        if (model == null || invC == null) return false;
        for (int i = 0; i < invC.getSizeInventory(); i++)
        {
            ItemStack m = model[i];
            ItemStack c = invC.getStackInSlot(i);
            if (!compareStacksForColor(m, c))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean matches(InventoryCrafting invC, World world)
    {
        result = null;
        if (invC != null)
        {
            for (Map.Entry<String, ItemStack[]> recipe : recipes.entrySet())
            {
                if (match(recipe.getValue(), invC))
                {
                    result = makeBackpack(invC.getStackInSlot(4), recipe.getKey());
                }
            }
        }
        return result != null;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Override
    public ItemStack getCraftingResult(InventoryCrafting invC)
    {

        return result.copy();
    }

    /**
     * Returns the size of the recipe area
     */
    @Override
    public int getRecipeSize()
    {
        return 10;
    }

    @Override
    public ItemStack getRecipeOutput()
    {
        return result;
    }


}
