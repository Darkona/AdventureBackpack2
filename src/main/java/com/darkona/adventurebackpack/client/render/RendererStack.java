package com.darkona.adventurebackpack.client.render;

import java.lang.reflect.Method;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.config.ConfigHandler;

public class RendererStack extends ModelRenderer
{
    public ItemStack stack;
    private boolean which;

    public RendererStack(ModelBase modelBase, boolean which)
    {
        super(modelBase);
        this.which = which;
        addChild(new Thing(modelBase));
    }

    private class Thing extends ModelRenderer
    {
        public Thing(ModelBase modelBase)
        {
            super(modelBase);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(float par1)
        {
            if (stack == null)
            {
                return;
            }

            boolean isGregtechTool = stack.getItem().getUnlocalizedName().equals("gt.metatool.01");
            int meta = stack.getItemDamage();

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (which)
            {
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glPushMatrix();
                GL11.glRotatef(-90F, 0, 1, 0);
                GL11.glRotatef(isGregtechTool ? getGregtechToolRotationAngle(meta, which) : -225F, 0, 0, 1);
            }
            else
            {
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                GL11.glPushMatrix();
                GL11.glRotatef(isGregtechTool ? getGregtechToolRotationAngle(meta, which) : 45F, 0, 0, 1);
            }

            if (isGregtechTool)
                renderGregtechTool(stack);
            else
                CopygirlRenderUtils.renderItemIn3d(stack);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private static Object gregtechToolRenderer;
    private static Object[] emptyObjectArray = {};
    private static int[] gregtechTools90Degree = {10, 14, 18, 22, 34, 150, 160};

    private static float getGregtechToolRotationAngle(int meta, boolean which)
    {
        for (int tool90 : gregtechTools90Degree)
        {
            if (meta == tool90)
                return which? 45F : 135F;
        }
        return which ? -45F : 45F;
    }

    private static void renderGregtechTool(ItemStack stack)
    {
        if (!ConfigHandler.IS_GREGTECH)
            return;

        try
        {
            Class<?> clazz = Class.forName("gregtech.common.render.GT_MetaGenerated_Tool_Renderer");
            if (gregtechToolRenderer == null)
                gregtechToolRenderer = clazz.newInstance();
            Method doRender = clazz.getMethod("renderItem", IItemRenderer.ItemRenderType.class, ItemStack.class, Object[].class);
            doRender.invoke(gregtechToolRenderer, IItemRenderer.ItemRenderType.ENTITY, stack, emptyObjectArray);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
    }
}