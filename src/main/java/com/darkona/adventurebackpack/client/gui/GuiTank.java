package com.darkona.adventurebackpack.client.gui;

import codechicken.lib.render.TextureUtils;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiTank
{
    private int height;
    private int width;
    private int startX;
    private int startY;
    private int offsetX = 0;
    private int offsetY = 0;
    private int resolution;
    private int liquidPerPixel;
    private float zLevel;
    private FluidTank tank;

    public GuiTank(int X, int Y, int H, int W, int resolution)
    {
        this.startX = X;
        this.startY = Y;
        this.height = H;
        this.width = W;
        this.resolution = resolution > 0 ? W / resolution : W;
    }

    public List<String> getTankTooltip()
    {
        FluidStack fluid = tank.getFluid();
        String fluidName = (fluid != null) ? fluid.getLocalizedName() : "None";
        String fluidAmount = (fluid != null) ? fluid.amount + "/" + ConfigHandler.BACKPACK_TANK_CAPACITY : "Empty";
        ArrayList<String> tankTips = new ArrayList<String>();
        tankTips.add(fluidName);
        tankTips.add(fluidAmount);
        return tankTips;
    }

    public void draw(GuiWithTanks gui, FluidTank theFluid)
    {
        tank = theFluid;
        liquidPerPixel = Math.max(1, tank.getCapacity() / Math.max(1, this.height));
        this.zLevel = gui.getZLevel()+1;
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

    public void draw(GuiWithTanks gui, FluidTank theFluid, int X, int Y)
    {
        offsetX = X;
        offsetY = Y;
        draw(gui, theFluid);
    }

    private void drawMethodOne(GuiWithTanks gui)
    {
        if(tank.getFluid() != null)
        {
            FluidStack fluid = tank.getFluid();

            IIcon icon = fluid.getFluid().getStillIcon();
            if (icon == null) return;
            int pixelsY = fluid.amount / liquidPerPixel;
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            int maxY = (startY + offsetY) + height;
            for (int i = (startX + offsetX); i < (startX + offsetX) + width; i += resolution)
            {
                for (int j = maxY - resolution; j >= maxY - pixelsY; j -= resolution)
                {
                    GL11.glColor4f(1, 1, 1, 1);
                    gui.drawTexturedModelRectFromIcon(i, j, icon, resolution, resolution);
                }
            }
        }
    }

    private void drawMethodTwo()
    {
        if(tank.getFluid() != null)
        {
            FluidStack fluid = tank.getFluid();

            IIcon icon = fluid.getFluid().getStillIcon();
            if (icon == null) return;
            int pixelsY = fluid.amount / liquidPerPixel;
            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            int top = (startY + offsetY) + height - pixelsY;
            int maxY = (startY + offsetY) + height - 1;
            for (int i = (startX + offsetX); i < (startX + offsetX) + width; i += resolution)
            {
                int iconY = 7;
                for (int j = maxY; j >= top; j--)
                {
                    GL11.glColor4f(1, 1, 1, 1);
                    drawFluidPixelFromIcon(i, j, icon, resolution, 1, 0, iconY, resolution, 0, zLevel);
                    iconY = (iconY == 0) ? 7 : iconY - 1;
                }
            }
        }
    }

    private void drawMethodThree()
    {
        if(tank.getFluid() != null)
        {
            FluidStack fluid = tank.getFluid();

            try
            {
                IIcon icon = fluid.getFluid().getStillIcon();
                if (icon == null) return;
                TextureUtils.bindAtlas(fluid.getFluid().getSpriteNumber());
                drawFluidColumn(startX + offsetX, startY + offsetY, width, height, icon, fluid.amount, liquidPerPixel, zLevel);
            } catch (Exception oops)
            {
                LogHelper.error("Exception while trying to render the fluid in the GUI");
            }
        }
    }

    public boolean inTank(GuiWithTanks gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getLeft();
        mouseY -= gui.getTop();
        return startX <= mouseX && mouseX <= (startX + offsetX) + width && (startY + offsetY) <= mouseY && mouseY <= (startY + offsetY) + height;
    }

    public static int fluidTop(int startY, int height, int amount, int liquidPerPixel)
    {
        int filled = liquidPerPixel <= 0 ? height : Math.min(height, amount / liquidPerPixel);
        return startY + height - Math.max(0, filled);
    }

    public static void drawFluidColumn(int x, int startY, int width, int height, IIcon icon, int amount, int liquidPerPixel, float z)
    {
        int bottom = startY + height;
        int top = fluidTop(startY, height, amount, liquidPerPixel);
        int split = Math.min(top + 5, bottom);
        try
        {
            if (split > top)
            {
                GL11.glColor4f(1, 1, 1, 1);
                drawFluidPixelFromIcon(x, top, icon, width, split - top, 0, 0, 0, 0, z);
            }
            if (split < bottom)
            {
                GL11.glColor4f(0.9f, 0.9f, 0.9f, 1);
                drawFluidPixelFromIcon(x, split, icon, width, bottom - split, 0, 0, 0, 0, z);
            }
        } finally
        {
            GL11.glColor4f(1, 1, 1, 1);
        }
    }

    public static void drawFluidPixelFromIcon(int x, int y, IIcon icon, int w, int h, int srcX, int srcY, int srcW, int srcH, float zLevel)
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
