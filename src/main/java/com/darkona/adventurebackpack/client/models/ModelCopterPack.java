package com.darkona.adventurebackpack.client.models;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

/**
 * Created on 31/12/2014
 *
 * @author Darkona
 */
public class ModelCopterPack extends ModelBiped
{

    public static ModelCopterPack instance = new ModelCopterPack();;
    public ModelRenderer Base;
    public ModelRenderer EngineBody;
    public ModelRenderer TankTop;
    public ModelRenderer ChestTop;
    public ModelRenderer EnginePistonLeft;
    public ModelRenderer EnginePistonRight;
    public ModelRenderer Axis;
    public ModelRenderer Blade1;
    public ModelRenderer Blade2;
    public ModelRenderer Blade3;
    public ModelRenderer Blade4;
    public ModelRenderer TankWallLeftBack;
    public ModelRenderer TankWallRightBack;
    public ModelRenderer TankWallLeftFront;
    public ModelRenderer TankWallRightFront;
    public ModelRenderer TankBottom;
    public ModelRenderer FuelLine1;
    public ModelRenderer FuelLine2;

    public ModelCopterPack() {


        this.textureWidth = 64;
        this.textureHeight = 64;

        this.Base = new ModelRenderer(this, 0, 0);
        this.Base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Base.addBox(-4.0F, 0.0F, 2.0F, 8, 12, 1);


        //TANK

        this.TankTop = new ModelRenderer(this, 0, 33);
        this.TankTop.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankTop.addBox(1.0F, 0.0F, 3.0F, 5, 1, 5);
        this.Base.addChild(this.TankTop);

        this.TankWallLeftFront = new ModelRenderer(this, 0, 40);
        this.TankWallLeftFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallLeftFront.addBox(5.0F, 1.0F, 3.0F, 1, 4, 1);
        this.TankTop.addChild(this.TankWallLeftFront);

        this.TankWallLeftBack = new ModelRenderer(this, 16, 40);
        this.TankWallLeftBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallLeftBack.addBox(5.0F, 1.0F, 7.0F, 1, 4, 1);
        this.TankTop.addChild(this.TankWallLeftBack);

        this.TankWallRightFront = new ModelRenderer(this, 0, 32);
        this.TankWallRightFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallRightFront.addBox(1.0F, 1.0F, 3.0F, 1, 4, 1);
        this.TankTop.addChild(this.TankWallRightFront);

        this.TankWallRightBack = new ModelRenderer(this, 16, 32);
        this.TankWallRightBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallRightBack.addBox(1.0F, 1.0F, 7.0F, 1, 4, 1);
        this.TankTop.addChild(this.TankWallRightBack);

        this.TankBottom = new ModelRenderer(this, 0, 41);
        this.TankBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankBottom.addBox(1.0F, 5.0F, 3.0F, 5, 1, 5);
        this.TankTop.addChild(this.TankBottom);

        this.FuelLine1 = new ModelRenderer(this, 15, 48);
        this.FuelLine1.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.FuelLine1.addBox(3.0F, 1.0F, 4.0F, 1, 4, 1);
        this.TankBottom.addChild(this.FuelLine1);

        this.FuelLine2 = new ModelRenderer(this, 0, 48);
        this.FuelLine2.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.FuelLine2.addBox(2.0F, 1.0F, 4.0F, 1, 1, 1);
        this.TankBottom.addChild(this.FuelLine2);


        //ENGINE
        this.EngineBody = new ModelRenderer(this, 0, 23);
        this.EngineBody.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.EngineBody.addBox(-2.0F, 8.0F, 3.0F, 4, 4, 4);
        this.Base.addChild(this.EngineBody);

        this.EnginePistonLeft = new ModelRenderer(this, 0, 18);
        this.EnginePistonLeft.setRotationPoint(0.0F, 8.0F, 5.0F);
        this.EnginePistonLeft.addBox(1.7F, 1.0F, -1.0F, 4, 2, 2);
        this.setRotateAngle(EnginePistonLeft, 0.0F, -0.7853981633974483F, 0.0F);
        this.EngineBody.addChild(this.EnginePistonLeft);

        this.EnginePistonRight = new ModelRenderer(this, 13, 18);
        this.EnginePistonRight.setRotationPoint(0.0F, 8.0F, 5.0F);
        this.EnginePistonRight.addBox(1.7F, 1.0F, -1.0F, 4, 2, 2);
        this.setRotateAngle(EnginePistonRight, 0.0F, -2.356194490192345F, 0.0F);
        this.EngineBody.addChild(this.EnginePistonRight);

        this.Axis = new ModelRenderer(this, 25, 22);
        this.Axis.setRotationPoint(0.0F, 8.0F, 5.0F);
        this.Axis.addBox(-0.5F, -25.0F, -0.5F, 1, 25, 1);
        this.EngineBody.addChild(this.Axis);

        this.Blade1 = new ModelRenderer(this, 29, 0);
        this.Blade1.setRotationPoint(0.0F, -25.0F, 0.0F);
        this.Blade1.addBox(0.0F, -0.5F, -1.0F, 15, 1, 2);
        this.Axis.addChild(this.Blade1);

        this.Blade2 = new ModelRenderer(this, 30, 4);
        this.Blade2.setRotationPoint(0.0F, -25.0F, 0.0F);
        this.Blade2.addBox(-1.0F, -0.5F, 0.0F, 2, 1, 15);
        this.Axis.addChild(this.Blade2);

        this.Blade3 = new ModelRenderer(this, 29, 0);
        this.Blade3.setRotationPoint(0.0F, -25.0F, 0.0F);
        this.Blade3.addBox(0.0F, -0.5F, -1.0F, 15, 1, 2);
        this.setRotateAngle(Blade3, 0.0F, 0.0F, 3.141592653589793F);
        this.Axis.addChild(this.Blade3);

        this.Blade4 = new ModelRenderer(this, 30, 4);
        this.Blade4.setRotationPoint(0.0F, -25.0F, 0.0F);
        this.Blade4.addBox(-1.0F, -0.5F, 0.0F, 2, 1, 15);
        this.setRotateAngle(Blade4, -3.141592653589793F, 0.0F, 0.0F);
        this.Axis.addChild(this.Blade4);

        this.bipedBody.addChild(this.Base);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
        setRotationAngles(f,f1,f2,f3,f4,f5,entity);
        this.Base.render(f5);

        if(entity != null && entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            isSneak = player.isSneaking();
            if(!player.onGround)
            {
                float rad = this.Axis.rotateAngleY;
                float deg = rad * 57.2957795f;
                rad = (deg < 360) ? (deg + 16) / 57.2957795f : 0;
                this.Axis.rotateAngleY = rad;
            }
        }
    }

    /**
     * This is a helper function from Tabula to set the rotation of model parts
     */
    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}