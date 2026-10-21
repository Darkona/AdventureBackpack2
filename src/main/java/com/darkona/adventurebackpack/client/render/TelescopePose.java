package com.darkona.adventurebackpack.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public final class TelescopePose
{
    private static final float ARM_LIFT = 1.9198F;
    private static final float CROUCH_LIFT = 0.2617994F;
    private static final float ARM_TURN = 0.2617994F;
    private static final float MIN_X = -2.4F;
    private static final float MAX_X = 3.3F;
    private static final ModelBase DETACHED = new ModelBase() {};

    private static Entity posing;
    private static boolean crouching;

    private TelescopePose() {}

    public static void install(ModelBiped model)
    {
        if (model == null || model.bipedRightArm == null || model.bipedRightArm instanceof Arm) return;
        model.bipedRightArm = new Arm(model, model.bipedRightArm);
    }

    public static void begin(Entity player, boolean sneaking)
    {
        posing = player;
        crouching = sneaking;
    }

    public static void end()
    {
        posing = null;
    }

    private static void apply(ModelBiped model)
    {
        float x = model.bipedHead.rotateAngleX - ARM_LIFT - (crouching ? CROUCH_LIFT : 0F);
        ModelRenderer right = model.bipedRightArm;
        right.rotateAngleX = Math.max(MIN_X, Math.min(MAX_X, x));
        right.rotateAngleY = model.bipedHead.rotateAngleY - ARM_TURN;
        right.rotateAngleZ = 0F;
    }

    private static final class Arm extends ModelRenderer
    {
        private final ModelBiped model;

        Arm(ModelBiped model, ModelRenderer base)
        {
            super(DETACHED);
            this.model = model;
            textureWidth = base.textureWidth;
            textureHeight = base.textureHeight;
            cubeList = base.cubeList;
            childModels = base.childModels;
            mirror = base.mirror;
            showModel = base.showModel;
            isHidden = base.isHidden;
            offsetX = base.offsetX;
            offsetY = base.offsetY;
            offsetZ = base.offsetZ;
            rotationPointX = base.rotationPointX;
            rotationPointY = base.rotationPointY;
            rotationPointZ = base.rotationPointZ;
            rotateAngleX = base.rotateAngleX;
            rotateAngleY = base.rotateAngleY;
            rotateAngleZ = base.rotateAngleZ;
        }

        @Override
        public void render(float scale)
        {
            if (posing != null) apply(model);
            super.render(scale);
        }
    }
}
