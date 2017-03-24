package com.darkona.adventurebackpack.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.darkona.adventurebackpack.client.models.ModelCampFire;
import com.darkona.adventurebackpack.util.Resources;

/**
 * Created on 05/01/2015
 *
 * @author Darkona
 */
public class RendererCampFire extends TileEntitySpecialRenderer
{

    //    private ModelAdventureBackpackBlock model;
    private ModelCampFire model;

    public RendererCampFire()
    {
        this.model = new ModelCampFire();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float par8)
    {
        ResourceLocation modelTexture = Resources.modelTextures("campFire");
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.2f, (float) z + 0.5F);
        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        bindTexture(modelTexture);
        model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1 / 20F);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }


}
