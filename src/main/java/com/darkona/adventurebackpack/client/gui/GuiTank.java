package com.darkona.adventurebackpack.client.gui;

import codechicken.lib.render.TextureUtils;
import com.darkona.adventurebackpack.common.Tint;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
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
    private boolean gauge;
    private final List<String> tooltip = new ArrayList<String>(2);
    private Fluid tooltipFluid;
    private int tooltipAmount;

    private static final int GAUGE_INSET = 3;
    private static final int GAUGE_MARGIN = 2;
    private static final int GAUGE_COLOR = 0xD0FFFFFF;
    private static final int SURFACE_COLOR = 0x90FFFFFF;

    public GuiTank(int X, int Y, int H, int W, int resolution)
    {
        this.startX = X;
        this.startY = Y;
        this.height = H;
        this.width = W;
        this.resolution = resolution > 0 ? W / resolution : W;
    }

    public GuiTank withGauge()
    {
        gauge = true;
        return this;
    }

    public List<String> getTankTooltip()
    {
        FluidStack fluid = tank == null ? null : tank.getFluid();
        Fluid kind = fluid == null ? null : fluid.getFluid();
        int amount = fluid == null ? -1 : fluid.amount;
        if (tooltip.isEmpty() || kind != tooltipFluid || amount != tooltipAmount)
        {
            tooltipFluid = kind;
            tooltipAmount = amount;
            tooltip.clear();
            tooltip.add(fluid != null ? fluid.getLocalizedName() : "None");
            tooltip.add(fluid != null ? fluid.amount + "/" + tank.getCapacity() : "Empty");
        }
        return tooltip;
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
        GL11.glColor4f(1, 1, 1, 1);
        if (gauge) drawGauge();
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
            int color = fluid.getFluid().getColor(fluid);
            GL11.glColor4f(Tint.red(color), Tint.green(color), Tint.blue(color), 1);
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
            int color = fluid.getFluid().getColor(fluid);
            GL11.glColor4f(Tint.red(color), Tint.green(color), Tint.blue(color), 1);
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
                drawFluidColumn(startX + offsetX, startY + offsetY, width, height, icon, fluid.amount, liquidPerPixel, zLevel, fluid.getFluid().getColor(fluid));
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

    private static final float BODY_SHADE = 0.9f;

    public static int gaugeX(int startX, int width)
    {
        return startX + width - GAUGE_INSET;
    }

    private void drawGauge()
    {
        int x = gaugeX(startX + offsetX, width);
        int top = startY + offsetY;
        drawLine(x, top + GAUGE_MARGIN, 1, height - 2 * GAUGE_MARGIN, GAUGE_COLOR, zLevel);
        FluidStack fluid = tank.getFluid();
        if (fluid != null && fluid.amount > 0)
        {
            int surface = fluidTop(top, height, fluid.amount, liquidPerPixel);
            if (surface < top + height) drawLine(startX + offsetX, surface, width, 1, SURFACE_COLOR, zLevel);
        }
    }

    private static void drawLine(int x, int y, int w, int h, int argb, float z)
    {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator t = Tessellator.instance;
        t.startDrawingQuads();
        t.setColorRGBA_I(argb & 0xFFFFFF, argb >>> 24);
        t.addVertex(x, y + h, z);
        t.addVertex(x + w, y + h, z);
        t.addVertex(x + w, y, z);
        t.addVertex(x, y, z);
        t.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

    public static int fluidTop(int startY, int height, int amount, int liquidPerPixel)
    {
        int filled = liquidPerPixel <= 0 ? height : Math.min(height, amount / liquidPerPixel);
        return startY + height - Math.max(0, filled);
    }

    public static void drawFluidColumn(int x, int startY, int width, int height, IIcon icon, int amount, int liquidPerPixel, float z, int color)
    {
        int bottom = startY + height;
        int top = fluidTop(startY, height, amount, liquidPerPixel);
        int split = Math.min(top + 5, bottom);
        try
        {
            if (split > top)
            {
                GL11.glColor4f(Tint.red(color), Tint.green(color), Tint.blue(color), 1);
                drawFluidPixelFromIcon(x, top, icon, width, split - top, 0, 0, 0, 0, z);
            }
            if (split < bottom)
            {
                int body = Tint.scale(color, BODY_SHADE);
                GL11.glColor4f(Tint.red(body), Tint.green(body), Tint.blue(body), 1);
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
