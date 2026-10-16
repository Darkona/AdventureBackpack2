package com.darkona.adventurebackpack.client.render;

import adventurebackpack.api.IBackWearableItem;
import com.darkona.adventurebackpack.block.BlockGear;
import com.darkona.adventurebackpack.block.TileGear;
import com.darkona.adventurebackpack.inventory.GearType;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RendererGearBlock extends TileEntitySpecialRenderer
{
    private static final float SCALE = 1 / 16F;
    private final ResourceLocation[] textures = new ResourceLocation[GearType.values().length];

    @Override
    public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks)
    {
        if (!(te instanceof TileGear)) return;
        ItemStack gear = ((TileGear) te).getGear();
        GearType type = GearType.of(gear);
        if (type == null) return;
        ResourceLocation texture = textures[type.ordinal()];
        if (texture == null)
        {
            texture = ((IBackWearableItem) gear.getItem()).getWearableTexture(gear);
            textures[type.ordinal()] = texture;
        }
        int facing = BlockGear.facing(te.getBlockMetadata());

        GL11.glPushMatrix();
        try
        {
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glTranslatef((float) x + 0.5F, (float) y + 0.75F, (float) z + 0.5F);
            GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(facing * 90F, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.0F, -0.3F);
            bindTexture(texture);
            switch (type)
            {
                case JETPACK:
                    ClientProxy.modelSteamJetpack.renderPlaced(gear, SCALE);
                    break;
                case COPTER:
                    ClientProxy.modelCopterPack.renderPlaced(gear, SCALE);
                    break;
                case SCUBA:
                    ClientProxy.modelScubaGear.renderPlaced(SCALE);
                    break;
                default:
                    ClientProxy.modelFlamethrowerPack.renderPlaced(SCALE);
                    break;
            }
        } finally
        {
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }
}
