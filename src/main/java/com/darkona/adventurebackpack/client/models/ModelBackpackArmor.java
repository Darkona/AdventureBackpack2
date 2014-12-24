package com.darkona.adventurebackpack.client.models;

import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.darkona.adventurebackpack.client.render.RendererStack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

import java.util.List;

/**
 * Created on 17/12/2014
 *
 * @author Darkona
 */
public class ModelBackpackArmor extends ModelBiped
{
    public static final ModelBackpackArmor instance = new ModelBackpackArmor();

    public ModelRenderer mainBody;
    public ModelRenderer tankLeftTop;
    public ModelRenderer tankRightTop;
    public ModelRenderer bed;
    public ModelRenderer villagerNose;
    public ModelRenderer pigNose;
    public ModelRenderer ocelotNose;
    public ModelRenderer leftStrap;
    public ModelRenderer rightStrap;
    public ModelRenderer top;
    public ModelRenderer bottom;
    public ModelRenderer pocketFace;
    public ModelRenderer tankLeftBottom;
    public ModelRenderer tankLeftWall4;
    public ModelRenderer tankLeftWall3;
    public ModelRenderer tankLeftWall2;
    public ModelRenderer tankLeftWall1;
    public ModelRenderer tankRightBottom;
    public ModelRenderer tankRightWall2;
    public ModelRenderer tankRightWall1;
    public ModelRenderer tankRightWall3;
    public ModelRenderer tankRightWall4;
    public ModelRenderer bedStrapLeftMid;
    public ModelRenderer bedStrapRightBottom;
    public ModelRenderer bedStrapLeftBottom;
    public ModelRenderer bedStrapRightMid;
    public ModelRenderer bedStrapRightTop;
    public ModelRenderer bedStrapLeftTop;
    RendererStack lowerTool;
    RendererStack upperTool;
    private IAdvBackpack backpack;
    private float scale = -1;

    public ModelBackpackArmor() {
        this.textureWidth = 128;
        this.textureHeight = 64;

        //Main Backpack

        this.mainBody = new ModelRenderer(this, 0, 9);
        this.mainBody.addBox(-5.0F, 0.0F, -3.0F, 10, 9, 5);
        this.mainBody.setRotationPoint(0.0F, 0.0F, 0.0F);

        this.leftStrap = new ModelRenderer(this, 21, 24);
        this.leftStrap.setRotationPoint(3.0F, 0.0F, -3.0F);
        this.leftStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.leftStrap);

        this.rightStrap = new ModelRenderer(this, 26, 24);
        this.rightStrap.setRotationPoint(-4.0F, 0.0F, -3.0F);
        this.rightStrap.addBox(0.0F, 0.0F, -1.0F, 1, 8, 1);
        this.mainBody.addChild(this.rightStrap);

        this.top = new ModelRenderer(this, 0, 0);
        this.top.setRotationPoint(0.0F, 0.0F, -3.0F);
        this.top.addBox(-5.0F, -3.0F, 0.0F, 10, 3, 5);
        this.mainBody.addChild(this.top);

        this.bottom = new ModelRenderer(this, 0, 34);
        this.bottom.setRotationPoint(-5.0F, 9.0F, -3.0F);
        this.bottom.addBox(0.0F, 0.0F, 0.0F, 10, 1, 4);
        this.mainBody.addChild(this.bottom);

        this.pocketFace = new ModelRenderer(this, 0, 24);
        this.pocketFace.setRotationPoint(0.0F, 6.9F, 2.0F);
        this.pocketFace.addBox(-4.0F, -6.0F, 0.0F, 8, 6, 2);
        this.mainBody.addChild(this.pocketFace);

        //Left Tank

        this.tankLeftTop = new ModelRenderer(this, 0, 40);
        this.tankLeftTop.setRotationPoint(5.0F, -1.0F, -2.5F);
        this.tankLeftTop.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);

        this.tankLeftBottom = new ModelRenderer(this, 0, 46);
        this.tankLeftBottom.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.tankLeftBottom.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);
        this.tankLeftTop.addChild(this.tankLeftBottom);

        this.tankLeftWall1 = new ModelRenderer(this, 0, 52);
        this.tankLeftWall1.setRotationPoint(3.0F, -8.0F, 0.0F);
        this.tankLeftWall1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall1);

        this.tankLeftWall2 = new ModelRenderer(this, 5, 52);
        this.tankLeftWall2.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.tankLeftWall2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall2);

        this.tankLeftWall3 = new ModelRenderer(this, 10, 52);
        this.tankLeftWall3.setRotationPoint(0.0F, -8.0F, 3.0F);
        this.tankLeftWall3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall3);

        this.tankLeftWall4 = new ModelRenderer(this, 15, 52);
        this.tankLeftWall4.setRotationPoint(3.0F, -8.0F, 3.0F);
        this.tankLeftWall4.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankLeftBottom.addChild(this.tankLeftWall4);

        //Right Tank

        this.tankRightTop = new ModelRenderer(this, 17, 40);
        this.tankRightTop.setRotationPoint(-9.0F, -1.0F, -2.5F);
        this.tankRightTop.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);

        this.tankRightBottom = new ModelRenderer(this, 17, 46);
        this.tankRightBottom.setRotationPoint(0.0F, 9.0F, 0.0F);
        this.tankRightBottom.addBox(0.0F, 0.0F, 0.0F, 4, 1, 4);
        this.tankRightTop.addChild(this.tankRightBottom);

        this.tankRightWall1 = new ModelRenderer(this, 22, 52);
        this.tankRightWall1.setRotationPoint(3.0F, -8.0F, 3.0F);
        this.tankRightWall1.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall1);

        this.tankRightWall2 = new ModelRenderer(this, 27, 52);
        this.tankRightWall2.setRotationPoint(3.0F, -8.0F, 0.0F);
        this.tankRightWall2.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall2);

        this.tankRightWall3 = new ModelRenderer(this, 32, 52);
        this.tankRightWall3.setRotationPoint(0.0F, -8.0F, 3.0F);
        this.tankRightWall3.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall3);

        this.tankRightWall4 = new ModelRenderer(this, 37, 52);
        this.tankRightWall4.setRotationPoint(0.0F, -8.0F, 0.0F);
        this.tankRightWall4.addBox(0.0F, 0.0F, 0.0F, 1, 8, 1);
        this.tankRightBottom.addChild(this.tankRightWall4);

        //Bed

        this.bed = new ModelRenderer(this, 31, 0);
        this.bed.setRotationPoint(-7.0F, 7.0F, 2.0F);
        this.bed.addBox(0.0F, 0.0F, 0.0F, 14, 2, 2);

        this.bedStrapRightTop = new ModelRenderer(this, 40, 5);
        this.bedStrapRightTop.setRotationPoint(2.0F, -1.0F, 0.0F);
        this.bedStrapRightTop.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3);
        this.bed.addChild(this.bedStrapRightTop);

        this.bedStrapRightMid = new ModelRenderer(this, 38, 10);
        this.bedStrapRightMid.setRotationPoint(2.0F, 0.0F, 2.0F);
        this.bedStrapRightMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bed.addChild(this.bedStrapRightMid);

        this.bedStrapRightBottom = new ModelRenderer(this, 42, 15);
        this.bedStrapRightBottom.setRotationPoint(2.0F, 2.0F, -1.0F);
        this.bedStrapRightBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 3);
        this.bed.addChild(this.bedStrapRightBottom);

        this.bedStrapLeftTop = new ModelRenderer(this, 31, 5);
        this.bedStrapLeftTop.setRotationPoint(11.0F, -1.0F, 0.0F);
        this.bedStrapLeftTop.addBox(0.0F, 0.0F, 0.0F, 1, 1, 3);
        this.bed.addChild(this.bedStrapLeftTop);

        this.bedStrapLeftMid = new ModelRenderer(this, 31, 10);
        this.bedStrapLeftMid.setRotationPoint(10.0F, 0.0F, 2.0F);
        this.bedStrapLeftMid.addBox(0.0F, 0.0F, 0.0F, 2, 3, 1);
        this.bed.addChild(this.bedStrapLeftMid);

        this.bedStrapLeftBottom = new ModelRenderer(this, 31, 15);
        this.bedStrapLeftBottom.setRotationPoint(10.0F, 2.0F, -1.0F);
        this.bedStrapLeftBottom.addBox(0.0F, 0.0F, 0.0F, 2, 1, 3);
        this.bed.addChild(this.bedStrapLeftBottom);

        //Noses

        this.villagerNose = new ModelRenderer(this, 64, 0);
        this.villagerNose.setRotationPoint(-1.0F, 4.0F, 4.0F);
        this.villagerNose.addBox(0.0F, 0.0F, 0.0F, 2, 4, 2);

        ocelotNose = new ModelRenderer(this, 74, 0);
        ocelotNose.setRotationPoint(-1.0F, 4.0F, 4.0F);
        ocelotNose.addBox(0.0F, 0.0F, 0.0F, 3, 2, 1);

        pigNose = new ModelRenderer(this, 74, 0);
        pigNose.setRotationPoint(-2.0F, 4.0F, 4.0F);
        pigNose.addBox(0.0F, 0.0F, 0.0F, 4, 3, 1);

        lowerTool = new RendererStack(this, true);
        upperTool = new RendererStack(this, false);



        bipedBody.addChild(mainBody);
        bipedBody.addChild(bed);
        bipedBody.addChild(tankLeftTop);
        bipedBody.addChild(tankRightTop);
        bipedBody.addChild(villagerNose);
        bipedBody.addChild(ocelotNose);
        bipedBody.addChild(pigNose);
        mainBody.addChild(lowerTool);
        mainBody.addChild(upperTool);


        float offsetZ = 0.3F;
        float offsetY = 0.2F;
        for(ModelRenderer part : (List<ModelRenderer>)bipedBody.childModels){
          //  setRotationPoins(part, part.rotationPointX, part.rotationPointY + offsetY, part.rotationPointZ + offsetZ);
            setOffset(part,part.offsetX+0,part.offsetY + offsetY,part.offsetZ + offsetZ);
        }
    }

    public ModelBackpackArmor setBackpack(IAdvBackpack backpack)
    {
        this.backpack = backpack;
        return instance;
    }

    private void startBlending()
    {
        //GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_CULL_FACE);
    }

    private void stopBlending()
    {
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_BLEND);
       // GL11.glPopMatrix();
    }
    private void renderBackpack(Float scale){

        backpack.openInventory();
        String color = backpack.getColorName();
        for(ModelRenderer model : (List<ModelRenderer>)bipedBody.childModels)
        {
            model.mirror = false;
        }

        lowerTool.setRotationPoint(-.5F, .10F, .3F);
        setOffset(lowerTool, -.28F, 0.8F, -.1F );
        setOffset(upperTool, 0.0f, 0.04f, 0.25f );

        lowerTool.stack = backpack.getStackInSlot(Constants.lowerTool);
        upperTool.stack = backpack.getStackInSlot(Constants.upperTool);

        if(color.equals("Quartz") || color.equals("Slime") || color.equals("Snow")){
            startBlending();
            this.mainBody.render(scale);
            stopBlending();
        }else{
            this.mainBody.render(scale);
        }

        GL11.glPushMatrix();

        tankLeftTop.render(scale);
        tankRightTop.render(scale);

        bed.render(scale);
        if(color.equals("Pig") || color.equals("Horse")){
            pigNose.render(scale);
        }
        if(color.equals("Villager") || color.equals("IronGolem")){
            villagerNose.render(scale);
        }
        if(color.equals("Ocelot")){
            ocelotNose.render(scale);
        }
        GL11.glPopMatrix();


        GL11.glPushMatrix();
        GL11.glTranslatef(bipedBody.offsetX + 0, bipedBody.offsetY + 0.2F, bipedBody.offsetZ + 0.3f);
        renderFluidInTank(backpack.getLeftTank(), -.17f, .1f, .13f, tankLeftTop);
        renderFluidInTank(backpack.getRightTank(), .41f, .1f, .13f, tankRightTop);
        GL11.glPopMatrix();
    }

    private void renderFluidInTank(FluidTank tank,  float offsetX, float offsetY, float offsetZ, ModelRenderer parent){
        //Side true is left, false is right
        if (tank != null && tank.getFluid() != null && tank.getFluid().getFluid().getIcon() != null)
        {
            //Size of the cuboid
            //Y-- is up, Y++ is down
            float minX = 0f;
            float minY = 0.5f;
            float minZ = 0f;

            float maxX = 0.17f;
            float maxY = 0f;
            float maxZ = 0.17f;
            Vector3 victor = new Vector3(
                        (parent.rotationPointX * 0.1f + offsetX), //
                        (parent.rotationPointY * 0.1f + offsetY),
                        (parent.rotationPointZ * 0.1f + offsetZ));

            Cuboid6 cat = new Cuboid6(minX, minY, minZ, maxX, maxY, maxZ);
            //Thanks Chickenbones!
            RenderUtils.renderFluidCuboid(tank.getFluid(), cat.add(victor), ((1.0F * tank.getFluidAmount()) / (1.0F * Constants.basicTankCapacity)), 0.8);
        }
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {

        isSneak = ((entity != null) ? ((EntityLivingBase) entity).isSneaking() : false);
        float oV = (isSneak) ? 0 : .3F;

        float scale = f5 * 0.9f;
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        GL11.glPushMatrix();

        GL11.glTranslatef(bipedBody.offsetX, bipedBody.offsetY, bipedBody.offsetZ);
        GL11.glColor4f(1, 1, 1, 1);

        if (bipedBody.rotateAngleX == 0.0F && bipedBody.rotateAngleY == 0.0F && bipedBody.rotateAngleZ == 0.0F)
        {
            if (bipedBody.rotationPointX == 0.0F && bipedBody.rotationPointY == 0.0F && bipedBody.rotationPointZ == 0.0F)
            {
                renderBackpack(scale);
            }
            else
            {
                GL11.glTranslatef(bipedBody.rotationPointX * f5, bipedBody.rotationPointY * f5, bipedBody.rotationPointZ * f5);
                renderBackpack(scale);
                GL11.glTranslatef(-bipedBody.rotationPointX * f5, -bipedBody.rotationPointY * f5, -bipedBody.rotationPointZ * f5);
            }
        }
        else
        {
            GL11.glPushMatrix();
            GL11.glTranslatef(bipedBody.rotationPointX * f5, bipedBody.rotationPointY * f5, bipedBody.rotationPointZ * f5);

            if (bipedBody.rotateAngleZ != 0.0F)
            {
                GL11.glRotatef(bipedBody.rotateAngleZ * (180F / (float)Math.PI), 0.0F, 0.0F, 1.0F);
            }

            if (bipedBody.rotateAngleY != 0.0F)
            {
                GL11.glRotatef(bipedBody.rotateAngleY * (180F / (float)Math.PI), 0.0F, 1.0F, 0.0F);
            }

            if (bipedBody.rotateAngleX != 0.0F)
            {
                GL11.glRotatef(bipedBody.rotateAngleX * (180F / (float)Math.PI), 1.0F, 0.0F, 0.0F);
            }
            renderBackpack(scale);
            GL11.glPopMatrix();
        }
        GL11.glTranslatef(-bipedBody.offsetX, -bipedBody.offsetY, -(bipedBody.offsetZ));
        GL11.glPopMatrix();
    }

    private void setRotationPoins(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotationPointX = x;
        modelRenderer.rotationPointY = y;
        modelRenderer.rotationPointZ = z;
    }

    public void setOffset(ModelRenderer modelRenderer, float x, float y, float z){
        modelRenderer.offsetX = x;
        modelRenderer.offsetY = y;
        modelRenderer.offsetZ = z;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float v1, float v2, float v3, float v4, float v5, float v6, Entity entity)
    {
        super.setRotationAngles(v1, v2, v3, v4, v5, v6, entity);
    }

}
