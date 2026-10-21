package com.darkona.adventurebackpack.common;

public final class Tint
{
    public static final int WHITE = 0xFFFFFF;

    private Tint()
    {
    }

    public static float red(int rgb)
    {
        return (rgb >> 16 & 0xFF) / 255f;
    }

    public static float green(int rgb)
    {
        return (rgb >> 8 & 0xFF) / 255f;
    }

    public static float blue(int rgb)
    {
        return (rgb & 0xFF) / 255f;
    }

    public static int scale(int rgb, float factor)
    {
        return channel(rgb >> 16, factor) << 16 | channel(rgb >> 8, factor) << 8 | channel(rgb, factor);
    }

    public static int multiply(int a, int b)
    {
        int r = (a >> 16 & 0xFF) * (b >> 16 & 0xFF) / 255;
        int g = (a >> 8 & 0xFF) * (b >> 8 & 0xFF) / 255;
        int bl = (a & 0xFF) * (b & 0xFF) / 255;
        return r << 16 | g << 8 | bl;
    }

    private static int channel(int c, float factor)
    {
        int v = Math.round((c & 0xFF) * factor);
        return v < 0 ? 0 : v > 255 ? 255 : v;
    }
}
