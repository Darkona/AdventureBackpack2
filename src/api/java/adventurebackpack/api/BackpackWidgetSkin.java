package adventurebackpack.api;

import net.minecraft.util.ResourceLocation;

public final class BackpackWidgetSkin
{
    public static final int WHITE = 0xFFFFFFFF;

    private final ResourceLocation texture;
    private final int textureSize;
    private final int frameU;
    private final int frameV;
    private final int tint;
    private final String emptyTip;

    public BackpackWidgetSkin(ResourceLocation texture, int textureSize, int frameU, int frameV, int tint, String emptyTip)
    {
        if (texture == null) throw new IllegalArgumentException("a widget skin needs a texture");
        if (textureSize <= 0) throw new IllegalArgumentException("texture size " + textureSize);
        this.texture = texture;
        this.textureSize = textureSize;
        this.frameU = frameU;
        this.frameV = frameV;
        this.tint = tint;
        this.emptyTip = emptyTip;
    }

    public ResourceLocation getTexture()
    {
        return texture;
    }

    public int getTextureSize()
    {
        return textureSize;
    }

    public int getFrameU()
    {
        return frameU;
    }

    public int getFrameV()
    {
        return frameV;
    }

    public int getTint()
    {
        return tint;
    }

    public String getEmptyTip()
    {
        return emptyTip;
    }
}
