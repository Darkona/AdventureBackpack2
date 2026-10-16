package com.darkona.adventurebackpack.common;

public final class ShownKey
{
    private Object source;
    private int number;
    private Object extra;
    private boolean known;

    public boolean changed(Object source, int number, Object extra)
    {
        if (known && source == this.source && number == this.number && extra == this.extra) return false;
        this.source = source;
        this.number = number;
        this.extra = extra;
        known = true;
        return true;
    }

    public void reset()
    {
        known = false;
        source = extra = null;
    }
}
