package com.darkona.adventurebackpack.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import scala.collection.parallel.ParIterableLike;

/**
 * Created on 13/10/2014
 *
 * @author Darkona
 */
public class RendererHose implements IItemRenderer
{

    private static RenderItem renderHose = new RenderItem();
    private FontRenderer fontRenderer;
    private Tessellator tessellator = Tessellator.instance;

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type)
    {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
    {
        return false;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void renderItem(ItemRenderType type, ItemStack hose, Object... data)
    {
        fontRenderer = Minecraft.getMinecraft().fontRenderer;
        switch (type)
        {
            case INVENTORY:

                // ====================Render the item===================== //
                IIcon icon = hose.getItem().getIcon(hose, 1);
                renderHose.renderIcon(0, 0, icon, 16, 16);
                if (hose.hasTagCompound())
                {
                    String amount = Integer.toString(hose.getTagCompound().getInteger("amount"));
                    String name = hose.getTagCompound().getString("fluid");
                    String mode;
                    switch (hose.getTagCompound().getInteger("mode"))
                    {
                        case 0:
                            mode = "Suck";
                            break;
                        case 1:
                            mode = "Spill";
                            break;
                        case 2:
                            mode = "Drink";
                            break;
                        default:
                            mode = "Useless";
                            break;
                    }

                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glDepthMask(false);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                    tessellator.startDrawing(GL11.GL_QUADS);

                    tessellator.setColorRGBA(0, 0, 0, 0);

                    tessellator.addVertex(0, 0, 0);
                    tessellator.addVertex(0, 8, 0);
                    tessellator.addVertex(8, 8, 0);
                    tessellator.addVertex(8, 0, 0);

                    tessellator.draw();

                    GL11.glDepthMask(true);
                    GL11.glDisable(GL11.GL_BLEND);

                    GL11.glEnable(GL11.GL_TEXTURE_2D);

                    GL11.glPushMatrix();
                    GL11.glScalef(0.5f, 0.5f, 0.5f);
                    if (fontRenderer != null)
                    {
                        fontRenderer.drawStringWithShadow(mode, 0, 0, 0xFFFFFF);
                        fontRenderer.drawStringWithShadow(amount, 0, 18, 0xFFFFFF);
                        fontRenderer.drawStringWithShadow(name, 0, 24, 0xFFFFFF);
                    }
                    GL11.glPopMatrix();


                    break;

                }
        }
    }

}
