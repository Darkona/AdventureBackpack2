package com.darkona.adventurebackpack.inventory;

import com.darkona.adventurebackpack.common.ToolCycleFilter;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;

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

    public static boolean isListed(Item item)
    {
        return ConfigHandler.TOOL_CYCLE_FILTER.listed(Item.itemRegistry.getNameForObject(item));
    }

    public static boolean isBlacklisted(ItemStack stack)
    {
        return stack != null && stack.getItem() != null && ConfigHandler.TOOL_CYCLE_LIST_MODE == ToolCycleFilter.Mode.BLACKLIST && isListed(stack.getItem());
    }

    private static final String[] VALID_TOOL_NAMES = {
            "wrench", "hammer", "axe", "shovel", "grafter", "scoop", "crowbar", "mattock", "drill",
    };

    private static final String[] INVALID_TOOL_NAMES = {
            "bucket", "sword", "dagger", "sabre", "rapier", "shield", "cutlass", "bow", "whip"
    };

    private static final class ModTools
    {
        static final Class<?> BUILDCRAFT_WRENCH = find("buildcraft.api.tools.IToolWrench");
        static final Class<?> IC2_ELECTRIC = find("ic2.api.item.IElectricItem");
        static final Class<?> COFH_TOOL = find("cofh.core.item.tool");
        static final Class<?> THERMAL_TOOL = find("thermalexpansion.item.tool");

        private static Class<?> find(String name)
        {
            try
            {
                return Class.forName(name);
            } catch (Throwable absent)
            {
                return null;
            }
        }
    }

    private static boolean isBuiltInTool(ItemStack stack)
    {
        if (stack == null || stack.getMaxStackSize() != 1) return false;
        Item item = stack.getItem();

        if (item instanceof ItemTool || item instanceof ItemHoe || item instanceof ItemShears || item instanceof ItemFishingRod || item instanceof ItemFlintAndSteel)
        {
            return true;
        }

        if (item instanceof ItemHose || item instanceof ItemAdventureBackpack)
        {
            return false;
        }

        String name = item.getUnlocalizedName().toLowerCase();
        for (String toolName : VALID_TOOL_NAMES)
        {
            if (name.contains(toolName)) return true;
        }
        for (String toolName : INVALID_TOOL_NAMES)
        {
            if (name.contains(toolName)) return false;
        }

        if (item.getClass().getName().contains("tconstruct.items.tools")) return true;
        if (ModTools.BUILDCRAFT_WRENCH != null && ModTools.BUILDCRAFT_WRENCH.isInstance(item)) return true;
        if (ModTools.IC2_ELECTRIC != null && ModTools.IC2_ELECTRIC.isInstance(item)) return true;
        if (ModTools.COFH_TOOL != null)
        {
            if (ModTools.COFH_TOOL.isInstance(item)) return true;
            if (ModTools.THERMAL_TOOL != null && ModTools.THERMAL_TOOL.isInstance(item)) return true;
        }
        return false;
    }
}
