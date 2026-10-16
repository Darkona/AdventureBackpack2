package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

public class RenderHandler
{
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void playerSpecialsRendering(RenderPlayerEvent.Specials.Pre event)
    {
        if (!ConfigHandler.RENDER_BACKPACK_ON_PLAYER) return;
        EntityPlayer player = event.entityPlayer;
        if (!Wearing.isWearingWearable(player)) return;

        float rotationY = event.renderer.modelBipedMain.bipedBody.rotateAngleY;
        float rotationX = event.renderer.modelBipedMain.bipedBody.rotateAngleX;
        float rotationZ = event.renderer.modelBipedMain.bipedBody.rotateAngleZ;

        double x = player.posX;
        double y = player.posY;
        double z = player.posZ;

        float pitch = player.rotationPitch;
        float yaw = player.rotationYaw;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        try
        {
            ClientProxy.rendererWearableEquipped.render(player, x, y, z, rotationX, rotationY, rotationZ, pitch, yaw,
                    event.renderer.modelBipedMain.bipedHead.rotateAngleX, event.renderer.modelBipedMain.bipedHead.rotateAngleY);
        } finally
        {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }

        event.renderCape = false;
    }
}
