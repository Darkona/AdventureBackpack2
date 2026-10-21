package com.darkona.adventurebackpack.common.layout;

import java.util.Arrays;

public final class WidgetLayout
{
    public static final int NONE = -1;

    private final int count;
    private final int[] kind;
    private final int[] param;
    private final int[] x;
    private final int[] y;
    private final int[] w;
    private final int[] h;
    private final int[] slotFirst;
    private final int[] slots;
    private final boolean[] owned;
    private final int slotCount;
    private final int[] slotX;
    private final int[] slotY;
    private final int[] slotInv;
    private final int[] slotWidget;
    private final int inventorySize;
    private final int[] widgetAt;
    private final int[] slotAt;
    private final int width;
    private final int height;
    private final int upperHeight;
    private final int lowerX;
    private final int lowerWidth;
    private final int rowSize;

    private WidgetLayout(Builder b)
    {
        count = b.count;
        kind = Arrays.copyOf(b.kind, count);
        param = Arrays.copyOf(b.param, count);
        x = Arrays.copyOf(b.x, count);
        y = Arrays.copyOf(b.y, count);
        w = Arrays.copyOf(b.w, count);
        h = Arrays.copyOf(b.h, count);
        slotFirst = Arrays.copyOf(b.slotFirst, count);
        slots = Arrays.copyOf(b.slots, count);
        owned = Arrays.copyOf(b.owned, count);
        slotCount = b.slotCount;
        slotX = Arrays.copyOf(b.slotX, slotCount);
        slotY = Arrays.copyOf(b.slotY, slotCount);
        slotInv = Arrays.copyOf(b.slotInv, slotCount);
        slotWidget = Arrays.copyOf(b.slotWidget, slotCount);
        inventorySize = b.inventorySize;
        widgetAt = new int[inventorySize];
        slotAt = new int[inventorySize];
        Arrays.fill(widgetAt, NONE);
        Arrays.fill(slotAt, NONE);
        for (int s = 0; s < slotCount; s++)
        {
            int inv = slotInv[s];
            if (owned[slotWidget[s]] && inv >= 0 && inv < inventorySize)
            {
                widgetAt[inv] = slotWidget[s];
                slotAt[inv] = s;
            }
        }
        width = b.width;
        height = b.height;
        upperHeight = b.upperHeight;
        lowerX = b.lowerX;
        lowerWidth = b.lowerWidth;
        rowSize = b.rowSize;
    }

    public int count()
    {
        return count;
    }

    public int kind(int widget)
    {
        return kind[widget];
    }

    public int param(int widget)
    {
        return param[widget];
    }

    public int x(int widget)
    {
        return x[widget];
    }

    public int y(int widget)
    {
        return y[widget];
    }

    public int w(int widget)
    {
        return w[widget];
    }

    public int h(int widget)
    {
        return h[widget];
    }

    public int slotFirst(int widget)
    {
        return slotFirst[widget];
    }

    public int slots(int widget)
    {
        return slots[widget];
    }

    public int invIndex(int widget, int n)
    {
        return slotInv[slotFirst[widget] + n];
    }

    public int rowSize()
    {
        return rowSize;
    }

    public boolean ownsInventory(int widget)
    {
        return owned[widget];
    }

    public int slotCount()
    {
        return slotCount;
    }

    public int slotX(int slot)
    {
        return slotX[slot];
    }

    public int slotY(int slot)
    {
        return slotY[slot];
    }

    public int slotInv(int slot)
    {
        return slotInv[slot];
    }

    public int slotWidget(int slot)
    {
        return slotWidget[slot];
    }

    public int inventorySize()
    {
        return inventorySize;
    }

    public int widgetAt(int inventoryIndex)
    {
        return inventoryIndex < 0 || inventoryIndex >= inventorySize ? NONE : widgetAt[inventoryIndex];
    }

    public int slotAt(int inventoryIndex)
    {
        return inventoryIndex < 0 || inventoryIndex >= inventorySize ? NONE : slotAt[inventoryIndex];
    }

    public int kindAt(int inventoryIndex)
    {
        int widget = widgetAt(inventoryIndex);
        return widget == NONE ? NONE : kind[widget];
    }

    public int first(int widgetKind)
    {
        for (int i = 0; i < count; i++)
        {
            if (kind[i] == widgetKind) return i;
        }
        return NONE;
    }

    public int widgetOf(int widgetKind, int n)
    {
        for (int i = 0; i < count; i++)
        {
            if (kind[i] == widgetKind && n-- == 0) return i;
        }
        return NONE;
    }

    public int countOf(int widgetKind)
    {
        int n = 0;
        for (int i = 0; i < count; i++)
        {
            if (kind[i] == widgetKind) n++;
        }
        return n;
    }

    public int slotsOf(int widgetKind)
    {
        int n = 0;
        for (int i = 0; i < count; i++)
        {
            if (kind[i] == widgetKind) n += slots[i];
        }
        return n;
    }

    public int width()
    {
        return width;
    }

    public int height()
    {
        return height;
    }

    public int upperHeight()
    {
        return upperHeight;
    }

    public int lowerX()
    {
        return lowerX;
    }

    public int lowerWidth()
    {
        return lowerWidth;
    }

    public static final class Builder
    {
        private int count;
        private int[] kind = new int[16];
        private int[] param = new int[16];
        private int[] x = new int[16];
        private int[] y = new int[16];
        private int[] w = new int[16];
        private int[] h = new int[16];
        private int[] slotFirst = new int[16];
        private int[] slots = new int[16];
        private boolean[] owned = new boolean[16];
        private boolean[] fit = new boolean[16];
        private int slotCount;
        private int[] slotX = new int[64];
        private int[] slotY = new int[64];
        private int[] slotInv = new int[64];
        private int[] slotWidget = new int[64];
        private int inventorySize;
        private int width;
        private int height;
        private int upperHeight;
        private int lowerX;
        private int lowerWidth;
        private int rowSize = 9;

        public Builder rowSize(int size)
        {
            rowSize = size;
            return this;
        }

        public Builder window(int width, int height, int upperHeight, int lowerX, int lowerWidth)
        {
            this.width = width;
            this.height = height;
            this.upperHeight = upperHeight;
            this.lowerX = lowerX;
            this.lowerWidth = lowerWidth;
            return this;
        }

        public Builder inventorySize(int size)
        {
            inventorySize = size;
            return this;
        }

        public int widget(int widgetKind, int widgetParam, boolean ownsInventory)
        {
            return widget(widgetKind, widgetParam, 0, 0, 0, 0, ownsInventory, true);
        }

        public int widget(int widgetKind, int widgetParam, int wx, int wy, int ww, int wh, boolean ownsInventory)
        {
            return widget(widgetKind, widgetParam, wx, wy, ww, wh, ownsInventory, false);
        }

        private int widget(int widgetKind, int widgetParam, int wx, int wy, int ww, int wh, boolean ownsInventory, boolean fitSlots)
        {
            if (count == kind.length)
            {
                int n = count * 2;
                kind = Arrays.copyOf(kind, n);
                param = Arrays.copyOf(param, n);
                x = Arrays.copyOf(x, n);
                y = Arrays.copyOf(y, n);
                w = Arrays.copyOf(w, n);
                h = Arrays.copyOf(h, n);
                slotFirst = Arrays.copyOf(slotFirst, n);
                slots = Arrays.copyOf(slots, n);
                owned = Arrays.copyOf(owned, n);
                fit = Arrays.copyOf(fit, n);
            }
            kind[count] = widgetKind;
            param[count] = widgetParam;
            x[count] = wx;
            y[count] = wy;
            w[count] = ww;
            h[count] = wh;
            slotFirst[count] = slotCount;
            slots[count] = 0;
            owned[count] = ownsInventory;
            fit[count] = fitSlots;
            return count++;
        }

        public Builder slot(int sx, int sy, int inventoryIndex)
        {
            if (count == 0) throw new IllegalStateException("a slot needs a widget");
            if (slotCount == slotX.length)
            {
                int n = slotCount * 2;
                slotX = Arrays.copyOf(slotX, n);
                slotY = Arrays.copyOf(slotY, n);
                slotInv = Arrays.copyOf(slotInv, n);
                slotWidget = Arrays.copyOf(slotWidget, n);
            }
            int widget = count - 1;
            slotX[slotCount] = sx;
            slotY[slotCount] = sy;
            slotInv[slotCount] = inventoryIndex;
            slotWidget[slotCount] = widget;
            slotCount++;
            slots[widget]++;
            return this;
        }

        public Builder shiftAll(int dx)
        {
            if (dx == 0) return this;
            for (int i = 0; i < count; i++) x[i] += dx;
            for (int s = 0; s < slotCount; s++) slotX[s] += dx;
            width += dx;
            lowerX += dx;
            return this;
        }

        public WidgetLayout build()
        {
            for (int i = 0; i < count; i++)
            {
                if (!fit[i] || slots[i] == 0) continue;
                int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
                for (int s = slotFirst[i]; s < slotFirst[i] + slots[i]; s++)
                {
                    minX = Math.min(minX, slotX[s]);
                    minY = Math.min(minY, slotY[s]);
                    maxX = Math.max(maxX, slotX[s] + 16);
                    maxY = Math.max(maxY, slotY[s] + 16);
                }
                x[i] = minX;
                y[i] = minY;
                w[i] = maxX - minX;
                h[i] = maxY - minY;
            }
            return new WidgetLayout(this);
        }
    }
}
