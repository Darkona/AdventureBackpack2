package com.darkona.adventurebackpack.client.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * campFire - Darkona
 * Created using Tabula 4.1.0
 */
public class ModelCampFire extends ModelBase
{
    public ModelRenderer rock1;
    public ModelRenderer rock2;
    public ModelRenderer rock3;
    public ModelRenderer rock4;
    public ModelRenderer rock5;
    public ModelRenderer rock6;
    public ModelRenderer rock7;
    public ModelRenderer rock8;
    public ModelRenderer rock9;
    public ModelRenderer rock10;
    public ModelRenderer rock11;
    public ModelRenderer rock12;
    public ModelRenderer rock13;
    public ModelRenderer rock14;
    public ModelRenderer rock15;
    public ModelRenderer rock16;
    public ModelRenderer rock17;
    public ModelRenderer stick1;
    public ModelRenderer stick2;
    public ModelRenderer stick3;
    public ModelRenderer stick4;
    public ModelRenderer log;

    public ModelCampFire()
    {
        this.textureWidth = 32;
        this.textureHeight = 32;
        this.rock1 = new ModelRenderer(this, 0, 8);
        this.rock1.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock1.addBox(-1.0F, -2.0F, 4.0F, 2, 2, 2, 0.0F);
        this.rock8 = new ModelRenderer(this, 0, 8);
        this.rock8.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock8.addBox(-2.0F, -2.0F, -6.0F, 2, 2, 2, 0.0F);
        this.log = new ModelRenderer(this, 6, 19);
        this.log.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.log.addBox(0.0F, -3.0F, -2.0F, 3, 3, 10, 0.0F);
        this.setRotateAngle(log, 0.36425021489121656F, 0.0F, 0.0F);
        this.rock4 = new ModelRenderer(this, 0, 8);
        this.rock4.setRotationPoint(1.0F, 23.0F, 1.0F);
        this.rock4.addBox(4.0F, -1.0F, -2.0F, 2, 2, 2, 0.0F);
        this.stick4 = new ModelRenderer(this, 0, 17);
        this.stick4.setRotationPoint(-5.0F, 24.0F, -1.0F);
        this.stick4.addBox(-0.5F, -14.0F, -0.5F, 1, 14, 1, 0.0F);
        this.setRotateAngle(stick4, 0.5009094953223726F, -1.9123572614101867F, 0.0F);
        this.rock15 = new ModelRenderer(this, 0, 0);
        this.rock15.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock15.addBox(6.0F, -3.0F, 1.0F, 1, 1, 1, 0.0F);
        this.stick2 = new ModelRenderer(this, 0, 17);
        this.stick2.setRotationPoint(6.0F, 22.0F, 1.0F);
        this.stick2.addBox(-0.5F, -14.0F, -0.5F, 1, 14, 1, 0.0F);
        this.setRotateAngle(stick2, 0.40980330836826856F, 1.5481070465189704F, 0.0F);
        this.rock7 = new ModelRenderer(this, 10, 7);
        this.rock7.setRotationPoint(0.0F, 23.0F, 0.0F);
        this.rock7.addBox(-5.0F, -2.0F, -5.0F, 3, 3, 3, 0.0F);
        this.rock16 = new ModelRenderer(this, 0, 0);
        this.rock16.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock16.addBox(-5.0F, -1.0F, -2.0F, 1, 1, 1, 0.0F);
        this.rock5 = new ModelRenderer(this, 0, 8);
        this.rock5.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock5.addBox(-6.0F, -2.0F, -1.0F, 2, 2, 2, 0.0F);
        this.rock13 = new ModelRenderer(this, 0, 8);
        this.rock13.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock13.addBox(3.0F, -2.0F, -5.0F, 2, 2, 2, 0.0F);
        this.rock6 = new ModelRenderer(this, 0, 0);
        this.rock6.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock6.addBox(2.0F, -1.0F, -6.0F, 1, 1, 1, 0.0F);
        this.stick1 = new ModelRenderer(this, 0, 17);
        this.stick1.setRotationPoint(-3.0F, 24.0F, 5.0F);
        this.stick1.addBox(-0.5F, -14.0F, -0.5F, 1, 14, 1, 0.0F);
        this.setRotateAngle(stick1, 0.40980330836826856F, 0.7285004297824331F, 0.6373942428283291F);
        this.rock3 = new ModelRenderer(this, 0, 8);
        this.rock3.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock3.addBox(-3.0F, -2.0F, 2.0F, 2, 2, 2, 0.0F);
        this.rock10 = new ModelRenderer(this, 0, 8);
        this.rock10.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock10.addBox(0.0F, -2.0F, -7.0F, 2, 2, 2, 0.0F);
        this.rock11 = new ModelRenderer(this, 0, 8);
        this.rock11.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock11.addBox(4.0F, -2.0F, 1.0F, 2, 2, 2, 0.0F);
        this.rock12 = new ModelRenderer(this, 10, 7);
        this.rock12.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock12.addBox(1.0F, -3.0F, 3.0F, 3, 3, 3, 0.0F);
        this.stick3 = new ModelRenderer(this, 0, 17);
        this.stick3.setRotationPoint(4.0F, 24.0F, -6.0F);
        this.stick3.addBox(-0.5F, -14.0F, -0.5F, 1, 14, 1, 0.0F);
        this.setRotateAngle(stick3, -0.5462880558742251F, -0.36425021489121656F, 0.0F);
        this.rock17 = new ModelRenderer(this, 0, 0);
        this.rock17.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock17.addBox(4.0F, -1.0F, 3.0F, 1, 1, 1, 0.0F);
        this.rock9 = new ModelRenderer(this, 0, 8);
        this.rock9.setRotationPoint(-1.0F, 24.0F, 1.0F);
        this.rock9.addBox(-4.0F, -2.0F, 0.0F, 2, 2, 2, 0.0F);
        this.rock14 = new ModelRenderer(this, 0, 8);
        this.rock14.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock14.addBox(4.0F, -2.0F, -3.0F, 2, 2, 2, 0.0F);
        this.rock2 = new ModelRenderer(this, 0, 0);
        this.rock2.setRotationPoint(0.0F, 24.0F, 0.0F);
        this.rock2.addBox(-2.0F, -1.0F, 4.0F, 1, 1, 1, 0.0F);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        this.rock1.render(f5);
        this.rock8.render(f5);
        this.log.render(f5);
        this.rock4.render(f5);
        this.stick4.render(f5);
        this.rock15.render(f5);
        this.stick2.render(f5);
        this.rock7.render(f5);
        this.rock16.render(f5);
        this.rock5.render(f5);
        this.rock13.render(f5);
        this.rock6.render(f5);
        this.stick1.render(f5);
        this.rock3.render(f5);
        this.rock10.render(f5);
        this.rock11.render(f5);
        this.rock12.render(f5);
        this.stick3.render(f5);
        this.rock17.render(f5);
        this.rock9.render(f5);
        this.rock14.render(f5);
        this.rock2.render(f5);
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
