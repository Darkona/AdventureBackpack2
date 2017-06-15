package com.darkona.adventurebackpack.client.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.darkona.adventurebackpack.client.render.RendererStack;
import com.darkona.adventurebackpack.util.Utils;

/**
 * clockworkCrossbow - Darkona
 * Created using Tabula 4.1.0
 */
public class ModelClockworkCrossbow extends ModelBase
{
    public ModelRenderer xbowBody;
    public ModelRenderer leftArc1;
    public ModelRenderer rightArc1;
    public ModelRenderer magazine;
    public ModelRenderer handle;
    public ModelRenderer trigger;
    public ModelRenderer handle2;
    public ModelRenderer reloader1;
    public ModelRenderer wheel;
    public ModelRenderer point;
    public ModelRenderer sniperLeg1;
    public ModelRenderer sniperLeg1_1;
    public ModelRenderer hookBolt;
    public ModelRenderer leftArc2;
    public ModelRenderer leftArc3;
    public ModelRenderer stringLeft;
    public ModelRenderer rightArc2;
    public ModelRenderer rightArc3;
    public ModelRenderer stringRight;
    public ModelRenderer arrow1;
    public ModelRenderer arrow2;
    public ModelRenderer arrow3;
    public ModelRenderer reloader2;
    public ModelRenderer reloader3;
    public ModelRenderer shape37;
    public ModelRenderer shape38;
    public ModelRenderer hookHead;
    public ModelRenderer hookTooth1;
    public ModelRenderer hookTooth2;
    public ModelRenderer hookTooth3;
    public ModelRenderer hookTooth4;
    public RendererStack arrow;
    private ItemStack xbow;

    public ModelClockworkCrossbow()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.hookTooth1 = new ModelRenderer(this, 0, 0);
        this.hookTooth1.setRotationPoint(0.0F, -1.0F, 3.0F);
        this.hookTooth1.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F);
        this.reloader3 = new ModelRenderer(this, 5, 45);
        this.reloader3.setRotationPoint(1.0F, 3.0F, 0.0F);
        this.reloader3.addBox(0.0F, -1.0F, -1.0F, 5, 2, 2, 0.0F);
        this.shape38 = new ModelRenderer(this, 0, 50);
        this.shape38.setRotationPoint(-0.7F, 7.3F, -0.5F);
        this.shape38.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(shape38, 0.0F, 0.0F, -1.1344640137963142F);
        this.hookTooth4 = new ModelRenderer(this, 0, 0);
        this.hookTooth4.setRotationPoint(0.0F, 3.0F, -1.0F);
        this.hookTooth4.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F);

        this.stringLeft = new ModelRenderer(this, 56, 36);
        this.stringLeft.setRotationPoint(7.3F, 0.3F, 2.0F);
        this.stringLeft.addBox(-0.5F, 0.0F, -0.5F, 1, 22, 1, 0.0F);
        this.setRotateAngle(stringLeft, 1.5707963267948966F, -0.5235987755982988F, 0.0F);
        this.stringRight = new ModelRenderer(this, 56, 36);
        this.stringRight.setRotationPoint(7.3F, 0.3F, -2.0F);
        this.stringRight.addBox(-0.5F, 0.0F, -0.5F, 1, 22, 1, 0.0F);
        this.setRotateAngle(stringRight, -1.5707963267948966F, 0.5235987755982988F, 0.0F);

        this.wheel = new ModelRenderer(this, 32, 17);
        this.wheel.setRotationPoint(-4.0F, 4.0F, 9.0F);
        this.wheel.addBox(0.0F, -2.5F, -2.5F, 1, 5, 5, 0.0F);
        this.reloader1 = new ModelRenderer(this, 0, 41);
        this.reloader1.setRotationPoint(3.0F, 3.0F, 11.0F);
        this.reloader1.addBox(0.0F, -0.5F, -0.5F, 7, 1, 1, 0.0F);
        this.sniperLeg1 = new ModelRenderer(this, 0, 50);
        this.sniperLeg1.setRotationPoint(-2.0F, 3.0F, -12.0F);
        this.sniperLeg1.addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1, 0.0F);
        this.setRotateAngle(sniperLeg1, 0.0F, 0.0F, 0.4363323129985824F);
        this.trigger = new ModelRenderer(this, 0, 20);
        this.trigger.setRotationPoint(-1.0F, 4.0F, 9.0F);
        this.trigger.addBox(0.0F, 0.0F, 0.0F, 2, 4, 3, 0.0F);

        this.shape37 = new ModelRenderer(this, 0, 50);
        this.shape37.setRotationPoint(0.3F, 6.6F, -0.5F);
        this.shape37.addBox(0.0F, 0.0F, 0.0F, 1, 2, 1, 0.0F);
        this.setRotateAngle(shape37, 0.0F, 0.0F, 1.1344640137963142F);
        this.sniperLeg1_1 = new ModelRenderer(this, 0, 50);
        this.sniperLeg1_1.setRotationPoint(2.0F, 3.0F, -12.0F);
        this.sniperLeg1_1.addBox(-0.5F, 0.0F, -0.5F, 1, 7, 1, 0.0F);
        this.setRotateAngle(sniperLeg1_1, 0.0F, 0.0F, -0.4363323129985824F);

        this.hookTooth2 = new ModelRenderer(this, 0, 0);
        this.hookTooth2.setRotationPoint(0.0F, -1.0F, -1.0F);
        this.hookTooth2.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F);

        this.arrow1 = new ModelRenderer(this, 0, 0);
        this.arrow1.setRotationPoint(10.0F, 1.0F, 3.0F);
        this.arrow1.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.arrow2 = new ModelRenderer(this, 0, 0);
        this.arrow2.setRotationPoint(10.0F, 4.0F, 3.0F);
        this.arrow2.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);
        this.arrow3 = new ModelRenderer(this, 0, 0);
        this.arrow3.setRotationPoint(10.0F, 7.0F, 3.0F);
        this.arrow3.addBox(0.0F, 0.0F, 0.0F, 0, 0, 0, 0.0F);

        this.hookHead = new ModelRenderer(this, 0, 0);
        this.hookHead.setRotationPoint(-0.5F, -1.0F, -1.0F);
        this.hookHead.addBox(0.0F, 0.0F, 0.0F, 1, 3, 3, 0.0F);
        this.xbowBody = new ModelRenderer(this, 0, 35);
        this.xbowBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.xbowBody.addBox(-3.0F, 0.0F, -13.0F, 6, 4, 25, 0.0F);
        this.hookBolt = new ModelRenderer(this, 18, 12);
        this.hookBolt.setRotationPoint(0.5F, -1.0F, -20.0F);
        this.hookBolt.addBox(0.0F, 0.0F, 0.0F, 22, 1, 1, 0.0F);
        this.setRotateAngle(hookBolt, 0.0F, -1.5707963267948966F, 0.0F);
        this.point = new ModelRenderer(this, 45, 16);
        this.point.setRotationPoint(-2.0F, 0.0F, -16.0F);
        this.point.addBox(0.0F, 0.0F, 0.0F, 4, 2, 3, 0.0F);

        this.rightArc1 = new ModelRenderer(this, 0, 36);
        this.rightArc1.setRotationPoint(-2.0F, 0.1F, -13.0F);
        this.rightArc1.addBox(-8.0F, 0.0F, 0.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(rightArc1, 0.0F, -0.5235987755982988F, 0.0F);
        this.rightArc2 = new ModelRenderer(this, 0, 36);
        this.rightArc2.setRotationPoint(-8.0F, 0.0F, 0.0F);
        this.rightArc2.addBox(0.0F, 0.0F, -2.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(rightArc2, 0.0F, -2.367539130330308F, 0.0F);
        this.rightArc3 = new ModelRenderer(this, 0, 36);
        this.rightArc3.setRotationPoint(8.0F, 0.0F, 0.0F);
        this.rightArc3.addBox(0.0F, 0.0F, -2.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(rightArc3, 0.0F, 0.7853981633974483F, 0.0F);

        this.leftArc1 = new ModelRenderer(this, 0, 36);
        this.leftArc1.setRotationPoint(2.0F, 0.1F, -13.0F);
        this.leftArc1.addBox(0.0F, 0.0F, 0.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(leftArc1, 0.0F, 0.5235987755982988F, 0.0F);
        this.leftArc2 = new ModelRenderer(this, 0, 36);
        this.leftArc2.setRotationPoint(8.0F, 0.0F, 0.0F);
        this.leftArc2.addBox(0.0F, 0.0F, 0.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(leftArc2, 0.0F, -0.7853981633974483F, 0.0F);
        this.leftArc3 = new ModelRenderer(this, 0, 36);
        this.leftArc3.setRotationPoint(8.0F, 0.0F, 0.0F);
        this.leftArc3.addBox(0.0F, 0.0F, 0.0F, 8, 2, 2, 0.0F);
        this.setRotateAngle(leftArc3, 0.0F, -0.7853981633974483F, 0.0F);

        this.reloader2 = new ModelRenderer(this, 0, 44);
        this.reloader2.setRotationPoint(6.0F, 1.0F, 0.0F);
        this.reloader2.addBox(0.0F, -0.5F, -0.5F, 1, 4, 1, 0.0F);
        this.handle2 = new ModelRenderer(this, 39, 53);
        this.handle2.setRotationPoint(-1.0F, 0.0F, 12.0F);
        this.handle2.addBox(0.0F, 0.0F, 0.0F, 2, 4, 3, 0.0F);
        this.handle = new ModelRenderer(this, 39, 40);
        this.handle.setRotationPoint(-1.0F, 5.0F, 14.0F);
        this.handle.addBox(0.0F, -3.0F, 1.0F, 2, 10, 3, 0.0F);
        this.setRotateAngle(handle, 1.0016444577195458F, 0.0F, 0.0F);
        this.magazine = new ModelRenderer(this, 0, 16);
        this.magazine.setRotationPoint(-5.0F, 1.0F, -7.0F);
        this.magazine.addBox(0.0F, 0.0F, 0.0F, 10, 6, 12, 0.0F);
        this.hookTooth3 = new ModelRenderer(this, 0, 0);
        this.hookTooth3.setRotationPoint(0.0F, 3.0F, 3.0F);
        this.hookTooth3.addBox(0.0F, 0.0F, 0.0F, 3, 1, 1, 0.0F);

        this.hookHead.addChild(this.hookTooth1);
        this.reloader2.addChild(this.reloader3);
        this.sniperLeg1_1.addChild(this.shape38);
        this.hookHead.addChild(this.hookTooth4);
        this.leftArc3.addChild(this.stringLeft);
        this.sniperLeg1.addChild(this.shape37);
        this.leftArc1.addChild(this.leftArc2);
        this.rightArc3.addChild(this.stringRight);
        this.magazine.addChild(this.arrow3);
        this.hookHead.addChild(this.hookTooth2);
        this.magazine.addChild(this.arrow2);
        this.hookBolt.addChild(this.hookHead);
        this.rightArc1.addChild(this.rightArc2);
        this.rightArc2.addChild(this.rightArc3);
        this.magazine.addChild(this.arrow1);
        this.leftArc2.addChild(this.leftArc3);
        this.reloader1.addChild(this.reloader2);
        this.hookHead.addChild(this.hookTooth3);

        arrow = new RendererStack(this, true);
        arrow.stack = new ItemStack(Items.arrow, 1);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, ItemStack xbow)
    {
        this.xbow = xbow;
        render(entity, f, f1, f2, f3, f4, f5);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        this.wheel.render(f5);
        this.reloader1.render(f5);
        this.sniperLeg1.render(f5);
        this.trigger.render(f5);

        this.sniperLeg1_1.render(f5);
        this.xbowBody.render(f5);
        //this.hookBolt.render(f5);
        this.point.render(f5);

        this.handle2.render(f5);
        this.handle.render(1 / 18f);
        this.magazine.render(f5);

        ModelRenderer[] arrows = {arrow1, arrow2, arrow3};

        setRotateAngle(arrow, Utils.degreesToRadians(-90), Utils.degreesToRadians(0), Utils.degreesToRadians(0));
        for (ModelRenderer model : arrows)
        {
            arrow.setRotationPoint(model.rotationPointX, model.rotationPointY, model.rotationPointZ);
            setOffset(arrow, -.25f, .1f, -.2f);
            arrow.render(f5);
            setOffset(arrow, -.75f, .1f, -.2f);
            arrow.render(f5);
        }
        if (xbow != null && xbow.hasTagCompound() && xbow.stackTagCompound.hasKey("Shot"))
        {
            if ((xbow.stackTagCompound.getByte("Shot") > 0))
            {
                setRotateAngle(stringLeft, 1.5707963267948966F, -0.5235987755982988F, 0.0F);
                setRotateAngle(stringRight, -1.5707963267948966F, 0.5235987755982988F, 0.0F);
            } else
            {
                renderNormal();
            }
        } else
        {
            renderNormal();
        }
        this.leftArc1.render(f5);
        this.rightArc1.render(f5);
    }

    private void renderNormal()
    {
        setRotateAngle(stringLeft, 1.5707963267948966F, 0.0F, 0.0F);
        setRotateAngle(stringRight, -1.5707963267948966F, 0.0F, 0.0F);
        setOffset(arrow, -.0f, -.0f, -.2f);
        setRotateAngle(arrow, Utils.degreesToRadians(-90), Utils.degreesToRadians(0), Utils.degreesToRadians(90));
        arrow.setRotationPoint(xbowBody.rotationPointX, xbowBody.rotationPointY, xbowBody.rotationPointZ);
        GL11.glPushMatrix();
        GL11.glScalef(1.0f, 1.0f, 2f);
        arrow.render(1 / 14);
        GL11.glPopMatrix();
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

    public void setOffset(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.offsetX = x;
        modelRenderer.offsetY = y;
        modelRenderer.offsetZ = z;
    }
}
