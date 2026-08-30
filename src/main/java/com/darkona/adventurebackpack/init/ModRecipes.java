package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.common.BoatRecipeRules;
import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.compat.tech.TechParts;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.recipes.BackpackRecipesList;
import com.darkona.adventurebackpack.init.recipes.BackpackUpgradeRecipe;
import com.darkona.adventurebackpack.item.ItemComponent;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.reference.ModInfo;
import com.darkona.adventurebackpack.util.LogHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created on 20/10/2014
 *
 * @author Darkona
 */
public class ModRecipes
{
    /** The Clockwork Crossbow, built around a clock, and its Bolt Cartridges: 6 arrows and an iron ingot each. */
    private static void crossbowRecipes()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.cwxbow),
                "IBI",
                "SCS",
                " P ",
                'I', "ingotIron",
                'B', new ItemStack(Items.bow, 1, OreDictionary.WILDCARD_VALUE),
                'S', Items.string,
                'C', Items.clock,
                'P', "plankWood"
        ));
        cartridgeRecipe(CrossbowLogic.NORMAL, "stickWood");
        if (ConfigHandler.CROSSBOW_ELEMENTAL_ENABLED)
        {
            cartridgeRecipe(CrossbowLogic.BLAZING, Items.blaze_powder);
            cartridgeRecipe(CrossbowLogic.FREEZING, Items.snowball);
            cartridgeRecipe(CrossbowLogic.POISON, Items.spider_eye);
            cartridgeRecipe(CrossbowLogic.STUNNING, Items.slime_ball);
        }
    }

    private static void cartridgeRecipe(int type, Object extra)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.boltCartridge, 1, type),
                "AAA",
                "XIX",
                "AAA",
                'A', Items.arrow,
                'X', extra,
                'I', "ingotIron"
        ));
    }

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
/*
        GameRegistry.addRecipe(new ItemStack(ModItems.adventureSuit),
                " V ",
                " W ",
                " P ",
                'V', Items.leather_chestplate,
                'W', Blocks.wool,
                'P', Items.leather_leggings
        );
*/
        if (ConfigHandler.PISTONBOOTS_ENABLED) GameRegistry.addRecipe(new ItemStack(ModItems.pistonBoots),
                " B ",
                "PSP",
                'B', Items.leather_boots,
                'P', Blocks.piston,
                'S', Items.slime_ball
        );

        GameRegistry.addShapelessRecipe(new ItemStack(ModItems.melonJuiceBottle),
                Items.melon, Items.potionitem
        );

        if (ConfigHandler.CROSSBOW_ENABLED) crossbowRecipes();

        if (ConfigHandler.HOSE_ENABLED) GameRegistry.addRecipe(new ItemStack(ModItems.hose),
                "NGG",
                "  G",
                "  G",
                'N', new ItemStack(ModItems.component, 1, 3),
                'G', new ItemStack(Items.dye, 1, 2)
        );

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
                        GameRegistry.addRecipe(new BackpackUpgradeRecipe(BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), i), (Object[]) field.get(br)));
                        counter++;
                    }
                } catch (Exception oops)
                {
                    LogHelper.error("Huge mistake during reflection. Some bad things might happen: " + oops.getClass().getName());
                    oops.printStackTrace();
                }
            }

        }
        RecipeSorter.register(ModInfo.MOD_ID + ":backpackUpgrade", BackpackUpgradeRecipe.class, RecipeSorter.Category.SHAPED, "after:forge:shapedore");
        LogHelper.info("Loaded " + counter + " backpack recipes.");

    }

    private static ItemStack component(int damage, int amount)
    {
        return new ItemStack(ModItems.component, amount, damage);
    }

    /**
     * The Inflatable Boat, its Inflatable Chambers, the Hydro Blades and the motorized boat (recipes.boat). In postInit,
     * so the Ore Dictionary and the other mods' items are complete; the items are always registered, only the
     * recipes depend on the mode and on what the loaded mods provide.
     */
    private static void boatRecipes()
    {
        BoatRecipeRules.Mode mode = ConfigHandler.BOAT_RECIPE_MODE;
        List<String> rubbers = TechParts.presentOres(ConfigHandler.BOAT_RUBBER);
        List<Object> motors = TechParts.find(ConfigHandler.BOAT_MOTORS);

        // Inflatable Chamber: leather sealed with slime, or rubber from a technical mod
        if (BoatRecipeRules.vanillaRecipe(mode, !rubbers.isEmpty()))
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.INFLATABLE_CHAMBER, 1),
                    "LSL",
                    'L', Items.leather,
                    'S', Items.slime_ball));
        }
        if (BoatRecipeRules.technicalRecipe(mode, !rubbers.isEmpty()))
        {
            for (String rubber : rubbers)
            {
                GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.INFLATABLE_CHAMBER, 2),
                        "RRR",
                        'R', rubber));
            }
        }

        // Hydro Blades: a small iron propeller
        GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.HYDRO_BLADES, 1),
                "I I",
                " S ",
                "I I",
                'I', "ingotIron",
                'S', "stickWood"));

        // Inflatable Boat: five chambers, a rope around them and the Hose Nozzle as the valve
        GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.INFLATABLE_BOAT, 1),
                "SNS",
                "C C",
                "CCC",
                'S', Items.string,
                'N', component(ItemComponent.HOSE_NOZZLE, 1),
                'C', component(ItemComponent.INFLATABLE_CHAMBER, 1)));

        // Motorized boat: an outboard engine (piston and furnace, or another mod's electric motor) and Hydro Blades
        if (BoatRecipeRules.vanillaRecipe(mode, !motors.isEmpty()))
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.MOTORIZED_BOAT, 1),
                    " P ",
                    "BFH",
                    'P', Blocks.piston,
                    'F', Blocks.furnace,
                    'B', component(ItemComponent.INFLATABLE_BOAT, 1),
                    'H', component(ItemComponent.HYDRO_BLADES, 1)));
        }
        if (BoatRecipeRules.technicalRecipe(mode, !motors.isEmpty()))
        {
            for (Object motor : motors)
            {
                GameRegistry.addRecipe(new ShapelessOreRecipe(component(ItemComponent.MOTORIZED_BOAT, 1),
                        component(ItemComponent.INFLATABLE_BOAT, 1), motor, component(ItemComponent.HYDRO_BLADES, 1)));
            }
        }
        LogHelper.info("Inflatable boat recipes: mode " + mode + ", rubber " + rubbers + ", " + motors.size() + " electric motor(s) found.");
    }

    public static void conditionalInit()
    {
        if (ConfigHandler.INFLATABLEBOAT_ENABLED) boatRecipes();

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
            if (ConfigHandler.COPTERPACK_ENABLED) GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.copterPack),
                    "WBW",
                    "TEI",
                    "CDI",
                    'W', "plankWood",
                    'B', new ItemStack(ModItems.component, 1, 6),
                    'T', new ItemStack(ModItems.component, 1, 2),
                    'E', new ItemStack(ModItems.component, 1, 5),
                    'C', new ItemStack(Items.dye, 1, 2),
                    'D', Items.diamond,
                    'I', Items.iron_ingot
            ));
        }

        // Railcraft's firebox and low pressure boiler tank when Railcraft is present, a furnace and a cauldron otherwise
        if (ConfigHandler.STEAMJETPACK_ENABLED && ModFluids.steam != null)
        {
            try
            {
                ItemStack firebox = null;
                ItemStack boiler = null;
                if (ConfigHandler.IS_RAILCRAFT)
                {
                    firebox = GameRegistry.findItemStack("Railcraft", "machine.beta.boiler.firebox.solid", 1);
                    boiler = GameRegistry.findItemStack("Railcraft", "machine.beta.boiler.tank.pressure.low", 1);
                }
                if (firebox == null || boiler == null)
                {
                    firebox = new ItemStack(Blocks.furnace);
                    boiler = new ItemStack(Items.cauldron);
                }
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.steamJetpack),
                        "BWT",
                        "GIG",
                        "xWC",
                        'W', "plankWood",
                        'G', "ingotGold",
                        'I', "ingotIron",
                        'B', boiler,
                        'x', firebox,
                        'C', new ItemStack(Items.dye, 1, 2),
                        'T', new ItemStack(ModItems.component, 1, 2)
                ));
            } catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
}
