package com.darkona.adventurebackpack.client.models;

import codechicken.lib.render.RenderUtils;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import org.lwjgl.opengl.GL11;

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

    public abstract void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5, ItemStack stack);

    private static final Vector3 VICTOR = new Vector3();
    private static final Cuboid6 CAT = new Cuboid6(0, 0, 0, 0, 0, 0);

    public void renderFluidInTank(FluidTank tank, Vector3 minCoords, Vector3 maxCoords, Vector3 offset, ModelRenderer parent)
    {
        if (tank != null) renderFluidInTank(tank.getFluid(), tank.getCapacity(), minCoords, maxCoords, offset, parent);
    }

    public void renderFluidInTank(FluidStack fluid, int capacity, Vector3 minCoords, Vector3 maxCoords, Vector3 offset, ModelRenderer parent)
    {
        if (fluid != null && fluid.getFluid() != null && fluid.getFluid().getIcon() != null)
        {
            VICTOR.set(
                    (parent.rotationPointX * 0.1f + parent.offsetX * 0.1 + offset.x) ,
                    (parent.rotationPointY * 0.1f + parent.offsetY * 0.1 + offset.y),
                    (parent.rotationPointZ * 0.1f + parent.offsetZ * 0.1 + offset.z));

            CAT.set(minCoords.x,minCoords.y,minCoords.z, maxCoords.x, maxCoords.y, maxCoords.z);
            GL11.glPushMatrix();
            try
            {
                RenderUtils.renderFluidCuboid(fluid, CAT.add(VICTOR), ((1.0F * fluid.amount) / (1.0F * capacity)), 0.8);
            } catch (Exception e)
            {
            } finally
            {
                GL11.glPopMatrix();
            }
        }
    }
}
