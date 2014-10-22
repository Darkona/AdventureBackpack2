package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.init.recipes.AbstractBackpackRecipe;
import com.darkona.adventurebackpack.reference.ModInfo;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.RecipeSorter;

/**
 * Created on 20/10/2014
 *
 * @author Darkona
 */
public class ModRecipes
{
    private static ItemStack bc(String color, String colorName)
    {
        ItemStack backpack = new ItemStack(ModItems.adventureBackpack);
        backpack.setTagCompound(new NBTTagCompound());
        backpack.stackTagCompound.setString("colorName", colorName);
        return backpack;
    }

    public static AbstractBackpackRecipe backpackRecipe;

    public static void init()
    {
        ItemStack a = bc("standard", "Standard");

        //Sleeping Bag - temporal recipe
        GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 1),
                "  X",
                "CCC",
                'X', Blocks.wool,
                'C', Blocks.carpet
        );

        //Backpack Tank
        GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 2),
                "GIG",
                "GGG",
                "GIG",
                'G', Blocks.glass,
                'I', Items.iron_ingot
        );

        //Hose Nozzle
        GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 3),
                " G ",
                "ILI",
                "   ",
                'G', Items.gold_ingot,
                'I', Items.iron_ingot,
                'L', Blocks.lever
        );

        //Machete Handle
        GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 4),
                "YIY",
                "BSB",
                "RbR",
                'Y', new ItemStack(Items.dye, 1, 11),
                'B', new ItemStack(Items.dye, 1, 4),
                'R', new ItemStack(Items.dye, 1, 1),
                'b', new ItemStack(Items.dye, 1, 0),
                'I', Items.iron_ingot,
                'S', Items.stick
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.machete),
                " I ",
                " I ",
                " H ",
                'I', Items.iron_ingot,
                'H', new ItemStack(ModItems.component, 1, 4)
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.adventureHat),
                "   ",
                "nC ",
                "LLL",
                'n', Items.gold_nugget,
                'C', Items.leather_helmet,
                'L', Items.leather
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.adventureSuit),
                " V ",
                " W ",
                " P ",
                'V', Items.leather_chestplate,
                'W', Blocks.wool,
                'P', Items.leather_leggings
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.pistonBoots),
                " B ",
                "PSP",
                'B', Items.leather_boots,
                'P', Blocks.piston,
                'S', Items.slime_ball
        );

        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.melonJuiceBottle),
                Items.melon, Items.potionitem
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.hose),
                "NGG",
                "  G",
                "  G",
                'N', new ItemStack(ModItems.component, 1, 3),
                'G', new ItemStack(Items.dye, 1, 2)
        );

        GameRegistry.addRecipe(a,
                "LGL",
                "TCT",
                "LSL",
                'L', Items.leather,
                'G', Items.gold_ingot,
                'T', new ItemStack(ModItems.component, 1, 2),
                'S', new ItemStack(ModItems.component, 1, 1),
                'C', Blocks.chest
        );

        GameRegistry.addRecipe(new AbstractBackpackRecipe());
        RecipeSorter.register(ModInfo.MOD_ID + "adventureBackpack", AbstractBackpackRecipe.class, RecipeSorter.Category.UNKNOWN, "after:minecraft:shapeless");
    }
}
