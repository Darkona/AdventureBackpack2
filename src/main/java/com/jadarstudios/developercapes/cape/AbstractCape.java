package com.jadarstudios.developercapes.cape;

import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;

/**
 * @author jadar
 */
public abstract class AbstractCape implements ICape {
    protected String name;
    protected ITextureObject texture;
    protected ResourceLocation location;

    public AbstractCape(String name) {
        this.name = name;
    }

    public AbstractCape() {
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ITextureObject getTexture() {
        return this.texture;
    }

    @Override
    public ResourceLocation getLocation() {
        return this.location;
    }

}
