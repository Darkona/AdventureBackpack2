package com.darkona.adventurebackpack.client.models;

import com.darkona.adventurebackpack.common.QuiverRules;
import com.darkona.adventurebackpack.util.Resources;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public final class QuiverParts
{
    private static final ResourceLocation TEXTURE = Resources.modelTextures("quiver");
    private static final int TEXTURE_SIZE = 32;
    private static final float[][] ARROWS = {
            {1.5f, 1.5f, 6f},
            {0.5f, 0.5f, 5f},
            {2.5f, 2.5f, 5f},
            {2.5f, 0.5f, 4f},
            {0.5f, 2.5f, 4f},
    };

    public final ModelRenderer body;
    private final ModelRenderer[] arrows = new ModelRenderer[QuiverRules.MAX_ARROWS_DRAWN];

    public QuiverParts(ModelBase model, float x, float y, float z)
    {
        body = new ModelRenderer(model, 0, 0).setTextureSize(TEXTURE_SIZE, TEXTURE_SIZE);
        body.setRotationPoint(x, y, z);
        body.addBox(0f, 0f, 0f, 4, 10, 4);
        for (int i = 0; i < arrows.length; i++)
        {
            float[] a = ARROWS[i];
            int h = (int) a[2];
            ModelRenderer shaft = new ModelRenderer(model, 0, 16).setTextureSize(TEXTURE_SIZE, TEXTURE_SIZE);
            shaft.setRotationPoint(a[0], 0f, a[1]);
            shaft.addBox(0f, -h, 0f, 1, h, 1);
            ModelRenderer fletchA = new ModelRenderer(model, 8, 16).setTextureSize(TEXTURE_SIZE, TEXTURE_SIZE);
            fletchA.addBox(-1f, -h, 0.5f, 3, 3, 0);
            shaft.addChild(fletchA);
            ModelRenderer fletchB = new ModelRenderer(model, 8, 20).setTextureSize(TEXTURE_SIZE, TEXTURE_SIZE);
            fletchB.addBox(0.5f, -h, -1f, 0, 3, 3);
            shaft.addChild(fletchB);
            body.addChild(shaft);
            arrows[i] = shaft;
        }
    }

    public void render(float scale, int arrowCount)
    {
        for (int i = 0; i < arrows.length; i++)
        {
            arrows[i].showModel = i < arrowCount;
        }
        Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
        body.render(scale);
    }
}
