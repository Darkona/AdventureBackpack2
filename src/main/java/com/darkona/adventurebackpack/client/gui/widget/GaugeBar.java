package com.darkona.adventurebackpack.client.gui.widget;

import net.minecraft.client.gui.Gui;

public final class GaugeBar
{
    private static final int FRAME = 0xFF373737;
    private static final int TRACK = 0xFF555555;
    private static final int LEVEL_LINE = 0xD0FFFFFF;
    private static final int INSET = 1;

    private GaugeBar()
    {
    }

    public static void drawBar(int x, int y, int w, int h, float fill, int color)
    {
        Gui.drawRect(x, y, x + w, y + h, FRAME);
        Gui.drawRect(x + INSET, y + INSET, x + w - INSET, y + h - INSET, TRACK);
        float level = fill < 0 ? 0 : fill > 1 ? 1 : fill;
        int iw = w - 2 * INSET;
        int ih = h - 2 * INSET;
        if (h >= w)
        {
            int filled = Math.round(ih * level);
            if (filled <= 0) return;
            int top = y + INSET + ih - filled;
            Gui.drawRect(x + INSET, top, x + INSET + iw, y + INSET + ih, color);
            Gui.drawRect(x + INSET, top, x + INSET + iw, top + 1, LEVEL_LINE);
        } else
        {
            int filled = Math.round(iw * level);
            if (filled <= 0) return;
            int right = x + INSET + filled;
            Gui.drawRect(x + INSET, y + INSET, right, y + INSET + ih, color);
            Gui.drawRect(right - 1, y + INSET, right, y + INSET + ih, LEVEL_LINE);
        }
    }
}
