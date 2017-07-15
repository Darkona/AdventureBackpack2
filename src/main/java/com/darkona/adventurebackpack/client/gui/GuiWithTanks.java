package com.darkona.adventurebackpack.client.gui;

import org.lwjgl.input.Mouse;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

/**
 * Created on 06/01/2015
 *
 * @author Darkona
 */
public abstract class GuiWithTanks extends GuiContainer
{
    GuiWithTanks(Container p_i1072_1_)
    {
        super(p_i1072_1_);
    }

    public int getLeft()
    {
        return guiLeft;
    }

    public int getTop()
    {
        return guiTop;
    }

    public float getZLevel()
    {
        return zLevel;
    }

    @Override
    public void handleMouseInput()
    {
        if (Mouse.getEventDWheel() != 0)
        {
            return; // forbid mouseWheel, preventing glitches with Shift+Wheel on fluid containers and so on
        }

        super.handleMouseInput();
    }

}
