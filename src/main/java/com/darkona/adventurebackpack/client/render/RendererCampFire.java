package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.block.TileCampfire;
import com.darkona.adventurebackpack.client.models.ModelCampFire;
import com.darkona.adventurebackpack.util.Resources;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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

        EntityItem food = te instanceof TileCampfire ? ((TileCampfire) te).getDisplayItem() : null;
        if (food != null) renderFood(food, x, y, z, te.getWorldObj().getTotalWorldTime(), par8);
    }

    /**
     * The item cooking, hung under the top of the sticks and turning slowly like on a spit. The entity is cached by the
     * tile; drawn the way an item frame draws its item (no bobbing, no spin of its own).
     */
    private static void renderFood(EntityItem food, double x, double y, double z, long time, float partialTicks)
    {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.2F, (float) z + 0.5F);
        GL11.glRotatef(((time % 360L) + partialTicks) * 2.0F, 0.0F, 1.0F, 0.0F);
        GL11.glScalef(0.8F, 0.8F, 0.8F);
        RenderItem.renderInFrame = true;
        RenderManager.instance.renderEntityWithPosYaw(food, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
        RenderItem.renderInFrame = false;
        GL11.glPopMatrix();
    }


}
