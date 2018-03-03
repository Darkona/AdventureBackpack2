package com.darkona.adventurebackpack.handlers;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

import com.darkona.adventurebackpack.block.BlockSleepingBag;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.entity.ai.EntityAIHorseFollowOwner;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import com.darkona.adventurebackpack.item.IBackWearableItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ServerProxy;
import com.darkona.adventurebackpack.reference.BackpackTypes;
import com.darkona.adventurebackpack.util.EnchUtils;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Wearing;

/**
 * Created on 11/10/2014
 * Handle ALL the events!
 *
 * @author Darkona
 * @see com.darkona.adventurebackpack.client.ClientActions
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

    @SubscribeEvent
    public void joinPlayer(EntityJoinWorldEvent event)
    {
        if (!event.world.isRemote && event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;
            LogHelper.info("Joined EntityPlayer of name: " + event.entity.getCommandSenderName());
            NBTTagCompound playerData = ServerProxy.extractPlayerProps(player.getUniqueID());
            if (playerData != null)
            {
                BackpackProperty.get(player).loadNBTData(playerData);
                BackpackProperty.sync(player);
                LogHelper.info("Stored properties retrieved");
            }
        }
    }

    @SubscribeEvent
    public void playerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            EntityPlayer player = event.player;

            BackpackProperty.sync(player);

            if (Wearing.isWearingCopter(player))
            {
                ServerActions.copterSoundAtLogin(player);
            }
            if (Wearing.isWearingJetpack(player))
            {
                ServerActions.jetpackSoundAtLogin(player);
            }
        }
    }

    @SubscribeEvent
    public void playerTravelsAcrossDimensions(PlayerEvent.PlayerChangedDimensionEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            BackpackProperty.sync(event.player);
        }
    }

    /**
     * Used for the Piston Boots to give them their amazing powers.
     */
    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event)
    {
        if (event.entity != null && event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (Wearing.isWearingBoots(player) && player.onGround)
            {
                ServerActions.pistonBootsJump(player);
            }
        }
    }

    private boolean pistonBootsStepHeight = false;

    @SubscribeEvent
    public void pistonBootsUnequipped(LivingEvent.LivingUpdateEvent event)
    {
        if (event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entityLiving;
            if (Wearing.isWearingBoots(player))
            {
                if (!pistonBootsStepHeight)
                {
                    pistonBootsStepHeight = true;
                }
            }
            else
            {
                if (pistonBootsStepHeight)
                {
                    player.stepHeight = 0.5001F;
                    pistonBootsStepHeight = false;
                }
            }
        }
    }

    /**
     * Used by the Piston boots to lessen the fall damage. It's hacky, but I don't care.
     */
    @SubscribeEvent
    public void onFall(LivingFallEvent event)
    {
        if (event.entity != null)
        {
            if (event.entityLiving instanceof EntityCreature && ConfigHandler.fixLead)
            {
                EntityCreature creature = (EntityCreature) event.entityLiving;
                if (creature.getLeashed() && creature.getLeashedToEntity() != null && creature.getLeashedToEntity() instanceof EntityPlayer)
                {
                    EntityPlayer player = (EntityPlayer) creature.getLeashedToEntity();
                    if (creature.motionY > -2.0f && player.motionY > -2.0f)
                    {
                        event.setCanceled(true);
                    }
                }
            }

            if (event.entityLiving instanceof EntityFriendlySpider)
            {
                if (((EntityFriendlySpider) event.entityLiving).riddenByEntity != null
                        && ((EntityFriendlySpider) event.entityLiving).riddenByEntity instanceof EntityPlayer
                        && event.distance < 5)
                {
                    event.setCanceled(true);
                }
            }

            if (event.entityLiving instanceof EntityPlayer)
            {
                if (Wearing.isWearingBoots(((EntityPlayer) event.entityLiving)) && event.distance < 8)
                {
                    event.setCanceled(true);
                }
                if (Wearing.isWearingTheRightBackpack((EntityPlayer) event.entityLiving, BackpackTypes.IRON_GOLEM) && ConfigHandler.backpackAbilities)
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerDies(LivingDeathEvent event)
    {
        if (event.entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (!player.worldObj.isRemote)
            {
                BackpackProperty props = BackpackProperty.get(player);

                if (ConfigHandler.enableCampfireSpawn && props.isForcedCampFire())
                {
                    ChunkCoordinates lastCampFire = props.getCampFire();
                    if (lastCampFire != null)
                    {
                        player.setSpawnChunk(lastCampFire, false, player.dimension);
                    }
                    //Set the forced spawn coordinates on the campfire. False, because the player must respawn at spawn point if there's no campfire.
                }

                if (Wearing.isWearingWearable(player))
                {
                    if (Wearing.isWearingTheRightBackpack(player, BackpackTypes.CREEPER))
                    {
                        player.worldObj.createExplosion(player, player.posX, player.posY, player.posZ, 4.0F, false);
                    }

                    if (player.getEntityWorld().getGameRules().getGameRuleBooleanValue("keepInventory"))
                    {
                        ((IBackWearableItem) props.getWearable().getItem()).onPlayerDeath(player.worldObj, player, props.getWearable());
                        ServerProxy.storePlayerProps(player);
                    }
                }
            }
        }
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void playerDeathDrop(PlayerDropsEvent event)
    {
        EntityPlayer player = event.entityPlayer;

        if (Wearing.isWearingWearable(player))
        {
            ItemStack pack = Wearing.getWearingWearable(player);
            BackpackProperty props = BackpackProperty.get(player);

            if (EnchUtils.isSoulBounded(pack)
                    || (ConfigHandler.backpackDeathPlace && pack.getItem() instanceof ItemAdventureBackpack))
            {
                ((IBackWearableItem) props.getWearable().getItem()).onPlayerDeath(player.worldObj, player, props.getWearable());
                ServerProxy.storePlayerProps(player);
            }
            else
            {
                event.drops.add(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, pack));
                props.setWearable(null);
            }
        }
    }

    @SubscribeEvent
    public void playerRespawn(PlayerEvent.PlayerRespawnEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            BackpackProperty.sync(event.player);
        }
    }

    @SubscribeEvent
    public void playerCraftsBackpack(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting.getItem() == ModItems.adventureBackpack)
        {
            LogHelper.info("Player crafted a backpack, and that backpack's appearance is: "
                    + BackpackTypes.getSkinName(event.crafting));

            if (!ConfigHandler.consumeDragonEgg && BackpackTypes.getType(event.crafting) == BackpackTypes.DRAGON)
            {
                event.player.dropPlayerItemWithRandomChoice(new ItemStack(Blocks.dragon_egg, 1), false);
                event.player.playSound("mob.enderdragon.growl", 1.0f, 5.0f);
            }
        }
    }

    @SubscribeEvent
    public void interactWithCreatures(EntityInteractEvent event)
    {
        EntityPlayer player = event.entityPlayer;

        if (!player.worldObj.isRemote)
        {
            if (event.target instanceof EntitySpider)
            {
                if (Wearing.isWearingTheRightBackpack(player, BackpackTypes.SPIDER))
                {
                    EntityFriendlySpider pet = new EntityFriendlySpider(event.target.worldObj);
                    pet.setLocationAndAngles(event.target.posX, event.target.posY, event.target.posZ, event.target.rotationYaw, event.target.rotationPitch);
                    event.target.setDead();
                    event.entityPlayer.worldObj.spawnEntityInWorld(pet);
                    event.entityPlayer.mountEntity(pet);
                }
            }
            if (event.target instanceof EntityHorse)
            {
                EntityHorse horse = (EntityHorse) event.target;
                ItemStack stack = player.getCurrentEquippedItem();

                if (stack != null && stack.getItem() != null && stack.getItem() instanceof ItemNameTag && stack.hasDisplayName())
                {
                    if (horse.getCustomNameTag() == null || horse.getCustomNameTag().equals("") && horse.isTame())
                    {
                        horse.setTamedBy(player);
                        horse.tasks.addTask(4, new EntityAIHorseFollowOwner(horse, 1.5d, 2.0f, 20.0f));

                        if (horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange) != null)
                        {
                            horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setBaseValue(100.0D);
                            LogHelper.info("The horse follow range is now: " + horse.getEntityAttribute(SharedMonsterAttributes.followRange).getBaseValue());
                        }
                    }
                }
            }
        }
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent
    public void playerWokeUp(PlayerWakeUpEvent event)
    {
        if (event.entity.worldObj.isRemote)
            return;

        EntityPlayer player = event.entityPlayer;
        ChunkCoordinates bedLocation = player.getBedLocation(player.dimension);
        if (bedLocation != null && player.worldObj.getBlock(bedLocation.posX, bedLocation.posY, bedLocation.posZ) == ModBlocks.blockSleepingBag)
        {
            //If the player wakes up in one of those super confortable SleepingBags (tm) (Patent Pending)
            if (BlockSleepingBag.isSleepingInPortableBag(player))
            {
                BlockSleepingBag.packPortableSleepingBag(player);
                BackpackProperty.get(player).setWakingUpInPortableBag(true);
                LogHelper.info("Player just woke up in a portable sleeping bag.");
            }
            else
            {
                BackpackProperty.get(player).setForceCampFire(true);
                LogHelper.info("Player just woke up in a sleeping bag, forcing respawn in the last lighted campfire, if there's any");
            }
        }
        else
        {
            //If it's a regular bed or whatever
            BackpackProperty.get(player).setForceCampFire(false);
        }
    }

    @SubscribeEvent
    public void tickPlayer(TickEvent.PlayerTickEvent event)
    {
        EntityPlayer player = event.player;
        if (player != null && !player.isDead && Wearing.isWearingWearable(player))
        {
            if (event.phase == TickEvent.Phase.START)
            {
                BackpackProperty.get(player).executeWearableUpdateProtocol();
            }
            if (event.phase == TickEvent.Phase.END)
            {
                if (!player.worldObj.isRemote)
                {
                    if (BackpackProperty.get(player).isWakingUpInPortableBag() && Wearing.isWearingBackpack(player))
                    {
                        BlockSleepingBag.restoreOriginalSpawn(player, Wearing.getWearingBackpackInv(player).getExtendedProperties());
                        BackpackProperty.get(player).setWakingUpInPortableBag(false);
                    }
                }
            }
        }
    }

}
