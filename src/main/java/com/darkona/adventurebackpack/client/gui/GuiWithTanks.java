package com.darkona.adventurebackpack.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

/**
 * Created on 06/01/2015
 *
 * @author Darkona
 */
public abstract class GuiWithTanks extends GuiContainer
{
    public GuiWithTanks(Container p_i1072_1_)
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

    public float getZLevel() {return zLevel;}

    protected String getFirstWord(String text)
    {
        if (text.indexOf(' ') > -1)
        { // Check if there is more than one word.
            String firstWord = text.substring(0, text.indexOf(' '));
            String secondWord = text.substring(text.indexOf(' ') + 1);
            return firstWord.equals("Molten") ? secondWord : firstWord;// Extract first word.
        } else
        {
            return text; // Text is the first word itself.
        }
    }
}
