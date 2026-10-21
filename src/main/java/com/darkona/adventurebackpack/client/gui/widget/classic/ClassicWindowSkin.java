package com.darkona.adventurebackpack.client.gui.widget.classic;

import com.darkona.adventurebackpack.client.gui.widget.BackgroundComposer;
import com.darkona.adventurebackpack.client.gui.widget.WindowSkin;
import com.darkona.adventurebackpack.common.layout.WidgetLayout;

public final class ClassicWindowSkin extends WindowSkin
{
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

    private final int texture;

    public ClassicWindowSkin(int texture)
    {
        this.texture = texture;
    }

    @Override
    public void paint(BackgroundComposer c, WidgetLayout l)
    {
        c.use(texture, BackgroundComposer.WHITE);
        if (l.lowerX() <= 0 && l.lowerWidth() >= l.width())
        {
            paintSingle(c, l);
            return;
        }
        int w = l.width();
        int upper = l.upperHeight();
        int bandY = upper - EDGE;
        int lowerX = l.lowerX();
        int lowerRight = lowerX + l.lowerWidth();
        int bottomY = l.height() - EDGE;

        c.add(0, 0, TOP_LEFT_U, TOP_LEFT_V, EDGE, EDGE);
        c.repeat(EDGE, 0, w - 2 * EDGE, EDGE, TOP_U, TOP_V, TOP_LEN, EDGE);
        c.add(w - EDGE, 0, TOP_RIGHT_U, TOP_RIGHT_V, EDGE, EDGE);
        c.repeat(0, EDGE, EDGE, bandY - EDGE, LEFT_U, LEFT_V, EDGE, LEFT_LEN);
        c.repeat(w - EDGE, EDGE, EDGE, bandY - EDGE, RIGHT_U, RIGHT_V, EDGE, RIGHT_LEN);
        c.repeat(EDGE, EDGE, w - 2 * EDGE, bandY - EDGE, FILL_U, FILL_V, FILL_W, FILL_H);

        c.add(0, bandY, BAND_LEFT_CORNER_U, BAND_V, EDGE, EDGE);
        c.repeat(EDGE, bandY, lowerX - STEP / 2 - EDGE, EDGE, BAND_LEFT_U, BAND_V, BAND_LEFT_LEN, EDGE);
        c.add(lowerX - STEP / 2, bandY, BAND_LEFT_STEP_U, BAND_V, STEP, EDGE);
        c.repeat(lowerX + STEP / 2, bandY, lowerRight - lowerX - STEP, EDGE, FILL_U, FILL_V, FILL_W, FILL_H);
        c.add(lowerRight - STEP / 2, bandY, BAND_RIGHT_STEP_U, BAND_V, STEP, EDGE);
        c.repeat(lowerRight + STEP / 2, bandY, w - EDGE - lowerRight - STEP / 2, EDGE, BAND_RIGHT_U, BAND_V, BAND_RIGHT_LEN, EDGE);
        c.add(w - EDGE, bandY, BAND_RIGHT_CORNER_U, BAND_V, EDGE, EDGE);

        c.repeat(lowerX, upper, EDGE, bottomY - upper, LEFT_U, LEFT_V, EDGE, LEFT_LEN);
        c.repeat(lowerRight - EDGE, upper, EDGE, bottomY - upper, RIGHT_U, RIGHT_V, EDGE, RIGHT_LEN);
        c.repeat(lowerX + EDGE, upper, lowerRight - lowerX - 2 * EDGE, bottomY - upper, FILL_U, FILL_V, FILL_W, FILL_H);
        c.add(lowerX, bottomY, BOTTOM_LEFT_U, BOTTOM_LEFT_V, EDGE, EDGE);
        c.repeat(lowerX + EDGE, bottomY, lowerRight - lowerX - 2 * EDGE, EDGE, BOTTOM_U, BOTTOM_V, BOTTOM_LEN, EDGE);
        c.add(lowerRight - EDGE, bottomY, BOTTOM_RIGHT_U, BOTTOM_RIGHT_V, EDGE, EDGE);
    }

    private void paintSingle(BackgroundComposer c, WidgetLayout l)
    {
        int w = l.width();
        int h = l.height();
        c.add(0, 0, TOP_LEFT_U, TOP_LEFT_V, EDGE, EDGE);
        c.repeat(EDGE, 0, w - 2 * EDGE, EDGE, TOP_U, TOP_V, TOP_LEN, EDGE);
        c.add(w - EDGE, 0, TOP_RIGHT_U, TOP_RIGHT_V, EDGE, EDGE);
        c.repeat(0, EDGE, EDGE, h - 2 * EDGE, LEFT_U, LEFT_V, EDGE, LEFT_LEN);
        c.repeat(w - EDGE, EDGE, EDGE, h - 2 * EDGE, RIGHT_U, RIGHT_V, EDGE, RIGHT_LEN);
        c.repeat(EDGE, EDGE, w - 2 * EDGE, h - 2 * EDGE, FILL_U, FILL_V, FILL_W, FILL_H);
        c.add(0, h - EDGE, BOTTOM_LEFT_U, BOTTOM_LEFT_V, EDGE, EDGE);
        c.repeat(EDGE, h - EDGE, w - 2 * EDGE, EDGE, BOTTOM_U, BOTTOM_V, BOTTOM_LEN, EDGE);
        c.add(w - EDGE, h - EDGE, BOTTOM_RIGHT_U, BOTTOM_RIGHT_V, EDGE, EDGE);
    }
}
