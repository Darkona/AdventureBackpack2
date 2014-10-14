package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.AdventureBackpack;
import com.darkona.adventurebackpack.common.Actions;
import com.darkona.adventurebackpack.events.HoseSpillEvent;
import com.darkona.adventurebackpack.events.HoseSuckEvent;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.items.ItemAdventureBackpack;
import com.darkona.adventurebackpack.items.ItemHose;
import com.darkona.adventurebackpack.network.CycleToolMessage;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Mouse;

/**
 * Created by Darkona on 11/10/2014.
 */
public class EventHandler {

    static int dWheel = 0;
    static int theSlot = -1;
    static boolean isHose = false;
    static boolean isTool = false;

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


        EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;

        if (event.phase == TickEvent.Phase.START) {
            dWheel = Mouse.getDWheel() / 120;
            if (player != null) {
                if (player.isSneaking()) {
                    ItemStack backpack = player.getCurrentArmor(2);
                    if (backpack != null && backpack.getItem() instanceof ItemAdventureBackpack) {

                        Minecraft.getMinecraft().playerController.updateController();
                        if (player.getCurrentEquippedItem() != null) {
                            if (SlotTool.isValidTool(player.getCurrentEquippedItem())) {
                                isTool = true;
                                theSlot = player.inventory.currentItem;
                            }
                            if (player.getCurrentEquippedItem().getItem() instanceof ItemHose) {
                                isHose = true;
                                theSlot = player.inventory.currentItem;
                            }
                        }
                    }
                } else {
                    theSlot = -1;
                }
            }
        }

        if (event.phase == TickEvent.Phase.END) {
            if (player != null) {
                if (theSlot > -1 && dWheel != Mouse.getDWheel()) {

                    if (isHose) {
                        player.inventory.currentItem = theSlot;
                        LogHelper.info("Sending hose switch message");
                        AdventureBackpack.networkWrapper.sendToServer(new CycleToolMessage(-1, theSlot, false));
                    }

                    if (isTool) {

                        LogHelper.info("Sending tool cycle message");
                        player.inventory.currentItem = theSlot;
                        AdventureBackpack.networkWrapper.sendToServer(new CycleToolMessage(dWheel - Mouse.getDWheel(), theSlot, true));
                    }

                }


            }
            theSlot = -1;
            isHose = false;
            isTool = false;
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void Suck(HoseSuckEvent event) {
        FluidStack result = Actions.attemptFill(event.world, event.target, event.entityPlayer, event.currentTank);
        if (result != null) {
            event.fluidResult = result;
            event.setResult(Event.Result.ALLOW);
        } else {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void Spill(HoseSpillEvent event) {
        FluidStack result = Actions.attemptPour(event.player, event.world, event.x, event.y, event.z, event.currentTank);
        if (result != null) {
            event.fluidResult = result;
            event.setResult(Event.Result.ALLOW);
        } else {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerDies(LivingDeathEvent event) {
        if (event.entity instanceof EntityPlayer && Utils.isWearingBackpack((EntityPlayer) event.entity)) {
            Actions.tryPlaceOnDeath((EntityPlayer) event.entity);
        }
        event.setResult(Event.Result.ALLOW);
    }


}
