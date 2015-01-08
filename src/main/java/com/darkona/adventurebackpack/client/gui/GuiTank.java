package com.darkona.adventurebackpack.client.gui;

import codechicken.lib.render.TextureUtils;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Darkona on 12/10/2014.
 */
public class GuiTank
{
    private int height;
    private int width;
    private int startX;
    private int startY;
    private int resolution;
    private int liquidPerPixel;
    private float zLevel;
    private FluidStack fluid;

    /**
     * Draws the fluid from a fluidstack in a GUI.
     *
     * @param startX     The startX coordinate to start drawing from.
     * @param Y          The startY coordinate to start drawing from.
     * @param H          The height in pixels of the tank.
     * @param W          The width in pixels of the tank.
     * @param resolution The resolution of the fluid painted in the tank. Higher values
     *                   mean smaller and more numerous boxes. Values can be 1, 2, 4,
     *                   8, 16. Other values are untested, but i guess they should
     *                   always be integer divisors of the width, with modulus 0;
     */
    public GuiTank(int startX, int Y, int H, int W, int resolution, int maxCapacity)
    {
        this.startX = startX;
        this.startY = Y;
        this.height = H;
        this.width = W;
        this.resolution = resolution > 0 ? W / resolution : W;
        liquidPerPixel = maxCapacity / this.height;
    }

    public List<String> getTankTooltip()
    {
        String fluidName = (fluid != null) ? fluid.getLocalizedName() : "None";
        String fluidAmount = (fluid != null) ? fluid.amount + "/" + Constants.basicTankCapacity : "Empty";
        ArrayList<String> tankTips = new ArrayList<String>();
        tankTips.add(fluidName);
        tankTips.add(fluidAmount);
        return tankTips;
    }

    /**
     * @param gui
     * @param theFluid
     */
    public void draw(IBackpackGui gui, FluidStack theFluid)
    {
        fluid = theFluid;
        this.zLevel = gui.getZLevel();
        switch (ConfigHandler.GUI_TANK_RENDER)
        {
            case 1:
                drawMethodOne(gui);
                break;
            case 2:
                drawMethodTwo();
                break;
            case 3:
                drawMethodThree();
                break;
            default:
                drawMethodThree();
                break;
        }

    }

    /**
     * @param gui
     * @param
     */
    private void drawMethodOne(IBackpackGui gui)
    {
        if (fluid != null)
        {
            IIcon icon = fluid.getFluid().getStillIcon();
            int pixelsY = fluid.amount / liquidPerPixel;
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            int maxY = startY + height;
            for (int i = startX; i < startX + width; i += resolution)
            {
                for (int j = maxY - resolution; j >= maxY - pixelsY; j -= resolution)
                {
                    GL11.glPushMatrix();
                    GL11.glColor4f(1, 1, 1, 1);
                    gui.drawTexturedModelRectFromIcon(i, j, icon, resolution, resolution);
                    GL11.glPopMatrix();
                }
            }
        }
    }

    /**
     * @param
     */
    private void drawMethodTwo()
    {
        if (fluid != null)
        {
            IIcon icon = fluid.getFluid().getStillIcon();
            int pixelsY = fluid.amount / liquidPerPixel;
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            int top = startY + height - pixelsY;
            int maxY = startY + height - 1;
            for (int i = startX; i < startX + width; i += resolution)
            {
                int iconY = 7;
                for (int j = maxY; j >= top; j--)
                {
                    GL11.glPushMatrix();
                    GL11.glColor4f(1, 1, 1, 1);
                    drawFluidPixelFromIcon(i, j, icon, resolution, 1, 0, iconY, resolution, 0);
                    iconY = (iconY == 0) ? 7 : iconY - 1;
                    GL11.glPopMatrix();
                }
            }
        }
    }

    /**
     * @param
     */
    private void drawMethodThree()
    {
        if (fluid != null)
        {
            try
            {
                IIcon icon = fluid.getFluid().getStillIcon();
                TextureUtils.bindAtlas(fluid.getFluid().getSpriteNumber());
                int top = startY + height - (fluid.amount / liquidPerPixel);
                for (int j = startY + height - 1; j >= top; j--)
                {
                    for (int i = startX; i <= startX + width - 1; i++)
                    {
                        GL11.glPushMatrix();
                        if (j >= top + 5)
                        {
                            GL11.glColor4f(0.9f, 0.9f, 0.9f, 1);
                        } else
                        {
                            GL11.glColor4f(1, 1, 1, 1);
                        }
                        drawFluidPixelFromIcon(i, j, icon, 1, 1, 0, 0, 0, 0);
                        GL11.glPopMatrix();
                    }
                }
            } catch (Exception oops)
            {
                LogHelper.error("Exception while trying to render the fluid in the GUI");
                //oops.printStackTrace();
            }
        }
    }

    /**
     * @param gui
     * @param mouseX
     * @param mouseY
     * @return
     */
    public boolean inTank(IBackpackGui gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getLeft();
        mouseY -= gui.getTop();
        return startX <= mouseX && mouseX <= startX + width && startY <= mouseY && mouseY <= startY + height;
    }

    /**
     * Draws a box textured with the selected box of an icon.
     *
     * @param x    The startX coordinate where to start drawing the box.
     * @param y    The startY coordinate where to start drawing the box.
     * @param icon The icon to draw from.
     * @param w    The Width of the drawed box.
     * @param h    The height of the drawed box.
     * @param srcX The startX coordinate from the icon to start drawing from. Starts
     *             at 0.
     * @param srcY The startY coordinate from the icon to start drawing from. Starts
     *             at 0.
     * @param srcW The width of the selection in the icon to draw from. Starts at
     *             0.
     * @param srcH The height of the selection in the icon to draw from. Starts
     *             at 0.
     */
    public void drawFluidPixelFromIcon(int x, int y, IIcon icon, int w, int h, int srcX, int srcY, int srcW, int srcH)
    {
        double minU = icon.getMinU();
        double maxU = icon.getMaxU();
        double minV = icon.getMinV();
        double maxV = icon.getMaxV();

        double singleU = (maxU - minU) / icon.getIconHeight();
        double singleV = (maxV - minV) / icon.getIconWidth();

        double newMinU = minU + (singleU * srcX);
        double newMinV = minV + (singleV * srcY);

        double newMaxU = newMinU + (singleU * srcW);
        double newMaxV = newMinV + (singleV * srcH);

        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV(x, y + h, zLevel, newMinU, newMaxV);
        tessellator.addVertexWithUV(x + w, y + h, zLevel, newMaxU, newMaxV);
        tessellator.addVertexWithUV(x + w, y, zLevel, newMaxU, newMinV);
        tessellator.addVertexWithUV(x, y, zLevel, newMinU, newMinV);
        tessellator.draw();


    }
}
