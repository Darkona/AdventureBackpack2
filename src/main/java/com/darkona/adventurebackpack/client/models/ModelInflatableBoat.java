package com.darkona.adventurebackpack.client.models;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.util.Utils;

/**
 * InflatableBoat - Darkona
 * Created using Tabula 4.1.0
 */
public class ModelInflatableBoat extends ModelBase
{
    public ModelRenderer boatSides1;
    public ModelRenderer boatSides2;
    public ModelRenderer boatSides3;
    public ModelRenderer boatSides4;
    public ModelRenderer boatSides5;
    public ModelRenderer EngineBody;
    public ModelRenderer Axis;
    public ModelRenderer EnginePistonLeft;
    public ModelRenderer EnginePistonRight;
    public ModelRenderer TankTop;
    public ModelRenderer TankBottom;
    public ModelRenderer TankWallRightFront;
    public ModelRenderer TankWallLeftFront;
    public ModelRenderer TankWallRightBack;
    public ModelRenderer TankWallLeftBack;
    public ModelRenderer Blade1;
    public ModelRenderer Blade2;
    public ModelRenderer Blade3;
    public ModelRenderer Blade4;

    public ModelInflatableBoat()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;
        this.boatSides4 = new ModelRenderer(this, 0, 36);
        this.boatSides4.setRotationPoint(0.0F, 4.0F, -9.0F);
        this.boatSides4.addBox(-10.0F, -7.0F, -1.0F, 20, 6, 3, 0.0F);
        this.setRotateAngle(boatSides4, 0.0F, 3.141592653589793F, 0.0F);
        this.Axis = new ModelRenderer(this, 25, 55);
        this.Axis.setRotationPoint(16.0F, 1.0F, 0.0F);
        this.Axis.addBox(-0.5F, -6.8F, -0.5F, 1, 8, 1, 0.0F);
        this.setRotateAngle(Axis, 3.141592653589793F, 0.045553093477052F, -0.4363323129985824F);
        this.Blade3 = new ModelRenderer(this, 20, 48);
        this.Blade3.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.Blade3.addBox(0.0F, -0.5F, -1.0F, 4, 1, 2, 0.0F);
        this.setRotateAngle(Blade3, -0.3490658503988659F, 0.0F, 3.141592653589793F);
        this.TankWallRightFront = new ModelRenderer(this, 16, 59);
        this.TankWallRightFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallRightFront.addBox(0.0F, 1.0F, 0.0F, 1, 4, 1, 0.0F);
        this.EnginePistonLeft = new ModelRenderer(this, 0, 55);
        this.EnginePistonLeft.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.EnginePistonLeft.addBox(1.7F, 1.0F, -1.0F, 4, 2, 2, 0.0F);
        this.setRotateAngle(EnginePistonLeft, 0.0F, -0.7853981633974483F, 0.0F);
        this.boatSides1 = new ModelRenderer(this, 0, 10);
        this.boatSides1.setRotationPoint(0.0F, 4.0F, 0.0F);
        this.boatSides1.addBox(-12.0F, -9.0F, -3.0F, 24, 18, 4, 0.0F);
        this.setRotateAngle(boatSides1, 1.5707963267948966F, 0.0F, 0.0F);
        this.boatSides2 = new ModelRenderer(this, 0, 0);
        this.boatSides2.setRotationPoint(-11.0F, 4.0F, 0.0F);
        this.boatSides2.addBox(-11.0F, -7.0F, -1.3F, 22, 6, 3, 0.0F);
        this.setRotateAngle(boatSides2, 0.0F, 4.6898742330339624F, 0.0F);
        this.TankTop = new ModelRenderer(this, 32, 58);
        this.TankTop.setRotationPoint(2.5F, -6.0F, -0.5F);
        this.TankTop.addBox(0.0F, 0.0F, 0.0F, 5, 1, 5, 0.0F);
        this.setRotateAngle(TankTop, 0.0F, -1.5707963267948966F, 0.0F);
        this.Blade1 = new ModelRenderer(this, 20, 48);
        this.Blade1.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.Blade1.addBox(0.0F, -0.5F, -1.0F, 4, 1, 2, 0.0F);
        this.setRotateAngle(Blade1, -0.3490658503988659F, 0.0F, 0.0F);
        this.TankBottom = new ModelRenderer(this, 32, 58);
        this.TankBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankBottom.addBox(0.0F, 5.0F, 0.0F, 5, 1, 5, 0.0F);
        this.boatSides3 = new ModelRenderer(this, 0, 0);
        this.boatSides3.setRotationPoint(11.0F, 4.0F, 0.0F);
        this.boatSides3.addBox(-11.0F, -7.0F, -1.0F, 22, 6, 3, 0.0F);
        this.setRotateAngle(boatSides3, 0.0F, 1.5707963267948966F, 0.0F);
        this.Blade4 = new ModelRenderer(this, 30, 48);
        this.Blade4.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.Blade4.addBox(-1.0F, -0.5F, 0.0F, 2, 1, 4, 0.0F);
        this.setRotateAngle(Blade4, -3.141592653589793F, 0.0F, 0.3490658503988659F);
        this.TankWallRightBack = new ModelRenderer(this, 16, 59);
        this.TankWallRightBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallRightBack.addBox(0.0F, 1.0F, 4.0F, 1, 4, 1, 0.0F);
        this.boatSides5 = new ModelRenderer(this, 0, 36);
        this.boatSides5.setRotationPoint(0.0F, 4.0F, 9.0F);
        this.boatSides5.addBox(-10.0F, -7.0F, -1.0F, 20, 6, 3, 0.0F);
        this.EngineBody = new ModelRenderer(this, 0, 46);
        this.EngineBody.setRotationPoint(13.3F, -3.0F, 0.0F);
        this.EngineBody.addBox(-2.0F, 0.0F, 0.0F, 4, 4, 4, 0.0F);
        this.setRotateAngle(EngineBody, 0.0F, 1.5707963267948966F, 0.0F);
        this.TankWallLeftFront = new ModelRenderer(this, 16, 59);
        this.TankWallLeftFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallLeftFront.addBox(4.0F, 1.0F, 0.0F, 1, 4, 1, 0.0F);
        this.Blade2 = new ModelRenderer(this, 30, 48);
        this.Blade2.setRotationPoint(0.0F, -7.0F, 0.0F);
        this.Blade2.addBox(-1.0F, -0.5F, 0.0F, 2, 1, 4, 0.0F);
        this.setRotateAngle(Blade2, 0.0F, 0.0F, -0.3490658503988659F);
        this.TankWallLeftBack = new ModelRenderer(this, 16, 59);
        this.TankWallLeftBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallLeftBack.addBox(4.0F, 1.0F, 4.0F, 1, 4, 1, 0.0F);
        this.EnginePistonRight = new ModelRenderer(this, 13, 55);
        this.EnginePistonRight.setRotationPoint(0.0F, 0.0F, 2.0F);
        this.EnginePistonRight.addBox(1.7F, 1.0F, -1.0F, 4, 2, 2, 0.0F);
        this.setRotateAngle(EnginePistonRight, 0.0F, -2.356194490192345F, 0.0F);
        this.Axis.addChild(this.Blade3);
        this.TankTop.addChild(this.TankWallRightFront);
        this.EngineBody.addChild(this.EnginePistonLeft);
        this.EngineBody.addChild(this.TankTop);
        this.Axis.addChild(this.Blade1);
        this.TankTop.addChild(this.TankBottom);
        this.Axis.addChild(this.Blade4);
        this.TankTop.addChild(this.TankWallRightBack);
        this.TankTop.addChild(this.TankWallLeftFront);
        this.Axis.addChild(this.Blade2);
        this.TankTop.addChild(this.TankWallLeftBack);
        this.EngineBody.addChild(this.EnginePistonRight);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        if (Utils.notNullAndInstanceOf(entity, EntityInflatableBoat.class))
        {
            EntityInflatableBoat theBoat = (EntityInflatableBoat) entity;

            if (theBoat.isMotorized() && theBoat.isInflated())
            {
                Axis.isHidden = EngineBody.isHidden = false;
            }
            else
            {
                Axis.isHidden = EngineBody.isHidden = true;
            }

            GL11.glPushMatrix();
            GL11.glScalef(theBoat.inflation, theBoat.inflation, theBoat.inflation);

            this.boatSides5.render(f5);
            this.boatSides3.render(f5);
            this.boatSides2.render(f5);
            this.boatSides1.render(f5);
            this.boatSides4.render(f5);
            this.Axis.render(f5);
            this.EngineBody.render(f5);

            GL11.glPopMatrix();
        }
        else
        {
            this.boatSides5.render(f5);
            this.boatSides3.render(f5);
            this.boatSides2.render(f5);
            this.Axis.render(f5);
            this.boatSides1.render(f5);
            this.boatSides4.render(f5);
            this.EngineBody.render(f5);
        }
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
