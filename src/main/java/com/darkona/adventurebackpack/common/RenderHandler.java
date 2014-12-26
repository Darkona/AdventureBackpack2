package com.darkona.adventurebackpack.common;

import baubles.api.BaublesApi;
import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.client.models.ModelBackpackArmor;
import com.darkona.adventurebackpack.client.render.RendererBackpackArmor;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.proxy.ClientProxy;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Resources;
import copy.betterstorage.utils.ReflectionUtils;
import copy.betterstorage.utils.RenderUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import scala.runtime.MethodCache;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
           //ClientProxy.rendererBackpackArmor.render(event.entity,0,0,0,0,0, backpack);
           event.renderCape = false;
        }
    }
}
