package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.common.CrossbowAnimation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public final class CrossbowHoldPose
{
    private static final float RAD_TO_DEG = 180F / (float) Math.PI;
    private static final ModelBase DETACHED = new ModelBase() {};
    private static final float[] pose = new float[CrossbowAnimation.POSE_SIZE];

    private static Entity posing;
    private static boolean steady;
    private static float recoil;
    private static float age;
    private static boolean posed;

    private CrossbowHoldPose() {}

    public static void install(ModelBiped model)
    {
        if (model == null || model.bipedBody == null) return;
        List children = model.bipedBody.childModels;
        if (children != null)
        {
            for (int i = 0; i < children.size(); i++)
            {
                if (children.get(i) instanceof Hook) return;
            }
        }
        model.bipedBody.addChild(new Hook(model));
    }

    public static void begin(Entity player, boolean steadyPose, float recoilAmount, float ageTicks)
    {
        posing = player;
        steady = steadyPose;
        recoil = recoilAmount;
        age = ageTicks;
        posed = false;
    }

    public static void end()
    {
        posing = null;
        posed = false;
    }

    public static boolean isPosed(Entity entity)
    {
        return posed && entity != null && entity == posing;
    }

    public static void armToWeapon(float pixel)
    {
        GL11.glRotatef(-pose[CrossbowAnimation.POSE_RIGHT_X] * RAD_TO_DEG, 1F, 0F, 0F);
        GL11.glRotatef(-pose[CrossbowAnimation.POSE_RIGHT_Y] * RAD_TO_DEG, 0F, 1F, 0F);
        GL11.glRotatef(-pose[CrossbowAnimation.POSE_RIGHT_Z] * RAD_TO_DEG, 0F, 0F, 1F);
        GL11.glRotatef(pose[CrossbowAnimation.POSE_WEAPON_YAW] * RAD_TO_DEG, 0F, 1F, 0F);
        GL11.glRotatef(pose[CrossbowAnimation.POSE_WEAPON_PITCH] * RAD_TO_DEG, 1F, 0F, 0F);
        GL11.glTranslatef(0F, 0F, pose[CrossbowAnimation.POSE_KICK_BACK] * pixel);
    }

    private static void apply(ModelBiped model)
    {
        CrossbowAnimation.holdPose(model.bipedHead.rotateAngleX, model.bipedHead.rotateAngleY, steady, recoil, age, pose);
        ModelRenderer right = model.bipedRightArm;
        ModelRenderer left = model.bipedLeftArm;
        right.rotateAngleX = pose[CrossbowAnimation.POSE_RIGHT_X];
        right.rotateAngleY = pose[CrossbowAnimation.POSE_RIGHT_Y];
        right.rotateAngleZ = pose[CrossbowAnimation.POSE_RIGHT_Z];
        left.rotateAngleX = pose[CrossbowAnimation.POSE_LEFT_X];
        left.rotateAngleY = pose[CrossbowAnimation.POSE_LEFT_Y];
        left.rotateAngleZ = pose[CrossbowAnimation.POSE_LEFT_Z];
        right.rotationPointZ += pose[CrossbowAnimation.POSE_RIGHT_SHOULDER_Z];
        left.rotationPointZ += pose[CrossbowAnimation.POSE_LEFT_SHOULDER_Z];
        posed = true;
    }

    private static final class Hook extends ModelRenderer
    {
        private final ModelBiped model;

        Hook(ModelBiped model)
        {
            super(DETACHED);
            this.model = model;
        }

        @Override
        public void render(float scale)
        {
            if (posing == null) return;
            try
            {
                apply(model);
            } catch (Throwable ignored)
            {
            }
        }

        @Override
        public void renderWithRotation(float scale) {}

        @Override
        public void postRender(float scale) {}
    }
}
