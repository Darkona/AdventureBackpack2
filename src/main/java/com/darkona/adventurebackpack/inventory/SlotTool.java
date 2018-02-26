package com.darkona.adventurebackpack.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemFlintAndSteel;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;

import com.darkona.adventurebackpack.util.GregtechUtils;
import com.darkona.adventurebackpack.util.ThaumcraftUtils;
import com.darkona.adventurebackpack.util.TinkersUtils;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class SlotTool extends SlotAdventure
{
    private static final String[] VALID_TOOL_NAMES = {"axe", "crowbar", "drill", "grafter", "hammer", "scoop", "shovel",
            "wrench",};
    private static final String[] INVALID_TOOL_NAMES = {"bow", "bucket", "shield", "sword",};
    private static final String[] INVALID_TINKER_NAMES = {"battleaxe", "bow", "cleaver", "cutlass", "dagger", "rapier",
            "sabre", "shield", "sign", "sword",};

    SlotTool(IInventory inventory, int slotIndex, int posX, int posY)
    {
        super(inventory, slotIndex, posX, posY);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return isValidTool(stack);
    }

    public static boolean isValidTool(ItemStack stack)
    {
        if (stack == null || stack.getMaxStackSize() != 1)
            return false;

        Item item = stack.getItem();
        String clazzName = item.getClass().getName();
        String objectName = Item.itemRegistry.getNameForObject(item);
        String itemName = item.getUnlocalizedName().toLowerCase();

        // Vanilla
        if (item instanceof ItemTool || item instanceof ItemHoe || item instanceof ItemShears
                || item instanceof ItemFishingRod || item instanceof ItemFlintAndSteel)
        {
            return true;
        }

        // GregTech
        if (GregtechUtils.isTool(itemName))
        {
            int meta = stack.getItemDamage();
            return !(meta == 0 || meta == 24 || meta > 169); // 0 = sword, 24 = mortar, 170+ = turbines
        }
        if (itemName.startsWith("gt.metaitem")) return false; // charged baterries and such

        // Tinkers Construct
        if (TinkersUtils.isTool(clazzName))
        {
            for (String toolName : INVALID_TINKER_NAMES)
                if (itemName.contains(toolName)) return false;
            return true;
        }

        // Thaumcraft
        if (ThaumcraftUtils.isTool(stack)) return true;

        // Ender IO
        // Yeta Wrench uses Shift+Scroll for switch own modes
        if (objectName.equals("EnderIO:itemYetaWrench")) return false;

        // Extra Utilities
        if (clazzName.equals("com.rwtema.extrautils.item.ItemBuildersWand")) return true;

        // Better builders Wands
        if (objectName.startsWith("betterbuilderswands:wand")) return true;

        // Just for extra compatibility and/or security and/or less annoyance
        for (String toolName : INVALID_TOOL_NAMES)
            if (itemName.contains(toolName)) return false;

        for (String toolName : VALID_TOOL_NAMES)
            if (itemName.contains(toolName)) return true;

        // Mekanism
        if (clazzName.startsWith("mekanism.common.item"))
            return !itemName.contains("disassembler") && !itemName.contains("robit");

        // And also this because I'm a badass
        try
        {
            // Buildcraft
            if (java.lang.Class.forName("buildcraft.api.tools.IToolWrench").isInstance(item)) return true;
        }
        catch (ClassNotFoundException e)
        { /* */ }
        try
        {
            // IndustrialCraft
            if (java.lang.Class.forName("ic2.api.item.IElectricItem").isInstance(item)) return true;
        }
        catch (ClassNotFoundException e)
        { /* */ }
        try
        {
            // Thermal Expansion
            if (java.lang.Class.forName("cofh.core.item.tool").isInstance(item)) return true;
            if (java.lang.Class.forName("thermalexpansion.item.tool").isInstance(item)) return true;
        }
        catch (ClassNotFoundException e)
        { /* */ }

        return false;
    }
}
