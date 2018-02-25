package com.darkona.adventurebackpack.util;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import com.darkona.adventurebackpack.reference.LoadedMods;

/**
 * Created on 30.01.2018
 *
 * @author Ugachaga
 */
public final class GregtechUtils
{
    private static final String CLASS_RENDERER = "gregtech.common.render.GT_MetaGenerated_Tool_Renderer";

    private static final String TOOLS_NAME = "gt.metatool.01";
    private static final int[] ROTATED_45_TOOLS = {16, 26, 30, 130};
    private static final int[] ROTATED_90_TOOLS = {10, 14, 18, 22, 34, 150, 160};

    private static Class<?> toolRenderer;
    private static Object toolRendererInstance;

    private GregtechUtils() {}

    static
    {
        if (LoadedMods.GREGTECH)
        {
            createToolRendererInstance();
        }
    }

    private static void createToolRendererInstance()
    {
        if (Utils.inClient())
        {
            try
            {
                toolRenderer = Class.forName(CLASS_RENDERER);
                toolRendererInstance = toolRenderer.newInstance();
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting instance of Gregtech Tools Renderer: " + e);
            }
        }
    }

    public static boolean isTool(@Nonnull ItemStack stack)
    {
        return LoadedMods.GREGTECH && stack.getItem().getUnlocalizedName().equals(TOOLS_NAME);
    }

    public static boolean isTool(String itemName)
    {
        return LoadedMods.GREGTECH && itemName.equals(TOOLS_NAME);
    }

    public static float getToolRotationAngle(ItemStack stack, boolean isLowerSlot)
    {
        int meta = stack.getItemDamage();

        for (int rotated45 : ROTATED_45_TOOLS)
            if (meta == rotated45)
                return isLowerSlot ? 0F : 90F;

        for (int rotated90 : ROTATED_90_TOOLS)
            if (meta == rotated90)
                return isLowerSlot ? 45F : 135F;

        return isLowerSlot ? -45F : 45F;
    }

    public static void renderTool(ItemStack stack, IItemRenderer.ItemRenderType renderType)
    {
        ToolRenderHelper.render(stack, renderType, toolRenderer, toolRendererInstance);
    }
}
