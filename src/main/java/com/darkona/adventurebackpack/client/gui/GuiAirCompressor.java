package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.block.TileAirCompressor;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.ContainerAirCompressor;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** GUI of the Air Compressor: its air tank, the fire of the furnace, the container slots and the Scuba Gear slot. */
public class GuiAirCompressor extends GuiWithTanks
{
    private static final ResourceLocation texture = Resources.guiTextures("guiAirCompressor");
    private static final GuiTank airTank = new GuiTank(8, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER);

    private final TileAirCompressor tile;
    private int shownAir = -1;
    private String airLabel = "";
    private final String title;

    public GuiAirCompressor(InventoryPlayer invPlayer, TileAirCompressor tile)
    {
        super(new ContainerAirCompressor(invPlayer, tile));
        this.tile = tile;
        this.title = I18n.format("container.adventurebackpack:airCompressor");
        xSize = 176;
        ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GL11.glColor4f(1, 1, 1, 1);
        this.mc.renderEngine.bindTexture(texture);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        int air = tile.getTank().getFluidAmount();
        if (air != shownAir)
        {
            shownAir = air;
            airLabel = I18n.format("adventurebackpack:gui.scuba.air", air, tile.getTank().getCapacity());
        }
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        if (tile.getBurnTicks() > 0 && tile.getFuelTicks() > 0)
        {
            this.mc.renderEngine.bindTexture(texture);
            GL11.glColor4f(1, 1, 1, 1);
            int flame = tile.getBurnTicks() * 13 / tile.getFuelTicks();
            drawTexturedModalRect(78, 48 + 12 - flame, 176, 12 - flame, 14, flame + 1);
        }
        airTank.draw(this, tile.getTank());
        fontRendererObj.drawString(title, 52, 6, 0x373737, false);
        fontRendererObj.drawString(airLabel, 52, 16, 0x373737, false);
    }
}
