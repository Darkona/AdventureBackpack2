package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.config.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
                   // RenderHelper.enableStandardItemLighting();
                   // RenderHelper.enableGUIStandardItemLighting();

                    GL11.glDisable(GL11.GL_LIGHTING); //Forge: Make sure that render states are reset, a renderEffect can derp them up.
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    //GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.0F);
                    renderHose.renderIcon(0, 0, icon, 16, 16);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    GL11.glDisable(GL11.GL_BLEND);

                    if(ConfigHandler.TANKS_OVERLAY)
                    {
                        GL11.glPushMatrix();
                        GL11.glScalef(0.5f, 0.5f, 0.5f);
                        if (fontRenderer != null)
                        {
                            fontRenderer.drawString(mode, 0, 0, 0xFFFFFF);
                            fontRenderer.drawString(amount, 0, 18, 0xFFFFFF);
                            fontRenderer.drawString(name, 0, 24, 0xFFFFFF);
                        }
                        GL11.glPopMatrix();
                    }
                    //RenderHelper.disableStandardItemLighting();
                    GL11.glDisable(GL12.GL_RESCALE_NORMAL);
                    break;

                }
        }
    }

}
