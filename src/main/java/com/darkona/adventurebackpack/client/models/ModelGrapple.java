package com.darkona.adventurebackpack.client.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelGrapple extends ModelBase
{
    private final ModelRenderer eye;
    private final ModelRenderer shank;
    private final ModelRenderer tooth0;
    private final ModelRenderer tooth1;
    private final ModelRenderer tooth2;
    private final ModelRenderer tooth3;

    public ModelGrapple()
    {
        textureWidth = 32;
        textureHeight = 32;
        ModelRenderer eye = new ModelRenderer(this, 0, 0);
        eye.addBox(-5.0F, -1.0F, -1.0F, 1, 2, 2, 0.0F);
        ModelRenderer shank = new ModelRenderer(this, 6, 0);
        shank.addBox(-4.0F, -0.5F, -0.5F, 6, 1, 1, 0.0F);
        ModelRenderer tooth0 = new ModelRenderer(this, 20, 0);
        tooth0.setRotationPoint(2.0F, 0.0F, 0.0F);
        tooth0.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(tooth0, 0.0F, 0.0F, 0.6981F);
        ModelRenderer barb0 = new ModelRenderer(this, 6, 2);
        barb0.setRotationPoint(3.0F, 0.0F, 0.0F);
        barb0.addBox(0.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(barb0, 0.0F, 0.0F, 0.9599F);
        ModelRenderer tooth1 = new ModelRenderer(this, 20, 0);
        tooth1.setRotationPoint(2.0F, 0.0F, 0.0F);
        tooth1.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(tooth1, 0.0F, 0.0F, -0.6981F);
        ModelRenderer barb1 = new ModelRenderer(this, 6, 2);
        barb1.setRotationPoint(3.0F, 0.0F, 0.0F);
        barb1.addBox(0.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(barb1, 0.0F, 0.0F, -0.9599F);
        ModelRenderer tooth2 = new ModelRenderer(this, 20, 0);
        tooth2.setRotationPoint(2.0F, 0.0F, 0.0F);
        tooth2.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(tooth2, 0.0F, 0.6981F, 0.0F);
        ModelRenderer barb2 = new ModelRenderer(this, 6, 2);
        barb2.setRotationPoint(3.0F, 0.0F, 0.0F);
        barb2.addBox(0.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(barb2, 0.0F, 0.9599F, 0.0F);
        ModelRenderer tooth3 = new ModelRenderer(this, 20, 0);
        tooth3.setRotationPoint(2.0F, 0.0F, 0.0F);
        tooth3.addBox(0.0F, -0.5F, -0.5F, 3, 1, 1, 0.0F);
        setRotateAngle(tooth3, 0.0F, -0.6981F, 0.0F);
        ModelRenderer barb3 = new ModelRenderer(this, 6, 2);
        barb3.setRotationPoint(3.0F, 0.0F, 0.0F);
        barb3.addBox(0.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        setRotateAngle(barb3, 0.0F, -0.9599F, 0.0F);
        tooth0.addChild(barb0);
        tooth1.addChild(barb1);
        tooth2.addChild(barb2);
        tooth3.addChild(barb3);
        this.eye = eye;
        this.shank = shank;
        this.tooth0 = tooth0;
        this.tooth1 = tooth1;
        this.tooth2 = tooth2;
        this.tooth3 = tooth3;
    }

    public void render(float scale)
    {
        eye.render(scale);
        shank.render(scale);
        tooth0.render(scale);
        tooth1.render(scale);
        tooth2.render(scale);
        tooth3.render(scale);
    }

    private static void setRotateAngle(ModelRenderer part, float x, float y, float z)
    {
        part.rotateAngleX = x;
        part.rotateAngleY = y;
        part.rotateAngleZ = z;
    }
}
