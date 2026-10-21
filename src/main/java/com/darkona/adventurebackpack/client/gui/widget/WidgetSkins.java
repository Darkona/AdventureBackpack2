package com.darkona.adventurebackpack.client.gui.widget;

import net.minecraft.util.ResourceLocation;

import java.util.Arrays;

public final class WidgetSkins
{
    public static final int NEUTRAL_FRAME_U = 0;
    public static final int NEUTRAL_FRAME_V = 18;
    public static final int NEUTRAL_TINT = 0xFFFFFFFF;

    private static volatile WidgetSkin[] skins = new WidgetSkin[32];
    private static volatile ResourceLocation[] textures = new ResourceLocation[0];
    private static volatile int[] textureSizes = new int[0];
    private static volatile WindowSkin window = new WindowSkin();
    private static volatile WidgetSkin fallback = new WidgetSkin();

    private WidgetSkins()
    {
    }

    public static synchronized int texture(ResourceLocation location, int size)
    {
        for (int i = 0; i < textures.length; i++)
        {
            if (textures[i].equals(location)) return i;
        }
        ResourceLocation[] t = Arrays.copyOf(textures, textures.length + 1);
        int[] s = Arrays.copyOf(textureSizes, textureSizes.length + 1);
        t[t.length - 1] = location;
        s[s.length - 1] = size;
        textureSizes = s;
        textures = t;
        return t.length - 1;
    }

    public static int textureCount()
    {
        return textures.length;
    }

    public static ResourceLocation textureAt(int index)
    {
        return textures[index];
    }

    public static int textureSize(int index)
    {
        return textureSizes[index];
    }

    public static synchronized void register(int kind, WidgetSkin skin)
    {
        WidgetSkin[] next = Arrays.copyOf(skins, Math.max(skins.length, kind + 1));
        next[kind] = skin;
        skins = next;
    }

    public static void window(WindowSkin skin)
    {
        window = skin;
    }

    public static void fallback(WidgetSkin skin)
    {
        fallback = skin;
    }

    public static WindowSkin window()
    {
        return window;
    }

    public static WidgetSkin get(int kind)
    {
        WidgetSkin[] s = skins;
        WidgetSkin skin = kind >= 0 && kind < s.length ? s[kind] : null;
        return skin == null ? fallback : skin;
    }
}
