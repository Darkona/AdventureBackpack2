package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.config.Keybindings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.darkona.adventurebackpack.client.gui.widget.BackpackButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiToggleButton
{
    private static final int ON = 0x1E7A1E;
    private static final int OFF = 0x8A2020;
    private static final float LABEL_SCALE = 0.75F;

    private final int x;
    private final int y;
    private final int w;
    private final int h;
    private final String titleKey;
    private final List<String> tooltipOn = new ArrayList<String>(2);
    private final List<String> tooltipOff = new ArrayList<String>(2);
    private String labelOn = "";
    private String labelOff = "";

    public static final int WIDTH = 18;
    public static final int HEIGHT = 12;

    public GuiToggleButton(int x, int y, String titleKey)
    {
        this.x = x;
        this.y = y;
        this.w = WIDTH;
        this.h = HEIGHT;
        this.titleKey = titleKey;
    }

    public void init()
    {
        labelOn = I18n.format("adventurebackpack:gui.toggle.on");
        labelOff = I18n.format("adventurebackpack:gui.toggle.off");
        String hint = EnumChatFormatting.GRAY + I18n.format("adventurebackpack:gui.toggle.hint", Keybindings.keyName(Keybindings.ability));
        tooltipOn.clear();
        tooltipOn.add(I18n.format(titleKey, I18n.format("adventurebackpack:gui.toggle.stateOn")));
        tooltipOn.add(hint);
        tooltipOff.clear();
        tooltipOff.add(I18n.format(titleKey, I18n.format("adventurebackpack:gui.toggle.stateOff")));
        tooltipOff.add(hint);
    }

    public boolean inButton(GuiWithTanks gui, int mouseX, int mouseY)
    {
        mouseX -= gui.getLeft();
        mouseY -= gui.getTop();
        return x <= mouseX && mouseX < x + w && y <= mouseY && mouseY < y + h;
    }

    public void draw(Gui gui, Minecraft mc, boolean on, boolean hover)
    {
        BackpackButton.drawFace(gui, mc, x, y, h, hover, on);
        FontRenderer font = mc.fontRenderer;
        String label = on ? labelOn : labelOff;
        int shift = on ? 1 : 0;
        GL11.glPushMatrix();
        try
        {
            GL11.glTranslatef(x + w / 2.0F + shift, y + (h - 8 * LABEL_SCALE) / 2.0F + shift, 0);
            GL11.glScalef(LABEL_SCALE, LABEL_SCALE, 1);
            font.drawString(label, -font.getStringWidth(label) / 2, 0, on ? ON : OFF, false);
        } finally
        {
            GL11.glPopMatrix();
        }
    }

    public List<String> tooltip(boolean on)
    {
        return on ? tooltipOn : tooltipOff;
    }
}
