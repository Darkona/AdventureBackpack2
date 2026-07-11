package com.darkona.adventurebackpack.client.models;

import com.darkona.adventurebackpack.common.CrossbowAnimation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

/**
 * The Clockwork Crossbow: dark walnut tiller, stock and limbs, iron fittings, a brass six-chamber drum over the
 * rail that turns one step per shot and empties as the magazine does, a brass gear and winding key that turn with
 * it, and the string on the rail. Units are sixteenths of a block, +Y up, -Z forward (muzzle), +X the shooter's
 * right; the item renderer turns this frame into each render type's frame.
 * <p>
 * Geometry, UV layout and texture are generated together by {@code notes/crossbow-art.py} (Javier's copy of the
 * script); the constructor body is its output. Only the drum, gear, key, bolts, strings and cartridge move; every
 * angle comes from {@link CrossbowAnimation} so the frame-by-frame work is arithmetic (performance contract, tier 4).
 */
@SideOnly(Side.CLIENT)
public class ModelClockworkCrossbow extends ModelBase
{
    private static final float DEG = (float) Math.PI / 180F;

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

    private final ModelRenderer[] chambers = new ModelRenderer[CrossbowAnimation.CHAMBERS];
    /** Scratch for {@link CrossbowAnimation#stringTransform}: angle, axis X, axis Z, length. */
    private final float[] stringMath = new float[4];

    public ModelClockworkCrossbow()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        body = new ModelRenderer(this, 0, 0);
        body.setRotationPoint(0F, 1.5F, 0F);
        body.addBox(-2F, -10F, -1.5F, 4, 24, 3, 0.0F);
        setRotateAngle(body, -1.570796F, 0.0F, 0.0F);
        stock = new ModelRenderer(this, 20, 0);
        stock.setRotationPoint(0F, 1.5F, 9F);
        stock.addBox(-2F, 0F, -2F, 4, 10, 4, 0.0F);
        setRotateAngle(stock, 1.780236F, 0.0F, 0.0F);
        buttPlate = new ModelRenderer(this, 14, 46);
        buttPlate.setRotationPoint(0F, 0F, 0F);
        buttPlate.addBox(-2F, 10F, -2.5F, 4, 1, 5, 0.0F);
        grip = new ModelRenderer(this, 30, 27);
        grip.setRotationPoint(0F, 0F, 6F);
        grip.addBox(-1.5F, -7F, -1.5F, 3, 7, 3, 0.0F);
        setRotateAngle(grip, -0.349066F, 0.0F, 0.0F);
        foregrip = new ModelRenderer(this, 0, 38);
        foregrip.setRotationPoint(0F, 0F, -8F);
        foregrip.addBox(-1.5F, -5F, -1.5F, 3, 5, 3, 0.0F);
        setRotateAngle(foregrip, -0.174533F, 0.0F, 0.0F);
        trigger = new ModelRenderer(this, 14, 53);
        trigger.setRotationPoint(0F, 0F, 3.5F);
        trigger.addBox(-0.5F, -3F, -0.5F, 1, 3, 1, 0.0F);
        setRotateAngle(trigger, 0.349066F, 0.0F, 0.0F);
        guard = new ModelRenderer(this, 46, 38);
        guard.setRotationPoint(0F, 0F, 1F);
        guard.addBox(-0.5F, -4.5F, 0F, 1, 1, 6, 0.0F);
        guardPost = new ModelRenderer(this, 32, 46);
        guardPost.setRotationPoint(0F, 0F, 1F);
        guardPost.addBox(-0.5F, -4.5F, 0F, 1, 5, 1, 0.0F);
        rail = new ModelRenderer(this, 14, 0);
        rail.setRotationPoint(0F, 3.5F, 0F);
        rail.addBox(-1F, -3F, -0.5F, 2, 16, 1, 0.0F);
        setRotateAngle(rail, -1.570796F, 0.0F, 0.0F);
        receiver = new ModelRenderer(this, 42, 27);
        receiver.setRotationPoint(0F, 0F, 0F);
        receiver.addBox(-2.5F, 3F, 2F, 5, 4, 5, 0.0F);
        bracket = new ModelRenderer(this, 28, 38);
        bracket.setRotationPoint(0F, 0F, 0F);
        bracket.addBox(-3F, 0F, -15F, 6, 4, 3, 0.0F);
        sight = new ModelRenderer(this, 24, 53);
        sight.setRotationPoint(0F, 0F, 0F);
        sight.addBox(-0.5F, 4F, -14.5F, 1, 2, 1, 0.0F);
        yokeR = new ModelRenderer(this, 22, 38);
        yokeR.setRotationPoint(0F, 0F, 0F);
        yokeR.addBox(3.5F, 2F, -7F, 1, 6, 2, 0.0F);
        yokeL = new ModelRenderer(this, 22, 38);
        yokeL.setRotationPoint(0F, 0F, 0F);
        yokeL.addBox(-4.5F, 2F, -7F, 1, 6, 2, 0.0F);
        gear = new ModelRenderer(this, 0, 0);
        gear.setRotationPoint(2.5F, 5F, 4.5F);
        gearA = new ModelRenderer(this, 12, 38);
        gearA.setRotationPoint(0F, 0F, 0F);
        gearA.addBox(0F, -2F, -2F, 1, 4, 4, 0.0F);
        gearB = new ModelRenderer(this, 12, 38);
        gearB.setRotationPoint(0F, 0F, 0F);
        gearB.addBox(0F, -2F, -2F, 1, 4, 4, 0.0F);
        setRotateAngle(gearB, 0.785398F, 0.0F, 0.0F);
        gearHub = new ModelRenderer(this, 54, 53);
        gearHub.setRotationPoint(0F, 0F, 0F);
        gearHub.addBox(1F, -0.5F, -0.5F, 1, 1, 1, 0.0F);
        key = new ModelRenderer(this, 0, 0);
        key.setRotationPoint(-2.5F, 5F, 4.5F);
        keyStem = new ModelRenderer(this, 42, 53);
        keyStem.setRotationPoint(0F, 0F, 0F);
        keyStem.addBox(-2F, -0.5F, -0.5F, 2, 1, 1, 0.0F);
        keyBow = new ModelRenderer(this, 42, 46);
        keyBow.setRotationPoint(0F, 0F, 0F);
        keyBow.addBox(-3F, -2F, -0.5F, 1, 4, 1, 0.0F);
        drum = new ModelRenderer(this, 0, 0);
        drum.setRotationPoint(0F, 8F, -6F);
        chamber0 = new ModelRenderer(this, 40, 0);
        chamber0.setRotationPoint(0F, 0F, 0F);
        chamber0.addBox(-1F, -3.5F, -5F, 2, 2, 10, 0.0F);
        chamber1 = new ModelRenderer(this, 40, 0);
        chamber1.setRotationPoint(0F, 0F, 0F);
        chamber1.addBox(-1F, -3.5F, -5F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber1, 0.0F, 0.0F, 1.047198F);
        chamber2 = new ModelRenderer(this, 40, 0);
        chamber2.setRotationPoint(0F, 0F, 0F);
        chamber2.addBox(-1F, -3.5F, -5F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber2, 0.0F, 0.0F, 2.094395F);
        chamber3 = new ModelRenderer(this, 40, 0);
        chamber3.setRotationPoint(0F, 0F, 0F);
        chamber3.addBox(-1F, -3.5F, -5F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber3, 0.0F, 0.0F, 3.141593F);
        chamber4 = new ModelRenderer(this, 40, 0);
        chamber4.setRotationPoint(0F, 0F, 0F);
        chamber4.addBox(-1F, -3.5F, -5F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber4, 0.0F, 0.0F, 4.18879F);
        chamber5 = new ModelRenderer(this, 40, 0);
        chamber5.setRotationPoint(0F, 0F, 0F);
        chamber5.addBox(-1F, -3.5F, -5F, 2, 2, 10, 0.0F);
        setRotateAngle(chamber5, 0.0F, 0.0F, 5.235988F);
        plateF0 = new ModelRenderer(this, 0, 46);
        plateF0.setRotationPoint(0F, 0F, -5.5F);
        plateF0.addBox(-3F, -3F, -0.5F, 6, 6, 1, 0.0F);
        plateB0 = new ModelRenderer(this, 0, 46);
        plateB0.setRotationPoint(0F, 0F, 5.5F);
        plateB0.addBox(-3F, -3F, -0.5F, 6, 6, 1, 0.0F);
        plateF1 = new ModelRenderer(this, 0, 46);
        plateF1.setRotationPoint(0F, 0F, -5.5F);
        plateF1.addBox(-3F, -3F, -0.5F, 6, 6, 1, 0.0F);
        setRotateAngle(plateF1, 0.0F, 0.0F, 0.523599F);
        plateB1 = new ModelRenderer(this, 0, 46);
        plateB1.setRotationPoint(0F, 0F, 5.5F);
        plateB1.addBox(-3F, -3F, -0.5F, 6, 6, 1, 0.0F);
        setRotateAngle(plateB1, 0.0F, 0.0F, 0.523599F);
        plateF2 = new ModelRenderer(this, 0, 46);
        plateF2.setRotationPoint(0F, 0F, -5.5F);
        plateF2.addBox(-3F, -3F, -0.5F, 6, 6, 1, 0.0F);
        setRotateAngle(plateF2, 0.0F, 0.0F, 1.047198F);
        plateB2 = new ModelRenderer(this, 0, 46);
        plateB2.setRotationPoint(0F, 0F, 5.5F);
        plateB2.addBox(-3F, -3F, -0.5F, 6, 6, 1, 0.0F);
        setRotateAngle(plateB2, 0.0F, 0.0F, 1.047198F);
        spindle = new ModelRenderer(this, 18, 53);
        spindle.setRotationPoint(0F, 0F, 0F);
        spindle.addBox(-0.5F, -0.5F, -7.5F, 1, 1, 2, 0.0F);
        bolt = new ModelRenderer(this, 36, 0);
        bolt.setRotationPoint(0F, -2.5F, 0F);
        bolt.addBox(-0.5F, -5.5F, -0.5F, 1, 12, 1, 0.0F);
        setRotateAngle(bolt, -1.570796F, 0.0F, 0.0F);
        boltTip = new ModelRenderer(this, 28, 53);
        boltTip.setRotationPoint(0F, -2.5F, 0F);
        boltTip.addBox(-0.5F, 6.5F, -0.5F, 1, 2, 1, 0.0F);
        setRotateAngle(boltTip, -1.570796F, 0.0F, 0.0F);
        fletchV = new ModelRenderer(this, 36, 46);
        fletchV.setRotationPoint(0F, -2.5F, 0F);
        fletchV.addBox(0F, -7F, -1.5F, 0, 2, 3, 0.0F);
        setRotateAngle(fletchV, -1.570796F, 0.0F, 0.0F);
        fletchH = new ModelRenderer(this, 48, 53);
        fletchH.setRotationPoint(0F, -2.5F, 0F);
        fletchH.addBox(-1.5F, -7F, 0F, 3, 2, 0, 0.0F);
        setRotateAngle(fletchH, -1.570796F, 0.0F, 0.0F);
        armR1 = new ModelRenderer(this, 46, 46);
        armR1.setRotationPoint(2F, 1.5F, -12.5F);
        armR1.addBox(0F, -1F, -1F, 6, 2, 2, 0.0F);
        setRotateAngle(armR1, 0.0F, -0.261799F, 0.0F);
        armR2 = new ModelRenderer(this, 0, 53);
        armR2.setRotationPoint(6F, 0F, 0F);
        armR2.addBox(0F, -1F, -1F, 5, 2, 2, 0.0F);
        setRotateAngle(armR2, 0.0F, -0.349066F, 0.0F);
        armR3 = new ModelRenderer(this, 32, 53);
        armR3.setRotationPoint(5F, 0F, 0F);
        armR3.addBox(0F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(armR3, 0.0F, -0.349066F, 0.0F);
        armL1 = new ModelRenderer(this, 46, 46);
        armL1.setRotationPoint(-2F, 1.5F, -12.5F);
        armL1.addBox(-6F, -1F, -1F, 6, 2, 2, 0.0F);
        setRotateAngle(armL1, 0.0F, 0.261799F, 0.0F);
        armL2 = new ModelRenderer(this, 0, 53);
        armL2.setRotationPoint(-6F, 0F, 0F);
        armL2.addBox(-5F, -1F, -1F, 5, 2, 2, 0.0F);
        setRotateAngle(armL2, 0.0F, 0.349066F, 0.0F);
        armL3 = new ModelRenderer(this, 32, 53);
        armL3.setRotationPoint(-5F, 0F, 0F);
        armL3.addBox(-4F, -0.5F, -0.5F, 4, 1, 1, 0.0F);
        setRotateAngle(armL3, 0.0F, 0.349066F, 0.0F);
        string = new ModelRenderer(this, 0, 27);
        string.setRotationPoint(0F, 0F, 0F);
        string.addBox(-0.5F, 0F, -0.5F, 1, 10, 1, 0.0F);
        cartridge = new ModelRenderer(this, 4, 27);
        cartridge.setRotationPoint(0F, 12F, -6F);
        cartridge.addBox(-2.5F, -1F, -4F, 5, 2, 8, 0.0F);
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
        chambers[0] = chamber0;
        chambers[1] = chamber1;
        chambers[2] = chamber2;
        chambers[3] = chamber3;
        chambers[4] = chamber4;
        chambers[5] = chamber5;
    }

    /** Static pose: full drum, cocked string. Used by nothing in the mod; kept for ModelBase callers. */
    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        render(f5, CrossbowAnimation.CHAMBERS, CrossbowAnimation.CHAMBERS, 0, CrossbowAnimation.NEVER, -1F);
    }

    /**
     * Draws the crossbow in its current state. Leaves the GL matrix and colour as it found them.
     *
     * @param scale          model unit in blocks (1/16)
     * @param ammo           bolts in the magazine
     * @param capacity       bolts in a full magazine
     * @param type           bolt type of the loaded magazine (fletching tint)
     * @param sinceShot      ticks since the last shot with the partial tick, or {@link CrossbowAnimation#NEVER}
     * @param reloadProgress 0..1 while a reload runs, else -1
     */
    public void render(float scale, int ammo, int capacity, int type, float sinceShot, float reloadProgress)
    {
        float drumDegrees = CrossbowAnimation.drumDegrees(ammo, capacity, sinceShot, reloadProgress);
        drum.rotateAngleZ = drumDegrees * DEG;
        gear.rotateAngleX = drumDegrees * 2F * DEG;
        key.rotateAngleX = -drumDegrees * 2F * DEG;

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

        if (CrossbowAnimation.boltsVisible(reloadProgress))
        {
            renderBolts(scale, ammo, capacity, type);
        }

        float nockZ = CrossbowAnimation.stringZ(sinceShot);
        renderString(scale, CrossbowAnimation.TIP_X, nockZ);
        renderString(scale, -CrossbowAnimation.TIP_X, nockZ);

        if (CrossbowAnimation.cartridgeVisible(reloadProgress))
        {
            GL11.glPushMatrix();
            try
            {
                GL11.glTranslatef(0F, CrossbowAnimation.cartridgeHeight(reloadProgress) * scale, 0F);
                cartridge.render(scale);
            } finally
            {
                GL11.glPopMatrix();
            }
        }
    }

    /** One bolt per loaded chamber, drawn inside the drum and chamber transforms; the fletching takes the type tint. */
    private void renderBolts(float scale, int ammo, int capacity, int type)
    {
        int color = CrossbowAnimation.typeColor(type);
        float red = (color >> 16 & 0xFF) / 255F;
        float green = (color >> 8 & 0xFF) / 255F;
        float blue = (color & 0xFF) / 255F;
        for (int chamber = 0; chamber < CrossbowAnimation.CHAMBERS; chamber++)
        {
            if (!CrossbowAnimation.chamberLoaded(chamber, ammo, capacity)) continue;
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

    /** The string box runs along +Y from the origin: move it to the limb tip, aim it at the latch, stretch it. */
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
