package com.darkona.adventurebackpack.init;

import com.darkona.adventurebackpack.common.Actions;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

/**
 * Created by Darkona on 11/10/2014.
 */
public class ModEventListeners {
    /*Minecraft.getMinecraft().gameSettings.keyBindJump.isPressed()*/

    @SubscribeEvent
    public void onTickPassed(LivingEvent.LivingJumpEvent event) {
        if (event.entity != null &&
                event.entityLiving instanceof EntityPlayer &&
                ((EntityPlayer) event.entityLiving).onGround &&
                Wearing.isWearingBoots(((EntityPlayer) event.entityLiving))
                ) {
            Actions.pistonBootsJump(((EntityPlayer) event.entityLiving));
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event) {
        if (event.entity != null &&
                event.entityLiving instanceof EntityPlayer &&
                Wearing.isWearingBoots(((EntityPlayer) event.entityLiving)) &&
                event.distance < 8) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {

        }

        if (event.phase == TickEvent.Phase.END) {

        }
    }


}
