package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.config.Keybindings;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
    private static final int BORDER = 0xFF373737;
    private static final int ON = 0xFF3C9A3C;
    private static final int ON_HOVER = 0xFF5CC85C;
    private static final int OFF = 0xFF8A3030;
    private static final int OFF_HOVER = 0xFFB05050;
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

    public GuiToggleButton(int x, int y, int w, int h, String titleKey)
    {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
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

    public void draw(FontRenderer font, boolean on, boolean hover)
    {
        Gui.drawRect(x, y, x + w, y + h, BORDER);
        Gui.drawRect(x + 1, y + 1, x + w - 1, y + h - 1, on ? (hover ? ON_HOVER : ON) : (hover ? OFF_HOVER : OFF));
        String label = on ? labelOn : labelOff;
        GL11.glPushMatrix();
        try
        {
            GL11.glTranslatef(x + w / 2.0F, y + (h - 8 * LABEL_SCALE) / 2.0F, 0);
            GL11.glScalef(LABEL_SCALE, LABEL_SCALE, 1);
            font.drawString(label, -font.getStringWidth(label) / 2, 0, 0xFFFFFF, false);
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
