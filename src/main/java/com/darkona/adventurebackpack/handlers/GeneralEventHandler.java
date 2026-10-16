package com.darkona.adventurebackpack.handlers;

import com.darkona.adventurebackpack.common.AbilityToggle;
import com.darkona.adventurebackpack.common.BackpackAbilities;
import com.darkona.adventurebackpack.common.QuiverFeedRules;
import com.darkona.adventurebackpack.common.ServerActions;
import com.darkona.adventurebackpack.config.ConfigHandler;
import com.darkona.adventurebackpack.entity.ai.EntityAIHorseFollowOwner;
import adventurebackpack.api.WearableEvent;
import com.darkona.adventurebackpack.fluids.FluidEffectRegistry;
import com.darkona.adventurebackpack.init.ModNetwork;
import com.darkona.adventurebackpack.inventory.InventoryBackpack;
import com.darkona.adventurebackpack.network.messages.EntitySoundPacket;
import com.darkona.adventurebackpack.util.Utils;
import com.darkona.adventurebackpack.util.Wearing;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAppleGold;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.UUID;

public class GeneralEventHandler
{
    @SubscribeEvent
    public void eatGoldenApple(PlayerUseItemEvent.Finish event)
    {
        EntityPlayer player = event.entityPlayer;
        if (ConfigHandler.RAINBOW_ENABLED && AbilityToggle.isActive(player))
        {
            if (event.item.getItem() instanceof ItemAppleGold &&
                    Wearing.isWearingTheRightBackpack(player, "Rainbow"))
            {
                ItemStack backpack = Wearing.getWearingBackpack(player);
                if (BackpackAbilities.isRainbowRunning(player.worldObj, backpack)) return;
                BackpackAbilities.startRainbowRun(player.worldObj, backpack);
                if (!player.worldObj.isRemote)
                {
                    String nyanString =
                            EnumChatFormatting.RED + "N" +
                                    EnumChatFormatting.GOLD + "Y" +
                                    EnumChatFormatting.YELLOW + "A" +
                                    EnumChatFormatting.GREEN + "N" +
                                    EnumChatFormatting.AQUA + "C" +
                                    EnumChatFormatting.BLUE + "A" +
                                    EnumChatFormatting.DARK_PURPLE + "T";
                    player.addChatComponentMessage(new ChatComponentText(nyanString));
                    ModNetwork.sendToNearby(new EntitySoundPacket.Message(EntitySoundPacket.NYAN_SOUND, player), player);
                }
            }
        }

        if (event.item.getItem() == Items.potionitem && event.item.getItemDamage() == 0)
        {
            if (!player.worldObj.isRemote)
            {
                FluidEffectRegistry.executeFluidEffectsForFluid(FluidRegistry.WATER, player, player.getEntityWorld());
            }
        }
    }

    @SubscribeEvent
    public void detectBow(ArrowNockEvent event)
    {
        if (!ConfigHandler.SKELETON_ENABLED || !AbilityToggle.isActive(event.entityPlayer)) return;
        if (Wearing.isWearingTheRightBackpack(event.entityPlayer, "Skeleton"))
        {
            InventoryBackpack backpack = new InventoryBackpack(Wearing.getWearingBackpack(event.entityPlayer));
            if (backpack.hasItem(Items.arrow))
            {
                event.entityPlayer.setItemInUse(event.result, event.result.getMaxItemUseDuration());
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void detectArrow(ArrowLooseEvent event)
    {
        if (!ConfigHandler.SKELETON_ENABLED || !AbilityToggle.isActive(event.entityPlayer)) return;
        if (Wearing.isWearingTheRightBackpack(event.entityPlayer, "Skeleton"))
        {
            InventoryBackpack backpack = new InventoryBackpack(Wearing.getWearingBackpack(event.entityPlayer));
            if (backpack.hasItem(Items.arrow))
            {
                ServerActions.leakArrow(event.entityPlayer, event.bow, event.charge);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void detectLightning(EntityStruckByLightningEvent event)
    {
        if (event.entity != null && event.entity instanceof EntityPlayer)
        {
            ServerActions.electrify((EntityPlayer) event.entity);
        }
    }

    @SubscribeEvent
    public void skeletonArrowSpeed(EntityJoinWorldEvent event)
    {
        if (event.world.isRemote || !(event.entity instanceof EntityArrow)) return;
        EntityArrow arrow = (EntityArrow) event.entity;
        if (arrow.ticksExisted > 0 || !(arrow.shootingEntity instanceof EntityPlayer)) return;
        double factor = QuiverFeedRules.speedFactor(ConfigHandler.SKELETON_ARROW_SPEED_BONUS);
        if (factor == 1.0 || !ConfigHandler.SKELETON_ENABLED) return;
        EntityPlayer shooter = (EntityPlayer) arrow.shootingEntity;
        if (!AbilityToggle.isActive(shooter) || !Wearing.isWearingTheRightBackpack(shooter, "Skeleton")) return;
        arrow.motionX *= factor;
        arrow.motionY *= factor;
        arrow.motionZ *= factor;
    }

    @SubscribeEvent
    public void makeHorsesFollowOwner(EntityJoinWorldEvent event)
    {
        if (!ConfigHandler.HORSES_FOLLOW_OWNER) return;
        if(event.entity instanceof EntityHorse)
        {
            EntityHorse horse = ((EntityHorse)event.entity);
            if(!horse.isDead && horse.isTame() && horse.hasCustomNameTag())
            {
                UUID ownerUUID = Utils.parseUUIDOrNull(horse.func_152119_ch());
                if (ownerUUID != null)
                {
                    boolean set = true;
                    if(horse.worldObj.func_152378_a(ownerUUID) != null)
                    {
                        for (Object entry : horse.tasks.taskEntries)
                        {
                            if (((EntityAITasks.EntityAITaskEntry) entry).action instanceof EntityAIHorseFollowOwner)
                            {
                            	set = false;
                            }
                        }
                    }
                    if(set)
                    {
                    	horse.tasks.addTask(4, new EntityAIHorseFollowOwner(horse, 1.5d, 2.0f, 20.0f));
                    	
                    	if (horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange) != null)
                    	{
                    		horse.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.followRange).setBaseValue(100.0D);
                    	}
                    }
            	}
            }
        }
    }

    @SubscribeEvent
    public void backpackUnequipped(WearableEvent.UnequipWearableEvent event)
    {
    }
}
