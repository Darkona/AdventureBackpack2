package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.reference.BackpackNames;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraftforge.common.ChestGenHooks;

import java.util.Random;

/**
 * Created on 24/12/2014
 *
 * @author Darkona
 */
public class ModWorldGen
{
    public static void init()
    {

        //Dungeon Generation

        for (int i = 0; i < BackpackNames.backpackNames.length; i++)
        {
            if (BackpackNames.backpackNames[i].equals("IronGolem"))
            {
                ItemStack backpack = BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), i);
                ChestGenHooks.addItem(ChestGenHooks.VILLAGE_BLACKSMITH, new WeightedRandomChestContent(backpack, 1, 1, 2));
            } else if (BackpackNames.backpackNames[i].equals("Bat"))
            {
                ItemStack backpack = BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), i);
                ChestGenHooks.addItem(ChestGenHooks.DUNGEON_CHEST, new WeightedRandomChestContent(backpack, 1, 1, 2));
                ChestGenHooks.addItem(ChestGenHooks.MINESHAFT_CORRIDOR, new WeightedRandomChestContent(backpack, 1, 1, 12));
            } else if (BackpackNames.backpackNames[i].equals("Pigman") && ConfigHandler.PIGMAN_ALLOWED)
            {
                ItemStack backpack = BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), i);
                ChestGenHooks.addItem(ChestGenHooks.PYRAMID_DESERT_CHEST, new WeightedRandomChestContent(backpack, 1, 1, 12));
                VillagerRegistry.instance().registerVillageTradeHandler(i, new ModWorldGen.TradeHandler(backpack));
            } else if (BackpackNames.backpackNames[i].equals("Villager"))
            {
                ItemStack backpack = BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), i);
                VillagerRegistry.instance().registerVillageTradeHandler(1, new ModWorldGen.TradeHandler(backpack));
                VillagerRegistry.instance().registerVillageTradeHandler(2, new ModWorldGen.TradeHandler(backpack));
                VillagerRegistry.instance().registerVillageTradeHandler(3, new ModWorldGen.TradeHandler(backpack));
            } else if (BackpackNames.backpackNames[i].equals("Standard") && ConfigHandler.BONUS_CHEST_ALLOWED)
            {
                ItemStack backpack = BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), i);
                ChestGenHooks.addItem(ChestGenHooks.BONUS_CHEST, new WeightedRandomChestContent(backpack, 0, 1, 5));
            }
        }

        //Villager Trade


    }

    public static class TradeHandler implements VillagerRegistry.IVillageTradeHandler
    {

        ItemStack backpack;

        TradeHandler(ItemStack backpack)
        {
            this.backpack = backpack;
        }

        /**
         * Called to allow changing the content of the {@link net.minecraft.village.MerchantRecipeList} for the villager
         * supplied during creation
         *
         * @param villager
         * @param recipeList
         * @param random
         */
        @Override
        public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random)
        {
            //0 Farmer, 1 Librarian, 2Priest, 3 Blacksmith, 4 Butcher
            if (villager.getProfession() == 1 || villager.getProfession() == 2)
            {
                ItemStack payment = BackpackNames.setBackpackColorNameFromDamage(new ItemStack(ModItems.adventureBackpack), 0);
                recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 10), payment, this.backpack));
            }
            if (villager.getProfession() == 3)
            {
                ItemStack payment = new ItemStack(ModItems.adventureBackpack);
                BackpackNames.setBackpackColorName(payment, "IronGolem");
                recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 10), payment, this.backpack));
            }
        }
    }

}
