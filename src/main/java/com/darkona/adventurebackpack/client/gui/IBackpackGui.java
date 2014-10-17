package com.darkona.adventurebackpack.client.gui;

import net.minecraft.util.IIcon;

/**
 * Created by Darkona on 12/10/2014.
 */
public interface IBackpackGui
{

    public void drawTexturedModelRectFromIcon(int i, int j, IIcon icon, int h, int w);

    public int getLeft();

    public int getTop();

    public void drawTexturedModalRect(int i, int j, int srcX, int srcY, int w, int h);

    public float getZLevel();
}
