package com.darkona.adventurebackpack.client.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import com.darkona.adventurebackpack.block.TileAdventureBackpack;
import com.darkona.adventurebackpack.client.models.ModelBackpackBlock;
import com.darkona.adventurebackpack.util.Resources;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */

public class RendererAdventureBackpackBlock extends TileEntitySpecialRenderer
{
    private ModelBackpackBlock model;

    public RendererAdventureBackpackBlock()
    {
        this.model = new ModelBackpackBlock();
    }

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float par8)
    {
        TileAdventureBackpack tileBackpack = (TileAdventureBackpack) te;
        int dir = te.getBlockMetadata();
        if ((dir & 8) >= 8)
        {
            dir -= 8;
        }
        if ((dir & 4) >= 4)
        {
            dir -= 4;
        }

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 0.5f, (float) z + 0.5F);

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        GL11.glPushMatrix();
        if (dir == 0)
        {
            GL11.glRotatef(-180F, 0.0F, 1.0F, 0.0F);
        }
        if (dir % 2 != 0)
        {
            GL11.glRotatef(dir * (-90F), 0.0F, 1.0F, 0.0F);
        }
        if (dir % 2 == 0)
        {
            GL11.glRotatef(dir * (-180F), 0.0F, 1.0F, 0.0F);
        }
        ResourceLocation modelTexture = Resources.getBackpackTexture(tileBackpack.getType());

        bindTexture(modelTexture);
        model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1 / 20F, tileBackpack);

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
