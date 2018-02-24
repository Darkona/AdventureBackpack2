package com.darkona.adventurebackpack.util;

import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import com.darkona.adventurebackpack.reference.LoadedMods;

/**
 * Created on 06.02.2018
 *
 * @author Ugachaga
 */
public final class ThaumcraftUtils
{
    public static final boolean DIAL_BOTTOM = setDialBottom();

    private static final String CLASS_RENDERER = "thaumcraft.client.renderers.item.ItemWandRenderer";
    private static final String CLASS_CONFIG = "thaumcraft.common.config.Config";
    private static final String CLASS_WANDS = "thaumcraft.common.items.wands.ItemWandCasting";
    private static final String METHOD_RENDERER = "renderItem";
    private static final String FIELD_DIAL_BOTTOM = "dialBottom";
    private static final Object[] EMPTY_OBJECT = {};

    private static Object toolRendererInstance;

    private ThaumcraftUtils() {}

    static
    {
        if (LoadedMods.THAUMCRAFT)
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
                toolRendererInstance = Class.forName(CLASS_RENDERER).newInstance();
            }
            catch (Exception e)
            {
                LogHelper.error("Error getting instance of Thaumcraft Wands Renderer: " + e);
            }
        }
    }

    private static boolean setDialBottom()
    {
        if (!LoadedMods.THAUMCRAFT || Utils.inServer())
            return false;

        try
        {
            return Class.forName(CLASS_CONFIG).getField(FIELD_DIAL_BOTTOM).getBoolean(null);
        }
        catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
        {
            return false;
        }
    }

    public static boolean isTool(ItemStack stack)
    {
        if (!LoadedMods.THAUMCRAFT || stack == null)
            return false;

        try
        {
            return Class.forName(CLASS_WANDS).isInstance(stack.getItem());
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
        if (toolRendererInstance == null)
            return;

        try
        {
            Class.forName(CLASS_RENDERER)
                    .getMethod(METHOD_RENDERER, IItemRenderer.ItemRenderType.class, ItemStack.class, Object[].class)
                    .invoke(toolRendererInstance, renderType, stack, EMPTY_OBJECT);
        }
        catch (Exception e)
        { /*  */ }
    }
}
