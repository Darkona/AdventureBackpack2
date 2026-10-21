package com.darkona.adventurebackpack.client.gui.widget;

import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public final class ActionButton
{
    public static final int SIZE = 16;
    public static final int SORT = 0, LOCK = 1;

    private static final ResourceLocation WIDGETS = Resources.guiTextures("guiBackpackWidgets");
    private static final int FACE_V = 40;
    private static final int FACE_U = 0, FACE_HOVER_U = 16, FACE_PRESSED_U = 32, FACE_ACTIVE_U = 48;
    private static final int GLYPH_V = 56;
    private static final int SORT_U = 0, SORT_W = 10, SORT_H = 10, SORT_X = 3, SORT_Y = 3;
    private static final int LOCK_CLOSED_U = 16, LOCK_OPEN_U = 30, LOCK_W = 9, LOCK_H = 12, LOCK_X = 4, LOCK_Y = 3;
    private static final int BADGE_U = 44;
    public static final int BADGE_W = 6, BADGE_H = 8;

    private final int x;
    private final int y;
    private final int mode;

    public ActionButton(int x, int y, int mode)
    {
        this.x = x;
        this.y = y;
        this.mode = mode;
    }

    public int mode()
    {
        return mode;
    }

    public boolean contains(int localX, int localY)
    {
        return localX >= x && localX < x + SIZE && localY >= y && localY < y + SIZE;
    }

    public void draw(Gui gui, Minecraft mc, int left, int top, boolean hover, boolean pressed, boolean active)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(WIDGETS);
        int face = pressed ? FACE_PRESSED_U : active ? FACE_ACTIVE_U : hover ? FACE_HOVER_U : FACE_U;
        gui.drawTexturedModalRect(left + x, top + y, face, FACE_V, SIZE, SIZE);
        int shift = pressed || active ? 1 : 0;
        if (mode == SORT) gui.drawTexturedModalRect(left + x + SORT_X + shift, top + y + SORT_Y + shift, SORT_U, GLYPH_V, SORT_W, SORT_H);
        else gui.drawTexturedModalRect(left + x + LOCK_X + shift, top + y + LOCK_Y + shift, active ? LOCK_CLOSED_U : LOCK_OPEN_U, GLYPH_V, LOCK_W, LOCK_H);
    }

    public static void drawBadge(Gui gui, Minecraft mc, int x, int y)
    {
        GL11.glColor4f(1, 1, 1, 1);
        mc.renderEngine.bindTexture(WIDGETS);
        gui.drawTexturedModalRect(x, y, BADGE_U, GLYPH_V, BADGE_W, BADGE_H);
    }
}
