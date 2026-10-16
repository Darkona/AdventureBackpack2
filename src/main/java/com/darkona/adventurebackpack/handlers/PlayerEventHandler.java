package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.inventory.QuiverFeed;
import com.darkona.adventurebackpack.common.AbilityToggle;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.DeathRoute;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.common.SleepingBagSpawn;
import com.darkona.adventurebackpack.common.SpiderCare;
import com.darkona.adventurebackpack.common.SpiderTaming;
import com.darkona.adventurebackpack.compat.graves.GraveCompat;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.EntityFriendlySpider;
import com.darkona.adventurebackpack.entity.ai.EntityAIHorseFollowOwner;
import com.darkona.adventurebackpack.init.ModBlocks;
import com.darkona.adventurebackpack.init.ModItems;
import adventurebackpack.api.IBackWearableItem;
import com.darkona.adventurebackpack.item.ItemAdventureBackpack;
import com.darkona.adventurebackpack.item.ItemPistonBoots;
import com.darkona.adventurebackpack.playerProperties.BackpackProperty;
import com.darkona.adventurebackpack.proxy.ServerProxy;
import com.darkona.adventurebackpack.reference.BackpackNames;
import com.darkona.adventurebackpack.util.LogHelper;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemNameTag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;

import java.util.UUID;

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
        if (!event.world.isRemote)
        {
            if (Utils.notNullAndInstanceOf(event.entity,  EntityPlayer.class))
            {
                EntityPlayer player = (EntityPlayer) event.entity;
                NBTTagCompound playerData = ServerProxy.extractPlayerProps(player.getUniqueID());
                if (playerData != null)
                {
                    BackpackProperty.get(player).loadNBTData(playerData);
                    BackpackProperty.syncToNear(player);
                }
            }
        }
    }

    @SubscribeEvent
    public void playerLogsIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            BackpackProperty.sync(event.player);
            SpiderTaming.verify(event.player.worldObj, event.player.getUniqueID());
        }
    }

    @SubscribeEvent
    public void playerLogsOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player instanceof EntityPlayerMP)
        {
            QuiverFeed.flush(event.player);
            SleepingBagSpawn.restoreNow(event.player);
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

    @SubscribeEvent
    public void onPlayerJump(LivingEvent.LivingJumpEvent event)
    {
        if (event.entity != null &&
                event.entityLiving instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.entity;

            if (ConfigHandler.PISTONBOOTS_ENABLED && Wearing.isWearingBoots(player) && player.onGround)
            {
                ServerActions.pistonBootsJump(player);
            }
        }
    }

    @SubscribeEvent
    public void onFall(LivingFallEvent event)
    {
        if (event.entity != null)
        {
            if (event.entityLiving instanceof EntityCreature && ConfigHandler.FIX_LEAD)
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
                if (ConfigHandler.PISTONBOOTS_ENABLED && Wearing.isWearingBoots(((EntityPlayer) event.entityLiving)) && event.distance < ConfigHandler.PISTONBOOTS_SAFE_FALL_DISTANCE)
                {
                    event.setCanceled(true);
                }
                if(Wearing.isWearingTheRightBackpack((EntityPlayer)event.entityLiving,"IronGolem") && ConfigHandler.IRONGOLEM_ENABLED && AbilityToggle.isActive((EntityPlayer) event.entityLiving))
                {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void playerDies(LivingDeathEvent event)
    {
        if (Utils.notNullAndInstanceOf(event.entity, EntityPlayer.class))
        {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (!player.worldObj.isRemote)
            {
                BackpackProperty props = BackpackProperty.get(player);
                if (props.isForcedCampFire())
                {
                    ChunkCoordinates lastCampFire = BackpackProperty.get(player).getCampFire();
                    if (lastCampFire != null)
                    {
                        player.setSpawnChunk(lastCampFire, false, player.dimension);
                    }
                }
                if (player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory"))
                {
                    ServerProxy.storePlayerProps(player);
                }
            }
        }
        event.setResult(Event.Result.ALLOW);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void wearableToInventoryGrave(LivingDeathEvent event)
    {
        if (!(event.entityLiving instanceof EntityPlayer) || event.entityLiving.worldObj.isRemote) return;
        EntityPlayer player = (EntityPlayer) event.entityLiving;
        QuiverFeed.flush(player);
        BackpackProperty props = BackpackProperty.get(player);
        if (props == null || !props.hasWearable()) return;
        boolean keepInventory = player.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");
        int freeSlot = player.inventory.getFirstEmptyStack();
        DeathRoute.Route route = DeathRoute.beforeDeath(true, props.isWearableInBauble(), keepInventory, ConfigHandler.COMPAT_GRAVES_ENABLED,
                GraveCompat.graveStoneMakesGrave(event.source), freeSlot >= 0);
        if (route != DeathRoute.Route.GRAVE_INVENTORY) return;

        ItemStack wearable = props.getWearable();
        props.executeWearableUnequipProtocol();
        if (wearable.getItem() instanceof ItemAdventureBackpack)
        {
            ItemAdventureBackpack.deathEffects(player.worldObj, player, wearable);
        }
        player.inventory.setInventorySlotContents(freeSlot, wearable);
        props.setWearable(null);
        deathMessage(player, wearable, "grave", MathHelper.floor_double(player.posX), MathHelper.floor_double(player.posY), MathHelper.floor_double(player.posZ));
    }

    @SubscribeEvent(priority = EventPriority.HIGH, receiveCanceled = true)
    public void playerDrops(PlayerDropsEvent event)
    {
        EntityPlayer player = event.entityPlayer;
        if (player == null || player.worldObj.isRemote) return;
        BackpackProperty props = BackpackProperty.get(player);
        ItemStack wearable = props.getWearable();
        boolean keepInventory = player.getEntityWorld().getGameRules().getGameRuleBooleanValue("keepInventory");
        DeathRoute.Route route = DeathRoute.onDrops(wearable != null, props.isWearableInBauble(), keepInventory, event.isCanceled(),
                ConfigHandler.COMPAT_GRAVES_ENABLED, GraveCompat.hasDropsGrave(), ConfigHandler.BACKPACK_DEATH_PLACE,
                wearable != null && wearable.getItem() instanceof ItemAdventureBackpack);
        int x = MathHelper.floor_double(player.posX), y = MathHelper.floor_double(player.posY), z = MathHelper.floor_double(player.posZ);
        player.captureDrops = !event.isCanceled();
        try
        {
            switch (route)
            {
                case BAUBLES:
                    props.unlinkBauble();
                    break;
                case GRAVE_DROPS:
                    dropWearable(player, wearable);
                    deathMessage(player, wearable, "grave", x, y, z);
                    break;
                case PLACE:
                    ChunkCoordinates placed = ((ItemAdventureBackpack) wearable.getItem()).placeOrDropOnDeath(player.worldObj, player, wearable);
                    if (placed != null)
                    {
                        deathMessage(player, wearable, "placed", placed.posX, placed.posY, placed.posZ);
                    } else
                    {
                        deathMessage(player, wearable, "dropped", x, y, z);
                    }
                    break;
                case DROP:
                    dropWearable(player, wearable);
                    deathMessage(player, wearable, "dropped", x, y, z);
                    break;
                default:
                    break;
            }
        } finally
        {
            player.captureDrops = false;
        }
        ServerProxy.storePlayerProps(player);
    }

    private static void dropWearable(EntityPlayer player, ItemStack wearable)
    {
        if (wearable.getItem() instanceof ItemAdventureBackpack)
        {
            ((ItemAdventureBackpack) wearable.getItem()).dropOnDeath(player.worldObj, player, wearable);
        } else if (wearable.getItem() instanceof IBackWearableItem)
        {
            ((IBackWearableItem) wearable.getItem()).onPlayerDeath(player.worldObj, player, wearable);
        }
    }

    private static void deathMessage(EntityPlayer player, ItemStack wearable, String where, int x, int y, int z)
    {
        if (!ConfigHandler.BACKPACK_DEATH_MESSAGE) return;
        player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.death." + where,
                wearable.getDisplayName(), x, y, z, player.dimension));
    }

    @SubscribeEvent
    public void playerCraftsBackpack(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting != null && event.crafting.getItem() == ModItems.adventureBackpack)
        {
            if (BackpackNames.getBackpackColorName(event.crafting).equals("Dragon"))
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
        if (!event.entityPlayer.worldObj.isRemote)
        {
            if (event.target != null && event.target.getClass() == EntitySpider.class && tameSpider(player, (EntitySpider) event.target))
            {
                event.setCanceled(true);
                return;
            }
            if (Utils.notNullAndInstanceOf(event.target, EntityHorse.class))
            {
                ItemStack stack = player.getCurrentEquippedItem();
                EntityHorse horse = (EntityHorse) event.target;
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
    public void blazePunch(LivingHurtEvent event)
    {
        if (event.entityLiving.worldObj.isRemote || !"player".equals(event.source.damageType)) return;
        Entity attacker = event.source.getEntity();
        if (attacker instanceof EntityPlayer && event.source.getSourceOfDamage() == attacker)
        {
            BackpackAbilities.blazePunch((EntityPlayer) attacker, event.entityLiving);
        }
    }

    private static boolean tameSpider(EntityPlayer player, EntitySpider spider)
    {
        if (!ConfigHandler.BACKPACK_ABILITIES || !ConfigHandler.SPIDER_ENABLED || ConfigHandler.SPIDER_MAX_TAMED <= 0) return false;
        if (spider.isDead || spider.riddenByEntity != null) return false;
        ItemStack held = player.getCurrentEquippedItem();
        if (held == null || held.getItem() == null) return false;
        Object tameItem = Item.itemRegistry.getObject(ConfigHandler.SPIDER_TAME_ITEM);
        if (tameItem == null || held.getItem() != tameItem) return false;
        if (!Wearing.isWearingTheRightBackpack(player, "Spider")) return false;

        WorldServer world = (WorldServer) spider.worldObj;
        UUID owner = player.getUniqueID();
        if (!SpiderTaming.canTameMore(world, owner, ConfigHandler.SPIDER_MAX_TAMED)
                && (SpiderTaming.verify(world, owner) == 0 || !SpiderTaming.canTameMore(world, owner, ConfigHandler.SPIDER_MAX_TAMED)))
        {
            player.addChatComponentMessage(new ChatComponentTranslation("adventurebackpack:messages.spider.limit", ConfigHandler.SPIDER_MAX_TAMED));
            return true;
        }
        if (!player.capabilities.isCreativeMode)
        {
            if (--held.stackSize <= 0)
            {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
        }
        double y = spider.posY + spider.height;
        if (!SpiderTaming.rollTames(spider.getRNG().nextFloat(), ConfigHandler.SPIDER_TAME_CHANCE))
        {
            world.func_147487_a("smoke", spider.posX, y, spider.posZ, 7, 0.5, 0.3, 0.5, 0.02);
            return true;
        }
        EntityFriendlySpider pet = new EntityFriendlySpider(world);
        pet.setLocationAndAngles(spider.posX, spider.posY, spider.posZ, spider.rotationYaw, spider.rotationPitch);
        pet.setHealth(SpiderCare.tamedHealth(pet.getMaxHealth()));
        if (spider.hasCustomNameTag())
        {
            pet.setCustomNameTag(spider.getCustomNameTag());
        }
        pet.setOwner(owner);
        spider.setDead();
        world.spawnEntityInWorld(pet);
        SpiderTaming.add(world, owner, pet.getUniqueID(), world.provider.dimensionId,
                MathHelper.floor_double(pet.posX) >> 4, MathHelper.floor_double(pet.posZ) >> 4);
        world.func_147487_a("heart", pet.posX, y, pet.posZ, 7, 0.5, 0.3, 0.5, 0.02);
        return true;
    }

    @SubscribeEvent
    public void playerWokeUp(PlayerWakeUpEvent event)
    {
        if (event.entity.worldObj.isRemote) return;
        ChunkCoordinates bedLocation = event.entityPlayer.getBedLocation(event.entityPlayer.dimension);
        if (bedLocation != null && event.entityPlayer.worldObj.getBlock(bedLocation.posX, bedLocation.posY, bedLocation.posZ) == ModBlocks.blockSleepingBag)
        {
            BackpackProperty.get(event.entityPlayer).setForceCampFire(true);
            LogHelper.info("Player just woke up in a sleeping bag, forcing respawn in the last lighted campfire, if there's any");
        } else
        {
            BackpackProperty.get(event.entityPlayer).setForceCampFire(false);
        }
    }

    @SubscribeEvent
    public void startTracking(net.minecraftforge.event.entity.player.PlayerEvent.StartTracking event)
    {
        if (event.target instanceof EntityPlayer && event.entityPlayer instanceof EntityPlayerMP)
        {
            BackpackProperty.syncTo((EntityPlayer) event.target, (EntityPlayerMP) event.entityPlayer);
        }
    }

    @SubscribeEvent
    public void tickPlayer(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START && event.player != null) ItemPistonBoots.tickUnworn(event.player);
        if (event.phase == TickEvent.Phase.END && event.side.isServer() && event.player != null) SleepingBagSpawn.restoreWhenAwake(event.player);
        if (event.player == null || event.player.isDead) return;
        BackpackProperty props = BackpackProperty.get(event.player);
        if (props == null) return;
        ItemStack worn = props.getWearable();
        if (worn != null && worn.getItem() instanceof IBackWearableItem)
        {
            if(event.phase == TickEvent.Phase.START)
            {
                props.executeWearableUpdateProtocol();
            }
            if (event.phase == TickEvent.Phase.END)
            {
                if (event.side.isServer() && event.player instanceof EntityPlayerMP)
                {
                    QuiverFeed.tick(event.player, props);
                    BackpackProperty.syncToNearIfChanged((EntityPlayerMP) event.player, props);
                }
            }
        }
    }
}
