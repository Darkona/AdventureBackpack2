package com.darkona.adventurebackpack.handlers;

import baubles.api.BaublesApi;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Created on 25/12/2014
 *
 * @author Darkona
 */
public class RenderHandler
{


    @SubscribeEvent
    //@SideOnly(Side.CLIENT)
    public void playerRendering(RenderPlayerEvent.Specials.Pre event)
    {
        if(ConfigHandler.IS_BAUBLES)
        {
            EntityPlayer player = event.entityPlayer;
            IInventory baubles = BaublesApi.getBaubles(player);
            ItemStack backpack = baubles.getStackInSlot(0);

            if(backpack != null && backpack.getItem() instanceof ItemAdventureBackpack)
            {
                float rotationY = event.renderer.modelBipedMain.bipedBody.rotateAngleY;
                float rotationX = event.renderer.modelBipedMain.bipedBody.rotateAngleX;
                float rotationZ = event.renderer.modelBipedMain.bipedBody.rotateAngleZ;

                double x = event.entity.posX;
                double y = event.entity.posY;
                double z = event.entity.posZ;

                float yaw = event.entity.rotationYaw;
                float pitch =  event.entity.rotationPitch;

                ClientProxy.rendererBackpackArmor.render(event.entity, x, y, z, rotationY, pitch, backpack, rotationX, rotationY, rotationZ);

                event.renderCape = false;
            }
        }



    }
}
