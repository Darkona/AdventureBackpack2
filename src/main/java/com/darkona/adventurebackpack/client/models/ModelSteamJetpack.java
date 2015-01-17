package com.darkona.adventurebackpack.client.models;

import codechicken.lib.vec.Vector3;
import com.darkona.adventurebackpack.inventory.InventorySteamJetpack;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.List;


public class ModelSteamJetpack extends ModelWearable {
    public ModelRenderer Base;
    public ModelRenderer tubeStraightLeft;
    public ModelRenderer tubeStraightRight;
    public ModelRenderer tubeEndLeft;
    public ModelRenderer tubeEndRight;
    public ModelRenderer tankWallLeft;
    public ModelRenderer tankWallRight;
    public ModelRenderer fireBox;
    public ModelRenderer tankTop;
    public ModelRenderer tankBottom;
    public ModelRenderer pressureTank;
    public ModelRenderer waterTube1;
    public ModelRenderer waterTube2;
    public ModelRenderer tubeBendLeft;
    public ModelRenderer tubeBendRight;
    private ItemStack jetpack;

    @SuppressWarnings("unchecked")
    public ModelSteamJetpack() {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.tubeStraightRight = new ModelRenderer(this, 0, 14);
        this.tubeStraightRight.setRotationPoint(-3.0F, 1.0F, 8.3F);
        this.tubeStraightRight.addBox(-5.8F, 0.0F, 0.0F, 6, 1, 1, 0.0F);

        this.tubeEndLeft = new ModelRenderer(this, 0, 17);
        this.tubeEndLeft.setRotationPoint(7.6F, 1.7F, 8.3F);
        this.tubeEndLeft.addBox(0.0F, 0.0F, 0.0F, 1, 4, 1, -0.1F);

        this.setRotateAngle(tubeEndLeft, 0.0F, 0.0F, -0.2617993877991494F);
        this.tankBottom = new ModelRenderer(this, 10, 20);
        this.tankBottom.setRotationPoint(-1.0F, 8.0F, 3.0F);
        this.tankBottom.addBox(-3.0F, 1.0F, 0.0F, 3, 1, 3, 0.0F);
        this.tubeBendLeft = new ModelRenderer(this, 0, 14);
        this.tubeBendLeft.setRotationPoint(-2.0F, 1.0F, 2.0F);
        this.tubeBendLeft.addBox(0.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.setRotateAngle(tubeBendLeft, 0.0F, -0.8080874436733745F, 0.0F);
        this.waterTube2 = new ModelRenderer(this, 10, 17);
        this.waterTube2.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.waterTube2.addBox(-2.0F, 1.0F, 1.0F, 2, 1, 1, 0.0F);
        this.tubeEndRight = new ModelRenderer(this, 0, 17);
        this.tubeEndRight.setRotationPoint(-7.7F, 1.6F, 8.3F);
        this.tubeEndRight.addBox(-1.0F, 0.0F, 0.0F, 1, 4, 1, -0.1F);
        this.setRotateAngle(tubeEndRight, 0.0F, 0.0F, 0.2617993877991494F);
        this.Base = new ModelRenderer(this, 0, 0);
        this.Base.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.Base.addBox(-4.0F, 0.0F, 2.0F, 8, 12, 1, 0.0F);
        this.waterTube1 = new ModelRenderer(this, 10, 17);
        this.waterTube1.setRotationPoint(0.0F, 1.0F, 0.0F);
        this.waterTube1.addBox(-2.0F, 1.0F, 1.0F, 1, 1, 1, 0.0F);
        this.tubeBendRight = new ModelRenderer(this, 0, 14);
        this.tubeBendRight.setRotationPoint(-2.0F, 1.0F, 2.0F);
        this.tubeBendRight.addBox(-5.0F, 0.0F, 0.0F, 5, 1, 1, 0.0F);
        this.setRotateAngle(tubeBendRight, 0.0F, 0.7976154681614086F, 0.0F);
        this.tankWallLeft = new ModelRenderer(this, 3, 23);
        this.tankWallLeft.setRotationPoint(-1.0F, 1.0F, 1.0F);
        this.tankWallLeft.addBox(0.0F, 6.0F, 2.0F, 1, 1, 3, 0.0F);
        this.tankWallRight = new ModelRenderer(this, 5, 17);
        this.tankWallRight.setRotationPoint(-1.0F, 1.0F, 3.0F);
        this.tankWallRight.addBox(-3.0F, 0.0F, 2.0F, 1, 8, 1, 0.0F);
        this.pressureTank = new ModelRenderer(this, 19, 0);
        this.pressureTank.setRotationPoint(2.0F, 0.0F, 3.0F);
        this.pressureTank.addBox(-3.0F, 0.0F, 0.0F, 5, 7, 3, 0.0F);
        this.fireBox = new ModelRenderer(this, 26, 25);
        this.fireBox.setRotationPoint(2.0F, 8.0F, 3.0F);
        this.fireBox.addBox(-3.0F, 0.0F, 0.0F, 5, 4, 3, 0.0F);
        this.tankTop = new ModelRenderer(this, 10, 20);
        this.tankTop.setRotationPoint(-4.0F, 0.0F, 3.0F);
        this.tankTop.addBox(0.0F, 0.0F, 0.0F, 3, 1, 3, 0.0F);
        this.tubeStraightLeft = new ModelRenderer(this, 0, 14);
        this.tubeStraightLeft.setRotationPoint(2.7F, 1.0F, 8.3F);
        this.tubeStraightLeft.addBox(0.0F, 0.0F, 0.0F, 6, 1, 1, 0.0F);

        this.Base.addChild(this.tankBottom);
        this.pressureTank.addChild(this.tubeBendLeft);
        this.waterTube1.addChild(this.waterTube2);
        this.tankBottom.addChild(this.waterTube1);
        this.pressureTank.addChild(this.tubeBendRight);
        this.Base.addChild(this.pressureTank);
        this.Base.addChild(this.fireBox);
        this.Base.addChild(this.tankTop);

        bipedBody.addChild(Base);
        bipedBody.addChild(tubeStraightLeft);
        bipedBody.addChild(tubeStraightRight);
        bipedBody.addChild(tubeEndLeft);
        bipedBody.addChild(tubeEndRight);
        bipedBody.addChild(tankWallLeft);
        bipedBody.addChild(tankWallRight);


        float offsetZ = 0.08F;
        float offsetY = 0.0F;
        for (ModelRenderer part : (List<ModelRenderer>) bipedBody.childModels)
        {
            setOffset(part, part.offsetX + 0, part.offsetY + offsetY, part.offsetZ + offsetZ);
        }
    }

    public ModelSteamJetpack setWearable(ItemStack wearable)
    {
        this.jetpack = wearable;
        return this;
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, ItemStack stack)
    {
        this.jetpack = stack;
        render(entity, f, f1,f2,f3,f4,f5);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        isSneak = (entity != null && entity.isSneaking());

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
                    renderSteamPack(f5);
                } else
                {
                    GL11.glTranslatef(bipedBody.rotationPointX * f5, bipedBody.rotationPointY * f5, bipedBody.rotationPointZ * f5);
                    renderSteamPack(f5);
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
                renderSteamPack(f5);
                GL11.glPopMatrix();
            }
            GL11.glTranslatef(-bipedBody.offsetX, -bipedBody.offsetY, -(bipedBody.offsetZ));
            GL11.glPopMatrix();
        }

    }

    public void renderSteamPack(float f5)
    {
        InventorySteamJetpack inv = new InventorySteamJetpack(jetpack);

        this.fireBox.setTextureOffset((inv.getStatus() == 0) ? 9 : 26, 25);

        tubeStraightRight.render(f5);
        tubeEndLeft.render(f5);
        tubeEndRight.render(f5);
        Base.render(f5);
        tankWallLeft.render(f5);
        tankWallRight.render(f5);
        tubeStraightLeft.render(f5);

        GL11.glPushMatrix();
        renderFluidInTank(inv.getWaterTank(), new Vector3(0, .5f, 0), new Vector3(.155f, 0, .135f), new Vector3(0.18f, 0.0625f, -0.045f), tankTop);
        GL11.glPopMatrix();

    }

}
