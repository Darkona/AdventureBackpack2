package com.darkona.adventurebackpack.client.gui;

import net.minecraft.util.IIcon;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */
public interface IBackpackGui
{
    void drawTexturedModelRectFromIcon(int i, int j, IIcon icon, int h, int w);

    int getLeft();

    int getTop();

    //public void drawTexturedModalRect(int i, int j, int srcX, int srcY, int w, int h);

    float getZLevel();
}
