package com.darkona.adventurebackpack.client.gui;

import com.darkona.adventurebackpack.inventory.BackpackLayout;

import java.util.Arrays;

public final class BackpackBackground
{
    public static final int STRIDE = 6;

    private static final int EDGE = 4;
    private static final int TOP_LEFT_U = 0, TOP_LEFT_V = 0;
    private static final int TOP_U = 4, TOP_V = 0, TOP_LEN = 240;
    private static final int TOP_RIGHT_U = 244, TOP_RIGHT_V = 0;
    private static final int LEFT_U = 0, LEFT_V = 4, LEFT_LEN = 100;
    private static final int RIGHT_U = 244, RIGHT_V = 4, RIGHT_LEN = 100;
    private static final int BOTTOM_LEFT_U = 36, BOTTOM_LEFT_V = 203;
    private static final int BOTTOM_U = 40, BOTTOM_V = 203, BOTTOM_LEN = 168;
    private static final int BOTTOM_RIGHT_U = 208, BOTTOM_RIGHT_V = 203;
    private static final int FILL_U = 42, FILL_V = 6, FILL_W = 18, FILL_H = 70;
    private static final int BAND_V = 117;
    private static final int BAND_LEFT_CORNER_U = 0, BAND_LEFT_U = 4, BAND_LEFT_LEN = 29, BAND_LEFT_STEP_U = 33;
    private static final int BAND_RIGHT_STEP_U = 209, BAND_RIGHT_U = 215, BAND_RIGHT_LEN = 29, BAND_RIGHT_CORNER_U = 244;
    private static final int STEP = 6;
    private static final int STORAGE_U = 61, STORAGE_V = 6;
    private static final int CRAFT_U = 151, CRAFT_V = 60;
    private static final int RESULT_U = 225, RESULT_V = 96;
    private static final int TOOL_U = 43, TOOL_V = 78;
    private static final int PLAYER_U = 43, PLAYER_V = 124;
    private static final int BUCKETS_LEFT_U = 5, BUCKETS_RIGHT_U = 225, BUCKETS_V = 6, BUCKETS_H = 48;
    private static final int TANK_LEFT_U = 24, TANK_RIGHT_U = 206, TANK_TOP_V = 6, TANK_BODY_V = 7, TANK_BODY_H = 25;
    private static final int TANK_MARK_V = 32, TANK_BOTTOM_V = 107, TANK_FRAME_W = 18;
    private static final int PIPE_U = 205, PIPE_V = 108, PIPE_W = 20, PIPE_H = 6;

    private int[] out = new int[STRIDE * 128];
    private int size;

    private BackpackBackground()
    {
    }

    public static int[] compose(BackpackLayout l)
    {
        BackpackBackground b = new BackpackBackground();
        b.panels(l);
        b.slots(l);
        b.tanks(l);
        return Arrays.copyOf(b.out, b.size);
    }

    private void panels(BackpackLayout l)
    {
        int w = l.width();
        int upper = l.upperHeight();
        int bandY = upper - EDGE;
        int lowerX = l.lowerX();
        int lowerRight = lowerX + l.lowerWidth();
        int bottomY = l.height() - EDGE;

        add(0, 0, TOP_LEFT_U, TOP_LEFT_V, EDGE, EDGE);
        repeat(EDGE, 0, w - 2 * EDGE, EDGE, TOP_U, TOP_V, TOP_LEN, EDGE);
        add(w - EDGE, 0, TOP_RIGHT_U, TOP_RIGHT_V, EDGE, EDGE);
        repeat(0, EDGE, EDGE, bandY - EDGE, LEFT_U, LEFT_V, EDGE, LEFT_LEN);
        repeat(w - EDGE, EDGE, EDGE, bandY - EDGE, RIGHT_U, RIGHT_V, EDGE, RIGHT_LEN);
        repeat(EDGE, EDGE, w - 2 * EDGE, bandY - EDGE, FILL_U, FILL_V, FILL_W, FILL_H);

        add(0, bandY, BAND_LEFT_CORNER_U, BAND_V, EDGE, EDGE);
        repeat(EDGE, bandY, lowerX - STEP / 2 - EDGE, EDGE, BAND_LEFT_U, BAND_V, BAND_LEFT_LEN, EDGE);
        add(lowerX - STEP / 2, bandY, BAND_LEFT_STEP_U, BAND_V, STEP, EDGE);
        repeat(lowerX + STEP / 2, bandY, lowerRight - lowerX - STEP, EDGE, FILL_U, FILL_V, FILL_W, FILL_H);
        add(lowerRight - STEP / 2, bandY, BAND_RIGHT_STEP_U, BAND_V, STEP, EDGE);
        repeat(lowerRight + STEP / 2, bandY, w - EDGE - lowerRight - STEP / 2, EDGE, BAND_RIGHT_U, BAND_V, BAND_RIGHT_LEN, EDGE);
        add(w - EDGE, bandY, BAND_RIGHT_CORNER_U, BAND_V, EDGE, EDGE);

        repeat(lowerX, upper, EDGE, bottomY - upper, LEFT_U, LEFT_V, EDGE, LEFT_LEN);
        repeat(lowerRight - EDGE, upper, EDGE, bottomY - upper, RIGHT_U, RIGHT_V, EDGE, RIGHT_LEN);
        repeat(lowerX + EDGE, upper, lowerRight - lowerX - 2 * EDGE, bottomY - upper, FILL_U, FILL_V, FILL_W, FILL_H);
        add(lowerX, bottomY, BOTTOM_LEFT_U, BOTTOM_LEFT_V, EDGE, EDGE);
        repeat(lowerX + EDGE, bottomY, lowerRight - lowerX - 2 * EDGE, EDGE, BOTTOM_U, BOTTOM_V, BOTTOM_LEN, EDGE);
        add(lowerRight - EDGE, bottomY, BOTTOM_RIGHT_U, BOTTOM_RIGHT_V, EDGE, EDGE);
    }

    private void slots(BackpackLayout l)
    {
        int s = BackpackLayout.SLOT;
        for (int k = 0; k < l.storage(); k++)
        {
            add(l.storageX(k) - 1, l.storageY(k) - 1, STORAGE_U, STORAGE_V, s, s);
        }
        add(l.craftLeft() - 1, l.craftTop() - 1, CRAFT_U, CRAFT_V, 3 * s, 3 * s);
        add(l.resultX() - 1, l.resultY() - 1, RESULT_U, RESULT_V, s, s);
        add(l.toolX() - 1, l.toolY(0) - 1, TOOL_U, TOOL_V, s, 2 * s);
        add(l.bucketX(0) - 1, l.bucketY(0) - 1, BUCKETS_LEFT_U, BUCKETS_V, s, BUCKETS_H);
        if (l.tanks() > 1) add(l.bucketX(2) - 1, l.bucketY(2) - 1, BUCKETS_RIGHT_U, BUCKETS_V, s, BUCKETS_H);
        for (int q = 0; q < l.quiver(); q++)
        {
            add(l.quiverX(q) - 1, l.quiverY(q) - 1, STORAGE_U, STORAGE_V, s, s);
        }
        for (int i = 0; i < 27; i++)
        {
            add(l.playerX() + s * (i % 9) - 1, l.playerY() + s * (i / 9) - 1, PLAYER_U, PLAYER_V, s, s);
        }
        for (int i = 0; i < 9; i++)
        {
            add(l.playerX() + s * i - 1, l.hotbarY() - 1, PLAYER_U, PLAYER_V, s, s);
        }
    }

    private void tanks(BackpackLayout l)
    {
        int h = l.tankHeight();
        for (int t = 0; t < l.tanks(); t++)
        {
            int u = t == 0 ? TANK_LEFT_U : TANK_RIGHT_U;
            int x = l.tankX(t) - 1;
            int y = l.tankY() - 1;
            add(x, y, u, TANK_TOP_V, TANK_FRAME_W, 1);
            repeat(x, y + 1, TANK_FRAME_W, h, u, TANK_BODY_V, TANK_FRAME_W, TANK_BODY_H);
            for (int q = 1; q <= 3; q++)
            {
                add(x, l.tankY() + h * q / 4, u, TANK_MARK_V, TANK_FRAME_W, 1);
            }
            add(x, l.tankY() + h, u, TANK_BOTTOM_V, TANK_FRAME_W, 1);
        }
        add(l.tankX(1) - 2, l.resultY() + 11, PIPE_U, PIPE_V, PIPE_W, PIPE_H);
    }

    private void repeat(int x, int y, int w, int h, int u, int v, int pieceW, int pieceH)
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

    private void add(int x, int y, int u, int v, int w, int h)
    {
        if (w <= 0 || h <= 0) return;
        if (size + STRIDE > out.length) out = Arrays.copyOf(out, out.length * 2);
        out[size++] = x;
        out[size++] = y;
        out[size++] = u;
        out[size++] = v;
        out[size++] = w;
        out[size++] = h;
    }
}
