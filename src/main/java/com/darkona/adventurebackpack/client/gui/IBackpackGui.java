package com.darkona.adventurebackpack.client.gui;

import net.minecraft.util.IIcon;

public interface IBackpackGui
{
    public void drawTexturedModelRectFromIcon(int i, int j, IIcon icon, int h, int w);

    public int getLeft();

    public int getTop();

    public float getZLevel();
}
