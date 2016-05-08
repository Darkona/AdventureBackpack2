package com.jadarstudios.developercapes.cape;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

/**
 * @author jadar
 */
public interface ICape {

    public String getName();

    public ITextureObject getTexture();

    public ResourceLocation getLocation();

    public void loadTexture(AbstractClientPlayer player);

    public boolean isTextureLoaded(AbstractClientPlayer player);
}
