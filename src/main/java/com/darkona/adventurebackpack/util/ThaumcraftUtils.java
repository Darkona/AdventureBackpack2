package com.darkona.adventurebackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import com.darkona.adventurebackpack.config.ConfigHandler;

/**
 * Created on 06.02.2018
 *
 * @author Ugachaga
 */
public class ThaumcraftUtils
{
    private static final String CLASS_RENDERER = "thaumcraft.client.renderers.item.ItemWandRenderer";
    private static final String METHOD_RENDERER = "renderItem";
    private static final Object[] EMPTY_OBJECT = {};

    private static final String CLASS_WANDS = "thaumcraft.common.items.wands.ItemWandCasting";

    private static Object toolRendererInstance;

    private ThaumcraftUtils() {}

    static
    {
        if (ConfigHandler.IS_THAUMCRAFT && Utils.inClient())
        {
            try
            {
                toolRendererInstance = Class.forName(CLASS_RENDERER).newInstance();
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting Thaumcraft Wands Renderer instance: " + e);
            }
        }
    }

    public static boolean isTool(ItemStack stack)
    {
        if (!ConfigHandler.IS_THAUMCRAFT || stack == null)
            return false;

        try
        {
            return java.lang.Class.forName(CLASS_WANDS).isInstance(stack.getItem());
        }
        catch (ClassNotFoundException e)
        {
            return false;
        }
    }

    public static float getToolRotationAngle(ItemStack stack, boolean isLowerSlot)
    {
        return isLowerSlot ? 0F : 90F;
    }

    public static void renderTool(ItemStack stack, IItemRenderer.ItemRenderType renderType)
    {
        try
        {
            Class.forName(CLASS_RENDERER)
                    .getMethod(METHOD_RENDERER, IItemRenderer.ItemRenderType.class, ItemStack.class, Object[].class)
                    .invoke(toolRendererInstance, renderType, stack, EMPTY_OBJECT);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }
}
