package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.inventory.CopterContainer;
import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

/**
 * Created on 03/01/2015
 *
 * @author Darkona
 */
public class GuiCopterPack extends GuiContainer implements IBackpackGui
{
    private InventoryCopterPack inventory;
    private static final ResourceLocation texture = Resources.guiTextures("guiCopterPack");
    private FluidStack fuel;
    private static GuiTank fuelTank = new GuiTank(77, 8, 72, 16, ConfigHandler.GUI_TANK_RENDER, 6000);
    public int lefties;
    public int topsies;

    public GuiCopterPack(EntityPlayer player, InventoryCopterPack inv)
    {
        super(new CopterContainer(player, inv));
        this.inventory = inv;
        xSize = 176;
        ySize = 166;
        this.lefties = guiLeft;
        this.topsies = guiTop;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_)
    {
        GL11.glColor4f(1, 1, 1, 1);

        this.mc.renderEngine.bindTexture(texture);

        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);


    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        inventory.openInventory();
        fuel = inventory.getFuelTank().getFluid();
        fuelTank.draw(this, fuel);

        GL11.glPushMatrix();
        String name = (fuel != null) ? fuel.getLocalizedName() : "None";
        String amount = (fuel != null ? fuel.amount : "Empty").toString();
        String capacity = Integer.toString(inventory.getFuelTank().getCapacity());
        int offsetY = 32;
        int offsetX = 8;
        fontRendererObj.drawString(name, 1 + offsetX, offsetY, 0x373737, false);
        fontRendererObj.drawString(amount, 1 + offsetX, 10 + offsetY, 0x373737, false);
        fontRendererObj.drawString(capacity, 1 + offsetX, 20 + offsetY, 0x373737, false);
        GL11.glPopMatrix();
    }

    @Override
    public int getLeft()
    {
        return this.lefties;
    }

    @Override
    public int getTop()
    {
        return this.topsies;
    }

    @Override
    public float getZLevel()
    {
        return 0;
    }
}
