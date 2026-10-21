package com.darkona.adventurebackpack.client.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelTelescope extends ModelBase
{
    private final ModelRenderer tube;

    public ModelTelescope()
    {
        textureWidth = 32;
        textureHeight = 16;
        tube = new ModelRenderer(this, 0, 0);
        tube.addBox(-2F, -2F, -6F, 4, 4, 12);
        tube.setRotationPoint(0F, 0F, 0F);
    }

    public void render(float scale)
    {
        tube.render(scale);
    }
}
