package com.darkona.adventurebackpack.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 06/01/2015
 *
 * @author Darkona
 */
public abstract class GuiWithTanks extends GuiContainer
{
    /**
     * The label column of the two-tank windows (Scuba Gear, Flamethrower Pack): from right of the container slots
     * (x 48) to the frame of the right tank (x 115).
     */
    protected static final int LABEL_X = 50;
    protected static final int LABEL_WIDTH = 64;
    private static final int LABEL_LINE = 10;
    private final List<String> labelLines = new ArrayList<String>();

    public GuiWithTanks(Container p_i1072_1_)
    {
        super(p_i1072_1_);
    }

    /** Rebuilds the label column, each part wrapped to its width ("" leaves an empty line). Only when a value changed. */
    protected void setLabels(String... parts)
    {
        labelLines.clear();
        for (String part : parts)
        {
            if (part.isEmpty()) labelLines.add(part);
            else labelLines.addAll(fontRendererObj.listFormattedStringToWidth(part, LABEL_WIDTH));
        }
    }

    /** Draws the label column built by {@link #setLabels}, from y down. */
    protected void drawLabels(int y, int color)
    {
        for (int i = 0; i < labelLines.size(); i++)
        {
            fontRendererObj.drawString(labelLines.get(i), LABEL_X, y + i * LABEL_LINE, color, false);
        }
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

}
