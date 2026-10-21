package com.darkona.adventurebackpack.client.gui.widget;

import com.darkona.adventurebackpack.common.layout.WidgetLayout;

public class FrameSkin extends WidgetSkin
{
    public static final int SIZE = 18;

    private final int texture;
    private final int u;
    private final int v;
    private final int argb;
    private final int layer;
    private final String tip;

    public FrameSkin(int texture, int u, int v, int argb, int layer, String tip)
    {
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.argb = argb;
        this.layer = layer;
        this.tip = tip;
    }

    @Override
    public int layer()
    {
        return layer;
    }

    @Override
    public void paint(BackgroundComposer c, WidgetLayout layout, int widget)
    {
        c.use(texture, argb);
        int first = layout.slotFirst(widget);
        for (int s = first; s < first + layout.slots(widget); s++)
        {
            c.add(layout.slotX(s) - 1, layout.slotY(s) - 1, u, v, SIZE, SIZE);
        }
    }

    @Override
    public String emptyTip()
    {
        return tip;
    }
}
