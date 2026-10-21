package com.darkona.adventurebackpack.inventory.widget.classic;

import com.darkona.adventurebackpack.inventory.widget.WidgetType;

public final class ControlWidget extends WidgetType
{
    private final Control control;

    public ControlWidget(Control control)
    {
        this.control = control;
    }

    @Override
    public Control control()
    {
        return control;
    }
}
