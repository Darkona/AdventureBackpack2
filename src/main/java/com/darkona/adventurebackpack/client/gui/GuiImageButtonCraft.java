package com.darkona.adventurebackpack.client.gui;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public class GuiImageButtonCraft
{
    private int X;
    private int Y;
    private int W;
    private int H;

    public GuiImageButtonCraft(int X, int Y, int W, int H)
    {
        this.X = X;
        this.Y = Y;
        this.W = W;
        this.H = H;
    }

    public void draw(GuiCraftAdvBackpack gui, int srcX, int srcY)
    {
        gui.drawTexturedModalRect(gui.getLeft() + X, gui.getTop() + Y, srcX, srcY, W, H);
    }

    public boolean inButton(GuiCraftAdvBackpack gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getLeft();
        mouseY -= gui.getTop();
        return X <= mouseX && mouseX <= X + W && Y <= mouseY && mouseY <= Y + H;
    }
}
