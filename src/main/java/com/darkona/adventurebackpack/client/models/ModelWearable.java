package com.darkona.adventurebackpack.client.models;

import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import com.darkona.adventurebackpack.common.Constants;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

/**
 * Created on 10/01/2015
 *
 * @author Darkona
 */
public abstract class ModelWearable extends ModelBiped
{

    protected void startBlending()
    {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    protected void stopBlending()
    {
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }

    public void setOffset(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.offsetX = x;
        modelRenderer.offsetY = y;
        modelRenderer.offsetZ = z;
    }

    public void setRotationPoints(ModelRenderer modelRenderer, float x, float y, float z)
    {
        modelRenderer.rotationPointX = x;
        modelRenderer.rotationPointY = y;
        modelRenderer.rotationPointZ = z;
    }

    public void renderFluidInTank(FluidTank tank, Vector3 minCoords, Vector3 maxCoords, Vector3 offset, ModelRenderer parent)
    {
        if (tank != null && tank.getFluid() != null && tank.getFluid().getFluid().getIcon() != null)
        {
            Vector3 victor = new Vector3(
                    (parent.rotationPointX * 0.1f + offset.x), //
                    (parent.rotationPointY * 0.1f + offset.y),
                    (parent.rotationPointZ * 0.1f + offset.z));

            Cuboid6 cat = new Cuboid6(minCoords.x,minCoords.y,minCoords.z, maxCoords.x, maxCoords.y, maxCoords.z);
            //Thanks Chickenbones!
            RenderUtils.renderFluidCuboid(tank.getFluid(), cat.add(victor), ((1.0F * tank.getFluidAmount()) / (1.0F * Constants.basicTankCapacity)), 0.8);
        }
    }

}
