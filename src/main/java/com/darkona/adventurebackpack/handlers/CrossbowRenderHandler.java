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

/**
 * Client hooks of the crossbow visuals: the partial tick for the item renderer (FML bus), the sniper zoom and the
 * two-handed aiming pose of a player holding the crossbow (Forge bus).
 * <p>
 * The crossbow is never "in use" ({@code EnumAction.none}), so vanilla never raises the arms by itself; this sets
 * {@code aimedBow} on the three biped models of {@link RenderPlayer} before it renders, exactly as vanilla does for
 * a drawn bow, and vanilla clears the flags after rendering; {@link CrossbowHoldPose} then turns the arms into a
 * two-handed hold, between this Pre and the Post. It only touches a plain {@code RenderPlayer}: a mod
 * that renders players with its own class (RenderPlayerAPI, Smart Moving, Mo' Bends) keeps its own arms, and any
 * failure inside a foreign renderer is swallowed so the player still renders. No allocation per frame.
 */
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

    /** Mode of the held crossbow, read from NBT only when the stack object changes. */
    private final CrossbowRenderState fovState = new CrossbowRenderState();

    /**
     * Sniper zoom: while the local player holds the crossbow in sniper mode with the bipod down, the field of view
     * narrows by {@code crossbow.sniper.zoom}. Vanilla eases the change over a few ticks, so it zooms in and out
     * smoothly. Fires once per client tick; no allocation, no NBT read unless the stack changed.
     */
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

    /** Mode and last shot of the stack being drawn, read from NBT only when the stack object changes. */
    private final CrossbowRenderState poseState = new CrossbowRenderState();

    /**
     * Raises the arms of a player holding the crossbow: {@code aimedBow} on the three biped models as a fallback
     * (vanilla's bow pose), then the two-handed hold of {@link CrossbowHoldPose} on top. Runs last so no other
     * handler can cancel the render after the pose started; {@link #onRenderPlayerPost} ends it.
     */
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
            // A foreign player renderer without the vanilla models: leave its pose alone.
            CrossbowHoldPose.end();
        }
    }

    @SubscribeEvent
    public void onRenderPlayerPost(RenderPlayerEvent.Post event)
    {
        CrossbowHoldPose.end();
    }

    /** The renderer whose models carry the pose hook. */
    private RenderPlayer hookedRenderer;

    /** The pose follows the held item only: the crossbow is aimed whenever it is in the main hand. */
    public static boolean isHoldingCrossbow(ItemStack held)
    {
        return held != null && held.getItem() == ModItems.cwxbow;
    }
}
