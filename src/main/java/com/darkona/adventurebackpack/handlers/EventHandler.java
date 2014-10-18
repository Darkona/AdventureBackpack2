package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.Actions;
import com.darkona.adventurebackpack.common.Constants;
import com.darkona.adventurebackpack.common.IAdvBackpack;
import com.darkona.adventurebackpack.events.HoseSpillEvent;
import com.darkona.adventurebackpack.events.HoseSuckEvent;
import com.darkona.adventurebackpack.events.UnequipBackpackEvent;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryItem;
import com.darkona.adventurebackpack.inventory.SlotTool;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemHose;
import com.darkona.adventurebackpack.misc.NyanMovingSound;
import com.darkona.adventurebackpack.network.CycleToolMessage;
import com.darkona.adventurebackpack.network.MessageConstants;
import com.darkona.adventurebackpack.network.NyanCatMessage;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

/**
 * Created on 11/10/2014
 * Handle ALL the events!
 *
 * @author Darkona
 * @see com.darkona.adventurebackpack.common.Actions
 */
public class EventHandler
{
    /**
     * Used for the Piston Boots to give them their amazing powers.
     *
     * @param event
     */
    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event)
    {
        if (event.entity != null &&
                event.entityLiving instanceof EntityPlayer &&
                ((EntityPlayer) event.entityLiving).onGround &&
                Wearing.isWearingBoots(((EntityPlayer) event.entityLiving))
                )
        {
            Actions.pistonBootsJump(((EntityPlayer) event.entityLiving));
        }
    }

    /**
     * Used by the Piston boots to lessen the fall damage. It's hacky, but I don't care.
     *
     * @param event
     */
    @SubscribeEvent
    public void onFall(LivingFallEvent event)
    {
        if (event.entity != null &&
                event.entityLiving instanceof EntityPlayer &&
                Wearing.isWearingBoots(((EntityPlayer) event.entityLiving)) &&
                event.distance < 8)
        {
            event.setCanceled(true);
        }
    }

    /**
     * For detecting when the hose is performing the suction of fluids from the world. Slurp!
     *
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void suck(HoseSuckEvent event)
    {
        FluidStack result = Actions.attemptFill(event.world, event.target, event.entityPlayer, event.currentTank);
        if (result != null)
        {
            event.fluidResult = result;
            event.setResult(Event.Result.ALLOW);
        } else
        {
            event.setResult(Event.Result.DENY);
        }
    }

    /**
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public void spill(HoseSpillEvent event)
    {
        FluidStack result = Actions.attemptPour(event.player, event.world, event.x, event.y, event.z, event.currentTank);
        if (result != null)
        {
            event.fluidResult = result;
            event.setResult(Event.Result.ALLOW);
        } else
        {
            event.setResult(Event.Result.DENY);
        }
    }

    /**
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerDies(LivingDeathEvent event)
    {
        if (event.entity instanceof EntityPlayer && Wearing.isWearingBackpack((EntityPlayer) event.entity))
        {
            EntityPlayer player = ((EntityPlayer) event.entity);
            if (Wearing.getWearingBackpack(player).getTagCompound().getString("colorName").equals("Creeper"))
            {
                player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, 4.0F, false);
            }
            Actions.tryPlaceOnDeath(player);
        }
        event.setResult(Event.Result.ALLOW);
    }
}

