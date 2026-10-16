package com.darkona.adventurebackpack.client.models;

import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.entity.Entity;

@SideOnly(Side.CLIENT)
public class ModelTamedSpider extends ModelSpider
{
    private static final float STANDING_Y = 15.0F;
    private static final float SITTING_Y = 19.0F;
    private static final float SITTING_LEG_DROP = 0.2F;

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float age, float headYaw, float headPitch,
                                  float scale, Entity entity)
    {
        super.setRotationAngles(limbSwing, limbSwingAmount, age, headYaw, headPitch, scale, entity);
        boolean sitting = entity instanceof EntityFriendlySpider && ((EntityFriendlySpider) entity).isSitting();
        float y = sitting ? SITTING_Y : STANDING_Y;
        spiderHead.rotationPointY = y;
        spiderNeck.rotationPointY = y;
        spiderBody.rotationPointY = y;
        setLeg(spiderLeg1, y, sitting, -1);
        setLeg(spiderLeg2, y, sitting, 1);
        setLeg(spiderLeg3, y, sitting, -1);
        setLeg(spiderLeg4, y, sitting, 1);
        setLeg(spiderLeg5, y, sitting, -1);
        setLeg(spiderLeg6, y, sitting, 1);
        setLeg(spiderLeg7, y, sitting, -1);
        setLeg(spiderLeg8, y, sitting, 1);
        if (sitting)
        {
            spiderHead.rotateAngleX = 0.0F;
        }
    }

    private static void setLeg(ModelRenderer leg, float y, boolean sitting, int side)
    {
        leg.rotationPointY = y;
        if (sitting) leg.rotateAngleZ = side * SITTING_LEG_DROP;
    }
}
