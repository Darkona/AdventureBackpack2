package com.darkona.adventurebackpack.client.gui.widget;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;

import java.util.Arrays;

public final class BackgroundComposer
{
    public static final int STRIDE = 8;
    public static final int X = 0, Y = 1, U = 2, V = 3, W = 4, H = 5, TEX = 6, ARGB = 7;
    public static final int WHITE = 0xFFFFFFFF;

    private int[] out = new int[STRIDE * 128];
    private int size;
    private int texture;
    private int argb = WHITE;

    private BackgroundComposer()
    {
    }

    public static int[] compose(WidgetLayout layout)
    {
        BackgroundComposer c = new BackgroundComposer();
        WidgetSkins.window().paint(c, layout);
        int[] order = paintOrder(layout);
        for (int w : order)
        {
            c.use(0, WHITE);
            WidgetSkins.get(layout.kind(w)).paint(c, layout, w);
        }
        for (int w = 0; w < layout.count(); w++)
        {
            c.use(0, WHITE);
            WidgetSkins.get(layout.kind(w)).paintOver(c, layout, w);
        }
        return Arrays.copyOf(c.out, c.size);
    }

    private static int[] paintOrder(WidgetLayout layout)
    {
        int n = layout.count();
        int[] order = new int[n];
        for (int i = 0; i < n; i++) order[i] = i;
        for (int i = 1; i < n; i++)
        {
            int w = order[i];
            int layer = WidgetSkins.get(layout.kind(w)).layer();
            int j = i - 1;
            while (j >= 0 && WidgetSkins.get(layout.kind(order[j])).layer() > layer)
            {
                order[j + 1] = order[j];
                j--;
            }
            order[j + 1] = w;
        }
        return order;
    }

    public BackgroundComposer use(int textureIndex, int color)
    {
        texture = textureIndex;
        argb = color;
        return this;
    }

    public void repeat(int x, int y, int w, int h, int u, int v, int pieceW, int pieceH)
    {
        for (int dy = 0; dy < h; dy += pieceH)
        {
            int ch = Math.min(pieceH, h - dy);
            for (int dx = 0; dx < w; dx += pieceW)
            {
                add(x + dx, y + dy, u, v, Math.min(pieceW, w - dx), ch);
            }
        }
    }

    public void add(int x, int y, int u, int v, int w, int h)
    {
        if (w <= 0 || h <= 0) return;
        if (size + STRIDE > out.length) out = Arrays.copyOf(out, out.length * 2);
        out[size++] = x;
        out[size++] = y;
        out[size++] = u;
        out[size++] = v;
        out[size++] = w;
        out[size++] = h;
        out[size++] = texture;
        out[size++] = argb;
    }
}
