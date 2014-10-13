package com.darkona.adventurebackpack.client.render;

import com.darkona.adventurebackpack.blocks.TileAdventureBackpack;
import com.darkona.adventurebackpack.models.ModelAdventureBackpackBlock;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Textures;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

/**
 * Created on 12/10/2014
 *
 * @author Darkona
 */

public class RendererAdventureBackpackBlock extends TileEntitySpecialRenderer {

    private final ModelAdventureBackpackBlock model;

    public RendererAdventureBackpackBlock() {
        this.model = new ModelAdventureBackpackBlock();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float par8) {
        int dir = tileEntity.getBlockMetadata(/*advbackpack.xCoord, advbackpack.yCoord, advbackpack.zCoord*/);
        if ((dir & 8) >= 8)
            dir -= 8;
        if ((dir & 4) >= 4)
            dir -= 4;

        TileAdventureBackpack bp = (TileAdventureBackpack) tileEntity;

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glTranslatef((float) x + 0.5F, (float) y + 1.1F, (float) z + 0.5F);


        // func_110628_a(Utils.getBackpackColor(bp));

        GL11.glPushMatrix();
        GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);

        GL11.glPushMatrix();
        if (dir == 0)
            GL11.glRotatef(-180F, 0.0F, 1.0F, 0.0F);
        if (dir % 2 != 0)
            GL11.glRotatef(dir * (-90F), 0.0F, 1.0F, 0.0F);
        if (dir % 2 == 0)
            GL11.glRotatef(dir * (-180F), 0.0F, 1.0F, 0.0F);
        bindTexture(Textures.backpackTexRL(bp));
        model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 1 / 20F, bp.getLeftTank(), bp.getRightTank(), bp.isSBDeployed());

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
    }
}
