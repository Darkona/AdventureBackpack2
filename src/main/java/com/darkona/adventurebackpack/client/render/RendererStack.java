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

import com.darkona.adventurebackpack.reference.ToolHandler;
import com.darkona.adventurebackpack.util.GregtechUtils;
import com.darkona.adventurebackpack.util.ThaumcraftUtils;
import com.darkona.adventurebackpack.util.TinkersUtils;

public class RendererStack extends ModelRenderer
{
    private static final Minecraft MC = Minecraft.getMinecraft();

    private boolean isLowerSlot;
    private ItemStack stack;
    private ToolHandler toolHandler = ToolHandler.VANILLA;

    public RendererStack(ModelBase modelBase, boolean isLowerSlot)
    {
        super(modelBase);
        this.isLowerSlot = isLowerSlot;
        addChild(new Thing(modelBase));
    }

    public void setStack(ItemStack stack, ToolHandler toolHandler)
    {
        this.stack = stack;
        this.toolHandler = toolHandler;
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

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (isLowerSlot)
            {
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glPushMatrix();
                GL11.glRotatef(-90F, 0, 1, 0);
            }
            else
            {
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                GL11.glPushMatrix();
            }
            GL11.glRotatef(getToolRotationAngle(stack, isLowerSlot, toolHandler), 0, 0, 1);

            switch (toolHandler)
            {
                case GREGTECH:
                    GregtechUtils.renderTool(stack, IItemRenderer.ItemRenderType.ENTITY);
                    break;
                case TCONSTRUCT:
                    TextureManager tm = MC.getTextureManager();
                    tm.bindTexture(tm.getResourceLocation(stack.getItemSpriteNumber()));
                    GL11.glTranslatef(-0.06F, -0.1F, 0F);
                    TinkersUtils.renderTool(stack, IItemRenderer.ItemRenderType.ENTITY);
                    break;
                case THAUMCRAFT:
                    GL11.glTranslatef(0F, -0.375F, 0F);
                    ThaumcraftUtils.renderTool(stack, IItemRenderer.ItemRenderType.ENTITY);
                    break;
                case VANILLA:
                default:
                    CopygirlRenderUtils.renderItemIn3d(stack);
                    break;
            }

            GL11.glPopAttrib();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }

        private float getToolRotationAngle(ItemStack stack, boolean isLowerSlot, ToolHandler toolHandler)
        {
            switch (toolHandler)
            {
                case GREGTECH:
                    return GregtechUtils.getToolRotationAngle(stack, isLowerSlot);
                case TCONSTRUCT:
                    return TinkersUtils.getToolRotationAngle(stack, isLowerSlot);
                case THAUMCRAFT:
                    return ThaumcraftUtils.getToolRotationAngle(stack, isLowerSlot);
                case VANILLA:
                default:
                    return isLowerSlot ? -225F : 45F;
            }
        }
    }
}