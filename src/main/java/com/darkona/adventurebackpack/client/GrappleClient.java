package com.darkona.adventurebackpack.client;

import com.darkona.adventurebackpack.common.GrappleLogic;
import com.darkona.adventurebackpack.entity.EntityGrapple;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.network.CrossbowPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@SideOnly(Side.CLIENT)
public final class GrappleClient
{
    private static int localHookId = -1;

    private GrappleClient() {}

    public static void pull(EntityGrapple hook, EntityPlayer owner, double[] out)
    {
        if (owner != Minecraft.getMinecraft().thePlayer) return;
        localHookId = hook.getEntityId();
        if (hook.releasedLocally || owner.isSneaking()) return;
        double distance = GrappleLogic.pull(owner.posX, owner.boundingBox.minY + GrappleLogic.BODY_CENTER, owner.posZ, hook.posX, hook.posY, hook.posZ, hook.getPullSpeed(), out);
        if (distance < hook.getReleaseDistance()) return;
        owner.motionX = out[0];
        owner.motionY = out[1];
        owner.motionZ = out[2];
        owner.fallDistance = 0F;
    }

    public static boolean hasLocalHook()
    {
        return localHookId >= 0;
    }

    public static void track(EntityGrapple hook, EntityPlayer owner)
    {
        if (owner == Minecraft.getMinecraft().thePlayer) localHookId = hook.getEntityId();
    }

    public static void forget(EntityGrapple hook)
    {
        if (localHookId == hook.getEntityId()) localHookId = -1;
    }

    public static void releaseOnJump(EntityPlayer player)
    {
        if (localHookId < 0 || player == null || player.worldObj == null) return;
        Entity entity = player.worldObj.getEntityByID(localHookId);
        if (!(entity instanceof EntityGrapple) || entity.isDead || ((EntityGrapple) entity).releasedLocally)
        {
            localHookId = -1;
            return;
        }
        ((EntityGrapple) entity).releasedLocally = true;
        ModNetwork.net.sendToServer(new CrossbowPacket.Message(CrossbowPacket.RELEASE_GRAPPLE));
    }
}
