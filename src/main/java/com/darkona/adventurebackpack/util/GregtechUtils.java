package com.darkona.adventurebackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Created on 30.01.2018
 *
 * @author Ugachaga
 */
public class GregtechUtils
{
    private static final String CLASS_RENDERER = "gregtech.common.render.GT_MetaGenerated_Tool_Renderer";
    private static final String METHOD_RENDERER = "renderItem";
    private static final Object[] EMPTY_OBJECT = {};

    private static final String TOOLS_NAME = "gt.metatool.01";
    private static final int[] ROTATED_TOOLS = {10, 14, 18, 22, 34, 150, 160};

    private static Object toolRenderer;

    private GregtechUtils(){}

    static
    {
        if (ConfigHandler.IS_GREGTECH)
        {
            try
            {
                toolRenderer = Class.forName(CLASS_RENDERER).newInstance();
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting instance of Gregtech: " + e.getMessage());
            }
        }
    }

    public static boolean isTool(ItemStack stack)
    {
        return ConfigHandler.IS_GREGTECH && stack.getItem().getUnlocalizedName().equals(TOOLS_NAME);
    }

    public static float getToolRotationAngle(ItemStack stack, boolean isLowerSlot)
    {
        int meta = stack.getItemDamage();
        for (int rotatedTool : ROTATED_TOOLS)
        {
            if (meta == rotatedTool)
                return isLowerSlot ? 45F : 135F;
        }
        return isLowerSlot ? -45F : 45F;
    }

    public static void renderTool(ItemStack stack, IItemRenderer.ItemRenderType renderType)
    {
        try
        {
            Class.forName(CLASS_RENDERER)
                    .getMethod(METHOD_RENDERER, IItemRenderer.ItemRenderType.class, ItemStack.class, Object[].class)
                    .invoke(toolRenderer, renderType, stack, EMPTY_OBJECT);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }
}
