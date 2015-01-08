package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.BackpackProperty;
import com.darkona.adventurebackpack.common.ServerActions;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

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
        if(event.entity instanceof EntityPlayer && BackpackProperty.get((EntityPlayer)event.entity) == null)
        {
            BackpackProperty.register((EntityPlayer)event.entity);
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
        if(event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.entity;

            NBTTagCompound playerData = ServerProxy.extractPlayerProps(player.getCommandSenderName());
            if(playerData != null)
            {
                LogHelper.info("I have DATA");
                BackpackProperty.get(player).loadNBTData(playerData);
            }
        }
    }

    @SubscribeEvent
    public void playerSpawns(PlayerEvent.PlayerRespawnEvent event)
    {
       /* if(event.player.worldObj.isRemote)return;
        LogHelper.info("I'm back!");
        BackpackProperty prop = BackpackProperty.get(event.player);
        if(prop != null && prop.getDimension() == event.player.dimension)
        {
            LogHelper.info("I have a campfire in this dimension.");
            ChunkCoordinates campfire = BackpackProperty.get(event.player).getCampFire();
            if(campfire != null)
            {
                LogHelper.info("I remember where it is.");
                ChunkCoordinates safe = EntityPlayer.verifyRespawnCoordinates(event.player.worldObj, campfire, true);
                event.player.setPositionAndUpdate(campfire.posX,campfire.posY,campfire.posZ);
                //event.player.setLocationAndAngles(campfire.posX + 0.5F, campfire.posY + 0.1F, campfire.posZ + 0.5F, 0.0F, 0.0F);
                //((EntityPlayerMP)event.player).playerNetServerHandler.setPlayerLocation(event.player.posX, event.player.posY, event.player.posZ, event.player.rotationYaw, event.player.rotationPitch);
                //((EntityPlayerMP)event.player).playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(campfire.posX, campfire.posY, campfire.posZ));
            }
        }*/
    }
    /**
     * @param event
     */
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void playerDies(LivingDeathEvent event)
    {

        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)event.entity;

            if(Wearing.isWearingBackpack(player))
            {
                if (BackpackNames.getBackpackColorName(Wearing.getWearingBackpack(player)).equals("Creeper"))
                {
                    player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, 4.0F, false);
                }
                if(!ServerActions.tryPlaceOnDeath(player)){
                    Wearing.getWearingBackpack(player).getItem().onDroppedByPlayer(Wearing.getWearingBackpack(player),player);
                }
            }

            if(!player.worldObj.isRemote)
            {
                LogHelper.info("I died.");
                NBTTagCompound playerData = new NBTTagCompound();
                BackpackProperty.get(player).saveNBTData(playerData);
                ServerProxy.storePlayerProps(player.getCommandSenderName(), playerData);
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


}

