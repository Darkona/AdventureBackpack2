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

/**
 * Client half of the grappling hook. In 1.7.10 each client moves its own player, so the owner's client applies the
 * pull to {@code thePlayer} every tick the hook is hooked, with the speed and release distance the server put in the
 * hook's spawn data. Other players' hooks only render. Runs only while a hook exists; remembers the local hook as an
 * entity id (a primitive, so nothing outlives the world).
 */
@SideOnly(Side.CLIENT)
public final class GrappleClient
{
    /** Entity id of the local player's hooked grapple, or -1. */
    private static int localHookId = -1;

    private GrappleClient() {}

    /** Called by a hooked grapple on the client every tick; {@code out} is the hook's reused vector. */
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

    /** A hook of the local player exists in the client world (flying or hooked): the crossbow's muzzle is empty. */
    public static boolean hasLocalHook()
    {
        return localHookId >= 0;
    }

    /** Called by a grapple on the client while it flies; remembers it when it belongs to the local player. */
    public static void track(EntityGrapple hook, EntityPlayer owner)
    {
        if (owner == Minecraft.getMinecraft().thePlayer) localHookId = hook.getEntityId();
    }

    /** The hook left the client world. */
    public static void forget(EntityGrapple hook)
    {
        if (localHookId == hook.getEntityId()) localHookId = -1;
    }

    /**
     * The jump key was pressed: when the local player hangs on a hook, stop pulling at once and ask the server to let
     * go. One packet per release, none when there is no hook.
     */
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
