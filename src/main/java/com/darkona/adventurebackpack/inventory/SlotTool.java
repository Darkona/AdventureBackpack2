package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.item.ItemHose;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

/**
 * Created by Darkona on 12/10/2014.
 */
public class SlotTool extends Slot {

    public SlotTool(IInventory inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return isValidTool(stack);
    }

    public static boolean isValidTool(ItemStack stack) {

        boolean valid = false;

        String[] validToolNames = {
                "wrench", "hammer", "axe", "shovel", "grafter", "scoop"
        };

        String[] invalidToolNames = {
                "bucket"
        };

        if (stack != null && stack.getMaxStackSize() == 1) {
            Item item = stack.getItem();
            String name = item.getUnlocalizedName().toLowerCase();

            // Vanilla
            if (item instanceof ItemTool || item instanceof ItemHoe) {
                return true;
            }

            //Adventure Backpack duh!
            if (item instanceof ItemHose)
                return false;


           /*  // BuildCraft
            if (item instanceof IToolWrench)
            {
                return true;
            }
            // IndustrialCraft
            if (item instanceof ISpecialElectricItem || item instanceof ICustomElectricItem)
            {
                return true;
            }
            // Railcraft
            if (item instanceof IToolCrowbar)
            {
                return true;
            }
            // Forestry
            if (item instanceof IToolScoop)
            {
                return true;
            }*/

            // Just for extra compatibility and/or security and/or less annoyance
            for (String toolName : validToolNames) {
                String a = toolName;
                if (name.contains(toolName)) return true;
            }

            for (String toolName : invalidToolNames) {
                String a = toolName;
                if (name.contains(toolName)) return false;
            }
            try {
                // Tinker's Construct
                if (java.lang.Class.forName("tconstruct.library.tools.ToolCore").isInstance(item)) {
                    return true;
                }
                if (java.lang.Class.forName(" buildcraft.api.tools.IToolWrench").isInstance(item)) {
                    return true;
                }


            } catch (Exception oops) {
            }

        }

        return valid;
    }
}
