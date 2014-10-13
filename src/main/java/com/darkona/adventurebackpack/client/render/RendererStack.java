package com.darkona.adventurebackpack.client.render;

import copy.betterstorage.utils.RenderUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class RendererStack extends ModelRenderer {

    public ItemStack stack;
    private boolean which;

    public RendererStack(ModelBase modelBase, boolean which) {
        super(modelBase);
        this.which = which;
        addChild(new Thing(modelBase));
    }

    private class Thing extends ModelRenderer {
        public Thing(ModelBase modelBase) {
            super(modelBase);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void render(float par1) {
            if (stack == null)
                return;
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
            if (which) {
                GL11.glScalef(0.6F, 0.6F, 0.6F);
                GL11.glPushMatrix();
                GL11.glRotatef(-90F, 0, 1, 0);
                GL11.glRotatef(-225F, 0, 0, 1);

            } else {
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                GL11.glPushMatrix();
                GL11.glRotatef(45F, 0, 0, 1);

            }
            RenderUtils.renderItemIn3d(stack);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
            GL11.glPopMatrix();

        }
    }

}