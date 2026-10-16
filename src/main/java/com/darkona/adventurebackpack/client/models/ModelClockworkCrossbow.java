package com.darkona.adventurebackpack.client.models;

import com.darkona.adventurebackpack.common.CrossbowAnimation;
import com.darkona.adventurebackpack.common.CrossbowLogic;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import java.nio.DoubleBuffer;

@SideOnly(Side.CLIENT)
public class ModelClockworkCrossbow extends ModelBase
{
    private static final float DEG = (float) Math.PI / 180F;
    private static final DoubleBuffer SLOT_PLANE = BufferUtils.createDoubleBuffer(4);
    private static final float PLATE_INFLATE = 0.03F;
    private static final float[] MAGAZINE_BOLT_X = {0F, -1.5F, 1.5F};

    public ModelRenderer body;
    public ModelRenderer stock;
    public ModelRenderer buttPlate;
    public ModelRenderer grip;
    public ModelRenderer foregrip;
    public ModelRenderer trigger;
    public ModelRenderer guard;
    public ModelRenderer guardPost;
    public ModelRenderer rail;
    public ModelRenderer receiver;
    public ModelRenderer bracket;
    public ModelRenderer sight;
    public ModelRenderer yokeR;
    public ModelRenderer yokeL;
    public ModelRenderer gear;
    public ModelRenderer gearA;
    public ModelRenderer gearB;
    public ModelRenderer gearHub;
    public ModelRenderer key;
    public ModelRenderer keyStem;
    public ModelRenderer keyBow;
    public ModelRenderer drum;
    public ModelRenderer chamber0;
    public ModelRenderer chamber1;
    public ModelRenderer chamber2;
    public ModelRenderer chamber3;
    public ModelRenderer chamber4;
    public ModelRenderer chamber5;
    public ModelRenderer plateF0;
    public ModelRenderer plateB0;
    public ModelRenderer plateF1;
    public ModelRenderer plateB1;
    public ModelRenderer plateF2;
    public ModelRenderer plateB2;
    public ModelRenderer spindle;
    public ModelRenderer bolt;
    public ModelRenderer boltTip;
    public ModelRenderer fletchV;
    public ModelRenderer fletchH;
    public ModelRenderer armR1;
    public ModelRenderer armR2;
    public ModelRenderer armR3;
    public ModelRenderer armL1;
    public ModelRenderer armL2;
    public ModelRenderer armL3;
    public ModelRenderer string;
    public ModelRenderer cartridge;
    private final float[] magazinePose = new float[CrossbowAnimation.MAG_SIZE];
    public ModelRenderer hook;
    public ModelRenderer hookEye;
    public ModelRenderer hookShank;
    public ModelRenderer hookTooth0;
    public ModelRenderer hookBarb0;
    public ModelRenderer hookTooth1;
    public ModelRenderer hookBarb1;
    public ModelRenderer hookTooth2;
    public ModelRenderer hookBarb2;
    public ModelRenderer hookTooth3;
    public ModelRenderer hookBarb3;
    public ModelRenderer legR;
    public ModelRenderer legL;

    private final ModelRenderer[] chambers = new ModelRenderer[CrossbowAnimation.CHAMBERS];
    private final float[] stringMath = new float[4];

    public ModelClockworkCrossbow()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        body = new ModelRenderer(this, 0, 0);
        body.setRotationPoint(0.0F, 1.5F, 0.0F);
        body.addBox(-2.0F, -10.0F, -1.5F, 4, 24, 3, 0.0F);
        setRotateAngle(body, -1.5708F, 0.0F, 0.0F);
        stock = new ModelRenderer(this, 20, 0);
        stock.setRotationPoint(0.0F, 1.5F, 9.0F);
        stock.addBox(-2.0F, 0.0F, -2.0F, 4, 10, 4, 0.0F);
        setRotateAngle(stock, 1.7802F, 0.0F, 0.0F);
        buttPlate = new ModelRenderer(this, 0, 35);
        buttPlate.addBox(-2.0F, 10.0F, -2.5F, 4, 1, 5, 0.0F);
        grip = new ModelRenderer(this, 44, 12);
        grip.setRotationPoint(0.0F, 0.0F, 6.0F);
        grip.addBox(-1.5F, -7.0F, -1.5F, 3, 7, 3, 0.0F);
        setRotateAngle(grip, -0.3491F, 0.0F, 0.0F);
        foregrip = new ModelRenderer(this, 0, 27);
        foregrip.setRotationPoint(0.0F, 0.0F, -8.0F);
        foregrip.addBox(-1.5F, -5.0F, -1.5F, 3, 5, 3, 0.0F);
        setRotateAngle(foregrip, -0.1745F, 0.0F, 0.0F);
        trigger = new ModelRenderer(this, 36, 13);
        trigger.setRotationPoint(0.0F, 0.0F, 3.5F);
        trigger.addBox(-0.5F, -3.0F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(trigger, 0.3491F, 0.0F, 0.0F);
        guard = new ModelRenderer(this, 40, 31);
        guard.setRotationPoint(0.0F, 0.0F, 1.0F);
        guard.addBox(-0.5F, -4.5F, 0.0F, 1, 1, 6, 0.0F);
        guardPost = new ModelRenderer(this, 54, 31);
        guardPost.setRotationPoint(0.0F, 0.0F, 1.0F);
        guardPost.addBox(-0.5F, -4.5F, 0.0F, 1, 5, 1, 0.0F);
        rail = new ModelRenderer(this, 14, 0);
        rail.setRotationPoint(0.0F, 3.5F, 0.0F);
        rail.addBox(-1.0F, -3.0F, -0.5F, 2, 16, 1, 0.0F);
        setRotateAngle(rail, -1.5708F, 0.0F, 0.0F);
        receiver = new ModelRenderer(this, 44, 22);
        receiver.addBox(-2.5F, 3.0F, 2.0F, 5, 4, 5, 0.0F);
        bracket = new ModelRenderer(this, 22, 27);
        bracket.addBox(-3.0F, 0.0F, -15.0F, 6, 4, 3, 0.0F);
        sight = new ModelRenderer(this, 36, 34);
        sight.addBox(-0.5F, 4.0F, -14.5F, 1, 2, 1, 0.0F);
        yokeR = new ModelRenderer(this, 56, 12);
        yokeR.addBox(3.5F, 2.0F, -7.0F, 1, 6, 2, 0.0F);
        yokeL = new ModelRenderer(this, 56, 12);
        yokeL.addBox(-4.5F, 2.0F, -7.0F, 1, 6, 2, 0.0F);
        gear = new ModelRenderer(this, 0, 0);
        gear.setRotationPoint(2.5F, 5.0F, 4.5F);
        gearA = new ModelRenderer(this, 12, 27);
        gearA.addBox(0.0F, -2.0F, -2.0F, 1, 4, 4, 0.0F);
        gearB = new ModelRenderer(this, 12, 27);
        gearB.addBox(0.0F, -2.0F, -2.0F, 1, 4, 4, 0.0F);
        setRotateAngle(gearB, 0.7854F, 0.0F, 0.0F);
        gearHub = new ModelRenderer(this, 28, 41);
        gearHub.addBox(1.0F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        key = new ModelRenderer(this, 0, 0);
        key.setRotationPoint(-2.5F, 5.0F, 4.5F);
        keyStem = new ModelRenderer(this, 58, 36);
        keyStem.addBox(-2.0F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        keyBow = new ModelRenderer(this, 18, 35);
        keyBow.addBox(-3.0F, -2.0F, -0.5F, 1, 4, 1, 0.0F);
        drum = new ModelRenderer(this, 0, 0);
        drum.setRotationPoint(0.0F, 8.0F, -6.0F);
        chamber0 = new ModelRenderer(this, 40, 0);
        chamber0.addBox(-1.0F, -3.5F, -5.0F, 2, 2, 10, 0.0F);
        chamber1 = new ModelRenderer(this, 40, 0);
        chamber1.addBox(-1.0F, -3.5F, -5.0F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber1, 0.0F, 0.0F, 1.0472F);
        chamber2 = new ModelRenderer(this, 40, 0);
        chamber2.addBox(-1.0F, -3.5F, -5.0F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber2, 0.0F, 0.0F, 2.0944F);
        chamber3 = new ModelRenderer(this, 40, 0);
        chamber3.addBox(-1.0F, -3.5F, -5.0F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber3, 0.0F, 0.0F, 3.1416F);
        chamber4 = new ModelRenderer(this, 40, 0);
        chamber4.addBox(-1.0F, -3.5F, -5.0F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber4, 0.0F, 0.0F, 4.1888F);
        chamber5 = new ModelRenderer(this, 40, 0);
        chamber5.addBox(-1.0F, -3.5F, -5.0F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber5, 0.0F, 0.0F, 5.236F);
        plateF0 = new ModelRenderer(this, 22, 34);
        plateF0.setRotationPoint(0.0F, 0.0F, -5.5F);
        plateF0.addBox(-3.0F, -3.0F, -0.5F, 6, 6, 1, 0.0F);
        plateB0 = new ModelRenderer(this, 22, 34);
        plateB0.setRotationPoint(0.0F, 0.0F, 5.5F);
        plateB0.addBox(-3.0F, -3.0F, -0.5F, 6, 6, 1, 0.0F);
        plateF1 = new ModelRenderer(this, 22, 34);
        plateF1.setRotationPoint(0.0F, 0.0F, -5.5F);
        plateF1.addBox(-3.0F, -3.0F, -0.5F, 6, 6, 1, PLATE_INFLATE);
        setRotateAngle(plateF1, 0.0F, 0.0F, 0.5236F);
        plateB1 = new ModelRenderer(this, 22, 34);
        plateB1.setRotationPoint(0.0F, 0.0F, 5.5F);
        plateB1.addBox(-3.0F, -3.0F, -0.5F, 6, 6, 1, PLATE_INFLATE);
        setRotateAngle(plateB1, 0.0F, 0.0F, 0.5236F);
        plateF2 = new ModelRenderer(this, 22, 34);
        plateF2.setRotationPoint(0.0F, 0.0F, -5.5F);
        plateF2.addBox(-3.0F, -3.0F, -0.5F, 6, 6, 1, 2F * PLATE_INFLATE);
        setRotateAngle(plateF2, 0.0F, 0.0F, 1.0472F);
        plateB2 = new ModelRenderer(this, 22, 34);
        plateB2.setRotationPoint(0.0F, 0.0F, 5.5F);
        plateB2.addBox(-3.0F, -3.0F, -0.5F, 6, 6, 1, 2F * PLATE_INFLATE);
        setRotateAngle(plateB2, 0.0F, 0.0F, 1.0472F);
        spindle = new ModelRenderer(this, 22, 41);
        spindle.addBox(-0.5F, -0.5F, -7.5F, 1, 1, 2, 0.0F);
        bolt = new ModelRenderer(this, 36, 0);
        bolt.setRotationPoint(0.0F, -2.5F, 0.0F);
        bolt.addBox(-0.5F, -5.5F, -0.5F, 1, 12, 1, 0.0F);
        setRotateAngle(bolt, -1.5708F, 0.0F, 0.0F);
        boltTip = new ModelRenderer(this, 32, 14);
        boltTip.setRotationPoint(0.0F, -2.5F, 0.0F);
        boltTip.addBox(-0.5F, 6.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(boltTip, -1.5708F, 0.0F, 0.0F);
        fletchV = new ModelRenderer(this, 58, 31);
        fletchV.setRotationPoint(0.0F, -2.5F, 0.0F);
        fletchV.addBox(0.0F, -7.0F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(fletchV, -1.5708F, 0.0F, 0.0F);
        fletchH = new ModelRenderer(this, 56, 20);
        fletchH.setRotationPoint(0.0F, -2.5F, 0.0F);
        fletchH.addBox(-1.5F, -7.0F, 0.0F, 3, 2, 0, 0.0F);
        setRotateAngle(fletchH, -1.5708F, 0.0F, 0.0F);
        armR1 = new ModelRenderer(this, 48, 38);
        armR1.setRotationPoint(2.0F, 1.5F, -12.5F);
        armR1.addBox(0.0F, -1.0F, -1.0F, 6, 2, 2, 0.0F);
        setRotateAngle(armR1, 0.0F, -0.2618F, 0.0F);
        armR2 = new ModelRenderer(this, 0, 41);
        armR2.setRotationPoint(6.0F, 0.0F, 0.0F);
        armR2.addBox(0.0F, -1.0F, -1.0F, 5, 2, 2, 0.0F);
        setRotateAngle(armR2, 0.0F, -0.3491F, 0.0F);
        armR3 = new ModelRenderer(this, 48, 42);
        armR3.setRotationPoint(5.0F, 0.0F, 0.0F);
        armR3.addBox(0.0F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(armR3, 0.0F, -0.3491F, 0.0F);
        armL1 = new ModelRenderer(this, 48, 38);
        armL1.setRotationPoint(-2.0F, 1.5F, -12.5F);
        armL1.addBox(-6.0F, -1.0F, -1.0F, 6, 2, 2, 0.0F);
        setRotateAngle(armL1, 0.0F, 0.2618F, 0.0F);
        armL2 = new ModelRenderer(this, 0, 41);
        armL2.setRotationPoint(-6.0F, 0.0F, 0.0F);
        armL2.addBox(-5.0F, -1.0F, -1.0F, 5, 2, 2, 0.0F);
        setRotateAngle(armL2, 0.0F, 0.3491F, 0.0F);
        armL3 = new ModelRenderer(this, 48, 42);
        armL3.setRotationPoint(-5.0F, 0.0F, 0.0F);
        armL3.addBox(-4.0F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(armL3, 0.0F, 0.3491F, 0.0F);
        string = new ModelRenderer(this, 40, 12);
        string.addBox(-0.5F, 0.0F, -0.5F, 1, 10, 1, 0.0F);
        cartridge = new ModelRenderer(this, 14, 17);
        cartridge.setRotationPoint(0.0F, 12.0F, -6.0F);
        cartridge.addBox(-2.5F, -1.0F, -4.0F, 5, 2, 8, 0.0F);
        hook = new ModelRenderer(this, 0, 0);
        hook.setRotationPoint(0.0F, 5.5F, -9.0F);
        hookEye = new ModelRenderer(this, 26, 14);
        hookEye.addBox(-1.0F, -1.0F, -0.5F, 2, 2, 1, 0.0F);
        hookShank = new ModelRenderer(this, 36, 38);
        hookShank.addBox(-0.5F, -0.5F, -5.5F, 1, 1, 5, 0.0F);
        hookTooth0 = new ModelRenderer(this, 14, 41);
        hookTooth0.setRotationPoint(0.0F, 0.0F, -5.5F);
        hookTooth0.addBox(-0.5F, -0.5F, -3.0F, 1, 1, 3, 0.0F);
        setRotateAngle(hookTooth0, 0.6981F, 0.0F, 0.0F);
        hookBarb0 = new ModelRenderer(this, 20, 14);
        hookBarb0.setRotationPoint(0.0F, 0.0F, -3.0F);
        hookBarb0.addBox(-0.5F, -0.5F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(hookBarb0, 0.9599F, 0.0F, 0.0F);
        hookTooth1 = new ModelRenderer(this, 14, 41);
        hookTooth1.setRotationPoint(0.0F, 0.0F, -5.5F);
        hookTooth1.addBox(-0.5F, -0.5F, -3.0F, 1, 1, 3, 0.0F);
        setRotateAngle(hookTooth1, -0.6981F, 0.0F, 0.0F);
        hookBarb1 = new ModelRenderer(this, 20, 14);
        hookBarb1.setRotationPoint(0.0F, 0.0F, -3.0F);
        hookBarb1.addBox(-0.5F, -0.5F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(hookBarb1, -0.9599F, 0.0F, 0.0F);
        hookTooth2 = new ModelRenderer(this, 14, 41);
        hookTooth2.setRotationPoint(0.0F, 0.0F, -5.5F);
        hookTooth2.addBox(-0.5F, -0.5F, -3.0F, 1, 1, 3, 0.0F);
        setRotateAngle(hookTooth2, 0.0F, -0.6981F, 0.0F);
        hookBarb2 = new ModelRenderer(this, 20, 14);
        hookBarb2.setRotationPoint(0.0F, 0.0F, -3.0F);
        hookBarb2.addBox(-0.5F, -0.5F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(hookBarb2, 0.0F, -0.9599F, 0.0F);
        hookTooth3 = new ModelRenderer(this, 14, 41);
        hookTooth3.setRotationPoint(0.0F, 0.0F, -5.5F);
        hookTooth3.addBox(-0.5F, -0.5F, -3.0F, 1, 1, 3, 0.0F);
        setRotateAngle(hookTooth3, 0.0F, 0.6981F, 0.0F);
        hookBarb3 = new ModelRenderer(this, 20, 14);
        hookBarb3.setRotationPoint(0.0F, 0.0F, -3.0F);
        hookBarb3.addBox(-0.5F, -0.5F, -2.0F, 1, 1, 2, 0.0F);
        setRotateAngle(hookBarb3, 0.0F, 0.9599F, 0.0F);
        legR = new ModelRenderer(this, 40, 23);
        legR.setRotationPoint(2.5F, 0.5F, -11.0F);
        legR.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1, 0.0F);
        setRotateAngle(legR, -1.5708F, 0.0F, 0.0F);
        legL = new ModelRenderer(this, 40, 23);
        legL.setRotationPoint(-2.5F, 0.5F, -11.0F);
        legL.addBox(-0.5F, -6.0F, -0.5F, 1, 6, 1, 0.0F);
        setRotateAngle(legL, -1.5708F, 0.0F, 0.0F);
        stock.addChild(buttPlate);
        gear.addChild(gearA);
        gear.addChild(gearB);
        gear.addChild(gearHub);
        key.addChild(keyStem);
        key.addChild(keyBow);
        drum.addChild(chamber0);
        drum.addChild(chamber1);
        drum.addChild(chamber2);
        drum.addChild(chamber3);
        drum.addChild(chamber4);
        drum.addChild(chamber5);
        drum.addChild(plateF0);
        drum.addChild(plateB0);
        drum.addChild(plateF1);
        drum.addChild(plateB1);
        drum.addChild(plateF2);
        drum.addChild(plateB2);
        drum.addChild(spindle);
        armR1.addChild(armR2);
        armR2.addChild(armR3);
        armL1.addChild(armL2);
        armL2.addChild(armL3);
        hook.addChild(hookEye);
        hook.addChild(hookShank);
        hook.addChild(hookTooth0);
        hookTooth0.addChild(hookBarb0);
        hook.addChild(hookTooth1);
        hookTooth1.addChild(hookBarb1);
        hook.addChild(hookTooth2);
        hookTooth2.addChild(hookBarb2);
        hook.addChild(hookTooth3);
        hookTooth3.addChild(hookBarb3);
        chambers[0] = chamber0;
        chambers[1] = chamber1;
        chambers[2] = chamber2;
        chambers[3] = chamber3;
        chambers[4] = chamber4;
        chambers[5] = chamber5;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        render(f5, CrossbowAnimation.CHAMBERS, CrossbowAnimation.CHAMBERS, CrossbowAnimation.NEVER, -1F, false, CrossbowLogic.MODE_REPEATING, 0F, false);
    }

    public void render(float scale, int ammo, int capacity, float sinceShot, float reloadProgress, boolean swapped, int mode, float bipodPose, boolean hookLoaded)
    {
        float drumDegrees = CrossbowAnimation.drumDegrees(ammo, capacity, sinceShot, reloadProgress);
        drum.rotateAngleZ = drumDegrees * DEG;
        gear.rotateAngleX = drumDegrees * 2F * DEG;
        key.rotateAngleX = -drumDegrees * 2F * DEG;
        boolean hook = mode == CrossbowLogic.MODE_GRAPPLE && hookLoaded;
        int hiddenChamber = hook ? CrossbowAnimation.firingChamber(ammo, capacity) : -1;

        body.render(scale);
        stock.render(scale);
        grip.render(scale);
        foregrip.render(scale);
        trigger.render(scale);
        guard.render(scale);
        guardPost.render(scale);
        rail.render(scale);
        receiver.render(scale);
        bracket.render(scale);
        sight.render(scale);
        yokeR.render(scale);
        yokeL.render(scale);
        gear.render(scale);
        key.render(scale);
        armR1.render(scale);
        armL1.render(scale);
        drum.render(scale);
        if (hook)
        {
            this.hook.render(scale);
        }
        if (mode == CrossbowLogic.MODE_SNIPER)
        {
            legR.rotateAngleX = CrossbowAnimation.legAngleX(bipodPose) * DEG;
            legL.rotateAngleX = legR.rotateAngleX;
            legR.rotateAngleZ = CrossbowAnimation.legAngleZ(bipodPose, true) * DEG;
            legL.rotateAngleZ = CrossbowAnimation.legAngleZ(bipodPose, false) * DEG;
            legR.render(scale);
            legL.render(scale);
        }

        if (CrossbowAnimation.boltsVisible(reloadProgress))
        {
            renderBolts(scale, ammo, capacity, hiddenChamber);
        }

        float nockZ = CrossbowAnimation.stringZ(sinceShot);
        renderString(scale, CrossbowAnimation.TIP_X, nockZ);
        renderString(scale, -CrossbowAnimation.TIP_X, nockZ);

        if (swapped && CrossbowAnimation.spentMagazine(reloadProgress, magazinePose))
        {
            renderMagazine(scale, 0);
        }
        if (CrossbowAnimation.newMagazine(reloadProgress, magazinePose))
        {
            renderMagazine(scale, CrossbowAnimation.magazineBolts(ammo));
        }
    }

    private void renderMagazine(float scale, int bolts)
    {
        float[] pose = magazinePose;
        boolean clipped = pose[CrossbowAnimation.MAG_CLIPPED] > 0F;
        GL11.glPushMatrix();
        try
        {
            if (clipped)
            {
                SLOT_PLANE.clear();
                SLOT_PLANE.put(0D).put(1D).put(0D).put(-CrossbowAnimation.SLOT_Y * scale).flip();
                GL11.glClipPlane(GL11.GL_CLIP_PLANE0, SLOT_PLANE);
                GL11.glEnable(GL11.GL_CLIP_PLANE0);
            }
            float s = pose[CrossbowAnimation.MAG_SCALE];
            GL11.glTranslatef((cartridge.rotationPointX + pose[CrossbowAnimation.MAG_X]) * scale,
                    (cartridge.rotationPointY + pose[CrossbowAnimation.MAG_Y]) * scale, cartridge.rotationPointZ * scale);
            GL11.glRotatef(pose[CrossbowAnimation.MAG_ROLL], 0F, 0F, 1F);
            GL11.glScalef(s, s, s);
            GL11.glTranslatef(-cartridge.rotationPointX * scale, -cartridge.rotationPointY * scale, -cartridge.rotationPointZ * scale);
            cartridge.render(scale);
            if (bolts > 0)
            {
                renderMagazineBolts(scale, bolts);
            }
        } finally
        {
            GL11.glDisable(GL11.GL_CLIP_PLANE0);
            GL11.glPopMatrix();
        }
    }

    private void renderMagazineBolts(float scale, int bolts)
    {
        int color = CrossbowAnimation.FLETCH_COLOR;
        float y = cartridge.rotationPointY + 1.5F - bolt.rotationPointY;
        for (int i = 0; i < bolts; i++)
        {
            GL11.glPushMatrix();
            try
            {
                GL11.glTranslatef(MAGAZINE_BOLT_X[i] * scale, y * scale, cartridge.rotationPointZ * scale);
                bolt.render(scale);
                boltTip.render(scale);
                GL11.glColor4f((color >> 16 & 0xFF) / 255F, (color >> 8 & 0xFF) / 255F, (color & 0xFF) / 255F, 1F);
                fletchV.render(scale);
            } finally
            {
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glPopMatrix();
            }
        }
    }

    private void renderBolts(float scale, int ammo, int capacity, int hiddenChamber)
    {
        int color = CrossbowAnimation.FLETCH_COLOR;
        float red = (color >> 16 & 0xFF) / 255F;
        float green = (color >> 8 & 0xFF) / 255F;
        float blue = (color & 0xFF) / 255F;
        for (int chamber = 0; chamber < CrossbowAnimation.CHAMBERS; chamber++)
        {
            if (chamber == hiddenChamber || !CrossbowAnimation.chamberLoaded(chamber, ammo, capacity)) continue;
            GL11.glPushMatrix();
            try
            {
                drum.postRender(scale);
                chambers[chamber].postRender(scale);
                bolt.render(scale);
                boltTip.render(scale);
                GL11.glColor4f(red, green, blue, 1F);
                fletchV.render(scale);
                fletchH.render(scale);
            } finally
            {
                GL11.glColor4f(1F, 1F, 1F, 1F);
                GL11.glPopMatrix();
            }
        }
    }

    private void renderString(float scale, float tipX, float nockZ)
    {
        CrossbowAnimation.stringTransform(tipX, CrossbowAnimation.TIP_Y, CrossbowAnimation.TIP_Z, 0F, CrossbowAnimation.STRING_Y, nockZ, stringMath);
        GL11.glPushMatrix();
        try
        {
            GL11.glTranslatef(tipX * scale, CrossbowAnimation.TIP_Y * scale, CrossbowAnimation.TIP_Z * scale);
            GL11.glRotatef(stringMath[0], stringMath[1], 0F, stringMath[2]);
            GL11.glScalef(1F, stringMath[3] / CrossbowAnimation.STRING_BOX_LENGTH, 1F);
            string.render(scale);
        } finally
        {
            GL11.glPopMatrix();
        }
    }

    private static void setRotateAngle(ModelRenderer part, float x, float y, float z)
    {
        part.rotateAngleX = x;
        part.rotateAngleY = y;
        part.rotateAngleZ = z;
    }
}
