package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.common.KeyActions;
import com.darkona.adventurebackpack.handlers.KeybindHandler;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;

public abstract class GuiWithTanks extends GuiContainer
{
    protected static final int LABEL_X = 50;
    protected static final int LABEL_WIDTH = 64;
    private static final int LABEL_LINE = 10;
    private final List<String> labelLines = new ArrayList<String>();

    public GuiWithTanks(Container p_i1072_1_)
    {
        super(p_i1072_1_);
    }

    protected void setLabels(String... parts)
    {
        labelLines.clear();
        for (String part : parts)
        {
            if (part.isEmpty()) labelLines.add(part);
            else labelLines.addAll(fontRendererObj.listFormattedStringToWidth(part, LABEL_WIDTH));
        }
    }

    protected void drawLabels(int y, int color)
    {
        for (int i = 0; i < labelLines.size(); i++)
        {
            fontRendererObj.drawString(labelLines.get(i), LABEL_X, y + i * LABEL_LINE, color, false);
        }
    }

    private GuiToggleButton toggle;

    protected void setToggle(GuiToggleButton button)
    {
        toggle = button;
        if (toggle != null) toggle.init();
    }

    protected boolean isToggleOn()
    {
        return false;
    }

    protected void drawToggle(int mouseX, int mouseY)
    {
        if (toggle != null) toggle.draw(this, mc, isToggleOn(), toggle.inButton(this, mouseX, mouseY));
    }

    protected boolean clickToggle(int mouseX, int mouseY)
    {
        if (toggle == null || !toggle.inButton(this, mouseX, mouseY)) return false;
        mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
        KeybindHandler.press(mc.thePlayer, KeyActions.Key.ABILITY, 1, false);
        return true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (toggle != null && toggle.inButton(this, mouseX, mouseY))
        {
            drawHoveringText(toggle.tooltip(isToggleOn()), mouseX, mouseY, fontRendererObj);
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
