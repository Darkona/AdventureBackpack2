package com.darkona.adventurebackpack.init.recipes;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.reference.BackpackTypes;

/**
 * Created on 20/10/2014
 *
 * @author Darkona
 */
public class BackpackRecipesList
{
    public List<BackpackRecipe> recipes;

    public BackpackRecipesList()
    {
        String[] covered = {"XXX", "XaX", "XXX"};
        ItemStack backpack = BackpackTypes.setBackpackSkinNameFromMeta(new ItemStack(ModItems.adventureBackpack), 0);

        ItemStack woolWhite = new ItemStack(Blocks.wool, 1, 0);
        ItemStack woolOrange = new ItemStack(Blocks.wool, 1, 1);
        ItemStack woolMagenta = new ItemStack(Blocks.wool, 1, 2);
        ItemStack woolLightBlue = new ItemStack(Blocks.wool, 1, 3);
        ItemStack woolYellow = new ItemStack(Blocks.wool, 1, 4);
        ItemStack woolLime = new ItemStack(Blocks.wool, 1, 5);
        ItemStack woolPink = new ItemStack(Blocks.wool, 1, 6);
        ItemStack woolGray = new ItemStack(Blocks.wool, 1, 7);
        ItemStack woolLightGray = new ItemStack(Blocks.wool, 1, 8);
        ItemStack woolCyan = new ItemStack(Blocks.wool, 1, 9);
        ItemStack woolPurple = new ItemStack(Blocks.wool, 1, 10);
        ItemStack woolBlue = new ItemStack(Blocks.wool, 1, 11);
        ItemStack woolBrown = new ItemStack(Blocks.wool, 1, 12);
        ItemStack woolGreen = new ItemStack(Blocks.wool, 1, 13);
        ItemStack woolRed = new ItemStack(Blocks.wool, 1, 14);
        ItemStack woolBlack = new ItemStack(Blocks.wool, 1, 15);

        Standard = reviewRecipe(
                "LGL",
                "TCT",
                "LSL",
                'L', Items.leather,
                'G', "ingotGold",
                'T', new ItemStack(ModItems.component, 1, 2),
                'S', new ItemStack(ModItems.component, 1, 1),
                'C', Blocks.chest
        );

        Black = reviewRecipe(covered,
                'X', woolBlack,
                'a', backpack
        );

        Blaze = reviewRecipe(
                "BFB",
                "BaB",
                "PLP",
                'B', Items.blaze_rod,
                'F', Items.fire_charge,
                'a', backpack,
                'P', Items.blaze_powder,
                'L', Items.lava_bucket
        );

        Blue = reviewRecipe(covered,
                'X', woolBlue,
                'a', backpack
        );

        Bookshelf = reviewRecipe(
                "BDB",
                "BaB",
                "bbb",
                'B', Blocks.bookshelf,
                'a', backpack,
                'b', Items.book
        );

        Brown = reviewRecipe(covered,
                'X', woolBrown,
                'a', backpack
        );

        BrownMushroom = reviewRecipe(covered,
                'X', Blocks.brown_mushroom,
                'a', backpack
        );

        Cactus = reviewRecipe(
                "CGC",
                "CaC",
                "SSS",
                'C', Blocks.cactus,
                'G', new ItemStack(Items.dye, 1, 2),
                'a', backpack,
                'S', Blocks.sand
        );

        Cake = reviewRecipe(
                "ECE",
                "WaW",
                "SmS",
                'a', backpack,
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
                'a', backpack,
                'E', Items.emerald,
                'D', Items.diamond
        );

        Chest = reviewRecipe(
                "CWC",
                "WaW",
                "CWC",
                'C', Blocks.chest,
                'W', "plankWood",
                'a', backpack
        );

        Chicken = reviewRecipe(
                "FnF",
                "FaF",
                "nEn",
                'F', Items.feather,
                'n', Items.gold_nugget,
                'a', backpack,
                'E', Items.egg
        );

        Coal = reviewRecipe(
                "cCc",
                "CaC",
                "ccc",
                'c', Items.coal,
                'C', "blockCoal",
                'a', backpack
        );

        Cookie = reviewRecipe(
                "cCc",
                "WaW",
                "ccc",
                'c', Items.cookie,
                'C', new ItemStack(Items.dye, 1, 3),//Chocolate
                'W', Items.wheat,
                'a', backpack
        );

        Cow = reviewRecipe(
                "BLB",
                "BaB",
                "LML",
                'B', Items.beef,
                'a', backpack,
                'L', Items.leather,
                'M', Items.milk_bucket
        );

        Creeper = reviewRecipe(
                "GHG",
                "GaG",
                "TNT",//see what I did there? ;D
                'G', Items.gunpowder,
                'H', new ItemStack(Items.skull, 1, 4), //Creeper Skull
                'a', backpack,
                'T', Blocks.tnt,
                'N', Blocks.tnt
        );

        Cyan = reviewRecipe(covered,
                'X', woolCyan,
                'a', backpack
        );

        Diamond = reviewRecipe(
                "GDG",
                "GaG",
                "GdG",
                'G', "blockGlass",
                'D', "blockDiamond",
                'a', backpack,
                'd', "gemDiamond"
        );

        Dragon = reviewRecipe(
                "EDE",
                "OaO",
                "POP",
                'E', Blocks.end_stone,
                'D', new ItemStack(Blocks.dragon_egg, 1),
                'O', Blocks.obsidian,
                'a', backpack,
                'P', Items.ender_pearl
        );

        Egg = reviewRecipe(covered,
                'X', Items.egg,
                'a', backpack
        );

        Emerald = reviewRecipe(
                "GEG",
                "GaG",
                "eGe",
                'G', Blocks.glass,
                'E', Blocks.emerald_block,
                'a', backpack,
                'e', Items.emerald
        );

        End = reviewRecipe(
                "eEe",
                "EaE",
                "eEe",
                'E', Blocks.end_stone,
                'e', Items.ender_eye,
                'a', backpack
        );

        Enderman = reviewRecipe(
                "PXP",
                "XaX",
                "PXP",
                'X', woolBlack,
                'P', Items.ender_pearl,
                'a', backpack
        );

        Ghast = reviewRecipe(
                "GFG",
                "TaT",
                "GTG",
                'G', Items.ghast_tear,
                'F', Items.fire_charge,
                'T', Items.gunpowder,
                'a', backpack
        );

        Glowstone = reviewRecipe(
                "GgG",
                "GaG",
                "GgG",
                'G', Blocks.glowstone,
                'g', "dustGlowstone",
                'a', backpack
        );

        Gold = reviewRecipe(
                "FGF",
                "FaF",
                "gFg",
                'F', "blockGlass",
                'G', "blockGold",
                'a', backpack,
                'g', "ingotGold"
        );

        Gray = reviewRecipe(covered,
                'X', woolGray,
                'a', backpack
        );

        Green = reviewRecipe(covered,
                'X', woolGreen,
                'a', backpack
        );

        Haybale = reviewRecipe(covered,
                'X', Blocks.hay_block,
                'a', backpack
        );

        Iron = reviewRecipe(
                "GIG",
                "GaG",
                "iGi",
                'G', "blockGlass",
                'I', "blockIron",
                'a', backpack,
                'i', Items.iron_ingot
        );

        Lapis = reviewRecipe(
                "GLG",
                "GaG",
                "lGl",
                'G', "blockGlass",
                'L', "blockLapis",
                'l', new ItemStack(Items.dye, 1, 4),
                'a', backpack
        );

        Leather = reviewRecipe(covered,
                'X', Items.leather,
                'a', backpack
        );

        LightBlue = reviewRecipe(covered,
                'X', woolLightBlue,
                'a', backpack
        );

        LightGray = reviewRecipe(covered,
                'X', woolLightGray,
                'a', backpack
        );

        Lime = reviewRecipe(covered,
                'X', woolLime,
                'a', backpack
        );

        Magenta = reviewRecipe(covered,
                'X', woolMagenta,
                'a', backpack
        );

        MagmaCube = reviewRecipe(
                "MLM",
                "MaM",
                "MLM",
                'M', Items.magma_cream,
                'a', backpack,
                'L', Items.lava_bucket
        );

        Melon = reviewRecipe(
                "mMm",
                "mam",
                "msm",
                'm', Items.melon,
                'M', Blocks.melon_block,
                'a', backpack,
                's', Items.melon_seeds
        );

        Mooshroom = reviewRecipe(
                "SRL",
                "BaB",
                "LRS",
                'R', Blocks.red_mushroom,
                'B', Blocks.brown_mushroom,
                'a', backpack,
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
                'a', backpack
        );

        Obsidian = reviewRecipe(
                covered,
                'X', Blocks.obsidian,
                'a', backpack
        );

        Ocelot = reviewRecipe(
                "FYF",
                "YaY",
                "FYF",
                'F', Items.fish,
                'Y', new ItemStack(Blocks.wool, 1, 4),
                'a', backpack
        );

        Orange = reviewRecipe(covered,
                'X', woolOrange,
                'a', backpack
        );

        Pig = reviewRecipe(covered,
                'X', Items.porkchop,
                'a', backpack
        );

        Pink = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 6),
                'a', backpack
        );

        Pumpkin = reviewRecipe(
                "PPP",
                "PaP",
                "PsP",
                'P', Blocks.pumpkin,
                'a', backpack,
                's', Items.pumpkin_seeds
        );

        Purple = reviewRecipe(covered,
                'X', woolPurple,
                'a', backpack
        );

        Quartz = reviewRecipe(
                "QqQ",
                "qaq",
                "QqQ",
                'Q', Blocks.quartz_block,
                'q', Items.quartz,
                'a', backpack
        );

        Rainbow = reviewRecipe(
                "RCP",
                "OaB",
                "YGF",
                'R', "dyeRed", //RED
                'O', "dyeOrange",//ORANGE
                'Y', "dyeYellow",//YELLOW
                'G', "dyeLime",//LIME
                'F', "dyeCyan",//CYAN
                'B', "dyeBlue",//BLUE
                'P', "dyePurple",//PURPLE
                'C', Items.record_cat,
                'a', backpack
        );

        Red = reviewRecipe(covered,
                'X', woolRed,
                'a', backpack
        );

        RedMushroom = reviewRecipe(covered,
                'X', Blocks.red_mushroom,
                'a', backpack
        );

        Redstone = reviewRecipe(
                "rRr",
                "RaR",
                "rRr",
                'R', "blockRedstone",
                'r', Items.redstone,
                'a', backpack
        );

        Sandstone = reviewRecipe(
                "CSC",
                "SaS",
                "CSC",
                'S', new ItemStack(Blocks.sandstone, 1, 0),
                'C', new ItemStack(Blocks.sandstone, 1, 1),
                'a', backpack
        );

        Sheep = reviewRecipe(
                "WPW",
                "WaW",
                "WWW",
                'W', woolWhite,
                'P', woolPink,
                'a', backpack
        );

        Skeleton = reviewRecipe(
                "BSB",
                "bab",
                "BAB",
                'B', Items.bone,
                'S', new ItemStack(Items.skull, 1, 0),//Skeleton skull
                'b', Items.bow,
                'A', Items.arrow,
                'a', backpack
        );

        Slime = reviewRecipe(covered,
                'X', "slimeball",
                'a', backpack
        );

        Snow = reviewRecipe(
                "III",
                "SaS",
                "sSs",
                'I', Blocks.ice,
                'S', Blocks.snow,
                's', Items.snowball,
                'a', backpack
        );

        Spider = reviewRecipe(
                "ESE",
                "LaL",
                "ESE",
                'E', Items.spider_eye,
                'S', Items.string,
                'L', Blocks.ladder,
                'a', backpack
        );

        White = reviewRecipe(covered,
                'X', new ItemStack(Blocks.wool, 1, 0),
                'a', backpack
        );

        Wither = reviewRecipe(
                "SSS",
                "sas",
                "NsD",
                'S', new ItemStack(Items.skull, 1, 1),//WitherSkelleton Skull
                's', Blocks.soul_sand,
                'N', Items.nether_star,
                'D', Items.diamond,
                'a', backpack
        );

        WitherSkeleton = reviewRecipe(
                "BsB",
                "SaS",
                "CBC",
                'B', Items.bone,
                'S', Items.stone_sword,
                'a', backpack,
                'C', Items.coal,
                's', new ItemStack(Items.skull, 1, 1)
        );

        Wolf = reviewRecipe(
                "BWB",
                "WaW",
                "BWB",
                'B', Items.bone,
                'W', woolWhite,
                'a', backpack
        );

        Yellow = reviewRecipe(covered,
                'X', woolYellow,
                'a', backpack
        );

        Zombie = reviewRecipe(
                "FSF",
                "FaF",
                "FFF",
                'F', Items.rotten_flesh,
                'S', new ItemStack(Items.skull, 1, 2),
                'a', backpack
        );

        Carrot = reviewRecipe(covered,
                'a', backpack,
                'X', Items.carrot
        );

        Silverfish = reviewRecipe(
                "CGC",
                "CaC",
                "CGC",
                'a', backpack,
                'G', woolLightGray,
                'C', new ItemStack(Blocks.stonebrick, 1, 2)
        );

        Sunflower = reviewRecipe(
                covered,
                'a', backpack,
                'X', new ItemStack(Blocks.double_plant, 1, 0)
        );

        Horse = reviewRecipe(
                "LSL",
                "XaX",
                "LXL",
                'L', Items.leather,
                'S', Items.saddle,
                'X', Blocks.hay_block,
                'a', backpack);

        Overworld = reviewRecipe(
                "BBB",
                "GaG",
                "CCC",
                'B', new ItemStack(Blocks.wool, 1, 3),
                'G', Blocks.grass,
                'a', backpack,
                'C', "cobblestone"

        );

        Squid = reviewRecipe(
                "BIB",
                "IaI",
                "BIB",
                'a', backpack,
                'B', woolBlue,
                'I', new ItemStack(Items.dye, 1, 0)
        );

        Sponge = reviewRecipe(covered,
                'X', Blocks.sponge,
                'a', backpack
        );
    }

    public final Object[] Silverfish;
    public final Object[] Squid;
    public final Object[] Sunflower;
    public final Object[] Horse;
    public final Object[] Overworld;
    public final Object[] Carrot;
    public final Object[] Black;
    public final Object[] Blaze;
    public final Object[] Blue;
    public final Object[] Bookshelf;
    public final Object[] Brown;
    public final Object[] BrownMushroom;
    public final Object[] Cactus;
    public final Object[] Cake;
    public final Object[] Chicken;
    public final Object[] Chest;
    public final Object[] Coal;
    public final Object[] Cookie;
    public final Object[] Cow;
    public final Object[] Creeper;
    public final Object[] Cyan;
    public final Object[] Diamond;
    public final Object[] Dragon;
    public final Object[] Egg;
    public final Object[] Emerald;
    public final Object[] End;
    public final Object[] Enderman;
    public final Object[] Ghast;
    public final Object[] Glowstone;
    public final Object[] Gold;
    public final Object[] Gray;
    public final Object[] Green;
    public final Object[] Haybale;
    public final Object[] Iron;
    public final Object[] Lapis;
    public final Object[] Leather;
    public final Object[] LightBlue;
    public final Object[] LightGray;
    public final Object[] Lime;
    public final Object[] Magenta;
    public final Object[] MagmaCube;
    public final Object[] Melon;
    public final Object[] Mooshroom;
    public final Object[] Nether;
    public final Object[] Obsidian;
    public final Object[] Ocelot;
    public final Object[] Orange;
    public final Object[] Pig;
    public final Object[] Pink;
    public final Object[] Pumpkin;
    public final Object[] Purple;
    public final Object[] Quartz;
    public final Object[] Rainbow;
    public final Object[] Red;
    public final Object[] RedMushroom;
    public final Object[] Redstone;
    public final Object[] Sandstone;
    public final Object[] Sheep;
    public final Object[] Skeleton;
    public final Object[] Slime;
    public final Object[] Snow;
    public final Object[] Spider;
    public final Object[] Standard;
    public final Object[] Sponge;
    public final Object[] White;
    public final Object[] Wither;
    public final Object[] WitherSkeleton;
    public final Object[] Wolf;
    public final Object[] Yellow;
    public final Object[] Zombie;
    public final Object[] ModdedNetwork;

    public static Object[] reviewRecipe(Object... objects)
    {
        return objects;
    }
}
