package com.darkona.adventurebackpack.common.layout.gear;

import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.BUTTON;
import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.HOTBAR;
import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.PLAYER;
import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.TANK;
import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.TOGGLE;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;
import com.darkona.adventurebackpack.common.layout.classic.ClassicArrangement;

public final class GearArrangement
{
    public static final int WIDTH = 176;
    public static final int HEIGHT = 166;
    public static final int UPPER = 80;
    public static final int SLOT = ClassicArrangement.SLOT;
    public static final int HEADER = ClassicArrangement.HEADER;
    public static final int TANK_HEIGHT = 64;
    public static final int BUCKET_X = 8;
    public static final int BUCKET_OUT_DY = 30;
    public static final int TANK_X = 27;
    public static final int RIGHT_TANK_X = 151;
    public static final int BUTTON_X = 7;
    public static final int BUTTON_Y = 57;
    public static final int PLAYER_X = 8;
    public static final int PLAYER_Y = 84;
    public static final int HOTBAR_Y = 142;
    public static final int TOGGLE_X = 96;
    public static final int TOGGLE_Y = 61;
    public static final int TOGGLE_W = 18;
    public static final int TOGGLE_H = 12;
    public static final int BAR_W = 8;
    public static final int TEMPERATURE_X = 136;
    public static final int FUEL_X = 50;
    public static final int FUEL_Y = 57;
    public static final int BURN_X = 72;
    public static final int BURN_Y = 57;
    public static final int BURN_W = 6;
    public static final int BURN_H = 16;

    private GearArrangement()
    {
    }

    public static WidgetLayout jetpack()
    {
        WidgetLayout.Builder b = start(3);
        leftTank(b, TANK, 0);
        rightTank(b, TANK, 1);
        b.widget(GearWidgets.FUEL_SLOT, 0, FUEL_X, FUEL_Y, SLOT - 2, SLOT - 2, true);
        b.slot(FUEL_X, FUEL_Y, 2);
        bar(b, GearWidgets.BAR_TEMPERATURE, TEMPERATURE_X, HEADER, BAR_W, TANK_HEIGHT);
        bar(b, GearWidgets.BAR_BURN, BURN_X, BURN_Y, BURN_W, BURN_H);
        controls(b, true);
        return finish(b);
    }

    public static WidgetLayout copter()
    {
        WidgetLayout.Builder b = start(2);
        leftTank(b, TANK, 0);
        controls(b, true);
        return finish(b);
    }

    public static WidgetLayout flamethrower()
    {
        WidgetLayout.Builder b = start(2);
        leftTank(b, GearWidgets.FUEL_TANK, 0);
        rightTank(b, GearWidgets.FUEL_TANK, 1);
        controls(b, true);
        return finish(b);
    }

    public static WidgetLayout scuba()
    {
        WidgetLayout.Builder b = start(2);
        leftTank(b, GearWidgets.AIR_TANK, 0);
        rightTank(b, GearWidgets.AIR_TANK, 1);
        controls(b, false);
        return finish(b);
    }

    private static WidgetLayout.Builder start(int inventorySize)
    {
        return new WidgetLayout.Builder().window(WIDTH, HEIGHT, UPPER, 0, WIDTH).rowSize(9).inventorySize(inventorySize);
    }

    private static void leftTank(WidgetLayout.Builder b, int kind, int param)
    {
        b.widget(kind, param, TANK_X, HEADER, ClassicArrangement.TANK_WIDTH, TANK_HEIGHT, true);
        b.slot(BUCKET_X, HEADER, 0);
        b.slot(BUCKET_X, HEADER + BUCKET_OUT_DY, 1);
    }

    private static void rightTank(WidgetLayout.Builder b, int kind, int param)
    {
        b.widget(kind, param, RIGHT_TANK_X, HEADER, ClassicArrangement.TANK_WIDTH, TANK_HEIGHT, true);
    }

    private static void bar(WidgetLayout.Builder b, int id, int x, int y, int w, int h)
    {
        b.widget(GearWidgets.BAR, id, x, y, w, h, false);
    }

    private static void controls(WidgetLayout.Builder b, boolean toggle)
    {
        b.widget(BUTTON, 0, BUTTON_X, BUTTON_Y, ClassicArrangement.BUTTON_SIZE, ClassicArrangement.BUTTON_SIZE, false);
        if (toggle) b.widget(TOGGLE, 0, TOGGLE_X, TOGGLE_Y, TOGGLE_W, TOGGLE_H, false);
    }

    private static WidgetLayout finish(WidgetLayout.Builder b)
    {
        b.widget(PLAYER, 0, false);
        for (int c = 0; c < 27; c++) b.slot(PLAYER_X + SLOT * (c % 9), PLAYER_Y + SLOT * (c / 9), 9 + c);
        b.widget(HOTBAR, 0, false);
        for (int c = 0; c < 9; c++) b.slot(PLAYER_X + SLOT * c, HOTBAR_Y, c);
        return b.build();
    }
}
