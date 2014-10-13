package com.darkona.adventurebackpack.models;

/**
 * Created by Darkona on 11/10/2014.
 */

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.darkona.adventurebackpack.client.render.RendererStack;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.util.LogHelper;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;


public class ModelAdventureBackpackArmor extends ModelBiped {
    public static final ModelAdventureBackpackArmor instance = new ModelAdventureBackpackArmor();
    ModelRenderer main;
    ModelRenderer side;
    ModelRenderer top;
    ModelRenderer right;
    ModelRenderer left;
    ModelRenderer tankwallright;
    ModelRenderer tankwallleft;
    ModelRenderer tankwallright2;
    ModelRenderer tankwallleft2;
    ModelRenderer tankwallleft3;
    ModelRenderer tankwallleft4;
    ModelRenderer tankwallright3;
    ModelRenderer tankwallright4;
    ModelRenderer tankbottomright;
    ModelRenderer tanktopright;
    ModelRenderer tanktopleft;
    ModelRenderer tankbottomleft;
    ModelRenderer bed;
    ModelRenderer bedstrapbottomright;
    ModelRenderer bedstrapsideleft;
    ModelRenderer bedstraptopleft;
    ModelRenderer bedstrapbottomleft;
    ModelRenderer bedstraptopright;
    ModelRenderer bedstrapsideright;
    ModelRenderer bedbuttonright;
    ModelRenderer bedbuttonleft;
    ModelRenderer bedstrapendleft;
    ModelRenderer bedstrapendright;
    RendererStack lowerTool;
    RendererStack upperTool;
    private FluidStack tankLeft;
    private FluidStack tankRight;
    private ItemStack stackUp;
    private ItemStack stackBottom;
    private float scale = -1;

    public ModelAdventureBackpackArmor() {

        textureWidth = 128;
        textureHeight = 64;

        lowerTool = new RendererStack(this, true);
        upperTool = new RendererStack(this, false);

        bipedBody.offsetY = -0.45F;
        bipedBody.offsetZ = 10F;

        main = new ModelRenderer(this, 0, 8);
        main.addBox(-5F, 0F, -3F, 10, 9, 5);
        main.setRotationPoint(0F, 11F, 0F);

        side = new ModelRenderer(this, 0, 22);
        side.addBox(0F, 0F, 0F, 8, 6, 2);
        side.setRotationPoint(-4F, 13F, 2F);

        top = new ModelRenderer(this, 0, 0);
        top.addBox(-5F, -3F, 0F, 10, 3, 5);
        top.setRotationPoint(0F, 11F, -3F);

        right = new ModelRenderer(this, 20, 22);
        right.addBox(0F, 0F, -1F, 1, 8, 1);
        right.setRotationPoint(-4F, 9F, -3F);

        left = new ModelRenderer(this, 20, 22);
        left.addBox(0F, 0F, -1F, 1, 8, 1);
        left.setRotationPoint(3F, 9F, -3F);

        tankwallright = new ModelRenderer(this, 0, 54);
        tankwallright.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallright.setRotationPoint(-6F, 11F, -2.5F);

        tankwallleft = new ModelRenderer(this, 0, 54);
        tankwallleft.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallleft.setRotationPoint(5F, 11F, -2.5F);

        tankwallright2 = new ModelRenderer(this, 0, 54);
        tankwallright2.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallright2.setRotationPoint(-9F, 11F, -2.5F);

        tankwallleft2 = new ModelRenderer(this, 0, 54);
        tankwallleft2.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallleft2.setRotationPoint(5F, 11F, 0.5F);

        tankwallleft3 = new ModelRenderer(this, 0, 54);
        tankwallleft3.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallleft3.setRotationPoint(8F, 11F, -2.5F);

        tankwallleft4 = new ModelRenderer(this, 0, 54);
        tankwallleft4.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallleft4.setRotationPoint(8F, 11F, 0.5F);

        tankwallright3 = new ModelRenderer(this, 0, 54);
        tankwallright3.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallright3.setRotationPoint(-6F, 11F, 0.5F);

        tankwallright4 = new ModelRenderer(this, 0, 54);
        tankwallright4.addBox(0F, 0F, 0F, 1, 8, 1);
        tankwallright4.setRotationPoint(-9F, 11F, 0.5F);

        tankbottomright = new ModelRenderer(this, 0, 59);
        tankbottomright.addBox(0F, 0F, 0F, 4, 1, 4);
        tankbottomright.setRotationPoint(-9F, 18F, -2.5F);

        tanktopright = new ModelRenderer(this, 0, 59);
        tanktopright.addBox(0F, 0F, 0F, 4, 1, 4);
        tanktopright.setRotationPoint(-9F, 10F, -2.5F);

        tanktopleft = new ModelRenderer(this, 0, 59);
        tanktopleft.addBox(0F, 0F, 0F, 4, 1, 4);
        tanktopleft.setRotationPoint(5F, 10F, -2.5F);

        tankbottomleft = new ModelRenderer(this, 0, 59);
        tankbottomleft.addBox(0F, 0F, 0F, 4, 1, 4);
        tankbottomleft.setRotationPoint(5F, 18F, -2.5F);

        bed = new ModelRenderer(this, 29, 0);
        bed.addBox(0F, 0F, 0F, 14, 2, 2);
        bed.setRotationPoint(-7F, 19.1F, 2F);

        bedstrapbottomright = new ModelRenderer(this, 0, 58);
        bedstrapbottomright.addBox(0F, 0F, 0F, 2, 1, 3);
        bedstrapbottomright.setRotationPoint(-5F, 20.7F, 1.3F);

        bedstrapsideleft = new ModelRenderer(this, 0, 58);
        bedstrapsideleft.addBox(0F, 0F, 0F, 2, 3, 1);
        bedstrapsideleft.setRotationPoint(3F, 18.7F, 3.4F);

        bedstraptopleft = new ModelRenderer(this, 0, 58);
        bedstraptopleft.addBox(0F, 0F, 0F, 2, 1, 2);
        bedstraptopleft.setRotationPoint(3F, 18.7F, 2F);

        bedstrapbottomleft = new ModelRenderer(this, 0, 58);
        bedstrapbottomleft.addBox(0F, 0F, 0F, 2, 1, 3);
        bedstrapbottomleft.setRotationPoint(3F, 20.7F, 1.3F);

        bedstraptopright = new ModelRenderer(this, 0, 58);
        bedstraptopright.addBox(0F, 0F, 0F, 2, 1, 2);
        bedstraptopright.setRotationPoint(-5F, 18.7F, 2F);

        bedstrapsideright = new ModelRenderer(this, 0, 58);
        bedstrapsideright.addBox(0F, 0F, 0F, 2, 3, 1);
        bedstrapsideright.setRotationPoint(-5F, 18.7F, 3.4F);

        bedbuttonright = new ModelRenderer(this, 0, 58);
        bedbuttonright.addBox(0F, 0F, 0F, 1, 1, 1);
        bedbuttonright.setRotationPoint(-4.5F, 18.3F, 1.7F);

        bedbuttonleft = new ModelRenderer(this, 0, 58);
        bedbuttonleft.addBox(0F, 0F, 0F, 1, 1, 1);
        bedbuttonleft.setRotationPoint(3.5F, 18.3F, 1.7F);

        bedstrapendleft = new ModelRenderer(this, 0, 58);
        bedstrapendleft.addBox(0F, 0F, 0F, 2, 1, 1);
        bedstrapendleft.setRotationPoint(3F, 20F, 1.3F);

        bedstrapendright = new ModelRenderer(this, 0, 58);
        bedstrapendright.addBox(0F, 0F, 0F, 2, 1, 1);
        bedstrapendright.setRotationPoint(-5F, 20F, 1.3F);

        ModelRenderer[] meh = {main, side, top, right, left, tankwallright, tankwallleft, tankwallright2, tankwallleft2, tankwallleft3, tankwallleft4,
                tankwallright3, tankwallright4, tankbottomright, tanktopright, tanktopleft, tankbottomleft, bed, bedstrapbottomright, bedstrapsideleft,
                bedstraptopleft, bedstrapbottomleft, bedstraptopright, bedstrapsideright, bedbuttonright, bedbuttonleft, bedstrapendleft, bedstrapendright};

        try {
            for (ModelRenderer part : meh) {
                part.setTextureSize(128, 64);
                part.mirror = false;
                bipedBody.addChild(part);
            }

            bipedBody.addChild(lowerTool);
            bipedBody.addChild(upperTool);

        } catch (Exception oops) {
            oops.printStackTrace();
        }

    }

    public ModelAdventureBackpackArmor setTanks(FluidStack tankLeft, FluidStack tankRight) {
        this.tankLeft = tankLeft;
        this.tankRight = tankRight;
       /* this.tankLeft = new FluidStack(FluidRegistry.LAVA,1000);
        this.tankLeft = new FluidStack(FluidRegistry.WATER,1000);*/

        return instance;
    }

    public ModelAdventureBackpackArmor setItems(ItemStack stackUp, ItemStack stackBottom) {
        this.stackUp = stackUp;
        this.stackBottom = stackBottom;
        return instance;
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {

        upperTool.offsetY = 0.6F;
        upperTool.offsetZ = .2F;
        lowerTool.setRotationPoint(-.5F, .10F, .3F);
        lowerTool.offsetX = -.28F;
        lowerTool.offsetY = 1.25F;
        lowerTool.offsetZ = -.1F;

        setRotationAngles(f, f1, f2, f3, f4, f5, entity);

        GL11.glPushMatrix();
        GL11.glColor4f(1, 1, 1, 1);
        if (scale > -1) {
            bipedBody.render(scale);
        } else {
            bipedBody.render(1 / 20.0F);
        }
        lowerTool.stack = stackBottom;
        upperTool.stack = stackUp;
        GL11.glPopMatrix();

        CCRenderState.reset();
        CCRenderState.pullLightmap();
        CCRenderState.useNormals = true;

        if (tankLeft != null && tankLeft.getFluid() != null && tankLeft.getFluid().getIcon() != null) {

            Vector3 victor = new Vector3((bipedBody.offsetX + tankbottomleft.rotationPointX * 0.1 - 0.21),
                    (bipedBody.offsetY + tankwallleft.rotationPointY * 0.1), (bipedBody.offsetZ + tankbottomleft.rotationPointZ * 0.1 + 0.125));

            Cuboid6 left = new Cuboid6(0, -0.205, 0.18, 0.15, -0.555, 0.02).add(victor);

            if (isSneak) {
                Vector3 victoria = new Vector3(0, 0, .2);
                left.add(victoria);
                left.min.y += .1;
                left.max.y += .03;
                GL11.glPushMatrix();
                GL11.glRotatef(30F, 1, 0, 0);
            }
            RenderUtils.renderFluidCuboid(tankLeft, left, ((1.0F * tankLeft.amount) / (1.0F * Constants.basicTankCapacity)), 0.2);
            if (isSneak) {
                GL11.glPopMatrix();
            }
        }

        if (tankRight != null && tankRight.getFluid() != null && tankRight.getFluid().getIcon() != null) {

            Vector3 victor = new Vector3((bipedBody.offsetX + tankbottomright.rotationPointX * 0.1 + 0.5),
                    (bipedBody.offsetY + tankwallleft.rotationPointY * 0.1), (bipedBody.offsetZ + tankbottomright.rotationPointZ * 0.1 + 0.125));
            Cuboid6 right = new Cuboid6(-0.04, -0.205, 0.18, 0.15, -0.555, 0.02).add(victor);
            if (isSneak) {
                Vector3 victoria = new Vector3(0, 0, .2);
                right.add(victoria);
                right.min.y += .1;
                right.max.y += .03;
                GL11.glPushMatrix();
                GL11.glRotatef(30F, 1, 0, 0);
            }
            RenderUtils.renderFluidCuboid(tankRight, right, ((1.0F * tankRight.amount) / (1.0F * Constants.basicTankCapacity)), 1);
            if (isSneak) {
                GL11.glPopMatrix();
            }
        }
    }

    public void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    @Override
    public void setRotationAngles(float v1, float v2, float v3, float v4, float v5, float v6, Entity entity) {
        // Copyboy: For some reason this is not properly updated.
        isSneak = ((entity != null) ? ((EntityLivingBase) entity).isSneaking() : false);
        bipedBody.offsetZ = (isSneak) ? 0 : .3F;
        super.setRotationAngles(v1, v2, v3, v4, v5, v6, entity);
    }

    @Override
    public void setLivingAnimations(EntityLivingBase entity, float par2, float par3, float partialTicks) {
        float angle = 0;
        top.rotateAngleX = (float) (angle * Math.PI / 4.0);
    }

    public ModelAdventureBackpackArmor setScale(float scale) {
        this.scale = scale;
        return this;
    }

}