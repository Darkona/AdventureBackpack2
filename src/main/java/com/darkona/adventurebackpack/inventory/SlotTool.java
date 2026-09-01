package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.ToolCycleFilter;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class SlotTool extends SlotAdventureBackpack
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

    /**
     * True when the stack may go into the backpack's tool slots, by hand or by cycling. Never a stackable item, a hose
     * or a backpack; past that, tweaks.toolcycle decides between the built-in rules and the config list (#49).
     */
    public static boolean isValidTool(ItemStack stack)
    {
        if (stack == null || stack.getItem() == null || stack.getMaxStackSize() != 1) return false;
        Item item = stack.getItem();
        if (item instanceof ItemHose || item instanceof ItemAdventureBackpack) return false;
        ToolCycleFilter.Mode mode = ConfigHandler.TOOL_CYCLE_LIST_MODE;
        if (mode == ToolCycleFilter.Mode.NONE) return isBuiltInTool(stack);
        boolean listed = isListed(item);
        return ToolCycleFilter.allows(mode, listed, mode == ToolCycleFilter.Mode.BLACKLIST && !listed && isBuiltInTool(stack));
    }

    /** True when the item is on the tweaks.toolcycle.list (whatever the mode). */
    public static boolean isListed(Item item)
    {
        return ConfigHandler.TOOL_CYCLE_FILTER.listed(Item.itemRegistry.getNameForObject(item));
    }

    /** True when the item is blacklisted: the Skeleton's bow obeys the blacklist too. */
    public static boolean isBlacklisted(ItemStack stack)
    {
        return stack != null && stack.getItem() != null && ConfigHandler.TOOL_CYCLE_LIST_MODE == ToolCycleFilter.Mode.BLACKLIST && isListed(stack.getItem());
    }

    /** The original rules: vanilla tools, known tool names, and tools of a few mods. */
    private static boolean isBuiltInTool(ItemStack stack)
    {
        boolean valid = false;

        String[] validToolNames = {
                "wrench", "hammer", "axe", "shovel", "grafter", "scoop", "crowbar", "mattock", "drill",/*"hatchet","excavator","chisel"*/
        };

        String[] invalidToolNames = {
                "bucket", "sword", "dagger", "sabre", "rapier", "shield", "cutlass", "bow", "whip"
        };

        if (stack != null && stack.getMaxStackSize() == 1)
        {
            Item item = stack.getItem();
            String name = item.getUnlocalizedName().toLowerCase();

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
                if (item.getClass().getName().contains("tconstruct.items.tools")) return true;
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
            //Thaumcraft
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

        return valid;
    }


}
