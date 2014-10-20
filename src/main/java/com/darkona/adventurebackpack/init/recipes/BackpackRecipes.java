package com.darkona.adventurebackpack.init.recipes;

import com.darkona.adventurebackpack.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

/**
 * Created on 20/10/2014
 *
 * @author Darkona
 */
public class BackpackRecipes
{

    public BackpackRecipes()
    {

        Black = reviewRecipe(
                "XXX",
                "XaX",
                "XXX",
                'X', new ItemStack(Blocks.wool, 1, 15),
                'a', ModItems.adventureBackpack
        );

        Blaze = reviewRecipe(
                "BFB",
                "BaB",
                "PLP",
                'B', Items.blaze_rod,
                'F', Items.fire_charge,
                'a', ModItems.adventureBackpack,
                'P', Items.blaze_powder,
                'L', Items.lava_bucket
        );

        Blue = reviewRecipe(
                "XXX",
                "XaX",
                "XXX",
                'X', new ItemStack(Blocks.wool, 1, 11),
                'a', ModItems.adventureBackpack
        );

        Bookshelf = reviewRecipe(
                "BDB",
                "BaB",
                "bbb",
                'B', Blocks.bookshelf,
                'a', ModItems.adventureBackpack,
                'b', Items.book
        );

        Brown = reviewRecipe(
                "XXX",
                "XaX",
                "XXX",
                'X', new ItemStack(Blocks.wool, 1, 12),
                'a', ModItems.adventureBackpack
        );

        BrownMushroom = reviewRecipe(
                "MMM",
                "MaM",
                "MSM",
                'M', Blocks.brown_mushroom,
                'a', ModItems.adventureBackpack,
                'S', Items.mushroom_stew
        );

        Cactus = reviewRecipe(
                "CGC",
                "CaC",
                "SSS",
                'C', Blocks.cactus,
                'G', new ItemStack(Items.dye, 1, 2),
                'a', ModItems.adventureBackpack,
                'S', Blocks.sand
        );

        Cake = reviewRecipe(
                "ECE",
                "WaW",
                "SmS",
                'a', ModItems.adventureBackpack,
                'E', Items.egg,
                'C', Items.cake,
                'W', Items.wheat,
                'S', Items.sugar,
                'm', Items.milk_bucket
        );

        Chest = reviewRecipe(
                "CWC",
                "WaW",
                "CWC",
                'C', Blocks.chest,
                'W', Blocks.planks,
                'a', ModItems.adventureBackpack
        );

        Chicken = reviewRecipe(
                "FnF",
                "FaF",
                "nEn",
                'F', Items.feather,
                'n', Items.gold_nugget,
                'a', ModItems.adventureBackpack,
                'E', Items.egg
        );

        Coal = reviewRecipe(
                "cCc",
                "CaC",
                "ccc",
                'c', Items.coal,
                'C', Blocks.coal_block,
                'a', ModItems.adventureBackpack
        );

        Cookie = reviewRecipe(
                "cCc",
                "WaW",
                "ccc",
                'c', Items.cookie,
                'C', new ItemStack(Items.dye, 1, 12),
                'W', Items.wheat,
                'a', ModItems.adventureBackpack
        );

        Cow = reviewRecipe(
                "BLB",
                "BaB",
                "LML",
                'B', Items.beef,
                'a', ModItems.adventureBackpack,
                'L', Items.leather,
                'M', Items.milk_bucket
        );

        Creeper = reviewRecipe(
                "GHG",
                "GaG",
                "TNT",//see what I did there? ;D
                'G', Items.gunpowder,
                'H', new ItemStack(Blocks.skull, 1, 4),
                'a', ModItems.adventureBackpack,
                'T', Blocks.tnt,
                'N', Blocks.tnt
        );

        Cyan = reviewRecipe(
                "XXX",
                "XaX",
                "XXX",
                'X', new ItemStack(Blocks.wool, 1, 9),
                'a', ModItems.adventureBackpack
        );

        Diamond = reviewRecipe(
                "GDG",
                "GaG",
                "GdG",
                'G', Blocks.glass,
                'D', Blocks.diamond_block,
                'a', ModItems.adventureBackpack,
                'd', Items.diamond
        );

        Dragon = reviewRecipe(
                "EDE",
                "OaO",
                "POP",
                'E', Blocks.end_stone,
                'D', new ItemStack(Blocks.dragon_egg, 1),
                'O', Blocks.obsidian,
                'a', ModItems.adventureBackpack,
                'P', Items.ender_pearl
        );
    }

    ;


    public final ItemStack[] Black;
    public final ItemStack[] Blaze;
    public final ItemStack[] Blue;
    public final ItemStack[] Bookshelf;
    public final ItemStack[] Brown;
    public final ItemStack[] BrownMushroom;
    public final ItemStack[] Cactus;
    public final ItemStack[] Cake;
    public final ItemStack[] Chicken;
    public final ItemStack[] Chest;
    public final ItemStack[] Coal;
    public final ItemStack[] Cookie;
    public final ItemStack[] Cow;
    public final ItemStack[] Creeper;
    public final ItemStack[] Cyan;
    public final ItemStack[] Diamond;
    public final ItemStack[] Dragon;


    public static ItemStack[] reviewRecipe(Object... objects)
    {
        String s = "";
        int i = 0;
        int j = 0;
        int k = 0;

        if (objects[i] instanceof String[])
        {
            String[] astring = (String[]) ((String[]) objects[i++]);

            for (int l = 0; l < astring.length; ++l)
            {
                String s1 = astring[l];
                ++k;
                j = s1.length();
                s = s + s1;
            }
        } else
        {
            while (objects[i] instanceof String)
            {
                String s2 = (String) objects[i++];
                ++k;
                j = s2.length();
                s = s + s2;
            }
        }

        HashMap hashmap;

        for (hashmap = new HashMap(); i < objects.length; i += 2)
        {
            Character character = (Character) objects[i];
            ItemStack itemstack1 = null;

            if (objects[i + 1] instanceof Item)
            {
                itemstack1 = new ItemStack((Item) objects[i + 1]);
            } else if (objects[i + 1] instanceof Block)
            {
                itemstack1 = new ItemStack((Block) objects[i + 1], 1);
            } else if (objects[i + 1] instanceof ItemStack)
            {
                itemstack1 = (ItemStack) objects[i + 1];
            }

            hashmap.put(character, itemstack1);
        }

        ItemStack[] aitemstack = new ItemStack[j * k];

        for (int i1 = 0; i1 < j * k; ++i1)
        {
            char c0 = s.charAt(i1);

            if (hashmap.containsKey(Character.valueOf(c0)))
            {
                aitemstack[i1] = ((ItemStack) hashmap.get(Character.valueOf(c0))).copy();
            } else
            {
                aitemstack[i1] = null;
            }
        }

        return aitemstack;
    }

}
