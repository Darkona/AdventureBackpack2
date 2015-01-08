package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.BackpackProperty;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.proxy.ServerProxy;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

/**
 * Created on 11/10/2014
 * Handle ALL the events!
 *
 * @author Darkona
 * @see com.darkona.adventurebackpack.common.ClientActions
 */
public class PlayerEventHandler
{

    @SubscribeEvent
    public void registerBackpackProperty(EntityEvent.EntityConstructing event)
    {
        if (event.entity instanceof EntityPlayer && BackpackProperty.get((EntityPlayer) event.entity) == null)
        {
            BackpackProperty.register((EntityPlayer) event.entity);
        }
    }

    /**
     * Used for the Piston Boots to give them their amazing powers.
     *
     * @param event
     */
    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event)
    {
        if (event.entity != null &&
                event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;


            if (Wearing.isWearingBoots(player) && player.onGround)
            {
                ServerActions.pistonBootsJump(player);
            }
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

    @SubscribeEvent
    public void playerIsBorn(EntityJoinWorldEvent event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            NBTTagCompound playerData = ServerProxy.extractPlayerProps(player.getCommandSenderName());
            if (playerData != null)
            {
                BackpackProperty.get(player).loadNBTData(playerData);
            }
        }
    }

    @SubscribeEvent
    public void playerSpawns(PlayerEvent.PlayerRespawnEvent event)
    {
        if(!event.player.worldObj.isRemote)
        {
            ChunkCoordinates bedLocation = event.player.getBedLocation(event.player.dimension);
            LogHelper.info("PlayerRespawnEvent: Player respawn coordinates are " + ((bedLocation != null) ? LogHelper.print3DCoords( bedLocation) : "null."));
        }
    }

    /**
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerDies(LivingDeathEvent event)
    {

        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (Wearing.isWearingBackpack(player))
            {
                if (BackpackNames.getBackpackColorName(Wearing.getWearingBackpack(player)).equals("Creeper"))
                {
                    player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, 4.0F, false);
                }
                if (!ServerActions.tryPlaceOnDeath(player))
                {
                    Wearing.getWearingBackpack(player).getItem().onDroppedByPlayer(Wearing.getWearingBackpack(player), player);
                }
            }

            if (!player.worldObj.isRemote)
            {
                if(BackpackProperty.get(player).isForceCampFire())
                {
                    ChunkCoordinates lastCampFire = BackpackProperty.get(player).getCampFire();
                    if(lastCampFire != null)
                    player.setSpawnChunk(lastCampFire,true,player.dimension);
                }
                NBTTagCompound playerData = new NBTTagCompound();
                BackpackProperty.get(player).saveNBTData(playerData);
                ServerProxy.storePlayerProps(player.getCommandSenderName(), playerData);
                LogHelper.info("Player just died, bedLocation is" + LogHelper.print3DCoords(player.getBedLocation(player.dimension)));
            }
        }

        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void playerCraftsBackpack(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting.getItem() == ModItems.adventureBackpack)
        {
            LogHelper.info("Player crafted a backpack, and that backpack's appearance is: " + event.crafting.getTagCompound().getString("colorName"));
            if (event.crafting.getTagCompound().getString("colorName").equals("Dragon"))
            {
                event.player.dropPlayerItemWithRandomChoice(new ItemStack(Blocks.dragon_egg, 1), false);
                event.player.playSound("mob.enderdragon.growl", 1.0f, 5.0f);
            }
        }
    }

    @SubscribeEvent
    public void rideSpider(EntityInteractEvent event)
    {
        EntityPlayer player = event.entityPlayer;
        if (!event.entityPlayer.worldObj.isRemote)
        {
            if (BackpackNames.getBackpackColorName(Wearing.getWearingBackpack(player)).equals("Spider"))
            {
                if (event.target instanceof EntitySpider)
                {
                    event.entityPlayer.mountEntity(event.target);
                }
            }
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent
    public void playerWokeUp(PlayerWakeUpEvent event)
    {
        if(event.entity.worldObj.isRemote)return;
        ChunkCoordinates bedLocation = event.entityPlayer.getBedLocation(event.entityPlayer.dimension);
        if(bedLocation != null && event.entityPlayer.worldObj.getBlock(bedLocation.posX,bedLocation.posY,bedLocation.posZ) == ModBlocks.blockSleepingBag)
        {
            //If the player wakes up in one of those super confortable SleepingBags (tm) (Patent Pending)
            BackpackProperty.get(event.entityPlayer).setForceCampFire(true);
            LogHelper.info("Player just woke up in a sleeping bag, forcing respawn in the last lighted campfire, if there's any");
        }else{
            //If it's a regular bed or whatever
            BackpackProperty.get(event.entityPlayer).setForceCampFire(false);
        }
    }


}

