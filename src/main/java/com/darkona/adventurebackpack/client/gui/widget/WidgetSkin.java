package com.darkona.adventurebackpack.client.gui.widget;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;

public class WidgetSkin
{
    public static final int DEFAULT_LAYER = 75;

    public int layer()
    {
        return DEFAULT_LAYER;
    }

    public void paint(BackgroundComposer c, WidgetLayout layout, int widget)
    {
    }

    public void paintOver(BackgroundComposer c, WidgetLayout layout, int widget)
    {
    }

    public String emptyTip()
    {
        return null;
    }
}
