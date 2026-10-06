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

/**
 * Two-handed third person hold of the Clockwork Crossbow on the vanilla player models.
 * <p>
 * {@code ModelBiped.render} sets every angle in {@code setRotationAngles} and draws the parts right away, so there is
 * no event between the two. The pose rides on an empty child of {@code bipedBody}: the body is drawn before the arms,
 * and when the body draws its children this hook overwrites the arm angles and shoulder pivots with
 * {@link CrossbowAnimation#holdPose}. It only acts between {@link #begin} and {@link #end} (the
 * {@code RenderPlayerEvent} Pre and Post of a player holding the crossbow), draws nothing and touches no GL state;
 * vanilla resets the angles and pivots on the next {@code setRotationAngles}. The first person arm never draws the
 * body, so it keeps the vanilla pose. No allocation per frame: one shared output array.
 */
@SideOnly(Side.CLIENT)
public final class CrossbowHoldPose
{
    private static final float RAD_TO_DEG = 180F / (float) Math.PI;
    /** The hooks are built on this model so they never join a player model's box list (arrows pick boxes from it). */
    private static final ModelBase DETACHED = new ModelBase() {};
    private static final float[] pose = new float[CrossbowAnimation.POSE_SIZE];

    private static Entity posing;
    private static boolean steady;
    private static float recoil;
    private static float age;
    /** The hook ran for the current player, so {@link #pose} holds its arms for the item renderer. */
    private static boolean posed;

    private CrossbowHoldPose() {}

    /** Adds the hook to {@code model}'s body once; later calls find it and return. */
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

    /** The next models drawn belong to {@code player}, who holds the crossbow. */
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

    /** True when {@code entity}'s arms were posed this frame: the weapon can be lined up with the look. */
    public static boolean isPosed(Entity entity)
    {
        return posed && entity != null && entity == posing;
    }

    /**
     * From the right arm's frame (at the hand) to the look frame of the weapon (ModelBiped axes: X left, Y down,
     * Z back): undo the arm's rotation, turn to the weapon's yaw and pitch and slide back by the kick.
     */
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

    /** The empty child of the body that poses the arms when the body is drawn. */
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
                // A model changed by another mod: leave its arms as they are.
            }
        }

        @Override
        public void renderWithRotation(float scale) {}

        @Override
        public void postRender(float scale) {}
    }
}
