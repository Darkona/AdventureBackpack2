package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.client.render.RendererItemClockworkCrossbow;
import com.darkona.adventurebackpack.init.ModItems;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderPlayerEvent;

/**
 * Client hooks of the crossbow visuals: the partial tick for the item renderer (FML bus) and the two-handed aiming
 * pose of a player holding the crossbow (Forge bus).
 * <p>
 * The crossbow is never "in use" ({@code EnumAction.none}), so vanilla never raises the arms by itself; this sets
 * {@code aimedBow} on the three biped models of {@link RenderPlayer} before it renders, exactly as vanilla does for
 * a drawn bow, and vanilla clears the flags after rendering. It only touches a plain {@code RenderPlayer}: a mod
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

    @SubscribeEvent
    public void onRenderPlayer(RenderPlayerEvent.Pre event)
    {
        try
        {
            RenderPlayer renderer = event.renderer;
            if (renderer == null || renderer.getClass() != RenderPlayer.class) return;
            EntityPlayer player = event.entityPlayer;
            if (player == null || !isHoldingCrossbow(player.getHeldItem())) return;
            if (renderer.modelBipedMain != null) renderer.modelBipedMain.aimedBow = true;
            if (renderer.modelArmorChestplate != null) renderer.modelArmorChestplate.aimedBow = true;
            if (renderer.modelArmor != null) renderer.modelArmor.aimedBow = true;
        } catch (Throwable ignored)
        {
            // A foreign player renderer without the vanilla models: leave its pose alone.
        }
    }

    /** The pose follows the held item only: the crossbow is aimed whenever it is in the main hand. */
    public static boolean isHoldingCrossbow(ItemStack held)
    {
        return held != null && held.getItem() == ModItems.cwxbow;
    }
}
