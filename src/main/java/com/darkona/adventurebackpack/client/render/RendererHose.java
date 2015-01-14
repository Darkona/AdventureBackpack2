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
                    renderHose.renderIcon(0, 0, icon, 16, 16);
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
                    break;

                }
        }
    }

}
