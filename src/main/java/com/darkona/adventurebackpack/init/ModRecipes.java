package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.recipes.BackpackRecipesList;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;

import java.lang.reflect.Field;

/**
 * Created on 20/10/2014
 *
 * @author Darkona
 */
public class ModRecipes
{
    private static ItemStack bc(int damage)
    {
        return BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), damage);
    }

    public static void init()
    {

        //CampFire
        GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.blockCampFire,
                " S ",
                "SxS",
                "ccc",
                'S', "stickWood",
                'x', Items.coal,
                'c', "cobblestone"
        ));

        //Sleeping Bag
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

        /*GameRegistry.addRecipe(a,
                "LGL",
                "TCT",
                "LSL",
                'L', Items.leather,
                'G', Items.gold_ingot,
                'T', new ItemStack(ModItems.component, 1, 2),
                'S', new ItemStack(ModItems.component, 1, 1),
                'C', Blocks.chest
        );*/

        BackpackRecipesList br = new BackpackRecipesList();
        int counter = 0;
        for (int i = 0; i < BackpackNames.backpackNames.length; i++)
        {
            for (Field field : BackpackRecipesList.class.getFields())
            {
                try
                {
                    if (field.getName().equals((BackpackNames.backpackNames[i])))
                    {
                        GameRegistry.addRecipe(new ShapedOreRecipe(BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), i), (Object[]) field.get(br)));
                        counter++;
                    }
                } catch (Exception oops)
                {
                    LogHelper.error("Huge mistake during reflection. Some bad things might happen: " + oops.getClass().getName());
                    oops.printStackTrace();
                }
            }

        }
        LogHelper.info("Loaded " + counter + " backpack recipes.");

        //GameRegistry.addRecipe(new AbstractBackpackRecipe());
        /*BackpackRecipes br = new BackpackRecipes();
        int i = 0;
        for (Field field : BackpackRecipes.class.getFields())
        {
            try
            {
                if (field.getType() == ItemStack[].class)
                {
                    AbstractBackpackRecipeTwo recipe = new AbstractBackpackRecipeTwo(field.getName(), (ItemStack[]) field.get(br));
                    GameRegistry.addRecipe(recipe);
                    //LogHelper.info("Loaded recipe for " + field.getName() + " backpack.");
                    i++;
                }
            } catch (Exception oops)
            {
                LogHelper.error("Huge mistake during reflection. Some bad things might happen.");
            }
        }
        LogHelper.info("Loaded " + i + " backpack recipes.");
        RecipeSorter.register(ModInfo.MOD_ID + ":adventureBackpack", AbstractBackpackRecipeTwo.class, RecipeSorter.Category.SHAPED, "after:minecraft:shapeless");
        */
    }

    public static void conditionalInit()
    {
        if(ConfigHandler.SADDLE_RECIPE)
        {
            GameRegistry.addRecipe(new ItemStack(Items.saddle),
                    "LLL",
                    "L L",
                    "I I",
                    'L', Items.leather,
                    'I', Items.iron_ingot
            );
        }

        if(ConfigHandler.IS_BUILDCRAFT)
        {
            //Copter Engine
            GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 5),
                    "IGI",
                    "PCP",
                    "FOF",
                    'I', Items.iron_ingot,
                    'G', Items.gold_ingot,
                    'P', Blocks.piston,
                    'F', Blocks.furnace,
                    'C', Items.cauldron,
                    'O', Blocks.obsidian
            );

            //Copter Blades
            GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 6),
                    "III",
                    " F ",
                    " F ",
                    'I', Items.iron_ingot,
                    'F', Blocks.fence
            );

            //Copter Pack
            GameRegistry.addRecipe(new ItemStack(ModItems.copterPack),
                    "WBW",
                    "TEI",
                    "CDI",
                    'W', Blocks.planks,
                    'B', new ItemStack(ModItems.component, 1, 6),
                    'T', new ItemStack(ModItems.component, 1, 2),
                    'E', new ItemStack(ModItems.component, 1, 5),
                    'C', new ItemStack(Items.dye, 1, 2),
                    'D', Items.diamond,
                    'I', Items.iron_ingot
            );
        }

        //Ok let's try getting railcraft stuff.
        //Seriously this is hard
        if(ConfigHandler.IS_RAILCRAFT)
        {
            try
            {
                LogHelper.info("Railcraft is present. Adding Steam Jetpack.");
                ItemStack firebox = GameRegistry.findItemStack("Railcraft", "machine.beta.boiler.firebox.solid", 1);
                ItemStack boiler = GameRegistry.findItemStack("Railcraft", "machine.beta.boiler.tank.pressure.low", 1);
                if(firebox != null && boiler != null)
                {
                    GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.steamJetpack),
                            "BWT",
                            "GIG",
                            "xWC",
                            'W', "plankWood",
                            'G', "ingotGold",
                            'I',"ingotIron",
                            'B', boiler,
                            'x', firebox,
                            'C', new ItemStack(Items.dye, 1, 2),
                            'T', new ItemStack(ModItems.component, 1, 2)
                    ));
                }

            }catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
