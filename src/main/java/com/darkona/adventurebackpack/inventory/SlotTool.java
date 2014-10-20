package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.item.ItemHose;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class SlotTool extends Slot
{

    public SlotTool(IInventory inventory, int id, int x, int y)
    {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return isValidTool(stack);
    }

    public static boolean isValidTool(ItemStack stack)
    {

        boolean valid = false;

        String[] validToolNames = {
                "wrench", "hammer", "axe", "shovel", "grafter", "scoop", "crowbar"
        };

        String[] invalidToolNames = {
                "bucket", "sword", "dagger"
        };

        if (stack != null && stack.getMaxStackSize() == 1)
        {
            Item item = stack.getItem();
            String name = item.getUnlocalizedName().toLowerCase();

            // Vanilla
            if (item instanceof ItemTool || item instanceof ItemHoe)
            {
                return true;
            }

            //Adventure Backpack duh!
            if (item instanceof ItemHose)
            {
                return false;
            }

            // Just for extra compatibility and/or security and/or less annoyance
            for (String toolName : validToolNames)
            {
                String a = toolName;
                if (name.contains(toolName)) return true;
            }

            for (String toolName : invalidToolNames)
            {
                String a = toolName;
                if (name.contains(toolName)) return false;
            }

            //And also this because I'm a badass
            try
            {
                // Tinker's Construct
                if (java.lang.Class.forName("tconstruct.library.tools.HarvestTool").isInstance(item)) return true;

                //Buildcraft
                if (java.lang.Class.forName("buildcraft.api.tools.IToolWrench").isInstance(item)) return true;

                //IndustrialCraft
                if (java.lang.Class.forName("ic2.api.item.IElectricItem").isInstance(item)) return true;

                //Thaumcraft


            } catch (Exception oops)
            {

            } finally
            {
                return valid;
            }

        }

        return valid;
    }
}
