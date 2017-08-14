package com.darkona.adventurebackpack.init.recipes;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.darkona.adventurebackpack.init.ModItems;

/**
 * Created on 20/10/2014
 *
 * @author Darkona
 */
public class BackpackRecipes
{
    public List<BackpackRecipe> recipes;

    BackpackRecipes()
    {
        String[] covered = {"XXX", "XaX", "XXX"};
        Black = reviewRecipe(covered,
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

        Blue = reviewRecipe(covered,
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

        Brown = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 12),
                'a', ModItems.adventureBackpack
        );

        BrownMushroom = reviewRecipe(covered,
                'X', Blocks.brown_mushroom,
                'a', ModItems.adventureBackpack
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

        ModdedNetwork = reviewRecipe(
                "EEE",
                "DaD",
                "DDD",
                'a', ModItems.adventureBackpack,
                'E', Items.emerald,
                'D', Items.diamond
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
                'C', new ItemStack(Items.dye, 1, 3),
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
                'H', new ItemStack(Items.skull, 1, 4), //Creeper Skull
                'a', ModItems.adventureBackpack,
                'T', Blocks.tnt,
                'N', Blocks.tnt
        );

        Cyan = reviewRecipe(covered,
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

        Egg = reviewRecipe(covered,
                'X', Items.egg,
                'a', ModItems.adventureBackpack
        );

        Emerald = reviewRecipe(
                "GEG",
                "GaG",
                "eGe",
                'G', Blocks.glass,
                'E', Blocks.emerald_block,
                'a', ModItems.adventureBackpack,
                'e', Items.emerald
        );

        End = reviewRecipe(
                "eEe",
                "EaE",
                "eEe",
                'E', Blocks.end_stone,
                'e', Items.ender_eye,
                'a', ModItems.adventureBackpack
        );

        Enderman = reviewRecipe(
                "PXP",
                "XaX",
                "PXP",
                'X', new ItemStack(Blocks.wool, 1, 15),
                'P', Items.ender_pearl,
                'a', ModItems.adventureBackpack
        );

        Ghast = reviewRecipe(
                "GFG",
                "TaT",
                "GTG",
                'G', Items.ghast_tear,
                'F', Items.fire_charge,
                'T', Items.gunpowder,
                'a', ModItems.adventureBackpack
        );

        Glowstone = reviewRecipe(
                "GgG",
                "GaG",
                "GgG",
                'G', Blocks.glowstone,
                'g', Items.glowstone_dust,
                'a', ModItems.adventureBackpack
        );

        Gold = reviewRecipe(
                "FGF",
                "FaF",
                "gFg",
                'F', Blocks.glass,
                'G', Blocks.gold_block,
                'a', ModItems.adventureBackpack,
                'g', Items.gold_ingot
        );

        Gray = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 7),
                'a', ModItems.adventureBackpack
        );

        Green = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 13),
                'a', ModItems.adventureBackpack
        );

        Haybale = reviewRecipe(covered,
                'X', Blocks.hay_block,
                'a', ModItems.adventureBackpack
        );

        Iron = reviewRecipe(
                "GIG",
                "GaG",
                "iGi",
                'G', Blocks.glass,
                'I', Blocks.iron_block,
                'a', ModItems.adventureBackpack,
                'i', Items.iron_ingot
        );

        Lapis = reviewRecipe(
                "GLG",
                "GaG",
                "lGl",
                'G', Blocks.glass,
                'L', Blocks.lapis_block,
                'l', new ItemStack(Items.dye, 1, 4),
                'a', ModItems.adventureBackpack
        );

        Leather = reviewRecipe(covered,
                'X', Items.leather,
                'a', ModItems.adventureBackpack
        );

        LightBlue = reviewRecipe(
                "XXX",
                "XaX",
                "XXX",
                'X', new ItemStack(Blocks.wool, 1, 3),
                'a', ModItems.adventureBackpack
        );

        LightGray = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 8),
                'a', ModItems.adventureBackpack
        );

        Lime = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 5),
                'a', ModItems.adventureBackpack
        );

        Magenta = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 2),
                'a', ModItems.adventureBackpack
        );

        MagmaCube = reviewRecipe(
                "MLM",
                "MaM",
                "MLM",
                'M', Items.magma_cream,
                'a', ModItems.adventureBackpack,
                'L', Items.lava_bucket
        );

        Melon = reviewRecipe(
                "mMm",
                "mam",
                "msm",
                'm', Items.melon,
                'M', Blocks.melon_block,
                'a', ModItems.adventureBackpack,
                's', Items.melon_seeds
        );

        Mooshroom = reviewRecipe(
                "SRL",
                "BaB",
                "LRS",
                'R', Blocks.red_mushroom,
                'B', Blocks.brown_mushroom,
                'a', ModItems.adventureBackpack,
                'S', Items.mushroom_stew,
                'L', Blocks.mycelium
        );

        Nether = reviewRecipe(
                "QwQ",
                "NaN",
                "QLQ",
                'Q', Items.quartz,
                'N', Blocks.netherrack,
                'w', Items.nether_wart,
                'L', Items.lava_bucket,
                'a', ModItems.adventureBackpack
        );

        Obsidian = reviewRecipe(
                covered,
                'X', Blocks.obsidian,
                'a', ModItems.adventureBackpack
        );

        Ocelot = reviewRecipe(
                "FYF",
                "YaY",
                "FYF",
                'F', Items.fish,
                'Y', new ItemStack(Blocks.wool, 1, 4),
                'a', ModItems.adventureBackpack
        );

        Orange = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 1),
                'a', ModItems.adventureBackpack
        );

        Pig = reviewRecipe(covered,
                'X', Items.porkchop,
                'a', ModItems.adventureBackpack
        );

        Pink = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 6),
                'a', ModItems.adventureBackpack
        );

        Pumpkin = reviewRecipe(
                "PPP",
                "PaP",
                "PsP",
                'P', Blocks.pumpkin,
                'a', ModItems.adventureBackpack,
                's', Items.pumpkin_seeds
        );

        Purple = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 10),
                'a', ModItems.adventureBackpack
        );

        Quartz = reviewRecipe(
                "QqQ",
                "qaq",
                "QqQ",
                'Q', Blocks.quartz_block,
                'q', Items.quartz,
                'a', ModItems.adventureBackpack
        );

        Rainbow = reviewRecipe(
                "RCP",
                "OaB",
                "YGF",
                'R', new ItemStack(Items.dye, 1, 1), //RED
                'O', new ItemStack(Items.dye, 1, 14),//ORANGE
                'Y', new ItemStack(Items.dye, 1, 11),//YELLOW
                'G', new ItemStack(Items.dye, 1, 10),//LIME
                'F', new ItemStack(Items.dye, 1, 6),//CYAN
                'B', new ItemStack(Items.dye, 1, 4),//BLUE
                'P', new ItemStack(Items.dye, 1, 5),//PURPLE
                'C', Items.record_cat,
                'a', ModItems.adventureBackpack
        );

        Red = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 14),
                'a', ModItems.adventureBackpack
        );

        RedMushroom = reviewRecipe(covered,
                'X', Blocks.red_mushroom,
                'a', ModItems.adventureBackpack
        );

        Redstone = reviewRecipe(
                "rRr",
                "RaR",
                "rRr",
                'R', Blocks.redstone_block,
                'r', Items.redstone,
                'a', ModItems.adventureBackpack
        );

        Sandstone = reviewRecipe(
                "CSC",
                "SaS",
                "CSC",
                'S', new ItemStack(Blocks.sandstone, 1, 0),
                'C', new ItemStack(Blocks.sandstone, 1, 1),
                'a', ModItems.adventureBackpack
        );

        Sheep = reviewRecipe(
                "WPW",
                "WaW",
                "WWW",
                'W', new ItemStack(Blocks.wool, 1, 0),
                'P', new ItemStack(Blocks.wool, 1, 6),
                'a', ModItems.adventureBackpack
        );

        Skeleton = reviewRecipe(
                "BSB",
                "bab",
                "BAB",
                'B', Items.bone,
                'S', new ItemStack(Items.skull, 1, 0),//Skeleton skull
                'b', Items.bow,
                'A', Items.arrow,
                'a', ModItems.adventureBackpack
        );

        Slime = reviewRecipe(covered,
                'X', Items.slime_ball,
                'a', ModItems.adventureBackpack
        );

        Snow = reviewRecipe(
                "sSs",
                "SaS",
                "sSs",
                'S', Blocks.snow,
                's', Items.snowball,
                'a', ModItems.adventureBackpack
        );

        Spider = reviewRecipe(
                "ESE",
                "LaL",
                "ESE",
                'E', Items.spider_eye,
                'S', Items.string,
                'L', Blocks.ladder,
                'a', ModItems.adventureBackpack
        );

        White = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 0),
                'a', ModItems.adventureBackpack
        );

        Wither = reviewRecipe(
                "SSS",
                "sas",
                "NsD",
                'S', new ItemStack(Items.skull, 1, 1),//WitherSkelleton Skull
                's', Blocks.soul_sand,
                'N', Items.nether_star,
                'D', Items.diamond,
                'a', ModItems.adventureBackpack
        );

        WitherSkeleton = reviewRecipe(
                "BsB",
                "SaS",
                "CBC",
                'B', Items.bone,
                'S', Items.stone_sword,
                'a', ModItems.adventureBackpack,
                'C', Items.coal,
                's', new ItemStack(Items.skull, 1, 1)
        );

        Wolf = reviewRecipe(
                "BWB",
                "WaW",
                "BWB",
                'B', Items.bone,
                'W', new ItemStack(Blocks.wool, 1, 0),
                'a', ModItems.adventureBackpack
        );

        Yellow = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 4),
                'a', ModItems.adventureBackpack
        );

        Zombie = reviewRecipe(
                "FSF",
                "FaF",
                "FFF",
                'F', Items.rotten_flesh,
                'S', new ItemStack(Items.skull, 1, 2),
                'a', ModItems.adventureBackpack
        );
    }

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
    public final ItemStack[] Egg;
    public final ItemStack[] Emerald;
    public final ItemStack[] End;
    public final ItemStack[] Enderman;
    public final ItemStack[] Ghast;
    public final ItemStack[] Glowstone;
    public final ItemStack[] Gold;
    public final ItemStack[] Gray;
    public final ItemStack[] Green;
    public final ItemStack[] Haybale;
    public final ItemStack[] Iron;
    public final ItemStack[] Lapis;
    public final ItemStack[] Leather;
    public final ItemStack[] LightBlue;
    public final ItemStack[] LightGray;
    public final ItemStack[] Lime;
    public final ItemStack[] Magenta;
    public final ItemStack[] MagmaCube;
    public final ItemStack[] Melon;
    public final ItemStack[] Mooshroom;
    public final ItemStack[] Nether;
    public final ItemStack[] Obsidian;
    public final ItemStack[] Ocelot;
    public final ItemStack[] Orange;
    public final ItemStack[] Pig;
    public final ItemStack[] Pink;
    public final ItemStack[] Pumpkin;
    public final ItemStack[] Purple;
    public final ItemStack[] Quartz;
    public final ItemStack[] Rainbow;
    public final ItemStack[] Red;
    public final ItemStack[] RedMushroom;
    public final ItemStack[] Redstone;
    public final ItemStack[] Sandstone;
    public final ItemStack[] Sheep;
    public final ItemStack[] Skeleton;
    public final ItemStack[] Slime;
    public final ItemStack[] Snow;
    public final ItemStack[] Spider;
    //public final ItemStack[] Sponge;
    public final ItemStack[] White;
    public final ItemStack[] Wither;
    public final ItemStack[] WitherSkeleton;
    public final ItemStack[] Wolf;
    public final ItemStack[] Yellow;
    public final ItemStack[] Zombie;
    public final ItemStack[] ModdedNetwork;


    @SuppressWarnings("unchecked")
    public static ItemStack[] reviewRecipe(Object... objects)
    {
        String s = "";
        //BackpackRecipe recipe = new BackpackRecipe();
        int i = 0;
        int j = 0;
        int k = 0;
        /*if(objects[i] instanceof String)
        {
            recipe.name = (String)objects[i];
            i++;
        }*/
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
        }
        else
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
            }
            else if (objects[i + 1] instanceof Block)
            {
                itemstack1 = new ItemStack((Block) objects[i + 1], 1);
            }
            else if (objects[i + 1] instanceof ItemStack)
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
            }
            else
            {
                aitemstack[i1] = null;
            }
        }
        //recipe.array = aitemstack;
        return aitemstack;
    }
}
