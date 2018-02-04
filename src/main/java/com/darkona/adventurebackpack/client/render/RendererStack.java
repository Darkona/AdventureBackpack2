package com.darkona.adventurebackpack.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import com.darkona.adventurebackpack.util.GregtechUtils;
import com.darkona.adventurebackpack.util.TinkersUtils;

public class RendererStack extends ModelRenderer
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    public ItemStack stack;
    private boolean isLowerSlot;

    public RendererStack(ModelBase modelBase, boolean isLowerSlot)
    {
        super(modelBase);
        this.isLowerSlot = isLowerSlot;
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
                return;


            boolean isGregtechTool = GregtechUtils.isTool(stack);
            boolean isTinkersTool = TinkersUtils.isTool(stack);

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (isLowerSlot)
            {
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glPushMatrix();
                GL11.glRotatef(-90F, 0, 1, 0);

                if (isGregtechTool || isTinkersTool)
                    GL11.glRotatef(getToolRotationAngle(stack, true, isGregtechTool), 0, 0, 1);
                else
                    GL11.glRotatef(-225F, 0, 0, 1);
            }
            else
            {
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                GL11.glPushMatrix();
                if (isGregtechTool || isTinkersTool)
                    GL11.glRotatef(getToolRotationAngle(stack, false, isGregtechTool), 0, 0, 1);
                else
                    GL11.glRotatef(45F, 0, 0, 1);
            }

            if (isGregtechTool)
                GregtechUtils.renderTool(stack, IItemRenderer.ItemRenderType.ENTITY);
            else if (isTinkersTool)
            {
                TextureManager tm = MC.getTextureManager();
                tm.bindTexture(tm.getResourceLocation(stack.getItemSpriteNumber()));
                TinkersUtils.renderTool(stack, IItemRenderer.ItemRenderType.ENTITY);
            }
            else
                CopygirlRenderUtils.renderItemIn3d(stack);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }

        private float getToolRotationAngle(ItemStack stack, boolean isLowerSlot, boolean isGregtech)
        {
            return isGregtech
                   ? GregtechUtils.getToolRotationAngle(stack, isLowerSlot)
                   : TinkersUtils.getToolRotationAngle(stack, isLowerSlot);
        }
    }
}