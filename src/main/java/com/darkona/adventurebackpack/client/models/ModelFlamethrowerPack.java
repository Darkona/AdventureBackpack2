package com.darkona.adventurebackpack.client.models;

import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

/**
 * Placeholder model of the Flamethrower Pack (the art pass replaces the boxes and UVs): two fuel tanks on a back
 * plate, hung from an anchor that copies the body's rotation, and the pyro mask (a hood over the whole head with a
 * filter) hung from an anchor that copies the head's rotation, so it follows the look. Texture:
 * models/flamethrowerPack.png, 64x64 (flammableTanks in the top half, pyroMask in the bottom half).
 */
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

        ModelRenderer tankLeft = new ModelRenderer(this, 0, 0);
        tankLeft.addBox(-4.5F, 0.5F, 2.0F, 4, 10, 4);
        ModelRenderer tankRight = new ModelRenderer(this, 0, 15);
        tankRight.addBox(0.5F, 0.5F, 2.0F, 4, 10, 4);
        ModelRenderer manifold = new ModelRenderer(this, 17, 15);
        manifold.addBox(-3.5F, 10.5F, 3.5F, 7, 1, 1);
        back.addChild(tankLeft);
        back.addChild(tankRight);
        back.addChild(manifold);

        ModelRenderer hood = new ModelRenderer(this, 0, 32);
        hood.addBox(-4.5F, -8.5F, -4.5F, 9, 9, 9);
        ModelRenderer filter = new ModelRenderer(this, 0, 57);
        filter.addBox(-1.5F, -3.5F, -6.5F, 3, 3, 2);
        face.addChild(hood);
        face.addChild(filter);
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
