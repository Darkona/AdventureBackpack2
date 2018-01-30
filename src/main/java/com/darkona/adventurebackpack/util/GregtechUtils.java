package com.darkona.adventurebackpack.util;

import java.lang.reflect.Method;

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
    private static final int[] ROTATED_TOOLS = {10, 14, 18, 22, 34, 150, 160};
    private static final Object[] EMPTY_OBJECT = {};

    private static Object toolRenderer;

    private GregtechUtils(){}

    public static boolean isTool(ItemStack stack)
    {
        return stack.getItem().getUnlocalizedName().equals("gt.metatool.01");
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
        if (!ConfigHandler.IS_GREGTECH)
            return;

        try
        {
            Class<?> clazz = Class.forName("gregtech.common.render.GT_MetaGenerated_Tool_Renderer");
            if (toolRenderer == null)
                toolRenderer = clazz.newInstance();
            Method doRender = clazz.getMethod("renderItem", IItemRenderer.ItemRenderType.class, ItemStack.class, Object[].class);
            doRender.invoke(toolRenderer, renderType, stack, EMPTY_OBJECT);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }
}
