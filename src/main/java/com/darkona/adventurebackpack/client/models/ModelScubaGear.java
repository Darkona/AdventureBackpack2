package com.darkona.adventurebackpack.client.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * Placeholder model of the Scuba Gear (the art pass replaces the boxes and UVs): two air tanks with valves on a back
 * plate, hung from an empty anchor that copies the body's rotation, and the diving mask hung from an anchor that
 * copies the head's rotation, so the mask follows the look (RendererWearableEquipped passes the head angles).
 * Texture: models/scubaGear.png, 64x64 (tanks in the top half, mask in the bottom half).
 */
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

        ModelRenderer plate = new ModelRenderer(this, 0, 13);
        plate.addBox(-4.0F, 1.0F, 2.0F, 8, 6, 1);
        ModelRenderer tankLeft = new ModelRenderer(this, 0, 0);
        tankLeft.addBox(-3.5F, 1.0F, 3.0F, 3, 10, 3);
        ModelRenderer tankRight = new ModelRenderer(this, 12, 0);
        tankRight.addBox(0.5F, 1.0F, 3.0F, 3, 10, 3);
        ModelRenderer valveLeft = new ModelRenderer(this, 24, 0);
        valveLeft.addBox(-2.5F, 0.0F, 4.0F, 1, 1, 1);
        ModelRenderer valveRight = new ModelRenderer(this, 24, 2);
        valveRight.addBox(1.5F, 0.0F, 4.0F, 1, 1, 1);
        back.addChild(plate);
        back.addChild(tankLeft);
        back.addChild(tankRight);
        back.addChild(valveLeft);
        back.addChild(valveRight);

        ModelRenderer visor = new ModelRenderer(this, 0, 32);
        visor.addBox(-4.0F, -6.0F, -5.0F, 8, 4, 1, 0.1F);
        ModelRenderer strap = new ModelRenderer(this, 0, 38);
        strap.addBox(-4.0F, -5.5F, -4.0F, 8, 2, 8, 0.15F);
        ModelRenderer regulator = new ModelRenderer(this, 20, 32);
        regulator.addBox(-1.0F, -2.5F, -5.5F, 2, 2, 1);
        face.addChild(visor);
        face.addChild(strap);
        face.addChild(regulator);
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
