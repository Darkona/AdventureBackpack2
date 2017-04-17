package com.darkona.adventurebackpack.client.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import com.darkona.adventurebackpack.client.models.ModelInflatableBoat;
import com.darkona.adventurebackpack.entity.EntityInflatableBoat;
import com.darkona.adventurebackpack.util.Resources;

/**
 * Created on 05/01/2015
 *
 * @author Darkona
 */
public class RendererInflatableBoat extends Render
{

    private ResourceLocation boatTexture = Resources.modelTextures("inflatableBoat");
    private ModelInflatableBoat boatModel = new ModelInflatableBoat();

    public RendererInflatableBoat()
    {
        this.shadowSize = 0.5F;
    }

    public void doRender(EntityInflatableBoat entity, double x, double y, double z, float var1, float var2)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        GL11.glRotatef(180.0F - var1, 0.0F, 1.0F, 0.0F);
        float f2 = (float) entity.getTimeSinceHit() - var2;
        float f3 = entity.getDamageTaken() - var2;

        if (f3 < 0.0F)
        {
            f3 = 0.0F;
        }

        if (f2 > 0.0F)
        {
            GL11.glRotatef(MathHelper.sin(f2) * f2 * f3 / 10.0F * (float) entity.getForwardDirection(), 1.0F, 0.0F, 0.0F);
        }

        float f4 = 0.75F;
        GL11.glScalef(f4, f4, f4);
        GL11.glScalef(1.0F / f4, 1.0F / f4, 1.0F / f4);
        this.bindEntityTexture(entity);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        this.boatModel.render(entity, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
    }


    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     *
     * @param p_110775_1_
     */
    @Override
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return boatTexture;
    }


    @Override
    public void doRender(Entity entity, double posX, double posY, double posZ, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityInflatableBoat) entity, posX, posY, posZ, p_76986_8_, p_76986_9_);
    }
}
