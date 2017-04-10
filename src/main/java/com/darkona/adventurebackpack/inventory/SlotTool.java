package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class SlotTool extends SlotAdventureBackpack
{
    SlotTool(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return isValidTool(stack);
    }

    private static final String[] VALID_TOOL_NAMES = {"wrench", "hammer", "axe", "shovel", "grafter", "scoop", "crowbar", "mattock", "drill",};

    private static final String[] INVALID_TOOL_NAMES = {"bucket", "sword", "dagger", "sabre", "rapier", "shield", "cutlass", "bow", "whip", "disassembler", "robit"};

    public static boolean isValidTool(ItemStack stack)
    {
        if (stack != null && stack.getMaxStackSize() == 1)
        {
            Item item = stack.getItem();
            String name = item.getUnlocalizedName().toLowerCase();
            String nameObj = Item.itemRegistry.getNameForObject(item);
            String clazz = item.getClass().getName();

            // Vanilla
            if (item instanceof ItemTool || item instanceof ItemHoe || item instanceof ItemShears || item instanceof ItemFishingRod || item instanceof ItemFlintAndSteel)
            {
                return true;
            }

            //Adventure Backpack duh!
            if (item instanceof ItemHose || item instanceof ItemAdventureBackpack)
            {
                return false;
            }

            //GregTech
            if (name.equals("gt.metatool.01"))
            {
                //0 = sword, 170 = turbines
                return !(stack.getItemDamage() == 0 || stack.getItemDamage() > 169);
            }
            //Charged baterries and such
            if (name.startsWith("gt.metaitem")) return false;

            //Ender IO
            //Yeta Wrench uses shift+Scroll for switch own modes
            if (nameObj.equals("EnderIO:itemYetaWrench")) return false;

            //Extra Utilities
            if (clazz.equals("com.rwtema.extrautils.item.ItemBuildersWand")) return true;

            // Better builders Wands
            if (nameObj.startsWith("betterbuilderswands:wand")) return true;

            // Just for extra compatibility and/or security and/or less annoyance
            for (String toolName : VALID_TOOL_NAMES)
            {
                if (name.contains(toolName)) return true;
            }

            for (String toolName : INVALID_TOOL_NAMES)
            {
                if (name.contains(toolName)) return false;
            }

            //And also this because I'm a badass
            try
            {
                // Tinker's Construct
                if (clazz.contains("tconstruct.items.tools")) return true;
            } catch (Exception oops)
            {
                //  oops.printStackTrace();
            }
            try
            {
                // Mekanism
                if (clazz.contains("mekanism.common.item")) return true;
            } catch (Exception oops)
            {
                //  oops.printStackTrace();
            }
            try
            {
                //Buildcraft
                if (java.lang.Class.forName("buildcraft.api.tools.IToolWrench").isInstance(item)) return true;
            } catch (Exception oops)
            {
                //  oops.printStackTrace();
            }
            try
            {
                //IndustrialCraft
                if (java.lang.Class.forName("ic2.api.item.IElectricItem").isInstance(item)) return true;
            } catch (Exception oops)
            {
                //  oops.printStackTrace();
            }
            try
            {
                //Thaumcraft
                if (java.lang.Class.forName("thaumcraft.common.items.wands.ItemWandCasting").isInstance(item))
                    return true;
            } catch (Exception oops)
            {
                //  oops.printStackTrace();
            }
            try
            {
                //Thermal Expansion
                if (java.lang.Class.forName("cofh.core.item.tool").isInstance(item)) return true;
                if (java.lang.Class.forName("thermalexpansion.item.tool").isInstance(item)) return true;
            } catch (Exception oops)
            {
                // oops.printStackTrace();
            }
        }
        return false;
    }
}
