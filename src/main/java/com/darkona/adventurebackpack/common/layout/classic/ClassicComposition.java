package com.darkona.adventurebackpack.common.layout.classic;

import static com.darkona.adventurebackpack.common.layout.classic.ClassicWidgets.*;

import com.darkona.adventurebackpack.common.layout.BackpackSpec;

public final class ClassicComposition
{
    private ClassicComposition()
    {
    }

    private static final int[] NONE = new int[0];

    public static BackpackSpec of(int rows, int columns, int quiver)
    {
        return of(rows, columns, quiver, NONE, NONE);
    }

    public static BackpackSpec of(int rows, int columns, int quiver, int[] accessories, int[] accessorySlots)
    {
        int r = ClassicArrangement.clamp(rows, ClassicArrangement.MIN_ROWS, ClassicArrangement.MAX_ROWS);
        int c = ClassicArrangement.clamp(columns, ClassicArrangement.MIN_COLUMNS, ClassicArrangement.MAX_COLUMNS);
        int q = ClassicArrangement.quiverFor(r, quiver);
        int n = 13 + accessories.length;
        int[] kinds = new int[n];
        int[] params = new int[n];
        int i = 0;
        kinds[i] = STORAGE;
        params[i++] = c;
        kinds[i++] = TOOLS;
        kinds[i++] = TANK;
        if (q > 0)
        {
            kinds[i] = QUIVER;
            params[i++] = q;
        } else
        {
            kinds[i] = TANK;
            params[i++] = 1;
        }
        kinds[i++] = ROPE;
        for (int a = 0; a < accessories.length; a++)
        {
            kinds[i] = accessories[a];
            params[i++] = accessorySlots[a];
        }
        kinds[i++] = CRAFT;
        kinds[i++] = RESULT;
        kinds[i++] = PLAYER;
        kinds[i++] = HOTBAR;
        kinds[i++] = BUTTON;
        kinds[i++] = TOGGLE;
        kinds[i++] = SORT;
        kinds[i++] = LOCK;
        return new BackpackSpec(r, c, kinds, params);
    }
}
