package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.client.render.CrossbowHoldPose;
import com.darkona.adventurebackpack.client.render.CrossbowRenderState;
import com.darkona.adventurebackpack.client.render.RendererItemClockworkCrossbow;
import com.darkona.adventurebackpack.common.CrossbowAnimation;
import com.darkona.adventurebackpack.common.CrossbowLogic;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.item.ItemCrossbow;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;

@SideOnly(Side.CLIENT)
public class CrossbowRenderHandler
{
    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            RendererItemClockworkCrossbow.partialTicks = event.renderTickTime;
        }
    }

    private final CrossbowRenderState fovState = new CrossbowRenderState();

    @SubscribeEvent
    public void onFovUpdate(FOVUpdateEvent event)
    {
        EntityPlayer player = event.entity;
        if (player == null || !ConfigHandler.CROSSBOW_ENABLED) return;
        ItemStack held = player.getHeldItem();
        if (!isHoldingCrossbow(held)) return;
        fovState.update(held);
        if (ItemCrossbow.effectiveMode(fovState.mode) != CrossbowLogic.MODE_SNIPER) return;
        if (ItemCrossbow.isBipodDeployed(player)) event.newfov *= ConfigHandler.SNIPER_ZOOM;
    }

    private final CrossbowRenderState poseState = new CrossbowRenderState();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRenderPlayer(RenderPlayerEvent.Pre event)
    {
        CrossbowHoldPose.end();
        try
        {
            RenderPlayer renderer = event.renderer;
            if (renderer == null || renderer.getClass() != RenderPlayer.class) return;
            EntityPlayer player = event.entityPlayer;
            ItemStack held = player != null ? player.getHeldItem() : null;
            if (!isHoldingCrossbow(held)) return;
            if (renderer.modelBipedMain != null) renderer.modelBipedMain.aimedBow = true;
            if (renderer.modelArmorChestplate != null) renderer.modelArmorChestplate.aimedBow = true;
            if (renderer.modelArmor != null) renderer.modelArmor.aimedBow = true;
            if (renderer != hookedRenderer)
            {
                CrossbowHoldPose.install(renderer.modelBipedMain);
                CrossbowHoldPose.install(renderer.modelArmorChestplate);
                CrossbowHoldPose.install(renderer.modelArmor);
                hookedRenderer = renderer;
            }
            poseState.update(held);
            float pt = event.partialRenderTick;
            float recoil = CrossbowAnimation.recoil(CrossbowAnimation.sinceShot(player.worldObj.getTotalWorldTime(), pt, poseState.lastShot));
            boolean steady = ItemCrossbow.effectiveMode(poseState.mode) == CrossbowLogic.MODE_SNIPER && ItemCrossbow.isBipodDeployed(player);
            CrossbowHoldPose.begin(player, steady, recoil, player.ticksExisted + pt);
        } catch (Throwable ignored)
        {
            CrossbowHoldPose.end();
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event)
    {
        CrossbowHoldPose.end();
    }

    private RenderPlayer hookedRenderer;

    public static boolean isHoldingCrossbow(ItemStack held)
    {
        return held != null && held.getItem() == ModItems.cwxbow;
    }
}
