package com.darkona.adventurebackpack.client.models;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import codechicken.lib.vec.Vector3;

import com.darkona.adventurebackpack.inventory.InventoryCopterPack;
import com.darkona.adventurebackpack.item.ItemCopterPack;
import com.darkona.adventurebackpack.util.Utils;

/**
 * Created on 31/12/2014
 *
 * @author Darkona
 */
public class ModelCopterPack extends ModelWearable
{
    public static ModelCopterPack instance = new ModelCopterPack();
    public ModelRenderer Base;
    public ModelRenderer EngineBody;
    public ModelRenderer TankTop;

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
    public ModelRenderer Escape1;
    public ModelRenderer Escape2;
    public ModelRenderer Escape3;
    public ModelRenderer EscapeFilter;
    private ItemStack copterPack;

    @SuppressWarnings("unchecked")
    private void init()
    {
        this.textureWidth = 64;
        this.textureHeight = 64;

        this.Base = new ModelRenderer(this, 0, 0);
        this.Base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Base.addBox(-4.0F, 0.0F, 2.0F, 8, 12, 1);

        //TANK
        this.TankTop = new ModelRenderer(this, 0, 33);
        this.TankTop.setRotationPoint(1.0F, 0.0F, 3.0F);
        this.TankTop.addBox(0.0F, 0.0F, 0.0F, 5, 1, 5, 0.0F);
        this.Base.addChild(this.TankTop);

        this.TankWallLeftFront = new ModelRenderer(this, 0, 40);
        this.TankWallLeftFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallLeftFront.addBox(4.0F, 1.0F, 0.0F, 1, 4, 1, 0.0F);
        this.TankTop.addChild(this.TankWallLeftFront);

        this.TankWallLeftBack = new ModelRenderer(this, 0, 32);
        this.TankWallLeftBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallLeftBack.addBox(4.0F, 1.0F, 4.0F, 1, 4, 1, 0.0F);
        this.TankTop.addChild(this.TankWallLeftBack);

        this.TankWallRightFront = new ModelRenderer(this, 16, 40);
        this.TankWallRightFront.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallRightFront.addBox(0.0F, 1.0F, 0.0F, 1, 4, 1, 0.0F);
        this.TankTop.addChild(this.TankWallRightFront);

        this.TankWallRightBack = new ModelRenderer(this, 16, 32);
        this.TankWallRightBack.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankWallRightBack.addBox(0.0F, 1.0F, 4.0F, 1, 4, 1, 0.0F);
        this.TankTop.addChild(this.TankWallRightBack);

        this.TankBottom = new ModelRenderer(this, 0, 41);
        this.TankBottom.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.TankBottom.addBox(0.0F, 5.0F, 0.0F, 5, 1, 5, 0.0F);
        this.TankTop.addChild(this.TankBottom);

        this.FuelLine1 = new ModelRenderer(this, 15, 48);
        this.FuelLine1.setRotationPoint(0.0F, 5.0F, 0.0F);
        this.FuelLine1.addBox(2.0F, 1.0F, 2.0F, 1, 4, 1, 0.0F);
        this.TankBottom.addChild(this.FuelLine1);

        this.FuelLine2 = new ModelRenderer(this, 0, 48);
        this.FuelLine2.setRotationPoint(0.0F, 8.0F, 0.0F);
        this.FuelLine2.addBox(1.0F, 1.0F, 2.0F, 1, 1, 1, 0.0F);
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
        this.Axis.setRotationPoint(0.0F, 8.0F, 5.75F);
        this.Axis.addBox(-0.5F, -25.0F, -0.5F, 1, 25, 1);
        //this.EngineBody.addChild(this.Axis);

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

        //ESCAPE
        this.Escape1 = new ModelRenderer(this, 9, 35);
        this.Escape1.setRotationPoint(-4.0F, 9.0F, 4.0F);
        this.Escape1.addBox(0.0F, 0.0F, 0.0F, 2, 1, 1);
        this.Base.addChild(this.Escape1);

        this.Escape2 = new ModelRenderer(this, 38, 40);
        this.Escape2.setRotationPoint(-4.0F, 0.0F, 4.0F);
        this.Escape2.addBox(0.0F, 0.0F, 0.0F, 1, 9, 1);
        this.Base.addChild(this.Escape2);

        this.Escape3 = new ModelRenderer(this, 6, 24);
        this.Escape3.setRotationPoint(-4.0F, 0.0F, 5.0F);
        this.Escape3.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2);
        this.Base.addChild(this.Escape3);

        this.EscapeFilter = new ModelRenderer(this, 35, 28);
        this.EscapeFilter.setRotationPoint(-4.4F, 2.0F, 3.5F);
        this.EscapeFilter.addBox(0.0F, 0.0F, 0.0F, 2, 5, 2);
        this.Base.addChild(this.EscapeFilter);

        this.bipedBody.addChild(this.Base);

        float offsetZ = 0.1F;
        float offsetY = 0.0F;
        for (ModelRenderer part : (List<ModelRenderer>) bipedBody.childModels)
        {
            setOffset(part, part.offsetX + 0, part.offsetY + offsetY, part.offsetZ + offsetZ);
        }
    }

    public ModelCopterPack setWearable(ItemStack wearable)
    {
        this.copterPack = wearable;
        return this;
    }

    public ModelCopterPack(ItemStack wearable)
    {
        this.copterPack = wearable;
        init();
    }

    public ModelCopterPack()
    {
        init();
    }

    private void renderCopterPack(Entity entity, float scale)
    {
        InventoryCopterPack copterInv = new InventoryCopterPack(this.copterPack);
        copterInv.openInventory();
        Axis.isHidden = true;
        if (copterPack != null && copterPack.stackTagCompound != null && copterPack.stackTagCompound.hasKey("status"))
        {
            if (copterPack.stackTagCompound.getByte("status") != ItemCopterPack.OFF_MODE)
            {
                Axis.isHidden = false;
                int degrees;
                if (entity.onGround || (entity.isSneaking()))
                {
                    degrees = 16;
                } else
                {
                    degrees = entity.motionY > 0 ? 36 : 28;
                }
                float deg = Utils.radiansToDegrees(this.Axis.rotateAngleY);
                this.Axis.rotateAngleY = (deg <= 360 + degrees) ? Utils.degreesToRadians(deg + degrees) : 0;
            }
        }
        this.Base.render(scale);
        this.Axis.render(scale);
        renderFluidInTank(copterInv.getFuelTank(), new Vector3(0, .25f, 0), new Vector3(.25f, 0, .25f), new Vector3(0f, 0.0625f, 0.0f), TankTop);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, ItemStack stack)
    {
        this.copterPack = stack;
        render(entity, f, f1, f2, f3, f4, f5);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        isSneak = (entity != null && entity.isSneaking());

        if (entity == null) Axis.isHidden = true;
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        if (entity != null && entity instanceof EntityPlayer)
        {
            GL11.glPushMatrix();

            GL11.glTranslatef(bipedBody.offsetX, bipedBody.offsetY, bipedBody.offsetZ);
            GL11.glColor4f(1, 1, 1, 1);

            if (bipedBody.rotateAngleX == 0.0F && bipedBody.rotateAngleY == 0.0F && bipedBody.rotateAngleZ == 0.0F)
            {
                if (bipedBody.rotationPointX == 0.0F && bipedBody.rotationPointY == 0.0F && bipedBody.rotationPointZ == 0.0F)
                {
                    renderCopterPack(entity, f5);

                } else
                {
                    GL11.glTranslatef(bipedBody.rotationPointX * f5, bipedBody.rotationPointY * f5, bipedBody.rotationPointZ * f5);
                    renderCopterPack(entity, f5);
                    GL11.glTranslatef(-bipedBody.rotationPointX * f5, -bipedBody.rotationPointY * f5, -bipedBody.rotationPointZ * f5);
                }
            } else
            {
                GL11.glPushMatrix();
                GL11.glTranslatef(bipedBody.rotationPointX * f5, bipedBody.rotationPointY * f5, bipedBody.rotationPointZ * f5);

                if (bipedBody.rotateAngleZ != 0.0F)
                {
                    GL11.glRotatef(bipedBody.rotateAngleZ * (180F / (float) Math.PI), 0.0F, 0.0F, 1.0F);
                }

                if (bipedBody.rotateAngleY != 0.0F)
                {
                    GL11.glRotatef(bipedBody.rotateAngleY * (180F / (float) Math.PI), 0.0F, 1.0F, 0.0F);
                }

                if (bipedBody.rotateAngleX != 0.0F)
                {
                    GL11.glRotatef(bipedBody.rotateAngleX * (180F / (float) Math.PI), 1.0F, 0.0F, 0.0F);
                }
                renderCopterPack(entity, f5);
                GL11.glPopMatrix();
            }
            GL11.glTranslatef(-bipedBody.offsetX, -bipedBody.offsetY, -(bipedBody.offsetZ));
            GL11.glPopMatrix();
        }
    }
}