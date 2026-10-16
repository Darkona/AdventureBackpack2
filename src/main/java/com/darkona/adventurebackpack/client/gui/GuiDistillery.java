package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileDistillery;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.ContainerDistillery;
import com.darkona.adventurebackpack.inventory.DistilleryItems;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiDistillery extends GuiWithTanks
{
    private static final ResourceLocation TEXTURE = Resources.guiTextures("guiDistillery");
    private static final int CATALYST_X = 112, CATALYST_Y = 17;
    private static final int FLAME_X = 113, FLAME_Y = 37;
    private static final int ARROW_X = 133, ARROW_Y = 38, ARROW_W = 12, ARROW_H = 7;

    private final TileDistillery tile;
    private final GuiTank waterView = new GuiTank(8, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER);
    private final GuiTank outputView = new GuiTank(152, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER);
    private final String title;
    private final List<String> waterTip = new ArrayList<String>();
    private final List<String> outputTip = new ArrayList<String>();
    private final List<String> catalystTip = new ArrayList<String>();
    private int shownWater = -1, shownOutput = -1;

    public GuiDistillery(InventoryPlayer invPlayer, TileDistillery tile)
    {
        super(new ContainerDistillery(invPlayer, tile));
        this.tile = tile;
        this.title = I18n.format("container.adventurebackpack:distillery");
        xSize = 176;
        ySize = 166;
        ItemStack hint = DistilleryItems.catalystHint();
        catalystTip.add(I18n.format("adventurebackpack:gui.distillery.catalyst", hint == null ? "-" : hint.getDisplayName()));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        try
        {
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1, 1, 1, 1);
            mc.renderEngine.bindTexture(TEXTURE);
            int burn = tile.getBurnTicks(), fuel = tile.getFuelTicks();
            if (burn > 0 && fuel > 0)
            {
                int flame = Math.min(12, burn * 13 / fuel);
                drawTexturedModalRect(FLAME_X, FLAME_Y + 12 - flame, 176, 12 - flame, 14, flame + 1);
            }
            int batch = Math.max(1, ConfigHandler.DISTILLERY_TICKS_PER_BATCH);
            int filled = Math.min(ARROW_W, tile.getProgress() * ARROW_W / batch);
            if (filled > 0) drawTexturedModalRect(ARROW_X, ARROW_Y, 176, 16, filled, ARROW_H);
            waterView.draw(this, tile.getWater());
            outputView.draw(this, tile.getOutput());
            fontRendererObj.drawString(title, 54, 5, 0x373737, false);
        } finally
        {
            GL11.glPopAttrib();
        }
        drawCatalystHint();
    }

    private void drawCatalystHint()
    {
        ItemStack hint = DistilleryItems.catalystHint();
        if (hint == null || tile.getStackInSlot(TileDistillery.SLOT_CATALYST) != null) return;
        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        try
        {
            RenderHelper.enableGUIStandardItemLighting();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            itemRender.renderItemIntoGUI(fontRendererObj, mc.getTextureManager(), hint, CATALYST_X, CATALYST_Y);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            drawRect(CATALYST_X, CATALYST_Y, CATALYST_X + 16, CATALYST_Y + 16, 0xAA8B8B8B);
        } catch (RuntimeException foreign)
        {
        } finally
        {
            GL11.glPopAttrib();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int x = mouseX - guiLeft, y = mouseY - guiTop;
        if (inside(x, y, 8, 8, 16, 72))
        {
            refreshTip(waterTip, tile.getWater(), true);
            drawHoveringText(waterTip, mouseX, mouseY, fontRendererObj);
        } else if (inside(x, y, 152, 8, 16, 72))
        {
            refreshTip(outputTip, tile.getOutput(), false);
            drawHoveringText(outputTip, mouseX, mouseY, fontRendererObj);
        } else if (inside(x, y, CATALYST_X, CATALYST_Y, 16, 16) && tile.getStackInSlot(TileDistillery.SLOT_CATALYST) == null)
        {
            drawHoveringText(catalystTip, mouseX, mouseY, fontRendererObj);
        }
    }

    private static boolean inside(int x, int y, int left, int top, int w, int h)
    {
        return x >= left && x < left + w && y >= top && y < top + h;
    }

    private void refreshTip(List<String> tip, FluidTank tank, boolean isWater)
    {
        int amount = tank.getFluidAmount();
        if (isWater ? amount == shownWater : amount == shownOutput) return;
        if (isWater) shownWater = amount;
        else shownOutput = amount;
        tip.clear();
        FluidStack fluid = tank.getFluid();
        tip.add(fluid == null || amount <= 0
                ? I18n.format(isWater ? "adventurebackpack:gui.distillery.water" : "adventurebackpack:gui.distillery.ethanol")
                : fluid.getLocalizedName());
        tip.add(amount + " / " + tank.getCapacity() + " mB");
    }
}
