package com.darkona.adventurebackpack.client.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public class ModelAdventureHat extends ModelBiped
{
    //fields
    ModelRenderer wing;
    ModelRenderer top;
    ModelRenderer thing;

    public static ModelAdventureHat instance = new ModelAdventureHat();

    public ModelAdventureHat()
    {
        textureWidth = 64;
        textureHeight = 32;

        wing = new ModelRenderer(this, 0, 0);
        wing.addBox(-6F, -8.5F, -6F, 12, 1, 12);
        wing.setRotationPoint(0F, 0F, 0F);
        wing.setTextureSize(64, 32);
        wing.mirror = true;
        setRotation(wing, 0F, 0F, 0.0523599F);

        thing = new ModelRenderer(this, 32, 21);
        thing.addBox(4F, -9F, -1F, 1, 1, 2);
        thing.setRotationPoint(0F, 0F, 0F);
        thing.setTextureSize(64, 32);
        thing.mirror = true;
        setRotation(thing, 0F, 0F, 0F);

        top = new ModelRenderer(this, 0, 21);
        top.addBox(-4F, -11F, -4F, 8, 3, 8);
        top.setRotationPoint(0F, 0F, 0F);
        top.setTextureSize(64, 32);
        top.mirror = true;
        setRotation(top, 0F, 0F, 0F);

        this.bipedHead.addChild(top);
        this.bipedHead.addChild(wing);
        this.bipedHead.addChild(thing);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        setRotationAngles(entity, f, f1, f2, f3, f4, f5);

        for (Object part : this.boxList)
        {
            ((ModelRenderer) part).rotateAngleX = bipedHead.rotateAngleX;
            ((ModelRenderer) part).rotateAngleY = bipedHead.rotateAngleY;
            ((ModelRenderer) part).rotateAngleZ = bipedHead.rotateAngleZ;
        }
        GL11.glPushMatrix();
        GL11.glScalef(1.2f, 1.2f, 1.2f);
        wing.render(f5);
        thing.render(f5);
        top.render(f5);
        GL11.glPopMatrix();
    }

    private void setRotation(ModelRenderer model, float x, float y, float z)
    {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }

}