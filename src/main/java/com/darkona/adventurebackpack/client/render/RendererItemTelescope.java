package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.client.models.ModelTelescope;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RendererItemTelescope implements IItemRenderer
{
    private static final float MODEL_SCALE = 0.0625F;
    private static final ModelTelescope MODEL = new ModelTelescope();
    private static final ResourceLocation TEXTURE = Resources.modelTextures("telescope");

    public static void drawModel()
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        MODEL.render(MODEL_SCALE);
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON || type == ItemRenderType.ENTITY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
    {
        GL11.glPushMatrix();
        try
        {
            switch (type)
            {
                case EQUIPPED_FIRST_PERSON:
                    GL11.glTranslatef(0.6F, 0.4F, 0.2F);
                    GL11.glRotatef(-20F, 0F, 1F, 0F);
                    break;
                case EQUIPPED:
                    GL11.glTranslatef(0.35F, 0.35F, 0F);
                    GL11.glRotatef(45F, 0F, 0F, 1F);
                    GL11.glRotatef(90F, 0F, 1F, 0F);
                    GL11.glScalef(0.5F, 0.5F, 1F);
                    break;
                default:
                    GL11.glTranslatef(0F, 0.1F, 0F);
                    break;
            }
            drawModel();
        } finally
        {
            GL11.glPopMatrix();
        }
    }
}
