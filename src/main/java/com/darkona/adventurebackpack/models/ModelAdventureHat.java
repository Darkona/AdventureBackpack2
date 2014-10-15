package com.darkona.adventurebackpack.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelAdventureHat extends ModelBiped {
    //fields
    ModelRenderer wing;
    ModelRenderer top;

    public static ModelAdventureHat instance = new ModelAdventureHat();

    public ModelAdventureHat() {
        textureWidth = 64;
        textureHeight = 64;

        wing = new ModelRenderer(this, 0, 0);
        wing.addBox(-7F, -8F, -7F, 14, 1, 14);
        wing.setRotationPoint(0F, 0F, 0F);

        top = new ModelRenderer(this, 0, 15);
        top.addBox(-5F, -11F, -5F, 10, 3, 10);
        top.setRotationPoint(0F, 0F, 0F);

        for (Object part : this.boxList) {
            setRotation((ModelRenderer) part, 0F, 0F, 0F);
            ((ModelRenderer) part).setTextureSize(64, 64);
            ((ModelRenderer) part).setRotationPoint(bipedHead.rotationPointX, bipedHead.rotationPointY, bipedHead.rotationPointZ);
        }
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);

        for (Object part : this.boxList) {
            ((ModelRenderer) part).rotateAngleX = bipedHead.rotateAngleX;
            ((ModelRenderer) part).rotateAngleY = bipedHead.rotateAngleY;
            ((ModelRenderer) part).rotateAngleZ = bipedHead.rotateAngleZ;
        }

        wing.render(f5);
        GL11.glPushMatrix();
        GL11.glScalef(0.95f, 1f, 0.95f);
        top.render(f5);
        GL11.glPopMatrix();

    }

    private void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

}