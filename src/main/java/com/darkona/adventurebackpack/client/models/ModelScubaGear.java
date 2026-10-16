package com.darkona.adventurebackpack.client.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class ModelScubaGear extends ModelWearable
{
    private final ModelRenderer back;
    private final ModelRenderer face;

    public ModelScubaGear()
    {
        textureWidth = 64;
        textureHeight = 64;
        back = new ModelRenderer(this, 0, 0);
        face = new ModelRenderer(this, 0, 0);
        ModelRenderer backPlate = new ModelRenderer(this, 12, 10);
        backPlate.addBox(-4.0F, 0.5F, 2.0F, 8, 8, 1, 0.0F);
        ModelRenderer strapTopL = new ModelRenderer(this, 36, 10);
        strapTopL.addBox(-3.5F, -0.5F, -2.5F, 2, 1, 6, 0.0F);
        ModelRenderer strapTopR = new ModelRenderer(this, 36, 10);
        strapTopR.addBox(1.5F, -0.5F, -2.5F, 2, 1, 6, 0.0F);
        ModelRenderer strapFrontL = new ModelRenderer(this, 30, 10);
        strapFrontL.addBox(-3.5F, 0.5F, -2.5F, 2, 7, 1, 0.0F);
        ModelRenderer strapFrontR = new ModelRenderer(this, 30, 10);
        strapFrontR.addBox(1.5F, 0.5F, -2.5F, 2, 7, 1, 0.0F);
        ModelRenderer buckle = new ModelRenderer(this, 56, 13);
        buckle.addBox(-1.5F, 3.5F, -2.5F, 3, 1, 1, 0.0F);
        ModelRenderer tankL = new ModelRenderer(this, 0, 0);
        tankL.addBox(-3.5F, 0.5F, 3.0F, 3, 10, 3, 0.0F);
        ModelRenderer tankR = new ModelRenderer(this, 0, 0);
        tankR.addBox(0.5F, 0.5F, 3.0F, 3, 10, 3, 0.0F);
        ModelRenderer capTopL = new ModelRenderer(this, 56, 10);
        capTopL.addBox(-3.0F, -0.5F, 3.5F, 2, 1, 2, 0.0F);
        ModelRenderer capTopR = new ModelRenderer(this, 56, 10);
        capTopR.addBox(1.0F, -0.5F, 3.5F, 2, 1, 2, 0.0F);
        ModelRenderer capBotL = new ModelRenderer(this, 56, 10);
        capBotL.addBox(-3.0F, 10.5F, 3.5F, 2, 1, 2, 0.0F);
        ModelRenderer capBotR = new ModelRenderer(this, 56, 10);
        capBotR.addBox(1.0F, 10.5F, 3.5F, 2, 1, 2, 0.0F);
        ModelRenderer bandTop = new ModelRenderer(this, 36, 17);
        bandTop.addBox(-4.0F, 2.5F, 2.5F, 8, 1, 4, 0.0F);
        ModelRenderer bandBot = new ModelRenderer(this, 36, 17);
        bandBot.addBox(-4.0F, 7.5F, 2.5F, 8, 1, 4, 0.0F);
        ModelRenderer manifold = new ModelRenderer(this, 52, 15);
        manifold.addBox(-2.5F, -1.5F, 4.0F, 5, 1, 1, 0.0F);
        ModelRenderer valveL = new ModelRenderer(this, 8, 13);
        valveL.addBox(-2.5F, -2.5F, 4.0F, 1, 1, 1, 0.0F);
        ModelRenderer valveR = new ModelRenderer(this, 8, 13);
        valveR.addBox(1.5F, -2.5F, 4.0F, 1, 1, 1, 0.0F);
        ModelRenderer hoseUp = new ModelRenderer(this, 52, 10);
        hoseUp.addBox(1.5F, -5.5F, 4.5F, 1, 4, 1, 0.0F);
        ModelRenderer visor = new ModelRenderer(this, 0, 19);
        visor.addBox(-4.0F, -6.0F, -5.0F, 8, 4, 1, 0.2F);
        ModelRenderer strap = new ModelRenderer(this, 12, 0);
        strap.addBox(-4.0F, -5.5F, -4.0F, 8, 2, 8, 0.6F);
        ModelRenderer regulator = new ModelRenderer(this, 0, 13);
        regulator.addBox(-1.5F, -2.5F, -5.5F, 3, 2, 1, 0.0F);
        ModelRenderer hoseSide = new ModelRenderer(this, 44, 0);
        hoseSide.addBox(4.0F, -3.5F, -5.0F, 1, 1, 9, 0.0F);
        ModelRenderer hoseFront = new ModelRenderer(this, 0, 16);
        hoseFront.addBox(1.5F, -3.5F, -5.5F, 3, 1, 1, 0.0F);
        ModelRenderer hoseBack = new ModelRenderer(this, 0, 16);
        hoseBack.addBox(2.0F, -3.5F, 4.0F, 3, 1, 1, 0.0F);
        back.addChild(backPlate);
        back.addChild(strapTopL);
        back.addChild(strapTopR);
        back.addChild(strapFrontL);
        back.addChild(strapFrontR);
        back.addChild(buckle);
        back.addChild(tankL);
        back.addChild(tankR);
        back.addChild(capTopL);
        back.addChild(capTopR);
        back.addChild(capBotL);
        back.addChild(capBotR);
        back.addChild(bandTop);
        back.addChild(bandBot);
        back.addChild(manifold);
        back.addChild(valveL);
        back.addChild(valveR);
        back.addChild(hoseUp);
        face.addChild(visor);
        face.addChild(strap);
        face.addChild(regulator);
        face.addChild(hoseSide);
        face.addChild(hoseFront);
        face.addChild(hoseBack);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale, ItemStack stack)
    {
        render(entity, f, f1, f2, f3, f4, scale);
    }

    @Override
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale)
    {
        boolean sneaking = entity != null && entity.isSneaking();
        copy(bipedBody, back);
        copy(bipedHead, face);
        face.rotationPointY = sneaking ? 1.0F : 0.0F;
        back.render(scale);
        face.render(scale);
    }

    public void renderPlaced(float scale)
    {
        back.rotateAngleX = back.rotateAngleY = back.rotateAngleZ = 0;
        back.rotationPointX = back.rotationPointY = back.rotationPointZ = 0;
        back.render(scale);
    }

    private static void copy(ModelRenderer from, ModelRenderer to)
    {
        to.rotateAngleX = from.rotateAngleX;
        to.rotateAngleY = from.rotateAngleY;
        to.rotateAngleZ = from.rotateAngleZ;
        to.rotationPointX = from.rotationPointX;
        to.rotationPointY = from.rotationPointY;
        to.rotationPointZ = from.rotationPointZ;
    }
}
