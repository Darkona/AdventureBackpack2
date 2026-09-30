package com.darkona.adventurebackpack.client.models;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * The Flamethrower Pack: two blackened steel fuel tanks with red enamel caps, brass valves, a pressure gauge
 * and a bottom manifold with the hose that feeds the hand nozzle, strapped by a leather harness; the pyro hood
 * (dark leather, two amber lenses, a brass filter canister) is drawn on the head.
 * <p>
 * Geometry, UV layout and texture (models/flamethrowerPack.png, 64x64) are generated together by
 * {@code notes/art-1.1/wearables.py}; the constructor body is its output. The parts hang from two empty anchors:
 * {@code back} copies the body's rotation and {@code face} the head's (RendererWearableEquipped passes the head
 * angles), so the mask follows the look. Nothing is allocated or read per frame (performance contract, tier 4).
 */
@SideOnly(Side.CLIENT)
public class ModelFlamethrowerPack extends ModelWearable
{
    private final ModelRenderer back;
    private final ModelRenderer face;

    public ModelFlamethrowerPack()
    {
        textureWidth = 64;
        textureHeight = 64;
        back = new ModelRenderer(this, 0, 0);
        face = new ModelRenderer(this, 0, 0);
        ModelRenderer backPlate = new ModelRenderer(this, 32, 13);
        backPlate.addBox(-4.5F, 0.5F, 2.0F, 9, 10, 1, 0.0F);
        ModelRenderer strapTopL = new ModelRenderer(this, 0, 16);
        strapTopL.addBox(-3.5F, -0.5F, -2.5F, 2, 1, 6, 0.0F);
        ModelRenderer strapTopR = new ModelRenderer(this, 0, 16);
        strapTopR.addBox(1.5F, -0.5F, -2.5F, 2, 1, 6, 0.0F);
        ModelRenderer strapFrontL = new ModelRenderer(this, 48, 0);
        strapFrontL.addBox(-3.5F, 0.5F, -2.5F, 2, 7, 1, 0.0F);
        ModelRenderer strapFrontR = new ModelRenderer(this, 48, 0);
        strapFrontR.addBox(1.5F, 0.5F, -2.5F, 2, 7, 1, 0.0F);
        ModelRenderer buckle = new ModelRenderer(this, 52, 17);
        buckle.addBox(-1.5F, 3.5F, -2.5F, 3, 1, 1, 0.0F);
        ModelRenderer tankL = new ModelRenderer(this, 32, 0);
        tankL.addBox(-4.5F, 0.5F, 3.0F, 4, 9, 4, 0.0F);
        ModelRenderer tankR = new ModelRenderer(this, 32, 0);
        tankR.addBox(0.5F, 0.5F, 3.0F, 4, 9, 4, 0.0F);
        ModelRenderer capL = new ModelRenderer(this, 52, 10);
        capL.addBox(-4.0F, -0.5F, 3.5F, 3, 1, 3, 0.0F);
        ModelRenderer capR = new ModelRenderer(this, 52, 10);
        capR.addBox(1.0F, -0.5F, 3.5F, 3, 1, 3, 0.0F);
        ModelRenderer valveL = new ModelRenderer(this, 48, 11);
        valveL.addBox(-3.0F, -1.5F, 4.5F, 1, 1, 1, 0.0F);
        ModelRenderer valveR = new ModelRenderer(this, 48, 11);
        valveR.addBox(2.0F, -1.5F, 4.5F, 1, 1, 1, 0.0F);
        ModelRenderer bandTop = new ModelRenderer(this, 0, 23);
        bandTop.addBox(-5.0F, 2.0F, 2.5F, 10, 1, 5, 0.0F);
        ModelRenderer bandBot = new ModelRenderer(this, 0, 23);
        bandBot.addBox(-5.0F, 7.0F, 2.5F, 10, 1, 5, 0.0F);
        ModelRenderer gauge = new ModelRenderer(this, 52, 14);
        gauge.addBox(-1.0F, 1.0F, 7.0F, 2, 2, 1, 0.0F);
        ModelRenderer manifold = new ModelRenderer(this, 16, 16);
        manifold.addBox(-3.0F, 9.5F, 4.5F, 6, 1, 1, 0.0F);
        ModelRenderer hoseDown = new ModelRenderer(this, 48, 8);
        hoseDown.addBox(-3.0F, 10.5F, 4.5F, 1, 2, 1, 0.0F);
        ModelRenderer hoseOut = new ModelRenderer(this, 16, 18);
        hoseOut.addBox(-5.5F, 11.5F, 4.5F, 3, 1, 1, 0.0F);
        ModelRenderer hood = new ModelRenderer(this, 0, 0);
        hood.addBox(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.6F);
        ModelRenderer lensL = new ModelRenderer(this, 58, 14);
        lensL.addBox(-3.0F, -6.0F, -5.0F, 2, 2, 1, 0.0F);
        ModelRenderer lensR = new ModelRenderer(this, 58, 14);
        lensR.addBox(1.0F, -6.0F, -5.0F, 2, 2, 1, 0.0F);
        ModelRenderer filter = new ModelRenderer(this, 54, 0);
        filter.addBox(-1.5F, -3.5F, -6.5F, 3, 3, 2, 0.0F);
        ModelRenderer filterRim = new ModelRenderer(this, 54, 5);
        filterRim.addBox(-2.0F, -4.0F, -5.0F, 4, 4, 1, 0.0F);
        back.addChild(backPlate);
        back.addChild(strapTopL);
        back.addChild(strapTopR);
        back.addChild(strapFrontL);
        back.addChild(strapFrontR);
        back.addChild(buckle);
        back.addChild(tankL);
        back.addChild(tankR);
        back.addChild(capL);
        back.addChild(capR);
        back.addChild(valveL);
        back.addChild(valveR);
        back.addChild(bandTop);
        back.addChild(bandBot);
        back.addChild(gauge);
        back.addChild(manifold);
        back.addChild(hoseDown);
        back.addChild(hoseOut);
        face.addChild(hood);
        face.addChild(lensL);
        face.addChild(lensR);
        face.addChild(filter);
        face.addChild(filterRim);
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
