package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.common.BoatRecipeRules;
import com.darkona.adventurebackpack.compat.tech.TechParts;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.init.recipes.BackpackRecipesList;
import com.darkona.adventurebackpack.init.recipes.BackpackUpgradeRecipe;
import com.darkona.adventurebackpack.init.recipes.CrossbowReloadRecipe;
import com.darkona.adventurebackpack.common.MagazineLogic;
import com.darkona.adventurebackpack.item.ItemBoltCartridge;
import com.darkona.adventurebackpack.inventory.AirContainers;
import com.darkona.adventurebackpack.item.ItemComponent;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.item.ItemFlamethrowerPack;
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

public class ModRecipes
{
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
        GameRegistry.addRecipe(new ShapedOreRecipe(ItemBoltCartridge.create(0, null),
                " T ",
                "XIX",
                'T', Items.string,
                'X', "stickWood",
                'I', "ingotIron"
        ));
        if (ConfigHandler.MAGAZINE_LEGACY_RECIPE)
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(ItemBoltCartridge.create(MagazineLogic.LEGACY_CHARGES, MagazineLogic.LEGACY_AMMO),
                    "AAA",
                    "XIX",
                    "AAA",
                    'A', Items.arrow,
                    'X', "stickWood",
                    'I', "ingotIron"
            ));
        }
        if (ConfigHandler.MAGAZINE_RELOAD_RECIPE)
        {
            GameRegistry.addRecipe(new CrossbowReloadRecipe());
            RecipeSorter.register(ModInfo.MOD_ID + ":crossbowReload", CrossbowReloadRecipe.class, RecipeSorter.Category.SHAPELESS, "after:forge:shapelessore");
        }
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.bolt, 4),
                "I",
                "X",
                "F",
                'I', "ingotIron",
                'X', "stickWood",
                'F', Items.feather
        ));
    }

    private static ItemStack bc(int damage)
    {
        return BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), damage);
    }

    public static void init()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.blockCampFire,
                " S ",
                "SxS",
                "ccc",
                'S', "stickWood",
                'x', Items.coal,
                'c', "cobblestone"
        ));

        GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 1),
                "  X",
                "CCC",
                'X', Blocks.wool,
                'C', Blocks.carpet
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 2),
                "GIG",
                "GGG",
                "GIG",
                'G', Blocks.glass,
                'I', Items.iron_ingot
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 3),
                " G ",
                "ILI",
                "   ",
                'G', Items.gold_ingot,
                'I', Items.iron_ingot,
                'L', Blocks.lever
        );

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
        GameRegistry.addRecipe(new ItemStack(ModItems.telescope),
                "EI ",
                "IDI",
                " IG",
                'E', Items.ender_pearl,
                'I', Items.iron_ingot,
                'D', Items.diamond,
                'G', Blocks.glass
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.rope, 4),
                "SSS",
                "S S",
                "SS|",
                'S', Items.string,
                '|', Items.stick
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.adventureJacket),
                "SIS",
                "LLL",
                "LLL",
                'S', Items.string,
                'I', Items.iron_ingot,
                'L', Items.leather
        );

        GameRegistry.addRecipe(new ItemStack(ModItems.adventurePants),
                "SIS",
                "L L",
                "L L",
                'S', Items.string,
                'I', Items.iron_ingot,
                'L', Items.leather
        );
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
                    if (field.getName().equals((BackpackNames.backpackNames[i])) && ConfigHandler.isBackpackTypeEnabled(BackpackNames.backpackNames[i]))
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

    private static void boatRecipes()
    {
        BoatRecipeRules.Mode mode = ConfigHandler.BOAT_RECIPE_MODE;
        List<String> rubbers = TechParts.presentOres(ConfigHandler.BOAT_RUBBER);
        List<Object> motors = TechParts.find(ConfigHandler.BOAT_MOTORS);

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

        GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.HYDRO_BLADES, 1),
                "I I",
                " S ",
                "I I",
                'I', "ingotIron",
                'S', "stickWood"));

        GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.INFLATABLE_BOAT, 1),
                "SNS",
                "C C",
                "CCC",
                'S', Items.string,
                'N', component(ItemComponent.HOSE_NOZZLE, 1),
                'C', component(ItemComponent.INFLATABLE_CHAMBER, 1)));

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

    private static void fuelStationRecipes()
    {
        BoatRecipeRules.Mode mode = ConfigHandler.FUELSTATION_RECIPE_MODE;
        List<String> plates = TechParts.presentOres(ConfigHandler.FUELSTATION_PLATES);
        if (BoatRecipeRules.vanillaRecipe(mode, !plates.isEmpty()))
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockFuelStation),
                    "IHI",
                    "GTG",
                    "III",
                    'I', "ingotIron",
                    'H', component(ItemComponent.HOSE_NOZZLE, 1),
                    'G', "paneGlass",
                    'T', component(ItemComponent.BACKPACK_TANK, 1)));
        }
        if (BoatRecipeRules.technicalRecipe(mode, !plates.isEmpty()))
        {
            for (String plate : plates)
            {
                GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockFuelStation),
                        "PHP",
                        "GTG",
                        "PPP",
                        'P', plate,
                        'H', component(ItemComponent.HOSE_NOZZLE, 1),
                        'G', "paneGlass",
                        'T', component(ItemComponent.BACKPACK_TANK, 1)));
            }
        }
        LogHelper.info("Fuel Station recipes: mode " + mode + ", plates " + plates + ".");
    }

    private static void scubaRecipes()
    {
        BoatRecipeRules.Mode mode = ConfigHandler.BOAT_RECIPE_MODE;
        List<String> rubbers = TechParts.presentOres(ConfigHandler.BOAT_RUBBER);

        GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.AIR_TANK, 1),
                "IHI",
                "I I",
                "III",
                'I', "ingotIron",
                'H', component(ItemComponent.HOSE_NOZZLE, 1)));

        if (BoatRecipeRules.vanillaRecipe(mode, !rubbers.isEmpty()))
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.DIVING_MASK, 1),
                    "LLL",
                    "LGL",
                    " H ",
                    'L', Items.leather,
                    'G', "paneGlass",
                    'H', component(ItemComponent.HOSE_NOZZLE, 1)));
        }
        if (BoatRecipeRules.technicalRecipe(mode, !rubbers.isEmpty()))
        {
            for (String rubber : rubbers)
            {
                GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.DIVING_MASK, 1),
                        "RRR",
                        "RGR",
                        " H ",
                        'R', rubber,
                        'G', "paneGlass",
                        'H', component(ItemComponent.HOSE_NOZZLE, 1)));
            }
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.scubaGear),
                " M ",
                "ASA",
                " L ",
                'M', component(ItemComponent.DIVING_MASK, 1),
                'A', component(ItemComponent.AIR_TANK, 1),
                'S', Items.string,
                'L', Items.leather));

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockAirCompressor),
                "IPI",
                "ITI",
                "IFI",
                'I', "ingotIron",
                'P', Blocks.piston,
                'T', component(2, 1),
                'F', Blocks.furnace));
    }

    private static void flamethrowerRecipes()
    {
        BoatRecipeRules.Mode rubberMode = ConfigHandler.BOAT_RECIPE_MODE;
        List<String> rubbers = TechParts.presentOres(ConfigHandler.BOAT_RUBBER);
        BoatRecipeRules.Mode plateMode = ConfigHandler.FLAMETHROWER_RECIPE_MODE;
        List<String> plates = TechParts.presentOres(ConfigHandler.FLAMETHROWER_PLATES);

        if (BoatRecipeRules.vanillaRecipe(rubberMode, !rubbers.isEmpty()))
        {
            GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.PYRO_MASK, 1),
                    "ILI",
                    "LGL",
                    "III",
                    'I', "ingotIron",
                    'L', Items.leather,
                    'G', "paneGlass"));
        }
        if (BoatRecipeRules.technicalRecipe(rubberMode, !rubbers.isEmpty()))
        {
            for (String rubber : rubbers)
            {
                GameRegistry.addRecipe(new ShapedOreRecipe(component(ItemComponent.PYRO_MASK, 1),
                        "IRI",
                        "RGR",
                        "III",
                        'I', "ingotIron",
                        'R', rubber,
                        'G', "paneGlass"));
            }
        }

        if (BoatRecipeRules.vanillaRecipe(plateMode, !plates.isEmpty()))
        {
            addFlamethrowerRecipes("ingotIron");
        }
        if (BoatRecipeRules.technicalRecipe(plateMode, !plates.isEmpty()))
        {
            for (String plate : plates)
            {
                addFlamethrowerRecipes(plate);
            }
        }
        LogHelper.info("Flamethrower recipes: mode " + plateMode + ", plates " + plates + ", rubber " + rubbers + ".");
    }

    private static void addFlamethrowerRecipes(String metal)
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.flamethrowerPack),
                " M ",
                "TLT",
                "IFI",
                'M', component(ItemComponent.PYRO_MASK, 1),
                'T', component(ItemComponent.BACKPACK_TANK, 1),
                'L', Items.leather,
                'I', metal,
                'F', Items.flint_and_steel));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.flamethrower),
                "IIH",
                "  F",
                'I', metal,
                'H', component(ItemComponent.HOSE_NOZZLE, 1),
                'F', Items.flint_and_steel));
    }

    private static void distilleryRecipe()
    {
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.blockDistillery),
                "IBI",
                "GCG",
                "IFI",
                'I', "ingotIron",
                'B', Items.bucket,
                'G', "paneGlass",
                'C', Items.cauldron,
                'F', Blocks.furnace));
    }

    public static void conditionalInit()
    {
        if (ConfigHandler.INFLATABLEBOAT_ENABLED) boatRecipes();
        if (ConfigHandler.FUELSTATION_ENABLED) fuelStationRecipes();
        if (AirContainers.usable()) scubaRecipes();
        if (ItemFlamethrowerPack.isUsable()) flamethrowerRecipes();
        if (ConfigHandler.DISTILLERY_ENABLED && ModFluids.ethanol != null) distilleryRecipe();

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

        if (ItemCopterPack.isUsable())
        {
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

            GameRegistry.addRecipe(new ItemStack(ModItems.component, 1, 6),
                    "III",
                    " F ",
                    " F ",
                    'I', Items.iron_ingot,
                    'F', Blocks.fence
            );

            GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.copterPack),
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
