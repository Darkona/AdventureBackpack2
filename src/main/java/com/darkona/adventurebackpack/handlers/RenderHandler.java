package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Created on 25/12/2014
 *
 * @author Darkona
 */
public class RenderHandler
{
    @SubscribeEvent
    public void playerSpecialsRendering(RenderPlayerEvent.Specials.Pre event)
    {
        if (Wearing.isWearingWearable(event.entityPlayer))
        {
            float rotationY = event.renderer.modelBipedMain.bipedBody.rotateAngleY;
            float rotationX = event.renderer.modelBipedMain.bipedBody.rotateAngleX;
            float rotationZ = event.renderer.modelBipedMain.bipedBody.rotateAngleZ;

            double x = event.entity.posX;
            double y = event.entity.posY;
            double z = event.entity.posZ;

            float pitch = event.entity.rotationPitch;
            float yaw = event.entity.rotationYaw;
            ClientProxy.rendererWearableEquipped.render(event.entity, x, y, z, rotationX, rotationY, rotationZ, pitch, yaw);

            event.renderCape = false;
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void playerRendering(RenderPlayerEvent.Pre event)
    {

    }

    public void playerRendering(RenderPlayerEvent.SetArmorModel event)
    {

    }


}
